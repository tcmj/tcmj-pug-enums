package com.tcmj.iso.api.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Function;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Holds all data of sub fields of a enum constant. */
public class NameTypeValue implements Comparable<NameTypeValue>, Serializable {

  /** Serialization version */
  private static final long serialVersionUID = 1L;

  public final String[] name;

  public final Class[] type;

  public final Object[] value;

  /** Name of sub element (field of enum constant). */
  public String[] getName() {
    return name;
  }

  /** Class type of sub element. */
  public Class[] getType() {
    return type;
  }

  /** Value of sub element. */
  public Object[] getValue() {
    return value;
  }

  private NameTypeValue(final String[] name, final Class[] type, final Object[] value) {
    this.name = Objects.requireNonNull(name, "String[] name");
    this.type = Objects.requireNonNull(type, "Class[] type");
    this.value = Objects.requireNonNull(value, "Object[] value");
    if ((name.length != type.length) || (name.length != value.length)) {
      throw new IllegalArgumentException(
          "Array size is not the same: " + name.length + "/" + type.length + "/" + value.length);
    }
  }

  /** Create a immutable instance of NameTypeValue used to hold field values of enums. */
  public static NameTypeValue of(final String[] name, final Class[] type, final Object[] value) {
    return new NameTypeValue(name, type, value);
  }

  @Override
  public int compareTo(NameTypeValue other) {
    String left = concat(getName());
    String right = concat(other.getName());
    return left.compareTo(right);
  }

  private static final String concat(final String[] value) {
    return "".concat(Stream.of(value).sorted().collect(Collectors.joining()));
  }

  private static final Function<String, String> QUOTE_STRING = (s) -> "\"".concat(s).concat("\"");
  private static final Function<Class, String> QUOTE_CLASS =
      (c) -> "\"".concat(c.getName()).concat("\"");
  private static final Function<Object, String> QUOTE_OBJECT =
      (v) -> "\"".concat(String.valueOf(v)).concat("\"");

  @Override
  public String toString() {
    final String[] names = (String[]) Stream.of(name).map(QUOTE_STRING).toArray(String[]::new);
    final String[] types = Stream.of(type).map(QUOTE_CLASS).toArray(String[]::new);
    final String[] values = Stream.of(value).map(QUOTE_OBJECT).toArray(String[]::new);
    return String.format(
        "{\"name\":%s,\"type\":%s,\"value\":%s}",
        Arrays.toString(names), Arrays.toString(types), Arrays.toString(values));
  }
}
