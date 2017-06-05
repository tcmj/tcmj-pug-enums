package com.tcmj.iso.exporter.tools;

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
      if (idxPkg == -1 || idxSemi == -1 || idxPkg > idxSemi || invalid) {
        throw new IllegalStateException("Cannot extract Package!");
      } else {
        return source.substring((idxPkg + 8), idxSemi);
      }
    }
    return null;
  }

  /** Simple java class name. eg.: MyEnum */
  public static String getClassNameSimple(String source) {
    int idxPkg = source.indexOf("enum ");
    if (idxPkg == -1) {
      throw new IllegalStateException("Cannot extract ClassName!");
    } else {
      int idxSemi = source.indexOf("{");
      return source.substring((idxPkg + 5), idxSemi).trim();
    }
  }

  public static String getClassName(String source) {
    String packageName = getPackageName(source);
    return packageName == null
        ? getClassNameSimple(source)
        : packageName + "." + getClassNameSimple(source);
  }

  /** Full java path and file name ending with a '.java' eg.: com/tcmj/iso/MyEnum.java */
  public static String getFileNameFull(String source) {
    return getPackageDirectories(source) + "/" + getFileNameSingle(source);
  }

  /** File name ending with a '.java' eg.: MyEnum.java */
  public static String getFileNameSingle(String source) {
    return getClassNameSimple(source) + ".java";
  }

  /** Package Directories eg.: com/tcmj/iso */
  public static String getPackageDirectories(String source) {
    return getPackageName(source).replace('.', '/').trim();
  }
}
