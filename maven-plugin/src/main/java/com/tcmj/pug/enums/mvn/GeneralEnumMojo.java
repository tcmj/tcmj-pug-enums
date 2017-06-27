package com.tcmj.pug.enums.mvn;

import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.exporter.impl.JavaSourceFileExporter;
import static com.tcmj.pug.enums.mvn.LittleHelper.arrange;
import static com.tcmj.pug.enums.mvn.LittleHelper.getLine;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/** tcmj pug enums maven plugin base. */
@Mojo(name = "generate-enum", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GeneralEnumMojo extends AbstractMojo {

  @Parameter(
    property = "com.tcmj.pug.enums.classname",
    defaultValue = "com.tcmj.generated.MyEnum",
    required = true
  )
  protected String className;

  @Parameter(
    property = "com.tcmj.pug.enums.sourcedirectory",
    defaultValue = "${project.build.sourceDirectory}",
    required = true
  )
  protected File sourceDirectory;

  @Parameter(property = "com.tcmj.pug.enums.url", required = true)
  protected String url;
  
  @Parameter(property = "com.tcmj.pug.enums.subfieldnames", required = false)
  protected String[] subFieldNames;
  
  /** Print actual configuration settings and version info of the plugin. */
  protected void displayYoureWelcome() {
    getLog().info(getLine());
    getLog().info(arrange("Welcome to the tcmj pug enums maven plugin!"));
    getLog().info(arrange("EnumClassName: " + this.className));
    getLog().info(arrange("SourceOutputDirectory: " + this.sourceDirectory));
    getLog().info(arrange("FetchURL: " + this.url));

    if(this.subFieldNames!=null && this.subFieldNames.length > 0){
      getLog().info(arrange("SubFieldNames (static): " + Arrays.toString(this.subFieldNames)));
    }else{
      getLog().info(arrange("SubFieldNames: <will be computed>"));
    }
        
    Object project = getPluginContext().get("project");
    getLog().info(arrange("org.apache.maven.project.MavenProject: " + project.getClass()));
    Object pluginDescriptor = getPluginContext().get("pluginDescriptor");
    getLog().info(arrange("org.apache.maven.plugin.descriptor.PluginDescriptor: " + pluginDescriptor.getClass()));
    getLog().info(getLine());
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    displayYoureWelcome();
  }

  /** Usually we want always the FileExporter used by the maven plugin. */
  protected EnumExporter getEnumExporter() {
    return new JavaSourceFileExporter();
  }

  protected Map<String, Object> getEnumExporterOptions() {
    Path exportPath = this.sourceDirectory.toPath();
    return JavaSourceFileExporter.createExportPathOptions(exportPath);
  }
}
