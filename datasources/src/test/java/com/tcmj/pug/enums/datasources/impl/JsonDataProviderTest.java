package com.tcmj.pug.enums.datasources.impl;

import com.tcmj.pug.enums.datasources.impl.JsonDataProvider;
import java.io.Reader;
import java.util.Arrays;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.datasources.tools.ReaderHelper;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * GSON - Json Data Provider Tests and Usages needs com.google.code.gson:gson as runtime dependency
 */
public class JsonDataProviderTest {

  @Test
  public void overallTestWithoutSubfields() throws Exception {
    Reader reader = ReaderHelper.getResource(JsonDataProviderTest.class, "continents.json");
    String fieldNameConstant = "nameUS";
    String[] fieldNames = null;
    Class[] fieldClasses = null;

    JsonDataProvider dataProvider =
        new JsonDataProvider(
            "com.tcmj.test.MySimpleJsonEnum", reader, fieldNameConstant, fieldNames, fieldClasses);
    EnumData data = dataProvider.load();

    assertThat("getClassNameSimple", data.getClassNameSimple(), equalTo("MySimpleJsonEnum"));
    assertThat("getClassName", data.getClassName(), equalTo("com.tcmj.test.MySimpleJsonEnum"));
    assertThat("getPackageName", data.getPackageName(), equalTo("com.tcmj.test"));
    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(false));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(2));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(0));
    assertThat("getKey", Arrays.toString(data.getData().stream().map(e -> e.getConstantName()).toArray()), equalTo("[Africa, Antarctica]"));
  }

  @Test
  public void overallTestWithSubfields() throws Exception {
    String fullClassName = "a.b.c.JsonEnum";
    Reader reader = ReaderHelper.getResource(JsonDataProviderTest.class, "continents.json");
    String fieldNameConstant = "nameUS";
    String[] fieldNames = new String[] {"areaKM2", "areaPct", "name"};
    Class[] fieldClasses = new Class[] {Integer.class, Float.class, String.class};

    JsonDataProvider dataProvider =
        new JsonDataProvider(fullClassName, reader, fieldNameConstant, fieldNames, fieldClasses);
    EnumData data = dataProvider.load();

    assertThat("getClassNameSimple", data.getClassNameSimple(), equalTo("JsonEnum"));
    assertThat("getClassName", data.getClassName(), equalTo("a.b.c.JsonEnum"));
    assertThat("getPackageName", data.getPackageName(), equalTo("a.b.c"));
    assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(true));
    assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(2));
    assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(3));
    assertThat("getName", Arrays.toString(data.getFieldNames()), equalTo("[areaKM2, areaPct, name]"));
    assertThat("getType", Arrays.toString(data.getFieldClasses()), equalTo("[class java.lang.Integer, class java.lang.Float, class java.lang.String]"));
    assertThat("getValue", Arrays.toString(data.getData().iterator().next().getValue()),equalTo("[30370000, 20.4, AF]"));
  }

  @Test
  public void testGetResource() throws Exception {
    Reader reader = ReaderHelper.getResource(JsonDataProviderTest.class, "continents.json");
    assertThat("Reader", reader, notNullValue());
    reader.close();
  }
}
