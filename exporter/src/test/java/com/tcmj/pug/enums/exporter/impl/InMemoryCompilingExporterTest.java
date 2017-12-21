package com.tcmj.pug.enums.exporter.impl;

import com.tcmj.pug.enums.api.EnumResult;
import com.tcmj.pug.enums.model.EnumData;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/** Unit Tests and Usage Examples of {@link InMemoryCompilingExporter}. */
public class InMemoryCompilingExporterTest {

  TestDataProvider data = new TestDataProvider();

  private EnumResult getTestEnumResult(String fullClassName, String enumDataContent) {
    EnumData fakeData = new EnumData();
    fakeData.setClassName(fullClassName);
    return EnumResult.of(fakeData, enumDataContent);
  }

  @Test
  public void testExportSimpleEnum() throws Exception {
    InMemoryCompilingExporter exporter = new InMemoryCompilingExporter();
    EnumResult enumResult = getTestEnumResult("one.two.three.SimpleEnum", data.getSimpleEnum());
    exporter.export(enumResult);
    assertThat("class", exporter.getEnumClass().getName(), equalTo("one.two.three.SimpleEnum"));
    assertThat("content", exporter.getEnumConstants().toString(), equalTo("[ONE, TWO, THREE]"));
  }

  @Test
  public void testExportExtendedEnum() throws Exception {
    EnumResult enumResult = getTestEnumResult("org.Animal", data.getExtendedEnum());
    InMemoryCompilingExporter exporter = new InMemoryCompilingExporter();
    exporter.export(enumResult);
    assertThat("class", exporter.getEnumClass().getName(), equalTo("org.Animal"));
    assertThat("content", exporter.getEnumConstants().toString(), equalTo("[DOG]"));
  }

  @Test
  public void testExportEnumWithoutPackage() throws Exception {
    EnumResult enumResult = getTestEnumResult("NoPckEnum", data.getEnumWithoutPackage());
    InMemoryCompilingExporter exporter = new InMemoryCompilingExporter();
    exporter.export(enumResult);
    assertThat("class", exporter.getEnumClass().getName(), equalTo("NoPckEnum"));
    assertThat("content", exporter.getEnumConstants().toString(), equalTo("[ONE, TWO, THREE]"));
  }

  @Test
  public void testExportWithoutOptions() throws Exception {
    InMemoryCompilingExporter exporter = new InMemoryCompilingExporter();
    String inputData = data.getSimpleEnum();
    EnumResult exported = exporter.export(EnumResult.of(data.getSimpleEnum()));
    assertThat("Chaining", exported.getResult(), equalTo(inputData));
  }

  @Test(expected = NullPointerException.class)
  public void testExportNull() throws Exception {
    /* Exporting a null value in the EnumResult will be detected and answered with a NullPointerException! */
    EnumResult enumResult = EnumResult.of(null);
    InMemoryCompilingExporter exporter = new InMemoryCompilingExporter();
    String inputData = data.getSimpleEnum();
    EnumResult export = exporter.export(enumResult);
  }
}
