package com.tcmj.pug.enums.exporter.tools;

import com.tcmj.pug.enums.api.EnumResult;
import com.tcmj.pug.enums.exporter.impl.TestDataProvider;
import com.tcmj.pug.enums.model.EnumData;
import org.junit.Assert;
import org.junit.Test;

import static com.tcmj.pug.enums.exporter.tools.MetaDataExtractor.getClassNameSimple;
import static com.tcmj.pug.enums.exporter.tools.MetaDataExtractor.getPackageName;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/** Unit tests of {@link MetaDataExtractor} */
public class MetaDataExtractorTest {
  private TestDataProvider data = new TestDataProvider();

  private String getTestEnum(String packageName, String classNameSimple) {
    return data.getEnumComplete(packageName, classNameSimple);
  }

  @Test
  public void removeJavadocs() {
    String clazz = data.getStaticEnumComplete();
    Assert.assertTrue(clazz.contains("/**"));
    String result = MetaDataExtractor.removeJavadocs(clazz);
    Assert.assertFalse(result.contains("/**"));
  }

  @Test
  public void removeDocs() {
    String clazz = data.getStaticEnumComplete();
    Assert.assertTrue(clazz.contains("/*"));
    String result = MetaDataExtractor.removeDocs(clazz);
    Assert.assertFalse(result.contains("/*"));
  }

  @Test
  public void getPackageNameGreenTest() {
    assertThat("1", getPackageName(getTestEnum("com.tcmj.generated", "Animals")), equalTo("com.tcmj.generated"));
    assertThat("2", getPackageName(data.getEnumNamed("com.tcmj.iso", "MyEnum")), equalTo("com.tcmj.iso"));
    assertThat("3", getPackageName(data.getEnumNamed("java.util.foo", "MyEnum")), equalTo("java.util.foo"));
    assertThat("4", getPackageName(data.getEnumNamed("a.b.c.d.e", "Food")), equalTo("a.b.c.d.e"));
  }

  @Test
  public void getPackageNameSpecialA() {
    String data = "/*license*/package ar.be.ce.da.es;/** classdoc */...";
    assertThat(getPackageName(data), equalTo("ar.be.ce.da.es"));
  }

  @Test
  public void getPackageNameSpecialB() {
    String data = "  package   x.y.z.a.b  ;  /** classdoc */...";
    assertThat(getPackageName(data), equalTo("x.y.z.a.b"));
  }

  @Test
  public void getPackageNameNoPackage() {
    String data = "  dsfdsf  x.y.z.a.b  enum bla {dsfd};  /** jklo */...";
    assertThat(getPackageName(data), equalTo(""));
  }

  @Test(expected = NullPointerException.class)
  public void getPackageNameNull() {
    getPackageName(null);
  }

  @Test
  public void getPackageDirectoriesNoPackage() {
    assertThat("1", MetaDataExtractor.getPackageDirectories("there is no package"), equalTo(""));
    assertThat("2", MetaDataExtractor.getPackageDirectories("public enum without package but a ;"), equalTo(""));
  }

  @Test
  public void getPackageDirectoriesGreen() {
    String source = "package org.foo.bar; class Money{...}";
    assertThat(MetaDataExtractor.getPackageDirectories(source), equalTo("org/foo/bar"));
  }

  @Test
  public void getClassNameSimpleGreen() {
    assertThat("1", getClassNameSimple(data.getEnumNamed("com.tcmj.iso", "MyEnum")), equalTo("MyEnum"));
    assertThat("2", getClassNameSimple(data.getEnumNamed("java.util.foo", "MyEnum")), equalTo("MyEnum"));
    assertThat("3", getClassNameSimple(data.getEnumNamed("a.b.c.d.e", "Food")), equalTo("Food"));
  }

  @Test
  public void getClassNameSimpleFromEnumWithJavadoc() {
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
    assertThat(MetaDataExtractor.getClassNameSimple(tstenum), equalTo("ClassNameHtmlEnum"));
  }

  @Test
  public void getPackageDirectoriesEnumResult() {
    EnumData enumDataContent = new EnumData();
    enumDataContent.setClassName("com.tcmj.iso.Planes");
    EnumResult enumResult = EnumResult.of(enumDataContent, null);
    assertThat(MetaDataExtractor.getPackageDirectories(enumResult), equalTo("com/tcmj/iso"));
    assertThat(MetaDataExtractor.getPackageDirectories(enumResult), equalTo("com/tcmj/iso"));
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
  public void getFileNameFull() {
    assertThat("1", MetaDataExtractor.getFileNameFull(data.getEnumNamed("com.tcmj.iso", "MyEnum")),
      equalTo("com/tcmj/iso/MyEnum.java"));
    assertThat("2", MetaDataExtractor.getFileNameFull(data.getEnumNamed("java.util.foo", "MyEnum")),
      equalTo("java/util/foo/MyEnum.java"));
    assertThat("3", MetaDataExtractor.getFileNameFull(data.getEnumNamed("a.b.c.d.e", "Food")),
      equalTo("a/b/c/d/e/Food.java"));
  }

  @Test
  public void getClassNameEnumResult() {
    EnumData enumDataContent = new EnumData();
    enumDataContent.setClassName("com.tcmj.iso.Planes");
    EnumResult enumResult = EnumResult.of(enumDataContent, null);
    assertThat(MetaDataExtractor.getClassName(enumResult), equalTo("com.tcmj.iso.Planes"));
  }

  @Test
  public void getClassNameString() {
    String source = " package   com.tcmj.iso ;  import java.util.Date;   public   enum   UnFormat  { A, B ,C  }";
    assertThat(MetaDataExtractor.getClassName(source), equalTo("com.tcmj.iso.UnFormat"));
  }

  @Test
  public void getFileNameSingleEnumResult() {
    EnumData enumDataContent = new EnumData();
    enumDataContent.setClassName("com.tcmj.iso.Planes");
    EnumResult enumResult = EnumResult.of(enumDataContent, null);
    assertThat(MetaDataExtractor.getFileNameSingle(enumResult), equalTo("Planes.java"));
  }

  @Test
  public void getFileNameSingleString() {
    String source = " package   com.tcmj.iso ;  import java.util.Date;   public   enum   UnFormat  { A, B ,C  }";
    assertThat(MetaDataExtractor.getFileNameSingle(source), equalTo("UnFormat.java"));
  }
}
