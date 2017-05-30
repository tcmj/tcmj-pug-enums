package com.tcmj.iso.api.model;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds all data of a sub field of a enum constant.
 */
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

    public NameTypeValue(final String[] name, final Class[] type, final Object[] value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public static NameTypeValue of(final String[] name, final Class[] type, final Object[] value) {
        return new NameTypeValue(name, type, value);
    }

    @Override public int compareTo(NameTypeValue other) {
        String left = "".concat(Stream.of(getName()).sorted().collect(Collectors.joining()));
        String right = "".concat(Stream.of(other.getName()).sorted().collect(Collectors.joining()));
        return left.compareTo(right);
    }
}
