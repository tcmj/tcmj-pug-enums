package com.tcmj.pug.enums.datasources.impl;

import com.tcmj.pug.enums.model.EnumData;
import org.hamcrest.CoreMatchers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class URLHtmlDataProviderTest {

  private URLHtmlDataProvider getDataProvider() {
    return new URLHtmlDataProvider(
        "https://en.wikipedia.org/wiki/States_of_Germany",
        "table.sortable",
        1, //enum constant column
        new int[]{1, 2, 3} //sub columns
    );
  }

  @Test
  public void testIsColumnInArrayUsingOrderedList() {
    final int[] columnPos = new int[]{1, 2, 3}; //given is a ordered list
    IntStream.of(3, 2, 1).forEach((value) -> assertThat("value=" + value, URLHtmlDataProvider.isColumnInArray(columnPos, value), is(true)));
    assertThat("Not in List", URLHtmlDataProvider.isColumnInArray(columnPos, 9), is(false));
  }

  @Test
  public void testIsColumnInArrayUsingUnorderedList() {
    final int[] columnPos = new int[]{2, 1, 3}; //given is a un-ordered list
    IntStream.of(3, 2, 1).forEach((value) -> assertThat("value=" + value, URLHtmlDataProvider.isColumnInArray(columnPos, value), is(true)));
    assertThat("Not in List", URLHtmlDataProvider.isColumnInArray(columnPos, 7), is(false));
  }

  @Test
  public void testGetColumnNames() throws Exception {
    URLHtmlDataProvider dataProvider = getDataProvider();
    Document doc = Jsoup.parse("<html><table id='mytbl'><tr><th>A</th><th>B</th><th>C</th></tr><tr><td>valueA</td><td>valueB</td><td>valueC</td></tr></table></html>");
    String[] columnNames = dataProvider.getColumnNames(doc.getElementById("mytbl"));
    assertThat(Stream.of(columnNames).collect(Collectors.toList()), CoreMatchers.hasItems("a", "b", "c"));
  }

  public static Document getMockHtmlFile(String filename) throws Exception {
    return Jsoup.parse(URLHtmlDataProvider.class.getResourceAsStream(filename), "UTF-8", "");
  }

  @Test
  public void overallTestWithoutSubfields() throws Exception {
    URLHtmlDataProvider dataProvider = spy(new URLHtmlDataProvider(
        "mockedunittest",
        "[title=Afghanistan]",
        3,
        null));

    doReturn(getMockHtmlFile("country.html")).when(dataProvider).getDocument("mockedunittest");

    EnumData data = dataProvider.load();

    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(false));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(3));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(0));
    assertThat("getKey", Arrays.toString(data.getData().stream()
        .map(e -> e.getConstantName())
        .filter(s -> s.startsWith("D"))
        .toArray()),
        equalTo("[DMA, DOM]"));
  }

  @Test
  public void overallTestWithSubfields() throws Exception {
    URLHtmlDataProvider dataProvider = spy(new URLHtmlDataProvider(
        "https://ttt.wikipedia.org/wiki/ISO_3166-1",
        "[title=Afghanistan]",
        1,
        new int[]{2, 3, 4}));

    doReturn(getMockHtmlFile("country.html")).when(dataProvider).getDocument("https://ttt.wikipedia.org/wiki/ISO_3166-1");

    EnumData data = dataProvider.load();

    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(true));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(3));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(3));
    assertThat("getName", Arrays.toString(data.getFieldNames()), equalTo("[alpha_2_code, alpha_3_code, numeric_code]"));
    assertThat("getType", Arrays.toString(data.getFieldClasses()), equalTo("[class java.lang.String, class java.lang.String, class java.lang.String]"));
    assertThat("getValue", Arrays.toString(data.getData().stream().findFirst().get().getValue()), equalTo("[AF, AFG, 004]"));
  }

  @Test
  public void testGetValueSpecialCase() throws Exception {
    URLHtmlDataProvider cut = spy(new URLHtmlDataProvider("States_of_Germany", "table.sortable", 3, new int[]{2, 3, 4}));//"a.b.c.MyEnum", 
    doReturn(getMockHtmlFile("states.html")).when(cut).getDocument("States_of_Germany");

    EnumData data = cut.load();
    data.getData().stream().forEach(System.out::println);
    
    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(true));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(16));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(3));
  }

  private static Element buildTestElement(String html) {
    Document document = Jsoup.parseBodyFragment("<body><table>" + html + "</table></body>");
    return document.select("table").select("tr").get(0).child(0);
  }

  @Test
  public void testGetValueSpecialCase001() throws Exception {
    assertThat("UnexpectedResult", getDataProvider().getValue(buildTestElement("<td class=\"abs\"><a href=\"bahamas.htm\">Bahamas</a></td>")), equalTo("Bahamas"));
  }

  @Test
  public void testGetValueSpecialCase002() throws Exception {
    URLHtmlDataProvider ccc = getDataProvider();
    String testValue = "<td style=\"text-align:right\"><span style=\"display:none\" class=\"sortkey\">7007112898530000000♠</span>11,289,853</td>";
    Element element = buildTestElement(testValue);
    String value = ccc.getValue(element);
    assertThat("UnexpectedResult", value, equalTo("11,289,853"));
  }

  @Test
  public void testGetValueSpecialCase003() throws Exception {
    String testValue1 = "<td><a href=\"/wiki/Mayotte\" title=\"Mayotte\">Mayotte</a></td>";
    String testValue2 = "<td><a href=\"/wiki/ISO_3166-1_alpha-2#YT\" title=\"ISO 3166-1 alpha-2\"><span style=\"font-family: monospace, monospace;\">YT</span></a></td>";
    String testValue3 = "<td><span style=\"font-family: monospace, monospace;\">MYT</span></td>";
    String testValue4 = "<td><span style=\"font-family: monospace, monospace;\">175</span></td>";
    String testValue5 = "<td><a href=\"/wiki/ISO_3166-2:YT\" title=\"ISO 3166-2:YT\">ISO 3166-2:YT</a></td>";
    String testValue6 = "<td style=\"background:#F99;vertical-align:middle;text-align:center;\" class=\"table-no\">No</td>";

    URLHtmlDataProvider ccc = getDataProvider();
    assertThat("R1", ccc.getValue(buildTestElement(testValue1)), equalTo("Mayotte"));
    assertThat("R2", ccc.getValue(buildTestElement(testValue2)), equalTo("YT"));
    assertThat("R3", ccc.getValue(buildTestElement(testValue3)), equalTo("MYT"));
    assertThat("R4", ccc.getValue(buildTestElement(testValue4)), equalTo("175"));
    assertThat("R5", ccc.getValue(buildTestElement(testValue5)), equalTo("ISO 3166-2:YT"));
    assertThat("R6", ccc.getValue(buildTestElement(testValue6)), equalTo("No"));
  }

  @Test
  public void testGetValueSpecialCase004() throws Exception {
    String testValue = "<td><a href=\"/wiki/Bavaria\">Bavaria</a><br>\n" +
        "                        (<i>Freistaat Bayern</i>)</td>";
    String result = getDataProvider().getValue(buildTestElement(testValue));
    assertThat("UnexpectedResult", result,
        anyOf(equalTo("Bavaria"), equalTo("Freistaat Bayern")));
  }

  @Test
  public void staticHtmlFileOffline() throws Exception {
    URL url = URLHtmlDataProvider.class.getResource("java.html");
    String myURL = url.toString();
    String mySelector = null;
    assertThat("HtmlTestFile must be available", Files.isRegularFile(Paths.get(URI.create(myURL))), is(true));
    URLHtmlDataProvider dataProvider =  new URLHtmlDataProvider( myURL, mySelector, 1, new int[]{2, 3} );
    EnumData data = dataProvider.load();
    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(true));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(3));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(2));
    assertThat("getName", Arrays.toString(data.getFieldNames()), equalTo("[codename, date]"));
    assertThat("getType", Arrays.toString(data.getFieldClasses()), equalTo("[class java.lang.String, class java.lang.String]"));
    assertThat("getConstantName", data.getData().stream().findFirst().get().getConstantName(), equalTo("JDK 1.0"));
    assertThat("getValue", Arrays.toString(data.getData().stream().findFirst().get().getValue()), equalTo("[Oak, 23.05.1995]"));
  }

  
}
