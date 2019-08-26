package com.northmeter.meshbluecontrol.control;
/**美的空调红外码*/
public class MiderKTHW {
	/* 
	 * model     模式标志:自动,制冷,抽湿,制热,送风
	 * switvh    开关   07024D020702A30602015611C511304DB2DE2107F83015015611C611304DB2DE2107F8
	 * speed     风速标志:自动，低风，中风，高风
	 * temp      温度：16~30
	 * 指令模板：zl="开，模式，24度，风速"
	 * */
	private static String number;
	private static String model;
	private static String temputer;
	private static String speed;
	private static String miderKthw;
	private String[] zl={"开","自动","17","自动"};
	/*模式 编码*/
	private static String getModel(String []zl){
		switch(zl[1]){
		case "自动":
			model="0001";
			break;
		case "制冷":
			model="0000";
			break;
		case "抽湿":
			model="0010";
			break;
		case "制热":
			model="0011";
			break;
		case "送风":
			model="0010";
			break;
		}
		String models=Integer.toHexString(Integer.valueOf(model, 2)).toUpperCase();		
		return models;
	}
	
	/*温度编码*/
	private static String getTemp(String[] zl){
		if(zl[1].equals("送风")){
			temputer="0111";
			String temp=Integer.toHexString(Integer.valueOf(temputer,2)).toUpperCase();
			return temp;
		}else{
		switch(zl[2]){
		case "17":
			temputer="0000";
			break;
		case "18":
			temputer="1000";
			break;
		case "19":
			temputer="1100";
			break;
		case "20":
			temputer="0100";
			break;
		case "21":
			temputer="0110";
			break;
		case "22":
			temputer="1110";
			break;
		case "23":
			temputer="1010";
			break;
		case "24":
			temputer="0010";
			break;
		case "25":
			temputer="0011";
			break;
		case "26":
			temputer="1011";
			break;
		case "27":
			temputer="1001";
			break;
		case "28":
			temputer="0001";
			break;
		case "29":
			temputer="0101";
			break;
		case "30":
			temputer="1101";
			break;
		}
		String temp=Integer.toHexString(Integer.valueOf(temputer,2)).toUpperCase();		
		return temp;
		}
	}
	
	/*风速编码*/
	private static String getSpeed(String[] zl){
		if(zl[1].equals("抽湿")|zl[1].equals("自动")){
			speed="1000";
		}else
		{
		switch(zl[3]){
		case"自动":
			speed="1101";
			break;
		case"一级":
			speed="1001";
			break;
		case"两级":
			speed="1010";
			break;
		case"三级":
			speed="1100";
			break;
		}
		}
		String speeds=Integer.toHexString(Integer.valueOf(speed, 2)).toUpperCase();
		return speeds;
	}
	
