package com.tcmj.pug.enums.api;

import java.util.Objects;

/** Defines the way to convert values to valid java enum constant names. */
@FunctionalInterface
public interface NamingStrategy {
  String convert(String value);

  /** No conversion - does nothing. */
  static NamingStrategy getDefault() {
    return value -> value;
  };

  default NamingStrategy and(NamingStrategy other) {
    Objects.requireNonNull(other);
    return (String source) -> other.convert(convert(source));
  }
}
