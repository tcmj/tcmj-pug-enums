# tcmj-pug-enums
The tcmj pug enums project is a toolkit to create high quality java enum classes from several datasources like html pages or json data. 
A highlight of the project is our maven plugin which allows you to create a java enum class simply by configuration.
Our killer-use-case is to have a actual version of all iso-3166 countries ready in the form of a java enum.
<br/><br/>
***

## Where to start
* Wiki: https://github.com/tcmj/tcmj-pug-enums/wiki

***
<br/><br/>
## Example Input Data
Input data can be visited at https://en.wikipedia.org/wiki/ISO_3166-1#Officially_assigned_code_elements

## Example Output Result 
automagically created an enum containing 249 countries with no single line of java code - just configuration of the maven plugin

Have a look at the output: https://github.com/tcmj/tcmj-pug-enums/wiki/Example-Output-Result

<br/><br/>

# Modules of the **tcmj-pug-enums** project

## tcmj-pug-enums-api
* Public API interfaces of the framework and model classes including a fluent version.
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
<br/><br/>

# Current Release Version: 1.2.7.1
# Current Development Snapshot: 1.2.7.15-SNAPSHOT
# Next Release Version: 1.2.7.15 (not yet available)

Here you can grab the dependency configuration: https://github.com/tcmj/tcmj-pug-enums/wiki/Setup-Installation



# Contribution
You're welcome! 



