package com.tcmj.pug.enums.example.fluent;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.SourceFormatter;
import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.builder.impl.StringBufferEnumBuilder;
import com.tcmj.pug.enums.exporter.EnumExporterFactory;
import com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.model.NameTypeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Very simple fluent example using a static data provider. */
public class FluentStaticDataExampleSimple {
  private static final transient Logger LOG = LoggerFactory.getLogger(FluentStaticDataExampleSimple.class);

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
      //     Fluent.builder()
      //          .className("org.my.data.Colors")
//          .dataProvider(new ContinentDataProvider())
//          .classBuilder(ClassBuilderFactory.getBestEnumBuilder())
//          .sourceFormatter(sourceFormatter)
//          .enumExporter(exporter)
//          .build();


//      Fluent.builder()
//          .dataProvider(new DataProvider() {
//            @Override
//            public EnumData load() {
//              EnumData model = new EnumData();
//              model.setClassName("org.my.data.Colors");
//              model.getData().add(NameTypeValue.of("RED"));
//              model.getData().add(NameTypeValue.of("GREEN"));
//              model.getData().add(NameTypeValue.of("BLUE"));
//              return model;
//            }
//          })
////                      .classBuilder(ClassBuilderFactory.getCodeModelEnumBuilder())
//          .classBuilder(new StringBufferEnumBuilder())
//          //   .classBuilder(new JavaPoetEnumBuilder())
//           .enumExporter(new ReportingEnumExporter())
////                      .sourceFormatter(new GoogleFormatter())
//
//          .build();


      Fluent.builder()
        .dataProvider(new DataProvider() {
          @Override
          public EnumData load() {
            EnumData model = new EnumData();
            model.getData().add(NameTypeValue.of("RED"));
            model.getData().add(NameTypeValue.of("GREEN"));
            model.getData().add(NameTypeValue.of("BLUE"));
            return model;
          }
        })
        .classBuilder(new StringBufferEnumBuilder().withName("org.my.data.Colors"))
        .enumExporter(new ReportingEnumExporter())
        .build();


    } catch (Exception e) {
      LOG.error("Exception!", e);
    }
  }
}
