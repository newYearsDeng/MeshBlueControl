package com.northmeter.meshbluecontrol.control;
/** 格力空调红外码*/
public class GreeKTHW {
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
	 * switch    开关
	 * speed     风速标志:自动，一级，两级，三级
	 * temp      温度：16~30
	 * updown	  上下扫风
	 *leftright 左右扫风
	 *   指令模板：zl="开，模式，24度，风速，上下，左右"
	 * * 指令模板：zl="开，模式，24度，风速"
	 * */
	
	/*第一个数据--模式标识和开关*/
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
	
	/*第二个数据：风速标识/ 扫风-睡眠（存在不取值的情况）*/
	private static String speedsleep(String[] zl){
		switch(zl[3]){
		case "自动":
			speed="00";
			break;
		case "一级":
			speed="10";
			break;
		case "二级":
			speed="01";
			break;
		case "三级":
			speed="11";
			break;
		}	
		StringBuffer sbu=new StringBuffer();
		sbu.append(speed);
		sbu.append("00");
		String speedss=sbu.reverse().toString();
		String speeds=(Integer.toHexString(Integer.valueOf(speedss,2))).toUpperCase();
		return speeds;
	}
	
	/*第三个数据：温度+0      16~30度 */ 
	private static String temputer(String[] zl){
		int temputers=Integer.parseInt(zl[2].toString());
		int tempute=temputers-16;
		String tem=Integer.toHexString(tempute).toUpperCase();
		StringBuffer sbuffer=new StringBuffer();
		sbuffer.append("0");
		sbuffer.append(tem);
	    temp=sbuffer.toString();
		return temp;
	}
	
	/*第四个数据： 上下/左右*/
	private static String upDownLeRi(String[] zl){
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
		String upd=Integer.toHexString(Integer.parseInt(updown)).toUpperCase();
		String leftr=Integer.toHexString(Integer.parseInt(leftright)).toUpperCase();
		StringBuffer stb=new StringBuffer();
		stb.append(leftr);
		stb.append(upd);
		updownleftright=stb.toString();
		return updownleftright;
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
		int checkcodes=(models-1)+((Integer.parseInt(zl[2].toString()))-16)+5+leftrig+0+0;
		//System.out.println("校验码：=  "+checkcodes);
		String check=Integer.toBinaryString(checkcodes);//转为2进制
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
		String checkc=Integer.toHexString(Integer.valueOf(code,2)).toUpperCase();//反向后先转为10进制，在转为16进制
		//System.out.println("checkc "+checkc);
		StringBuffer checkco=new StringBuffer();
			checkco.append(checkc);
			checkco.append("0");
			checkcode=checkco.toString();
			//System.out.println("checkcode:"+checkcode);
			return checkcode;
			
	}
	
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
	
	
	/*红外码拼接*/
	public static String getKTHWM(String type,String zl){
	    String ktwh=null;
	    String scode="";
	    String num=null;
	    StringBuffer sbu=new StringBuffer();
	    //如果输入的空调品牌是格力
		if(type.equals("GREE"))
		{
			String[] zllist = (zl).split(",");
			if(zllist[0].equals("关")){
				String holder="8000201120004E5D02502008032311ED22EC010206D00237027E023702";
				scode=scode+holder;
				return scode;
			}
			String modelSwitch=GreeKTHW.modelswitch(zllist);
			String speedSleep=GreeKTHW.speedsleep(zllist);
			String temper=GreeKTHW.temputer(zllist);
			String updownleri=GreeKTHW.upDownLeRi(zllist);
			String checkcodes=GreeKTHW.checkCode(zllist);
		    num=GreeKTHW.getnumber(zllist);
			StringBuffer gree=new StringBuffer();
			gree.append("37027E023702D0060201EC22ED1123");
			gree.append(speedSleep);
			gree.append(modelSwitch);
			gree.append(temper);
			gree.append("2050025D4E0020");
			gree.append(updownleri);
			gree.append("2000");
			gree.append(checkcodes);
			//System.out.println("改变反向前:"+gree.toString());格力反向了一次	 
			for (int i = gree.length()/2; i > 0; i--)
            {
				ktwh=gree.substring(i * 2-2, i * 2);       	   
				scode=scode+ktwh;
				//System.out.print(ktwh); 
        	    //System.out.print("r:"+scode);
            }
			scode=scode+num;
			//System.out.println("num  "+num);
		}
		//如果输入的空调品牌是美的
		if(type.equals("MIDEA")){
			MiderKTHW.getMiderKthw(zl);		
			scode=MiderKTHW.getMiderKthw(zl);
		}
		if(type!="GREE"&type!="MIDEA")
		{
			System.out.println("空调品牌错误");
		}
		
		return  scode;
		
	}
	
	
	
//	
//	/*测试*/
//	public static void main(String[] args){
//		String[]  test={"送风","开","24","自动","上下","左"};
//		                //送风,开,自动,24,上下,不选"		
//		
//		/* 自动,制冷,加湿,送风,制热
//		 * 0  ，  4  ，  2，    6，  1  
//		 * */
//		/*  指令模板：zl="开，模式，24度，风速，上下，左右"
//			指令模板：zl="开，模式，24度，风速"*/
//		//格力
//		    String zl1="开,自动,16,自动,上下关,左右关";
//			String zl2="调温,制冷,20,一级,上下关,左右";
//			String zl3="调温,送风,24,二级,上下,左右";
//			String zl4="调温,抽湿,28,三级,上下,左右";
//			String zl5="调温,制热,30,自动,上下,左右";
//			String zl6="关,制热,24,自动,上下,左右";
//		//美的
//			String mzl1="开,自动,17,自动";
//			String mzl2="调温,制冷,20,一级";
//			String mzl3="调温,送风,24,两级";
//			String mzl4="调温,抽湿,28,三级";
//			String mzl5="调温,制热,30,自动";
//			String mzl6="关";
//			String mzl7="开,摆风";
//		
//		String sg=GreeKTHW.getKTHWM("GREE",zl6);	
//		System.out.println("格力红外编码:"+sg);
//		String sm=GreeKTHW.getKTHWM("MIDEA",mzl7);
//		System.out.println("美的红外编码:"+sm);
//	}
		
}
