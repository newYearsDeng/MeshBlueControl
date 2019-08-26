package com.northmeter.meshbluecontrol.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Udp_Help {
	/**16进制字符串转换成byte数组进行广播*/
	public static byte[] strtoByteArray(String hexString) {
		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}

	/**byte数组转换为字符串*/
	public static String bytesToHexString(byte[] src){
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**bytes转换为16进制字符串 *******/
	public static String bytesToHex(byte[] buffer, int length) {
		String ret = "";
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(buffer[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}

		return ret;

	}

	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;

		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	public char[] stringToChar(String str) {
		char[] sendStr;
		String[] itemStr = str.split(" ");
		sendStr = new char[itemStr.length];
		for (int i = 0; i < itemStr.length; i++) {
			char ch = (char) Integer.parseInt(itemStr[i], 16);
			sendStr[i] = ch;
		}
		return sendStr;

	}

	/**ASCII转换为字符串*/
	public static String Ascii_To_String(String ascii){//ASCII转换为字符串
		StringBuffer s=new StringBuffer();
		String[] chars = new String[ascii.length()/2];
		for(int j=0;j<ascii.length()/2;j++){
			chars[j]=ascii.substring(j*2,j*2+2);
		}
		for(int i=0;i<chars.length;i++){
			//System.out.println(chars[i]+" "+(char)Integer.parseInt(Integer.valueOf(chars[i],16).toString()));
			s=s.append(String.valueOf((char)Integer.parseInt(Integer.valueOf(chars[i],16).toString())));

		}
		return s.toString();
	}

	/**字符串转换为ASCII码*/
	public static String String_To_Ascii(String str){
		StringBuffer s=new StringBuffer();
		char[] chars=str.toCharArray(); //把字符中转换为字符数组
		for(int i=0;i<chars.length;i++){//输出结果
			s.append(Integer.toHexString((int)chars[i]));
		}
		return s.toString();
	}

	public static String getAsciiTo645(String para){
		StringBuffer stringBuffer = new StringBuffer();
		for(int i=0;i<para.length()/2;i++){
			String result0 = Integer.toHexString(Integer.valueOf(para.substring(i*2,i*2+2),16)+51);
			if(result0.length()>2){
				result0 = result0.substring(result0.length()-2,result0.length());
			}
			stringBuffer.append(result0);
		}
		String flag = "3333333333333333333333333333333333333333";
		String result = stringBuffer.toString();
		result = result + flag.substring(result.length(),flag.length());
		return result;
	}

	/**16进制转10进制*/
	public static String get_inter_add(String add){//16进制转10进制,接收到的报文显示是处理
		StringBuffer sb=new StringBuffer();
		String[] chars=new String[add.length()/2];
		for(int i=0;i<add.length()/2;i++){
			chars[i]=add.substring(i*2,i*2+2);
			sb.append(Integer.valueOf(chars[i],16)+".");
		}
		return sb.toString().substring(0,sb.toString().length()-1);
	}

    /**10进制转16进制*/
    public static String intToHex(int data){//16进制转10进制,接收到的报文显示是处理
        String hexData = Integer.toHexString(data);
        if(hexData.length()<2){
            hexData = "0"+data;
        }
        return hexData;
    }


	/**
	 * 将时间转换为时间戳
	 */
	public static String dateToStamp(String s) throws ParseException{
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = simpleDateFormat.parse(s);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long ts = date.getTime();
		res = String.valueOf(ts);
		return res;
	}

	/**表号反向*/
	public static String reverseRst(String rst){
		//String newRst=rst.substring(2, rst.length()-2);
		String lastrst = "";
		for(int i=rst.length()/2;i>0;i--){
			lastrst=lastrst+rst.substring(i*2-2, i*2);
		}
		return lastrst;
	}

	/**获取校验码。总加和*/
	public static String get_sum(String num){
		int sum=0;
		for(int i=0;i<num.length()/2;i++){
			sum=sum+Integer.valueOf(num.substring(i*2,i*2+2),16);
		}
		String check_str=Integer.toHexString(sum);
		if(check_str.length()<2){
			check_str="0"+check_str;
		}else{
			System.out.println("总加和："+check_str);
			check_str=check_str.substring(check_str.length()-2,check_str.length());
		}
		return check_str;
	}


	/**读取图片数据 +33*/
	public static String getpage_HexTo645(String para){
		StringBuffer stringBuffer = new StringBuffer();
		for(int i=0;i<para.length()/2;i++){
			String result0 = Integer.toHexString(Integer.valueOf(para.substring(i*2,i*2+2))+51);
			if(result0.length()<2){
				result0 = "0"+result0;
			}
			stringBuffer.append(result0);
		}
		return stringBuffer.toString();

	}


	/**拍照窗口数据+33，再转换为16进制*/
	public static String get_came_hexTo645(String para){
		StringBuffer sb = new StringBuffer();
		String flag = "0000";
		String result = Integer.toHexString(Integer.parseInt(para));
		String hex = flag.substring(result.length(),4)+result;
		for(int i = 0;i<hex.length()/2;i++){
			String result0 = Integer.toHexString(Integer.valueOf(hex.substring(i*2,i*2+2),16)+51);
			if(result0.length()>2){
				result0 = result0.substring(result0.length()-2,result0.length());
			}
			sb.append(result0);
		}

		return sb.toString().toUpperCase();
	}

	/**数据-33 把645源数据还原*/
	public static String get_645ToHex(String para){
		StringBuffer stringBuffer = new StringBuffer();
		for(int i=0;i<para.length()/2;i++){
			String result0 = Integer.toHexString(Integer.valueOf(para.substring(i*2,i*2+2),16)-51);
			if(result0.length()<2){
				result0 = "0"+result0;
			}else if(result0.length()==8){//ffffffxx
				result0 = result0.substring(result0.length()-2,result0.length());
			}
			stringBuffer.append(result0);
		}
		return stringBuffer.toString();
	}

	/**数据+33 生成645*/
	public static String create_645ToHex(String para){
		StringBuffer stringBuffer = new StringBuffer();
		for(int i=0;i<para.length()/2;i++){
			String result0 = Integer.toHexString(Integer.valueOf(para.substring(i*2,i*2+2),16)+51);
			if(result0.length()<2){
				result0 = "0"+result0;
			}else if(result0.length()==8||result0.length()>2){//ffffffxx
				result0 = result0.substring(result0.length()-2,result0.length());
			}
			stringBuffer.append(result0);
		}
		return stringBuffer.toString();
	}

	/**10进制参数 +33*/
	public static String get_Stting_HexTo645(String para){
		String result = Integer.toHexString(Integer.valueOf(para)+51);
		if(result.length()>2){
			result = result.substring(result.length()-2,result.length());
		}
		return result;
	}

	/**socket设置 10进制参数转16进制+33*/
	public static String get_Socket_645Str(String para){
		String result = Integer.toHexString(Integer.parseInt(para)+51);
		if(result.length()>2){
			result = result.substring(result.length()-2,result.length());
		}
		return result.toUpperCase();
	}

	/**上报时间设置 16进制参数 +33*/
	public static String get_NBTimeTo645(String para,int state){
		switch(state){
			case 0:
				para = para.replaceAll("-","");
				if(para.length()>=6){
				    para = para.substring(para.length()-6,para.length());
                }
				break;
			case 1:
				para = para.replaceAll(":","");
				break;
		}
		String result="";
		for(int i=0;i<para.length()/2;i++){
			String result0 = Integer.toHexString(Integer.valueOf(para.substring(i*2,i*2+2),16)+51);
			if(result0.length()>2){
				result0 = result0.substring(result0.length()-2,result0.length());
			}
			result = result+result0;
		}

		return result;
	}
	public static String get_NBMinTo645(String para){
		String flag = "00000000";
		String resultHex = Integer.toHexString(Integer.parseInt(para));
		para = flag.substring(0,8-resultHex.length())+resultHex;
		String result="";
		for(int i=0;i<para.length()/2;i++){
			String result0 = Integer.toHexString(Integer.parseInt(para.substring(i*2,i*2+2),16)+51);
			if(result0.length()>2){
				result0 = result0.substring(result0.length()-2,result0.length());
			}
			result = result+result0;
		}

		return result;
	}

	/**
	 * @param 将字节数组转换为ImageView可调用的Bitmap对象
	 * @param bytes
	 * @param opts
	 * @return Bitmap
	 */
	public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}
	/**
	 * @param 图片缩放
	 * @param bitmap 对象
	 * @param w 要缩放的宽度
	 * @param h 要缩放的高度
	 * @return newBmp 新 Bitmap对象
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h){
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newBmp;
	}


	/**组装对水表操作的3762数据帧*/
	public static String get_3762_MeterStr(String last){
		//String first = "68"+"2字节长度"+"4a"+"主节点地址"+"f00200";
//		String first = "4a"+"000000000000"+"F00200"+last;
//		String length = getLength(first);
//		String data = "68"+length+first;
//		String cs = Udp_Help.get_sum(data);
//		String lastData = data+cs+"16";
//		return lastData.toUpperCase();
		return last;
	}

	/**计算2字节指令长度*/
	public static String getLength_2(String data){
		String hexStr = "";
		try{
			int len = data.length()/2;
			String hex = Integer.toHexString(len);
			if(hex.length()<4){
				String flag = "0000";
				hexStr = flag.substring(0,flag.length()-hex.length())+hex;
				hexStr = hexStr.substring(2,4)+ hexStr.substring(0,2);
			}else{
				hexStr = hex.substring(hex.length()-2,hex.length())+ hex.substring(hex.length()-4,hex.length()-2);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return hexStr;
	}

    /**计算1字节指令长度*/
    public static String getLength_1(String data){
        String hexStr = "";
        try{
            int len = data.length()/2;
            hexStr = Integer.toHexString(len);
            if(hexStr.length()<2){
                hexStr = "0"+hexStr;
            }else{
                hexStr = hexStr.substring(hexStr.length()-2,hexStr.length());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return hexStr;
    }


	public static String get_NBTimeToStr(String para,String flag){
		int time1 = Integer.valueOf(para.substring(0,2));
		int time2 = Integer.valueOf(para.substring(2,4));
		int time3 = Integer.valueOf(para.substring(4,6));
		return String.format("%02d", time1)+flag+String.format("%02d", time2)+flag+String.format("%02d", time3);
	}

	public static String get_NBPhotoTimeToStr(String para,String flag){
		int time1 = Integer.valueOf(para.substring(0,2));
		int time2 = Integer.valueOf(para.substring(2,4));
		return String.format("%02d", time1)+flag+String.format("%02d", time2);
	}

}