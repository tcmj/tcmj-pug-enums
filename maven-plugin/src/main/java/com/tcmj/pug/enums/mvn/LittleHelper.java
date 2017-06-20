package com.tcmj.pug.enums.mvn;

import org.apache.commons.lang3.StringUtils;

/** Common helping functions. */
public class LittleHelper {
  private static final String STARS = StringUtils.repeat("* ", 75);

  public static String arrange(String text) {
    String posedText = StringUtils.rightPad("  " + text, 143);
    return StringUtils.join("* *".intern(), posedText, "* *".intern());
  }

  public static String getLine() {
    return STARS;
  }
}
