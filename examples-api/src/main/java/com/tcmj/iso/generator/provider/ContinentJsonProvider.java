package com.tcmj.iso.generator.provider;

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
import com.tcmj.iso.api.DataProvider;
import com.tcmj.iso.api.EnumExporter;
import com.tcmj.iso.api.model.EnumData;
import com.tcmj.iso.builder.ClassBuilderFactory;
import com.tcmj.iso.exporter.EnumExporterFactory;
import com.tcmj.iso.exporter.impl.InMemoryCompilingExporter;
import com.tcmj.iso.exporter.impl.ReportingEnumExporter;
import com.tcmj.iso.generator.Fluent;

/** pugproductions - 2017-05-09 - tcmj. */
public class ContinentJsonProvider implements DataProvider {

  @Override
  public EnumData load() {

    try {
      URL continentsURL = ContinentJsonProvider.class.getResource("continents.json");
      Path path = Paths.get(continentsURL.toURI());
      JsonParser parser = new JsonParser();

      try (BufferedReader reader = Files.newBufferedReader(path)) {
        JsonArray outerArray = (JsonArray) parser.parse(reader);
        //                LOG.trace("Outter array structure successfully found and parsed!");

        for (JsonElement record : outerArray) {
          System.out.println("JsonRecord: " + record);

          JsonObject jsonObject = record.getAsJsonObject();

          String name = jsonObject.get("name").getAsString();
          System.out.println(name);

          for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            System.out.println("key=" + entry.getKey() + "   value=" + entry.getValue());
          }
          //                        .getAsJsonArray().get(0).getAsJsonObject()
          //                        .get("values").getAsJsonArray().get(0).getAsJsonObject()
          //                        .get("value").getAsString();

        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    DataProvider dataProvider = new ContinentJsonProvider();
    try {
      dataProvider.load();

      //            if (true) System.exit(0);

      EnumExporter exporterB = EnumExporterFactory.getReportingEnumExporter();
      EnumExporter exporterA = EnumExporterFactory.getInMemoryCompilingExporter();
      EnumExporter exporter =
          exporterA.and(
              exporterB, exporterB.createOptions(ReportingEnumExporter.LogLevel.SYSTEM_OUT.name()));

      Fluent.builder()
          .fromDataSource(dataProvider)
          .usingClassBuilder(ClassBuilderFactory.getJavaPoetEnumBuilder())
          .exportWith(exporter)
          .end();

      System.out.println(((InMemoryCompilingExporter) exporterA).getEnumConstants());

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
