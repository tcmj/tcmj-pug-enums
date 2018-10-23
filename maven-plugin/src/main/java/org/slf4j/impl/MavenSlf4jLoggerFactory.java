package org.slf4j.impl;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * MavenSlf4jLoggerFactory is an trivial implementation of {@link ILoggerFactory}.
 */
public class MavenSlf4jLoggerFactory implements ILoggerFactory {
  private transient Log mavenLog = new SystemStreamLog();

  @Override
  public Logger getLogger(final String name) {
    return new MavenSlf4jLogger(this.mavenLog);
  }

  public void setMavenLog(final Log log) {
    synchronized (MavenSlf4jLoggerFactory.class) {
      this.mavenLog = log;
    }
  }

}
