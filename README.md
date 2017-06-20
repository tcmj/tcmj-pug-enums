# tcmj-pug-enums
The tcmj pug enums project is a toolkit to create high quality java enum classes from any
datasources. It should be used if you need or want very actual data for example the list of
all countries including their iso codes or something like that. 

[TOC]


## tcmj-pug-enums-api
Public API interfaces of the whole framework and model classes including a fluent version.

## tcmj-pug-enums-datasources
Various implementations to load data from (Web pages, Json sources, CSV, ...).

## tcmj-pug-enums-builder
ClassBuilder implementations used to create the java source code files. At the moment we
have a plain StringBuilder version, a [JavaPoet](https://github.com/square/javapoet) version and a [CodeModel](https://mvnrepository.com/artifact/com.sun.codemodel/codemodel) variant

## tcmj-pug-enums-exporter
Export your data to file or compile directly to a class loader or simply report it to console (log)

## tcmj-pug-enums-maven-plugin
Amazing maven plugin which can be used to configure all things in a maven like style to get your enum ready! 



