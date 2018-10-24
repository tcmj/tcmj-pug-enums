package org.slf4j.impl;

import org.apache.maven.plugin.logging.Log;
import org.slf4j.Logger;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * Maven Routing Logger implementation of {@link Logger}.
 */
public class MavenSlf4jLogger extends MarkerIgnoringBase {
  private static final long serialVersionUID = 42L;
  private final transient Log mavenLog;

  MavenSlf4jLogger(final Log log) {
    super();
    this.mavenLog = log;
  }

  @Override
  public String getName() {
    return this.getClass().getName();
  }

  @Override
  public boolean isTraceEnabled() {
    return false;
  }

  @Override
  public void trace(final String msg) {
    this.mavenLog.debug(msg);
  }

  @Override
  public void trace(final String format, final Object arg) {
    this.mavenLog.debug(MessageFormatter.format(format, arg).getMessage());
  }

  @Override
  public void trace(final String format, final Object first, final Object second) {
    this.mavenLog.debug(MessageFormatter.format(format, first, second).getMessage());
  }

  @Override
  public void trace(final String format, final Object... array) {
    this.mavenLog.debug(MessageFormatter.format(format, array).getMessage());
  }

  @Override
  public void trace(final String msg, final Throwable thr) {
    this.mavenLog.debug(msg, thr);
  }

  @Override
  public boolean isDebugEnabled() {
    return true;
  }

  @Override
  public void debug(final String msg) {
    this.mavenLog.debug(msg);
  }

  @Override
  public void debug(final String format, final Object arg) {
    this.mavenLog.debug(MessageFormatter.format(format, arg).getMessage());
  }

  @Override
  public void debug(final String format, final Object first, final Object second) {
    this.mavenLog.debug(MessageFormatter.format(format, first, second).getMessage());
  }

  @Override
  public void debug(final String format, final Object... array) {
    this.mavenLog.debug(MessageFormatter.format(format, array).getMessage());
  }

  @Override
  public void debug(final String msg, final Throwable thr) {
    this.mavenLog.debug(msg, thr);
  }

  @Override
  public boolean isInfoEnabled() {
    return true;
  }

  @Override
  public void info(final String msg) {
    this.mavenLog.info(msg);
  }

  @Override
  public void info(final String format, final Object arg) {
    this.mavenLog.info(MessageFormatter.format(format, arg).getMessage());
  }

  @Override
  public void info(final String format, final Object first, final Object second) {
    this.mavenLog.info(MessageFormatter.format(format, first, second).getMessage());
  }

  @Override
  public void info(final String format, final Object... array) {
    this.mavenLog.info(MessageFormatter.format(format, array).getMessage());
  }

  @Override
  public void info(final String msg, final Throwable thr) {
    this.mavenLog.info(msg, thr);
  }

  @Override
  public boolean isWarnEnabled() {
    return true;
  }

  @Override
  public void warn(final String msg) {
    this.mavenLog.warn(msg);
  }

  @Override
  public void warn(final String format, final Object arg) {
    this.mavenLog.warn(MessageFormatter.format(format, arg).getMessage());
  }

  @Override
  public void warn(final String format, final Object... array) {
    this.mavenLog.warn(MessageFormatter.format(format, array).getMessage());
  }

  @Override
  public void warn(final String format, final Object first, final Object second) {
    this.mavenLog.warn(MessageFormatter.format(format, first, second).getMessage());
  }

  @Override
  public void warn(final String msg, final Throwable thr) {
    this.mavenLog.warn(msg, thr);
  }

  @Override
  public boolean isErrorEnabled() {
    return true;
  }

  @Override
  public void error(final String msg) {
    this.mavenLog.error(msg);
  }

  @Override
  public void error(final String format, final Object arg) {
    this.mavenLog.error(MessageFormatter.format(format, arg).getMessage());
  }

  @Override
  public void error(final String format, final Object first, final Object second) {
    this.mavenLog.error(MessageFormatter.format(format, first, second).getMessage());
  }

  @Override
  public void error(final String format, final Object... array) {
    this.mavenLog.error(MessageFormatter.format(format, array).getMessage());
  }

  @Override
  public void error(final String msg, final Throwable thr) {
    this.mavenLog.error(msg, thr);
  }
}
