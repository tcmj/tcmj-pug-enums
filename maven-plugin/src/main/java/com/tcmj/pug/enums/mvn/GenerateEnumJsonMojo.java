package com.tcmj.pug.enums.mvn;

import static com.tcmj.pug.enums.mvn.LittleHelper.arrange;
import static com.tcmj.pug.enums.mvn.LittleHelper.getLine;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/** Goal which extracts data from a Json source (URL or file). */
@Mojo(name = "generate-enum-json", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenerateEnumJsonMojo extends GeneralEnumMojo {

  @Parameter(
    property = "com.tcmj.pug.enums.dataprovider",
    defaultValue = "com.tcmj.generated.MyEnum",
    required = true
  )
  private String dataProvider;

  @Override
  protected void displayYoureWelcome() {
    super.displayYoureWelcome();
    getLog().info(arrange("Extracts EnumData from a JSON document!"));
    getLog().info(arrange("DataProvider: todo"));
    getLog().info(getLine());
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    displayYoureWelcome();
  }
}
