package com.tcmj.iso.api.tools;

import java.util.Objects;
import com.tcmj.iso.api.model.ClassCreationException;
import com.tcmj.iso.api.model.EnumData;
import com.tcmj.iso.api.model.NameTypeValue;

/** Provides code snippets to simplify working with {@link EnumData}. */
public class EnumDataHelper {

  public static String extractPackage(String fullClassName) {
    String className = Objects.requireNonNull(fullClassName, "Please provide a java class name with full package path!");
    int lastDot = className.lastIndexOf('.');
    return fullClassName.substring(0, lastDot);
  }

  public static String extractSimpleClassName(String fullClassName) {
    String className = Objects.requireNonNull(fullClassName, "Please provide a java class name with full package path!");
    int lastDot = className.lastIndexOf('.');
    return fullClassName.substring(lastDot + 1);
  }

  public static void addConstantValue(EnumData model, String constantName, Object... values) {
    Objects.requireNonNull(model.getFieldNames(), "No field names found in your model! Use EnumData#setFieldNames first!");
    Objects.requireNonNull(model.getFieldClasses(), "No field types found in your model! Use EnumData#setFieldClases first!");
    model.getData().add(NameTypeValue.of(constantName, values));
  }

  public static void addConstantWithoutSubfield(EnumData model, String constantName) {
    if (model.isEnumWithSubfields()) {
      throw new ClassCreationException("It is not wise to mix enum constants with and without subfields!");
    }
    model.getData().add(NameTypeValue.of(constantName));
  }
}
