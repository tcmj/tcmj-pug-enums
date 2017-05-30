package com.tcmj.iso.crawler;

import com.tcmj.iso.api.DataProvider;
import com.tcmj.iso.api.EnumExporter;
import com.tcmj.iso.api.NamingStrategy;
import com.tcmj.iso.builder.ClassBuilderFactory;
import com.tcmj.iso.builder.NamingStrategyFactory;
import com.tcmj.iso.builder.SourceFormatterFactory;
import com.tcmj.iso.datasources.impl.URLXPathHtmlDataProvider;
import com.tcmj.iso.exporter.EnumExporterFactory;
import com.tcmj.iso.exporter.impl.ReportingEnumExporter;
import com.tcmj.iso.generator.Fluent;

/**
 * Usage Example : Fluently load a Wikipedia table and transform it to a Java enum class.
 */
public class WikipediaExample {

    public static void main(String[] args) {
        try {
            Fluent.builder().fromDataSource(getMyDataProvider())
                    .usingClassBuilder(ClassBuilderFactory.getBestEnumBuilder())
                    .usingNamingStrategy(getMyNamingStrategy())
                    .format(SourceFormatterFactory.getBestSourceCodeFormatter())
                    .exportWith(getMyEnumExporter())
                    .end();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static EnumExporter getMyEnumExporter() {
        EnumExporter exporterA = EnumExporterFactory.getInMemoryCompilingExporter();
        EnumExporter exporterB = EnumExporterFactory.getReportingEnumExporter();
        return exporterA.and(exporterB, exporterB.createOptions(ReportingEnumExporter.LogLevel.SYSTEM_OUT.name()));
    }

    private static NamingStrategy getMyNamingStrategy() {
        NamingStrategy ns1 = NamingStrategyFactory.extractParenthesis();
        NamingStrategy ns2 = NamingStrategyFactory.removeProhibitedSpecials();
        NamingStrategy ns3 = NamingStrategyFactory.camelStrict();
        NamingStrategy ns4 = NamingStrategyFactory.harmonize();
        return ns1.and(ns2).and(ns3).and(ns4);
    }

    private static DataProvider getMyDataProvider() {
        return new URLXPathHtmlDataProvider(
                        "com.tcmj.test.MyWikipediaEnum", //enum name and path
                        "https://en.wikipedia.org/wiki/ISO_3166-1", //url to load
                        "[title=Afghanistan]",  //xpath to a record to further (also to a table possible)
                        1, //enum constant column
                        new int[]{2, 3, 4} //sub columns
                );
    }

}
