package com.tcmj.pug.enums.api;

import java.util.Map;
import java.util.Objects;
import com.tcmj.pug.enums.model.EnumData;
import com.tcmj.pug.enums.model.NameTypeValue;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fluent Enum Generator Builder. With this pattern all steps will be called in the correct order.
 * There are several ordered steps used to link the allowed actions together.
 */
public class Fluent {
  private static final Logger LOG = LoggerFactory.getLogger(Fluent.class);

  private static final Fluent INSTANCE = new Fluent();

  private final StepClassBuilder step2 = new MyStepClassBuilder();
  private final StepExporterOption step3 = new MyStepExporterOption();
  private final StepFormatterOption step4 = new MyStepFormatterOption();
  private final EGEndImpl end = new EGEndImpl();

  private Fluent() {
    //Fluent is not allowed to be instantiated in public
  }

  public static Fluent builder() {
    LOG.debug("Start building fluently...");
    return INSTANCE;
  }

  public class MyStepClassBuilder implements StepClassBuilder {
    @Override
    public EGEnd usingClassBuilder(ClassBuilder builder) {
      end.classBuilder = builder;
      return end;
    }
  }

  public class MyStepExporterOption implements StepExporterOption {
    @Override
    public StepFormatterOption convertConstantNames(NamingStrategy ns) {
      end.classBuilder.convertConstantNames(Objects.requireNonNull(ns, "NamingStrategy!"));
      return step4;
    }
    @Override
    public StepFormatterOption convertFieldNames(NamingStrategy ns) {
      end.classBuilder.convertFieldNames(Objects.requireNonNull(ns, "NamingStrategy!"));
      return step4;
    }
    @Override
    public StepFormatterOption exportWith(EnumExporter exporter) {
      end.enumExporter = Objects.requireNonNull(exporter, "EnumExporter!");
      return step4;
    }

    @Override
    public EGEnd exportWith(EnumExporter exporter, Map<String, Object> options) {
      end.enumExporter = Objects.requireNonNull(exporter, "EnumExporter!");
      end.enumExporterOptions = options;
      return step4;
    }

    @Override
    public EGEnd format(SourceFormatter formatter) {
      end.formatter = Objects.requireNonNull(formatter, "SourceFormatter!");
      LOG.debug("...using SourceFormatter: {}", end.formatter);
      return end;
    }
    
    @Override
    public EGEnd useFixedFieldNames(String[] fieldNames) {
      end.fieldNames = fieldNames;
      return end;
    }

    @Override
    public void end() {
      LOG.debug("...end reached (of step3)...");
      chain();
    }
  }

  public class MyStepFormatterOption implements StepFormatterOption {
    @Override
    public void end() {
      LOG.debug("...end reached (of step4)...");
      chain();
    }

    @Override
    public EGEnd convertConstantNames(NamingStrategy ns) {
      end.classBuilder.convertConstantNames(Objects.requireNonNull(ns, "NamingStrategy!"));
      return end;
    }

    @Override
    public EGEnd convertFieldNames(NamingStrategy ns) {
      end.classBuilder.convertFieldNames(Objects.requireNonNull(ns, "NamingStrategy!"));
      return end;
    }
    
    @Override
    public StepFormatterOption exportWith(EnumExporter exporter) {
      end.enumExporter = Objects.requireNonNull(exporter, "EnumExporter!");
      return step4;
    }

    @Override
    public EGEnd exportWith(EnumExporter exporter, Map<String, Object> options) {
      end.enumExporter = Objects.requireNonNull(exporter, "EnumExporter!");
      end.enumExporterOptions = options;
      return step4;
    }

    @Override
    public EGEnd format(SourceFormatter formatter) {
      end.formatter = Objects.requireNonNull(formatter, "SourceFormatter!");
      LOG.debug("...using SourceFormatter: {}", end.formatter);
      return end;
    }

    @Override
    public EGEnd useFixedFieldNames(String[] fieldNames) {
      end.fieldNames = fieldNames;
      return end;
    }
  }

  /** Implementation of the final step. */
  public class EGEndImpl implements EGEnd {
    private EnumData data;
    private DataProvider dataProvider;
    private ClassBuilder classBuilder;
    private SourceFormatter formatter;
    private EnumExporter enumExporter;
    private Map<String, Object> enumExporterOptions;
    private String[] fieldNames;

    
    public DataProvider getDataProvider() {
      return dataProvider;
    }

    public EnumData getEnumData() {
      return data;
    }

    public Map<String, Object> getEnumExporterOptions() {
      return enumExporterOptions;
    }

    public EnumExporter getEnumExporter() {
      return enumExporter;
    }

    public SourceFormatter getFormatter() {
      return formatter;
    }

