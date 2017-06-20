package com.tcmj.pug.enums.builder.impl.format;

import com.tcmj.pug.enums.api.SourceFormatter;

/** Dummy which does nothing but pass the raw String through unchanged. */
public class NoFormatter implements SourceFormatter {
  @Override
  public String format(String rawSource) {
    return rawSource;
  }
}
