package com.tcmj.pug.enums.exporter;

import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.exporter.impl.InMemoryCompilingExporter;
import com.tcmj.pug.enums.exporter.impl.JavaSourceFileExporter;
import com.tcmj.pug.enums.exporter.impl.ReportingEnumExporter;

/** Provides access to all available exporters. */
public class EnumExporterFactory {

  /** Saves the given java enum source file to a physical file. */
  public static EnumExporter getJavaSourceFileExporter() {
    return new JavaSourceFileExporter();
  }

  /** Compiles the enum source in memory and loads them using a class loader. */
  public static EnumExporter getInMemoryCompilingExporter() {
    return new InMemoryCompilingExporter();
  }

  /** Prints the enum. */
  public static EnumExporter getReportingEnumExporter() {
    return new ReportingEnumExporter();
  }
}