    public ClassBuilder getClassBuilder() {
      return classBuilder;
    }

    @Override
    public StepFormatterOption exportWith(EnumExporter exporter) {
      end.enumExporter = Objects.requireNonNull(exporter, "EnumExporter");
      return step4;
    }

    @Override
    public EGEnd exportWith(EnumExporter exporter, Map<String, Object> options) {
      end.enumExporter = Objects.requireNonNull(exporter, "EnumExporter");
      end.enumExporterOptions = options;
      return step4;
    }

    @Override
    public void end() {
      LOG.debug("...end reached (of EGEndImpl)...");
      chain();
    }

    @Override
    public EGEnd convertConstantNames(NamingStrategy ns) {
      end.classBuilder.convertConstantNames(Objects.requireNonNull(ns, "NamingStrategy"));
      return end;
    }
    
    @Override
    public EGEnd convertFieldNames(NamingStrategy ns) {
      end.classBuilder.convertFieldNames(Objects.requireNonNull(ns, "NamingStrategy"));
      return end;
    }

    @Override
    public EGEnd format(SourceFormatter formatter) {
      end.formatter = Objects.requireNonNull(formatter, "SourceFormatter!");
      LOG.debug("...using SourceFormatter: {}", end.formatter);
      return end;
    }

    @Override
    public EGEnd useFixedFieldNames(String[] fieldNames) {
      this.fieldNames = fieldNames;
      return end;
    }
  }

  public StepClassBuilder fromDataSource(DataProvider dataProvider) {
    INSTANCE.end.dataProvider = Objects.requireNonNull(dataProvider, "DataProvider");
    LOG.debug("...fromDataSource({})...", end.getDataProvider());
    return step2;
  }

  /** Terminating interface, might also contain methods like execute(); */
  public interface EGEnd {
    void end();
    EGEnd convertConstantNames(NamingStrategy ns);
    EGEnd convertFieldNames(NamingStrategy ns);
    EGEnd useFixedFieldNames(String[] fieldNames);
    EGEnd format(SourceFormatter formatter);
    EGEnd exportWith(EnumExporter exporter);
    EGEnd exportWith(EnumExporter exporter, Map<String, Object> options);
  }

  public interface StepClassBuilder {
    EGEnd usingClassBuilder(ClassBuilder builder);
  }

  public interface StepExporterOption extends EGEnd {
    @Override
    StepFormatterOption convertConstantNames(NamingStrategy ns);
    @Override
    StepFormatterOption convertFieldNames(NamingStrategy ns);
    @Override
    StepFormatterOption exportWith(EnumExporter exporter);
    @Override
    EGEnd format(SourceFormatter formatter);
  }

  public interface StepFormatterOption extends EGEnd {}

  private void chain() {
    LOG.debug("...chaining all together and execute it...");

    end.data = Objects.requireNonNull(end.getDataProvider(), "DataProvider").load();
    final EnumData data = Objects.requireNonNull(end.getEnumData(), "EnumData object is null!");
    LOG.trace("PackageName: {} ClassName: {}", data.getPackageName(), data.getClassNameSimple());
    LOG.trace("DataProvider: {}", end.getDataProvider());
    
    if(end.fieldNames!=null){
      //Overriding the field names usually fetched by the data provider implementation!
      data.setFieldNames(end.fieldNames);
      LOG.trace("UsingFixedFieldNames: {}", Arrays.toString(data.getFieldNames()));
    }
      

    final ClassBuilder enumBuilder = Objects.requireNonNull(end.getClassBuilder(), "ClassBuilder");
    LOG.trace("ClassBuilder: {}", enumBuilder);

    enumBuilder.withName(data.getClassName());

    enumBuilder.addClassJavadoc(data.getJavaDoc(EnumData.JDocKeys.CLASS.name()));
    enumBuilder.setFields(data.getFieldNames(), data.getFieldClasses());

    final List<NameTypeValue> mapData =
        Objects.requireNonNull(data.getData(), "EnumData.Map is empty");

    mapData.forEach((nameTypeValue) -> {
      final String key = Objects.requireNonNull(nameTypeValue.getConstantName(), "EnumData.DataMap.Key");
      enumBuilder.addField(key, Objects.requireNonNull(nameTypeValue.getValue(), "NameTypeValue.Value"));
    });

    String myEnum = enumBuilder.build();

    if (end.getFormatter() != null) {
      LOG.trace("SourceFormatter: {}", end.getFormatter());
      myEnum = end.getFormatter().format(myEnum);
    }

    if (end.getEnumExporter() != null) {
      end.getEnumExporter().export(myEnum, end.getEnumExporterOptions());
    }

    LOG.info("Enum successfully created with {} characters!", myEnum.length());
  }
}
