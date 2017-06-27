package com.tcmj.pug.enums.datasources.impl;

import com.tcmj.pug.enums.model.ClassCreationException;
import java.util.Arrays;
import com.tcmj.pug.enums.model.EnumData;
import java.util.Objects;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/** pugproductions - 2017-05-16 - tcmj. */
public class URLHtmlDataProviderTest {

  @Test
  public void testIsColumnInArray() throws Exception {
    final int[] columnPos = new int[]{3,4,7};
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < 10; i++) {
      if (URLHtmlDataProvider.isColumnInArray(columnPos, i)) {
        result.append(i);
      }
    }
    assertThat( result.toString(), equalTo("347"));
  }
  
  
  @Test
  public void overallTestWithoutSubfields() throws Exception {

    URLHtmlDataProvider dataProvider = new URLHtmlDataProvider(
        "com.tcmj.test.MyWikipediaEnum",
        "https://en.wikipedia.org/wiki/ISO_3166-1",
        "[title=Afghanistan]",
        3,
        null);
    EnumData data = dataProvider.load();

    assertThat("getClassNameSimple", data.getClassNameSimple(), equalTo("MyWikipediaEnum"));
    assertThat("getClassName", data.getClassName(), equalTo("com.tcmj.test.MyWikipediaEnum"));
    assertThat("getPackageName", data.getPackageName(), equalTo("com.tcmj.test"));
    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(false));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(249));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(0));
    assertThat("getKey", Arrays.toString(data.getData().stream()
        .map(e -> e.getConstantName())
        .filter(s -> s.startsWith("F"))
        .toArray()),
        equalTo("[FLK, FRO, FJI, FIN, FRA, FSM]"));
  }

  @Test
  public void overallTestWithSubfields() throws Exception {
    URLHtmlDataProvider dataProvider = new URLHtmlDataProvider(
        "com.tcmj.test.MyWikipediaEnum",
        "https://en.wikipedia.org/wiki/ISO_3166-1",
        "[title=Afghanistan]",
        1,
        new int[]{2, 3, 4});
    EnumData data = dataProvider.load();

    assertThat("getClassNameSimple", data.getClassNameSimple(), equalTo("MyWikipediaEnum"));
    assertThat("getClassName", data.getClassName(), equalTo("com.tcmj.test.MyWikipediaEnum"));
    assertThat("getPackageName", data.getPackageName(), equalTo("com.tcmj.test"));
    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(true));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(249));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(3));
    assertThat("getName", Arrays.toString(data.getFieldNames()), equalTo("[alpha_2_code, alpha_3_code, numeric_code]"));
    assertThat("getType", Arrays.toString(data.getFieldClasses()), equalTo("[class java.lang.String, class java.lang.String, class java.lang.String]"));
    assertThat("getValue", Arrays.toString(data.getData().stream().findFirst().get().getValue()), equalTo("[AF, AFG, 004]"));
  }

  //@Test
  public void testGetValueSpecialCase() throws Exception {
    int columnPosConstant = 1;
    String url = "https://en.wikipedia.org/wiki/States_of_Germany";
    String cssselector = "#mw-content-text > div.mw-parser-output > table.sortable.wikitable"; //"[title=Hanover]"
    cssselector = "[title=Düsseldorf]"; //"[title=Hanover]"
    URLHtmlDataProvider cut = new URLHtmlDataProvider("a.b.c.MyEnum", url, cssselector, 1, new int[]{2, 3, 4});

    Document doc = Jsoup.connect(url).get();
    Element table = cut.locateTable(doc);
    Elements trs = table.select("tr");
    int curPos = 0;
    for (Element tr : trs) {
      curPos++;
      if (curPos == 1) {
        System.out.println("Skipping header record...");
        continue;
      }

      Element tdConstant = tr.child(columnPosConstant - 1);
      String constantName = cut.getValue(tdConstant);
      System.out.println(String.format("Record %d constant-column: '%s' using '%s'", curPos, tdConstant, constantName));
      System.out.println(String.format("'%s'", constantName));
      //      assertThat("record-should-not-empty: " + curPos, constantName, not(equalTo("")));

    }
  }

  //@todo fix  @Test
  public void testGetxxxxxValueSpecialCase() throws Exception {
    int columnPosConstant = 1;
    String url = "https://en.wikipedia.org/wiki/States_of_Germany";
    String cssselector = "29,477"; //"[title=Hanover]"

    //class="sortable wikitable jquery-tablesorter"
    Document doc = Jsoup.connect(url).get();

    Elements tables = doc.select(cssselector);
    System.out.println("tables.size()..." + tables.size());
    for (Element tbl : tables) {
      System.out.println("table:  " + tbl.cssSelector());
    }

    Element table = locateTable(doc, cssselector);
    System.out.println("table ..." + table);

  }

  static Element locateTable(Document doc, String cssSelector) throws Exception {
    Elements selectionOfAnyRecord = Objects.requireNonNull(doc.select(cssSelector), "Bad CSS selector result for: " + cssSelector);
    Element table = Objects.requireNonNull(selectionOfAnyRecord.get(0), "Bad CSS selector result for: " + cssSelector);
    boolean stillNotFound = true;
    while (stillNotFound) {
      if ("table".equalsIgnoreCase(table.tagName())) {
        stillNotFound = false;
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

}
