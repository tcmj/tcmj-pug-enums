package com.tcmj.pug.enums.example.fluent;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.NamingStrategy;
import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.builder.ClassBuilderFactory;
import com.tcmj.pug.enums.builder.SourceFormatterFactory;
import com.tcmj.pug.enums.datasources.impl.URLHtmlDataProvider;
import com.tcmj.pug.enums.exporter.EnumExporterFactory;
import com.tcmj.pug.enums.exporter.impl.JavaSourceFileExporter;
import com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Usage Example : Fluently load a Wikipedia table and transform it to a Java enum class.
 * Due to the fact that this example loads live data directly from wikipedia we cannot ensure that
 * the css-selector is always working. We are using a really bad css selector which is based on the
 * counts of the tables in the html page. Future changes on the wikipedia site will likely break this code.
 * Date: 2018-10-22
 */
public class WikipediaExample {
  private static final transient Logger LOG = LoggerFactory.getLogger(WikipediaExample.class);

  public static void main(String[] args) {
    try {
      Fluent.builder()
        .dataProvider(getMyDataProvider())
        .classBuilder(ClassBuilderFactory.getBestEnumBuilder().withName("com.tcmj.iso8583.de.Field"))
        .convertConstantNames(getNamingStrategyForConstantNames())
        .sourceFormatter(SourceFormatterFactory.getBestSourceCodeFormatter())
        .enumExporter(getMyEnumExporter())
        .build();

    } catch (Exception e) {
      LOG.error("Exception!", e);
    }
  }

  /** We can chain several exporters like this.. */
  private static EnumExporter getMyEnumExporter() {
    EnumExporter exporterA = EnumExporterFactory.getInMemoryCompilingExporter();
    EnumExporter exporterB = EnumExporterFactory.getReportingEnumExporter(ReportingEnumExporter.LogLevel.SYSTEM_OUT);
    EnumExporter exporterC = new JavaSourceFileExporter("./src/main/generated");
    return exporterA.and(exporterB).and(exporterC);
  }

  /** We format numbers to three digits. */
  private static NamingStrategy getNamingStrategyForConstantNames() {
    DecimalFormat df = new DecimalFormat("000");
    return ((String value) -> "DE" + df.format(Integer.parseInt(value)));
  }

  public static DataProvider getMyDataProvider() {
    final String cssSelector = "#mw-content-text > div > table:nth-child(54)";
    URLHtmlDataProvider provider = new URLHtmlDataProvider(
      "https://en.wikipedia.org/wiki/ISO_8583", //url to load
      cssSelector, //xpath to a table (we alternatively could use a xpath to a record)
      1, //enum constant column
      new int[]{3} //sub columns to be extracted
    );
    List<String> x = new ArrayList<>();
    IntStream.range(107, 222).forEach(value -> x.add("#" + String.valueOf(value)));
    x.addAll(Stream.of("#1", "#59", "#60").collect(Collectors.toList()));
    provider.setValuesToSkip(x);
    return provider;
  }

}
