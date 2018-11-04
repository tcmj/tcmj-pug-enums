package com.tcmj.plugins;

import com.tcmj.pug.enums.api.EnumResult;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Just another way to test our maven plugin with some nice features.
 */
public class GenerateEnumMojoTestCase extends AbstractMojoTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testNumber7b() throws Exception {

    //our base-dir is relative to the projects folder
    String pomPath = "src\\test\\resources\\projects\\html7\\pom.xml";

    GenerateEnumMojo enumMojo = getGenerateEnumMojo(pomPath);

    enumMojo.execute();

    EnumResult currentEnumResult = enumMojo.getCurrentEnumResult();

    enumMojo.getLog().info("Result: " + currentEnumResult.getResult());

    EnumResult projectArtifact = (EnumResult) getVariableValueFromObject(enumMojo, "currentEnumResult");
    assertNotNull("Result", projectArtifact);

    assertThat("classname", currentEnumResult.getResult(),
      containsString("public enum ColorEnum70 {"));

  }

  private GenerateEnumMojo getGenerateEnumMojo(String pomXml)
    throws Exception {
    File testPom = new File(getBasedir(), pomXml);
    GenerateEnumMojo mojo = (GenerateEnumMojo) lookupMojo("generate-enum", testPom);

    setVariableValueToObject(mojo, "className", "com.tcmj.html.test.ColorEnum70");

    return mojo;
  }


}