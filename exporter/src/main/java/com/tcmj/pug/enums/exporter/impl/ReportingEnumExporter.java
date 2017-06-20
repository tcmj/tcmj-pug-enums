package com.tcmj.pug.enums.exporter.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import com.tcmj.pug.enums.api.EnumExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reports the exporter calls.
 *
 * <p>You can choose between various possibilities to log
 *
 * <p>Have a look on {@link LogLevel} e.g.:
 *
 * <ul>
 *   <li>{@link LogLevel#INFO }
 *   <li>{@link LogLevel#SYSTEM_OUT}
 * </ul>
 *
 * <p>Mainly for debugging purpose.
 *
 * <pre>
 *     EnumExporter exporter = new ReportingExporter();
 *     Map<String, Object> options = exporter.createOptions(ReportingEnumExporter.LogLevel.SYSTEM_OUT.name());
 *     exporter.export(data.getSimpleEnum(), options)
 * </pre>
 */
public class ReportingEnumExporter implements EnumExporter {
  private static final Logger LOG = LoggerFactory.getLogger(ReportingEnumExporter.class);
  public static final String OPTION_LOG_LEVEL =
      "com.tcmj.iso.exporter.ReportingEnumExporter.loglevel";

  public enum LogLevel {
    DEBUG(LOG::debug),
    INFO(LOG::info),
    WARN(LOG::warn),
    ERROR(LOG::error),
    SYSTEM_OUT(
        (pattern, objects) ->
            System.out.println(String.format(pattern.replace("{}", "%s"), objects))),
    SYSTEM_ERR(
        (pattern, objects) ->
            System.err.println(String.format(pattern.replace("{}", "%s"), objects)));

    public BiConsumer<String, Object[]> getLogMethod() {
      return logMethod;
    }

    private final BiConsumer<String, Object[]> logMethod;

    LogLevel(BiConsumer<String, Object[]> logMethod) {
      this.logMethod = logMethod;
    }
  }

  private LogLevel currentLogLevel = LogLevel.INFO;

  private String name;

  public ReportingEnumExporter() {}

  public ReportingEnumExporter(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + (this.name == null ? "()" : "(" + this.name + ")")
        + "@"
        + Integer.toHexString(hashCode());
  }

  @Override
  public String export(String data, Map<String, Object> options) {
    if (options != null && options.size() > 0) {
      String level = (String) options.get(OPTION_LOG_LEVEL);
      if (level != null && !"".equals(level)) {
        currentLogLevel = LogLevel.valueOf(level.toUpperCase());
      }
    }
    //call to the chosen logging method:
    currentLogLevel.getLogMethod().accept("{}", new Object[] {data});

    return data; //if chaining is needed
  }

  @Override
  public EnumExporter and(EnumExporter other, Map<String, Object> optz) {
    Objects.requireNonNull(other);
    return (source, options) -> {
      LOG.info("Chaining: [{}] and [{}]", this, other);
      return other.export(export(source, options), options);
    };
  }

  @Override
  public Map<String, Object> createOptions(String... logLvl) {
    String level = logLvl[0].toUpperCase();
    return createLogLevelOption(LogLevel.valueOf(level));
  }

  public static Map<String, Object> createLogLevelOption(LogLevel logLevel) {
    Map<String, Object> options = new HashMap<>();
    options.put(OPTION_LOG_LEVEL, logLevel.name());
    return options;
  }
}
