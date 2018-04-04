package com.tcmj.pug.enums.example.fluent.statics;

import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.builder.ClassBuilderFactory;
import com.tcmj.pug.enums.builder.impl.StringBufferEnumBuilder;
import com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.model.NameTypeValue;

import static com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter.LogLevel.SYSTEM_OUT;

/**
 * Fluent example with subfields using a anonymous static data provider.
 * <pre>
 *
 * package org.my.data;
 *
 * public enum Colors {
 *   RED(255, 0, 0),
 *   GREEN(0, 255, 0),
 *   BLUE(0, 0, 255);
 *   private final int r;
 *   private final int g;
 *   private final int b;
 *   Colors(int r, int g, int b) {
 *     this.r = r;
 *     this.g = g;
 *     this.b = b;
 * }
 *
 * public int getR() {
 *   return this.r;
 * }
 * public int getG() {
 *   return this.g;
 * }
 * public int getB() {
 *   return this.b;
 * }
 *}
 * <pre/>
 */
public class Example02 {

  public static void main(String[] args) {

    Fluent.builder()
      .dataProvider(() -> {
        EnumData model = new EnumData();
        model.setFieldClasses(int.class, int.class, int.class);   //define datatypes
        model.setFieldNames("r", "g", "b");   //define names for the field variables
        model.getData().add(NameTypeValue.of("RED", new Object[]{255, 0, 0}));
        model.getData().add(NameTypeValue.of("GREEN", new Object[]{0, 255, 0}));
        model.getData().add(NameTypeValue.of("BLUE", new Object[]{0, 0, 255}));
        return model;
      })
      .classBuilder(new StringBufferEnumBuilder().withName("org.my.data.Colors"))
      .classBuilder(ClassBuilderFactory.getEnumClassBuilder().withName("org.my.data.Colors"))
      .enumExporter(new ReportingEnumExporter(SYSTEM_OUT))
      .build();

  }
}
