package com.tcmj.pug.enums.mvn;

import java.io.File;
import java.nio.file.Files;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import static org.codehaus.plexus.PlexusTestCase.getTestFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of GenerateEnumHtmlMojo.
 *
 * @see http://maven.apache.org/plugin-testing/maven-plugin-testing-harness/getting-started/index.html
 */
public class GenerateEnumHtmlMojoTest {
  @Rule
  public MojoRule rule = new MojoRule();

  @Rule
  public TestResources resources = new TestResources("src/test/resources/projects", "target/test-projects");

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
    File src = getTestFile("target/generated-test-sources/project-to-test/com/tcmj/html/test/MyCountriesEnum1.java");
    assertThat("Enum does not exist", Files.isRegularFile(src.toPath()), is(true));
  }

  @Test
  public void enumWithSubfields() throws Exception {
    GenerateEnumMojo mojo = getMojo("html2");
    mojo.execute();
    File src = getTestFile("target/generated-test-sources/project-to-test/com/tcmj/html/test/MyCountriesEnum2.java");
    assertThat("Enum does not exist", Files.isRegularFile(src.toPath()), is(true));
  }

  @Test
  public void overridenFieldNames() throws Exception {
    GenerateEnumMojo mojo = getMojo("html3");
    mojo.execute();
    File src = getTestFile("target/generated-test-sources/project-to-test/com/tcmj/html/test/StatesEnum.java");
    assertThat("Enum does not exist", Files.isRegularFile(src.toPath()), is(true));
  }

}
