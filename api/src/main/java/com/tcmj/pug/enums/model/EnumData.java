package com.tcmj.pug.enums.model;

import com.tcmj.pug.enums.api.NamingStrategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/** Model class which holds all data used to produce a java enum class. */
public class EnumData {
  private static final String LINE = System.getProperty("line.separator");

  private String packageName;
  private String className;
  private NamingStrategy namingStrategyConstants = NamingStrategy.getDefault();
  private NamingStrategy namingStrategyFields = NamingStrategy.getDefault();
  private final List<String> imports = new LinkedList<>();
  private String[] fieldNames;
  private Class[] fieldClasses;
  private final List<NameTypeValue> data = new LinkedList<>();

  /** Holds custom code used to place custom code on the standard getter methods. */
  private Map<String, String> mapCustomCode;

  /** Holds all possible javadoc content. */
  private Map<String, List<String>> mapJavaDoc;

  /** NamingStrategy used to convert the constant values of the enum. */
  public NamingStrategy getNamingStrategyConstants() {
    return namingStrategyConstants;
  }

  /** NamingStrategy used to convert the constant values of the enum. */
  public void setNamingStrategyConstants(NamingStrategy namingStrategy) {
    this.namingStrategyConstants = namingStrategy;
  }

  /** NamingStrategy used to convert the field variables of the enum. */
  public NamingStrategy getNamingStrategyFields() {
    return namingStrategyFields;
  }

  /** NamingStrategy used to convert the field variables of the enum. */
  public void setNamingStrategyFields(NamingStrategy namingStrategy) {
    this.namingStrategyFields = namingStrategy;
  }

  /** Java package name separated by dots. */
  public String getPackageName() {
    return packageName;
  }

  /** Java class name without package info. */
  public String getClassNameSimple() {
    return className;
  }

  /**
   * Set name of this enum. Can handle simple name and names with package information.
   *
   * @param className value with or without dots
   */
  public void setClassName(String className) {
    Objects.requireNonNull(className, "Class name may not be null!");
    int posLastDot = className.lastIndexOf('.');
    if (posLastDot >= 0) {
      this.packageName = className.substring(0, posLastDot);
      this.className = className.substring(posLastDot + 1);
    } else {
      this.className = className;
    }
  }

  /** Full class name with package separated with dots if a package is provided. eg. 'com.tcmj.PugEnumeration' */
  public String getClassName() {
    if (this.packageName == null || "".equals(this.packageName) || this.packageName.trim().length() < 1) {
      return className;
    } else {
      return this.packageName + "." + this.className;
    }
  }

  /** Retrieve the current set import statements. */
  public List<String> getImports() {
    return imports;
  }

  /** Retrieve the current set enum data. */
  public List<NameTypeValue> getData() {
    return data;
  }

  /** Add a single enum data. */
  public void addConstant(String constantName, Object... values) {
    if (values == null) {
      getData().add(NameTypeValue.of(constantName));
    } else {
      getData().add(NameTypeValue.of(constantName, values));
    }
  }

  /** Retrieve the amount of current enum values. */
  public int getEnumConstantsAmount() {
    return getData().size();
  }

  /** Retrieve the amount of subfield values. */
  public int getSubFieldsAmount() {
    return getData().iterator().next().getSubFieldsAmount();
  }

  /** Check for emptiness. */
  public boolean isEmpty() {
    return getData() != null && getData().isEmpty();
  }

  /** Check for subfields. */
  public boolean isEnumWithSubfields() {
    if (!isEmpty()) {
      return getSubFieldsAmount() > 0;
    } else return this.fieldNames != null && this.fieldNames.length > 0;
  }

  /** Retrieve the current field names. */
  public String[] getFieldNames() {
    return fieldNames == null ? null : Arrays.copyOf(fieldNames, fieldNames.length);
  }

  /** Set one or more field names. */
  public void setFieldNames(String... values) {
    if (this.fieldClasses != null && values != null && values.length != this.fieldClasses.length) {
      throw new IllegalArgumentException("Array size of classes/names is not the same: " + this.fieldClasses.length + "/" + values.length);
    }
    this.fieldNames = values;
  }

  /** Get sub field name at given position with already applied naming strategy. */
  public String getFieldName(int num) {
    if (this.fieldNames == null || this.fieldNames.length == 0) {
      throw new IllegalStateException("No sub field names available!");
    }
    return getNamingStrategyFields().convert(fieldNames[num]);
  }

  /** Retrieve the classes of the enum fields. */
  public Class[] getFieldClasses() {
    return fieldClasses == null ? null : Arrays.copyOf(fieldClasses, fieldClasses.length);
  }

  /** Set one or more field classes/types. */
  public void setFieldClasses(Class... values) {
    if (this.fieldNames != null && values != null && values.length != this.fieldNames.length) {
      throw new IllegalArgumentException("Array size of names/classes is not the same: " + this.fieldNames.length + "/" + values.length);
    }
    this.fieldClasses = values;
  }

  /** Retrieve a class of a enum field at a specific position. */
  public Class<?> getFieldClass(int no) {
    if (this.fieldClasses == null || this.fieldClasses.length == 0) {
      throw new IllegalStateException("No fieldClasses available! Therefore no value set on position " + no);
    }
    if (no < 0 || no > this.fieldClasses.length) {
      throw new IllegalStateException("Index out of range! No fieldClasses value on position " + no);
    }
    return fieldClasses[no];
  }

  /** Add custom code of a enum field. */
  public void addCustomCode(String fieldName, String code) {
    if (Stream.of(this.fieldNames).filter(s -> s.equals(fieldName)).count() != 1) {
      throw new ClassCreationException(
        "Cannot add custom code to a non existing field: " + fieldName);
    }
    if (mapCustomCode == null) {
      mapCustomCode = new HashMap<>();
    }
    this.mapCustomCode.put(fieldName, code);
  }

  /** Get custom code of a enum field. */
  public String getCustomCode(String fieldName) {
    if (mapCustomCode == null) {
      return null;
    }
    return mapCustomCode.get(fieldName);
  }

  /** Get java doc map holding all docx. */
  public Map<String, List<String>> getMapJavaDoc() {
    if (mapJavaDoc == null) {
      mapJavaDoc = new HashMap<>();
    }
    return mapJavaDoc;
  }

  /** Add java doc. */
  public void addJavaDoc(String key, String javaDoc) {

    List<String> lines = getMapJavaDoc().get(key);
    if (lines == null) {
      lines = new LinkedList<>();
      mapJavaDoc.put(key, lines);
    }
    lines.add(javaDoc);
  }

  /** Check for java doc. */
  public boolean isJavaDoc(String key) {
    List<String> lines = getMapJavaDoc().get(key);
    return !(lines == null || lines.isEmpty());
  }

  /** Get a java doc. */
  public String getJavaDoc(String key) {
    List<String> lines = getJavaDocLines(key);
    if (lines != null && !lines.isEmpty()) {
      StringBuilder buffer = new StringBuilder();
      for (String line : lines) {
        buffer.append(line);
        buffer.append("<br/>");
        buffer.append(LINE);
      }
      return buffer.toString();
    }
    return "";
  }

  /** Get a java doc lines. */
  public List<String> getJavaDocLines(String key) {
    if (mapJavaDoc == null) {
      return null;
    }
    List<String> lines = getMapJavaDoc().get(key);
    if (lines == null) {
      return null;
    }
    return lines;
  }

  /** Javadoc key enum. */
  public enum JDocKeys {
    CLASS
  }
}
