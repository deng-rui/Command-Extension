package extension.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
//Java

public class DateUtil {
	private static long getUTCTimeStr() {
		// 获取JDK当前时间
		Calendar cal = Calendar.getInstance();
		// 取得时间偏移量  
		final int zoneOffset = cal.get(Calendar.ZONE_OFFSET); 
		// 取得夏令时差 
		final int dstOffset = cal.get(Calendar.DST_OFFSET);  
		// 从本地时间里扣除这些差量，即可以取得UTC时间
		cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		return cal.getTimeInMillis();
	}
	/**
	 * 获取指定时间
	 * @param GMT 目标偏移量
	 * @param fot 目标语言喜好
	 * @return 格式化后GMT时间
	 */

	public static String getLocalTimeFromUTC(long GMT){
		return getLocalTimeFromUTC(GMT,0,false);
	}

	public static String getLocalTimeFromUTC(long GMT, int fot){
		return getLocalTimeFromUTC(GMT,fot,true);
	}

	public static String getLocalTimeFromUTC(long GMT, int fot, boolean slf){
		String[] ft=new String[]{"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd'T'HH:mm:ss'Z'","dd-MM-yyyy HH:mm:ss","MM-dd-yyyy HH:mm:ss"};
		long UTC = getUTCTimeStr();
		// UTC时间加上偏移量 即取得目标时间
		UTC = UTC + GMT;
		if (slf) {
			SimpleDateFormat sdf=new SimpleDateFormat(ft[fot]);
			return sdf.format(new Date(Long.valueOf(UTC/1000) * 1000L));
		}
		return String.valueOf(Long.valueOf(UTC));
	}
}