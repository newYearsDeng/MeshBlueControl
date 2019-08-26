package com.northmeter.meshbluecontrol.utils;

public class MyResult {

	// 各个参数的位数
	private int[] dataLen = { 8, 4, 6, 6, 6, 4, 4, 2 };
	/**
	 * 以下均为原始返回数据，要通过 get 方法处理才能得到正确的数据
	 */
	// 总电能 8
	private String zdn;
	// 电压 4
	private String dy;
	// 电流 6
	private String dl;
	// 功率 6
	private String gl;
	// 无功???? 6
	private String wg;
	// 频率 4
	private String pl;
	// 功率因数 4
	private String glys;
	// 跳合闸状态 2  合闸：00   跳闸：01
	private String thzzt;

	public MyResult() {

	}

	public MyResult(String result) {
		int i = 0;
		int start = 0;
		int end = dataLen[i];

		// 总电能 8
		zdn = result.substring(start, end);
		// 电压 4
		start = end;
		end += dataLen[++i];
		dy = result.substring(start, end);
		// 电流 6
		start = end;
		end += dataLen[++i];
		dl = result.substring(start, end);
		// 功率 6
		start = end;
		end += dataLen[++i];
		gl = result.substring(start, end);
		// 无功???? 6
		start = end;
		end += dataLen[++i];
		wg = result.substring(start, end);
		// 频率 4
		start = end;
		end += dataLen[++i];
		pl = result.substring(start, end);
		// 功率因数 4
		start = end;
		end += dataLen[++i];
		glys = result.substring(start, end);
		// 跳合闸状态 2
		start = end;
		end += dataLen[++i];
		thzzt = result.substring(start, end);
	}

	/**
	 * 对数据进行两位两位的反转 如：01 20 -> 20 01
	 * 
	 * @param data
	 *            需要反转的数据
	 * @return String 反转后的数据
	 */
	public String inverseData(String data) {
		String rtn = "";
		int start = 0;

		int len = data.length();
		String next = data.substring(start, start + 2);
		while (next.length() > 0) {
			rtn = next + rtn;

			start += 2;
			if (start < len)
				next = data.substring(start, start + 2);
			else
				break;
		}

		return rtn;
	}

	// 总电能 8
	public String getZdn() {
		String rtn = "";
		rtn = inverseData(zdn);
		rtn = rtn.substring(0, 6) + "." + rtn.substring(6, 8);
		return rtn;
	}

	// 电压 4
	public String getDy() {
		String rtn = "";
		rtn = inverseData(dy);
		rtn = rtn.substring(0, 3) + "." + rtn.substring(3, 4);
		return rtn;
	}

	// 电流 6
	public String getDl() {
		String rtn = "";
		rtn = inverseData(dl);
		rtn = rtn.substring(0, 3) + "." + rtn.substring(3, 6);
		return rtn;
	}

	// 功率 6
	public String getGl() {
		String rtn = "";
		rtn = inverseData(gl);
		rtn = rtn.substring(0, 2) + "." + rtn.substring(2, 6);
		return rtn;
	}

	// 无功???? 6
	public String getWg() {
		String rtn = "";
		rtn = inverseData(wg);
		if(!wg.equals("FFFFFF")){
			if(rtn.substring(2, 3).equals("0")){
				rtn = rtn.substring(3, 5) + "." + rtn.substring(5, 6);//这里把无功改为温度显示
			}else{
				rtn = "-"+ rtn.substring(3, 5) + "." + rtn.substring(5, 6);//这里把无功改为温度显示
			}
			
		}else{
			rtn = "0";
		}
		// rtn = rtn.substring(0, 3) + "." + rtn.substring(3, 4);
		return rtn;
	}

	// 频率 4
	public String getPl() {
		String rtn = "";
		rtn = inverseData(pl);
		rtn = rtn.substring(0, 2) + "." + rtn.substring(2, 4);
		return rtn;
	}

	// 功率因数 4
	public String getGlys() {
		String rtn = "";
		rtn = inverseData(glys);
		rtn = rtn.substring(0, 1) + "." + rtn.substring(1, 4);
		return rtn;
	}

	// 跳合闸状态 2
	public String getThzzt() {
		String rtn = "";
		rtn = inverseData(thzzt);
		// rtn = rtn.substring(0, 1) + "." + rtn.substring(1, 2);
		return rtn;
	}
}
