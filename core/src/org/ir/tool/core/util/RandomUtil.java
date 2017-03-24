package org.ir.tool.core.util;

import java.util.Random;

/**
 * Created by ekamolid on 12/19/2016.
 */
public class RandomUtil {

    public static enum RandomStringEnum {
        NON_ASCII,
        ASCII,
        ASCII_NOT_NUMBER,
        ASCII_NOT_NUMBER_LOWER,
    }

    static final String NON_ASCII = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzБВГДЖЗКЛМНПРСТФХЦЧШЩАЭЫУОЯЕЁЮИбвгджзклмнпрстфхцчшщаэыуояеёюи";
    static final String ASCII = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static final String ASCII_NOT_NUMBER = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static final String ASCII_NOT_NUMBER_LOWER = "abcdefghijklmnopqrstuvwxyz";
    static Random rnd = new Random();

    public static String generateRandomString(int len, RandomStringEnum randomStringEnum) {
        String str = null;
        if (randomStringEnum == RandomStringEnum.ASCII) {
            str = ASCII;
        } else if (randomStringEnum == RandomStringEnum.NON_ASCII) {
            str = NON_ASCII;
        } else if (randomStringEnum == RandomStringEnum.ASCII_NOT_NUMBER) {
            str = ASCII_NOT_NUMBER;
        } else if (randomStringEnum == RandomStringEnum.ASCII_NOT_NUMBER_LOWER) {
            str = ASCII_NOT_NUMBER_LOWER;
        }
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(str.charAt(rnd.nextInt(str.length())));
        }
        return sb.toString();
    }
}