	/*指定红外码编码 C*/
	private static String getMiderC(String[] zl){
		String models=MiderKTHW.getModel(zl);
		String temp=MiderKTHW.getTemp(zl);
		String cm=models+temp;
		//System.out.println("cm="+cm);
		return cm;
	}
	/*指定红外码编码 C的反码*/
	private static String getReverseC(String[] zl){
		String codec="";
		String former=MiderKTHW.getMiderC(zl);
		int intformer=Integer.parseInt(Integer.valueOf(former,16).toString());//16进制转换为10进制
		String intform=Integer.toBinaryString(intformer);//10进制转换为2进制
		//补齐8位后才计算补码
		String str ="00000000";
		intform=str.substring(0, 8-intform.length())+intform;
		//System.out.println("二进制C码 "+intform);
	    char[] code=intform.toCharArray();
		for(int i=0;i<code.length;i++){
			if(code[i]=='0' ){
				code[i]='1';
				codec=codec+code[i];
			}else{
				code[i]='0';
				codec=codec+code[i];
			}
			
		}
		//System.out.println("codec "+codec);	
		String reverseC=Integer.toHexString(Integer.valueOf(codec,2)).toUpperCase();
		//System.out.println("C反码 "+reverseC);
		if(reverseC.length()<2){
			String st ="00";
			reverseC=st.substring(0, 2-reverseC.length())+reverseC;
			//System.out.println("补齐两位 "+reverseC);
			return reverseC;		
		}else{
			return reverseC;
		}
	}
	
	
	/*指定红外码编码 B*/
	private static String getMiderB(String[] zl){
		String speeds=MiderKTHW.getSpeed(zl);
		String bm="F"+speeds;
		//System.out.println("bm="+bm);
		return bm;
	}
	/*指定红外码编码 B的反码*/
	private static String getReverseB(String[] zl){
		String codeb="";
		String former=MiderKTHW.getMiderB(zl);
		int intformer=Integer.parseInt(Integer.valueOf(former,16).toString());//16进制转换为10进制
		String intform=Integer.toBinaryString(intformer);//10进制转换为2进制
		//System.out.println("");
		//补齐8位后才计算补码
		String str ="00000000";
		intform=str.substring(0, 8-intform.length())+intform;		
		char[] intfo=intform.toCharArray();
		//System.out.println("二进制B码 "+intform);
		for(int i=0;i<intfo.length;i++){
			if(intfo[i]=='0' ){
				intfo[i]='1';
				codeb=codeb+intfo[i];
			}else{
				intfo[i]='0';
				codeb=codeb+intfo[i];
			}			
		}
		//System.out.println("codeb "+codeb);
		String reverseB=Integer.toHexString(Integer.valueOf(codeb, 2)).toUpperCase();
		//System.out.println("B反码:"+reverseB); 
		if(reverseB.length()<2){
			String st ="00";
			reverseB=st.substring(0, 2-reverseB.length())+reverseB;
			//System.out.println("补齐两位 "+reverseB);
			return reverseB;		
		}else{
			return reverseB;
		}
	}
	
	/*获取编号*/
	private static String getnumber(String[] zl){
		if(zl[0].equals("开")){
			number="01";
		}if(zl[0].equals("关")){
			number="02";
		}if(zl[0].equals("调温")){
			number=String.valueOf((Integer.parseInt(zl[2])-17+3));
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
	
	/*拼接触红外码*/
	public static String getMiderKthw(String zl){
		String numb=null;
		String[] zllist = (zl).split(",");
		numb=getnumber(zllist);
		if(zllist[0].equals("关")){
		  //miderKthw="07024D020702A30602015611C511304DB2DE2107F83015015611C611304DB2DE2107F8";
			miderKthw=numb+"08024C020702A30602015911C611304DB2DE2107F83115015A11C411304DB2DE2107F8";
			return miderKthw;
		}
		if(zllist[1].equals("摆风")){
			miderKthw=numb+"D3018302D301A40602010511B311304DB2D62907F8E614010411B211304DB2D62907F8";
			return miderKthw;
		}
		StringBuffer Kthw=new StringBuffer();		
		String Bm=MiderKTHW.getMiderB(zllist);
		String Cm=MiderKTHW.getMiderC(zllist);
		String reverseB=MiderKTHW.getReverseB(zllist);
		String reverseC=MiderKTHW.getReverseC(zllist);
		//08024C020702A20602015711C211304DB2 F80710EF 2E15015611C2 11304DB2 F80710EF 
		Kthw.append(numb);
		Kthw.append("08024C020702A20602015711C211304DB2");		             
		Kthw.append(Bm);
		Kthw.append(reverseB);
		Kthw.append(Cm);
		Kthw.append(reverseC);
		Kthw.append("2E15015611C2");
		Kthw.append("11304DB2");
		Kthw.append(Bm);
		Kthw.append(reverseB);
		Kthw.append(Cm);
		Kthw.append(reverseC);
		miderKthw=Kthw.toString();
		return miderKthw;
	}
	
	
//	public static void main(String[] args){
////		String[]  zl={"开","送风","28","自动"};
////		String s=MiderKTHW.getModel(zl);
////		String t=MiderKTHW.getTemp(zl);
////		String r=MiderKTHW.getSpeed(zl);
////		MiderKTHW.getMiderC(zl);
////		MiderKTHW.getMiderB(zl);
////		MiderKTHW.getReverseC(zl);
////		MiderKTHW.getReverseB(zl);
////		System.out.println(s+t+r);
//		String zllist="调温,送风,19,一级";
//		System.out.println(MiderKTHW.getMiderKthw(zllist));
//	}
}
