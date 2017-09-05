package com.tcmj.pug.enums.api;

import com.tcmj.pug.enums.model.EnumData;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains EnumData, SourceFormatter and resulting Enum String.
 */
public class EnumResult {

  private final EnumData data;
  private final SourceFormatter formatter;
  private final Map<String, Object> options = new HashMap<>();

  private final String result;

  public static EnumResult of(EnumData data, SourceFormatter formatter, String result) {
    return new EnumResult(data, formatter, result);
  }

  public static EnumResult of(EnumData data, String result) {
    return new EnumResult(data, null, result);
  }

  public static EnumResult of(String result) {
    return new EnumResult(null, null, result);
  }

  public EnumResult addOption(String key, Object value) {
    options.put(key, value);
    return this;
  }

  public Object getOption(String key) {
    return options.get(key);
  }

  public Object getOption(String key, Object value) {
    return options.getOrDefault(key, value);
  }

  public EnumResult(EnumData data, SourceFormatter formatter, String result) {
    this.data = data;
    this.formatter = formatter;
    this.result = result;
  }

  /**
   * @return the data
   */
  public EnumData getData() {
    return data;
  }

  /**
   * @return the formatter
   */
  public SourceFormatter getFormatter() {
    return formatter;
  }

  /**
   * @return the result after applying an possibly available Formatter.
   */
  public String getResultFormatted() {
    if (getFormatter() != null) {
      return getFormatter().format(result);
    }
    return result;
  }

  /**
   * @return the result (without applying a Formatter).
   */
  public String getResult() {
    return result;
  }

}
