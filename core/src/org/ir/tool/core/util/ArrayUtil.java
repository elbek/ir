package org.ir.tool.core.util;

/**
 * Created by ekamolid on 12/23/2016.
 */
public class ArrayUtil {
    public static byte[] grow(byte[] array) {
        int len = array.length;
        byte[] newArray = new byte[len * 2];
        System.arraycopy(array, 0, newArray, 0, len);
        return newArray;
    }

    public static byte[] grow(byte[] array, int minSize) {
        int len = array.length;
        assert len < minSize;
        int newSize = len * 2;
        if (newSize < minSize) {
            newSize = minSize;
        }
        byte[] newArray = new byte[newSize];
        System.arraycopy(array, 0, newArray, 0, len);
        return newArray;
    }
}