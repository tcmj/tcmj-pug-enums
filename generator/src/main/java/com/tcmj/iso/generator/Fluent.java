package com.tcmj.iso.generator;

import java.util.Map;
import java.util.Objects;
import com.tcmj.iso.api.ClassBuilder;
import com.tcmj.iso.api.DataProvider;
import com.tcmj.iso.api.EnumExporter;
import com.tcmj.iso.api.NamingStrategy;
import com.tcmj.iso.api.SourceFormatter;
import com.tcmj.iso.api.model.EnumData;
import com.tcmj.iso.api.model.NameTypeValue;
import com.tcmj.iso.builder.ClassBuilderFactory;
import com.tcmj.iso.builder.NamingStrategyFactory;
import java.io.UnsupportedEncodingException;
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

    @Override
    public StepExporterOption usingDefaultClassBuilder() {
      end.classBuilder = ClassBuilderFactory.getBestEnumBuilder();
      LOG.debug("...using default/best available ClassBuilder: {}", end.classBuilder);
      end.classBuilder.usingNamingStrategy(NamingStrategyFactory.harmonize());
      return step3;
    }
  }

  public class MyStepExporterOption implements StepExporterOption {
    @Override
    public StepFormatterOption usingNamingStrategy(NamingStrategy ns) {
      end.classBuilder.usingNamingStrategy(Objects.requireNonNull(ns, "NamingStrategy!"));
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
    public EGEnd usingNamingStrategy(NamingStrategy ns) {
      end.classBuilder.usingNamingStrategy(Objects.requireNonNull(ns, "NamingStrategy!"));
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
  }

  /** Implementation of the final step. */
  public class EGEndImpl implements EGEnd {
    private EnumData data;
    private DataProvider dataProvider;
    private ClassBuilder classBuilder;
    private SourceFormatter formatter;
    private EnumExporter enumExporter;
    private Map<String, Object> enumExporterOptions;

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
    public EGEnd usingNamingStrategy(NamingStrategy ns) {
      end.classBuilder.usingNamingStrategy(Objects.requireNonNull(ns, "NamingStrategy"));
      return end;
    }

    @Override
    public EGEnd format(SourceFormatter formatter) {
      end.formatter = Objects.requireNonNull(formatter, "SourceFormatter!");
      LOG.debug("...using SourceFormatter: {}", end.formatter);
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

    EGEnd usingNamingStrategy(NamingStrategy ns);

    EGEnd format(SourceFormatter formatter);

    EGEnd exportWith(EnumExporter exporter);

    EGEnd exportWith(EnumExporter exporter, Map<String, Object> options);
  }

  public interface StepClassBuilder {
    EGEnd usingClassBuilder(ClassBuilder builder);

    StepExporterOption usingDefaultClassBuilder();
  }

  public interface StepExporterOption extends EGEnd {
    @Override
    StepFormatterOption usingNamingStrategy(NamingStrategy ns);

    @Override
    StepFormatterOption exportWith(EnumExporter exporter);

    @Override
    EGEnd format(SourceFormatter formatter);
  }

  public interface StepFormatterOption extends EGEnd {}

  private void chain() {
    LOG.debug("...chaining all together and execute it...");

    end.data = Objects.requireNonNull(end.getDataProvider(), "DataProvider").load();
    final EnumData data = Objects.requireNonNull(end.getEnumData(), "EnumData");
    LOG.trace("PackageName: {} ClassName: {}", data.getPackageName(), data.getClassNameSimple());
    LOG.trace("DataProvider: {}", end.getDataProvider());

    ClassBuilder enumBuilder = Objects.requireNonNull(end.getClassBuilder(), "ClassBuilder");
    LOG.trace("ClassBuilder: {}", enumBuilder);

    enumBuilder.withName(data.getClassName());

    enumBuilder.addClassJavadoc(data.getJavaDoc(EnumData.JDocKeys.CLASS.name()));
    enumBuilder.setFields(data.getFieldNames(), data.getFieldClasses());

    final Map<String, NameTypeValue> mapData =
        Objects.requireNonNull(data.getData(), "EnumData.Map is empty");

    for (Map.Entry<String, NameTypeValue> entry : mapData.entrySet()) {

      final String key = Objects.requireNonNull(entry.getKey(), "EnumData.DataMap.Key");

      final NameTypeValue nameTypeValue =
          Objects.requireNonNull(entry.getValue(), "EnumData.DataMap.NameTypeValue");

      enumBuilder.addField(
          key, Objects.requireNonNull(nameTypeValue.getValue(), "NameTypeValue.Value"));
    }

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
