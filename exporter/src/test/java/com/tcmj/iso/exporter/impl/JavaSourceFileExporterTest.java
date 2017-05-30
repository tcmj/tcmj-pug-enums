package com.tcmj.iso.exporter.impl;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import com.tcmj.iso.api.EnumExporter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Unit Tests and Usage Examples of {@link JavaSourceFileExporter}.
 */
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
        try {
            Files.walkFileTree(testFiles, new SimpleFileVisitor<Path>() {
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
    public void testExportSuccessful() throws Exception {
        //given is a String containing a valid java enum object
        String pckg = "com.tcmj.iso.exporter.impl";
        String name = "Writers";
        String source = data.getEnumNamed(pckg, name);

        //when exporting
        EnumExporter exporter = new JavaSourceFileExporter();
        Map<String, Object> options = exporter.createOptions(testFiles.toString());
        exporter.export(source, options);

        //then we expect a written file and no exception
        String fullPath = "com/tcmj/iso/exporter/impl/Writers.java";
        Path exportPath = Paths.get(testFiles.toString(), fullPath);

        assertThat("Files may not exist?", Files.exists(exportPath), is(Boolean.TRUE));

    }

    @Test
    public void testExportWithNullOptions() throws Exception {
        try { //when exporting...
            new JavaSourceFileExporter().export(data.getSimpleEnum(), null);
            fail(" we expect a exception");
        } catch (Exception e) {
            assertThat("ErrorText", e.getMessage(), equalTo("Your options seems to be null! Please provide a path for the export!"));
        }
    }

    @Test
    public void shouldExportToFileAsOneLiner() throws Exception {
        new JavaSourceFileExporter().export(data.getUnformatedEnum(), JavaSourceFileExporter.createExportPathOptions(testFiles));

        //validate com.tcmj.iso
        Path exportPath = Paths.get(testFiles.toString(), "com/tcmj/iso/UnFormat.java");
        assertThat("File may not exist?", Files.exists(exportPath), is(Boolean.TRUE));

    }

    @Test(expected = JavaSourceFileExporter.JavaFileHasNotBeenCreatedException.class)
    public void testExportWithInvalidOptions() throws Exception {
        JavaSourceFileExporter exporter = new JavaSourceFileExporter();
        exporter.export(data.getSimpleEnum(), exporter.createOptions("::;;§$\\\\\\%§$%M;,§$% def;ectiveP,ath $§§"));
    }
}
