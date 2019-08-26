package com.northmeter.meshbluecontrol.control;
/**
 * 华凌空调红外码组合*/
public class HuaLingHW {

	/**
	 * 81 01 D3 01 88 01 1F 05   01 01 7C 0D 08 07     70 23 CB 26 01 00      20   03   06   2D   00 00 00 00     6B   关机 制冷 25度 高速方向90度
	   7A 01 D9 01 7A 01 2E 05   01 01 68 0D 00 07     70 23 CB 26 01 00      24   03   06   2D   00 00 00 00     6F   开机 制冷 25度 高速方向90度*/

	private static String model;
	private static String Switch;
	private static String speed;
	private static String temp;
	private static String updown;
	private static String checkcode;
	private static String number;
	/* 
	 * model     模式标志:自动,制冷,抽湿,送风,制热
	 * switvh    开关
	 * speed     风速标志:自动，一级，两级，三级
	 * temp      温度：16~30
	 * updown	  上下扫风
	 *leftright 左右扫风
	 *   指令模板：zl="开，模式，24度，风速，上下，左右"
	 * * 指令模板：zl="开，模式，24度，风速"
	 * */
	
	/**
	 * 第一个数据：
	 * 开关指示，20-关机 24-开机
	 * 第二个数据：
	 * 模式指示，01-制暖 02-除湿 03-制冷 07-送风*/
	private static String modelswitch(String[] zl){
		switch(zl[1]){
		case "自动":
			model="07";
			break;
		case "制冷":
			model="03";
			break;
		case "抽湿":
			model="02";
			break;
		case "送风":
			model="07";
			break;
		case "制热":
			model="01";
			break;		
		}
		
		switch (zl[0]){
		case "开":
			Switch="24";
			break;
		case "关":
			Switch="20";
			break;
		case "调温":
			Switch="24";
			break;
		}		
		StringBuffer ms=new StringBuffer();
		ms.append(Switch);
		ms.append(model);
		return  ms.toString();
	}
	
	/**第三个数据
	 * 温度：温度指示，0F-16度，0E-17度，0D-18度，0C-19度，... 01-30度， 01-30度，00-31度*/
	private static String getTemp(String[] zl){
		int temputers=Integer.parseInt(zl[2].toString());
		String temp_hex = "0"+Integer.toHexString(31-temputers).toUpperCase();
		return temp_hex;
	}
	
	/**第四个数据
	 * 风速及方向指示，
				bit0-bit2共三位指示风速大小，bit2bit1bit0:010-低速，011-中速，101-高速；
			    bit3-bit5共三位指示风速方向，bit5bit4bit3:001-0度，010-20度，011-40度，100-60度，101-90度；	
                bit5-bit4:11风速摇摆 ，其他风速固定。
	 */
	private static String getSpeed(String[] zl){
		//这里固定一个摇摆方向
		String fanx = "";
		if(zl[4].equals("上下")|zl[5].equals("左右")){
			fanx = "111";
		}else{
			fanx = "100";
		}
		switch(zl[3]){//风速
		case "自动":
			speed="010";
			break;
		case "一级":
			speed="010";
			break;
		case "二级":
			speed="011";
			break;
		case "三级":
			speed="101";
			break;
		default://这里4级或者5级风都是11
			speed="101";
			break;
		}	
		StringBuffer ms=new StringBuffer();
		ms.append(fanx);
		ms.append(speed);
		String sp = Integer.toHexString(Integer.valueOf(ms.toString(),2)).toUpperCase();
		return  sp;
		
	}
	/**
	 * 校验核
	 * 5最后一个字节：数据70后的校验和：如：
	   81 01 D3 01 88 01 1F 05   01 01 7C 0D 08 07     70 23 CB 26 01 00    20   03   06   2D   00 00 00 00 6B   关机 制冷 25度 高速方向90度
	       最后字节为6B,其值为数据  23 CB 26 01 00 20 03 06 2D 00 00 00 00 的累加和模256；
	*/
	private static String getCheckNumber(String msg){
		System.out.println( msg);
			    int sum=0;
				for(int i=0;i<msg.length()/2;i++){
					 sum=sum+Integer.valueOf(msg.substring(i*2,i*2+2),16);
				}
				System.out.println("sum:"+sum);
				sum = sum%256;
				System.out.println(sum);
				String check_str=Integer.toHexString(sum);
				if(check_str.length()<2){
					check_str="0"+check_str;
				}else{
					System.out.println("总加和："+check_str);
					check_str=check_str.substring(check_str.length()-2,check_str.length());
				}
			 return check_str.toUpperCase();
	}
	
	private static String getNumber(String[] zl){
		switch (zl[0]){
		case "开":
			number="01";
			break;
		case "关":
			number="02";
			break;
		case "调温":
			number="01";
			break;
		}	
		return number;
	}
	
	public static String getHuaLinHw(String zl){
		StringBuffer hwm_1=new StringBuffer();
		try{
		String[] zllist = zl.split(",");
		hwm_1.append(getNumber(zllist));
		hwm_1.append("7A01D9017E012A050101830DFD067023CB260100");
		hwm_1.append(modelswitch(zllist));
		hwm_1.append(getTemp(zllist));
		hwm_1.append(getSpeed(zllist));
		hwm_1.append("00000000");
		hwm_1.append(getCheckNumber(hwm_1.toString().substring(32,hwm_1.length())));
		}catch(Exception e){
			e.printStackTrace();
		}
		return hwm_1.toString();
	}
	
	
	
	public static void main(String [] args){
		//制冷（100） 风速1 扫风关 超强 20度
		String zl="开,制冷,25,三级,上下关,左右关";
		String zl1="开,送风,18,自动,上下,左右";
		System.out.println(getHuaLinHw(zl));
	}
	
	
}
