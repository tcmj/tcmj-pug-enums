package com.tcmj.pug.enums.datasources.impl;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.tools.EnumDataHelper;
import com.tcmj.pug.enums.model.ClassCreationException;
import com.tcmj.pug.enums.model.EnumData;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

/**
 * DataProvider which loads a given URL, applies a css select expression to obtain a html table and parse
 * the table data into an EnumData model.
 * Input.1 = URL -> HTML Document
 * Input.2 = HTML.DOC -> HTML.TABLE via CSS Select
 * Input.3 = HTML.TABLE -> TR.TD (specifying one for the constants)
 */
public class URLHtmlDataProvider implements DataProvider {
  private static final transient Logger LOG = LoggerFactory.getLogger(URLHtmlDataProvider.class);
  EnumData model = new EnumData();
  final String url;
  final int columnPosConstant;
  final int[] columnPos;
  final String cssSelector;

  Boolean keepFirstRow = Boolean.FALSE;

  /**
   * Option to disable skipping of the first row of a table which is usually the description/header.
   */
  public void setKeepFirstRow(Boolean keepFirstRow) {
    this.keepFirstRow = keepFirstRow;
  }

  public URLHtmlDataProvider(String url, String tableSelector, int columnPosConstant, int[] columnPos) {
    this.url = Objects.requireNonNull(url, "URL cannot be null!");
    if (tableSelector == null) {
      LOG.debug("No CSS selection set! Defaulting to the first '<table>' found!");
      this.cssSelector = "table";
    } else {
      this.cssSelector = tableSelector;
    }
    this.columnPosConstant = Objects.requireNonNull(columnPosConstant, "Column pos constant cannot be null!");
    this.columnPos = columnPos; //column indexes to take
  }

  /** Parse a html document either from a http url or a file. */
  Document getDocument(String urlToLoad) throws IOException {

    if (StringUtils.startsWithAny(urlToLoad, "http:", "https:")) {
      return Jsoup.connect(urlToLoad).get();
    }

//    URL urls = new URL(new URL("file:"), urlToLoad);
    if (StringUtils.startsWithAny(urlToLoad, "file:")) {

      URI baseURI = URI.create("file:/");
      URI uri = URI.create(urlToLoad);
      URI resultURI = baseURI.resolve(uri);

      Path path = Paths.get(resultURI);
      try (InputStream inStream = Files.newInputStream(path)) {
        String encoding = Charset.defaultCharset().name();
        return Jsoup.parse(inStream, encoding, "");
      }
    }

    final Path patha = Paths.get(urlToLoad);
    if (Files.isRegularFile(patha)) {
      try (InputStream inStream = Files.newInputStream(patha)) {
        String encoding = Charset.defaultCharset().name();
        return Jsoup.parse(inStream, encoding, "");
      }
    }
    return null;
  }

  Element locateTable(Document doc) throws Exception {

    Elements cssSelection = Objects.requireNonNull(doc, "No Document loaded!").select(this.cssSelector);

    if (cssSelection.isEmpty()) {
      throw new ClassCreationException("Bad CSS selector! No results found with: " + this.cssSelector);
    } else {
      LOG.debug("CSS selection got {} entries: {}", cssSelection.size(), cssSelection);
    }

    Element table = cssSelection.get(0); //use first

    boolean stillNotFound = true;
    while (stillNotFound) {
      if ("table".equalsIgnoreCase(table.tagName())) {
        stillNotFound = false;
        LOG.debug("Tag successfully found: <{}> ({})", table.tag(), table.cssSelector());
      } else {
        try {
          table = table.parent();
        } catch (Exception e) {
          throw new ClassCreationException("Cannot locate table by going upwards!");
        }
      }
    }
    return table;
  }

  @Override
  public EnumData load() {
    try {
      LOG.debug("Connection URL: {}", this.url);
      Document doc = getDocument(this.url);

      Element table = locateTable(doc);

      if (this.columnPos != null) {
        String[] columnNames = getColumnNames(table);
        if (columnNames != null && columnNames.length != columnPos.length) {
          throw new ClassCreationException(String.format("Amount of configured subfield columns (%d) does not match with header columns found (%d)!", columnPos.length, columnNames.length));
        }
        model.setFieldNames(columnNames);
        model.setFieldClasses(getColumnClasses(columnNames));
      }

      getRecordData(table);

    } catch (Exception e) {
      throw new ClassCreationException(e);
    }
    return model;
  }

