package com.tcmj.pug.enums.builder.impl;

import com.sun.codemodel.*;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import com.tcmj.pug.enums.api.ClassBuilder;
import com.tcmj.pug.enums.model.ClassCreationException;
import com.tcmj.pug.enums.model.NameTypeValue;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

import static com.sun.codemodel.JExpr._this;
import static com.tcmj.pug.enums.tools.CamelCase.toGetter;

/**
 * Implementation of a {@link com.tcmj.pug.enums.api.ClassBuilder}
 * using {@link com.sun.codemodel} which is part of the GlassFish project.
 */
public class CodeModelEnumBuilder extends AbstractClassBuilder {

  /** Main-Class of the com.sun.codemodel Framework. */
  private final JCodeModel codeModel = new JCodeModel();

  /** Modifier for enum fields. */
  private static final int PRIVATE_FINAL = JMod.PRIVATE + JMod.FINAL;

  /** com.sun.codemodel: Class object. */
  private JDefinedClass jclass;

  /** com.sun.codemodel: Constructor object. */
  private JMethod constructor;

  /** com.sun.codemodel: holds all fields of the enum. */
  private Map<String, JFieldVar> jfields = new HashMap<>();

  /** Holds all javadocs of the fields of the enum. */
  private Map<String, String> mapJavadocs = new HashMap<>();

  /** Holds all javadocs for custom code. */
  private Map<String, String> mapCustomCodeJavaDoc = new HashMap<>();

  public CodeModelEnumBuilder() {
  }

  @Override
  public ClassBuilder withName(String name) {
    super.withName(name);
    try {
      if (model.getPackageName() == null) {
        this.jclass
            = codeModel
                .rootPackage()
                ._class(JMod.PUBLIC, this.model.getPackageName(), ClassType.ENUM);
      } else {
        /* com.sun.codemodel: Package object. */
        JPackage jpackage = codeModel._package(this.model.getPackageName());
        this.jclass = jpackage._enum(this.model.getClassNameSimple());
      }
    } catch (Exception e) {
      throw new ClassCreationException(e);
    }
    return this;
  }

  @Override
  public ClassBuilder addClassJavadoc(String text) {
    super.addClassJavadoc(text);
    this.jclass.javadoc().add(text);
    return this;
  }

  /** add a javadoc to a field getter. */
  @Override
  public ClassBuilder addJavadoc(String fieldName, String javaDoc) {
    this.mapJavadocs.put(fieldName, javaDoc);
    return this;
  }

  @Override
  public ClassBuilder addCustomStaticGetterMethod(
      String methodName, String paramType, String paramName, String code, String javaDoc) {
    try {
      JMethod method = this.jclass.method(JMod.PUBLIC | JMod.STATIC, this.jclass, methodName);
      try {
        JType paramTypeType = JType.parse(this.codeModel, paramType); //void or primitive datatype
        method.param(paramTypeType, paramName);

      } catch (Exception e) {

        method.param(Class.forName(paramType), paramName);
      }

      method.javadoc().add(javaDoc);

      JBlock block = method.body();

      block.add(f -> f.p(code).nl());

    } catch (Exception e) {
      throw new ClassCreationException(e);
    }
    return this;
  }

  @Override
  public ClassBuilder overrideGetter(String fieldName, String code, String... javaDoc) {
    super.overrideGetter(fieldName, StringUtils.appendIfMissing(code, ";"), javaDoc);

    if (javaDoc != null && javaDoc.length > 0) {
      StringBuilder buffer = new StringBuilder();
      for (String s : javaDoc) {
        buffer.append(s);
      }
      this.mapCustomCodeJavaDoc.put(fieldName, buffer.toString());
    }

    return this;
  }

  private void writeGetters() {

    for (Map.Entry<String, JFieldVar> jFieldVarEntry : jfields.entrySet()) {
      String name = jFieldVarEntry.getKey();
      JFieldVar field1 = jFieldVarEntry.getValue();
      JMethod field1GetterMethod = this.jclass.method(JMod.PUBLIC, field1.type(), toGetter(name).toString());

      final String customCode = this.model.getCustomCode(name);
      if (customCode == null) {
        field1GetterMethod.body()._return(field1);
        String jdoc = mapJavadocs.get(name);
        if (StringUtils.isNotBlank(jdoc)) {
          field1GetterMethod.javadoc().add(jdoc);
        }
      } else {
        JBlock block = field1GetterMethod.body();
        block.add(f -> f.p(customCode).nl());
        String jdoc = mapCustomCodeJavaDoc.get(name);
        if (StringUtils.isNotBlank(jdoc)) {
          field1GetterMethod.javadoc().add(jdoc);
        }
      }
    }
  }

