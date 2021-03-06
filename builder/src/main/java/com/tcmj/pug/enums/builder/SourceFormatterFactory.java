package com.tcmj.pug.enums.builder;

import com.tcmj.pug.enums.api.SourceFormatter;
import com.tcmj.pug.enums.builder.impl.format.CompressSpaces;
import com.tcmj.pug.enums.builder.impl.format.ConvertTabsToSpaces;
import com.tcmj.pug.enums.builder.impl.format.GoogleFormatter;
import com.tcmj.pug.enums.builder.impl.format.NoFormatter;
import com.tcmj.pug.enums.builder.impl.format.RemoveLineBreaks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Factory to access most {@link SourceFormatter} implementations. */
public class SourceFormatterFactory {

  private static final transient Logger LOG = LoggerFactory.getLogger(SourceFormatterFactory.class);
  private static final SourceFormatter NO_FORMATTER = new NoFormatter();

  public static SourceFormatter getNoLineBreaksSourceCodeFormatter() {
    return new ConvertTabsToSpaces().and(new RemoveLineBreaks()).and(new CompressSpaces());
  }

  public static SourceFormatter getNoSourceCodeFormatter() {
    return NO_FORMATTER;
  }

  public static SourceFormatter getGoogleSourceCodeFormatter() {
    return new GoogleFormatter();
  }

  public static SourceFormatter getBestSourceCodeFormatter() {
    try {
      return getGoogleSourceCodeFormatter();
    } catch (Exception e) {
      LOG.trace("Stack", e);
      LOG.info(
          "com.google.googlejavaformat.java.Formatter seems not to be on classpath! More infos at https://github.com/google/google-java-format");
    }
    return getNoSourceCodeFormatter();
  }
}
