package com.tcmj.pug.enums.datasources.impl;

import com.tcmj.pug.enums.datasources.tools.ReaderHelper;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.model.NameTypeValue;
import org.junit.Test;

import java.io.Reader;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/** Test of StaticDataProvider. */
public class StaticDataProviderTest {

  @Test
  public void overallTestWithoutSubfields() throws Exception {
    StaticDataProvider dataProvider = new StaticDataProvider();
    dataProvider.addConstantWithoutSubfield("Africa");
    dataProvider.addConstantWithoutSubfield("Antarctica");
    EnumData data = dataProvider.load();

    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(false));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(2));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(0));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(0));
    assertThat("getKey", Arrays.toString(data.getData().stream().map(NameTypeValue::getConstantName).toArray()), equalTo("[Africa, Antarctica]"));
  }

  @Test
  public void overallTestWithSubfields() throws Exception {
    String[] fieldNames = new String[] {"areaKM2", "areaPct", "name"};
    Class[] fieldClasses = new Class[] {Integer.class, Float.class, String.class};

    StaticDataProvider dataProvider = new StaticDataProvider( fieldNames, fieldClasses);
    dataProvider.addConstantValue("Africa", 30370000, 20.4F, "AF");
    dataProvider.addConstantValue("Antarctica", 123123123, 55.22F, "AN");
    EnumData data = dataProvider.load();

    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(true));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(2));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(3));
    assertThat("getName", Arrays.toString(data.getFieldNames()), equalTo("[areaKM2, areaPct, name]"));
    assertThat("getType", Arrays.toString(data.getFieldClasses()), equalTo("[class java.lang.Integer, class java.lang.Float, class java.lang.String]"));
    assertThat("getValue1", Arrays.toString(data.getData().stream().findFirst().get().getValue()), equalTo("[30370000, 20.4, AF]"));
    assertThat("getValue1+2", Arrays.toString(data.getData().stream().map(e -> Arrays.toString(e.getValue())).toArray()), equalTo("[[30370000, 20.4, AF], [123123123, 55.22, AN]]"));
  }

  @Test
  public void testGetResource() throws Exception {
    try (Reader reader = ReaderHelper.getResource(StaticDataProviderTest.class, "continents.json")) {
      assertThat("Reader", reader, notNullValue());
    }
  }
}
