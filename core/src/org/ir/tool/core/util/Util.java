package org.ir.tool.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekamolid on 11/8/2016.
 */
public class Util {

    public String shuffle(String input) {
        List<Character> characters = new ArrayList<Character>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while (characters.size() != 0) {
            int randPicker = (int) (Math.random() * characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }

    public static boolean isEqualBytes(byte[] b1, byte[] b2) {
        assert b1 != null;
        assert b2 != null;
        if (b1 == b2) {
            return true;
        }

        if (b1.length != b2.length) {
            return false;
        }
        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }
}
