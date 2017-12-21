package com.tcmj.pug.enums.api.tools;

import com.tcmj.pug.enums.api.NamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides several NamingStrategy objects which can be chained together.
 *
 * @todo NamingStrategyConversion of constantName from='Bolivia (Plurinational State of)'
 * to='Boliviaplurinationalstateof'
 * @todo NamingStrategyConversion of constantName from='Bonaire, Sint Eustatius and Saba'
 * to='Bonairesinteustatiusandsaba'
 * @todo NamingStrategyConversion of constantName from='Bosnia and Herzegovina'
 * to='Bosniaandherzegovina'
 * @todo bei .harmonize darf das Space nicht weggenommen werden! Nur so kann ein weiterer NS
 * eingereiht werden!
 * <p>
 * pugproductions - 2017-05-22 - tcmj.
 */
public class NamingStrategyFactory {

  private static final transient Logger LOG = LoggerFactory.getLogger(NamingStrategyFactory.class);
  private static final NamingStrategy NO_RENAMING = value -> value;

  public static NamingStrategy getNoNamingStrategy() {
    return NO_RENAMING;
  }

  public static NamingStrategy lowerCase() {
    return value -> value == null ? null : value.toLowerCase();
  }
  public static NamingStrategy lowerCaseFirstLetter() {
    return value -> value == null ? null : Character.toLowerCase(value.charAt(0)) + value.substring(1);
  }

  public static NamingStrategy trim() {
    return value -> value == null ? null : value.trim();
  }

  public static NamingStrategy upperCase() {
    return value -> value == null ? null : value.toUpperCase();
  }

  public static NamingStrategy space2underline() {
    return value -> {
      if (value == null) {
        return null;
      }
      String tmp = value.replace(' ', '_'); //spaces -> underline
      while (tmp.contains("__")) {
        tmp = tmp.replaceAll("__", "_"); //remove duplicates
      }
      return tmp;
    };
  }

  public static NamingStrategy minus2underline() {
    return value -> value == null ? null : value.replace('-', '_');
  }

  public static NamingStrategy removeSpaces() {
    return value -> value == null ? null : value.replace(" ", "");
  }
  
  public static NamingStrategy removeDots() {
    return value -> value == null ? null : value.replace(".", "");
  }

  /**
   * simple camel case meaning that the first character will always uppered and each character after
   * a space or a underline will be uppered. All other characters will be ignored
   */
  public static NamingStrategy camel() {
    return value -> {
      if (value == null) {
        return null;
      }
      StringBuilder buffer = new StringBuilder();
      for (int i = 0; i < value.length();) {
        char current = value.charAt(i);
        if (i == 0) {
          buffer.append(Character.toUpperCase(current));
        } else if ((current == ' ' || current == '_') && i < value.length()) {
          char next = value.charAt(i + 1);
          buffer.append(Character.toUpperCase(next));
          i++;
        } else {
          buffer.append(current);
        }
        i++;
      }
      return buffer.toString();
    };
  }

  /**
   * strict camel case meaning that each character after the first uppered one will be strictly
   * lowered.
   */
  public static NamingStrategy camelStrict() {
    return value -> {
      if (value == null) {
        return null;
      }
      StringBuilder buffer = new StringBuilder();
      value = value.trim();
      for (int i = 0; i < value.length();) {
        char current = value.charAt(i);
        if (i == 0) {
          buffer.append(Character.toUpperCase(current));
        } else if ((current == ' ' || current == '_' || current == '-') && i < value.length()) {
          char next = value.charAt(i + 1);
          buffer.append(Character.toUpperCase(next));
          i++;
        } else {
          buffer.append(Character.toLowerCase(current));
        }
        i++;
      }
      return buffer.toString();
    };
  }

  /**
   * Amazing strategy which converts special characters (eg.: circumflex, acute, grave, ..) to its
   * latin derivative if possible and also removes all special characters which are non allowed to
   * be used in a java variable name.
   */
  public static NamingStrategy harmonize() {
    return value -> {
      if (value == null) {
        return null;
      }
      StringBuilder buffer = new StringBuilder();
      Pattern pattern = Pattern.compile("(.*?)(\\bSMALL|CAPITAL)( LETTER )([A-Z]{1})(.*)");
      for (int i = 0; i < value.length(); i++) {
        char current = value.charAt(i);
        int codePoint = value.codePointAt(i);
        if (current < 32) {
          LOG.debug("Removing control character '{}'({}): {}", current, codePoint, Character.getName(codePoint));
        } else if (current <= 47) {
          LOG.debug("Removing special character '{}'({}): {}", current, codePoint, Character.getName(codePoint));
        } else if (current <= 57) {
          LOG.trace("Leaving digit '{}'({}): {}", current, codePoint, Character.getName(codePoint));
          buffer.append(current);
        } else if (current <= 64) {
          LOG.debug("Removing special character '{}'({}): {}", current, codePoint, Character.getName(codePoint));
        } else if (current <= 90) { //A-Z
          buffer.append(current);
        } else if (current <= 96) { //[\]^_`
          LOG.debug("Removing special character '{}'({}): {}", current, codePoint, Character.getName(codePoint));
        } else if (current <= 122) { //a-z
          buffer.append(current);
        } else if (current <= 191) {
          LOG.debug("Removing special character '{}'({}): {}", current, codePoint, Character.getName(codePoint));
        } else if (current == 215 || current == 247 || current == 451 || current == 760) {
          LOG.debug("Removing very special character '{}'({}): {}", current, codePoint, Character.getName(codePoint));
        } else {
          String name = Character.getName(value.codePointAt(i));
          Matcher m = pattern.matcher(name);
          if (m.matches()) {
            String capital = m.group(2);
            String letter = m.group(4);
            String ourChar;
            if ("CAPITAL".equals(capital)) {
              ourChar = letter.toUpperCase();
            } else {
              ourChar = letter.toLowerCase();
            }
            buffer.append(ourChar);
            LOG.debug("Conversion of '{}'({}): {} to '{}'", current, codePoint, Character.getName(codePoint), ourChar);
          } else {
            LOG.debug("Leaving special '{}'({}): {}", current, codePoint, Character.getName(codePoint));
            buffer.append(current);
          }
        }
      }
      return buffer.toString();
    };
  }

