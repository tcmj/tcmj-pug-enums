package com.tcmj.pug.enums.mvn;

import org.apache.commons.lang3.StringUtils;

/** Common helping functions. */
public class LogFormatter {
  private static final String STARS = StringUtils.repeat("* ", 75);

  public static String arrange(String text) {
    String posedText = StringUtils.rightPad("  " + text, 143);
    return StringUtils.join("* *", posedText, "* *");
  }
  
  public static String encloseJavaDoc(String text) {
    String prefixed = StringUtils.prependIfMissing(text, "<div>");
    String suffixed = StringUtils.appendIfMissing(prefixed, "</div>");
    return suffixed+System.getProperty("line.separator");
  }

  public static String getLine() {
    return STARS;
  }
}
