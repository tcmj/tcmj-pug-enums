package com.tcmj.pug.enums.builder.impl.format;

import com.tcmj.pug.enums.builder.ClassBuilderFactory;
import com.tcmj.pug.enums.api.ClassBuilder;
import com.tcmj.pug.enums.api.SourceFormatter;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/** pugproductions - 2017-04-27 - tcmj. */
public class SourceFormatterTest {

  private ClassBuilder getClassBuilder() {
    return ClassBuilderFactory.getEnumClassBuilder()
        .withName("com.tcmj.iso.Fruits")
        .addField("APPLE")
        .addField("BANANA");
  }

  @Test
  public void shouldFormatInTheCorrectOrder() {
    //given: are several formatter objects
    SourceFormatter first = text -> text.replace("APPLE", "MOUSE");
    SourceFormatter second = text -> text.replace("MOUSE", "BLUE");
    SourceFormatter third = text -> text.replace("BLUE", "ENGLAND");

    final String source = "APPLE"; //our first word

    //when: applying one step after the next ...then: we check for the correct order
    assertThat("first(APPLE==>MOUSE)", first.format(source), equalTo("MOUSE"));
    assertThat("first_and_second(MOUSE==>BLUE)", first.and(second).format(source), equalTo("BLUE"));
    assertThat(
        "first_and_second_and_third(BLUE==>ENGLAND)",
        first.and(second).and(third).format(source),
        equalTo("ENGLAND"));
  }

  @Test
  public void shouldChainSeveralFormatterObjectsAttemptTwo() {
    //just another test to verify correct chaining
    SourceFormatter a2b = text -> text.replace('a', 'b');
    SourceFormatter b2c = text -> text.replace('b', 'c');
    SourceFormatter c2d = text -> text.replace('c', 'd');
    SourceFormatter d2e = text -> text.replace('d', 'e');
    SourceFormatter e2x = text -> text.replace('e', 'X');
    SourceFormatter sourceFormatter = a2b.and(b2c).and(c2d).and(d2e).and(e2x);
    final String source = "a()_b[}_F!_%&d_;e";
    final String expect = "X()_X[}_F!_%&X_;X";
    assertThat("first", sourceFormatter.format(source), equalTo(expect));
  }

  @Test
  public void shouldCorrectlyFormatInTheEnumBuilderClass() {
    SourceFormatter javaDocChanger =
        text -> {
          int markerPos = text.indexOf("xxMARKERxx");
          String replacement = String.format("<p>Changed at Pos %s</p>", markerPos);
          return text.replace("xxMARKERxx", replacement);
        };

    String result =
        getClassBuilder()
            .usingCustomFormatter(javaDocChanger)
            .addClassJavadoc("Our first javadoc Line")
            .addClassJavadoc("Here we put the xxMARKERxx and something else")
            .addClassJavadoc("Our last javadoc Line")
            .build();

    assertThat("JDoc1", result, containsString("Our first javadoc Line"));
    assertThat("JDoc2a", result, containsString("Here we put the <p>Changed at Pos "));
    assertThat("JDoc2b", result, containsString("</p> and something else"));
    assertThat("JDoc3", result, containsString("Our last javadoc Line"));
    assertThat("Fruits", result, containsString("public enum Fruits"));

    assertThat("ENGLAND", result, containsString("APPLE"));
    assertThat("BANANA", result, containsString("BANANA"));
  }
}
