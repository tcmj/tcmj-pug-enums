package com.tcmj.pug.enums.api;

import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.api.tools.EnumDataHelper;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/** Test of DataProvider. */
public class DataProviderTest {

  @Test
  public void testLoadEmptyProvider() throws Exception {
    DataProvider provider = EnumData::new;
    assertThat(provider.load().getData(), not(hasItem(anything())));
  }

  @Test
  public void testLoadSimpleProvider() throws Exception {
    DataProvider provider =
        () -> {
          EnumData enumData = new EnumData();
          EnumDataHelper.addConstantWithoutSubfield(enumData, "EINS");
          EnumDataHelper.addConstantWithoutSubfield(enumData, "ZWEI");
          EnumDataHelper.addConstantWithoutSubfield(enumData, "DREI");
          return enumData;
        };
    List<String> data = provider.load().getData().stream().map((t) ->  t.getConstantName()).collect(Collectors.toList());
    assertThat(data, hasItem(anything()));
    assertThat("provider", data, hasItems("ZWEI", "EINS", "DREI"));
    assertThat("provider.size", data.size(), is(3));
  }

  @Test
  public void testAnd() throws Exception {
    DataProvider provider1 =
        () -> {
          EnumData data = new EnumData();
          data.setFieldNames("numbers", "colors");
          data.setFieldClasses(Integer.class, String.class);
          EnumDataHelper.addConstantValue(data, "A", 1, "red");
          EnumDataHelper.addConstantValue(data, "B", 2, "blue");
          EnumDataHelper.addConstantValue(data, "C", 3, "green");
          return data;
        };
    DataProvider provider2 =
        () -> {
          EnumData data = new EnumData();
          data.setFieldNames("numbers", "colors");
          data.setFieldClasses(Integer.class, String.class);
          EnumDataHelper.addConstantValue(data, "D", 4, "black");
          EnumDataHelper.addConstantValue(data, "E", 5, "orange");
          EnumDataHelper.addConstantValue(data, "F", 6, "yellow");
          return data;
        };

    DataProvider provider = provider1.and(provider2);
    List<String> data = provider.load().getData().stream().map((t) -> t.getConstantName()).collect(Collectors.toList());
    assertThat(data, hasItem("A"));

    List<String> data1 = provider1.load().getData().stream().map((t) -> t.getConstantName()).collect(Collectors.toList());
    assertThat("provider1 == A,B,C", data1, hasItems("A", "B", "C"));
    assertThat("provider1 <> D,E,F", data1, not(hasItems("D", "E", "F")));
    assertThat("provider1.size", data1.size(), is(3));

    List<String> data2 = provider2.load().getData().stream().map((t) -> t.getConstantName()).collect(Collectors.toList());
    assertThat("provider2 == A,B,C", data2, hasItems("D", "E", "F"));
    assertThat("provider2 <> A,B,C", data2, not(hasItems("A", "B", "C")));
    assertThat("provider2.size", data2.size(), is(3));

    assertThat("provider", data, hasItems("A", "B", "C", "D", "E", "F"));
    assertThat("provider2.size", data.size(), is(6));
  }
}
