package com.tcmj.iso.mvn;

import static com.tcmj.iso.mvn.LittleHelper.arrange;
import static com.tcmj.iso.mvn.LittleHelper.getLine;
import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/** tcmj iso generator maven plugin base. */
@Mojo(name = "generate-enum", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GeneralEnumMojo extends AbstractMojo {
  protected final Log log = getLog();

  @Parameter(
    property = "tcmj.iso.generate.enum.classname",
    defaultValue = "com.tcmj.generated.MyEnum",
    required = true
  )
  protected String className;

  @Parameter(
    property = "tcmj.iso.generate.enum.sourcedirectory",
    defaultValue = "${project.build.sourceDirectory}",
    required = true
  )
  protected File sourceDirectory;

  @Parameter(property = "tcmj.iso.generate.enum.url", required = true)
  protected String url;

  /** Print actual configuration settings and version info of the plugin. */
  protected void displayYoureWelcome() {
    log.info("PluginContext: " + getPluginContext());
    log.info(getLine());
    log.info(arrange("Welcome to the tcmj iso enum generator maven plugin!"));
    log.info(arrange("ClassName: " + this.className));
    log.info(arrange("SourceDirectory: " + this.sourceDirectory));
    log.info(arrange("URL: " + this.url));

    log.info(arrange("PluginContext.size: " + getPluginContext().size()));
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
