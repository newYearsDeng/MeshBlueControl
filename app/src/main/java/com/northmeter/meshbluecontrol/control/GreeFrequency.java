package com.northmeter.meshbluecontrol.control;
/**
 * 格力变频*/
public class GreeFrequency {

	private static String model;
	private static String Switch;
	private static String speed;
	private static String temp;
	private static String updown;
	private static String leftright;
	private static String updownleftright;
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
	
	
	/**第一组数据
	 * 
	 * 9：模式开关 9 即1001  制冷模式为100   1：风速扫风睡眠 0001
	 * */	
	/*第一个数据 第二个数--模式标识和开关*/
	private static String modelswitch(String[] zl){
		switch(zl[1]){
		case "自动":
			model="000";
			break;
		case "制冷":
			model="100";
			break;
		case "抽湿":
			model="010";
			break;
		case "送风":
			model="110";
			break;
		case "制热":
			model="001";
			break;		
		}
		
		switch (zl[0]){
		case "开":
			Switch="1";
			break;
		case "关":
			Switch="0";
			break;
		case "调温":
			Switch="1";
			break;
		}		
		StringBuffer ms=new StringBuffer();
		ms.append(model);
		ms.append(Switch);
		String mosw=ms.reverse().toString();
		String msw=(Integer.toHexString(Integer.valueOf(mosw,2))).toUpperCase();
		return  msw;
	}
	
	/**第一个数据 第一个数：风速标识/ 扫风-睡眠（存在不取值的情况）
	 * 1：风速扫风睡眠 0001*/
	private static String speedsleep(String[] zl){
		String blowing = null;//扫风开关，0或者1
		

		switch(zl[3]){
		case "自动":
			speed="00";
			break;
		case "一级":
			speed="01";
			break;
		case "二级":
			speed="10";
			break;
		case "三级":
			speed="11";
			break;
		default://这里4级或者5级风都是11
			speed="11";
			break;
		}	
			
		
		if(zl[4].equals("上下")||zl[5].equals("左右")){
			blowing = "01";
		}else{
			blowing = "00";
		}
		StringBuffer sbu=new StringBuffer();
		sbu.append(blowing);
		sbu.append(speed);
		String speedss=sbu.toString();
		String speeds=(Integer.toHexString(Integer.valueOf(speedss,2))).toUpperCase();
		return speeds;
	}
	
	/** 第二个数据  温度定时数据
	 * 4：温度数据：4 （20-16）     0：定时数据0000*/
	private static String temp_timing(String[] zl){
		int temputers=Integer.parseInt(zl[2].toString());
		int tempute=temputers-16;
		String tem=Integer.toHexString(tempute).toUpperCase();
		StringBuffer sbuffer=new StringBuffer();
		sbuffer.append("0");
		sbuffer.append(tem);
	    temp=sbuffer.toString();
		return temp;
	}
	
	
	/**第三个数据,
	 * 0：定时数据0000   1：超强灯光健康干燥0001*/
	private static String timing_light(String[] zl){
		//第三个数据
		String light = "0010";
		String lights=(Integer.toHexString(Integer.valueOf(light,2))).toUpperCase();
		StringBuffer sbuffer=new StringBuffer();
		sbuffer.append(lights);
		sbuffer.append("0");
		
		//第四个数据  0：换气0000  5：定值0101 （第四个数据为 定值 50）
		return sbuffer.toString();
	}
	
	
	/**第二组数据
	/**0：上下扫风关0000  开：0001   左右扫风关：0000  开：0001
	 * */
	private static String get_two_data(String[] zl){
		//上下 左右扫风
		if(zl[4].equals("上下")){
			updown="1";
		}else{
			updown="0";
		}
		if(zl[5].equals("左右")){
			leftright="1";
		}else{
			leftright="0";
		}
		StringBuffer sbuffer=new StringBuffer();
		sbuffer.append(updown);
		sbuffer.append(leftright);
		//0：温度显示0000  2：定值0010
		sbuffer.append("20");
		//0：定值0000  0：定值0000
		sbuffer.append("00");

		//9：校验码=(模式 – 1) + (温度 – 16) + 5 +左右扫风+换气+节能
		sbuffer.append(checkCode(zl));
		return sbuffer.toString();
	}
	
