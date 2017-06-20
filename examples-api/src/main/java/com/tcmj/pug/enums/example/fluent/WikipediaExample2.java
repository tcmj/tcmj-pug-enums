package com.tcmj.pug.enums.example.fluent;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.Fluent;
import com.tcmj.pug.enums.api.NamingStrategy;
import com.tcmj.pug.enums.builder.ClassBuilderFactory;
import com.tcmj.pug.enums.builder.NamingStrategyFactory;
import com.tcmj.pug.enums.builder.SourceFormatterFactory;
import com.tcmj.pug.enums.datasources.impl.URLHtmlDataProvider;
import com.tcmj.pug.enums.exporter.EnumExporterFactory;
import com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Usage Example : Fluently load a Wikipedia table and transform it to a Java enum class. */
public class WikipediaExample2 {
  private static final transient Logger LOG = LoggerFactory.getLogger(WikipediaExample2.class);

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
      LOG.error("Exception!", e);
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
        new int[]{2, 3} //sub columns
    );
  }
}
