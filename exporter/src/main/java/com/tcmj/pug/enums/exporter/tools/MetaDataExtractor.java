package com.tcmj.pug.enums.exporter.tools;

import com.tcmj.pug.enums.api.EnumResult;
import com.tcmj.pug.enums.model.EnumData;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Extracts some data from the enum source needed by some exporter. */
public class MetaDataExtractor {

  /**
   * Remove all real javadoc comments (/**).
   */
  public static String removeJavadocs(String enumSource) {
    return enumSource.replaceAll("/\\*\\*(?s:(?!\\*/).)*\\*/", "");
  }

  /**
   * Remove all slash-star-comments (/*).
   */
  public static String removeDocs(String enumSource) {
    return enumSource.replaceAll("/\\*(?s:(?!\\*/).)*\\*/", "");
  }

  /**
   * Extract the java package name from a java source class.
   */
  public static String getPackageName(String source) {
    Objects.requireNonNull(source, "Cannot get a package from a null input!");
    String harmonized = removeDocs(removeJavadocs(source));
    return Stream.of(harmonized.split(System.lineSeparator()))
      .filter(line -> line.trim().startsWith("package"))
      .map(pckg -> {
        String tmp = pckg.trim().substring("package ".length()).trim();
        return tmp.substring(0, tmp.indexOf(";")).trim();
      }).collect(Collectors.joining());
  }

  /** Simple java class name. eg.: MyEnum */
  public static String getClassNameSimple(String source) {
    //@todo fix this mess !!!
    String a = getClassNameSimple0(source);
    if (a.contains(" ")) {
      a = getClassNameSimple1(source);
    }
    return a.trim();
  }

  private static String getClassNameSimple0(String source) {
    int idxPkg = source.indexOf("enum ");
    if (idxPkg == -1) {
      throw new IllegalStateException("Cannot extract ClassName!");
    } else {
      int idxSemi = source.indexOf('{');
      return source.substring((idxPkg + 5), idxSemi).trim();
    }
  }

  private static String getClassNameSimple1(String source) {
    int idxPkg = source.indexOf("public enum ");
    if (idxPkg == -1) {
      throw new IllegalStateException("Cannot extract ClassName!");
    } else {
      int idxSemi = source.indexOf('{');
      return source.substring((idxPkg + 7 + 5), idxSemi).trim();
    }
  }

  /**
   * Java styled package and class name separated with dots.
   */
  public static String getClassName(EnumResult enumResult) {
    EnumData enumData = Objects.requireNonNull(enumResult.getData(), "Cannot get EnumData object from EnumResult!");
    return enumData.getClassName();
  }

  public static String getClassName(String source) {
    String packageName = getPackageName(source);
    return packageName == null ? getClassNameSimple(source) : packageName + "." + getClassNameSimple(source);
  }

  /** Full java path and file name ending with a '.java' eg.: com/tcmj/iso/MyEnum.java */
  public static String getFileNameFull(String source) {
    return getPackageDirectories(source) + "/" + getFileNameSingle(source);
  }

  public static String getFileNameSingle(String source) {
    return getClassNameSimple(source).concat(".java");
  }

  /** File name ending with a '.java' eg.: MyEnum.java */
  public static String getFileNameSingle(EnumResult enumResult) {
    EnumData enumData = Objects.requireNonNull(enumResult.getData(), "Cannot get EnumData object from EnumResult!");
    String javaClassNameSimple = Objects.requireNonNull(enumData.getClassNameSimple(), "EnumData.getPackageName() of EnumResult!");
    return javaClassNameSimple.concat(".java");
  }

  public static String getPackageDirectories(String source) {
    String packageName = getPackageName(source);
    return getPackageDirectoriesIntern(packageName);
  }

  private static String getPackageDirectoriesIntern(String packageName) {
    return packageName == null ? null : packageName.replace('.', '/').trim();
  }

  /** Package Directories eg.: com/tcmj/iso */
  public static String getPackageDirectories(EnumResult enumResult) {
    EnumData enumData = Objects.requireNonNull(enumResult.getData(), "Cannot get EnumData object from EnumResult!");
    String javaPackageName = Objects.requireNonNull(enumData.getPackageName(), "EnumData.getPackageName() of EnumResult!");
    return getPackageDirectoriesIntern(javaPackageName);
  }
}
