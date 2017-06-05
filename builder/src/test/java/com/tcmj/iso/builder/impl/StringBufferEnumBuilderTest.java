package com.tcmj.iso.builder.impl;

import com.tcmj.iso.builder.ClassBuilderFactory;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/** * Unit Test for {@link StringBufferEnumBuilder}. */
public class StringBufferEnumBuilderTest extends ParentClassBuilderTest {

  @Before
  public void setUp() throws Exception {
    classBuilder = ClassBuilderFactory.getEnumClassBuilder();
    classBuilder.usingCustomFormatter(sourceFormatter);
  }

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
    assertThat("getter", result, containsString("public String getName() { return this.name; }"));
  }

  @Test
  public void shouldCreateEnumWithSingleValueWithTwoNumericSubfields() {
    result =
        classBuilder
            .withName("org.TimeUnit")
            .addField(
                "SECONDS", new String[] {"factor"}, new Class[] {Long.class}, new Object[] {1000L})
            .addField(
                "MILLIES", new String[] {"factor"}, new Class[] {Long.class}, new Object[] {1L})
            .build();

    assertThat("class", result, containsString("public enum TimeUnit {"));
    assertThat("constants", result, containsString("SECONDS(1000L), MILLIES(1L);"));
    assertThat("fields", result, containsString("private final Long factor;"));
    assertThat(
        "constructor", result, containsString("TimeUnit(Long factor) { this.factor = factor; }"));
    assertThat("getter", result, containsString("public Long getFactor() { return this.factor; }"));
  }

  @Test
  public void testAppendCharacterIfMissing_TwoEqualChars() {
    StringBuilder buffer = new StringBuilder("Please wait"); //given
    StringBufferEnumBuilder.appendCharacterIfMissing(buffer, ".."); //when
    assertThat(buffer.toString(), equalTo("Please wait..")); //then
  }

  @Test
  public void testAppendCharacterIfMissing_TwoDifferentChars() {
    StringBuilder buffer = new StringBuilder("myMethod"); //given
    StringBufferEnumBuilder.appendCharacterIfMissing(buffer, "()"); //when
    assertThat(buffer.toString(), equalTo("myMethod()")); //then
  }

  @Test
  public void testAppendCharacterIfMissing_TwoDifferentCharsButOnlyOneMissing() {
    StringBuilder buffer = new StringBuilder("myMethod("); //given
    StringBufferEnumBuilder.appendCharacterIfMissing(buffer, "()"); //when
    assertThat(buffer.toString(), equalTo("myMethod()")); //then
  }

  @Test
  public void testAppendCharacterIfMissing_ManyDifferentChars() {
    StringBuilder buffer = new StringBuilder("abcdefg"); //given
    StringBufferEnumBuilder.appendCharacterIfMissing(buffer, "efghij"); //when
    assertThat(buffer.toString(), equalTo("abcdefghij")); //then
  }

  @Test
  public void testAppendCharacterIfMissing_TextIsSmallerThanTrailerChars() {
    StringBuilder buffer = new StringBuilder("ab"); //given
    StringBufferEnumBuilder.appendCharacterIfMissing(buffer, "bcdefg()"); //when
    assertThat(buffer.toString(), equalTo("abcdefg()")); //then
  }

  @Test
  public void testAppendCharacterIfMissing_TextIsRepeating() {
    StringBuilder buffer = new StringBuilder("ab ab ab ab ab"); //given
    StringBufferEnumBuilder.appendCharacterIfMissing(buffer, "()"); //when
    assertThat(buffer.toString(), equalTo("ab ab ab ab ab()")); //then
  }
}