  /**
   * Flattens german Umlauts.
   * <pre>ö -> oe</pre>
   * <pre>Ö -> OE</pre>
   * <pre>ä -> ae</pre>
   * <pre>ß -> ss</pre>
   *
   * @return
   */
  public static NamingStrategy flattenGermanUmlauts() {
    return value -> {
      if (value == null) {
        return null;
      }
      StringBuilder buffer = new StringBuilder();
      for (int i = 0; i < value.length(); i++) {
        char current = value.charAt(i);
        char next = (i + 1) < value.length() ? value.charAt(i + 1) : ' ';
        switch (current) {
          case 'Ö':
            if (Character.isLowerCase(next)) {
              buffer.append("Oe");
            } else {
              buffer.append("OE");
            }
            break;
          case 'ö':
            buffer.append("oe");
            break;
          case 'Ä':
            if (Character.isLowerCase(next)) {
              buffer.append("Ae");
            } else {
              buffer.append("AE");
            }
            break;
          case 'ä':
            buffer.append("ae");
            break;
          case 'Ü':
            if (Character.isLowerCase(next)) {
              buffer.append("Ue");
            } else {
              buffer.append("UE");
            }
            break;
          case 'ü':
            buffer.append("ue");
            break;
          case 'ß':
            buffer.append("ss");
            break;
          default:
            buffer.append(current);
        }
      }
      return buffer.toString();
    };
  }

  /**
   * Amazing strategy which converts special characters (eg.: circumflex, acute, grave, ..) to its
   * latin derivative if possible Special characters will be left alone.
   */
  public static NamingStrategy replaceAtoZ() {
    return value -> {
      if (value == null) {
        return null;
      }
      StringBuilder buffer = new StringBuilder();
      Pattern pattern = Pattern.compile("(.*?)(\\bSMALL|CAPITAL)( LETTER )([A-Z]{1})(.*)");
      for (int i = 0; i < value.length(); i++) {
        char current = value.charAt(i);
        int codePoint = value.codePointAt(i);
        if (current >= 192) {
          String name = Character.getName(value.codePointAt(i));
          Matcher m = pattern.matcher(name);
          if (m.matches()) {
            String capital = m.group(2);
            String letter = m.group(4);
            String replacementChar;
            if ("CAPITAL".equals(capital)) {
              replacementChar = letter.toUpperCase();
            } else {
              replacementChar = letter.toLowerCase();
            }
            buffer.append(replacementChar);
            LOG.debug("Conversion of '{}'({}): {} to '{}'", current, codePoint, Character.getName(codePoint), replacementChar);
          } else {
            LOG.debug("Leaving special '{}'({}): {}", current, codePoint, Character.getName(codePoint));
            buffer.append(current);
          }
        } else {
          LOG.debug("Leave of '{}'({}): {} ", current, codePoint, Character.getName(codePoint));
          buffer.append(current);
        }
      }
      return buffer.toString();
    };
  }

  /** Remove all special characters but space, minus, underscore and dot */
  public static NamingStrategy removeProhibitedSpecials() {
    return value -> {
      if (value == null) {
        return null;
      }
      StringBuilder buffer = new StringBuilder();
      for (int i = 0; i < value.length(); i++) {
        char current = value.charAt(i);
        int codePoint = value.codePointAt(i);
        if (current == 32 || current == 45 || current == 46 || current == 95) {
          LOG.debug("Leave character '{}'({}): {}", current, codePoint, Character.getName(codePoint));
          buffer.append(current);
        } else if ((current >= 48 && current <= 57)
            || (current >= 65 && current <= 90)
            || (current >= 97 && current <= 122)) { //A-Z
          buffer.append(current);
        }
      }
      return buffer.toString();
    };
  }

  /**
   * Bolivia (Plurinational State of) --> Plurinational State of Congo (Democratic Republic of the)
   * --> Democratic Republic of the Congo Korea (Democratic People's Republic of) --> Democratic
   * People's Republic of Korea
   */
  public static NamingStrategy extractParenthesis() {
    return value -> {
      if (value == null) {
        return null;
      }
      StringBuilder buffer = new StringBuilder();
      Pattern pattern = Pattern.compile("(.*)(\\b*) (\\()(.*)(\\))");

      Matcher m = pattern.matcher(value.trim());
      if (m.matches()) {
        String pre = m.group(1);
        String inner = m.group(4);

        for (int i = 0; i <= m.groupCount(); i++) {
          LOG.debug("{}='{}'", i, m.group(i));
        }
        buffer.append(inner);
        buffer.append(" ");
        buffer.append(pre);
        //
        //                    LOG.debug("1='{}'",   b);
        //                    LOG.debug("2='{}'", c);
        //                    LOG.debug("2='{}'", c);
        //                    LOG.debug("3='{}'   4='{}'    5='{}'", m.group(3), m.group(4), m.group(4));

        LOG.debug("=======");
        return buffer.toString();
      } else {
        return value;
      }
    };
  }
}
