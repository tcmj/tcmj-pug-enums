package com.tcmj.pug.enums.example.fluent;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.Fluent;
import com.tcmj.pug.enums.api.NamingStrategy;
import com.tcmj.iso.builder.ClassBuilderFactory;
import com.tcmj.iso.builder.NamingStrategyFactory;
import com.tcmj.iso.builder.SourceFormatterFactory;
import com.tcmj.iso.datasources.impl.URLHtmlDataProvider;
import com.tcmj.iso.exporter.EnumExporterFactory;
import com.tcmj.iso.exporter.impl.ReportingEnumExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Usage Example : Fluently load a Wikipedia table and transform it to a Java enum class. */
public class WikipediaExample {
  private static final transient Logger LOG = LoggerFactory.getLogger(WikipediaExample.class);

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
      LOG.error("Exception!", e);    }
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
        "com.tcmj.test.MyWikipediaEnum", //enum name and path
        "https://en.wikipedia.org/wiki/ISO_3166-1", //url to load
        "[title=Afghanistan]", //xpath to a record to further (also to a table possible)
        1, //enum constant column
        new int[] {2, 3, 4} //sub columns
        );
  }
}