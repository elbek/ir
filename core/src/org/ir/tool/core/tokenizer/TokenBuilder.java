package org.ir.tool.core.tokenizer;

/**
 * Created by ekamolid on 12/21/2016.
 */
public class TokenBuilder {
    public static int MAX_LENGTH = 255;
    short current = 0;
    char[] chars = new char[MAX_LENGTH];

    public void append(String string) {
        assert current + string.length() < MAX_LENGTH;
        for (int i = 0; i < string.length(); i++) {
            chars[current++] = string.charAt(i);
        }
    }

    public void append(char ch) {
        assert current + 1 < MAX_LENGTH;
        chars[current++] = ch;
    }

    public void replace(short i, char ch) {
        assert i < MAX_LENGTH;
        chars[i] = ch;
    }

    public void replace(short start, String s) {
        assert start + s.length() < MAX_LENGTH;
        current = start;
        for (int i = 0; i < s.length(); i++) {
            chars[current++] = s.charAt(i);
        }
    }

    public void replace(short start, String s, short sStart, short sLength) {
        assert start + sLength < MAX_LENGTH;
        current = start;
        for (int i = sStart; i < sStart + sLength; i++) {
            chars[current++] = s.charAt(i);
        }
    }

    @Override
    public String toString() {
        if (current > 0) {
            return new String(chars, 0, current);
        }
        return null;
    }

    public char at(short i) {
        assert i < MAX_LENGTH;
        return chars[i];
    }

    public short length() {
        return current;
    }

    public void reset() {
        current = 0;
    }
}
