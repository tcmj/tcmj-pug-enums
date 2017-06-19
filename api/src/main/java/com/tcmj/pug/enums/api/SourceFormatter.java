package com.tcmj.pug.enums.api;

import java.util.Objects;

/**
 * Source Code Formatter Interface.
 *
 * <p>There are several implementations to adjust the output format of your enum objects.
 *
 * <p>You can easily chain various Formatters together using the {@link #and(SourceFormatter)}
 * method.
 */
@FunctionalInterface
public interface SourceFormatter {
  String format(String rawSource);

  default SourceFormatter and(SourceFormatter other) {
    Objects.requireNonNull(other);
    return (String source) -> other.format(format(source));
  }

  default SourceFormatter or(SourceFormatter other) {
    Objects.requireNonNull(other);
    return source -> other.format(SourceFormatter.this.format(source));
  }
}
