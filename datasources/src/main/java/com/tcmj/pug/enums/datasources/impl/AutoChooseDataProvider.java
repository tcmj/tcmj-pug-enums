package com.tcmj.pug.enums.datasources.impl;

import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.model.ClassCreationException;
import com.tcmj.pug.enums.model.EnumData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

/**
 * [W.I.P.] This DataProvider implementation chooses between HTML,JSON and CSV.
 */
public class AutoChooseDataProvider implements DataProvider {

  private static final transient Logger LOG = LoggerFactory.getLogger(AutoChooseDataProvider.class);
  final String url;
  final int columnPosConstant;
  final int[] columnPos;
  final String cssSelector;
  final String mode;

  public AutoChooseDataProvider(String mode, String url, String tableSelector, int columnPosConstant, int[] columnPos) {
    this.mode = mode;
    this.url = Objects.requireNonNull(url, "URL cannot be null!");
    this.columnPosConstant = Objects.requireNonNull(columnPosConstant, "Column pos constant cannot be null!");
    this.columnPos = columnPos == null ? null : Arrays.copyOf(columnPos, columnPos.length);
    if (tableSelector == null) {
      LOG.debug("No CSS selection set! Defaulting to the first '<table>' found!");
      this.cssSelector = "table";
    } else {
      this.cssSelector = tableSelector;
    }
  }

  @Override
  public EnumData load() {
    switch(mode) {
      case "html":
        URLHtmlDataProvider dataProvider = new URLHtmlDataProvider(this.url, this.cssSelector, this.columnPosConstant, this.columnPos);
        return dataProvider.load();
      case "json":
        Path path = Paths.get(URI.create(this.url));
        try (BufferedReader reader = Files.newBufferedReader(path)) {
          String[] fieldNames = new String[]{"areaKM2", "areaPct", "name"};
          Class[] fieldClasses = new Class[]{Integer.class, Float.class, String.class};
          JsonDataProvider jsonProvider = new JsonDataProvider(reader, this.cssSelector, fieldNames, fieldClasses);
          return jsonProvider.load();
        } catch (IOException ex) {
          throw new ClassCreationException(ex);
        }

      default:
        throw new ClassCreationException("Unknown Mode " + mode);
    }
  }

}
