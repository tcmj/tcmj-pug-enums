package com.tcmj.pug.enums.datasources.impl;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.tools.EnumDataHelper;
import com.tcmj.pug.enums.api.tools.Strings;
import com.tcmj.pug.enums.model.ClassCreationException;
import com.tcmj.pug.enums.model.EnumData;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * DataProvider which loads a given URL, applies a css select expression to obtain a html table and parse
 * the table data into an EnumData model.
 * Input.1 = URL -> HTML Document
 * Input.2 = HTML.DOC -> HTML.TABLE via CSS Select
 * Input.3 = HTML.TABLE -> TR.TD (specifying one for the constants)
 */
public class URLHtmlDataProvider implements DataProvider {
  private static final transient Logger LOG = LoggerFactory.getLogger(URLHtmlDataProvider.class);
  private final String url;
  private final int columnPosConstant;
  private final int[] columnPos;
  private final String cssSelector;
  private EnumData model = new EnumData();
  private List<Integer> rowNumbersToSkip;
  private List<String> enumValueNamesToSkip;

  /**
   * Constructs a dataprovider from html.
   *
   * @param url               can be a online http url or also a file url
   * @param tableSelector     use a css selector to locate your data
   * @param columnPosConstant column no of the main enum values
   * @param columnPos         additional columns which goes into subfields
   * @param skipFirstRow      usually the first row contains labels and you want to skip it
   */
  public URLHtmlDataProvider(String url, String tableSelector, int columnPosConstant, int[] columnPos, boolean skipFirstRow) {
    this.url = Objects.requireNonNull(url, "URL cannot be null!");
    if (tableSelector == null) {
      LOG.debug("No CSS selection set! Defaulting to the first '<table>' found!");
      this.cssSelector = "table";
    } else {
      this.cssSelector = tableSelector;
    }
    this.columnPosConstant = columnPosConstant;
    this.columnPos = columnPos == null ? null : Arrays.copyOf(columnPos, columnPos.length);
    if (skipFirstRow) {
      List<String> lst = new ArrayList<>();
      lst.add("#1");
      setValuesToSkip(lst);
    }
  }

  /**
   * Constructs a dataprovider from html.
   * Please note that the first row will be treated as labels and so be skipped.
   * @param url can be a online http url or also a file url
   * @param tableSelector use a css selector to locate your data
   * @param columnPosConstant column no of the main enum values
   * @param columnPos additional columns which goes into subfields
   */
  public URLHtmlDataProvider(String url, String tableSelector, int columnPosConstant, int[] columnPos) {
    this(url, tableSelector, columnPosConstant, columnPos, true);
  }

  private static Path tryToGetAbsolutePath(Path path) {
    try {
      return path.toAbsolutePath();
    } catch (Exception e) {
      return path;
    }
  }

  /** Parse a html document either from a http url or a file. */
  Document getDocument(String urlToLoad) throws IOException {
    if (StringUtils.startsWithAny(urlToLoad, "http:", "https:")) {
      LOG.info("Connecting to... '{}'", urlToLoad);
      return Jsoup.connect(urlToLoad).get();
    }
    Path path = Paths.get(URI.create(urlToLoad));
    LOG.info("Loading... '{}'", tryToGetAbsolutePath(path));
    try (InputStream inStream = Files.newInputStream(path)) {
      String encoding = Charset.defaultCharset().name();
      return Jsoup.parse(inStream, encoding, "");
    }
  }

