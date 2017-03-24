package org.ir.tool.core.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ekamolid on 12/15/2016.
 */
public class UTF8UtilTest {
    private String s = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzБВГДЖЗКЛМНПРСТФХЦЧШЩАЭЫУОЯЕЁЮИбвгджзклмнпрстфхцчшщаэыуояеёюи";

    @Test
    public void stringToUTF8Bytes() throws Exception {
        for (int i = 0; i < s.length(); i++) {
            if (s.length() - i == 1) {
                continue;
            }
            byte[] bytes = UTF8Util.stringToUTF8Bytes(s, i, s.length() - i);
            Assert.assertArrayEquals(s.substring(i, s.length()), bytes, s.substring(i, s.length()).getBytes());
        }
    }

    @Test
    public void bytesNeeded() throws Exception {
        for (int i = 0; i < s.length(); i++) {
            if (s.length() - i == 1) {
                continue;
            }
            int nd = UTF8Util.bytesNeeded(s, i, s.length() - i);
            Assert.assertEquals(nd, s.substring(i, s.length()).getBytes().length);
        }
    }
}