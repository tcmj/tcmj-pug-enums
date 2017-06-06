package com.tcmj.iso.mvn;

import static com.tcmj.iso.mvn.LittleHelper.arrange;
import static com.tcmj.iso.mvn.LittleHelper.getLine;
import java.util.Arrays;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/** Goal which extracts data from a URL (html table). */
@Mojo(name = "generate-enum-html", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenerateEnumHtmlMojo extends GeneralEnumMojo {

  @Parameter(
    property = "tcmj.iso.generate.enum.dataprovider",
    defaultValue = "com.tcmj.iso.datasources.impl.URLXPathHtmlDataProvider",
    required = true
  )
  private String dataProvider;

  @Parameter(
    property = "tcmj.iso.generate.enum.tablexpath",
    defaultValue = "table", //xpath to a record (also to a table possible),
    required = true
  )
  private String tableXpath;

  @Parameter(
    property = "tcmj.iso.generate.enum.constantcolumn",
    defaultValue = "1",
    required = true
  )
  private Integer constantColumn;

  @Parameter(property = "tcmj.iso.generate.enum.subdatacolumns")
  private Integer[] subDataColumns;

  /** Print actual configuration settings and version info of the plugin. */
  @Override
  protected void displayYoureWelcome() {
    super.displayYoureWelcome();
    log.info(arrange("Extracts EnumData from a table of a html document (URL)!"));
    log.info(arrange("DataProvider: URLXPathHtmlDataProvider"));
    log.info(arrange("TableXpath: " + this.tableXpath));
    log.info(arrange("ConstantColumn: " + this.constantColumn));
    if(this.subDataColumns==null){
      log.info(arrange("SubDataColumns: " + this.subDataColumns));
    }else{
      log.info(arrange("SubDataColumns: " + Arrays.toString(this.subDataColumns)));
    }
    log.info(getLine());
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    displayYoureWelcome();
  }
}