  private Element locateTable(Document doc) {

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
          throw new ClassCreationException("Cannot locate table by going upwards!", e);
        }
      }
    }
    return table;
  }

  private boolean noSubFieldsDefined() {
    return !subfieldsDefined();
  }

  private boolean subfieldsDefined() {
    return this.columnPos != null && this.columnPos.length > 0;
  }

  @Override
  public EnumData load() {
    try {
      Document doc = getDocument(this.url);

      Element table = locateTable(doc);
      String[] columnNames = getColumnNames(table);

      if (columnNames != null && columnNames.length != columnPos.length) {
        throw new ClassCreationException(String.format(
          "Amount of configured subfield columns (%d) does not match with header columns found (%d)!",
          columnPos.length, columnNames.length));
      }

      model.setFieldNames(columnNames);
      Class[] columnClasses = getColumnClasses(columnNames);
      model.setFieldClasses(columnClasses);

      getRecordData(table);

    } catch (Exception e) {
      throw new ClassCreationException(e);
    }
    return model;
  }

  private void getRecordData(Element table) {
    Elements trs = table.select("tr");
    int curPos = 0;
    for (Element tr : trs) {
      curPos++;

      if (rowNumbersToSkip != null && rowNumbersToSkip.contains(curPos)) {
        LOG.info("Skipping row number {}", curPos);
        continue;
      }
      Element tdConstant = tr.child(this.columnPosConstant - 1);
      final String constantName = getValue(tdConstant);

      if (enumValueNamesToSkip != null && enumValueNamesToSkip.stream()
        .anyMatch(s -> StringUtils.equalsIgnoreCase(s, constantName))) {
        LOG.info("Skipping row no {} with value '{}'", curPos, constantName);
        continue;
      }

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
  String getValue(Element element) {
    String value = Jsoup.clean(element.html(), Whitelist.none());
    if (value == null || value.isEmpty()) {
      value = element.text();
    }
    return Strings.replaceAllWhitespace(value);
  }

  /**
   * Try to parse the column names from the html table used for the subfields (if there are any).
   * Usually from the `th-tags` but if there are none (we saw this in fancy floating column-name tables) - we
   * have to work around this problem.
   * Technically we need the same amount of columns as defined in {@link #columnPos}.
   *
   * @param table html table object
   * @return string array containing table column names
   */
  String[] getColumnNames(Element table) {
    if (noSubFieldsDefined()) {
      return null; //we can skip this if we have no subfields
    }
    List<String> temp = new LinkedList<>();

    //try to do the job selecting all th columns
    Elements th = table.select("th");
    for (int curPos = 0; curPos < th.size(); curPos++) {
      Element element = th.get(curPos);
      String name = getValue(element);
      if (isColumnInArray(this.columnPos, curPos + 1)) {
        String normalized = StringUtils.lowerCase(name);
        normalized = StringUtils.replace(normalized, "-", "_");
        normalized = StringUtils.replace(normalized, " ", "_");
        normalized = StringUtils.replace(normalized, "(", "_");
        normalized = StringUtils.replace(normalized, "[", "_");
        normalized = StringUtils.replace(normalized, ")", "_");
        normalized = StringUtils.replace(normalized, "]", "_");
        normalized = StringUtils.replaceAll(normalized, "__", "_");

        normalized = normalized.replaceAll("[^a-zA-Z0-9_]", "");

        //remove trailing underscores
        if (normalized.charAt(normalized.length() - 1) == '_') {
          normalized = normalized.substring(0, normalized.length() - 1);
        }

        LOG.debug("Column Header {} found: '{}'='{}'", curPos, name, normalized);
        temp.add(normalized);
      }
    }

    //fallback-case
    if (temp.isEmpty()) {
      //no column headers (th-tags) found! we have to do something else
      int columnAmount = this.columnPos.length;
      for (int i = 1; i <= columnAmount; i++) {
        temp.add("column" + i);
      }
    }

    return temp.toArray(new String[0]);
  }


  static boolean isColumnInArray(int[] array, int value) {
    return IntStream.of(array).filter(elem -> elem == value).findAny().isPresent();
  }

  private Class[] getColumnClasses(String[] fields) {
    if (noSubFieldsDefined() || fields == null) {
      return null;
    }
    List<Class> temp = new LinkedList<>();
    Stream.of(fields).forEach((s) -> temp.add(String.class));
    return temp.toArray(new Class[0]);
  }

  public final void setValuesToSkip(List<String> valuesToSkip) {
    if (valuesToSkip == null || valuesToSkip.isEmpty()) {
      return;
    }
    List<Integer> lstRowNumbers = new ArrayList<>();
    List<String> lstEnumValueNames = new ArrayList<>();
    for (String value : valuesToSkip) {
      if (StringUtils.startsWith(value, "#")) {
        try {
          Integer rowNo = Integer.parseInt(value.substring(1));
          lstRowNumbers.add(rowNo);
        } catch (Exception ex) {
          LOG.error("Invalid rownumber specified to skip: '{}'! Write '#3' to skip row number 3!", value, ex);
        }
      } else {
        lstEnumValueNames.add(value);
      }
    }
    this.enumValueNamesToSkip = lstEnumValueNames;
    this.rowNumbersToSkip = lstRowNumbers;
  }
}
