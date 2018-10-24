package com.tcmj.pug.enums.api.tools;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class StringsTest {

  @Test
  public void removeWhitespace() {
    assertThat("null", Strings.removeWhitespace(null), nullValue());
    assertThat("1", Strings.removeWhitespace(""), equalTo(""));
    assertThat("2", Strings.removeWhitespace("x\u00A0x"), equalTo("xx"));
    assertThat("3", Strings.removeWhitespace("y\u2007y"), equalTo("yy"));
    assertThat("4", Strings.removeWhitespace("z\u202Fz"), equalTo("zz"));
    assertThat("5", Strings.removeWhitespace(" aa"), equalTo("aa"));
    assertThat("6", Strings.removeWhitespace(" aa "), equalTo("aa"));
    assertThat("7", Strings.removeWhitespace("aa "), equalTo("aa"));
  }

  @Test
  public void replaceSpaces() {
    assertThat("null", Strings.replaceSpaces(null), nullValue());
    assertThat("1", Strings.replaceSpaces(""), equalTo(""));
    assertThat("2", Strings.replaceSpaces("x\u00A0x"), equalTo("x x"));
    assertThat("3", Strings.replaceSpaces("y\u2007y"), equalTo("y y"));
    assertThat("4", Strings.replaceSpaces("z\u202Fz"), equalTo("z z"));
    assertThat("5", Strings.replaceSpaces("   aa   "), equalTo("   aa   "));
    assertThat("6", Strings.replaceSpaces("\u00A0o\u00A0o\u00A0o\u00A0"), equalTo(" o o o "));
    assertThat("7", Strings.replaceSpaces("\u00A0o\u2007o\u2007o\u00A0"), equalTo(" o o o "));
    assertThat("8", Strings.replaceSpaces("\u202Fo\u2007o\u2007o\u00A0"), equalTo(" o o o "));
  }


}