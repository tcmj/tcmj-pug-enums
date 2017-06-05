package com.tcmj.iso.api.tools;

import java.util.Objects;
import com.tcmj.iso.api.model.ClassCreationException;
import com.tcmj.iso.api.model.EnumData;
import com.tcmj.iso.api.model.NameTypeValue;

/** Provides code snippets to simplify working with {@link EnumData}. */
public class EnumDataHelper {

  public static String extractPackage(String fullClassName) {
    String className =
        Objects.requireNonNull(
            fullClassName, "Please provide a java class name with full package path!");
    int lastDot = className.lastIndexOf('.');
    return fullClassName.substring(0, lastDot);
  }

  public static String extractSimpleClassName(String fullClassName) {
    String className =
        Objects.requireNonNull(
            fullClassName, "Please provide a java class name with full package path!");
    int lastDot = className.lastIndexOf('.');
    return fullClassName.substring(lastDot + 1);
  }

  public static void addConstantValue(EnumData model, String constantName, Object... values) {
    String[] fieldNames =
        Objects.requireNonNull(
            model.getFieldNames(),
            "No field names found in your model! Use EnumData#setFieldNames first!");
    Class[] fieldTypes =
        Objects.requireNonNull(
            model.getFieldClasses(),
            "No field types found in your model! Use EnumData#setFieldClases first!");
    model.getData().put(constantName, NameTypeValue.of(fieldNames, fieldTypes, values));
  }

  public static void addConstantWithoutSubfield(EnumData model, String constantName) {
    if (model.isEnumWithSubfields()) {
      //        if(model.getFieldNames()!=null || model.getFieldClasses()!=null){
      throw new ClassCreationException(
          "Method is to add enum constant values when no subfields are required!");
    }
    model.getData().put(constantName, null);
  }
}
