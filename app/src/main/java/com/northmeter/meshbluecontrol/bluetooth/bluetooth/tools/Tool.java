package com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools;

/**
 * Created by benjamin on 16/4/27.
 */
public class Tool {
    public static String printBytes(byte[] bytes){
        String s = "";
        for(int i=0;i<bytes.length;i++){
            if(i!=bytes.length-1){
                s += Integer.toHexString(bytes[i] & 0x00ff)+"-";
            }else{
                s += Integer.toHexString(bytes[i] & 0x00ff);
            }
        }
        return s;
    }

    public static byte[] decodeStringTo16(String source){
        byte[] result = new byte[0];
        try {
            String[] temp = source.split("-");


            result = new byte[0];
            result = new byte[temp.length];
            for(int i=0;i<temp.length;i++){
                result [i] = (byte)Integer.parseInt(temp[i],16);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
