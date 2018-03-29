package com.tcmj.pug.enums.mvn;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Test of GenerateEnumHtmlMojo.
 * @see <url>http://maven.apache.org/plugin-testing/maven-plugin-testing-harness/getting-started/index.html</url>
 */
public class GenerateEnumHtmlMojoTest {
  @Rule
  public MojoRule rule = new MojoRule();

  @Rule
  public TestResources resources = new TestResources("src/test/resources/projects", "target/test-projects");

  static final Path outputPath = Paths.get("target/generated-test-sources/project-to-test/com/tcmj/html/test");

  private GenerateEnumMojo getMojo(String projectDir) throws Exception {
    File pom = new File(this.resources.getBasedir(projectDir), "pom.xml");
    assertThat("pom File object is null", pom, notNullValue());
    assertThat("pom.xml does not exist", Files.isRegularFile(pom.toPath()), is(true));
    GenerateEnumMojo mojo = (GenerateEnumMojo) this.rule.lookupMojo("generate-enum", pom);
    assertThat("Mojo is null in " + projectDir, mojo, notNullValue());
    return mojo;
  }

  @Test
  public void simpleEnumWithoutSubfields() throws Exception {
    GenerateEnumMojo mojo = getMojo("html1");
    mojo.execute();
    File src = new File("target/generated-test-sources/project-to-test/com/tcmj/html/test/MyCountriesEnum1.java");
    assertThat("Enum does not exist", Files.isRegularFile(src.toPath()), is(true));
  }

  @Test
  public void enumWithSubfields() throws Exception {
    GenerateEnumMojo mojo = getMojo("html2");
    mojo.execute();
    File src = new File("target/generated-test-sources/project-to-test/com/tcmj/html/test/MyCountriesEnum2.java");
    assertThat("Enum does not exist", Files.isRegularFile(src.toPath()), is(true));
  }

  @Test
  public void overridenFieldNames() throws Exception {
    GenerateEnumMojo mojo = getMojo("html3");
    mojo.execute();
    File src = new File("target/generated-test-sources/project-to-test/com/tcmj/html/test/StatesEnum.java");
    assertThat("Enum does not exist", Files.isRegularFile(src.toPath()), is(true));
  }

  @Test
  public void staticHtmlFile() throws Exception {
    //Just ensure that our static html file is available
    File html = new File(this.resources.getBasedir("html4"), "javacodes.html");
    assertThat("Html file is not available", Files.isRegularFile(html.toPath()), is(true));
    
    URL myUrl = html.toURI().toURL();
    System.out.println("myUrl="+myUrl);
        
    //..continue with regular test...
    GenerateEnumMojo mojo = getMojo("html4");
    mojo.execute();
    
  }
  @Test
  public void staticHtmlFile5() throws Exception {
    //Just ensure that our static html file is available
    File html = new File(this.resources.getBasedir("html5"), "eu.html");
    assertThat("Html file is not available", Files.isRegularFile(html.toPath()), is(true));
    
    URL myUrl = html.toURI().toURL();
    System.out.println("myUrl="+myUrl);
        
    //..continue with regular test...
    GenerateEnumMojo mojo = getMojo("html5");
    mojo.execute();
    
  }

  @Test
  public void staticHtmlFile6() throws Exception {
    //Just ensure that our static html file is available
    File html = new File(this.resources.getBasedir("html6"), "eu.html");
    assertThat("Html file is not available", Files.isRegularFile(html.toPath()), is(true));

    URL myUrl = html.toURI().toURL();
    System.out.println("myUrl="+myUrl);

    //..continue with regular test...
    GenerateEnumMojo mojo = getMojo("html6");
    mojo.execute();

  }

  /**
   * Per default the first row of a html table will be skipped. This test case
   * must not skip the first row because there are no header/title and we want
   * all data (including the first row). This 'first row' has the value 'BLACK'
   * so we can test against it.
   */
  @Test
  public void staticHtmlFileIncludingFirstRow() throws Exception {
    assertThat("Html file is not available", Files.isRegularFile(new File(this.resources.getBasedir("html7"), "example.html").toPath()), is(true));

    getMojo("html7").execute();

    Path result = outputPath.resolve(Paths.get("ColorEnum7.java"));
    assertThat("Result file hasn't been created!", Files.isRegularFile(result), is(true));
    String content = String.join("", Files.readAllLines(result));

    assertThat("Must contain BLACK", content, containsString("BLACK"));
  }


  /**
   * Per default the first row of a html table will be skipped. This test case
   * must not skip the first row because there are no header/title and we want
   * all data (including the first row). This 'first row' has the value 'BLACK'
   * so we can test against it.
   */
  @Test
  public void staticHtmlFile8DeleteRows() throws Exception {
    assertThat("Html file is not available", Files.isRegularFile(new File(this.resources.getBasedir("html8"), "example.html").toPath()), is(true));

    getMojo("html8").execute();

    Path result = outputPath.resolve(Paths.get("Revenues8.java"));
    assertThat("Result file hasn't been created!", Files.isRegularFile(result), is(true));
    String content = String.join("", Files.readAllLines(result));

    assertThat("1", content, not(containsString("WE_DONT_WANT_THIS_PRODUCT")));
    assertThat("2", content, not(containsString("NAME")));
    assertThat("3", content, not(containsString("SUM")));
    assertThat("4", content, containsString("PRODUCT_A"));
  }

}