  @Override
  public String build() {
    validate();
    try {
      writeFields();
      writeGetters();

      OutputStream os = new ByteArrayOutputStream();
      codeModel.build(new SingleStreamCodeWriter(os));
      String myEnum = os.toString();
      String substring = myEnum.substring(getLengthOfHeadline(myEnum));
      substring = injectImports(substring, this.model.getImports());

      //todo create a RemoveEmptyJDocLines-SourceCodeFormatter-Object
      substring = StringUtils.replace(substring, LINE + " * " + LINE, "");

      return applyFormatter(substring);

    } catch (Exception e) {
      throw new ClassCreationException(e);
    }
  }

  private String injectImports(String rawSource, List<String> imports) {
    if (this.model.getImports().isEmpty()) {
      return rawSource;
    }
    int countOSLB = StringUtils.countMatches(rawSource, LINE);
    int countBNLB = StringUtils.countMatches(rawSource, "\n");

    String splitChar;
    if (countBNLB > countOSLB) {
      splitChar = "\n";
    } else {
      splitChar = LINE;
    }

    List<String> result = new ArrayList<>();
    for (String s : rawSource.split(splitChar, -1)) {
      result.add(s);
      if (s.startsWith("package ")) {
        result.add("");
        for (String i : imports) {
          String importSingle = StringUtils.prependIfMissing(i, "import ");
          importSingle = StringUtils.appendIfMissing(importSingle, ";");
          result.add(importSingle);
        }
      }
    }
    return StringUtils.join(result, LINE);
  }

  private int getLengthOfHeadline(String myEnum) {
    int cutPos;
    if (this.model.getPackageName() != null) {
      cutPos = myEnum.indexOf("package");
    } else {
      int posA = myEnum.indexOf("/**");
      if (posA < 0) {
        posA = myEnum.indexOf("public enum");
      }
      if (posA < 0) {
        posA = myEnum.lastIndexOf("-----");
      }
      cutPos = posA;
    }
    return cutPos;
  }

  private void writeFields() {
    int loopCount = 0;
    for (NameTypeValue entry : this.model.getData()) {
      String constantName = entry.getConstantName();
      loopCount++;

      //New enum value
      JEnumConstant enumField = this.jclass.enumConstant(constantName);

      if (this.model.getFieldNames() != null) {
        int size = this.model.getFieldNames().length;
        for (int i = 0; i < size; i++) {
          String name = this.model.getFieldName(i);
          Class type = this.model.getFieldClass(i);
          Object value = entry.getValue()[i];

          //new enum constructor value
          addEnumFieldValue(enumField, type, value);

          //create exactly one constructor
          if (this.constructor == null) {
            this.constructor = this.jclass.constructor(JMod.NONE);
          }

          //create each constructor field (but only each field once)
          if (loopCount == 1) {
            writeConstructor(name, type);
          }
        }
      }
    }
  }

  private void addEnumFieldValue(JEnumConstant enumField, Class type, Object value) {
    if (type == String.class) {
      enumField.arg(JExpr.lit((String) value));
    } else if (type == Boolean.class) {
      enumField.arg(JExpr.lit((boolean) value));
    } else if (type == Integer.class || type == int.class) {
      enumField.arg(JExpr.lit((int) value));
    } else if (type == Long.class) {
      enumField.arg(JExpr.lit((long) value));
    } else if (type == Double.class) {
      enumField.arg(JExpr.lit((double) value));
    } else if (type == Character.class) {
      enumField.arg(JExpr.lit((char) value));
    } else {
      JClass refObject = codeModel.ref(type);
      JInvocation jInvocation = JExpr._new(refObject);
      enumField.arg(jInvocation);
    }
  }

  private void writeConstructor(String name, Class type) {
    JFieldVar field1 = this.jclass.field(PRIVATE_FINAL, type, name);
    jfields.put(name, field1);

    //create each constructor parameter
    JVar param1 = constructor.param(type, name);

    JBlock body = constructor.body();
    body.assign(_this().ref(field1), param1);
  }

  @Override
  public ClassBuilder addImport(String importTag) {
    super.addImport(importTag);
    this.model.getImports().add(importTag);
    //JClass mapper = codeModel.directClass(importTag);
    //JClass arrays = codeModel.ref(importTag);
    codeModel.ref(Objects.class);
    // System.out.println(codeModel.ref(Objects.class).name());
    //        JMethod method = this.jclass.method(JMod.PUBLIC | JMod.STATIC, Void.TYPE, "testMethod");
    //        JBlock executerBlock = method.body();
    //        executerBlock.staticInvoke(mapper, "get");

    return this;
  }

}
