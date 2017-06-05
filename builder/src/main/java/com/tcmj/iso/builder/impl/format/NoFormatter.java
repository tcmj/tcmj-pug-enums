package com.tcmj.iso.builder.impl.format;

import com.tcmj.iso.api.SourceFormatter;

/** Dummy which does nothing but pass the raw String through unchanged. */
public class NoFormatter implements SourceFormatter {
  @Override
  public String format(String rawSource) {
    return rawSource;
  }
}
