package com.tcmj.iso.exporter.tools;

import com.tcmj.iso.exporter.impl.TestDataProvider;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/** pugproductions - 2017-05-01 - tcmj. */
public class MetaDataExtractorTest {

  TestDataProvider data = new TestDataProvider();

  @Test
  public void testGetPackageName() throws Exception {
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
  public void testGetPackageNameNullPointer() throws Exception {
    MetaDataExtractor.getPackageName(null);
  }

  @Test(expected = IllegalStateException.class)
  public void testGetPackageNameNotAvailable() throws Exception {
    MetaDataExtractor.getPackageName("public enum without package but a ;");
  }

  @Test
  public void testGetClassNameSimple() throws Exception {
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
  public void testGetClassNameUnformated() throws Exception {
    String source =
        " package   com.tcmj.iso ;  import java.util.Date;   public   enum   UnFormat  { A, B ,C  }";
    assertThat(MetaDataExtractor.getClassNameSimple(source), equalTo("UnFormat"));
    assertThat(MetaDataExtractor.getPackageDirectories(source), equalTo("com/tcmj/iso"));
    assertThat(MetaDataExtractor.getFileNameSingle(source), equalTo("UnFormat.java"));
  }

  @Test
  public void testGetFileNameFull() throws Exception {
    assertThat(
        "1",
        MetaDataExtractor.getFileNameFull(data.getEnumNamed("com.tcmj.iso", "MyEnum")),
        equalTo("com/tcmj/iso/MyEnum.java"));
    assertThat(
        "2",
        MetaDataExtractor.getFileNameFull(data.getEnumNamed("java.util.foo", "MyEnum")),
        equalTo("java/util/foo/MyEnum.java"));
    assertThat(
        "3",
        MetaDataExtractor.getFileNameFull(data.getEnumNamed("a.b.c.d.e", "Food")),
        equalTo("a/b/c/d/e/Food.java"));
  }
}
