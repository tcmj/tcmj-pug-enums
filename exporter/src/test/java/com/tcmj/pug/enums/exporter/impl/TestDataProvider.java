package com.tcmj.pug.enums.exporter.impl;

import com.tcmj.pug.enums.api.EnumExporter;

/**
 * Creates java source enum test data in form of String objects. Used to test the {@link
 * EnumExporter} implementations.
 */
public class TestDataProvider {

  String[] data =
      new String[] {
        "package one.two.three; public enum SimpleEnum { ONE,TWO,THREE } ",
        "public enum NoPckEnum { ONE,TWO,THREE } ",
        " package   com.tcmj.iso ;  import java.util.Date;   public   enum   UnFormat  { A, B ,C  }",
        "package org; public enum Animal { DOG(\"Goldie\"); private final String name; Animal(String name) { this.name = name; } public String getName() { return name; } } "
      };

  public String getSimpleEnum() {
    return data[0];
  }

  public String getEnumWithoutPackage() {
    return data[1];
  }

  public String getUnformatedEnum() {
    return data[2];
  }

  public String getExtendedEnum() {
    return data[3];
  }

  public String getEnumNamed(String packageName, String className) {
    return "package "
        + packageName
        + "; import java.util.Date; public enum "
        + className
        + "{ RED,BLUE,GREEN }";
  }
}
