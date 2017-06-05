package com.tcmj.iso.builder.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.tcmj.iso.api.ClassBuilder;
import com.tcmj.iso.api.model.ClassCreationException;
import com.tcmj.iso.api.model.EnumData;
import com.tcmj.iso.api.model.NameTypeValue;
import com.tcmj.iso.tools.CamelCase;
import org.apache.commons.lang3.StringUtils;

/**
 * A simple Java source code builder to create enum objects. This implementation of {@link
 * ClassBuilder} uses a {@link StringBuilder} internally.
 */
public class StringBufferEnumBuilder extends AbstractClassBuilder {
  final StringBuilder buffer = new StringBuilder(TEMPLATE);
  static final String TEMPLATE =
      "[PACKAGE][IMPORTS][CLSJDOC][STARTCLASS][CONSTANTS][CONSTRUCTOR][CONTENT][ENDCLASS]";
  static final String PUBLIC_ENUM = "public enum ";
  static final char COMMA = ',';
  static final char SPACE = ' ';
  static final String COMMA_NEWLINE = "," + LINE;
  static final String TAB4 = "  ";
  static final String TAB5 = "   ";

  static final char OPENC = '(';
  static final char CLOSEC = ')';
  static final char OPENG = '{';
  static final char CLOSEG = '}';

  /** holds all javadocs of the fields of the enum. */
  Map<String, String> mapJavadocs = new HashMap<>();

  /** holds custom code according to fields. Signatur,Code. */
  Map<String, String> mapCustomCode = new HashMap<>();

  Map<String, String> mapCustomCodeJavaDoc = new HashMap<>();

  /** Replaces the java code for the getter of the field */
  public StringBufferEnumBuilder addCustomCode(String methodName, String code) {
    this.mapCustomCode.put(methodName, code);
    return this;
  }

  public StringBufferEnumBuilder addCustomCode(String methodName, String code, String javaDoc) {
    addCustomCode(methodName, code);
    this.mapCustomCodeJavaDoc.put(methodName, javaDoc);
    return this;
  }

  @Override
  public ClassBuilder addCustomStaticGetterMethod(
      String methodName, String paramType, String paramName, String code, String javaDoc) {
    //todo not yet tested
    addCustomCode(methodName, code);
    this.mapCustomCodeJavaDoc.put(methodName, javaDoc);
    return this;
  }

  private StringBufferEnumBuilder startValueRecord(StringBuffer buffer, String name) {

    String title = (String) CamelCase.toCamelCase(name);
    title = title.replace('&', '_');
    title = title.replace('(', '_');
    title = title.replace(')', '_');
    title = title.replace('’', '_');
    title = title.replace('.', '_');

    buffer.append(TAB4);
    buffer.append(title.toUpperCase());
    buffer.append(OPENC);
    return this;
  }

  private StringBufferEnumBuilder endValueRecord(StringBuffer buffer) {
    buffer.deleteCharAt(buffer.length() - 1);
    buffer.deleteCharAt(buffer.length() - 1);
    buffer.append(CLOSEC);
    buffer.append(COMMA_NEWLINE);
    return this;
  }

  private static String format(Object value, Class type) {
    if (CharSequence.class.isAssignableFrom(type)) {
      return "\"" + value + "\"";
    } else if (Long.class.isAssignableFrom(type)) {
      return String.valueOf(value) + "L";
    } else if (Number.class.isAssignableFrom(type)) {
      return String.valueOf(value);
    } else if (int.class.isAssignableFrom(type)) {
      return String.valueOf(value);
    } else {
      return "new " + type.getSimpleName() + "()";
    }
  }

  private void writeConstructor() {
    StringBuffer temp = new StringBuffer();
    if (this.model.isEmpty()) {
      temp.append("");
    } else if (this.model.isEnumWithSubfields()) {
      temp.append(LINE);
      temp.append(TAB4);
      temp.append(this.model.getClassNameSimple());
      temp.append(OPENC);

      NameTypeValue triple = this.model.getData().values().iterator().next();

      int size = this.model.getSubFieldsAmount();

      for (int i = 0; i < size; i++) {
        Class type = triple.getType()[i];
        String field = triple.getName()[i];
        temp.append(type.getSimpleName());
        temp.append(SPACE);
        temp.append(field);
        temp.append(COMMA);
        temp.append(SPACE);
      }
      deleteLast(temp, ", ");

      temp.append(CLOSEC);
      temp.append(SPACE);
      temp.append(OPENG);
      temp.append(LINE);

      for (int i = 0; i < size; i++) {
        String field = triple.getName()[i];
        temp.append(TAB4);
        temp.append(TAB4);
        temp.append("this.");
        temp.append(field);
        temp.append(" = ");
        temp.append(field);
        temp.append(";");
        temp.append(LINE);
      }
      deleteLast(temp, LINE);
      //            if (temp.charAt(temp.length() - 1) == LINE.charAt(0)) {
      //                temp.deleteCharAt(temp.length() - 1); //correcture of last loop iteration
      //            }
      temp.append(LINE);
      temp.append(TAB4);
      temp.append(CLOSEG);
      temp.append(LINE);

      //start field variables
    } else {
      temp.append("");
    }
    insertIfNotAlreadyDone("[CONSTRUCTOR]", temp.toString(), true);
  }

