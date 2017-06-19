package com.tcmj.iso.builder.impl.format;

import com.google.googlejavaformat.java.Formatter;
import com.tcmj.pug.enums.api.SourceFormatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Wrapper for the Google source code formatter. */
public class GoogleFormatter implements SourceFormatter {
  private static final transient Logger LOG = LoggerFactory.getLogger(GoogleFormatter.class);
  private static Formatter googleFormatter;

  @Override
  public String format(String rawSource) {
    try {
      lazyInitialize();
      LOG.trace("SourceFormatter.format: {}", googleFormatter);
      String formattedSource = googleFormatter.formatSource(rawSource);
      formattedSource = StringUtils.replace(formattedSource, "\n\n", "\n");
      return formattedSource;
    } catch (Exception e) {
      LOG.error("Skipping Google Formatter!", e);
    }
    return rawSource;
  }

  private void lazyInitialize() {
    if (googleFormatter == null) {
      synchronized (GoogleFormatter.class) {
        if (googleFormatter == null) {
          googleFormatter = new Formatter();
        }
      }
    }
  }
}
