package com.tcmj.pug.enums.exporter.impl;

import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.EnumResult;
import com.tcmj.pug.enums.exporter.tools.MetaDataExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Compiles and loads your given source enum at runtime. The compiled object will be loaded using
 * the current class loader.
 */
public class InMemoryCompilingExporter implements EnumExporter {

  /** slf4j Logging framework. */
  private static final Logger LOG = LoggerFactory.getLogger(InMemoryCompilingExporter.class);

  /** Gets the Java programming language compiler provided with this platform. */
  private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

  private EnumSet<? extends Enum> enumConstants;

  private Class<? extends Enum> enumClass;

  /**
   * Enum value objects in form of a EnumSet.
   */
  public EnumSet<? extends Enum> getEnumConstants() {
    return enumConstants;
  }

  /**
   * Class object of the in memory compiled enum instance.
   */
  public Class<? extends Enum> getEnumClass() {
    return enumClass;
  }

  /**
   * Exports to a ClassLoader. Compiles the given enum source and loads it into a classloader.
   * Formatters are ignored and data is used from the getResult method directly.
   * The class name and package will be usually fetched from an existing EnumData object. In case
   * of not providing a EnumData object in the EnumResult object it will be tried to fetch missing data from the content.
   *
   * @param enumResult containing the content and EnumData object with a class name and package.
   * @return EnumResult containing payload
   */
  @Override
  public EnumResult export(EnumResult enumResult) {
    LOG.debug("InMemoryCompilingExporter.export({})...", enumResult);
    EnumResult localEnumResult = Objects.requireNonNull(enumResult, "Parameter EnumResult may not be null!");
    String data = Objects.requireNonNull(enumResult.getResult(), "EnumResult has no content!");
    String className;
    if (localEnumResult.getData() == null) {
      LOG.trace("Trying to build class name and package from content...");
      className = MetaDataExtractor.getClassName(enumResult.getResult());
    } else {
      LOG.trace("Exporting class name and package from EnumData...");
      className = MetaDataExtractor.getClassName(enumResult);
    }
    try {
      ClassLoader classLoader = compile(className, data);
      Class<? extends Enum> localEnumClass = (Class<? extends Enum>) Class.forName(className, true, classLoader);
      this.enumConstants = EnumSet.allOf(localEnumClass);
      this.enumClass = localEnumClass;
      LOG.debug("Result: {} : {}", this.enumClass, this.enumConstants);
    } catch (ClassNotFoundException e) {
      LOG.error("Cannot load enum class : '{}'", className, e);
      throw new ClassNotCompileAndLoadException(e);
    }
    return enumResult;
  }

  private ClassLoader compile(String name, String src) {
    LOG.debug("Compiling enum: {}", name);
    MemClassLoader classLoader = new MemClassLoader();
    LOG.debug("Created a new MemClassLoader instance: {}", classLoader);
    try (JavaFileManager fileManager = new MemJavaFileManager(compiler, classLoader)) {
      JavaFileObject javaFile = new StringJavaFileObject(name, src);
      Collection<JavaFileObject> units = Collections.singleton(javaFile);
      JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, units);
      LOG.debug("Starting CompilationTask: {} for {}", task, units);
      Boolean withoutErrors = task.call();
      if (withoutErrors) {
        LOG.debug("Compilation successfully finished without errors for enum: {}", name);
        return classLoader;
      } else {
        throw new ClassNotCompileAndLoadException("Compilation results in errors!");
      }
    } catch (Exception e) {
      throw new ClassNotCompileAndLoadException(e);
    }
  }

  static class MemJavaFileObject extends SimpleJavaFileObject {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(8192);
    private final String className;

    MemJavaFileObject(String className) {
      super(
          URI.create("string:///" + className.replace('.', '/') + Kind.CLASS.extension),
          Kind.CLASS);
      this.className = className;
    }

    String getClassName() {
      return className;
    }

    byte[] getClassBytes() {
      return outputStream.toByteArray();
    }

    @Override
    public OutputStream openOutputStream() {
      return outputStream;
    }
  }

  public static class MemClassLoader extends ClassLoader {
    private final Map<String, MemJavaFileObject> classFiles = new HashMap<>();

    MemClassLoader() {
      super(ClassLoader.getSystemClassLoader());
    }

    public void addClassFile(MemJavaFileObject memJavaFileObject) {
      classFiles.put(memJavaFileObject.getClassName(), memJavaFileObject);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
      MemJavaFileObject fileObject = classFiles.get(name);
      if (fileObject != null) {
        byte[] bytes = fileObject.getClassBytes();
        return defineClass(name, bytes, 0, bytes.length);
      }
      return super.findClass(name);
    }
  }

  public static class ClassNotCompileAndLoadException extends RuntimeException {
    ClassNotCompileAndLoadException(Throwable cause) {
      super(cause);
    }

    ClassNotCompileAndLoadException(String text) {
      super(text);
    }
  }

  public static class MemJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private final MemClassLoader classLoader;

    MemJavaFileManager(JavaCompiler compiler, MemClassLoader classLoader) {
      super(compiler.getStandardFileManager(null, null, null));
      this.classLoader = classLoader;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
      Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
      MemJavaFileObject fileObject = new MemJavaFileObject(className);
      classLoader.addClassFile(fileObject);
      return fileObject;
    }
  }

  public static class StringJavaFileObject extends SimpleJavaFileObject {
    private final CharSequence code;

    StringJavaFileObject(String name, CharSequence code) {
      super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
      this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return code;
    }
  }
}
