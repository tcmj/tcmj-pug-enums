package com.tcmj.iso.api.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Function;
import java.util.Objects;
import java.util.stream.Stream;

/** Holds all data of a single enum constant values. The enum must consist of a constant name and may have sub fields. */
public class NameTypeValue implements Comparable<NameTypeValue>, Serializable {

  private static final long serialVersionUID = 1L;

  public final String constantName;
  public final Object[] values;

  /** Constant name. */
  public String getConstantName() {
    return constantName;
  }
 
  /** Value of sub element. */
  public Object[] getValue() {
    return values;
  }

  private NameTypeValue(final String constantName, final Object[] value) {
    this.constantName = constantName;
    this.values = value;
  }

  /** Create a immutable instance of NameTypeValue used to hold field values of enums. */
  public static NameTypeValue of(final String constantName, final Object[] value) {
    Objects.requireNonNull(constantName, "Constant name may not be null!");
    Objects.requireNonNull(value, "Object[] value may not be null!");
      return new NameTypeValue(constantName, value);
  }

  /** Create a immutable instance of NameTypeValue used to hold a enum constant values without having subfields. */
  public static NameTypeValue of(final String constantName) {
    Objects.requireNonNull(constantName, "Constant name may not be null!");
    return new NameTypeValue(constantName, null);
  }

  public int getSubFieldsAmount() {
    return values == null ? 0 : values.length;
  }

  @Override
  public int compareTo(NameTypeValue other) {
    return other==null?-1:this.constantName.compareTo(other.constantName);
  }
 

   private static final Function<Object, String> QUOTE_OBJECT = (v) -> "\"".concat(String.valueOf(v)).concat("\"");

  @Override
  public String toString() {
    final String[] value = Stream.of(this.values).map(QUOTE_OBJECT).toArray(String[]::new);
    return String.format( "{\"name\":%s,\"values\":%s}", this.constantName, Arrays.toString(value));
  }
}
