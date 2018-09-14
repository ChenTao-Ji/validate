package com.example.validate.util;

public class StringUtils {
    public static boolean isEmpty(String templateFiletype) {
        return templateFiletype == null || templateFiletype.equals("");
    }
}
