package com.tcmj.pug.enums.tools;

/**
 * Converts strings to camel case conform strings and also java properties to setter or getter
 * methods.
 *
 * @author Thomas Deutsch
 * @version $Revision: $
 */
public class CamelCase {

  /** Helper class with only static methods. */
  private CamelCase() {}

  /**
   * Converts any name to a java conform name (first letter capitalised). idea from middlegen's
   * dbnameconverter.
   *
   * <pre>
   * CamelCase.toCamelCase(null)                 = null
   * CamelCase.toCamelCase("")                   = ""
   * CamelCase.toCamelCase(" ")                  = ""
   * CamelCase.toCamelCase("one")                = "One"
   * CamelCase.toCamelCase("one_for_the_money")  = "OneForTheMoney"
   * CamelCase.toCamelCase("oneforthemoney")     = "Oneforthemoney"
   * CamelCase.toCamelCase("one-for-the-money")  = "OneForTheMoney"
   * CamelCase.toCamelCase("one for the money")  = "OneForTheMoney"
   * </pre>
   *
   * @param input the String to capitalise, may be null
   * @return capitalised String, {@code null} if null String input
   */
  public static CharSequence toCamelCase(CharSequence input) {
    if ("".equals(input) || input == null) {
      return input;
    }
    StringBuilder sb = new StringBuilder();

    boolean capitalize = true;
    boolean lastCapital = false;
    boolean lastDecapitalized = false;
    String p = null;

    for (int i = 0; i < input.length(); i++) {
      String c = input.toString().substring(i, i + 1);
      if ("_".equals(c) || " ".equals(c) || "-".equals(c)) {
        capitalize = true;
        continue;
      }

      if (c.toUpperCase().equals(c)) {
        if (lastDecapitalized && !lastCapital) {
          capitalize = true;
        }
        lastCapital = true;
      } else {
        lastCapital = false;
      }

      //if(forceFirstLetter && result.length()==0) capitalize = false;
      if (capitalize) {
        if (p == null || !p.equals("_")) {
          sb.append(c.toUpperCase());
          capitalize = false;
          p = c;
        } else {
          sb.append(c.toLowerCase());
          capitalize = false;
          p = c;
        }
      } else {
        sb.append(c.toLowerCase());
        lastDecapitalized = true;
        p = c;
      }
    }
    return sb.toString();
  }

  /**
   * Converts a string to a java conform get method name.
   *
   * <pre>
   * toGetter("ALL_IN_UPPER_CASE")   =  "getAllInUpperCase"
   * </pre>
   *
   * @param text the String to transform
   * @return getter String, {@code null} if null String input
   */
  public static CharSequence toGetter(CharSequence text) {
    return "get".concat(toCamelCase(text).toString());
  }

  /**
   * Converts a string to a java conform set method name.
   *
   * <pre>
   * toSetter("ALL_IN_UPPER_CASE")   =  "setAllInUpperCase"
   * </pre>
   *
   * @param text the String to transform
   * @return setter String, {@code null} if null String input
   */
  public static CharSequence toSetter(CharSequence text) {
    return "set".concat(toCamelCase(text).toString());
  }
}
