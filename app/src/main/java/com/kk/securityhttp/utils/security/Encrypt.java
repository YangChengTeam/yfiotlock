package com.kk.securityhttp.utils.security;

public class Encrypt {
    private static char[] k = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', '*', '!'};


    public static String encode(String str) {
        if (str == null) {
            return "";
        }
        str = encodeStr(str);
        StringBuilder sb = new StringBuilder();
        char[] array = str.toCharArray();
        sb.append("x");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i] + k[(i % k.length)]);
            sb.append("_");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("y");
        return sb.toString();
    }

    public static String decode(String str) {
        if (str == null) {
            return null;
        }
        if ((str.startsWith("x")) && (str.endsWith("y"))) {
            StringBuilder sb = new StringBuilder(str);
            sb.deleteCharAt(0);
            sb.deleteCharAt(sb.length() - 1);
            str = sb.toString();
            String[] strs = str.split("_");
            sb = new StringBuilder();
            for (int i = 0; i < strs.length; i++) {
                sb.append((char) (Integer.parseInt(strs[i]) - k[(i % k.length)]));
            }
            return decodeStr(sb.toString());
        }
        return "";
    }

    public static String decode2(String str) {
        if (str == null) {
            return null;
        }

        if ((str.startsWith("x")) && (str.endsWith("y"))) {
            StringBuilder sb = new StringBuilder(str);
            sb.deleteCharAt(0);
            sb.deleteCharAt(sb.length() - 1);
            str = sb.toString();
            String[] strs = str.split("_");
            sb = new StringBuilder();
            for (int i = 0; i < strs.length; i++) {
                sb.append((char) (Integer.parseInt(strs[i]) - k[(i % k.length)]));
            }

            return decodeStr(sb.toString());
        }
        return "";
    }


    public static String decodeStr(String str) {
        byte[] debytes = Base64.decode(str);
        return new String(debytes);
    }


    public static String encodeStr(String str) {
        return Base64.encode(str.getBytes());
    }
}
