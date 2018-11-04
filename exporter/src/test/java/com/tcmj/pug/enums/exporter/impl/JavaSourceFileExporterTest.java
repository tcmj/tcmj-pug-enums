package com.tcmj.pug.enums.exporter.impl;

import com.tcmj.pug.enums.api.EnumExporter;
import com.tcmj.pug.enums.api.EnumResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/** Unit Tests and Usage Examples of {@link JavaSourceFileExporter}. */
public class JavaSourceFileExporterTest {

  TestDataProvider data = new TestDataProvider();

  static Path testFiles;

  @BeforeClass
  public static void setupOnce() throws Exception {
    Path prefixPath = Paths.get(System.getProperty("java.io.tmpdir"));
    testFiles = Files.createTempDirectory(prefixPath, "tcmjUnitTests");
    System.out.println("Writing Test Files to: " + testFiles);
  }

  @AfterClass
  public static void cleanUp() throws Exception {
    rm(testFiles);
  }

  public static void rm(Path path) throws Exception {
    try {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
          if (e == null) {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
          } else {
            throw e;
          }
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void shouldWorkUsingGlobalOptions() {
    EnumResult er = EnumResult.of(data.getExtendedEnum()).addOption(JavaSourceFileExporter.OPTION_EXPORT_PATH_PREFIX, testFiles.toString());
    JavaSourceFileExporter exporter = new JavaSourceFileExporter();
    exporter.export(er);
    Path exportPath = Paths.get(testFiles.toString(), "org/Animal.java");
    assertThat("File may not exist?", Files.exists(exportPath), is(Boolean.TRUE));
    assertThat("AbsoluteExistingFileSet", er.optionExists(JavaSourceFileExporter.OPTION_RESULT_PATH), is(Boolean.TRUE));
  }

  @Test
  public void testExportSuccessful() {
    //given is a String containing a valid java enum object
    String pckg = "com.tcmj.iso.exporter.impl";
    String name = "Writers";
    String source = data.getEnumNamed(pckg, name);

    //when exporting
    EnumExporter exporter = new JavaSourceFileExporter();
    EnumResult enumResult = EnumResult.of(source);
    enumResult.addOption(JavaSourceFileExporter.OPTION_EXPORT_PATH_PREFIX, testFiles.toString());
    exporter.export(enumResult);

    //then we expect a written file and no exception
    String fullPath = "com/tcmj/iso/exporter/impl/Writers.java";
    Path exportPath = Paths.get(testFiles.toString(), fullPath);

    assertThat("Files may not exist?", Files.exists(exportPath), is(Boolean.TRUE));
    assertThat("AbsoluteExistingFileSet", enumResult.optionExists(JavaSourceFileExporter.OPTION_RESULT_PATH), is(Boolean.TRUE));
  }

  @Test
  public void shouldExportWithoutOptions() throws Exception {
    try { //when exporting...
      EnumResult enumResult = EnumResult.of(data.getSimpleEnum());
      //without setting this option: enumResult.addOption(JavaSourceFileExporter.OPTION_EXPORT_PATH_PREFIX, testFiles.toString());
      new JavaSourceFileExporter().export(enumResult);
      //we expect exporting to working dir extracting file name and package-directories from the content:
      assertThat("Files may not exist?", Files.exists(Paths.get(".", "one/two/three/SimpleEnum.java")), is(Boolean.TRUE));
      assertThat("AbsoluteExistingFileSet", enumResult.optionExists(JavaSourceFileExporter.OPTION_RESULT_PATH), is(Boolean.TRUE));
    } catch (Exception e) {
      fail("We don't want a Exception at this point: " + e.getMessage());
    } finally {
      rm(Paths.get(".", "one"));
    }
  }

  @Test(expected = JavaSourceFileExporter.JavaFileHasNotBeenCreatedException.class)
  public void testExportWithInvalidOptions() {
    EnumExporter exporter = new JavaSourceFileExporter();
    EnumResult enumResult = EnumResult.of(data.getSimpleEnum());
    enumResult.addOption(JavaSourceFileExporter.OPTION_EXPORT_PATH_PREFIX, "::;;§$\\\\\\%§$%M;,§$% def;ectiveP,ath $§§");
    exporter.export(enumResult);
  }
}
