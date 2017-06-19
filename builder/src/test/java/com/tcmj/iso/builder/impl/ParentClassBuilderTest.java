package com.tcmj.iso.builder.impl;

import java.util.Date;
import java.util.regex.Pattern;
import com.tcmj.pug.enums.api.ClassBuilder;
import com.tcmj.pug.enums.api.SourceFormatter;
import com.tcmj.iso.api.model.ClassCreationException;
import com.tcmj.iso.api.model.EnumData;
import com.tcmj.iso.builder.ClassBuilderFactory;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Parent class for all EnumBuilder implementations. This tests should be proceed with all
 * implementations.
 */
public abstract class ParentClassBuilderTest {
  protected ClassBuilder classBuilder;
  private SourceFormatter formatter1 = rawSource -> rawSource.replace(",", ", ");
  private SourceFormatter formatter2 = ClassBuilderFactory.getNoLineBreaksSourceCodeFormatter();
  protected SourceFormatter sourceFormatter = formatter1.and(formatter2);
  protected String result;

  @After
  public void after() throws Exception {
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
    assertThat(
        result,
        anyOf(
            containsString("public enum Human { MALE, FEMALE }"),
            containsString("public enum Human { MALE, FEMALE; }")));
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
    assertThat("class", result, containsString("public enum Country {"));
    assertThat("constants", result, containsString("GERMANY(\"Deutschland\", 200), FRANCE"));
    assertThat(
        "fields", result, containsString("private final String name; private final Integer size;"));
    assertThat(
        "constructor",
        result,
        containsString(
            "Country(String name, Integer size) { this.name = name; this.size = size; }"));
    assertThat("getter.getSize", result, containsString("public Integer getSize() { "));
    assertThat("getter.getName", result, containsString("public String getName() { "));
  }

  /**
   * package com.tcmj.any; public enum AnyName { AAA("700"), BBB("500"); private final String
   * version; AnyName(String version) { this.version = version; } public String getVersion() {
   * return "5"; } }
   */
  @Test
  public void shouldOverrideAGetterWithFixedValue() {
    result = createVersionSubfieldEnum().overrideGetter("version", "return \"5\";").build();
    assertThat(result, containsString("public String getVersion() { return \"5\"; }"));
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
    assertThat(
        result,
        containsString("public String getVersion() { return this.version.toLowerCase(); }"));
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
    assertThat("MUNICH", result, containsString("MUNICH(8000, \"Fleischpflanzerl\", new Date())"));
    assertThat("BERLIN", result, containsString("BERLIN(2000, \"Frikadelle\", new Date())"));
    assertThat("getOne", result, containsString("public int getOne() { return this.one + 50; }"));
    assertThat(
        "getTwo", result, containsString("public String getTwo() { return String.valueOf(one); }"));
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
    assertThat(
        "overrideGetter",
        result,
        containsString("public String getVersion() { return StringUtils.reverse(this.version); }"));
    assertThat("import", result, containsString("import org.apache.commons.lang3.StringUtils;"));
  }

  @Test
  public void shouldCreateASimpleClassJavaDoc() {
    result = createVersionSubfieldEnum().addClassJavadoc("Hello World").build();

    //        assertThat("enu", StringUtils.countMatches(result,"public enum AnyName"), is(1));
    final String regex = "/\\*(?:.|[\\n\\r])*?\\*/";
    assertThat(
        "Cannot validdfdfdate: ClassJavaDoc",
        Pattern.compile(regex).matcher(result).find(),
        is(true));

    //Regex-Replace:
    //System.out.println(Pattern.compile(regex).matcher(result).replaceAll("xxxxx"));

    System.out.println(Pattern.compile(regex).matcher(result).replaceAll("xxxxx"));

    assertThat(
        "Cannot validate: ClassJavaDoc", Pattern.compile(regex).matcher(result).find(), is(true));

    assertThat("jDocStart", result, containsString("/**"));
    assertThat(
        "jDoc",
        result,
        anyOf(containsString("/** Hello World */"), containsString("/** * Hello World */")));
    assertThat("jDocEnd", result, containsString("*/"));
  }

  private void validateGeneralConstruction() {
    assertThat(
        "Cannot validate: Package",
        result.matches(String.format("^package %s;.*", classBuilder.getModel().getPackageName())),
        is(true));

    //        if(classBuilder.getModel().getJavaDocLines(EnumData.JDocKeys.CLASS.name())!=null){
    if (classBuilder.getModel().isJavaDoc(EnumData.JDocKeys.CLASS.name())) {
      final String regex = "/\\*(?:.|[\\n\\r])*?\\*/";
      assertThat(
          "Cannot validate: ClassJavaDoc", Pattern.compile(regex).matcher(result).find(), is(true));
    }

    //        assertThat("enu", StringUtils.countMatches(result, "public enfum AnyName"), is(1));

  }
}
