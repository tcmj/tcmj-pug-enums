package com.tcmj.iso.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * API to implement a enum data exporter.
 * Examples: FileExporter, ClassLoadingExporter, SystemOutExporter
 */
public interface EnumExporter {

    String export(String data, Map<String,Object> options);

    default EnumExporter and(EnumExporter other, Map<String,Object> optz) {
        Objects.requireNonNull(other);
        return ( source,options) -> other.export(export(source,options),optz);
    }
    default Map<String, Object> createOptions(String... values) {
        return new HashMap<>();
    }
}
