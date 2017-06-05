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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fluent Enum Generator Builder.
 * With this pattern all steps will be called in the correct order.
 */
public class Fluent {
    private static final Logger LOG = LoggerFactory.getLogger(Fluent.class);
    private static Fluent instance = new Fluent();
    private StepClassBuilder step2 = new MyStepClassBuilder();
    private StepExporterOption step3 = new MyStepExporterOption();
    private StepFormatterOption step4 = new MyStepFormatterOption();
    private EGEndImpl end = new EGEndImpl();


    public static Fluent builder() {
        LOG.debug("Start building fluently...");
        return instance;
    }

    public class MyStepClassBuilder implements StepClassBuilder {
        @Override public EGEnd usingClassBuilder(ClassBuilder builder) {
            end.classBuilder = builder;
            return end;
        }
        @Override public StepExporterOption usingDefaultClassBuilder() {
            end.classBuilder = ClassBuilderFactory.getBestEnumBuilder();
            LOG.debug("...using default/best available ClassBuilder: {}", end.classBuilder);
            end.classBuilder.usingNamingStrategy(NamingStrategyFactory.harmonize());
            return step3;
        }
    }

    public class MyStepExporterOption implements StepExporterOption {
        @Override public StepFormatterOption usingNamingStrategy(NamingStrategy ns) {
            end.classBuilder.usingNamingStrategy(Objects.requireNonNull(ns, "Null not allowed as NamingStrategy!"));
            return step4;
        }
        @Override public StepFormatterOption exportWith(EnumExporter exporter) {
            end.enumExporter = Objects.requireNonNull(exporter, "Null not allowed as EnumExporter!");
            return step4;
        }
        @Override public EGEnd exportWith(EnumExporter exporter, Map<String, Object> options) {
            end.enumExporter = Objects.requireNonNull(exporter, "Null not allowed as EnumExporter!");
            end.enumExporterOptions = options;
            return step4;
        }
        @Override public EGEnd format(SourceFormatter formatter) {
            end.formatter = Objects.requireNonNull(formatter, "Null not allowed as SourceFormatter!");
            LOG.debug("...using SourceFormatter: {}", end.formatter);
            return end;
        }
        @Override public void end() {
            LOG.debug("...end reached (of step3)...");
            chain();
        }
    }

    public class MyStepFormatterOption implements StepFormatterOption {
        @Override public void end() {
            LOG.debug("...end reached (of step4)...");
            chain();
        }
        @Override public EGEnd usingNamingStrategy(NamingStrategy ns) {
            end.classBuilder.usingNamingStrategy(Objects.requireNonNull(ns, "Null not allowed as NamingStrategy!"));
            return end;
        }
        @Override public StepFormatterOption exportWith(EnumExporter exporter) {
            end.enumExporter = Objects.requireNonNull(exporter, "Null not allowed as EnumExporter!");
            return step4;
        }
        @Override public EGEnd exportWith(EnumExporter exporter, Map<String, Object> options) {
            end.enumExporter = Objects.requireNonNull(exporter, "Null not allowed as EnumExporter!");
            end.enumExporterOptions = options;
            return step4;
        }
        @Override public EGEnd format(SourceFormatter formatter) {
            end.formatter = Objects.requireNonNull(formatter, "Null not allowed as SourceFormatter!");
            LOG.debug("...using SourceFormatter: {}", end.formatter);
            return end;
        }
    }

    /**
     * Implementation of the final step.
     */
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

        public EnumData getData() {
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
        @Override public StepFormatterOption exportWith(EnumExporter exporter) {
            end.enumExporter = Objects.requireNonNull(exporter, "Null not allowed as EnumExporter!");
            return step4;
        }

        @Override public EGEnd exportWith(EnumExporter exporter, Map<String, Object> options) {
            end.enumExporter = Objects.requireNonNull(exporter, "Null not allowed as EnumExporter!");
            end.enumExporterOptions = options;
            return step4;
        }

        @Override public void end() {
            LOG.debug("...end reached (of EGEndImpl)...");
            chain();
        }

        @Override public EGEnd usingNamingStrategy(NamingStrategy ns) {
            end.classBuilder.usingNamingStrategy(Objects.requireNonNull(ns, "Null not allowed as NamingStrategy!"));
            return end;
        }

        @Override public EGEnd format(SourceFormatter formatter) {
            end.formatter = Objects.requireNonNull(formatter, "Null not allowed as SourceFormatter!");
            LOG.debug("...using SourceFormatter: {}", end.formatter);
            return end;
        }
    }

    public StepClassBuilder fromDataSource(DataProvider dataProvider) {
        instance.end.dataProvider = Objects.requireNonNull(dataProvider, "DataProvider cannot be null!");
        LOG.debug("...fromDataSource({})...", end.getDataProvider());
        return step2;
    }



    // Terminating interface, might also contain methods like execute();
    public interface EGEnd {
        void end();
        EGEnd usingNamingStrategy(NamingStrategy ns);
        EGEnd format(SourceFormatter formatter);
        EGEnd exportWith(EnumExporter exporter);
        EGEnd exportWith(EnumExporter exporter,  Map<String, Object> options);
    }

    public interface StepClassBuilder {
        EGEnd usingClassBuilder(ClassBuilder builder);
        StepExporterOption usingDefaultClassBuilder();
    }

    public interface StepExporterOption extends EGEnd {
        StepFormatterOption usingNamingStrategy(NamingStrategy ns);
        StepFormatterOption exportWith(EnumExporter exporter);
        EGEnd format(SourceFormatter formatter);
    }

    public interface StepFormatterOption extends EGEnd {

    }

    public void chain() {
        LOG.debug("...chaining all together and execute it...");
        try {
            end.data = end.getDataProvider().load();
            LOG.trace("++PackageName: {} ClassName: {}", end.getData().getPackageName() , end.getData().getClassNameSimple());
            LOG.trace("++DataProvider: {}", end.getDataProvider());

            ClassBuilder enumBuilder = end.getClassBuilder();
            LOG.trace("++ClassBuilder: {}", enumBuilder);

            enumBuilder.withName(end.getData().getClassName());

            enumBuilder.addClassJavadoc(end.getData().getJavaDoc(EnumData.JDocKeys.CLASS.name()));
            enumBuilder.setFields(end.getData().getFieldNames(), end.getData().getFieldClasses());
            for (Map.Entry<String, NameTypeValue> entry : end.getData().getData().entrySet()) {
                enumBuilder.addField(entry.getKey(), entry.getValue().getValue());
            }

            String myEnum = enumBuilder.build();

            if (end.getFormatter() != null) {
                LOG.trace("+SourceFormatter found: {}", end.getFormatter());
                myEnum = end.getFormatter().format(myEnum);
            }


            if(end.getEnumExporter()!=null){
                end.getEnumExporter().export(myEnum, end.getEnumExporterOptions());
            }

            LOG.info("Enum created with {} bytes!", myEnum.getBytes("UTF-8").length);
            LOG.info("...finished!");



        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
