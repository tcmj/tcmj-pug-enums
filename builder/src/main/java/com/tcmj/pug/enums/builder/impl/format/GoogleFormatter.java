package com.tcmj.pug.enums.builder.impl.format;

import com.google.googlejavaformat.java.Formatter;
import com.tcmj.pug.enums.api.SourceFormatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Wrapper for the Google source code formatter. */
public class GoogleFormatter implements SourceFormatter {
  private static final transient Logger LOG = LoggerFactory.getLogger(GoogleFormatter.class);
  private static final Formatter googleFormatter = new Formatter();
  ;

  @Override
  public String format(String rawSource) {
    try {
      LOG.trace("SourceFormatter.format: {}", googleFormatter);
      String formattedSource = googleFormatter.formatSource(rawSource);
      formattedSource = StringUtils.replace(formattedSource, "\n\n", "\n");
      return formattedSource;
    } catch (Exception e) {
      LOG.error("Skipping Google Formatter!", e);
    }
    return rawSource;
  }
}
