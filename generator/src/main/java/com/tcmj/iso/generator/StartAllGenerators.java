package com.tcmj.iso.generator;

import com.tcmj.iso.api.DataProvider;
import com.tcmj.iso.api.EnumExporter;
import com.tcmj.iso.api.SourceFormatter;
import com.tcmj.iso.builder.ClassBuilderFactory;
import com.tcmj.iso.exporter.EnumExporterFactory;
import com.tcmj.iso.exporter.impl.InMemoryCompilingExporter;
import com.tcmj.iso.exporter.impl.ReportingEnumExporter;
import com.tcmj.iso.generator.provider.ContinentDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Start the generation of all enum classes. */
public class StartAllGenerators {

  /** slf4j Logging framework. */
  private static final Logger LOG = LoggerFactory.getLogger(StartAllGenerators.class);

  static DataProvider[] providers =
      new DataProvider[] {
        new ContinentDataProvider(),
        //            new CountryGenerator()
      };

  /** Starting point. */
  public static void main(String[] args) {

    EnumExporter exporterB = EnumExporterFactory.getReportingEnumExporter();
    EnumExporter exporterA = EnumExporterFactory.getInMemoryCompilingExporter();
    EnumExporter exporter =
        exporterA.and(
            exporterB, exporterB.createOptions(ReportingEnumExporter.LogLevel.SYSTEM_OUT.name()));

    SourceFormatter sourceFormatter1 = ClassBuilderFactory.getGoogleSourceCodeFormatter();
    SourceFormatter sourceFormatter2 = ClassBuilderFactory.getNoLineBreaksSourceCodeFormatter();
    SourceFormatter sourceFormatter = sourceFormatter1.and(sourceFormatter2);

    for (DataProvider dataProvider : providers) {
      try {

        Fluent.builder()
            .fromDataSource(dataProvider)
            .usingClassBuilder(ClassBuilderFactory.getJavaPoetEnumBuilder())
            .format(sourceFormatter)
            .exportWith(exporter)
            .end();

        System.out.println(((InMemoryCompilingExporter) exporterA).getEnumConstants());

      } catch (Exception ex) {
        LOG.trace("Stacktrace", ex);
        LOG.error("Cannot create {}:{}", dataProvider, ex.toString());
      }
    }
  }
}
