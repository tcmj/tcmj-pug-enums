package com.tcmj.pug.enums.example.fluent;

import com.tcmj.pug.enums.api.ClassBuilder;
import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.EnumResult;
import com.tcmj.pug.enums.api.NamingStrategy;
import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.builder.SourceFormatterFactory;
import com.tcmj.pug.enums.builder.impl.JavaPoetEnumBuilder;
import com.tcmj.pug.enums.datasources.impl.URLHtmlDataProvider;
import com.tcmj.pug.enums.exporter.EnumExporterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.trim;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.upperCase;

/** Usage Example : Fluently load a Wikipedia table and transform it to a Java enum class. */
public class FluentSimpleURLHtmlDataExample {
  private static final transient Logger LOG = LoggerFactory.getLogger(FluentSimpleURLHtmlDataExample.class);

  public static void main(String[] args) {
    try {
      EnumResult myEnum = Fluent.builder()
        .dataProvider(getMyURLHtmlDataProvider())
        .classBuilder(getMyEnumBuilder())
        .convertConstantNames(howToConvertEnumNames())
        .convertFieldNames(howToConvertSubfields())
        .javaDocClassLevel("Unix Commands", "data has been crawled from wikipedia")
        .sourceFormatter(SourceFormatterFactory.getBestSourceCodeFormatter())
        .enumExporter(EnumExporterFactory.getReportingEnumExporter())
        .build();

      myEnum.getResult();

    } catch (Exception e) {
      LOG.error("Exception!", e);
    }
  }

  private static DataProvider getMyURLHtmlDataProvider() {
    return new URLHtmlDataProvider(
      "https://en.wikipedia.org/wiki/List_of_Unix_commands",
      "[title=Pwd]", //we select any content of the table to find the <table>
      1, new int[]{2, 4});
  }

  private static ClassBuilder getMyEnumBuilder() {
    return new JavaPoetEnumBuilder().withName("org.wikipedia.Cookies");
  }

  private static NamingStrategy howToConvertEnumNames() {
    return upperCase(); //we only need to convert to uppercase in this case
  }

  private static NamingStrategy howToConvertSubfields() {
    return trim(); //also nothing special needed here
  }
}
