package com.tcmj.pug.enums.datasources.impl;

import java.util.Arrays;
import com.tcmj.pug.enums.model.EnumData;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
    
    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(true));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(16));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(3));
  }
  
  @Test
  public void staticHtmlFileOffline() throws Exception {
    URL url = URLHtmlDataProvider.class.getResource("java.html");
    String myURL = url.toURI().toString();
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
