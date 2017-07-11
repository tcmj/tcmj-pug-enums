package com.tcmj.pug.enums.exporter.impl;

import java.util.function.BiConsumer;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.EnumResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reports the exporter calls.
 *
 * <p>
 * You can choose between various possibilities to log
 *
 * <p>
 * Have a look on {@link LogLevel} e.g.:
 *
 * <ul>
 * <li>{@link LogLevel#INFO }
 * <li>{@link LogLevel#SYSTEM_OUT}
 * </ul>
 *
 * <p>
 * Mainly for debugging purpose.
 *
 * <pre>
 *    EnumExporter exporter1 = new ReportingEnumExporter(ReportingEnumExporter.LogLevel.ERROR);
 *    EnumExporter exporter2 = new ReportingEnumExporter(ReportingEnumExporter.LogLevel.WARN);
 *    EnumExporter exporter3 = new ReportingEnumExporter(ReportingEnumExporter.LogLevel.DEBUG);
 *
 *    exporter1.chain(exporter2).chain(exporter3).export(eResult);
 * </pre>
 */
public class ReportingEnumExporter implements EnumExporter {
  private static final Logger LOG = LoggerFactory.getLogger(ReportingEnumExporter.class);
  public static final String OPTION_LOG_LEVEL = "com.tcmj.pug.enums.exporter.ReportingEnumExporter.loglevel";

  public static enum LogLevel {
    DEBUG(LOG::debug),
    INFO(LOG::info),
    WARN(LOG::warn),
    ERROR(LOG::error),
    SYSTEM_OUT((pattern, objects) -> System.out.println(String.format(pattern.replace("{}", "%s"), objects))),
    SYSTEM_ERR((pattern, objects) -> System.err.println(String.format(pattern.replace("{}", "%s"), objects)));

    public BiConsumer<String, Object[]> getLogMethod() {
      return logMethod;
    }

    private final BiConsumer<String, Object[]> logMethod;

    LogLevel(BiConsumer<String, Object[]> logMethod) {
      this.logMethod = logMethod;
    }
  }

  private LogLevel currentLogLevel;

  public void setCurrentLogLevel(LogLevel currentLogLevel) {
    this.currentLogLevel = currentLogLevel;
  }

  public ReportingEnumExporter() {
  }

  public ReportingEnumExporter(String name) {
    String level = name.toUpperCase();
    this.currentLogLevel = LogLevel.valueOf(level);
  }

  public ReportingEnumExporter(LogLevel currentLogLevel) {
    this.currentLogLevel = currentLogLevel;
  }

  @Override
  public EnumResult export(EnumResult data) {
    if (currentLogLevel == null) {
      Object llevel = data.getOption(OPTION_LOG_LEVEL);
      if (llevel == null) {
        currentLogLevel = LogLevel.INFO;
      } else if (llevel instanceof String) {
        currentLogLevel = LogLevel.valueOf(((String) llevel).toUpperCase());
      } else if (llevel instanceof LogLevel) {
        currentLogLevel = (LogLevel) llevel;
      }
    }

    //call to the chosen logging method:
    currentLogLevel.getLogMethod().accept("{}", new Object[]{data.getResultFormatted()});

    return data; //if chaining is needed
  }

}
