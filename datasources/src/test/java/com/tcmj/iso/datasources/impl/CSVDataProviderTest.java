package com.tcmj.iso.datasources.impl;

import java.io.Reader;
import java.util.Arrays;
import com.tcmj.iso.api.model.EnumData;
import com.tcmj.iso.datasources.tools.ReaderHelper;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/** pugproductions - 2017-05-16 - tcmj. */
public class CSVDataProviderTest {

  @Test
  public void overallTestWithoutSubfields() throws Exception {
    Reader reader = ReaderHelper.getResource(CSVDataProviderTest.class, "continents.csv");
    String fieldNameConstant = "nameUS";
    String[] fieldNames = null;
    Class[] fieldClasses = null;

    CSVDataProvider dataProvider =
        new CSVDataProvider(
            "com.tcmj.test.MySimpleCsvEnum", reader, fieldNameConstant, fieldNames, fieldClasses);
    EnumData data = dataProvider.load();

    assertThat("getClassNameSimple", data.getClassNameSimple(), equalTo("MySimpleCsvEnum"));
    assertThat("getClassName", data.getClassName(), equalTo("com.tcmj.test.MySimpleCsvEnum"));
    assertThat("getPackageName", data.getPackageName(), equalTo("com.tcmj.test"));
    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(false));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(2));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(0));
    assertThat(
        "getKey",
        Arrays.toString(data.getData().entrySet().stream().map(e -> e.getKey()).toArray()),
        equalTo("[Africa, Antarctica]"));
  }

  @Test
  public void overallTestWithSubfields() throws Exception {
    String fullClassName = "a.b.c.JsonEnum";
    Reader reader = ReaderHelper.getResource(JsonDataProviderTest.class, "continents.csv");
    String fieldNameConstant = "nameUS";
    String[] fieldNames = new String[] {"areaKM2", "areaPct", "name"};
    Class[] fieldClasses = new Class[] {Integer.class, Float.class, String.class};

    CSVDataProvider dataProvider =
        new CSVDataProvider(fullClassName, reader, fieldNameConstant, fieldNames, fieldClasses);
    EnumData data = dataProvider.load();

    assertThat("getClassNameSimple", data.getClassNameSimple(), equalTo("JsonEnum"));
    assertThat("getClassName", data.getClassName(), equalTo("a.b.c.JsonEnum"));
    assertThat("getPackageName", data.getPackageName(), equalTo("a.b.c"));
    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(true));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(2));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(3));
    assertThat(
        "getName",
        Arrays.toString(data.getData().entrySet().iterator().next().getValue().getName()),
        equalTo("[areaKM2, areaPct, name]"));
    assertThat(
        "getType",
        Arrays.toString(data.getData().entrySet().iterator().next().getValue().getType()),
        equalTo("[class java.lang.Integer, class java.lang.Float, class java.lang.String]"));
    assertThat(
        "getValue",
        Arrays.toString(data.getData().entrySet().iterator().next().getValue().getValue()),
        equalTo("[30370000, 20.4, AF]"));
  }
}
