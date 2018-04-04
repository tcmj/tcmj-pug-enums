package com.tcmj.pug.enums.example.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.fluent.Fluent;
import com.tcmj.pug.enums.builder.ClassBuilderFactory;
import com.tcmj.pug.enums.exporter.EnumExporterFactory;
import com.tcmj.pug.enums.model.EnumData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Json data provider loadad from an url.
 * TODO: not ready!
 */
public class ContinentJsonProvider implements DataProvider {
  private static final transient Logger LOG = LoggerFactory.getLogger(ContinentJsonProvider.class);

  public static void main(String[] args) {
    try {

      EnumExporter exporter = EnumExporterFactory.getReportingEnumExporter();

      Fluent.builder()
        .dataProvider(new ContinentJsonProvider())
        .classBuilder(ClassBuilderFactory.getJavaPoetEnumBuilder())
        .enumExporter(exporter)
        .build();

    } catch (Exception ex) {
      LOG.error("Whoop", ex);
    }
  }

  @Override
  public EnumData load() {
    EnumData model = new EnumData();
    try {
      model.setClassName("com.tcmj.pug.enums.example.provider.Continents");

      URL continentsURL = ContinentJsonProvider.class.getResource("continents.json");
      Path path = Paths.get(continentsURL.toURI());
      if (!Files.isRegularFile(path) || !Files.exists(path)) {
        throw new IllegalArgumentException("json file not found!");
      }
      JsonParser parser = new JsonParser();

      try (BufferedReader reader = Files.newBufferedReader(path)) {
        JsonArray outerArray = (JsonArray) parser.parse(reader);

        for (JsonElement record : outerArray) {
          LOG.info("JsonRecord: {}", record);

          JsonObject jsonObject = record.getAsJsonObject();

          String name = jsonObject.get("name").getAsString();
          LOG.info(name);

          for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            LOG.info("key={}, value={}", entry.getKey(), entry.getValue());
          }

        }
      }
    } catch (URISyntaxException urie) {
      throw new IllegalArgumentException("Invalid URL for json datasource!", urie);
    } catch (IOException ioe) {
      throw new IllegalArgumentException("Json File read problem!", ioe);
    }
    return model;
  }
}
