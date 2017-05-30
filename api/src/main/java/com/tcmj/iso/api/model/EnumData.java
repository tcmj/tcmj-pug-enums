package com.tcmj.iso.api.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import com.tcmj.iso.api.NamingStrategy;

/**
 * Model class which holds all data used to produce a java enum class.
 */
public class EnumData {
    private static final String LINE = System.getProperty("line.separator");

    private String packageName;
    private String className;
    private NamingStrategy namingStrategy = value -> value;
    private List<String> imports = new LinkedList<>();
    private String[] fieldNames;
    private Class[] fieldClasses;
    private Map<String, NameTypeValue> data = new LinkedHashMap<>();

    /** holds custom code used to place custom code on the standard getter methods. */
    Map<String, String> mapCustomCode;

    /** holds all possible javadoc content. */
    Map<String, List<String>> mapJavaDoc;

    public NamingStrategy getNamingStrategy() {
        return namingStrategy;
    }

    public void setNamingStrategy(NamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    //    public static final String CLASSJAVADOC = "CLSJD";
    public enum JDocKeys {
        CLASS
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassNameSimple() {
        return className;
    }

    /**
     * Full class name with package separated with dots if a package is provided.
     */
    public String getClassName() {
        if (this.packageName == null || "".equals(this.packageName) || this.packageName.trim().length() < 1) {
            return className;
        } else {
            return this.packageName + "." + this.className;
        }
    }

    public void setClassName(String className) {
        this.className = Objects.requireNonNull(className, "Class name may not be null!");
    }

    public List<String> getImports() {
        return imports;
    }

    public Map<String, NameTypeValue> getData() {
        return data;
    }

    public void setData(Map<String, NameTypeValue> data) {
        this.data = data;
    }

    public int getEnumConstantsAmount() {
        return getData().size();
    }

    public int getSubFieldsAmount() {
        try {
            return getData().values().iterator().next().getName().length;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isEmpty() {
        return getData()!=null && getData().size() == 0;
    }

    public boolean isEnumWithSubfields() {
        if (!isEmpty()) {
            return getSubFieldsAmount()>0;

        }else if(this.fieldNames!=null&&this.fieldNames.length>0){
            return true;
        }
        return false;
    }

    public String[] getFieldNames() {
        if (this.fieldNames == null) {
            updateFieldNamesAndClasses();
        }
        return fieldNames;
    }

    private void updateFieldNamesAndClasses() {
        for (NameTypeValue NameTypeValue : getData().values()) {
            if (NameTypeValue.getName() != null) {
                this.fieldNames = NameTypeValue.getName();
            }
            if (NameTypeValue.getType() != null) {
                this.fieldClasses = NameTypeValue.getType();
            }
        }
    }

    public void setFieldNames(String... fieldNames) {
        this.fieldNames = fieldNames;
    }

    public Class[] getFieldClasses() {
        if (this.fieldClasses == null) {
            updateFieldNamesAndClasses();
        }
        return fieldClasses;
    }

    public void setFieldClasses(Class... fieldClasses) {
        this.fieldClasses = fieldClasses;
    }

    public void addCustomCode(String fieldName, String code) {
        if (Stream.of(getFieldNames()).filter(s -> s.equals(fieldName)).count()!=1) {
            throw new ClassCreationException("Cannot add custom code to a non existing field: " + fieldName);
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
