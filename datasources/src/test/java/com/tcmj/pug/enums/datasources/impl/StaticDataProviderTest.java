package com.tcmj.pug.enums.datasources.impl;

import com.tcmj.pug.enums.datasources.impl.StaticDataProvider;
import java.io.Reader;
import java.util.Arrays;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.datasources.tools.ReaderHelper;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/** pugproductions - 2017-05-09 - tcmj. */
public class StaticDataProviderTest {

  @Test
  public void overallTestWithoutSubfields() throws Exception {
    StaticDataProvider dataProvider = new StaticDataProvider("com.tcmj.test.MySimpleJsonEnum");
    dataProvider.addConstantWithoutSubfield("Africa");
    dataProvider.addConstantWithoutSubfield("Antarctica");
    EnumData data = dataProvider.load();

    assertThat("getClassNameSimple", data.getClassNameSimple(), equalTo("MySimpleJsonEnum"));
    assertThat("getClassName", data.getClassName(), equalTo("com.tcmj.test.MySimpleJsonEnum"));
    assertThat("getPackageName", data.getPackageName(), equalTo("com.tcmj.test"));
    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(false));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(2));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(0));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(0));
    assertThat("getKey", Arrays.toString(data.getData().stream().map(e -> e.getConstantName()).toArray()), equalTo("[Africa, Antarctica]"));
  }

  @Test
  public void overallTestWithSubfields() throws Exception {
    String fullClassName = "a.b.c.JsonEnum";
    String[] fieldNames = new String[] {"areaKM2", "areaPct", "name"};
    Class[] fieldClasses = new Class[] {Integer.class, Float.class, String.class};

    StaticDataProvider dataProvider = new StaticDataProvider(fullClassName, fieldNames, fieldClasses);
    dataProvider.addConstantValue("Africa", 30370000, 20.4F, "AF");
    dataProvider.addConstantValue("Antarctica", 123123123, 55.22F, "AN");
    EnumData data = dataProvider.load();

    assertThat("getClassNameSimple", data.getClassNameSimple(), equalTo("JsonEnum"));
    assertThat("getClassName", data.getClassName(), equalTo("a.b.c.JsonEnum"));
    assertThat("getPackageName", data.getPackageName(), equalTo("a.b.c"));
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
    Reader reader = ReaderHelper.getResource(StaticDataProviderTest.class, "continents.json");
    assertThat("Reader", reader, notNullValue());
    reader.close();
  }
}
