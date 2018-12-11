package de.objectiveit.kempdnsscaler.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtilTest {

    @Test
    public void testSubtract1() {
        List<String> from = new ArrayList<String>() {{
            add("1");
            add("2");
            add("3");
        }};
        List<String> subtrahend = new ArrayList<String>() {{
            add("3");
            add("4");
            add("5");
        }};

        List<String> result = CollectionUtil.subtract(from, subtrahend);
        List<String> expected = new ArrayList<String>() {{
            add("1");
            add("2");
        }};
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testSubtract2() {
        List<String> from = new ArrayList<String>() {{
            add("1");
            add("2");
            add("3");
        }};
        List<String> subtrahend = new ArrayList<String>() {{
            add("4");
            add("5");
        }};

        List<String> result = CollectionUtil.subtract(from, subtrahend);
        List<String> expected = new ArrayList<String>() {{
            add("1");
            add("2");
            add("3");
        }};
        Assert.assertEquals(expected, result);
    }

}