  protected static String appendCharacterIfMissing(String tmp, String searchChar) {
    StringBuilder temp = new StringBuilder(tmp);
    appendCharacterIfMissing(temp, searchChar);
    return temp.toString();
  }

  protected static void appendCharacterIfMissing(StringBuilder tmp, String searchChar) {
    //step1: delete the trailing chars or parts of it
    for (int i = (searchChar.length() - 1); i >= 0; i--) {
      int lastCharPos = tmp.length() - 1;
      if (tmp.charAt(lastCharPos) == searchChar.charAt(i)) {
        tmp.deleteCharAt(lastCharPos);
      }
    }
    //step2: re-append the full suffix:
    tmp.append(searchChar);
  }

  private static void writeJavadoc(StringBuilder tmp, String javaDocText) {
    tmp.append(TAB4);
    tmp.append("/**");
    tmp.append(LINE);
    tmp.append(TAB5);
    tmp.append("* ");
    tmp.append(javaDocText);

    //last char of javadoc text should be a dot. The sentence will be written bold and is that single line.
    appendCharacterIfMissing(tmp, ".");

    tmp.append(LINE);
    tmp.append(TAB4);
    tmp.append(" */");
    tmp.append(LINE);
  }

  private void writeGetters() {
    buffer.append(LINE);
    NameTypeValue triple = this.model.getData().values().iterator().next();

    int size = this.model.getSubFieldsAmount();

    for (int i = 0; i < size; i++) {
      String nme = triple.getName()[i];
      Class cls = triple.getType()[i];
      String jdoc = mapJavadocs.get(nme);
      if (StringUtils.isNotBlank(jdoc)) {
        writeJavadoc(buffer, jdoc);
      }
      buffer.append(TAB4);
      buffer.append("public ");
      String type = cls.getSimpleName();
      buffer.append(type);
      buffer.append(SPACE);
      buffer.append(CamelCase.toGetter(nme));
      buffer.append(OPENC);
      buffer.append(CLOSEC);
      buffer.append(SPACE);
      buffer.append(OPENG);
      buffer.append(LINE);

      String customCode = this.model.getCustomCode(nme);
      buffer.append(TAB4);
      buffer.append(TAB4);
      if (customCode == null) {
        buffer.append("return this.");
        buffer.append(nme);
      } else {
        buffer.append(customCode);
      }
      appendCharacterIfMissing(buffer, ";");
      appendCharacterIfMissing(buffer, LINE);

      buffer.append(TAB4);
      buffer.append(CLOSEG);
      buffer.append(LINE);
    }
  }

  private void writeCustomCode() {
    if (mapCustomCode.isEmpty()) {
      return;
    }

    buffer.append(LINE);

    for (Map.Entry<String, String> entry : mapCustomCode.entrySet()) {
      String fieldName = entry.getKey();
      String customCode = entry.getValue();

      String jdoc = mapCustomCodeJavaDoc.get(fieldName);
      if (StringUtils.isNotBlank(jdoc)) {
        writeJavadoc(buffer, jdoc);
      }

      //signature:
      buffer.append(TAB4);
      buffer.append(fieldName);
      appendCharacterIfMissing(buffer, " {");

      //custom return code of the field:
      buffer.append(LINE);
      buffer.append(TAB4);
      buffer.append(TAB4);
      buffer.append(customCode);
      buffer.append(LINE);
      buffer.append(TAB4);
      buffer.append(CLOSEG);
      buffer.append(LINE);
    }
  }

  public String build() {
    validate();
    try {
      writePackage();
      writeSTARTCLASS();
      writeFields();
      writeConstructor();
      writeGetters();
      writeCustomCode();

      buffer.append(CLOSEG);
      buffer.append(LINE);

      insertIfNotAlreadyDone("[PACKAGE]", "", true);
      writeCLSJDOC();
      insertIfNotAlreadyDone("[STARTCLASS]", "", true);
      insertIfNotAlreadyDone("[CONSTANTS]", "", true);
      insertIfNotAlreadyDone("[CONSTRUCTOR]", "", true);
      insertIfNotAlreadyDone("[CONTENT]", "", true);
      insertIfNotAlreadyDone("[ENDCLASS]", "", true);

      writeIMPORTS();

      return applyFormatter(buffer.toString());

    } catch (Exception e) {
      e.printStackTrace();
      throw new ClassCreationException(e.getMessage());
    }
  }

