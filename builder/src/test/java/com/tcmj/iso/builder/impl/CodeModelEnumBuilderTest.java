package com.tcmj.iso.builder.impl;

import com.tcmj.iso.builder.ClassBuilderFactory;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/** Unit Test for {@link CodeModelEnumBuilder}. */
public class CodeModelEnumBuilderTest extends ParentClassBuilderTest {

  @Before
  public void setUp() throws Exception {
    result = null;
    classBuilder = ClassBuilderFactory.getCodeModelEnumBuilder();
    classBuilder.usingCustomFormatter(sourceFormatter);
  }

  /**
   * package org; public enum Animal { DOG("Goldie"); private final String name; Animal(String name)
   * { this.name = name; } public String getName() { return name; } }
   */
  @Test
  public void shouldCreateEnumWithSingleValueWithAStringSubfield() {
    result =
        classBuilder
            .withName("org.Animal")
            .addField(
                "DOG", new String[] {"name"}, new Class[] {String.class}, new Object[] {"Goldie"})
            .build();
    assertThat("class", result, containsString("public enum Animal {"));
    assertThat("constants", result, containsString("DOG(\"Goldie\");"));
    assertThat("fields", result, containsString("private final String name;"));
    assertThat("constructor", result, containsString("Animal(String name) { this.name = name; }"));
    assertThat("getter", result, containsString("public String getName() { return name; }"));
  }

  /**
   * package org; public enum TimeUnit { SECONDS(1000L), MILLIES(1L); private final Long factor;
   * TimeUnit(Long factor) { this.factor = factor; } public Long getFactor() { return factor; } }
   */
  @Test
  public void shouldCreateEnumWithSingleValueWithTwoNumericSubfields() {
    result = classBuilder
            .withName("org.TimeUnit")
            .addField( "SECONDS", new String[] {"factor"}, new Class[] {Long.class}, new Object[] {1000L})
            .addField( "MILLIES", new String[] {"factor"}, new Class[] {Long.class}, new Object[] {1L})
            .build();
    assertThat("class", result, containsString("public enum TimeUnit {"));
    assertThat("constants", result, containsString("SECONDS(1000L), MILLIES(1L);"));
    assertThat("fields", result, containsString("private final Long factor;"));
    assertThat(
        "constructor", result, containsString("TimeUnit(Long factor) { this.factor = factor; }"));
    assertThat("getter", result, containsString("public Long getFactor() { return factor; }"));
  }
}
