package com.tcmj.pug.enums.datasources.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.api.tools.EnumDataHelper;
import com.tcmj.pug.enums.model.ClassCreationException;
import com.tcmj.pug.enums.model.EnumData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/** GSON - Json Data Provider needs com.google.code.gson:gson as runtime dependency */
public class JsonDataProvider implements DataProvider {
  private static final transient Logger LOG = LoggerFactory.getLogger(JsonDataProvider.class);

  EnumData model = new EnumData();
  final Reader reader;

  /** Json Field which should be used for the enum constant values. */
  String fieldNameConstants;

  public JsonDataProvider(
      Reader reader,
      String fieldNameConstant,
      String[] fieldNames,
      Class[] fieldClasses) {
    this.reader = Objects.requireNonNull(reader, "Reader cannot be null!");
    this.fieldNameConstants = Objects.requireNonNull(fieldNameConstant, "Please define the json field name used for the enum constants!");
    model.setFieldNames(fieldNames);
    model.setFieldClasses(fieldClasses);
    LOG.info("PackageName={}, SimpleClassName={}, FullClassName={}", model.getPackageName(), model.getClassNameSimple(), model.getClassName());
    LOG.info("JsonFieldNameConstants={}, FieldNames={}, FieldClasses={}", fieldNameConstant, model.getFieldNames(), model.getFieldClasses());
  }

  private boolean isSubfields() {
    return this.model.isEnumWithSubfields();
  }

  @Override
  public EnumData load() {
    try {
      JsonParser parser = new JsonParser();
      JsonArray outerArray = (JsonArray) parser.parse(reader);
      LOG.trace("Outter array structure successfully found and parsed!");
      for (JsonElement record : outerArray) {
        LOG.debug("JsonRecord: {}", record);
        JsonObject jsonObjectRecord = record.getAsJsonObject();
        String constantName = Objects.requireNonNull(jsonObjectRecord.get(fieldNameConstants).getAsString(), "ConstantFieldName not found: " + fieldNameConstants);
        LOG.debug("Main Json field successfully found for the enum constant values: '{}'='{}'", fieldNameConstants, constantName);
        if (isSubfields()) {
          Object[] values = new Object[model.getFieldNames().length];
          for (int i = 0; i < model.getFieldNames().length; i++) {
            String expectedField = model.getFieldNames()[i];
            Class fieldType = model.getFieldClass(i);
            JsonElement jsonElement = Objects.requireNonNull( jsonObjectRecord.get(expectedField), "SubFieldName not found: " + expectedField);
            LOG.debug("Subfield successfully found: '{}'='{}'", expectedField, jsonElement);
            Object value = toType(jsonElement, fieldType);
            values[i] = value;
          }
          EnumDataHelper.addConstantValue(model, constantName, values);
          LOG.debug("Successfully added following values '{}'  to enum constant '{}'", values, constantName);
        } else {
          EnumDataHelper.addConstantWithoutSubfield(model, constantName);
          LOG.debug("Successfully added enum constant '{}'", constantName);
        }
      }
    } catch (Exception e) {
      LOG.error("Cannot parse JSON file to EnumData!", e);
      LOG.error("Example JSON Structure: {\"name\":\"AF\",\"nameUS\":\"Africa\",\"nameDE\":\"Afrika\",\"areaKM2\":30370000,\"areaPct\":20.4,\"elevationHighest\":5895,\"elevationLowest\":-155}");
      throw new ClassCreationException(e);
    }
    return model;
  }

  private static Object toType(JsonElement jsonElement, Class type) {
    if (type == Integer.class) {
      return jsonElement.getAsInt();
    } else if (type == Long.class) {
      return jsonElement.getAsLong();
    } else if (type == Float.class) {
      return jsonElement.getAsFloat();
    } else if (type == Double.class) {
      return jsonElement.getAsDouble();
    } else if (type == Boolean.class) {
      return jsonElement.getAsBoolean();
    } else if (type == Byte.class) {
      return jsonElement.getAsByte();
    } else if (type == Character.class) {
      return jsonElement.getAsCharacter();
    } else if (type == BigDecimal.class) {
      return jsonElement.getAsBigDecimal();
    } else if (type == BigInteger.class) {
      return jsonElement.getAsBigInteger();
    } else {
      return jsonElement.getAsString();
    }
  }
}
