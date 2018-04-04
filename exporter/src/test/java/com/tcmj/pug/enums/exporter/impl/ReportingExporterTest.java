package com.tcmj.pug.enums.exporter.impl;

import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.EnumResult;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/** Unit Tests and Usage Examples of {@link ReportingEnumExporter}. */
public class ReportingExporterTest {

  private TestDataProvider data = new TestDataProvider();

  @Test
  public void shouldAlsoWorkUsingGlobalOptions() {
    new ReportingEnumExporter().export(EnumResult.of(data.getExtendedEnum()).addOption(ReportingEnumExporter.OPTION_LOG_LEVEL, ReportingEnumExporter.LogLevel.ERROR.name()));
  }

  @Test
  public void shouldBePossibleToUseReportingEnumExporterWithoutAnyOptions() {
    new ReportingEnumExporter().export(EnumResult.of(data.getExtendedEnum()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldResultInAnIllegalArgumentExceptionIfUsingUnknownLogLevels() {
    EnumExporter exporter = new ReportingEnumExporter("THIS_IS_NOT_POSSIBLE");
    exporter.export(EnumResult.of(data.getSimpleEnum()));
    fail("It should not be possible to set the LogLevel option to 123! Only allowed values are 'LogLevel.values()'!");
  }

  @Test
  public void shouldConstructableByStringsSystemOut() {
    System.err.println("System.out.println:");
    EnumExporter exporter = new ReportingEnumExporter("System_Out");
    exporter.export(EnumResult.of(data.getSimpleEnum()));
  }

  @Test
  public void shouldConstructableByStringsSystemErr() {
    System.err.println("System.err.println:");
    EnumExporter exporter = new ReportingEnumExporter("System_ERR");
    exporter.export(EnumResult.of(data.getSimpleEnum()));
  }

  @Test
  public void shouldBePossibleToCombineSeveralExporters() {
    EnumResult eResult = EnumResult.of(data.getExtendedEnum());

    EnumExporter exporter1 = new ReportingEnumExporter(ReportingEnumExporter.LogLevel.ERROR);
    EnumExporter exporter2 = new ReportingEnumExporter(ReportingEnumExporter.LogLevel.WARN);
    EnumExporter exporter3 = new ReportingEnumExporter(ReportingEnumExporter.LogLevel.DEBUG);

    EnumResult chain = exporter1.and(exporter2).and(exporter3).export(eResult);
    System.out.println(chain);
    assertThat("chainData", chain, notNullValue());
  }

  @Test
  public void shouldBePossibleToUseSetCurrentLogLevel() {
    ReportingEnumExporter exporter = new ReportingEnumExporter(ReportingEnumExporter.LogLevel.ERROR);
    assertThat("LogLevel should be ERROR", exporter.getCurrentLogLevel(), is(ReportingEnumExporter.LogLevel.ERROR));

    exporter.setCurrentLogLevel(ReportingEnumExporter.LogLevel.DEBUG);
    assertThat("LogLevel should be DEBUG", exporter.getCurrentLogLevel(), is(ReportingEnumExporter.LogLevel.DEBUG));
  }

}
