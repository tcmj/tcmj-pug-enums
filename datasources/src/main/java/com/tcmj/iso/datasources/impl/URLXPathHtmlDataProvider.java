package com.tcmj.iso.datasources.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import com.tcmj.iso.api.DataProvider;
import com.tcmj.iso.api.model.ClassCreationException;
import com.tcmj.iso.api.model.EnumData;
import com.tcmj.iso.api.tools.EnumDataHelper;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dataprovider which loads a given URL, applies a xpath expression to obtain
 * a html table and parse the table data into an EnumData model.
 * Input.1 = URL        -> HTML Document
 * Input.2 = HTML.DOC   -> HTML.TABLE via XPath
 * Input.3 = HTML.TABLE -> TR.TD (specifying one for the constants)
 * https://en.wikipedia.org/wiki/ISO_3166-1
 */
public class URLXPathHtmlDataProvider implements DataProvider {

    private static final transient Logger LOG = LoggerFactory.getLogger(URLXPathHtmlDataProvider.class);
    EnumData model = new EnumData();
    final String url;
    final int columnPosConstant;
    final int[] columnPos;
    final String xPathTable;

    public URLXPathHtmlDataProvider(String fullClassName, String url, String tableSelector, int columnPosConstant, int[] columnPos) {
        model.setPackageName(EnumDataHelper.extractPackage(fullClassName));
        model.setClassName(EnumDataHelper.extractSimpleClassName(fullClassName));
        this.url = Objects.requireNonNull(url, "URL cannot be null!");
        this.xPathTable = Objects.requireNonNull(tableSelector, "XPath selector for table cannot be null!");
        this.columnPosConstant = Objects.requireNonNull(columnPosConstant, "Column pos constant cannot be null!");
        this.columnPos = columnPos; //column indexes to take

    }

    private Element locateTable(Document doc) throws Exception {
        Elements selectionOfAnyRecord = doc.select(this.xPathTable);
        System.out.println(selectionOfAnyRecord);
        Element table = selectionOfAnyRecord.get(0);
        boolean stillNotFound = true;
        while (stillNotFound) {
            if ("table".equalsIgnoreCase(table.tagName())) {
                stillNotFound = false;
                LOG.debug("Tag successfully found: <{}>", table.tag());
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

    @Override public EnumData load() {
        try {
            LOG.debug("Connection URL: {}", this.url);
            Document doc = Jsoup.connect(this.url).get();

            Element table = locateTable(doc);

            String[] columnNames = getColumnNames(table);

            model.setFieldNames(columnNames);
            model.setFieldClasses(getColumnClasses(columnNames));

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
            if (curPos == 1) {
                LOG.trace("Skipping header record...");
                continue;
            }

            Element tdConstant = tr.child(this.columnPosConstant - 1);
            LOG.trace("Column for constants: {}",tdConstant);
            String constantName = getValue(tdConstant);

            if (this.model.isEnumWithSubfields()) {
                Object[] values = new Object[columnPos.length];
                for (int i = 0; i < columnPos.length; i++) {
                    int pos = columnPos[i] - 1;
                    Element td = tr.child(pos);
                    String value = getValue(td);
                    values[i] = value;
                }
                LOG.debug("addConstantValue(EnumData,{},{})", constantName, Arrays.toString(values));
                EnumDataHelper.addConstantValue(model, constantName, values);
            } else {
                LOG.debug("addConstantWithoutSubfield(EnumData,{})", constantName);
                EnumDataHelper.addConstantWithoutSubfield(model, constantName);
            }
        }
    }

    private String getValue(Element element) throws Exception {
        String value = null;
        if (element.hasText()) {
            if(element.children().size()==1){
                value = element.text();
            }else{
                for (Element child : element.children()) {
                    String valueC = child.text();
                    if(valueC!=null && valueC.length()>0 && (value==null || value.length() < valueC.length())){
                        value = valueC;
                    }
                }
            }
        } else {
            Elements link = element.select("a[href]");
            value = link.text();
        }
        if(value==null){
            value = element.text();
        }
        return value;
    }

    private String[] getColumnNames(Element table) throws Exception {
        List<String> temp = new LinkedList<>();
        Elements th = table.select("th");
        int curPos = 0;
        for (Element element : th) {
            curPos++;
            String name;
            if (element.hasText()) {
                name = element.text();
            } else {
                Elements link = element.select("a[href]");
                name = link.text();
            }
            if (this.columnPos != null && this.columnPos.length > 0) {
                if (Arrays.binarySearch(this.columnPos, curPos) >= 0) {
                    String normalized = StringUtils.lowerCase(name);
                    normalized = StringUtils.replace(normalized, "-", "_");
                    normalized = StringUtils.replace(normalized, " ", "_");
                    LOG.debug("Column Header found: '{}'='{}'", name, normalized);
                    temp.add(normalized);
                }
            }
        }
        return temp.toArray(new String[0]);
    }

    private Class[] getColumnClasses(String[] fields) throws Exception {
        List<Class> temp = new LinkedList<>();
        for (String field : fields) {
            temp.add(String.class);
        }
        return temp.toArray(new Class[0]);
    }


}
