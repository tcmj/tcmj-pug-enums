package com.tcmj.pug.enums.datasources.impl;

import java.io.Reader;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import com.tcmj.pug.enums.api.DataProvider;
import com.tcmj.pug.enums.model.ClassCreationException;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.api.tools.EnumDataHelper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Dataprovider which parses a comma separated value document to a EnumModel object. */
public class CSVDataProvider implements DataProvider {
  private static final transient Logger LOG = LoggerFactory.getLogger(CSVDataProvider.class);
  private EnumData model = new EnumData();
  private final Reader reader;

  /** CSV Field which should be used for the enum constant values. */
  private final String fieldNameConstants;

  public CSVDataProvider(
      String fullClassName,
      Reader reader,
      String fieldNameConstant,
      String[] fieldNames,
      Class[] fieldClasses) {
    this.fieldNameConstants =
        Objects.requireNonNull(
            fieldNameConstant, "Please define the csv field name used for the enum constants!");
    model.setPackageName(EnumDataHelper.extractPackage(fullClassName));
    model.setClassName(EnumDataHelper.extractSimpleClassName(fullClassName));
    this.reader = Objects.requireNonNull(reader, "Reader cannot be null!");
    model.setFieldNames(fieldNames);
    model.setFieldClasses(fieldClasses);
    LOG.info(
        "PackageName={}, SimpleClassName={}, FullClassName={}",
        model.getPackageName(),
        model.getClassNameSimple(),
        model.getClassName());
    LOG.info(
        "CSVFieldNameConstants={}, FieldNames={}, FieldClasses={}",
        fieldNameConstant,
        model.getFieldNames(),
        model.getFieldClasses());
  }

  private Set<String> getHeaders(Map<String, Integer> map) throws Exception {
    Set<String> headers = new LinkedHashSet<>();
    if (model.isEnumWithSubfields()) {
      Set<String> keyMaps = map.keySet();
      for (String fieldNames : model.getFieldNames()) {
        if (keyMaps.contains(fieldNames)) {
          headers.add(fieldNames);
        }
      }
    }
    return headers;
  }

  @Override
  public EnumData load() {
    try (CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(this.reader)) {
      Set<String> headers = getHeaders(parser.getHeaderMap());
      LOG.info("Header: {}", parser.getHeaderMap());
      LOG.info("HeaderUsing: {}", headers);
      for (CSVRecord record : parser.getRecords()) {
        String constantName =
            Objects.requireNonNull(
                record.get(fieldNameConstants),
                "ConstantFieldName not found: " + fieldNameConstants);
        LOG.debug(
            "CSV field successfully found for enum constant value: '{}'='{}'",
            fieldNameConstants,
            constantName);
        boolean removed = headers.remove(constantName);
        if (removed) {
          LOG.debug("Constant CSV field successfully removed from headermap!");
        }
        String name = record.get(this.fieldNameConstants);

        if (isSubfields()) {
          int i = 0;
          Object[] values = new Object[headers.size()];
          for (String header : headers) {
            String subName = record.get(header);
            LOG.trace("Subfield[{}]: {}={} ", i, header, subName);
            values[i] = subName;
            i++;
          }
          EnumDataHelper.addConstantValue(model, name, values);
          LOG.debug(
              "Successfully added following values '{}'  to enum constant '{}'", values, name);
        } else {
          LOG.trace("Simple Enum: {}={} ", fieldNameConstants, name);
          EnumDataHelper.addConstantWithoutSubfield(model, name);
          LOG.debug("Successfully added enum constant '{}'", name);
        }
      }
    } catch (Exception e) {
      throw new ClassCreationException(e);
    }
    return model;
  }

  private boolean isSubfields() {
    return this.model.isEnumWithSubfields();
  }
}
