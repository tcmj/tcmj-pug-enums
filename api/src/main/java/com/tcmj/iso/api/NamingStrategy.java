package com.tcmj.iso.api;

import java.util.Objects;

/**
 * Defines the way to convert values to valid java enum constant names.
 */
@FunctionalInterface
public interface NamingStrategy {
    String convert(String value);

    default NamingStrategy and(NamingStrategy other) {
        Objects.requireNonNull(other);
        return (String source) -> other.convert(convert(source));
    }

}
