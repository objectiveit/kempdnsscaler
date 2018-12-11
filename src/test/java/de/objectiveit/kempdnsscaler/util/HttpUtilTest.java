package de.objectiveit.kempdnsscaler.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static de.objectiveit.kempdnsscaler.util.HttpUtil.formatQueryParams;
import static de.objectiveit.kempdnsscaler.util.HttpUtil.isValidURL;

public class HttpUtilTest {

    @Test
    public void testValidURL() {
        Assert.assertTrue(isValidURL("http://127.0.0.1"));
        Assert.assertTrue(isValidURL("https://127.0.0.1:443"));
        Assert.assertTrue(isValidURL("http://google.com"));
        Assert.assertTrue(isValidURL("https://google.com"));
        Assert.assertTrue(isValidURL("http://google.com:443"));
        Assert.assertTrue(isValidURL("https://google.com:443"));
        Assert.assertTrue(isValidURL("http://google.com/"));
        Assert.assertTrue(isValidURL("https://google.com/"));
        Assert.assertTrue(isValidURL("http://google.com//"));
        Assert.assertTrue(isValidURL("https://google.com//"));
    }

    @Test
    public void testInvalidURL() {
        Assert.assertFalse(isValidURL("google.com"));
        Assert.assertFalse(isValidURL("google.com:443"));
    }

    @Test
    public void testFormatQueryParams() {
        Map<String, Object> queryParams = new HashMap<String, Object>() {{
            put("key1", "value1");
            put("key2", 2);
            put("key3", true);
        }};

        String result = formatQueryParams(queryParams);
        String expected = "?key1=value1&key2=2&key3=true";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testFormatQueryParamsEdgeCases() {
        Assert.assertEquals("", formatQueryParams(null));
        Assert.assertEquals("", formatQueryParams(new HashMap<>()));
    }

}
