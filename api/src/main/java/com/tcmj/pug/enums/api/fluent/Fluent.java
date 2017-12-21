package com.tcmj.pug.enums.api.fluent;

import com.tcmj.pug.enums.api.*;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.model.NameTypeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.tcmj.pug.enums.api.tools.NamingStrategyFactory.*;

/**
 * Fluent API.
 */
public class Fluent {
  private static final Logger LOG = LoggerFactory.getLogger(Fluent.class);
  private static final Fluent INSTANCE = new Fluent();

  private DataProvider dataProvider;
  private ClassBuilder classBuilder;
  private SourceFormatter sourceFormatter;
  private EnumExporter enumExporter;

  private String fullClassName;

  private String[] fixedFieldNames;

  private NamingStrategy namingStrategyConstantNames;
  private NamingStrategy namingStrategyFieldNames;

  private Fluent() {
    //not allowed to be instantiated  
  }

  public static Fluent builder() {
    return INSTANCE;
  }

  public Fluent dataProvider(DataProvider value) {
    dataProvider = Objects.requireNonNull(value, "DataProvider may not be null!");
    LOG.debug("dataProvider {}..", getDataProvider());
    return INSTANCE;
  }

  public Fluent classBuilder(ClassBuilder value) {
    classBuilder = Objects.requireNonNull(value, "ClassBuilder may not be null!");
    LOG.debug("classBuilder {}..", getClassBuilder());
    return INSTANCE;
  }

  public Fluent sourceFormatter(SourceFormatter value) {
    sourceFormatter = Objects.requireNonNull(value, "SourceFormatter may not be null!");
    LOG.debug("sourceFormatter {}..", getSourceFormatter());
    return INSTANCE;
  }

  public Fluent enumExporter(EnumExporter value) {
    enumExporter = Objects.requireNonNull(value, "EnumExporter may not be null!");
    LOG.debug("enumExporter {}..", getEnumExporter());
    return INSTANCE;
  }

  public Fluent useFixedFieldNames(String... overrideFieldNames) {
    this.fixedFieldNames = Objects.requireNonNull(overrideFieldNames, "Field names may not be null if you want to override them!");
    return INSTANCE;
  }

  public Fluent className(String fullClassName) {
    this.fullClassName = Objects.requireNonNull(fullClassName, "Class name may not be null!");
    return INSTANCE;
  }

  public Fluent convertConstantNames(NamingStrategy ns) {
    this.namingStrategyConstantNames = Objects.requireNonNull(ns, "NamingStrategy for constant names may not be null!");
    return INSTANCE;
  }

  public Fluent usingDefaultConstantNameConversion() {
    this.namingStrategyConstantNames = getDefaultNamingStrategyConstantNames();
    return INSTANCE;
  }

  public Fluent convertFieldNames(NamingStrategy ns) {
    this.namingStrategyFieldNames = Objects.requireNonNull(ns, "NamingStrategy for field names may not be null!");
    return INSTANCE;
  }

  public Fluent usingDefaultFieldNameConversion() {
    this.namingStrategyFieldNames = getDefaultNamingStrategyFieldNames();
    return INSTANCE;
  }

  public Fluent javaDocClassLevel(String... lines) {
    final ClassBuilder localClassBuilder = Objects.requireNonNull(getClassBuilder(), "classBuilder(ClassBuilder) must set before calling javaDocClassLevel(String...)!");
    Stream.of(lines).forEach(localClassBuilder::addClassJavadoc);
    return this;
  }

  public EnumResult build() {
    LOG.debug("build()..");

    final DataProvider localDataProvider = Objects.requireNonNull(getDataProvider(), "getDataProvider() returns a null DataProvider object!");
    final ClassBuilder localClassBuilder = Objects.requireNonNull(getClassBuilder(), "getClassBuilder() returns a null ClassBuilder object!");
    final SourceFormatter localSourceFormatter = Objects.requireNonNull(getSourceFormatter(), "getSourceFormatter() returns a null SourceFormatter object!");
    final EnumExporter localEnumExporter = Objects.requireNonNull(getEnumExporter(), "getEnumExporter() returns a null EnumExporter object!");

    LOG.debug("DataProvider: {}, ClassBuilder: {}, SourceFormatter: {}, EnumExporter: {}", localDataProvider, localClassBuilder, localSourceFormatter, localEnumExporter);

    final EnumData enumData = Objects.requireNonNull(localDataProvider.load(), "DataProvider.load() returns a null EnumData object!");

    localClassBuilder.withName(this.fullClassName);
    localClassBuilder.addClassJavadoc(enumData.getJavaDoc(EnumData.JDocKeys.CLASS.name()));

    if (this.fixedFieldNames != null) {
      //Overriding the field names usually fetched by the data provider implementation!
      enumData.setFieldNames(this.fixedFieldNames);
      LOG.debug("UsingFixedFieldNames: {}", Arrays.toString(enumData.getFieldNames()));
    }

    if (this.namingStrategyFieldNames != null) {
      localClassBuilder.convertFieldNames(Objects.requireNonNull(this.namingStrategyFieldNames, "NamingStrategy for field names is null!"));
    }

    if (this.namingStrategyConstantNames != null) {
      localClassBuilder.convertConstantNames(Objects.requireNonNull(this.namingStrategyConstantNames, "NamingStrategy for constant names is null!"));
    }

    //note that we use the fieldnames which are possibly been overriden!
    localClassBuilder.setFields(enumData.getFieldNames(), enumData.getFieldClasses());

    final List<NameTypeValue> mapData = Objects.requireNonNull(enumData.getData(), "EnumData has no records loaded! It's empty!");

    //add each data record to the classbuilder
    mapData.forEach((nameTypeValue) -> localClassBuilder.addField(nameTypeValue.getConstantName(), nameTypeValue.getValue()));

    String myEnum = getClassBuilder().build();

    EnumResult enumResult = EnumResult.of(enumData, getSourceFormatter(), myEnum);

    return getEnumExporter().export(enumResult);

  }

  /**
   * @return the dataProvider which is used to load the enum data.
   */
  public DataProvider getDataProvider() {
    return dataProvider;
  }

  public ClassBuilder getClassBuilder() {
    return classBuilder;
  }

  public SourceFormatter getSourceFormatter() {
    return sourceFormatter;
  }

  public EnumExporter getEnumExporter() {
    return enumExporter;
  }

  public static NamingStrategy getDefaultNamingStrategyConstantNames() {
    return minus2underline()
        .and(flattenGermanUmlauts())
        .and(space2underline())
        .and(replaceAtoZ())
        .and(removeProhibitedSpecials())
        .and(removeDots())
        .and(upperCase());
  }

  public static NamingStrategy getDefaultNamingStrategyFieldNames() {
    NamingStrategy ns1 = extractParenthesis();
    NamingStrategy ns2 = removeProhibitedSpecials();
    NamingStrategy ns3 = camelStrict();
    NamingStrategy ns4 = harmonize();
    NamingStrategy ns5 = lowerCaseFirstLetter();
    return ns1.and(ns2).and(ns3).and(ns4).and(ns5);
  }

}
