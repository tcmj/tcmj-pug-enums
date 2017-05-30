package com.tcmj.iso.builder.impl.format;

import com.tcmj.iso.api.SourceFormatter;

/**
 * Replace a tabulator character (\t) to a single space.
 */
public class ConvertTabsToSpaces implements SourceFormatter {
    @Override public String format(String source) {
        if (source != null) {
            return source.replace('\t',' ');
        }
        return source;
    }
}
