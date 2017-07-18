package com.tcmj.pug.enums.example.fluent;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.NamingStrategy;
import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.builder.ClassBuilderFactory;
import com.tcmj.pug.enums.api.tools.NamingStrategyFactory;
import com.tcmj.pug.enums.builder.SourceFormatterFactory;
import com.tcmj.pug.enums.datasources.impl.URLHtmlDataProvider;
import com.tcmj.pug.enums.exporter.EnumExporterFactory;
import com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Usage Example : Fluently load a Wikipedia table and transform it to a Java enum class. */
public class WikipediaExample3 {
  private static final transient Logger LOG = LoggerFactory.getLogger(WikipediaExample3.class);

  public static void main(String[] args) {
    try {
      Fluent.builder()
          .className("com.tcmj.html.MyStatesEnum4")
          .dataProvider(getMyDataProvider())
          .classBuilder(ClassBuilderFactory.getBestEnumBuilder())
          .convertConstantNames(getMyNamingStrategy())
          .sourceFormatter(SourceFormatterFactory.getBestSourceCodeFormatter())
          .enumExporter(EnumExporterFactory.getReportingEnumExporter(ReportingEnumExporter.LogLevel.SYSTEM_OUT))
          .build();

    } catch (Exception e) {
      LOG.error("Exception!", e);
    }
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
        "com.tcmj.html.MyStatesEnum4", 
        "https://en.wikipedia.org/wiki/States_of_Germany", 
        "table.sortable", 
        3, //enum constant column
        new int[]{ 4,5,3} //sub columns
    );
  }
}
