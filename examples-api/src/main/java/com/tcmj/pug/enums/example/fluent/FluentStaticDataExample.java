package com.tcmj.pug.enums.example.fluent;

import com.tcmj.pug.enums.api.ClassBuilder;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.Fluent;
import com.tcmj.pug.enums.api.SourceFormatter;
import com.tcmj.pug.enums.builder.ClassBuilderFactory;
import com.tcmj.iso.exporter.EnumExporterFactory;
import com.tcmj.iso.exporter.impl.ReportingEnumExporter;
import com.tcmj.pug.enums.example.provider.ContinentDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Fluent example using a static data provider. */
public class FluentStaticDataExample {
  private static final transient Logger LOG = LoggerFactory.getLogger(FluentStaticDataExample.class);

  public static void main(String[] args) {
    try {

      /* Usage-Example of chaining several source code formatters together */
      SourceFormatter sourceFormatter1 = rawSource -> rawSource.replace("Africa", "Jamaika");
      SourceFormatter sourceFormatter2 = rawSource -> rawSource.replace("Antarctica", "Jamaika");
      SourceFormatter sourceFormatter = sourceFormatter1.and(sourceFormatter2);

      /* Usage-Example of chaining several exporters together */
      EnumExporter exporterA = EnumExporterFactory.getInMemoryCompilingExporter();
      EnumExporter exporterB = EnumExporterFactory.getReportingEnumExporter();
      EnumExporter exporter = exporterA.and(exporterB, exporterB.createOptions(ReportingEnumExporter.LogLevel.SYSTEM_OUT.name()));

      /* Main call */
      Fluent.builder()
          .fromDataSource(new ContinentDataProvider())
          .usingClassBuilder(ClassBuilderFactory.getEnumClassBuilder())
          .format(sourceFormatter)
          .exportWith(exporter)
          .end();

    } catch (Exception e) {
      LOG.error("Exception!", e);    }
  }
}
