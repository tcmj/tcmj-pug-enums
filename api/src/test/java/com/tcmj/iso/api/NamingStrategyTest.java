package com.tcmj.iso.api;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * pugproductions - 2017-05-22 - tcmj.
 */
public class NamingStrategyTest {

    @Test
    public void shouldConvertSimpleUsage()   {
        String given = "Hallo Welt";
        NamingStrategy ns = value -> value;
        assertThat("value -> value", ns.convert(given), equalTo("Hallo Welt"));
    }

    @Test
    public void shouldConvertSimpleUsageTrim()  {
        String given = "  Trimming    ";
        NamingStrategy ns = value -> value.trim();
        assertThat("value.trim()", ns.convert(given), equalTo("Trimming"));
    }

    @Test
    public void shouldConvertSimpleUsageLowerCase() {
        String given = "PLEASE BE QUIET!";
        NamingStrategy ns = value -> value.toLowerCase();
        assertThat("value.toLowerCase()", ns.convert(given), equalTo("please be quiet!"));
    }

    @Test
    public void shouldImplementedInANullSafeManner() {
        String given = null;
        NamingStrategy ns = value -> value;
        assertThat("null", ns.convert(given), nullValue());
    }

    @Test
    public void chainingIsAwesome() {
        String given = "  OH How Can i use this VALUE   ";
        NamingStrategy trim = value -> value.trim();
        NamingStrategy lower = value -> value.toLowerCase();
        NamingStrategy space2underline = value -> value.replace(' ','_');
        NamingStrategy ns = trim.and(lower).and(space2underline);
        assertThat(  ns.convert(given), equalTo("oh_how_can_i_use_this_value"));
    }
}
