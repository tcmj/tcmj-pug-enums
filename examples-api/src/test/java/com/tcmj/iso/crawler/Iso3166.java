package com.tcmj.iso.crawler;

import com.tcmj.iso.api.DataProvider;
import com.tcmj.iso.api.EnumExporter;
import com.tcmj.iso.api.NamingStrategy;
import com.tcmj.iso.builder.ClassBuilderFactory;
import com.tcmj.iso.builder.NamingStrategyFactory;
import com.tcmj.iso.builder.SourceFormatterFactory;
import com.tcmj.iso.datasources.impl.URLHtmlDataProvider;
import com.tcmj.iso.exporter.EnumExporterFactory;
import com.tcmj.iso.exporter.impl.ReportingEnumExporter;
import com.tcmj.iso.generator.Fluent;

/** Usage Example : Fluently load a Wikipedia table and transform it to a Java enum class. */
public class Iso3166 {

  public static void main(String[] args) {
    try {
      Fluent.builder()
          .fromDataSource(getMyDataProvider())
          .usingClassBuilder(ClassBuilderFactory.getBestEnumBuilder())
          .convertConstantNames(getConstantsNamingStrategy())
          .convertFieldNames(getFieldsNamingStrategy())
          .format(SourceFormatterFactory.getBestSourceCodeFormatter())
          .exportWith(getMyEnumExporter())
          .end();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static EnumExporter getMyEnumExporter() {
    EnumExporter exporterA = EnumExporterFactory.getInMemoryCompilingExporter();
    EnumExporter exporterB = EnumExporterFactory.getReportingEnumExporter();
    return exporterA.and(
        exporterB, exporterB.createOptions(ReportingEnumExporter.LogLevel.SYSTEM_OUT.name()));
  }

  private static NamingStrategy getConstantsNamingStrategy() {
    return NamingStrategyFactory.upperCase()
        .and(NamingStrategyFactory.space2underline())
        .and(NamingStrategyFactory.replaceAtoZ())
        .and(NamingStrategyFactory.removeProhibitedSpecials())
        .and(NamingStrategyFactory.minus2underline());
  }
  private static NamingStrategy getFieldsNamingStrategy() {
    return 
        NamingStrategyFactory.removeProhibitedSpecials()
        .and(NamingStrategyFactory.minus2underline());
  }
  private static DataProvider getMyDataProvider() {
    return new URLHtmlDataProvider(
        "com.tcmj.test.MyWikipediaEnum", //enum name and path
        "http://www.nationsonline.org/oneworld/country_code_list.htm", //url to load
        "table#codelist", //css to a record to further (also to a table possible)
        2, //enum constant column
        new int[] { 3, 4, 5} //sub columns
        );
  }
}
