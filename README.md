# tcmj-pug-enums
The tcmj pug enums project is a toolkit to create high quality java enum classes from several datasources like html pages or json data. 
A highlight of the project is our maven plugin which allows you to create a java enum class simply by configuration.
Our killer-use-case is to have a actual version of all iso-3166 countries ready in the form of a java enum.


##Example Input Data
Input data can be visited at https://en.wikipedia.org/wiki/ISO_3166-1

##Example Output Result 
automagically creates an enum containing 249 countries

```java
package com.tcmj.iso3166;

/** 
 * ISO-3166 Countries  including alpha2, alpha3 and numeric code. 
 * Loaded from https://en.wikipedia.org/wiki/ISO_3166-1
 */
public enum Countries {
  AFGHANISTAN("AF", "AFG", "004"),
  ALANDISLANDS("AX", "ALA", "248"),
  ALBANIA("AL", "ALB", "008"),
  //..
  GERMANY("DE", "DEU", "276"),
  //..
  YEMEN("YE", "YEM", "887"),
  ZAMBIA("ZM", "ZMB", "894"),
  ZIMBABWE("ZW", "ZWE", "716");
 
  private final String alpha2;
  private final String alpha3;
  private final String numeric;

  Countries(String alpha2, String alpha3, String numeric) {
    this.alpha2 = alpha2;
    this.alpha3 = alpha3;
    this.numeric = numeric;
  }
  public String getAlpha2() {
    return this.alpha2;
  }
  public String getAlpha3() {
    return this.alpha3;
  }
  public String getNumeric() {
    return this.numeric;
  }
}
```

#Following a list of all modules of the **tcmj-pug-enums** project


## tcmj-pug-enums-api
* Public API interfaces of the whole framework and model classes including a fluent version.
* Implementing this interfaces you can create your very own 
 * datasources, exporters, enum classbuilders, naming strategies, source formatters..

## tcmj-pug-enums-datasources
* Various implementations to load data from (Web pages, Json sources, CSV, ...).

## tcmj-pug-enums-builder
* ClassBuilder implementations used to create the java source code files. At the moment we have a 
 * plain StringBuilder version, a [JavaPoet](https://github.com/square/javapoet) version and a [CodeModel](https://mvnrepository.com/artifact/com.sun.codemodel/codemodel) variant

## tcmj-pug-enums-exporter
* Export your data to file or compile directly to a class loader or simply report it to console (log)

## tcmj-pug-enums-maven-plugin
*  Amazing maven plugin which can be used to configure all things in a maven like style to get your enum ready! 




---
## Changelog
### Version 1.2.6.15
* First Release Version with a working version of all modules.

### Version 1.2.7.1
* Feature: Override Subfield Names #3
* Cosmetic: Change system parameter names #4
* Maven plugin does not work without subfields #5
* Ability to define javadoc on class level in maven-plugin #6



