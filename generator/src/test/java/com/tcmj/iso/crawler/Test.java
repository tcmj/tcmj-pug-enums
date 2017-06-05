package com.tcmj.iso.crawler;

import com.tcmj.iso.api.EnumExporter;
import com.tcmj.iso.api.SourceFormatter;
import com.tcmj.iso.exporter.EnumExporterFactory;
import com.tcmj.iso.exporter.impl.ReportingEnumExporter;
import com.tcmj.iso.generator.Fluent;
import com.tcmj.iso.generator.provider.ContinentDataProvider;

/** pugproductions - 2017-05-15 - tcmj. */
public class Test {

  public static void main(String[] args) {
    try {

      //            Fluent.builder()
      //                    .fromDataSource(new ContinentDataProvider())
      //                    .usingDefaultClassBuilder()
      //                    .end();

      /* Usage-Example of chaining several source code formatters together */
      SourceFormatter sourceFormatter1 = rawSource -> rawSource.replace("Africa", "Jamaika");
      SourceFormatter sourceFormatter2 = rawSource -> rawSource.replace("Antarctica", "Jamaika");
      SourceFormatter sourceFormatter = sourceFormatter1.and(sourceFormatter2);

      EnumExporter exporterA = EnumExporterFactory.getInMemoryCompilingExporter();
      EnumExporter exporterB = EnumExporterFactory.getReportingEnumExporter();
      EnumExporter exporter =
          exporterA.and(
              exporterB, exporterB.createOptions(ReportingEnumExporter.LogLevel.SYSTEM_OUT.name()));

      Fluent.builder()
          .fromDataSource(new ContinentDataProvider())
          .usingDefaultClassBuilder()
          .format(sourceFormatter)
          .exportWith(exporter)
          .end();
      //            System.out.println(classBuilder);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
