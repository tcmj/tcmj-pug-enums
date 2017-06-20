package com.tcmj.pug.enums.example.provider;

import java.io.BufferedReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.Fluent;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.iso.builder.ClassBuilderFactory;
import com.tcmj.iso.exporter.EnumExporterFactory;
import com.tcmj.iso.exporter.impl.ReportingEnumExporter;

/** Json data provider loadad from an url. 
 * TODO: not ready!
 */
public class ContinentJsonProvider implements DataProvider {

  @Override
  public EnumData load() {
    EnumData model = new EnumData();
    try {
      model.setPackageName("com.tcmj.pug.enums.example.provider");
      model.setClassName("Continents");
      
      
      URL continentsURL = ContinentJsonProvider.class.getResource("continents.json");
      Path path = Paths.get(continentsURL.toURI());
      if (!Files.isRegularFile(path) || !Files.exists(path)) {
        throw new IllegalArgumentException("json file not found!");
      }
      JsonParser parser = new JsonParser();

      try (BufferedReader reader = Files.newBufferedReader(path)) {
        JsonArray outerArray = (JsonArray) parser.parse(reader);

        for (JsonElement record : outerArray) {
          System.out.println("JsonRecord: " + record);

          JsonObject jsonObject = record.getAsJsonObject();

          String name = jsonObject.get("name").getAsString();
          System.out.println(name);

          for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            System.out.println("key=" + entry.getKey() + "   value=" + entry.getValue());
          }

        }
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("json datasource errror!", e);
    }
    return model;
  }

  public static void main(String[] args) {
    try {

      EnumExporter exporter = EnumExporterFactory.getReportingEnumExporter();

      Fluent.builder()
          .fromDataSource(new ContinentJsonProvider())
          .usingClassBuilder(ClassBuilderFactory.getJavaPoetEnumBuilder())
          .exportWith(exporter, exporter.createOptions(ReportingEnumExporter.LogLevel.SYSTEM_OUT.name()))
          .end();

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
