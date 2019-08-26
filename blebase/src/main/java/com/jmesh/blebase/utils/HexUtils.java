package com.jmesh.blebase.utils;

/**
 * Created by BC119 on 2018/6/21.
 */

public class HexUtils {
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        if (data == null)
            return null;
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }


    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, true);
    }

    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }


    protected static String encodeHexStr(byte[] data, char[] toDigits) {
        return new String(encodeHex(data, toDigits));
    }

    public static String formatHexString(byte[] data) {
        return formatHexString(data, false);
    }

    public static String formatHexString(byte[] data, boolean addSpace) {
        if (data == null || data.length < 1)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
            if (addSpace)
                sb.append(" ");
        }
        return sb.toString().trim();
    }

    public static byte[] decodeHex(char[] data) {

        int len = data.length;

        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }

        byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }


    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch
                    + " at index " + index);
        }
        return digit;
    }


    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String extractData(byte[] data, int position) {
        return HexUtils.formatHexString(new byte[]{data[position]});
    }

    public static byte[] linkIntToHexByteWithFigureCount(Integer... integers) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < integers.length; i += 2) {
            int figureCount = integers[i];
            int number = integers[i + 1];
            stringBuilder.append(numToHexString(figureCount, number));
        }
        return hexStringToBytes(stringBuilder.toString());
    }

    public static String numToHexString(int figureCount/*最少位数*/, int number) {
        return String.format("%0" + figureCount + "x", number);
    }

    public static byte[] linkByte(byte[] byte1, byte[] byte2) {
        byte[] resultByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, resultByte, 0, byte1.length);
        System.arraycopy(byte2, 0, resultByte, byte1.length, byte2.length);
        return resultByte;
    }

    public static int byteArrayToInt(byte[] bArr) {
        if (bArr.length != 4) {
            return -1;
        }
        return (int) ((((bArr[3] & 0xff) << 24)
                | ((bArr[2] & 0xff) << 16)
                | ((bArr[1] & 0xff) << 8)
                | ((bArr[0] & 0xff) << 0)));
    }

    public static byte[] intercept(byte[] src, int len) {
        if (src == null) {
            return src;
        }
        if (src.length <= len) {
            return src;
        }
        byte[] vaildData = new byte[len];
        for (int i = 0; i < vaildData.length; i++) {
            vaildData[i] = src[i];
        }
        return vaildData;
    }

    public static byte[] intercept(byte[] src, int start, int length) {
        if (src == null) {
            return src;
        }
        if (src.length < start + length) {
            return src;
        }
        byte[] vaildData = new byte[length];
        for (int i = 0; i < length; i++) {
            vaildData[i] = src[start + i];
        }
        return vaildData;
    }

    public static byte[] reversal(byte[] src) {
        if (src == null) {
            return src;
        }
        byte[] result = new byte[src.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = src[src.length - i - 1];
        }
        return result;
    }

    public static String convertToMacString(byte[] src) {
        if (src == null || src.length != 6) {
            return "error src";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            builder.append(numToHexString(2, src[i] & 0xFF));
            if (i != src.length - 1) {
                builder.append(":");
            }
        }
        return builder.toString().toUpperCase();
    }

    public static byte[] macStringToByte(String mac) {
        String newMac = deleteCharFromString(mac, ":");
        return HexUtils.hexStringToBytes(newMac);
    }

    public static String deleteCharFromString(String src, String willDeleted) {
        if (src == null) {
            return null;
        }
        String newSrc = src.replace(willDeleted, "");
        return newSrc;
    }

    /**
     * 截取16个字节的UUID
     */
    public static byte[] getRealUUID(byte[] uuid) {
        if (uuid.length <= 16) {
            return uuid;
        }
        byte[] realUUID = new byte[16];
        for (int i = 0; i < realUUID.length; i++) {
            realUUID[i] = uuid[i];
        }
        return realUUID;
    }

}