  private void writeIMPORTS() {
    if (!this.model.getImports().isEmpty()) {
      StringBuilder tmp = new StringBuilder();
      for (String importLine : this.model.getImports()) {
        String importSingle = StringUtils.prependIfMissing(importLine, "import ");
        tmp.append(StringUtils.appendIfMissing(importSingle, ";" + LINE));
      }
      insertIfNotAlreadyDone("[IMPORTS]", tmp.toString(), true);
    } else {
      insertIfNotAlreadyDone("[IMPORTS]", "", true);
    }
  }

  private void writeCLSJDOC() {
    StringBuilder classJavaDoc = new StringBuilder();

    List<String> javaDocLines = this.model.getJavaDocLines(EnumData.JDocKeys.CLASS.name());
    if (javaDocLines != null && !javaDocLines.isEmpty()) {
      classJavaDoc.append("/**");
      classJavaDoc.append(LINE);

      for (String line : javaDocLines) {
        String myLine = StringUtils.prependIfMissing(line, " * ");
        myLine = StringUtils.appendIfMissing(myLine, LINE);
        classJavaDoc.append(myLine);
      }
      classJavaDoc.append("*/");
      classJavaDoc.append(LINE);
    }
    insertIfNotAlreadyDone("[CLSJDOC]", classJavaDoc.toString(), true);
  }

  private void writeSTARTCLASS() {
    String content =
        PUBLIC_ENUM
            + Objects.requireNonNull(
                this.model.getClassNameSimple(), "No class name set! Use .withName(String)!")
            + " {"
            + LINE;
    insertIfNotAlreadyDone("[STARTCLASS]", content, true);
  }

  private void writeFields() {
    StringBuffer temp = new StringBuffer();

    if (this.model.isEmpty()) { //no fields <fictional case>
      temp.append("");
    } else if (this.model.isEnumWithSubfields()) { //constants with sub values

      //start enum constants

      for (Map.Entry<String, NameTypeValue> entry : this.model.getData().entrySet()) {
        String constantName = entry.getKey();
        startValueRecord(temp, constantName);

        NameTypeValue triple = entry.getValue();

        int size = this.model.getSubFieldsAmount();

        for (int i = 0; i < size; i++) {

          Class type = triple.getType()[i];
          Object value = triple.getValue()[i];
          temp.append(format(value, type));
          temp.append(COMMA);
          temp.append(SPACE);
        }
        endValueRecord(temp);
      }

      deleteLast(temp, COMMA_NEWLINE);
      temp.append(";");

      //start field variables
      NameTypeValue triple = this.model.getData().values().iterator().next();

      int size = this.model.getSubFieldsAmount();

      for (int i = 0; i < size; i++) {
        String name = triple.getName()[i];
        Class type = triple.getType()[i];

        temp.append(LINE);
        temp.append(TAB4);
        temp.append("private final ");
        temp.append(type.getSimpleName());
        temp.append(SPACE);
        temp.append(name);
        temp.append(";");
      }

    } else { //without subfields

      for (Map.Entry<String, NameTypeValue> entry : this.model.getData().entrySet()) {
        String constantName = entry.getKey();
        temp.append(TAB4);
        temp.append(constantName);
        temp.append(",");
        temp.append(LINE);
      }
      deleteLast(temp, LINE);
      deleteLast(temp, ",");
      temp.append(";");
    }
    insertIfNotAlreadyDone("[CONSTANTS]", temp.toString(), true);
  }

  public void writePackage() {
    String packageName = this.model.getPackageName();
    String pName = appendCharacterIfMissing(packageName, ";");

    int found = insertIfNotAlreadyDone("[PACKAGE]", "package " + pName + LINE + LINE, true);
  }

  @Override
  public ClassBuilder addJavadoc(String fieldName, String javaDoc) {
    this.mapJavadocs.put(fieldName, javaDoc);
    return this;
  }

  public void deleteLast(StringBuffer bbb, String value) {
    int idx = bbb.lastIndexOf(value);
    if (idx >= 0) {
      bbb.delete(idx, idx + value.length());

    } else {
      throw new UnsupportedOperationException("cannot find fuin: " + value);
    }
  }

  public int insertIfNotAlreadyDone(String tag, String content, boolean removeTag) {
    int pos = buffer.indexOf(tag);
    if (pos >= 0) {
      if (removeTag) {
        buffer.delete(pos, pos + tag.length());
      }
      buffer.insert(pos, content);
      return pos + tag.length();
    }
    return -1;
  }
}
