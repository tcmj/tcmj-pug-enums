package com.tcmj.pug.enums.datasources.impl;

import java.util.Objects;
import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.api.tools.EnumDataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Static data provider which can be used to build a data source programmatically. */
public class StaticDataProvider implements DataProvider {
  private static final transient Logger LOG = LoggerFactory.getLogger(StaticDataProvider.class);
  EnumData model = new EnumData();

  public StaticDataProvider() {
  }

  public StaticDataProvider(String[] fieldNames, Class[] fieldClasses) {
    model.setFieldNames(Objects.requireNonNull(fieldNames, "FieldNames cannot be null!"));
    model.setFieldClasses(Objects.requireNonNull(fieldClasses, "FieldClasses cannot be null!"));
    LOG.info("FieldNames={}, FieldClasses={}", model.getFieldNames(), model.getFieldClasses());
  }

  public EnumData getModel() {
    return model;
  }

  public void addConstantWithoutSubfield(String constantName) {
    EnumDataHelper.addConstantWithoutSubfield(model, constantName);
  }

  public void addConstantValue(String constantName, Object... values) {
    EnumDataHelper.addConstantValue(model, constantName, values);
  }

  @Override
  public EnumData load() {
    return model;
  }
}
