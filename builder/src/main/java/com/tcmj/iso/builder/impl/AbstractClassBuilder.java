package com.tcmj.iso.builder.impl;

import java.util.Objects;
import com.tcmj.iso.api.ClassBuilder;
import com.tcmj.iso.api.NamingStrategy;
import com.tcmj.iso.api.SourceFormatter;
import com.tcmj.iso.api.model.ClassCreationException;
import com.tcmj.iso.api.model.EnumData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A skeletal implementation of a {@link ClassBuilder} basically to fill all data into the data
 * model.
 */
public abstract class AbstractClassBuilder implements ClassBuilder {

  private static final transient Logger LOG = LoggerFactory.getLogger(ClassBuilder.class);

  /** OS specific line separator. */
  protected static final String LINE = System.getProperty("line.separator");

  /** The main pojo which holds all enum specific data. */
  protected EnumData model = new EnumData();

  /** Formatter object used to reformat the code after building. */
  protected SourceFormatter sourceFormatter;

  @Override
  public ClassBuilder withName(String name) {
    this.model.setPackageName(StringUtils.substringBeforeLast(name, "."));
    this.model.setClassName(StringUtils.substringAfterLast(name, "."));
    return this;
  }

  @Override
  public ClassBuilder convertConstantNames(NamingStrategy instance) {
    LOG.debug("...using NamingStrategy for constant names: {}", instance);
    this.model.setNamingStrategyConstants(instance);
    return this;
  }
  
  @Override
  public ClassBuilder convertFieldNames(NamingStrategy instance) {
    LOG.debug("...using NamingStrategy for field variable names: {}", instance);
    this.model.setNamingStrategyFields(instance);
    return this;
  }
  
  @Override
  public ClassBuilder usingCustomFormatter(SourceFormatter object) {
    this.sourceFormatter = Objects.requireNonNull(object, "Null not allowed for SourceFormatter!");
    return this;
  }

  @Override
  public ClassBuilder addImport(String importTag) {
    this.model.getImports().add(importTag);
    return this;
  }

  @Override
  public ClassBuilder addClassJavadoc(String text) {
    this.model.addJavaDoc(EnumData.JDocKeys.CLASS.name(), text);
    return this;
  }

  @Override
  public abstract ClassBuilder addJavadoc(String fieldName, String javaDoc);

  @Override
  public ClassBuilder setFields(String[] fieldNames, Class[] classes) {
    this.model.setFieldNames(fieldNames);
    this.model.setFieldClasses(classes);
    return this;
  }

  @Override
  public ClassBuilder addField(String constantName, Object[] values) {
    this.model.addConstant(constantName, values);
    return this;
  }
  
  @Override
  public ClassBuilder addField(String constantName, String[] fieldNames, Class[] classes, Object[] values) {
    this.model.setFieldNames(fieldNames);
    this.model.setFieldClasses(classes);
    this.model.addConstant(constantName, values);
    return this; 
  }

  @Override
  public ClassBuilder addField(String constantName) {
    this.model.addConstant(constantName);
    return this;  
  }
  
  
  @Override
  public ClassBuilder addCustomStaticGetterMethod(
      String methodName, String paramType, String paramName, String code, String javaDoc) {
    return this;
  }

  @Override
  public ClassBuilder overrideGetter(String fieldName, String code, String... javaDoc) {
    this.model.addCustomCode(fieldName, code);
    //todo javadoc
    return this;
  }

  @Override
  public abstract String build();

  @Override
  public EnumData getModel() {
    return this.model;
  }

  protected String insertLineBreakAfterCodeValues(String myEnum) throws Exception {
    StringBuilder builder = new StringBuilder(myEnum);
    int posA = builder.indexOf("public enum");
    int posB = builder.indexOf(";", posA);
    builder.insert(posB + 1, LINE);
    return builder.toString();
  }

  protected void validate() {
    if (this.model.getData().isEmpty()) {
      throw new ClassCreationException("No Fields set! Use one of the addField methods!");
    }
    if (StringUtils.isBlank(this.model.getClassNameSimple())) {
      throw new ClassCreationException("No ClassName is set! Use the withName(String) method!");
    }
    if (StringUtils.isBlank(this.model.getPackageName())) {
      throw new ClassCreationException(
          "No PackageName is set! Use something like .withName(\"com.tcmj.MyEnum\")!");
    }
  }

  protected String applyFormatter(String raw) {
    if (this.sourceFormatter != null) {
      LOG.trace("SourceFormatter found: {}", sourceFormatter);
      return this.sourceFormatter.format(raw);
    }
    return raw;
  }
}
