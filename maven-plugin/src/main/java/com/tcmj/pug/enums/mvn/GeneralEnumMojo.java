package com.tcmj.pug.enums.mvn;

import com.tcmj.pug.enums.api.ClassBuilder;
import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.EnumResult;
import com.tcmj.pug.enums.api.NamingStrategy;
import com.tcmj.pug.enums.api.SourceFormatter;
import com.tcmj.pug.enums.builder.ClassBuilderFactory;
import com.tcmj.pug.enums.builder.SourceFormatterFactory;
import com.tcmj.pug.enums.exporter.impl.JavaSourceFileExporter;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.model.NameTypeValue;
import static com.tcmj.pug.enums.mvn.LittleHelper.arrange;
import static com.tcmj.pug.enums.mvn.LittleHelper.encloseJavaDoc;
import static com.tcmj.pug.enums.mvn.LittleHelper.getLine;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * <p>
 * Abstract tcmj pug enums superclass for Mojos generating Java source enum classes from datasources.</p>
 */
@Mojo(name = "generate-enum", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public abstract class GeneralEnumMojo extends AbstractMojo {

  /** Mandatory Property which data provider should be used. This class name should be created in the {@link #getDataProvider()} method. */
  @Parameter(property = "com.tcmj.pug.enums.dataprovider", defaultValue = "com.tcmj.pug.enums.datasources.impl.URLXPathHtmlDataProvider")
  protected String dataProvider;

  /** Mandatory Property which defines the full class name (including packages). It defaults to 'com.tcmj.generated.MyEnum'. */
  @Parameter(property = "com.tcmj.pug.enums.classname", defaultValue = "com.tcmj.generated.MyEnum", required = true)
  protected String className;

  /** Mandatory Property which defines the output path to save the generated enum java files. It defaults to mavens 'project.build.sourceDirectory'. */
  @Parameter(property = "com.tcmj.pug.enums.sourcedirectory", defaultValue = "${project.build.sourceDirectory}", required = true)
  protected File sourceDirectory;

  /** Mandatory Property which defines the location (url) where to load the input data. */
  @Parameter(property = "com.tcmj.pug.enums.url", required = true)
  protected String url;

  /** Optional Property to override the column names used for the sub fields in the java enum class. */
  @Parameter(property = "com.tcmj.pug.enums.subfieldnames", required = false)
  protected String[] subFieldNames;

  /** Optional Property to write some static javadoc lines to our java enum. */
  @Parameter(property = "com.tcmj.pug.enums.classjavadoc", required = false)
  protected String[] javadocClassLevel;

  /** Subclasses have to provide the DataProvider to be used. */
  protected abstract DataProvider getDataProvider();

  /** Subclasses have to provide the naming conversion strategy used to change constant names. */
  protected abstract NamingStrategy getDefaultNamingStrategyConstantNames();

  /** Subclasses have to provide the naming conversion strategy used to change field names. */
  protected abstract NamingStrategy getDefaultNamingStrategyFieldNames();

  /** Define which ClassBuilder implementation we want to use. */
  protected ClassBuilder getClassBuilder() {
    return ClassBuilderFactory.getBestEnumBuilder(); //..the best we can get
  }

  /** Define which Source Formatter implementation we want to use. */
  protected SourceFormatter getSourceFormatter() {
    return SourceFormatterFactory.getBestSourceCodeFormatter(); //..the best we can get
  }

  /** Print actual configuration settings and version info of the plugin. */
  protected void displayYoureWelcome() {
    getLog().info(getLine());
    getLog().info(arrange("Welcome to the tcmj pug enums maven plugin!"));
    getLog().info(getLine());
    getLog().info(arrange("EnumClassName: " + this.className));
    getLog().info(arrange("SourceOutputDirectory: " + this.sourceDirectory));
    getLog().info(arrange("FetchURL: " + this.url));

    if (isParameterSet(this.subFieldNames)) {
      getLog().info(arrange("SubFieldNames fixed to: " + Arrays.toString(this.subFieldNames)));
    } else {
      getLog().info(arrange("SubFieldNames: <will be computed>"));
    }

    if (isParameterSet(this.javadocClassLevel)) {
      Stream.of(this.javadocClassLevel).map((v) -> arrange("JavaDocClassLevel: " + v)).forEach((t) -> getLog().info(t));
    } else {
      getLog().info(arrange("JavaDocClassLevel: <will be computed>"));
    }

//    Object project = getPluginContext().get("project");
//    getLog().info(arrange("org.apache.maven.project.MavenProject: " + project.getClass()));
//    Object pluginDescriptor = getPluginContext().get("pluginDescriptor");
//    getLog().info(arrange("org.apache.maven.plugin.descriptor.PluginDescriptor: " + pluginDescriptor.getClass()));
//    getLog().info(getLine());
  }

  protected static boolean isParameterSet(String[] param) {
    return param != null && param.length > 0;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      displayYoureWelcome();

      final DataProvider myDataProvider = Objects.requireNonNull(getDataProvider(), "getDataProvider() delivers a NULL DataProvider object!");
      getLog().info(arrange("DataProvider: " + myDataProvider));

      final ClassBuilder myClassBuilder = Objects.requireNonNull(getClassBuilder(), "getClassBuilder() delivers a NULL ClassBuilder object!");
      getLog().info(arrange("ClassBuilder: " + myClassBuilder));

      final SourceFormatter mySourceFormatter = Objects.requireNonNull(getSourceFormatter(), "getSourceFormatter() delivers a NULL SourceFormatter object!");
      getLog().info(arrange("SourceFormatter: " + mySourceFormatter));

      final EnumExporter myEnumExporter = Objects.requireNonNull(getEnumExporter(), "getEnumExporter() delivers a NULL EnumExporter object!");

      final EnumData data = Objects.requireNonNull(myDataProvider.load(), "DataProvider.load() returns a NULL EnumData object!");
      Objects.requireNonNull(data.getData(), "EnumData has no records loaded! It's empty!");
      data.setClassName(className);

      if (isParameterSet(this.subFieldNames)) {
        //Overriding the field names usually fetched by the data provider implementation!
        data.setFieldNames(this.subFieldNames);
      }

      data.setNamingStrategyConstants(getDefaultNamingStrategyConstantNames());
      data.setNamingStrategyFields(getDefaultNamingStrategyFieldNames());

      if (isParameterSet(this.javadocClassLevel)) {
        Stream.of(this.javadocClassLevel).map((v) -> encloseJavaDoc(v)).forEach(text -> data.addJavaDoc(EnumData.JDocKeys.CLASS.name(), text));
      } else {
        data.addJavaDoc(EnumData.JDocKeys.CLASS.name(), encloseJavaDoc("Data has been fetched from '" + this.url + "'."));
      }

      myClassBuilder.importData(data);

      String myEnum = myClassBuilder.build();

      EnumResult eResult = EnumResult.of(data, mySourceFormatter, myEnum);

      //add option for JavaSourceFileExporter: as global option
      eResult.addOption(JavaSourceFileExporter.OPTION_EXPORT_PATH_PREFIX, sourceDirectory);

      myEnumExporter.export(eResult);

      getLog().info(String.format("Enum successfully created with %s characters!", myEnum.length()));

    } catch (Exception e) {
      getLog().error("Cannot create your enum: " + className + "!", e);
      throw new MojoExecutionException("ExecutionFailure!", e);
    }
  }

  /** Usually we want always the FileExporter to save the enum into file system. */
  protected EnumExporter getEnumExporter() {
    return new JavaSourceFileExporter();
  }
}
