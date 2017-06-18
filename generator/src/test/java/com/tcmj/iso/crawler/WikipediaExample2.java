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
public class WikipediaExample2 {

  public static void main(String[] args) {
    try {
      Fluent.builder()
          .fromDataSource(getMyDataProvider())
          .usingClassBuilder(ClassBuilderFactory.getBestEnumBuilder())
          .convertConstantNames(getMyNamingStrategy())
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

  private static NamingStrategy getMyNamingStrategy() {
    NamingStrategy ns1 = NamingStrategyFactory.extractParenthesis();
    NamingStrategy ns2 = NamingStrategyFactory.removeProhibitedSpecials();
    NamingStrategy ns3 = NamingStrategyFactory.camelStrict();
    NamingStrategy ns4 = NamingStrategyFactory.harmonize();
    return ns1.and(ns2).and(ns3).and(ns4);
  }

  private static DataProvider getMyDataProvider() {
    return new URLHtmlDataProvider(
        "com.tcmj.test.MTI", //enum name and path
        "https://de.wikipedia.org/wiki/ISO_8583", //url to load
        "table:nth-of-type(3)", //xpath to a record to further (also to a table possible)
        1, //enum constant column
        new int[] {2, 3} //sub columns
        );
  }
}
