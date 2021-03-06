package com.tcmj.pug.enums.builder.impl;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.tcmj.pug.enums.api.ClassBuilder;
import com.tcmj.pug.enums.model.ClassCreationException;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.model.NameTypeValue;
import com.tcmj.pug.enums.tools.CamelCase;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

/** EnumBuilder implementation using JavaPoet framework. */
public class JavaPoetEnumBuilder extends AbstractClassBuilder {
  private static final transient Logger LOG = LoggerFactory.getLogger(JavaPoetEnumBuilder.class);

  private TypeSpec.Builder builder;
  private MethodSpec.Builder constructor = MethodSpec.constructorBuilder();

  @Override
  public ClassBuilder withName(String name) {
    super.withName(name);
    builder = TypeSpec.enumBuilder(this.model.getClassNameSimple()).addModifiers(Modifier.PUBLIC);
    return this;
  }

  @Override
  public ClassBuilder addJavadoc(String fieldName, String javaDoc) {
    return this;
  }

  @Override
  public String build() {
    validate();
    try {

      writeEnumConstants();

      if (this.model.getSubFieldsAmount() > 0) {
        addSubFields();
        addConstructor();
        writeGetters();
      }

      //Start: Class-JavaDoc
      List<String> javaDocLines = this.model.getJavaDocLines(EnumData.JDocKeys.CLASS.name());
      if (javaDocLines != null && javaDocLines.size() > 0) {
        for (String line : javaDocLines) {
          builder.addJavadoc(line);
        }
      }
      //End: Class-JavaDoc

      TypeSpec typeSpec = builder.build();

      String packageName = StringUtils.defaultString(this.model.getPackageName());

      JavaFile javaFile = JavaFile.builder(packageName, typeSpec).skipJavaLangImports(true).build();

      StringBuffer buffer = new StringBuffer();

      if (this.model.getImports().isEmpty()) {
        javaFile.writeTo(buffer);
      } else {
        String finalString = injectImports(javaFile, this.model.getImports());
        buffer.append(finalString);
      }

      return applyFormatter(buffer.toString());

    } catch (Exception e) {
      throw new ClassCreationException(e);
    }
  }

  private void writeGetters() {

    builder.addMethod(MethodSpec.methodBuilder("beep").addModifiers(Modifier.PUBLIC).build());
  }

  private void addConstructor() {
    //finally add the constructor to the enum (..after construction iterating over each sub-field)
    builder.addMethod(constructor.build());
  }

  private void addSubFields() {
    if (this.model.getSubFieldsAmount() > 0) {
      for (int i = 0; i < this.model.getSubFieldsAmount(); i++) {
        String fieldName = this.model.getFieldName(i);
        Class fieldClass = this.model.getFieldClass(i);
        //the (final) field needed to hold the value of the enum subfield
        builder.addField(fieldClass, fieldName, Modifier.PRIVATE, Modifier.FINAL);
        //create each constructor field (but only each field once)
        constructor
            .addParameter(fieldClass, fieldName)
            .addStatement("this.$N = $N", fieldName, fieldName);
        //create a custom or standard getter
        builder.addMethod(createGetter(fieldName, fieldClass));
      }
    }
  }

  private MethodSpec createGetter(String fieldName, Class fieldClass) {
    String customCode = this.model.getCustomCode(fieldName);
    if (customCode == null) {
      return MethodSpec.methodBuilder(CamelCase.toGetter(fieldName).toString())
          .addModifiers(Modifier.PUBLIC)
          .returns(fieldClass)
          .addStatement("return this.$N", fieldName)
          .build();
    } else {
      return MethodSpec.methodBuilder(CamelCase.toGetter(fieldName).toString())
          .addModifiers(Modifier.PUBLIC)
          .returns(fieldClass)
          .addStatement(customCode)
          .build();
    }
  }

  private String injectImports(JavaFile javaFile, List<String> imports) {
    String rawSource = javaFile.toString();

    List<String> result = new ArrayList<>();
    for (String s : rawSource.split("\n", -1)) {
      result.add(s);
      if (s.startsWith("package ")) {
        result.add("");
        for (String i : imports) {
          result.add("import " + i + ";");
        }
      }
    }
    return String.join("\n", result);
  }

  private void writeEnumConstants() {
    boolean hasSubfields = this.model.isEnumWithSubfields();
    //..loop through all data entries (Key=constant_name Value=NULL|Subfields
    for (NameTypeValue entry : this.model.getData()) {
      String constantName = entry.getConstantName();
      String newConstantName = model.getNamingStrategyConstants().convert(constantName);
      if (LOG.isTraceEnabled() && !StringUtils.equals(constantName, newConstantName)) {
        LOG.trace("NamingStrategy changes constantName from='{}' to='{}'", constantName, newConstantName);
      }

      if (hasSubfields) {
        Pair<String, Object[]> pair = format(this.model.getFieldClasses(), entry.getValue());
        TypeSpec.Builder constants = TypeSpec.anonymousClassBuilder(pair.getLeft(), pair.getRight());

        builder.addEnumConstant(newConstantName, constants.build());
      } else {
        //the main (uppercase) enum constant value
        builder.addEnumConstant(newConstantName);
      }
    }
  }

  @Override
  public ClassBuilder overrideGetter(String fieldName, String code, String... javaDoc) {
    return super.overrideGetter(fieldName, StringUtils.removeEnd(code, ";"), javaDoc);

    //
    //        if (javaDoc != null && javaDoc.length > 0) {
    //            StringBuilder buffer = new StringBuilder();
    //            for (String s : javaDoc) {
    //                buffer.append(s);
    //            }
    //            this.mapCustomCodeJavaDoc.put(fieldName, buffer.toString());
    //        }
  }
 
  private static Pair<String, Object[]> format(Class[] type, Object[] obj) {

    StringBuilder buffer = new StringBuilder();
    int idx = -1;
    for (Class aClass : type) {
      idx++;
      if (CharSequence.class.isAssignableFrom(aClass)) {
        buffer.append("$S");
      } else if (Float.class.isAssignableFrom(aClass)) {
        buffer.append("$L");
        buffer.append("F");
      } else if (Number.class.isAssignableFrom(aClass)) {
        buffer.append("$L");
      } else if (int.class.isAssignableFrom(aClass)) {
        buffer.append("$L");
      } else {
        obj[idx] = type[idx];
        buffer.append("new $T()");
      }
      buffer.append(",");
    }
    buffer.deleteCharAt(buffer.length() - 1);
    return new ImmutablePair<>(buffer.toString(), obj);
  }

  @Override
  public ClassBuilder addCustomStaticGetMethod(
    String methodName, Class paramType, String paramName, String code, String javaDoc) {
    try {

      if (StringUtils.equals(StringUtils.right(code, 1), ";")) {
        code = StringUtils.left(code, StringUtils.length(code) - 1);
      }

      ClassName myClass = ClassName.get(getModel().getPackageName(), getModel().getClassNameSimple());

      MethodSpec method = MethodSpec.methodBuilder(methodName)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(myClass)
        .addParameter(paramType, paramName)
        .addStatement(code)
        .build();

      builder.addMethod(method);
    } catch (Exception e) {
      throw new ClassCreationException(e);
    }
    return this;
  }


}
