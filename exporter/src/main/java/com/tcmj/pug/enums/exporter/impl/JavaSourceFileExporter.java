package com.tcmj.pug.enums.exporter.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.exporter.tools.MetaDataExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Exports your enum source to a .java file. */
public class JavaSourceFileExporter implements EnumExporter {
  /** slf4j Logging framework. */
  private static final Logger LOG = LoggerFactory.getLogger(JavaSourceFileExporter.class);

  public static final String OPTION_EXPORT_PATH_PREFIX =
      "com.tcmj.iso.exporter.JavaSourceFileExporter.path.prefix";

  @Override
  public String export(String data, Map<String, Object> options) {
    Objects.requireNonNull(
        options, "Your options seems to be null! Please provide a path for the export!");
    String exportPathPrefix = (String) options.get(OPTION_EXPORT_PATH_PREFIX);

    String directories = MetaDataExtractor.getPackageDirectories(data); //eg.: com/tcmj/iso
    String fileName = MetaDataExtractor.getFileNameSingle(data); //eg.: MyEnum.java

    Path exportDir = Paths.get(exportPathPrefix, directories);
    try {
      Path exportPath = Paths.get(exportPathPrefix, directories, fileName);
      LOG.info("Writing Enum to {}", exportPath);

      Files.createDirectories(exportDir);
      Files.write(exportPath, data.getBytes("UTF-8"));
    } catch (IOException e) {
      LOG.error("Cannot write Enum '{}' to '{}'", fileName, exportDir, e);
      throw new JavaFileHasNotBeenCreatedException(e);
    }
    return data;
  }

  @Override
  public Map<String, Object> createOptions(String... exportPath) {
    return createExportPathOptions(exportPath[0]);
  }

  public static Map<String, Object> createExportPathOptions(String stringPath) {
    try {
      return createExportPathOptions(Paths.get(stringPath));
    } catch (Exception e) {
      LOG.error("Cannot set option '{}' to '{}'", OPTION_EXPORT_PATH_PREFIX, stringPath, e);
      throw new JavaFileHasNotBeenCreatedException(e);
    }
  }

  public static Map<String, Object> createExportPathOptions(Path out) {
    try {
      Map<String, Object> options = new HashMap<>();
      options.put(OPTION_EXPORT_PATH_PREFIX, out.toAbsolutePath().toString());
      return options;
    } catch (Exception e) {
      LOG.error("Cannot set option '{}' to '{}'", OPTION_EXPORT_PATH_PREFIX, out, e);
      throw new JavaFileHasNotBeenCreatedException(e);
    }
  }

  public static class JavaFileHasNotBeenCreatedException extends RuntimeException {
    public JavaFileHasNotBeenCreatedException(Throwable cause) {
      super(cause);
    }
  }
}
