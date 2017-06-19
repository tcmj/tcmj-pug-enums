package com.tcmj.iso.exporter.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.iso.exporter.tools.MetaDataExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compiles and loads your given source enum at runtime. The compiled object will be loaded using
 * the current class loader.
 */
public class InMemoryCompilingExporter implements EnumExporter {
  /** slf4j Logging framework. */
  private static final Logger LOG = LoggerFactory.getLogger(InMemoryCompilingExporter.class);

  private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

  private EnumSet<? extends Enum> enumConstants;
  private Class<? extends Enum> enumClass;

  public EnumSet<? extends Enum> getEnumConstants() {
    return enumConstants;
  }

  public Class<? extends Enum> getEnumClass() {
    return enumClass;
  }

  @Override
  public String export(String data, Map<String, Object> options) {
    String className = MetaDataExtractor.getClassName(data);
    try {
      ClassLoader classLoader = compile(className, data);
      Class<? extends Enum> enumClass =
          (Class<? extends Enum>) Class.forName(className, true, classLoader);
      this.enumConstants = EnumSet.allOf(enumClass);
      this.enumClass = enumClass;
      LOG.debug("Result: {} : {}", enumClass, this.enumConstants);
    } catch (ClassNotFoundException e) {
      LOG.error("Cannot load enum class : '{}'", className, e);
      throw new ClassNotCompileAndLoadException(e);
    }
    return data;
  }

  private ClassLoader compile(String name, String src) {
    LOG.debug("Compiling enum: {}", name);
    MemClassLoader classLoader = new MemClassLoader();
    LOG.debug("Created a new MemClassLoader instance: {}", classLoader);
    try (JavaFileManager fileManager = new MemJavaFileManager(compiler, classLoader)) {
      JavaFileObject javaFile = new StringJavaFileObject(name, src);
      Collection<JavaFileObject> units = Collections.singleton(javaFile);
      JavaCompiler.CompilationTask task =
          compiler.getTask(null, fileManager, null, null, null, units);
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

  public class MemJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private final MemClassLoader classLoader;

    public MemJavaFileManager(JavaCompiler compiler, MemClassLoader classLoader) {
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

  class MemJavaFileObject extends SimpleJavaFileObject {
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

  public class MemClassLoader extends ClassLoader {
    private final Map<String, MemJavaFileObject> classFiles = new HashMap<>();

    public MemClassLoader() {
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

  public class StringJavaFileObject extends SimpleJavaFileObject {
    private final CharSequence code;

    public StringJavaFileObject(String name, CharSequence code) {
      super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
      this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return code;
    }
  }

  public static class ClassNotCompileAndLoadException extends RuntimeException {
    public ClassNotCompileAndLoadException(Throwable cause) {
      super(cause);
    }

    public ClassNotCompileAndLoadException(String text) {
      super(text);
    }
  }
}
