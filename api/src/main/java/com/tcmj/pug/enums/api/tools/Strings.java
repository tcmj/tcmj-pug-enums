package com.tcmj.pug.enums.api.tools;

/**
 * Small static helper methods to remove dependencies on some external libs.
 */
public final class Strings {

  private Strings() {
  }

  public static boolean isWhitespace(final char value) {
    return Character.isWhitespace(value) || value == '\u00A0' || value == '\u2007' || value == '\u202F';
  }

  public static String replaceSpaces(final String value) {
    return replaceSpaces(value, new char[]{'\u00A0', '\u2007', '\u202F'}, ' ');
  }

  /**
   * Convert all non-printable characters with a space char.
   *
   * @return see unit tests
   */
  public static String replaceAllWhitespace(final String value) {
    final char SPACE = ' ';
    return replaceSpaces(value, new char[]{'\t', '\f', '\t', '\n', '\u00A0', '\u2007', '\u202F'}, SPACE);
  }

  public static String replaceAllWhitespace(final String value, final char with) {
    return replaceSpaces(value, new char[]{'\t', '\f', '\t', '\n', '\u00A0', '\u2007', '\u202F'}, with);
  }

  private static String replaceSpaces(final String value, final char[] toRemove, final char replacement) {
    if (value == null) {
      return null;
    }
    StringBuilder tmp = new StringBuilder();
    loopThrougTheWord:
    for (int len = value.length(), i = 0; i < len; i++) {
      char current = value.charAt(i);
      for (char remove : toRemove) {
        if (current == remove) {
          tmp.append(replacement);
          continue loopThrougTheWord;
        }
      }
      tmp.append(current);

    }
    return tmp.toString();
  }

  public static boolean isNotWhitespace(char value) {
    return !isWhitespace(value);
  }

  public static String removeWhitespace(String value) {
    if (value == null) {
      return null;
    }
    StringBuilder tmp = new StringBuilder();
    for (int len = value.length(), i = 0; i < len; i++) {
      char currentValue = value.charAt(i);
      if (isNotWhitespace(currentValue)) {
        tmp.append(currentValue);
      }
    }
    return tmp.toString();
  }

}