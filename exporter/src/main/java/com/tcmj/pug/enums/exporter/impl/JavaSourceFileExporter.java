package com.tcmj.pug.enums.exporter.impl;

import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.EnumResult;
import com.tcmj.pug.enums.exporter.tools.MetaDataExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Exports your enum source to a .java file. */
public class JavaSourceFileExporter implements EnumExporter {
  /** slf4j Logging framework. */
  private static final Logger LOG = LoggerFactory.getLogger(JavaSourceFileExporter.class);

  public static final String OPTION_EXPORT_PATH_PREFIX = "com.tcmj.iso.exporter.JavaSourceFileExporter.path.prefix";
  public static final String OPTION_EXPORT_ENCODING = "com.tcmj.iso.exporter.JavaSourceFileExporter.encoding";
  public static final String OPTION_RESULT_PATH = "com.tcmj.iso.exporter.JavaSourceFileExporter.sourcefile";

  /** Directory to store the enum. Of course the package structure will also be built in this folder. */
  private String baseDirectory;

  public JavaSourceFileExporter() {
  }

  public JavaSourceFileExporter(String baseDir) {
    this.baseDirectory = baseDir;
  }

  @Override
  public EnumResult export(EnumResult enumResult) {
    Objects.requireNonNull(enumResult, "Your EnumResult seems to be null! Please provide such a object!");
    String directories = null, fileName = null;
    try {

      LOG.debug("JavaSourceFileExporter.export({})...", enumResult);
      EnumResult localEnumResult = Objects.requireNonNull(enumResult, "Parameter EnumResult may not be null!");
      String data = Objects.requireNonNull(enumResult.getResultFormatted(), "EnumResult.getResultFormatted() is null!");

      if (localEnumResult.getData() == null) {
        LOG.trace("Getting directories and file name from enum content...");
        directories = MetaDataExtractor.getPackageDirectories(data); //eg.: com/tcmj/iso
        fileName = MetaDataExtractor.getFileNameSingle(data); //eg.: MyEnum.java
      } else {
        LOG.trace("Getting directories and file name from EnumData object...");
        directories = MetaDataExtractor.getPackageDirectories(enumResult); //eg.: com/tcmj/iso
        fileName = MetaDataExtractor.getFileNameSingle(enumResult); //eg.: MyEnum.java
      }

      String exportPathPrefix;
      if (this.baseDirectory == null) {
        Object someWhatFile = enumResult.getOption(OPTION_EXPORT_PATH_PREFIX);
        if (someWhatFile == null) {
          exportPathPrefix = ".";
        } else if (someWhatFile instanceof java.io.File) {
          exportPathPrefix = ((java.io.File) someWhatFile).getPath();
        } else if (someWhatFile instanceof Path) {
          exportPathPrefix = someWhatFile.toString();
        } else {
          exportPathPrefix = String.valueOf(someWhatFile);
        }
      } else {
        exportPathPrefix = this.baseDirectory;
      }
      LOG.debug("Exporting file={} to package-directories={}, using output-directory={}", fileName, directories, exportPathPrefix);

      Path exportDir = Paths.get(exportPathPrefix, directories);
      Path exportPath = Paths.get(exportPathPrefix, directories, fileName);

      if (Files.exists(exportPath)) {
        LOG.warn("File: {}", exportPath);
        LOG.warn("...already exists and will not be touched! Consider using maven-clean plugin!");
      } else {
        LOG.info("Writing : {}", exportPath);
        Files.createDirectories(exportDir);
        String finalContent = Objects.requireNonNull(enumResult.getResultFormatted(), "EnumResult.ResultFormatted");
        Files.write(exportPath, finalContent.getBytes(getEncoding(enumResult)));
        appendResultFile(exportPath, enumResult);
      }
    } catch (Exception e) {
      LOG.error("Cannot write Enum '{}' to '{}'", fileName, directories, e);
      throw new JavaFileHasNotBeenCreatedException(e);
    }
    return enumResult;
  }

  private void appendResultFile(Path path, EnumResult enumResult) {
    Path absolutePath = path.toAbsolutePath();
    if (Files.isRegularFile(absolutePath)) {
      enumResult.addOption(JavaSourceFileExporter.OPTION_RESULT_PATH, absolutePath);
    }
  }

  private Charset getEncoding(EnumResult enumResult) {
    Object someEncoding = enumResult.getOption(OPTION_EXPORT_ENCODING);
    Charset charset;
    try {
      String charsetName = (String) someEncoding;
      charset = Charset.forName(charsetName);
      LOG.debug("Using charset: '{}' to export our enum", charset);
    } catch (Exception e) {
      charset = Charset.defaultCharset();
      LOG.error("Cannot determine output charset from {}! Fallback to the default charset: {}, ErrMsg={}", someEncoding, charset, e.getMessage());
    }
    return charset;
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
