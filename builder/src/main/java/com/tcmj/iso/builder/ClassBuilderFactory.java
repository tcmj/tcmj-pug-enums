package com.tcmj.iso.builder;


import com.tcmj.iso.api.ClassBuilder;
import com.tcmj.iso.api.SourceFormatter;
import com.tcmj.iso.builder.impl.CodeModelEnumBuilder;
import com.tcmj.iso.builder.impl.JavaPoetEnumBuilder;
import com.tcmj.iso.builder.impl.StringBufferEnumBuilder;
import com.tcmj.iso.builder.impl.format.CompressSpaces;
import com.tcmj.iso.builder.impl.format.ConvertTabsToSpaces;
import com.tcmj.iso.builder.impl.format.GoogleFormatter;
import com.tcmj.iso.builder.impl.format.NoFormatter;
import com.tcmj.iso.builder.impl.format.RemoveLineBreaks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory to access all {@link ClassBuilder} implementations.
 */
public class ClassBuilderFactory {

    private static final transient Logger LOG = LoggerFactory.getLogger(ClassBuilderFactory.class);
    private static final SourceFormatter NO_FORMATTER = new NoFormatter();

    /**
     * Simplest version of the enum class builder backed up by a StringBuilder.
     */
    public static ClassBuilder getEnumClassBuilder() {
        return new StringBufferEnumBuilder();
    }

    /**
     * Enum class builder implemented with https://codemodel.java.net/ from Suns Glassfish project.
     */
    public static ClassBuilder getCodeModelEnumBuilder() {
        return new CodeModelEnumBuilder();
    }

    /**
     * Enum class builder implemented with https://github.com/square/javapoet
     */
    public static ClassBuilder getJavaPoetEnumBuilder() {
        return new JavaPoetEnumBuilder();
    }

    /**
     * Dependent of the availability of the specific jars on the classpath the best choice will be made.
     */
    public static ClassBuilder getBestEnumBuilder() {
        try {
            return getJavaPoetEnumBuilder();
        } catch (Exception e) {
            LOG.trace("Stack", e);
            LOG.info("JavaPoet seems not to be on classpath! More infos at https://github.com/square/javapoet");
        }

        try {
            return getCodeModelEnumBuilder();
        } catch (Exception e) {
            LOG.trace("Stack", e);
            LOG.info("com.sun.codemodel seems not to be on classpath! More infos at https://codemodel.java.net/");
        }

        return getEnumClassBuilder();
    }

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
            LOG.info("com.google.googlejavaformat.java.Formatter seems not to be on classpath! More infos at https://github.com/google/google-java-format");
        }
        return getNoSourceCodeFormatter();
    }

}
