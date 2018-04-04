package com.tcmj.pug.enums.example.fluent.html;

import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.builder.impl.StringBufferEnumBuilder;
import com.tcmj.pug.enums.datasources.impl.URLHtmlDataProvider;
import com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter.LogLevel.SYSTEM_OUT;

/**
 * Minimalistic fluent example.
 * Reading from wikipedia, creating a simple enum.
 * <pre>
 *
 * package org.wikipedia.states;
 *
 * public enum Germany {
 * Baden-Württemberg,
 * Bavaria,
 * Berlin,
 * Brandenburg,
 * Bremen,
 * Hamburg,
 * Hessen,
 * Niedersachsen,
 * Mecklenburg-Vorpommern,
 * North Rhine- Westphalia,
 * Rhineland-Palatinate,
 * Saarland,
 * Sachsen,
 * Sachsen-Anhalt,
 * Schleswig-Holstein,
 * Thuringia;
 * }
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
          3,
          null))
        .classBuilder(new StringBufferEnumBuilder().withName("org.wikipedia.states.Germany"))
        .enumExporter(new ReportingEnumExporter(SYSTEM_OUT))
        .build();

    } catch (Exception ex) {
      LOG.error("Whoop", ex);
    }

  }
}
