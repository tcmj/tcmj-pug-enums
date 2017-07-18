package com.tcmj.pug.enums.mvn;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.NamingStrategy;
import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.api.tools.NamingStrategyFactory;
import com.tcmj.pug.enums.datasources.impl.URLHtmlDataProvider;
import static com.tcmj.pug.enums.mvn.LittleHelper.arrange;
import static com.tcmj.pug.enums.mvn.LittleHelper.getLine;
import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/** Goal which extracts data from a URL (html table). */
@Mojo(name = "generate-enum-html", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenerateEnumHtmlMojo extends GeneralEnumMojo {
  
  /** Css selector to a record (also to a table possible). */
  @Parameter(property = "com.tcmj.pug.enums.cssselector", defaultValue = "table", required = true)
  private String tableCssSelector;
  
  /** Physical position of the column to be used to extract the enum constant names (beginning/defaulting with/to 1). */
  @Parameter(property = "com.tcmj.pug.enums.constantcolumn", defaultValue = "1", required = true)
  private Integer constantColumn;

  /** Optional possibiity to extract further columns and use it as fields in the enum. */
  @Parameter(property = "com.tcmj.pug.enums.subdatacolumns", required = false)
  private Integer[] subDataColumns;

  @Override
  protected void displayYoureWelcome() {  //attach some more logging..
    super.displayYoureWelcome();
    getLog().info(arrange("Extracts EnumData from a table of a html document using a URLXPathHtmlDataProvider!"));
    getLog().info(arrange("CSS Locator used to locate the table: " + this.tableCssSelector));
    getLog().info(arrange("Constant column used in Enum: " + this.constantColumn));
    getLog().info(arrange("SubData columns to include: " + Arrays.toString(this.subDataColumns)));
    getLog().info(getLine());
  }

  
  @Override
  protected DataProvider getDataProvider() {
    if (!StringUtils.equals(this.dataProvider, "com.tcmj.pug.enums.datasources.impl.URLXPathHtmlDataProvider")) {
      throw new UnsupportedOperationException("NotYetImplemented ! Cannot change data provider class to: "+this.dataProvider);
    }
    return new URLHtmlDataProvider(
        this.className,
        this.url,
        this.tableCssSelector, //xpath to a record to further (also to a table possible)
        this.constantColumn, //enum constant column
        this.subDataColumns == null ? null : Stream.of(this.subDataColumns).mapToInt(i -> i).toArray() //convert to int[]
    );
  }

  @Override
  protected NamingStrategy getDefaultNamingStrategyConstantNames() {
    return Fluent.getDefaultNamingStrategyConstantNames();
//    NamingStrategy ns1 = NamingStrategyFactory.extractParenthesis();
//    NamingStrategy ns2 = NamingStrategyFactory.removeProhibitedSpecials();
//    NamingStrategy ns3 = NamingStrategyFactory.camelStrict();
//    NamingStrategy ns4 = NamingStrategyFactory.harmonize();
//    NamingStrategy ns5 = NamingStrategyFactory.upperCase();
//    return ns1.and(ns2).and(ns3).and(ns4).and(ns5);
  }

  @Override
  protected NamingStrategy getDefaultNamingStrategyFieldNames() {
    NamingStrategy ns1 = NamingStrategyFactory.extractParenthesis();
    NamingStrategy ns2 = NamingStrategyFactory.removeProhibitedSpecials();
    NamingStrategy ns3 = NamingStrategyFactory.camelStrict();
    NamingStrategy ns4 = NamingStrategyFactory.harmonize();
    return ns1.and(ns2).and(ns3).and(ns4);
  }
 
}
