package com.tcmj.pug.enums.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import com.tcmj.pug.enums.api.NamingStrategy;

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

  /** holds custom code used to place custom code on the standard getter methods. */
  Map<String, String> mapCustomCode;

  /** holds all possible javadoc content. */
  Map<String, List<String>> mapJavaDoc;

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

  public enum JDocKeys {
    CLASS
  }

  public String getPackageName() {
    return packageName;
  }
 
  public String getClassNameSimple() {
    return className;
  }

  /** Full class name with package separated with dots if a package is provided. eg. 'com.tcmj.PugEnumeration' */
  public String getClassName() {
    if (this.packageName == null || "".equals(this.packageName) || this.packageName.trim().length() < 1) {
      return className;
    } else {
      return this.packageName + "." + this.className;
    }
  }

  public void setClassName(String className) {
    Objects.requireNonNull(className, "Class name may not be null!");
    int posLastDot = className.lastIndexOf('.');
    if(posLastDot >= 0){
       this.packageName = className.substring(0, posLastDot);
       this.className = className.substring(posLastDot+1);
    }else{
      this.className = className;
    }
  }

  public List<String> getImports() {
    return imports;
  }

  public List<NameTypeValue> getData() {
    return data;
  }

  public EnumData addConstant(String constantName, Object... values) {
    if (values == null) {
      getData().add(NameTypeValue.of(constantName));
    } else {
      getData().add(NameTypeValue.of(constantName, values));
    }
    return this;
  }

  public int getEnumConstantsAmount() {
    return getData().size();
  }

  public int getSubFieldsAmount() {
    return getData().iterator().next().getSubFieldsAmount();
  }

  public boolean isEmpty() {
    return getData() != null && getData().isEmpty();
  }

  public boolean isEnumWithSubfields() {
    if (!isEmpty()) {
      return getSubFieldsAmount() > 0;

    } else if (this.fieldNames != null && this.fieldNames.length > 0) {
      return true;
    }
    return false;
  }

  public String[] getFieldNames() {
    return fieldNames;
  }

  /** Get sub field name at given position with allready applied naming strategy. */
  public String getFieldName(int num) {
    if (this.fieldNames == null || this.fieldNames.length == 0) {
      throw new IllegalStateException("No sub field names available!");
    }
    return getNamingStrategyFields().convert(fieldNames[num]);
  }

  public void setFieldNames(String... values) {
    if (this.fieldClasses != null && values != null && values.length != this.fieldClasses.length) {
      throw new IllegalArgumentException("Array size of classes/names is not the same: " + this.fieldClasses.length + "/" + values.length);
    }
    this.fieldNames = values;
  }

  public Class[] getFieldClasses() {
    return fieldClasses;
  }

  public void setFieldClasses(Class... values) {
    if (this.fieldNames != null && values != null && values.length != this.fieldNames.length) {
      throw new IllegalArgumentException("Array size of names/classes is not the same: " + this.fieldNames.length + "/" + values.length);
    }
    this.fieldClasses = values;
  }

  public void addCustomCode(String fieldName, String code) {
    if (Stream.of(getFieldNames()).filter(s -> s.equals(fieldName)).count() != 1) {
      throw new ClassCreationException(
          "Cannot add custom code to a non existing field: " + fieldName);
    }
    if (mapCustomCode == null) {
      mapCustomCode = new HashMap<>();
    }
    this.mapCustomCode.put(fieldName, code);
  }

  public String getCustomCode(String fieldName) {
    if (mapCustomCode == null) {
      return null;
    }
    return mapCustomCode.get(fieldName);
  }

  public Map<String, List<String>> getMapJavaDoc() {
    if (mapJavaDoc == null) {
      mapJavaDoc = new HashMap<>();
    }
    return mapJavaDoc;
  }

  public void addJavaDoc(String key, String javaDoc) {

    List<String> lines = getMapJavaDoc().get(key);
    if (lines == null) {
      lines = new LinkedList<>();
      mapJavaDoc.put(key, lines);
    }
    lines.add(javaDoc);
  }

  public boolean isJavaDoc(String key) {
    List<String> lines = getMapJavaDoc().get(key);
    return !(lines == null || lines.isEmpty());
  }

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
}
