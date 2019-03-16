package com.tcmj.pug.enums.exporter.impl;

import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.model.EnumData;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

/**
 * Creates java source enum test data in form of String objects. Used to test the {@link
 * EnumExporter} implementations.
 */
public class TestDataProvider {
  public static final String LINESEP = System.lineSeparator();
  private final Random random = new Random();
  String[] data = new String[]{
    "package one.two.three; public enum SimpleEnum { ONE,TWO,THREE } ",
    "public enum NoPckEnum { ONE,TWO,THREE } ",
    " package   com.tcmj.iso ;  import java.util.Date;   public   enum   UnFormat  { A, B ,C  }",
    "package org; public enum Animal { DOG(\"Goldie\"); private final String name; Animal(String name) { this.name = name; } public String getName() { return name; } } "
  };

  public EnumData getEnumData() {
    return new EnumData();
  }

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

  /**
   * Create a enum text object with a given package.
   */
  public String getEnumNamed(String packageName, String className) {
    return "package "
      + packageName
      + "; import java.util.Date; public enum "
      + className
      + "{ RED,BLUE,GREEN }";
  }

  /**
   * Create a enum text object without the leading java package line.
   */
  public String getEnumNamed(String className) {
    return
      "import java.util.Date; public enum "
        + className
        + "{ RED,BLUE,GREEN }";
  }

  public String getEnumSimple(String packageName, String classNameSimple) {
    return join(System.lineSeparator(),
      packageName + ";",
      "",  //sometimes an additional newline
      "import java.util.Date; ",
      "import java.util.Objects; ",
      "",
      "public enum " + classNameSimple + " { ",
      "   MARS, SNICKERS, TOBLERONE,",
      "}");
  }

  /**
   * Generates a complete enum object with a little bit of randomness.
   * complete means with package, javadoc.
   * randomness means sometimes more or less empty lines and sometimes with subfields
   */
  public String getEnumComplete(String packageName, String classNameSimple) {
    String enumSubs = random.nextBoolean() ? "(\"value\")" : "";
    return join(System.lineSeparator(),
      "package " + packageName + ";",
      random.nextBoolean() ? null : "",  //sometimes an additional newline
      "import java.util.Date; ",
      "import java.util.Objects; ",
      random.nextBoolean() ? null : "",
      "/** ",
      " * This is a generated Enum object.",
      " * @author me",
      " */ ",
      "public enum " + classNameSimple + " { ",
      "   RED" + enumSubs + ",",
      "   BLUE" + enumSubs + ",",
      "   GREEN" + enumSubs + ";",
      "".equals(enumSubs) ? "" : "   private final String myField;",
      random.nextBoolean() ? null : "",
      "".equals(enumSubs) ? "" : "   " + classNameSimple + "(String name) {",
      "".equals(enumSubs) ? "" : "      this.myField = name;",
      "".equals(enumSubs) ? "" : "   }",
      random.nextBoolean() ? null : "",
      "".equals(enumSubs) ? "" : "   public String getMyField() {",
      "".equals(enumSubs) ? "" : "      return this.myField;",
      "".equals(enumSubs) ? "" : "   }",
      "}");
  }

  private String join(final String separator, final String... objects) {
    Objects.requireNonNull(separator, "Separator cannot be null");
    final StringBuilder result = new StringBuilder();
    final Iterator<String> iterator = Arrays.asList(objects).iterator();
    while (iterator.hasNext()) {
      final String value = iterator.next();
      if (value == null) {
        continue;
      } else {
        result.append(value);
      }
      if (iterator.hasNext()) {
        result.append(separator);
      }
    }
    return result.toString();
  }

  public String getStaticEnumComplete() {
    return "/* some licence */" + LINESEP +
      "package a.b.c.d;" + LINESEP
      + "/** " + LINESEP + " * some class doc. " + LINESEP + " */" + LINESEP + "public enum MyEnum { " + LINESEP
      + "    ONE,TWO,THREE;" + LINESEP +
      "   private final String myField;" + LINESEP +
      "   /** " + LINESEP + "    * some constructor doc. " + LINESEP + "    */" + LINESEP +
      "   MyEnum(String name) {" + LINESEP +
      "      //set the field name:" + LINESEP +
      "      this.myField = name;" + LINESEP +
      "   }" + LINESEP +
      "   /** " + LINESEP + "    * some getter doc. " + LINESEP + "     wo star  " + LINESEP + "    */" + LINESEP +
      "   public String getMyField() {" + LINESEP +
      "      /* here we return! */" + LINESEP +
      "      return this.myField;" + LINESEP +
      "   }" + LINESEP +
      "}";
  }
}
