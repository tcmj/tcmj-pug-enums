package com.tcmj.pug.enums.mvn;

import java.io.File;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import static org.codehaus.plexus.PlexusTestCase.getTestFile;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

  public GenerateEnumHtmlMojoTest() {
  }

  @Test
  public void simple() throws Exception {
    File pom4 = getTestFile("src/test/resources/projects/html1/pom.xml");
    File projectCopy = this.resources.getBasedir("html1");
    File pom = new File(projectCopy, "pom.xml");
    Assert.assertNotNull(pom);
    Assert.assertTrue(pom.exists());

    GenerateEnumHtmlMojo mojo = (GenerateEnumHtmlMojo) this.rule.lookupMojo("generate-enum-html", pom);
    Assert.assertNotNull(mojo);
    mojo.execute();
  }
  @Test
  public void subs() throws Exception {
     File projectCopy = this.resources.getBasedir("html2");
    File pom = new File(projectCopy, "pom.xml");
    Assert.assertNotNull(pom);
    Assert.assertTrue(pom.exists());

    GenerateEnumHtmlMojo mojo = (GenerateEnumHtmlMojo) this.rule.lookupMojo("generate-enum-html", pom);
    Assert.assertNotNull(mojo);
    mojo.execute();
  }
  @Test
  public void subs3() throws Exception {
     File projectCopy = this.resources.getBasedir("html3");
    File pom = new File(projectCopy, "pom.xml");
    Assert.assertNotNull(pom);
    Assert.assertTrue(pom.exists());

    GenerateEnumHtmlMojo mojo = (GenerateEnumHtmlMojo) this.rule.lookupMojo("generate-enum-html", pom);
    Assert.assertNotNull(mojo);
    mojo.execute();
  }

}
