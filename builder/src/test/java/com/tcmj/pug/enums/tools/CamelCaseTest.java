package com.tcmj.pug.enums.tools;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CamelCaseTest {

  @Test
  public void toCamelCase() {
    assertThat(CamelCase.toCamelCase(null), nullValue());
    assertThat(CamelCase.toCamelCase(""), equalTo(""));
    assertThat(CamelCase.toCamelCase(" "), equalTo(""));
    assertThat(CamelCase.toCamelCase("one"), equalTo("One"));
    assertThat(CamelCase.toCamelCase("one_for_the_money"), equalTo("OneForTheMoney"));
    assertThat(CamelCase.toCamelCase("oneforthemoney"), equalTo("Oneforthemoney"));
    assertThat(CamelCase.toCamelCase("one-for-the-money"), equalTo("OneForTheMoney"));
    assertThat(CamelCase.toCamelCase("one for the money"), equalTo("OneForTheMoney"));
  }

  @Test
  public void toGetter() {
    assertThat(CamelCase.toGetter("appel"), equalTo("getAppel"));
    assertThat(CamelCase.toGetter("one_for_the_money"), equalTo("getOneForTheMoney"));
  }

  @Test
  public void toSetter() {
    assertThat(CamelCase.toSetter("appel"), equalTo("setAppel"));
    assertThat(CamelCase.toSetter("one_for_the_money"), equalTo("setOneForTheMoney"));
  }
}