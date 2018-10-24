package com.tcmj.pug.enums.builder.impl;

import com.tcmj.pug.enums.api.ClassBuilder;
import com.tcmj.pug.enums.api.SourceFormatter;
import com.tcmj.pug.enums.builder.ClassBuilderFactory;
import com.tcmj.pug.enums.model.ClassCreationException;
import com.tcmj.pug.enums.model.EnumData;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Test;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * Parent class for all EnumBuilder implementations. This tests should be proceed with all
 * implementations.
 */
public abstract class ParentClassBuilderTest {
  ClassBuilder classBuilder;
  private SourceFormatter formatter1 = rawSource -> rawSource.replace(",", ", ");
  private SourceFormatter formatter2 = ClassBuilderFactory.getNoLineBreaksSourceCodeFormatter();
  SourceFormatter sourceFormatter = formatter1.and(formatter2);
  String result;

  @After
  public void after() {
    if (result != null) {
      System.out.println(result);
      validateGeneralConstruction();
    }
  }

  private ClassBuilder createVersionSubfieldEnum() {
    return classBuilder
        .withName("com.tcmj.any.AnyName")
        .setFields(new String[] {"version"}, new Class[] {String.class})
        .addField("AAA", new Object[]{"700"})
        .addField("BBB", new Object[]{"500"});
  }

  @Test(expected = ClassCreationException.class)
  public void cannotCreateClassWhenNoFieldsWereSet() {
    classBuilder.build();
  }

  @Test(expected = ClassCreationException.class)
  public void cannotCreateClassWhenNoFieldsWereSetButName() {
    classBuilder.withName("org.Human").build();
  }

  @Test(expected = ClassCreationException.class)
  public void cannotCreateClassWithoutPackageAndName() {
    classBuilder.addField("MALE").build();
  }

  /** public enum Human {MALE, FEMALE} */
  @Test
  public void shouldCreateEnumWithSingleValueWithoutSubfields() {
    result = classBuilder.withName("org.Human").addField("MALE").addField("FEMALE").build();
    MatcherAssert.assertThat(
        result,
      CoreMatchers.anyOf(
        CoreMatchers.containsString("public enum Human { MALE, FEMALE }"),
        CoreMatchers.containsString("public enum Human { MALE, FEMALE; }")));
  }

  /**
   * package org; public enum Country { GERMANY("Deutschland", 200), FRANCE("Frankreich", 300);
   * private final String name; private final Integer size; Country(String name, Integer size) {
   * this.name = name; this.size = size; } public Integer getSize() { return size; } public String
   * getName() { return name; } }
   */
  @Test
  public void shouldCreateCountryEnumWithGermanyAndFranceAndAStringSubfieldAndAIntegerSubfield() {
    result =
        classBuilder
            .withName("org.Country")
            .setFields(new String[] {"name", "size"}, new Class[] {String.class, Integer.class})
            .addField("GERMANY", new Object[]{"Deutschland", 200})
            .addField("FRANCE", new Object[]{"Frankreich", 300})
            .build();
    MatcherAssert.assertThat("class", result, CoreMatchers.containsString("public enum Country {"));
    MatcherAssert.assertThat("constants", result, CoreMatchers.containsString("GERMANY(\"Deutschland\", 200), FRANCE"));
    MatcherAssert.assertThat(
      "fields", result, CoreMatchers.containsString("private final String name; private final Integer size;"));
    MatcherAssert.assertThat(
        "constructor",
        result,
      CoreMatchers.containsString(
            "Country(String name, Integer size) { this.name = name; this.size = size; }"));
    MatcherAssert.assertThat("getter.getSize", result, CoreMatchers.containsString("public Integer getSize() { "));
    MatcherAssert.assertThat("getter.getName", result, CoreMatchers.containsString("public String getName() { "));
  }

  /**
   * package com.tcmj.any; public enum AnyName { AAA("700"), BBB("500"); private final String
   * version; AnyName(String version) { this.version = version; } public String getVersion() {
   * return "5"; } }
   */
  @Test
  public void shouldOverrideAGetterWithFixedValue() {
    result = createVersionSubfieldEnum().overrideGetter("version", "return \"5\";").build();
    MatcherAssert.assertThat(result, CoreMatchers.containsString("public String getVersion() { return \"5\"; }"));
  }

  /**
   * package com.tcmj.any; public enum AnyName { AAA("700"), BBB("500"); private final String
   * version; AnyName(String version) { this.version = version; } public String getVersion() {
   * return this.version.toLowerCase(); } }
   */
  @Test
  public void shouldOverrideAGetterWithToLowerCase() {
    result =
        createVersionSubfieldEnum()
            .overrideGetter("version", "return this.version.toLowerCase();")
            .build();
    MatcherAssert.assertThat(
        result,
      CoreMatchers.containsString("public String getVersion() { return this.version.toLowerCase(); }"));
  }

