package com.scwang.smart.refresh.header;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        System.out.println(new SimpleDateFormat("'Last update' M-d HH:mm", Locale.CHINA).format(new Date()));
        assertEquals(4, 2 + 2);
    }
}