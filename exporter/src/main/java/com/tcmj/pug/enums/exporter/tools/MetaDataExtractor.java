package com.tcmj.pug.enums.exporter.tools;

import com.tcmj.pug.enums.api.EnumResult;
import com.tcmj.pug.enums.model.EnumData;

import java.util.Objects;

/** Extracts some data from the enum source needed by some exporter. */
public class MetaDataExtractor {

  /**
   * Extract java package name.
   *
   * @todo use regex
   */
  public static String getPackageName(String source) {
    int idxPkg = source.indexOf("package");
    int idxSemi = source.indexOf(";");
    if (idxPkg >= 0) {
      boolean invalid = !source.substring(0, idxPkg).trim().equals("");
      if (idxSemi == -1 || idxPkg > idxSemi || invalid) {
        throw new IllegalStateException("Cannot extract Package!");
      } else {
        return source.substring((idxPkg + 8), idxSemi);
      }
    }
    return null;
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
      int idxSemi = source.indexOf("{");
      return source.substring((idxPkg + 5), idxSemi).trim();
    }
  }

  private static String getClassNameSimple1(String source) {
    int idxPkg = source.indexOf("public enum ");
    if (idxPkg == -1) {
      throw new IllegalStateException("Cannot extract ClassName!");
    } else {
      int idxSemi = source.indexOf("{");
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
    return getPackageName(source).replace('.', '/').trim();
  }

  /** Package Directories eg.: com/tcmj/iso */
  public static String getPackageDirectories(EnumResult enumResult) {
    EnumData enumData = Objects.requireNonNull(enumResult.getData(), "Cannot get EnumData object from EnumResult!");
    String javaPackageName = Objects.requireNonNull(enumData.getPackageName(), "EnumData.getPackageName() of EnumResult!");
    return javaPackageName.replace('.', '/').trim();
  }
}
