package com.bechris100.remote4j.commons.utils;

public class StringGenerator {

    public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public static final String NUMBERS = "0123456789";
    public static final String DEFAULT_CHARACTER_MAP = UPPERCASE + LOWERCASE + NUMBERS;

    public static String generateString(int length) {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < length; i++)
            str.append(DEFAULT_CHARACTER_MAP.charAt(Utility.getRandomInteger(0, DEFAULT_CHARACTER_MAP.length() - 1)));

        return str.toString();
    }

    public static String generateString(String charMap, int length) {
        if (charMap == null)
            return "";

        if (charMap.isEmpty())
            return "";

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < length; i++)
            str.append(charMap.charAt(Utility.getRandomInteger(0, charMap.length() - 1)));

        return str.toString();
    }

}
