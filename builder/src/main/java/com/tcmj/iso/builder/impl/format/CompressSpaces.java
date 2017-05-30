package com.tcmj.iso.builder.impl.format;

import com.tcmj.iso.api.SourceFormatter;

/**
 * Every couple of joined spaces will be set to a single space.
 */
public class CompressSpaces implements SourceFormatter {
    private static final String SPACE = " ";
    @Override public String format(String source) {
        if (source != null) {
            String result = source.replace("     ", SPACE);
            result = result.replace("    ", SPACE);
            result = result.replace("   ", SPACE);
            result = result.replace("  ", SPACE);
            return result;
        }
        return source;
    }
}
