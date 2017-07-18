package com.tcmj.pug.enums.example.provider;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.model.EnumData;

import static com.tcmj.pug.enums.api.tools.EnumDataHelper.addConstantValue;

/** Planet earths continents.  */
public class ContinentDataProvider implements DataProvider {

  @Override
  public EnumData load() {
    EnumData model = new EnumData();
    model.setPackageName("com.tcmj.iso.world");
    model.setClassName("Continent");

    model.addJavaDoc(EnumData.JDocKeys.CLASS.name(), "7 Continents of the World.");
    model.addJavaDoc(EnumData.JDocKeys.CLASS.name(), "https://en.wikipedia.org/wiki/Continent");
    model.addJavaDoc(EnumData.JDocKeys.CLASS.name(), "Last-Updated: 2017-05-03");

    //        model.setFieldNames("nameUS", "nameDE", "FlaecheInKM2", "FlaecheInProzent", "HoechsterPunktInMeter", "NiedrigsterPunktInMeter");
    model.setFieldNames(
        "nameUS", "nameDE", "areaKM2", "areaPct", "elevationHighest", "elevationLowest");
    model.setFieldClasses(
        String.class, String.class, Integer.class, Float.class, Integer.class, Integer.class);
    
  
//    addConstantValue(model, "AF", "Africa", "Afrika", 30_370_000, Float.valueOf( "20.4f"), 5_895, -155);
    addConstantValue(model, "AF", "Africa", "Afrika", 30_370_000, 20.4F, 5_895, -155);
    addConstantValue(model, "AN", "Antarctica", "Antarktis", 13_720_000, 9.2F, 4_892, -50);
    addConstantValue(model, "AS", "Asia", "Asien", 43_820_000, 29.5F, 8_848, -427);
    addConstantValue(model, "AU", "Australia", "Australien", 9_008_500, 5.9F, 4_884, -15);
    addConstantValue(model, "EU", "Europe", "Europa", 10_180_000, 6.8F, 5_642, -28);
    addConstantValue(model, "NA", "North America", "Nord Amerika", 24_490_000, 16.5F, 6_198, -86);
    addConstantValue(model, "SA", "South America", "Süd Amerika", 17_840_000, 12.0F, 6_960, -105);
    addConstantValue(model, "OC", "Oceania", "Ozeanien", 1_260_000, 0F, 0, 0);

    model.addJavaDoc("nameUS", "Name in english");

    return model;
  }
}
