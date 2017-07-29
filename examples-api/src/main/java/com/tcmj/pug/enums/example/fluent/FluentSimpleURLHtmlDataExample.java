package com.tcmj.pug.enums.example.fluent;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.EnumResult;
import com.tcmj.pug.enums.api.NamingStrategy;
import com.tcmj.pug.enums.api.tools.NamingStrategyFactory;
import com.tcmj.pug.enums.builder.SourceFormatterFactory;
import com.tcmj.pug.enums.builder.impl.JavaPoetEnumBuilder;
import com.tcmj.pug.enums.datasources.impl.URLHtmlDataProvider;
import com.tcmj.pug.enums.exporter.EnumExporterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Usage Example : Fluently load a Wikipedia table and transform it to a Java enum class. */
public class FluentSimpleURLHtmlDataExample {
  private static final transient Logger LOG = LoggerFactory.getLogger(FluentSimpleURLHtmlDataExample.class);

  public static void main(String[] args) {
    try {

//      Fluent.builder()
//          .fromDataSource(new URLHtmlDataProvider("com.tcmj.StatesOfGermany", "https://en.wikipedia.org/wiki/States_of_Germany", "[title=Hanover]", 3, null))
//          .usingClassBuilder(new JavaPoetEnumBuilder())
//          .convertConstantNames(getMyNamingStrategy())
//          .format(SourceFormatterFactory.getBestSourceCodeFormatter())
//          .exportWith(EnumExporterFactory.getReportingEnumExporter())
//          .end();

      EnumResult enumResult =
      com.tcmj.pug.enums.api.fluent.Fluent.builder()
          .dataProvider(new URLHtmlDataProvider("https://en.wikipedia.org/wiki/States_of_Germany", "[title=Hanover]", 3, null))
          .classBuilder(new JavaPoetEnumBuilder())
          .usingDefaultConstantNameConversion()
//          .convertConstantNames(getMyNamingStrategy())
          .javaDocClassLevel("This is a java enum.", "Source is from xyz")
          .sourceFormatter(SourceFormatterFactory.getBestSourceCodeFormatter())
          .enumExporter(EnumExporterFactory.getReportingEnumExporter())
          .className("com.tcmj.StatesOfGermany")
          .build();
      
      enumResult.getResult();

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
        "https://en.wikipedia.org/wiki/ISO_3166-1", //url to load
        "[title=Afghanistan]", //xpath to a record to further (also to a table possible)
        1, //enum constant column
        new int[]{2, 3, 4} //sub columns
    );
  }
}
