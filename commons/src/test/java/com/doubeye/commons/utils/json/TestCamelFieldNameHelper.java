package com.doubeye.commons.utils.json;

import com.doubeye.commons.utils.json.mapper.CamelFieldNameHelper;
import junit.framework.TestCase;
import org.junit.Assert;

public class TestCamelFieldNameHelper extends TestCase{
    public void testAll() {
        String testToCamel = "camel_field_name_helper";
        String testToCamelMultiSeparator = "camel@@field@@name@@helper";
        String testToCamelResult = CamelFieldNameHelper.toCamel(testToCamel, "_");
        String testToCamelMultiSeparatorResult = CamelFieldNameHelper.toCamel(testToCamelMultiSeparator, "@@");

        Assert.assertEquals(testToCamelResult, "camelFieldNameHelper");
        Assert.assertEquals(testToCamelMultiSeparatorResult, "camelFieldNameHelper");


        String testToCamelNoSeparator = "camelFieldNameHelper";
        Assert.assertEquals(CamelFieldNameHelper.toCamel(testToCamelNoSeparator, "_"), "camelFieldNameHelper");

        String testFromCamel = "camelFieldNameHelperA";

        String testFromCamelResult = CamelFieldNameHelper.fromCamel(testFromCamel, "_");
        String testFromCamelResultMulti = CamelFieldNameHelper.fromCamel(testFromCamel, "++");
        Assert.assertEquals(testFromCamelResult, "camel_field_name_helper_a");
        Assert.assertEquals(testFromCamelResultMulti, "camel++field++name++helper++a");
    }
}
