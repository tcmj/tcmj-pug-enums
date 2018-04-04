package com.tcmj.pug.enums.example.fluent;

import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.SourceFormatter;
import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.builder.ClassBuilderFactory;
import com.tcmj.pug.enums.example.provider.ContinentDataProvider;
import com.tcmj.pug.enums.exporter.EnumExporterFactory;
import com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter;
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
      EnumExporter exporterB = EnumExporterFactory.getReportingEnumExporter(ReportingEnumExporter.LogLevel.SYSTEM_OUT);
      EnumExporter exporter = exporterA.and(exporterB);

      /* Main call */
      Fluent.builder()
          .dataProvider(new ContinentDataProvider())
          .classBuilder(ClassBuilderFactory.getBestEnumBuilder())
          .sourceFormatter(sourceFormatter)
          .enumExporter(exporter)
          .build();

    } catch (Exception e) {
      LOG.error("Exception!", e);    }
  }
}
