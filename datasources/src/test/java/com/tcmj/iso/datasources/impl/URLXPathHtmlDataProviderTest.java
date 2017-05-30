package com.tcmj.iso.datasources.impl;

import java.util.Arrays;
import com.tcmj.iso.api.model.EnumData;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * pugproductions - 2017-05-16 - tcmj.
 */
public class URLXPathHtmlDataProviderTest {

    @Test
    public void overallTestWithoutSubfields() throws Exception {

        URLXPathHtmlDataProvider dataProvider = new URLXPathHtmlDataProvider(
                "com.tcmj.test.MyWikipediaEnum",
                "https://en.wikipedia.org/wiki/ISO_3166-1",
                "[title=Afghanistan]",
                3,
                null
        );
        EnumData data = dataProvider.load();

        assertThat("getClassNameSimple", data.getClassNameSimple(), equalTo("MyWikipediaEnum"));
        assertThat("getClassName", data.getClassName(), equalTo("com.tcmj.test.MyWikipediaEnum"));
        assertThat("getPackageName", data.getPackageName(), equalTo("com.tcmj.test"));
        assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(false));
        assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(249));
        assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(0));
        assertThat("getKey", Arrays.toString(data.getData().entrySet().stream().map(e -> e.getKey()).filter(s -> s.startsWith("F"))
                .toArray()), equalTo("[FLK, FRO, FJI, FIN, FRA, FSM]"));
    }

    @Test
    public void overallTestWithSubfields() throws Exception {
        URLXPathHtmlDataProvider dataProvider = new URLXPathHtmlDataProvider(
                "com.tcmj.test.MyWikipediaEnum",
                "https://en.wikipedia.org/wiki/ISO_3166-1",
                "[title=Afghanistan]",
                1,
                new int[]{2, 3, 4}
        );
        EnumData data = dataProvider.load();

        assertThat("getClassNameSimple", data.getClassNameSimple(), equalTo("MyWikipediaEnum"));
        assertThat("getClassName", data.getClassName(), equalTo("com.tcmj.test.MyWikipediaEnum"));
        assertThat("getPackageName", data.getPackageName(), equalTo("com.tcmj.test"));
        assertThat("isEnumWithSubfields", data.isEnumWithSubfields(), is(true));
        assertThat("getEnumConstantsAmount", data.getEnumConstantsAmount(), is(249));
        assertThat("getSubFieldsAmount", data.getSubFieldsAmount(), is(3));
        assertThat("getName", Arrays.toString(data.getData().entrySet().iterator().next().getValue().getName()), equalTo("[alpha_2_code, alpha_3_code, numeric_code]"));
        assertThat("getType", Arrays.toString(data.getData().entrySet().iterator().next().getValue().getType()), equalTo("[class java.lang.String, class java.lang.String, class java.lang.String]"));
        assertThat("getValue", Arrays.toString(data.getData().entrySet().iterator().next().getValue().getValue()), equalTo("[AF, AFG, 004]"));
    }
}
