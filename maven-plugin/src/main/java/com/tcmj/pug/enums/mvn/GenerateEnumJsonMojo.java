package com.tcmj.pug.enums.mvn;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.NamingStrategy;
import static com.tcmj.pug.enums.mvn.LittleHelper.arrange;
import static com.tcmj.pug.enums.mvn.LittleHelper.getLine;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/** Goal which extracts data from a Json source (URL or file). */
@Mojo(name = "generate-enum-json", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenerateEnumJsonMojo extends GeneralEnumMojo {

  @Override
  protected void displayYoureWelcome() {
    super.displayYoureWelcome();
    getLog().info(arrange("Extracts EnumData from a JSON document!"));
    getLog().info(arrange("DataProvider: todo"));
    getLog().info(getLine());
  }

  @Override
  protected DataProvider getDataProvider() {
    throw new UnsupportedOperationException("Not supported yet."); 
  }

  @Override
  protected NamingStrategy getDefaultNamingStrategyConstantNames() {
    throw new UnsupportedOperationException("Not supported yet."); 
  }

  @Override
  protected NamingStrategy getDefaultNamingStrategyFieldNames() {
    throw new UnsupportedOperationException("Not supported yet."); 
  }
}
