package com.tcmj.iso.mvn;

import com.tcmj.iso.api.ClassBuilder;
import com.tcmj.iso.api.DataProvider;
import com.tcmj.iso.api.EnumExporter;
import com.tcmj.iso.api.NamingStrategy;
import com.tcmj.iso.api.SourceFormatter;
import com.tcmj.iso.builder.ClassBuilderFactory;
import com.tcmj.iso.builder.NamingStrategyFactory;
import com.tcmj.iso.builder.SourceFormatterFactory;
import com.tcmj.iso.datasources.impl.URLXPathHtmlDataProvider;
import com.tcmj.iso.exporter.EnumExporterFactory;
import com.tcmj.iso.exporter.impl.ReportingEnumExporter;
import com.tcmj.iso.generator.Fluent;
import static com.tcmj.iso.mvn.LittleHelper.arrange;
import static com.tcmj.iso.mvn.LittleHelper.getLine;
import java.util.Arrays;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/** Goal which extracts data from a URL (html table). */
@Mojo(name = "generate-enum-html", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenerateEnumHtmlMojo extends GeneralEnumMojo {

  @Parameter(
    property = "tcmj.iso.generate.enum.dataprovider",
    defaultValue = "com.tcmj.iso.datasources.impl.URLXPathHtmlDataProvider",
    required = true
  )
  private String dataProvider;

  @Parameter(
    property = "tcmj.iso.generate.enum.cssselector",
    defaultValue = "table", //css selector to a record (also to a table possible),
    required = true
  )
  private String tableCssSelector;

  @Parameter(
    property = "tcmj.iso.generate.enum.constantcolumn",
    defaultValue = "1",
    required = true
  )
  private Integer constantColumn;

  @Parameter(property = "tcmj.iso.generate.enum.subdatacolumns")
  private Integer[] subDataColumns;

  /** Print actual configuration settings and version info of the plugin. */
  @Override
  protected void displayYoureWelcome() {
    super.displayYoureWelcome();
    getLog()
        .info(
            arrange(
                "Extracts EnumData from a table of a html document using a URLXPathHtmlDataProvider!"));
    getLog().info(arrange("CSS Locator used to locate the table: " + this.tableCssSelector));
    getLog().info(arrange("Constant column used in Enum: " + this.constantColumn));
    getLog().info(arrange("SubData columns to include: " + Arrays.toString(this.subDataColumns)));
    getLog().info(getLine());
  }

  private DataProvider getMyDataProvider() {
    int[] subs = null;
    if (this.subDataColumns != null) {

      Integer[] objectArray = this.subDataColumns;
      subs = new int[objectArray.length];
      for (int ctr = 0; ctr < objectArray.length; ctr++) {
        subs[ctr] = objectArray[ctr].intValue(); // returns int value
      }
    }

    return new URLXPathHtmlDataProvider(
        this.className,
        this.url,
        this.tableCssSelector, //xpath to a record to further (also to a table possible)
        this.constantColumn, //enum constant column
        subs //sub columns
        );
  }

  private static NamingStrategy getMyNamingStrategy() {
    NamingStrategy ns1 = NamingStrategyFactory.extractParenthesis();
    NamingStrategy ns2 = NamingStrategyFactory.removeProhibitedSpecials();
    NamingStrategy ns3 = NamingStrategyFactory.camelStrict();
    NamingStrategy ns4 = NamingStrategyFactory.harmonize();
    return ns1.and(ns2).and(ns3).and(ns4);
  }

  private static EnumExporter getMyEnumExporter() {
    EnumExporter exporterA = EnumExporterFactory.getInMemoryCompilingExporter();
    EnumExporter exporterB = EnumExporterFactory.getReportingEnumExporter();
    return exporterA.and(
        exporterB, exporterB.createOptions(ReportingEnumExporter.LogLevel.SYSTEM_OUT.name()));
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    super.execute();

    try {
      final DataProvider myDataProvider = getMyDataProvider();
      getLog().info(arrange("DataProvider: " + myDataProvider));

      final ClassBuilder bestEnumBuilder = ClassBuilderFactory.getBestEnumBuilder();
      getLog().info(arrange("ClassBuilder: " + bestEnumBuilder));

      final SourceFormatter bestSourceCodeFormatter =
          SourceFormatterFactory.getBestSourceCodeFormatter();
      getLog().info(arrange("SourceFormatter: " + bestSourceCodeFormatter));

      final EnumExporter enumExporter = getEnumExporter();
      final Map<String, Object> exporterOptions = getEnumExporterOptions();
      Fluent.builder()
          .fromDataSource(myDataProvider)
          .usingClassBuilder(bestEnumBuilder)
          .usingNamingStrategy(getMyNamingStrategy())
          .format(bestSourceCodeFormatter)
          .exportWith(enumExporter, exporterOptions)
          //.exportWith(EnumExporterFactory.getReportingEnumExporter())
          .end();

    } catch (Exception e) {
      getLog().error("Cannot create your enum! ", e);
      throw new MojoExecutionException("ExecutionFailure!", e);
    }
  }
}