  /**
   * package com.tcmj.a.b.c; import java.util.Date; public enum Sign { MUNICH(8000,
   * "Fleischpflanzerl", new Date()), BERLIN(2000, "Frikadelle", new Date()); private final int one;
   * private final String two; private final Date three; Sign(int one, String two, Date three) {
   * this.one = one; this.two = two; this.three = three; } public int getOne() { return this.one +
   * 50; } public String getTwo() { return String.valueOf(one); } public Date getThree() { return
   * three; } }
   */
  @Test
  public void shouldOverrideTwoGettersWithToLowerCase() {
    result =
        classBuilder
            .withName("com.tcmj.a.b.c.Sign")
            .setFields(
                new String[] {"one", "two", "three"},
                new Class[] {int.class, String.class, Date.class})
            .addField("MUNICH", new Object[]{8000, "Fleischpflanzerl", new Date()})
            .addField("BERLIN", new Object[]{2000, "Frikadelle", new Date()})
            .overrideGetter("two", "return String.valueOf(one);")
            .overrideGetter("one", "return this.one + 50;")
            .build();
    MatcherAssert.assertThat("MUNICH", result, CoreMatchers.containsString("MUNICH(8000, \"Fleischpflanzerl\", new Date())"));
    MatcherAssert.assertThat("BERLIN", result, CoreMatchers.containsString("BERLIN(2000, \"Frikadelle\", new Date())"));
    MatcherAssert.assertThat("getOne", result, CoreMatchers.containsString("public int getOne() { return this.one + 50; }"));
    MatcherAssert.assertThat(
      "getTwo", result, CoreMatchers.containsString("public String getTwo() { return String.valueOf(one); }"));
  }

  /**
   * package com.tcmj.any; import org.apache.commons.lang3.StringUtils; import
   * org.apache.commons.lang3.StringUtils; public enum AnyName { AAA("700"), BBB("500"); private
   * final String version; AnyName(String version) { this.version = version; } public String
   * getVersion() { return StringUtils.reverse(this.version); } }
   */
  @Test
  public void shouldOverrideAGetterWithMandatoryImport() {
    result =
        createVersionSubfieldEnum()
            .overrideGetter("version", "return StringUtils.reverse(this.version);")
            .addImport("org.apache.commons.lang3.StringUtils")
            .build();
    MatcherAssert.assertThat(
        "overrideGetter",
        result,
      CoreMatchers.containsString("public String getVersion() { return StringUtils.reverse(this.version); }"));
    MatcherAssert.assertThat("import", result, CoreMatchers.containsString("import org.apache.commons.lang3.StringUtils;"));
  }

  @Test
  public void shouldCreateASimpleClassJavaDoc() {
    result = createVersionSubfieldEnum().addClassJavadoc("Hello World").build();

    //        assertThat("enu", StringUtils.countMatches(result,"public enum AnyName"), is(1));
    final String regex = "/\\*(?:.|[\\n\\r])*?\\*/";
    MatcherAssert.assertThat(
        "Cannot validdfdfdate: ClassJavaDoc",
        Pattern.compile(regex).matcher(result).find(),
      CoreMatchers.is(true));

    //Regex-Replace:
    //System.out.println(Pattern.compile(regex).matcher(result).replaceAll("xxxxx"));

    System.out.println(Pattern.compile(regex).matcher(result).replaceAll("xxxxx"));

    MatcherAssert.assertThat(
      "Cannot validate: ClassJavaDoc", Pattern.compile(regex).matcher(result).find(), CoreMatchers.is(true));

    MatcherAssert.assertThat("jDocStart", result, CoreMatchers.containsString("/**"));
    MatcherAssert.assertThat(
        "jDoc",
        result,
      CoreMatchers.anyOf(CoreMatchers.containsString("/** Hello World */"), CoreMatchers.containsString("/** * Hello World */")));
    MatcherAssert.assertThat("jDocEnd", result, CoreMatchers.containsString("*/"));
  }

  private void validateGeneralConstruction() {
    MatcherAssert.assertThat(
        "Cannot validate: Package",
        result.matches(String.format("^package %s;.*", classBuilder.getModel().getPackageName())),
      CoreMatchers.is(true));

    //        if(classBuilder.getModel().getJavaDocLines(EnumData.JDocKeys.CLASS.name())!=null){
    if (classBuilder.getModel().isJavaDoc(EnumData.JDocKeys.CLASS.name())) {
      final String regex = "/\\*(?:.|[\\n\\r])*?\\*/";
      MatcherAssert.assertThat(
        "Cannot validate: ClassJavaDoc", Pattern.compile(regex).matcher(result).find(), CoreMatchers.is(true));
    }

    //        assertThat("enu", StringUtils.countMatches(result, "public enfum AnyName"), is(1));

  }

  @Test
  public void shouldHandleCustomStaticGetterMethod() {
    result =
      classBuilder
        .withName("org.a.Currency")
        .addField(
          "EUR",
          new String[]{"name", "exponent"},
          new Class[]{String.class, Integer.class},
          new Object[]{"Euro", 2})
        .addField(
          "USD",
          new String[]{"name", "exponent"},
          new Class[]{String.class, Integer.class},
          new Object[]{"US Dollar", 2})
        .addCustomStaticGetMethod(
          "get",
          String.class,
          "value",
          "return java.util.stream.Stream.of(values()).filter(code -> code.name.equalsIgnoreCase(value)).findFirst().get();",
          "convert to amount")
        .build();

    MatcherAssert.assertThat(
      result,
      CoreMatchers.containsString("public static Currency get(String value) { return java.util.stream.Stream.of(values()).filter(code -> code.name.equalsIgnoreCase(value)).findFirst().get(); }"));
  }

}
