package com.tcmj.iso.exporter.impl;

import java.util.Map;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Unit Tests and Usage Examples of {@link InMemoryCompilingExporter}.
 */
public class InMemoryCompilingExporterTest {

    TestDataProvider data = new TestDataProvider();

    @Test
    public void testExportSimpleEnum() throws Exception {
        InMemoryCompilingExporter exporter = new InMemoryCompilingExporter();
        Map<String, Object> options = exporter.createOptions("one.two.three.SimpleEnum");
        exporter.export(data.getSimpleEnum(), null);
        assertThat("class", exporter.getEnumClass().getName(), equalTo("one.two.three.SimpleEnum"));
        assertThat("content", exporter.getEnumConstants().toString(), equalTo("[ONE, TWO, THREE]"));
    }

    @Test
    public void testExportExtendedEnum() throws Exception {
        InMemoryCompilingExporter exporter = new InMemoryCompilingExporter();
        Map<String, Object> options = exporter.createOptions("org.Animal");
        exporter.export(data.getExtendedEnum(), null);
        assertThat("class", exporter.getEnumClass().getName(), equalTo("org.Animal"));
        assertThat("content", exporter.getEnumConstants().toString(), equalTo("[DOG]"));
    }

    @Test
    public void testExportEnumWithoutPackage() throws Exception {
        InMemoryCompilingExporter exporter = new InMemoryCompilingExporter();
        Map<String, Object> options = exporter.createOptions("NoPckEnum");
        exporter.export(data.getEnumWithoutPackage(), null);
        assertThat("class", exporter.getEnumClass().getName(), equalTo("NoPckEnum"));
        assertThat("content", exporter.getEnumConstants().toString(), equalTo("[ONE, TWO, THREE]"));
    }

    @Test
    public void testExportWithNullOptions() throws Exception {
        try { //this exporter does not need options!
            InMemoryCompilingExporter exporter = new InMemoryCompilingExporter();
            String inputData = data.getSimpleEnum();
            String export = exporter.export(inputData, null);
            assertThat("Chaining", export, equalTo(inputData));
        } catch (Exception e) {
            fail(" we expect a exception");
        }
    }

}
