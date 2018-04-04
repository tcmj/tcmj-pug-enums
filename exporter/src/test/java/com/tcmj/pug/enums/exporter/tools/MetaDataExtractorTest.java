package com.tcmj.pug.enums.exporter.tools;

import com.tcmj.pug.enums.exporter.impl.TestDataProvider;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/** Unit tests of {@link MetaDataExtractor} */
public class MetaDataExtractorTest {

  private TestDataProvider data = new TestDataProvider();

  @Test
  public void testGetPackageName() {
    System.out.println(data.getEnumNamed("com.tcmj.iso", "MyEnum"));

    assertThat(
        "1",
        MetaDataExtractor.getPackageName(data.getEnumNamed("com.tcmj.iso", "MyEnum")),
        equalTo("com.tcmj.iso"));
    assertThat(
        "2",
        MetaDataExtractor.getPackageName(data.getEnumNamed("java.util.foo", "MyEnum")),
        equalTo("java.util.foo"));
    assertThat(
        "3",
        MetaDataExtractor.getPackageName(data.getEnumNamed("a.b.c.d.e", "Food")),
        equalTo("a.b.c.d.e"));
  }

  @Test(expected = NullPointerException.class)
  public void testGetPackageNameNullPointer() {
    MetaDataExtractor.getPackageName(null);
  }

  @Test
  public void testGetPackageDirectoriesNull() {
    assertThat(MetaDataExtractor.getPackageDirectories("there is no package"), nullValue());
  }

  @Test
  public void testGetgetPackageDirectories() {
    String source = "package org.foo.bar; class Money{...}";
    assertThat(MetaDataExtractor.getPackageDirectories(source), equalTo("org/foo/bar"));
  }

  @Test
  public void testGetPackageNameNotAvailable() {
    assertThat(MetaDataExtractor.getPackageDirectories("public enum without package but a ;"), nullValue());
  }

  @Test
  public void testGetClassNameSimple() {
    assertThat(
        "1",
        MetaDataExtractor.getClassNameSimple(data.getEnumNamed("com.tcmj.iso", "MyEnum")),
        equalTo("MyEnum"));
    assertThat(
        "2",
        MetaDataExtractor.getClassNameSimple(data.getEnumNamed("java.util.foo", "MyEnum")),
        equalTo("MyEnum"));
    assertThat(
        "3",
        MetaDataExtractor.getClassNameSimple(data.getEnumNamed("a.b.c.d.e", "Food")),
        equalTo("Food"));
  }

  @Test
  public void testGetClassNameSimpleFromEnumWithJavadoc() {
    //given
    String tstenum = "package com.tcmj.html;\n"
        + "/**\n"
        + "  *\n"
        + "  * This is my dynamically generated java enum class.\n"
        + "  * <p>Data has been fetched from https://en.wikipedia.org/wiki/ISO_3166-1\n"
        + "  *\n"
        + "  */\n"
        + "public enum ClassNameHtmlEnum {\n"
        + "  AFGHANISTAN(\"AF\", \"AFG\", \"004\"),\n"
        + "...";
    System.out.println(tstenum);
    assertThat(
        MetaDataExtractor.getClassNameSimple(tstenum),
        equalTo("ClassNameHtmlEnum"));

  }

  @Test
  public void testGetClassNameUnformated() {
    String source = " package   com.tcmj.iso ;  import java.util.Date;   public   enum   UnFormat  { A, B ,C  }";
    assertThat(MetaDataExtractor.getClassNameSimple(source), equalTo("UnFormat"));
    assertThat(MetaDataExtractor.getPackageDirectories(source), equalTo("com/tcmj/iso"));
    assertThat(MetaDataExtractor.getPackageDirectories(source), equalTo("com/tcmj/iso"));
    assertThat(MetaDataExtractor.getFileNameSingle(source), equalTo("UnFormat.java"));
  }

  @Test
  public void testGetFileNameFull() {
    assertThat("1", MetaDataExtractor.getFileNameFull(data.getEnumNamed("com.tcmj.iso", "MyEnum")),
        equalTo("com/tcmj/iso/MyEnum.java"));
    assertThat("2", MetaDataExtractor.getFileNameFull(data.getEnumNamed("java.util.foo", "MyEnum")),
        equalTo("java/util/foo/MyEnum.java"));
    assertThat("3", MetaDataExtractor.getFileNameFull(data.getEnumNamed("a.b.c.d.e", "Food")),
        equalTo("a/b/c/d/e/Food.java"));
  }
}