  private void getRecordData(Element table) throws Exception {
    Elements trs = table.select("tr");
    int curPos = 0;
    for (Element tr : trs) {
      curPos++;
      if (curPos == 1 && this.keepFirstRow == Boolean.FALSE) {
        LOG.debug("Skipping first (header) record: {}...", tr);
        continue;
      }

      Element tdConstant = tr.child(this.columnPosConstant - 1);
      String constantName = getValue(tdConstant);
      LOG.trace("Record {} constant-column: '{}' using '{}'", curPos, tdConstant, constantName);

      if (StringUtils.isBlank(constantName)) {
        //usecase: skip empty table rows used for formatting purpose
        LOG.debug("Skipping blank record {}", curPos);
        continue;
      }

      if (this.model.isEnumWithSubfields()) {
        Object[] values = new Object[columnPos.length];
        for (int i = 0; i < columnPos.length; i++) {
          int pos = columnPos[i] - 1;
          Element td = tr.child(pos);
          String value = getValue(td);
          values[i] = value;
        }
        LOG.debug("EnumData.add({},{})", constantName, Arrays.toString(values));
        EnumDataHelper.addConstantValue(model, constantName, values);
      } else {
        LOG.debug("EnumData.add({})", constantName);
        EnumDataHelper.addConstantWithoutSubfield(model, constantName);
      }
    }
  }

  /**
   * Universal extraction function used for html table records.
   *
   * @param element <th> Jsoup Element Object which can be contain everything (eg. links)
   * @return hopefully the value you want to extract.
   */
  String getValue(Element element) throws Exception {
    String value = null;
    //get all text nodes and take the first found
    Optional<TextNode> first = element.textNodes().stream().findFirst();
    if (first.isPresent()) {
      String text = first.get().text();
      String alpha = text.replaceAll("[^a-zA-Z0-9]", "");
      boolean moreThanOneChars = StringUtils.trim(alpha).length() >= 1;
      if(moreThanOneChars){
        return text;
      }
    }
    if (element.hasText()) {
      if (element.children().size() == 1) {
        value = element.text();
      } else {
        for (Element child : element.children()) {
          String valueC = child.text();
          if (valueC != null
              && valueC.length() > 0
              && (value == null || value.length() < valueC.length())) {
            value = valueC;
          }
        }
      }
    } else {
      Elements link = element.select("a[href]");
      value = link.text();
    }
    if (value == null) {
      value = element.text();
    }
    //replace special whitespaces eg. &nbsp
    value = value.replace('\u00A0', ' ');
    value = value.replace('\u2007', ' ');
    value = value.replace('\u202F', ' ');

    return value;
  }

  String[] getColumnNames(Element table) throws Exception {
    if (isNoSubFieldsDefined()) {
      return null;
    }

    List<String> temp = new LinkedList<>();
    Elements th = table.select("th");
    int curPos = 0;
    for (Element element : th) {
      curPos++;
      String name = getValue(element);
      if (isColumnInArray(this.columnPos, curPos)) {
        String normalized = StringUtils.lowerCase(name);
        normalized = StringUtils.replace(normalized, "-", "_");
        normalized = StringUtils.replace(normalized, " ", "_");
        LOG.debug("Column Header {} found: '{}'='{}'", curPos, name, normalized);
        temp.add(normalized);
      }
    }
    return temp.toArray(new String[0]);
  }

  private boolean isNoSubFieldsDefined() {
    return !(this.columnPos != null && this.columnPos.length > 0);
  }

  static boolean isColumnInArray(int[] array, int value) {
    return IntStream.of(array).filter(elem -> elem == value).findAny().isPresent();
  }

  private Class[] getColumnClasses(String[] fields) throws Exception {
    if (isNoSubFieldsDefined()) {
      return null;
    }
    List<Class> temp = new LinkedList<>();
    for (String field : fields) {
      temp.add(String.class);
    }
    return temp.toArray(new Class[0]);
  }
}
