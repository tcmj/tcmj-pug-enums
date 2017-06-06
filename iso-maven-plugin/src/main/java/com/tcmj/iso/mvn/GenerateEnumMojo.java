package com.tcmj.iso.mvn;

import static com.tcmj.iso.mvn.LittleHelper.arrange;
import static com.tcmj.iso.mvn.LittleHelper.getLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/** tcmj iso generator maven plugin. */
@Mojo(name = "generate-enum", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenerateEnumMojo extends AbstractMojo {
  private final Log log = getLog();

  /**
   * property is only for -D usage. In pom.xml you have to use the fieldname ('className' <- case
   * sensitive) as configuration xml tag!
   */
  @Parameter(
    property = "tcmj.iso.generate.enum.classname",
    defaultValue = "com.tcmj.generated.MyEnum",
    required = true
  )
  private String className;

  /** Print actual configuration settings and version info of the plugin. */
  private void displayYoureWelcome() {
    log.info("PluginContext: " + getPluginContext());
    log.info(getLine());
    log.info(arrange("Welcome to the tcmj iso enum generator maven plugin!"));
    log.info(arrange("ClassName: " + this.className));
    log.info(arrange("PluginContext: " + getPluginContext().size()));
    Object project = getPluginContext().get("project");
    log.info(arrange("Project: " + project.getClass()));
    Object pluginDescriptor = getPluginContext().get("pluginDescriptor");
    log.info(arrange("PluginDescriptor: " + pluginDescriptor.getClass()));
    log.info(getLine());
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    displayYoureWelcome();
  }
}
