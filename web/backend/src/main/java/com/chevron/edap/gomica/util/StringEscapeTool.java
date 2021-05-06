package com.chevron.edap.gomica.util;

import java.util.regex.Pattern;

public class StringEscapeTool {

    public static String replaceXMLSpecialChars(String s) {
        if(s == null || s.isEmpty()) return s;
        Pattern p = Pattern.compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFF]+");
        String returnContent = p.matcher(s).replaceAll("");
        return returnContent.replaceAll("[^\\x00-\\x7F]", "")
                .replaceAll("&", "&amp;")
                .replaceAll("<", "")
                .replaceAll(">","");
    }
}
