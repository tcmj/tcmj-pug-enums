package com.tcmj.iso.exporter.impl;

import java.util.Map;
import com.tcmj.pug.enums.api.EnumExporter;
import org.junit.Before;
import org.junit.Test;

import static com.tcmj.iso.exporter.impl.ReportingEnumExporter.LogLevel.SYSTEM_ERR;
import static com.tcmj.iso.exporter.impl.ReportingEnumExporter.createLogLevelOption;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/** Unit Tests and Usage Examples of {@link ReportingEnumExporter}. */
public class ReportingExporterTest {

  TestDataProvider data = new TestDataProvider();
  EnumExporter exporter;

  @Before
  public void setupEach() throws Exception {
    exporter = new ReportingEnumExporter();
  }

  @Test
  public void testExportSimpleEnumUsingINFOLogLevel() throws Exception {
    Map<String, Object> options = exporter.createOptions("info");
    assertThat("chainData", exporter.export(data.getSimpleEnum(), options), notNullValue());
  }

  @Test
  public void testExportExtendedEnumUsingERRORLogLevel() throws Exception {
    Map<String, Object> options = exporter.createOptions("ERROR");
    assertThat("chainData", exporter.export(data.getExtendedEnum(), options), notNullValue());
  }

  @Test
  public void shouldReportUsingSystemOutPrintln() throws Exception {
    Map<String, Object> options =
        exporter.createOptions(ReportingEnumExporter.LogLevel.SYSTEM_OUT.name());
    assertThat("chainData", exporter.export(data.getExtendedEnum(), options), notNullValue());
  }

  @Test
  public void shouldReportUsingSystemErrPrintln() throws Exception {
    new ReportingEnumExporter().export(data.getUnformatedEnum(), createLogLevelOption(SYSTEM_ERR));
  }

  @Test
  public void shouldBePossibleToUseReportingEnumExporterWithNullOptions() throws Exception {
    EnumExporter exporter = new ReportingEnumExporter();
    assertThat("chainData", exporter.export(data.getEnumWithoutPackage(), null), notNullValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExportWithInvalidNonExistingOptionValue() throws Exception {
    EnumExporter exporter = new ReportingEnumExporter();
    exporter.export(data.getSimpleEnum(), exporter.createOptions("123"));
    fail(
        "It should not be possible to set the LogLevel option to 123! Only allowed values are 'LogLevel.values()'!");
  }

  @Test
  public void testChainingAbilityOfReportingEnumExporter() throws Exception {
    EnumExporter exporter1 = new ReportingEnumExporter("1");
    Map<String, Object> opt1 = exporter1.createOptions("error");
    EnumExporter exporter2 = new ReportingEnumExporter("2");
    Map<String, Object> opt2 = exporter2.createOptions("warn");
    EnumExporter exporter3 = new ReportingEnumExporter("3");
    Map<String, Object> opt3 = exporter3.createOptions("debug");

    final String enumString = data.getSimpleEnum();

    String chain = exporter1.and(exporter2, opt2).and(exporter3, opt3).export(enumString, opt1);
    System.out.println(chain);
    assertThat("chainData", chain, notNullValue());
  }
}
