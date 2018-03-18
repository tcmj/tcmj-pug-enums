package com.tcmj.pug.enums.api;

import com.tcmj.pug.enums.model.EnumData;

/**
 * API Interface which allows implementation of different java source code builders. The
 * implementation is reduced to especially create builder for enum objects.
 */
public interface ClassBuilder {

  /** [Mandatory] Define a full qualified java class path and name. */
  ClassBuilder withName(String name);

  /** [Optional] Change constant names in the enum. */
  ClassBuilder convertConstantNames(NamingStrategy instance);

  /** [Optional] Change variable/field names in the enum. */
  ClassBuilder convertFieldNames(NamingStrategy instance);

  /** [Optional] Add additional imports to the enum. */
  ClassBuilder addImport(String importTag);

  /** [Optional] Add a class javadoc to your enum. */
  ClassBuilder addClassJavadoc(String text);

  /** [Optional] Add a javadoc to the public getter of a field. */
  ClassBuilder addJavadoc(String fieldName, String javaDoc);

  /** [Conditional] Add a field to the enum. */
  ClassBuilder setFields(String[] fieldNames, Class[] classes);

  /** [Conditional] Add a field to the enum. */
  ClassBuilder addField(String constantName, Object[] values);

  /** [Conditional] Add a field to the enum. */
  ClassBuilder addField(String constantName, String[] fieldNames, Class[] classes, Object[] values);

  /** [Conditional] Add a enum constant without any sub fields. */
  ClassBuilder addField(String constantName);

  /** [Optional] Add a custom static getter method to your enum. */
  ClassBuilder addCustomStaticGetterMethod(
    String methodName, String paramType, String paramName, String code, String javaDoc);

  /** [Optional] Override one of the getter methods of your enum. */
  ClassBuilder overrideGetter(String fieldName, String code, String... javaDoc);

  /**
   * [Optional] Use a specific source code formatter or simply a chance to do some customizations
   * afterwards.
   */
  ClassBuilder usingCustomFormatter(SourceFormatter sourceFormatter);

  /** [Mandatory] Finally create the Object. */
  String build();

  /** Access to the values backing the object. */
  EnumData getModel();

  /** [Optional] Import an EnumData object. */
  ClassBuilder importData(EnumData data);

}
