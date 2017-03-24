package org.ir.tool.core.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by ekamolid on 12/10/2016.
 */
public class UTF8Util {

    public static int stringToUTF8Bytes(String str, int start, int len, byte[] bytes, int byteStart) {
        for (int i = start; i < start + len; i++) {
            char ch = str.charAt(i);
            byte b = charToUtf8(ch, bytes, byteStart);
            byteStart += b;
        }
        return byteStart;
    }

    public static byte[] stringToUTF8Bytes(String str, int start, int len) {
        int nd = bytesNeeded(str, start, len);
        byte[] bytes = new byte[nd];
        int startBytes = 0;
        for (int i = start; i < start + len; i++) {
            char ch = str.charAt(i);
            byte n = charToUtf8(ch, bytes, startBytes);
            startBytes += n;
        }
        return bytes;
    }

    public static int bytesNeeded(String s, int start, int len) {
        int nd = 0;
        for (int i = start; i < start + len; i++) {
            nd += bytesNeeded(s.charAt(i));
        }
        return nd;
    }

    public static byte bytesNeeded(char ch) {
        int codePoint = (int) ch;
        if (codePoint <= 0x007F) {
            return 1;
        } else if (codePoint <= 0x07FF) {
            return 2;
        } else if (codePoint <= 0xFFFF) {
            return 3;
        } else {
            return 4;
        }
    }

    public static byte charToUtf8(char ch, byte[] bytes, int start) {
        int codePoint = (int) ch;
//        128 = 1 << 7
        if (codePoint <= 0x007F) {
            bytes[start] = (byte) codePoint;
            return 1;
        } else if (codePoint <= 0x07FF) {
            bytes[start++] = (byte) (3 << 6 | codePoint >>> 6);
            bytes[start] = (byte) (128 | (byte) (codePoint & 0x3F));
            return 2;
        } else if (codePoint <= 0xFFFF) {
            bytes[start++] = (byte) (7 << 5 | codePoint >>> 12);
            byte b = (byte) (128 | (byte) ((codePoint >>> 6) & 0x3F));
            bytes[start++] = b;
            b = (byte) (128 | (byte) (codePoint & 0x3F));
            bytes[start] = b;
            return 3;
        } else {
            bytes[start++] = (byte) (15 << 4 | codePoint >>> 18);
            byte b = (byte) (128 | (byte) ((codePoint >>> 12) & 0x3F));
            bytes[start++] = b;
            b = (byte) (128 | (byte) ((codePoint >>> 6) & 0x3F));
            bytes[start++] = b;
            b = (byte) (128 | (byte) (codePoint & 0x3F));
            bytes[start] = b;
            return 4;
        }
    }

    public static void main(String[] args) {
        String s = "БВГДЖЗКЛМНПРСТФХЦЧШЩАЭЫУОЯЕЁЮИбвгджзклмнпрстфхцчшщаэыуояеёюи";
        byte bt[] = new byte[(s.length() - 10) * 2];
        stringToUTF8Bytes(s, 10, s.length() - 10, bt, 0);
        for (int i = 0; i < 1100000; i++) {
            //omit surrogates
            if (((short) i & 0xFFFF) >= 0xd800 && ((short) i & 0xFFFF) <= 0xdfff) {
                continue;
            }
            char ch = (char) i;
            byte[] bytes = new byte[4];
            byte b = charToUtf8(ch, bytes, 0);
            byte[] r = new byte[b];
            System.arraycopy(bytes, 0, r, 0, b);
            if (!Arrays.equals(r, String.valueOf(ch).getBytes(StandardCharsets.UTF_8))) {
                System.out.println("not equal " + Integer.toHexString(i));
            }
        }
    }
}