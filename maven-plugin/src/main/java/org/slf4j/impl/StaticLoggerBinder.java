package org.slf4j.impl;


import org.apache.maven.plugin.logging.Log;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * The binding of {@link LoggerFactory} class with an actual instance of
 * {@link ILoggerFactory} is performed using information returned by this class.
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {
  public static String REQUESTED_API_VERSION = "1.7"; // !final
  private static StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
  private final transient MavenSlf4jLoggerFactory loggers = new MavenSlf4jLoggerFactory();

  private StaticLoggerBinder() {
  }

  /** Urgently needed! this method will be called reflectively during initialization! */
  public static StaticLoggerBinder getSingleton() {
    return StaticLoggerBinder.SINGLETON;
  }

  @Override
  public ILoggerFactory getLoggerFactory() {
    return this.loggers;
  }

  @Override
  public String getLoggerFactoryClassStr() {
    return this.loggers.getClass().getName();
  }

  public void setMavenLog(final Log log) {
    this.loggers.setMavenLog(log);
  }
}
