package com.tcmj.pug.enums.example.fluent;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.NamingStrategy;
import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.builder.ClassBuilderFactory;
import com.tcmj.pug.enums.api.tools.NamingStrategyFactory;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.minus2underline;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.removeProhibitedSpecials;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.replaceAtoZ;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.space2underline;
import com.tcmj.pug.enums.builder.SourceFormatterFactory;
import com.tcmj.pug.enums.datasources.impl.URLHtmlDataProvider;
import com.tcmj.pug.enums.exporter.EnumExporterFactory;
import com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Usage Example : Fluently load a Wikipedia table and transform it to a Java enum class. */
public class FluentURLHtmlDataProviderIso3166Example {
  private static final transient Logger LOG = LoggerFactory.getLogger(FluentURLHtmlDataProviderIso3166Example.class);

  public static void main(String[] args) {
    try {
      Fluent.builder()
          .className("com.tcmj.test.MyWikipediaEnum")
          .dataProvider(getMyDataProvider())
          .classBuilder(ClassBuilderFactory.getBestEnumBuilder())
          .convertConstantNames(getConstantsNamingStrategy())
          .convertFieldNames(getFieldsNamingStrategy())
          .sourceFormatter(SourceFormatterFactory.getBestSourceCodeFormatter())
          .enumExporter(getMyEnumExporter())
          .build();

    } catch (Exception e) {
      LOG.error("Exception!", e);    }
  }

  private static EnumExporter getMyEnumExporter() {
    EnumExporter exporterA = EnumExporterFactory.getInMemoryCompilingExporter();
    EnumExporter exporterB = EnumExporterFactory.getReportingEnumExporter(ReportingEnumExporter.LogLevel.SYSTEM_OUT);
    return exporterA.and(exporterB);
  }

  private static NamingStrategy getConstantsNamingStrategy() {
    return NamingStrategyFactory.upperCase()
        .and(space2underline())
        .and(replaceAtoZ())
        .and(removeProhibitedSpecials())
        .and(minus2underline());
  }
  private static NamingStrategy getFieldsNamingStrategy() {
    return 
        NamingStrategyFactory.removeProhibitedSpecials()
        .and(minus2underline());
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
