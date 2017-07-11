package com.tcmj.pug.enums.api;

import java.util.Objects;

/**
 * API to implement a enum data exporter. Examples: FileExporter, ClassLoadingExporter,
 * SystemOutExporter
 */
public interface EnumExporter {

  EnumResult export(EnumResult data);

  default EnumExporter and(EnumExporter other) {
    Objects.requireNonNull(other);
    return (source) -> other.export(export(source));
  }
}