	/*校验码   校验码= [(模式 – 1) + (温度– 16) + 5 +左右扫风+换气+节能]取二进制后四位*/
	private static String checkCode(String[] zl){	
		String modercheck = null;
		switch(zl[1]){
		case "自动":
			modercheck="000";
			break;
		case "制冷":
			modercheck="100";
			break;
		case "抽湿":
			modercheck="010";
			break;
		case "送风":
			modercheck="110";
			break;
		case "制热":
			modercheck="001";
			break;		
		}
		String code="";
		//System.out.println("modercheck"+modercheck);
		StringBuffer modersb=new StringBuffer();
		modersb.append(modercheck);
		int models=Integer.valueOf(modersb.reverse().toString(),2); //Integer.parseInt(modelint);
		//System.out.println("models "+models);
		int leftrig=Integer.parseInt(leftright);
		//System.out.println("leftig "+leftrig);
		System.out.println(models+"/"+((Integer.parseInt(zl[2].toString()))-16));
		int checkcodes=(models-1)+((Integer.parseInt(zl[2].toString()))-16)+5+leftrig+0+0;
		System.out.println(checkcodes);
		//System.out.println("校验码：=  "+checkcodes);
		String check=Integer.toBinaryString(checkcodes);//转为2进制
		System.out.println(check);
		//System.out.println("check "+check);
		if(check.length()>4){
		     code=check.substring(check.length()-4, check.length());//截取最后4位
		}else
		{
			code=check;
		}
		//System.out.println("code "+code);
//		StringBuffer str=new StringBuffer();
//		str.append(code);
		//把得到的校验码转为16进制
		System.out.println("="+code);
		String checkc=Integer.toHexString(Integer.valueOf(code,2)).toUpperCase();//反向后先转为10进制，在转为16进制
		System.out.println(checkc);
		//System.out.println("checkc "+checkc);
		StringBuffer checkco=new StringBuffer();
			checkco.append(checkc);
			checkco.append("0");
			checkcode=checkco.toString();
			//System.out.println("checkcode:"+checkcode);
			System.out.println(checkcode);
			return checkcode;
			
	}
	
	
	/**第四组数据
	 * 0：定值0000  0：定值0000
	 * 0：温度显示0000  0：定值0000
	 * 0：定值0000  1：风速档位0001
	 * 0：节能0000   8：校验码=(模式 – 1) + (温度 – 16) + 5 +换气+节能 + 风速档位 -2 
	 * */
	private static String get_Four_data(String[] zl){
		String speed_msg = null;
		switch(zl[3]){
		case "自动":
			speed_msg="0000";
			break;
		case "一级":
			speed_msg="0001";
			break;
		case "二级":
			speed_msg="0010";
			break;
		case "三级":
			speed_msg="0011";
			break;
		case "四级":
			speed_msg="0100";
			break;
		case "五级":
			speed_msg="0101";
			break;
		}	
		String speedcode = (Integer.toHexString(Integer.valueOf(speed_msg,2))).toUpperCase();
		StringBuffer sbuffer=new StringBuffer();
		sbuffer.append("00");
		sbuffer.append("00");
		sbuffer.append(speedcode);
		sbuffer.append("0");
		sbuffer.append(checkCodeTwo(zl,speed_msg));
		return sbuffer.toString();
		
	}
	/**第二个校验核
	 * 
	 * 9：校验码=(模式 – 1) + (温度 – 16) + 5 +左右扫风+换气+节能*/
	/**8：校验码=(模式 – 1) + (温度 – 16) + 5 +换气+节能 + 风速档位 -2 */
	private static String checkCodeTwo(String[] zl,String speeds){	
		String modercheck = null;
		switch(zl[1]){
		case "自动":
			modercheck="000";
			break;
		case "制冷":
			modercheck="100";
			break;
		case "抽湿":
			modercheck="010";
			break;
		case "送风":
			modercheck="110";
			break;
		case "制热":
			modercheck="001";
			break;		
		}
		String code="";
		StringBuffer modersb=new StringBuffer();
		modersb.append(modercheck);
		int models=Integer.valueOf(modersb.reverse().toString(),2); //Integer.parseInt(modelint);
		int speedmath = Integer.valueOf(speeds,2);

		System.out.println(models+"/"+((Integer.parseInt(zl[2].toString()))-16));
		int checkcodes=(models-1)+((Integer.parseInt(zl[2].toString()))-16)+5+0+0+(speedmath-2);
		String check=Integer.toBinaryString(checkcodes);//转为2进制
		System.out.println(check);
		if(check.length()>4){
		     code=check.substring(check.length()-4, check.length());//截取最后4位
		}else
		{
			code=check;
		}
		//把得到的校验码转为16进制
		String checkc=Integer.toHexString(Integer.valueOf(code,2)).toUpperCase();//反向后先转为10进制，在转为16进制
		System.out.println(checkc);
		StringBuffer checkco=new StringBuffer();
			checkco.append(checkc);
			checkco.append("0");
			checkcode=checkco.toString();
			System.out.println(checkcode);
			return checkcode;
			
	}
	
	/***-----------------------------*/

	private static String getnumber(String[] zl){
		if(zl[0].equals("开")){
			number="01";
		}if(zl[0].equals("关")){
			number="02";
		}if(zl[0].equals("调温")){
			number=String.valueOf((Integer.parseInt(zl[2])-16+3));
			if(number.length()<2){
				String st ="00";
				number=st.substring(0, 2-number.length())+number;
				//System.out.println("补齐两位 "+number);
				return number;		
			}else
			{
				return number;
			}
		}
		
		return number;
	}
	
	/**拼接红外码
	 * 关机：F002DE01F5023206040137247411233104005002714E002000200010769F0136247511233104007002E849002000005040
	 * */
	public static String getGreeFrequency(String zl){
		String[] zllist = zl.split(",");
		if(zllist[0].equals("关")){
			String holder="03F002DE01F5023206040137247411233104005002714E002000200010769F0136247511233104007002E849002000005040";
			return holder;
		}
		
		StringBuffer hwm=new StringBuffer();
		hwm.append(getnumber(zllist));//
		hwm.append("F502D601FA02300604011824891123");
		hwm.append(speedsleep(zllist));
		hwm.append(modelswitch(zllist));
		hwm.append(temp_timing(zllist));
		hwm.append(timing_light(zllist));
		hwm.append("50");
		
		hwm.append("02734E0020");
		hwm.append(get_two_data(zllist));
		
		hwm.append("789F0136246D1123");
		hwm.append(speedsleep(zllist));
		hwm.append(modelswitch(zllist));
		hwm.append(temp_timing(zllist));
		hwm.append(timing_light(zllist));
		hwm.append("70");
		
		hwm.append("02D6490020");
		hwm.append(get_Four_data(zllist));
		
		
		return hwm.toString();
	}
	
//	public static void main(String [] args){
//		String zl1="关,制冷,20,一级,上下关,左右关";
//		System.out.println(getGreeFrequency(zl1));
//		
//	}
	
	
}
