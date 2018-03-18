package com.tcmj.pug.enums.example.fluent.statics;

import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.builder.impl.StringBufferEnumBuilder;
import com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.model.NameTypeValue;

import static com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter.LogLevel.SYSTEM_OUT;

/**
 * Minimalistic fluent example using a anonymous static data provider.
 * <pre>
 *
 * package org.my.data;
 *
 * public enum Colors {
 *   RED,
 *   GREEN,
 *   BLUE;
 * }
 * <pre/>
 */
public class Example02 {

  public static void main(String[] args) throws Exception {

    Fluent.builder()
      .dataProvider(() -> {
        EnumData model = new EnumData();
        model.getData().add(NameTypeValue.of("RED", new Object[]{255, 0, 0}));
        model.getData().add(NameTypeValue.of("GREEN", new Object[]{0, 255, 0}));
        model.getData().add(NameTypeValue.of("BLUE", new Object[]{0, 0, 255}));
        return model;
      })
      .classBuilder(new StringBufferEnumBuilder().withName("org.my.data.Colors"))
      .enumExporter(new ReportingEnumExporter(SYSTEM_OUT))
      .build();

  }
}
