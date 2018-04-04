package com.tcmj.pug.enums.example.fluent.html;

import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.builder.SourceFormatterFactory;
import com.tcmj.pug.enums.builder.impl.StringBufferEnumBuilder;
import com.tcmj.pug.enums.datasources.impl.URLHtmlDataProvider;
import com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter.LogLevel.SYSTEM_OUT;

/**
 * [Fluent][Html][Online] example.
 * Reading from wikipedia, creating a simple enum.
 * <pre>
 * package org.wikipedia.states; public enum Germany { BW, BY, BE, BB, HB, HH, HE, NI, MV, NW, RP, SL, SN, ST, SH, TH; }
 * <pre/>
 */
public class Example01 {
  private static final transient Logger LOG = LoggerFactory.getLogger(Example01.class);


  public static void main(String[] args) {
    try {

      Fluent.builder()
        .dataProvider(new URLHtmlDataProvider(
          "https://en.wikipedia.org/wiki/States_of_Germany",
          "[title=Hanover]",
          13, //ISO 3166-2 Code
          null))
        .classBuilder(new StringBufferEnumBuilder().withName("org.wikipedia.states.Germany"))
        .sourceFormatter(SourceFormatterFactory.getNoLineBreaksSourceCodeFormatter())
        .enumExporter(new ReportingEnumExporter(SYSTEM_OUT))
        .build();

    } catch (Exception ex) {
      LOG.error("Whoop", ex);
    }

  }
}
