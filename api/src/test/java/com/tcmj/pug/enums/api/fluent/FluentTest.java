package com.tcmj.pug.enums.api.fluent;

import java.util.Arrays;
import java.util.Collection;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test of getDefaultNamingStrategyConstantNames method, of class Fluent.
 */
@RunWith(Parameterized.class)
public class FluentTest {
  @Parameters(name = "{index}: {0}={1}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
      {"Baden-Württemberg", "BADEN_WUERTTEMBERG"},
      {"Freistaat Bayern", "FREISTAAT_BAYERN"},
      {"(Freie und Hansestadt Hamburg)", "FREIE_UND_HANSESTADT_HAMBURG"},
      {"North Rhine- Westphalia", "NORTH_RHINE_WESTPHALIA"},
      {"Saarland", "SAARLAND"},
      {"Schleswig-Holstein", "SCHLESWIG_HOLSTEIN"},
      {"Freistaat Thüringen", "FREISTAAT_THUERINGEN"}
    });
  }
  private final String paramInput;
  private final String paramExpected;

  public FluentTest(String input, String expected) {
    this.paramInput = input;
    this.paramExpected = expected;
  }

  @Test
  public void testGetDefaultNamingStrategyConstantNames() {
    assertThat(Fluent.getDefaultNamingStrategyConstantNames().convert(this.paramInput), equalTo(paramExpected));
  }

}
