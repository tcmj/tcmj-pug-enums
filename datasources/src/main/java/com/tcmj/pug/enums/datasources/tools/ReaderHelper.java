package com.tcmj.pug.enums.datasources.tools;

import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Methods used to load files. */
public class ReaderHelper {
  public static Reader getResource(Class reference, String filename) {
    try {
      URL url = reference.getResource(filename);
      Path path = Paths.get(url.toURI());
      return Files.newBufferedReader(path);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }
}
