package dev.isnow.qchecker.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {
    public boolean containsIgnoreCase(String str, String searchStr) {
        final int length = searchStr.length();
        final char firstCharUpper = Character.toUpperCase(searchStr.charAt(0));
        final char firstCharLower = Character.toLowerCase(searchStr.charAt(0));
        for (int i = str.length() - length; i >= 0; i--) {
            final char charAt = str.charAt(i);
            if (charAt == firstCharUpper || charAt == firstCharLower) {
                if (str.regionMatches(true, i, searchStr, 0, length)) {
                    return true;
                }
            }
        }
        return false;
    }
}
