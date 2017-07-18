package com.tcmj.pug.enums.example.fluent;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.NamingStrategy;
import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.builder.ClassBuilderFactory;
import com.tcmj.pug.enums.api.tools.NamingStrategyFactory;
import com.tcmj.pug.enums.builder.SourceFormatterFactory;
import com.tcmj.pug.enums.datasources.impl.URLHtmlDataProvider;
import com.tcmj.pug.enums.exporter.EnumExporterFactory;
import com.tcmj.pug.enums.exporter.impl.JavaSourceFileExporter;
import com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Usage Example : Fluently load a Wikipedia table and transform it to a Java enum class. */
public class WikipediaExample {
  private static final transient Logger LOG = LoggerFactory.getLogger(WikipediaExample.class);

  public static void main(String[] args) {
    try {
      Fluent.builder()
          .className("com.tcmj.iso3166.Countries")
          .dataProvider(getMyDataProvider())
          .classBuilder(ClassBuilderFactory.getBestEnumBuilder())
          .useFixedFieldNames(new String[]{"alpha2", "alpha3", "numeric"})
          .convertConstantNames(getNamingStrategyForConstantNames())
          .sourceFormatter(SourceFormatterFactory.getBestSourceCodeFormatter())
          .enumExporter(getMyEnumExporter())
          .build();

    } catch (Exception e) {
      LOG.error("Exception!", e);
    }
  }

  private static EnumExporter getMyEnumExporter() {
    EnumExporter exporterA = EnumExporterFactory.getInMemoryCompilingExporter();
    EnumExporter exporterB = EnumExporterFactory.getReportingEnumExporter(ReportingEnumExporter.LogLevel.SYSTEM_OUT);
    EnumExporter exporterC = new JavaSourceFileExporter("./src/main/generated");

    return exporterA.and(exporterB).and(exporterC);
  }

  private static NamingStrategy getNamingStrategyForConstantNames() {
    NamingStrategy ns1 = NamingStrategyFactory.extractParenthesis();
    NamingStrategy ns2 = NamingStrategyFactory.removeProhibitedSpecials();
    NamingStrategy ns3 = NamingStrategyFactory.camelStrict();
    NamingStrategy ns4 = NamingStrategyFactory.harmonize();
    NamingStrategy ns5 = NamingStrategyFactory.upperCase();
    return ns1.and(ns2).and(ns3).and(ns4).and(ns5);
  }

  private static DataProvider getMyDataProvider() {
    return new URLHtmlDataProvider(
        "com.tcmj.iso3166.Countries", //enum name and path
        "https://en.wikipedia.org/wiki/ISO_3166-1", //url to load
        "[title=Afghanistan]", //xpath to a record to further (also to a table possible)
        1, //enum constant column
        new int[]{2, 3, 4} //sub columns
    );
  }
}
