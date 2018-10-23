package com.tcmj.pug.enums.api.tools;

import org.junit.Test;

import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.aToZandNumbersOnly;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.camel;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.camelStrict;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.extractParenthesis;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.flattenGermanUmlauts;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.harmonize;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.lowerCase;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.removeCommas;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.removeProhibitedSpecials;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.removeSpaces;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.replaceAtoZ;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.space2underline;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.trim;
import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.upperCase;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class NamingStrategyFactoryTest {
  
  
  @Test
  public void testLowerCase() {
    assertThat("lowerCase()", lowerCase().convert("PLEASE BE QUIET!"), equalTo("please be quiet!"));
    assertThat("lowerCase().nullsafe", lowerCase().convert(null), nullValue());
  }

  @Test
  public void testTrim() {
    assertThat("trim()", trim().convert("   aaa   "), equalTo("aaa"));
    assertThat("trim().nullsafe", trim().convert(null), nullValue());
  }

  @Test
  public void testUpperCase() {
    assertThat("upperCase()", upperCase().convert("aaa"), equalTo("AAA"));
    assertThat("upperCase().nullsafe", upperCase().convert(null), nullValue());
  }

  @Test
  public void testSpace2underline() {
    assertThat("1", space2underline().convert("hallo welt"), equalTo("hallo_welt"));
    assertThat("2", space2underline().convert("hallo  welt"), equalTo("hallo_welt"));
    assertThat("3", space2underline().convert("hallo   welt"), equalTo("hallo_welt"));
    assertThat("4", space2underline().convert("hallo       welt     zwei"), equalTo("hallo_welt_zwei"));
    assertThat("space2underline().nullsafe", space2underline().convert(null), nullValue());
  }

  @Test
  public void testRemoveSpaces() {
    assertThat("trim()", removeSpaces().convert("hallo welt"), equalTo("hallowelt"));
    assertThat("trim().nullsafe", removeSpaces().convert(null), nullValue());
  }

  @Test
  public void testCamel() {
    //simplified camel case handling
    assertThat("1", camel().convert("hallo welt"), equalTo("HalloWelt"));
    assertThat("2", camel().convert("hallo_welt"), equalTo("HalloWelt"));
    assertThat("3", camel().convert("lowered Camel UPPERED"), equalTo("LoweredCamelUPPERED"));
    assertThat("4", camel().convert("ALL_BIG ALL GREAT"), equalTo("ALLBIGALLGREAT"));
    assertThat("null", camel().convert(null), nullValue());
  }

  @Test
  public void testCamelStrict() {
    //strict camel case handling
    assertThat("1", camelStrict().convert("hallo welt"), equalTo("HalloWelt"));
    assertThat("2", camelStrict().convert("hallo_welt"), equalTo("HalloWelt"));
    assertThat("3", camelStrict().convert("HALLO_WELT"), equalTo("HalloWelt"));
    assertThat("4", camelStrict().convert("lowered Camel UPPERED"), equalTo("LoweredCamelUppered"));
    assertThat("5", camelStrict().convert("ALL_BIG ALL GREAT"), equalTo("AllBigAllGreat"));
    assertThat("6", camelStrict().convert("Afghanistan"), equalTo("Afghanistan"));
    assertThat("null", camelStrict().convert(null), nullValue());
  }

  @Test
  public void testHarmonize() {
    //replace everything you can to get java conform - including replacement of special version of a-Z with plain versions
    assertThat("1.ABC", harmonize().convert("ABC"), equalTo("ABC"));
    assertThat("2.XYZ", harmonize().convert("XYZ"), equalTo("XYZ"));
    assertThat("3.abc", harmonize().convert("abc"), equalTo("abc"));
    assertThat("4.xyz", harmonize().convert("xyz"), equalTo("xyz"));
    assertThat("ã", harmonize().convert("ã"), equalTo("a"));
    assertThat("í", harmonize().convert("í"), equalTo("i"));
    assertThat("Î", harmonize().convert("Î"), equalTo("I"));
    assertThat("Å", harmonize().convert("Å"), equalTo("A"));
    assertThat("é", harmonize().convert("é"), equalTo("e"));
    assertThat("è", harmonize().convert("è"), equalTo("e"));
    assertThat("É", harmonize().convert("É"), equalTo("E"));
    assertThat("États", harmonize().convert("États-Unis d'Amérique"), equalTo("EtatsUnisdAmerique"));
    assertThat("specials-À", harmonize().convert("À"), equalTo("A"));
    assertThat("specials-2", harmonize().convert("()=?ß"), equalTo("s"));
    assertThat("specials-3", harmonize().convert("()=?ß`´üÖ*'+#-_.:,;"), equalTo("suO"));
    assertThat("specials-4", harmonize().convert("a°^1§$%&/"), equalTo("a1"));
    assertThat("trim().nullsafe", harmonize().convert(null), nullValue());
  }

  @Test
  public void testReplaceAtoZ() {
    //leave everything as it is but replace special version of a-Z with plain versions
    assertThat("1.ABC", replaceAtoZ().convert("ABC"), equalTo("ABC"));
    assertThat("2.XYZ", replaceAtoZ().convert("XYZ"), equalTo("XYZ"));
    assertThat("3.abc", replaceAtoZ().convert("abc"), equalTo("abc"));
    assertThat("4.xyz", replaceAtoZ().convert("xyz"), equalTo("xyz"));
    assertThat("États", replaceAtoZ().convert("États-Unis d'Amérique"), equalTo("Etats-Unis d'Amerique"));
    assertThat("specials-À", replaceAtoZ().convert("À"), equalTo("A"));
    assertThat("specials-2", replaceAtoZ().convert("()=?ß"), equalTo("()=?s"));
    assertThat("specials-3", replaceAtoZ().convert("()=?ß`´üÖ*'+#-_.:,;"), equalTo("()=?s`´uO*'+#-_.:,;"));
    assertThat("specials-4", replaceAtoZ().convert("a°^1§$%&/"), equalTo("a°^1§$%&/"));
    assertThat("nullsafety", replaceAtoZ().convert(null), nullValue());
  }

  @Test
  public void testRemoveProhibitedSpecials() {
    assertThat("null", removeProhibitedSpecials().convert(null), nullValue());
    //leave everything as it is but replace special version of a-Z with plain versions
    assertThat("1", removeProhibitedSpecials().convert("AbCZz4"), equalTo("AbCZz4"));
    assertThat("2", removeProhibitedSpecials().convert("abra-cadabra"), equalTo("abra-cadabra"));
    assertThat("3.32", removeProhibitedSpecials().convert("x x"), equalTo("x x"));
    assertThat("3.45", removeProhibitedSpecials().convert("x-x"), equalTo("x-x"));
    assertThat("3.46", removeProhibitedSpecials().convert("z.z"), equalTo("z.z"));
    assertThat("3.95", removeProhibitedSpecials().convert("o_O"), equalTo("o_O"));
    assertThat("3.95", removeProhibitedSpecials().convert("o_O"), equalTo("o_O"));
  }

  @Test
  public void testExtractParenthesis() {
    assertThat("null", extractParenthesis().convert(null), nullValue());
    assertThat( "1", extractParenthesis().convert("Bolivia (Plurinational State of)"),
        equalTo("Plurinational State of Bolivia"));
    assertThat( "2", extractParenthesis().convert("Congo (Democratic Republic of the)"),
        equalTo("Democratic Republic of the Congo"));
    assertThat( "3", extractParenthesis().convert(" Korea (Democratic People's Republic of) "),
        equalTo("Democratic People's Republic of Korea"));
  }
  
  @Test
  public void testFlattenGermanUmlauts() {
    assertThat("1", flattenGermanUmlauts().convert("Mühle"), equalTo("Muehle"));
    assertThat("2", flattenGermanUmlauts().convert("Ärger"), equalTo("Aerger"));
    assertThat("3", flattenGermanUmlauts().convert("Hallö meine großen lüneburger Ösen"), equalTo("Halloe meine grossen lueneburger Oesen"));
    assertThat("4", flattenGermanUmlauts().convert("xxöxx xxÖxx xxäxx xxÄxx xxüxx xxÜxx"), equalTo("xxoexx xxOexx xxaexx xxAexx xxuexx xxUexx"));
    assertThat("nullsafe", flattenGermanUmlauts().convert(null), nullValue());
  }

  @Test
  public void testcamel() {
    assertThat("1", camel().convert("mühle"), equalTo("Mühle"));
    assertThat("2", camel().convert("ärger"), equalTo("Ärger"));
    assertThat("3", camel().convert("Hallö meine großen lüneburger Ösen"), equalTo("HallöMeineGroßenLüneburgerÖsen"));
    assertThat("4", camel().convert("xxöxx xxÖxx xxäxx xxÄxx xxüxx xxÜxx"), equalTo("XxöxxXxÖxxXxäxxXxÄxxXxüxxXxÜxx"));
    assertThat("nullsafe", camel().convert(null), nullValue());
  }

  @Test
  public void testRemoveCommas() {
    assertThat("removeCommas()", removeCommas().convert("Dampf, Hans"), equalTo("Dampf Hans"));
    assertThat("nullsafe", removeCommas().convert(null), nullValue());
  }

  @Test
  public void testAToZandNumbersOnly() {
    assertThat("aToZandNumbersOnly()", aToZandNumbersOnly().convert("H$e§l?lo%W)o/r(l!d"), equalTo("HelloWorld"));
    assertThat("aToZandNumbersOnly()", aToZandNumbersOnly().convert("H$e§l?lo %W)o/r(l!d"), equalTo("Hello World"));
    assertThat("nullsafe", aToZandNumbersOnly().convert(null), nullValue());
  }

}
