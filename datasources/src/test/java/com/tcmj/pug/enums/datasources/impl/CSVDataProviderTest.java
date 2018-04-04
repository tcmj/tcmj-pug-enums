package com.tcmj.pug.enums.datasources.impl;

import com.tcmj.pug.enums.datasources.tools.ReaderHelper;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.model.NameTypeValue;
import org.junit.Test;

import java.io.Reader;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/** Test of CSVDataProvider. */
public class CSVDataProviderTest {

  @Test
  public void overallTestWithoutSubfields() {
    Reader reader = ReaderHelper.getResource(CSVDataProviderTest.class, "continents.csv");
    String fieldNameConstant = "nameUS";
    String[] fieldNames = null;
    Class[] fieldClasses = null;

    CSVDataProvider dataProvider = new CSVDataProvider(reader, fieldNameConstant, fieldNames, fieldClasses);
    EnumData data = dataProvider.load();

    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(false));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(2));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(0));
    assertThat("getKey", Arrays.toString(data.getData().stream().map(NameTypeValue::getConstantName).toArray()), equalTo("[Africa, Antarctica]"));
  }

  @Test
  public void overallTestWithSubfields() {
    Reader reader = ReaderHelper.getResource(JsonDataProviderTest.class, "continents.csv");
    String fieldNameConstant = "nameUS";
    String[] fieldNames = new String[]{"areaKM2", "areaPct", "name"};
    Class[] fieldClasses = new Class[]{Integer.class, Float.class, String.class};

    CSVDataProvider dataProvider = new CSVDataProvider(reader, fieldNameConstant, fieldNames, fieldClasses);
    EnumData data = dataProvider.load();

    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(true));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(2));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(3));
    assertThat("getName", Arrays.toString(data.getFieldNames()), equalTo("[areaKM2, areaPct, name]"));
    assertThat("getType", Arrays.toString(data.getFieldClasses()), equalTo("[class java.lang.Integer, class java.lang.Float, class java.lang.String]"));
    assertThat("getValue", Arrays.toString(data.getData().stream().findFirst().get().getValue()), equalTo("[30370000, 20.4, AF]"));
  }
}
