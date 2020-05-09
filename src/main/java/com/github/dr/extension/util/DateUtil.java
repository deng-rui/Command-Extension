package com.github.dr.extension.util;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * @author Dr
 */
public class DateUtil {

    private static long getUtcTimeStr() {
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

    public static String simp(long gmt, int fot){
		String[] ft=new String[]{"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd'T'HH:mm:ss'Z'","dd-MM-yyyy HH:mm:ss","MM-dd-yyyy HH:mm:ss"};
		return new SimpleDateFormat(ft[fot]).format(new Date(gmt));
	}

	/**
	 * 获取指定时间
	 * @param GMT 目标偏移量
	 * @param fot 目标语言喜好
	 * @return 格式化后GMT时间
	 */
    public static String longtoTime(long time){
		return simp(Long.valueOf(time) * 1000L,1);
	}

    public static long getLocalTimeFromU(){
		return Long.parseLong(getLocalTimeFromU(0,0,false));
	}

    public static long getLocalTimeFromU(long gmt){
		return Long.parseLong(getLocalTimeFromU(gmt,0,false));
	}

    public static String getLocalTimeFromU(long gmt, int fot){
		return getLocalTimeFromU(gmt,fot,true);
	}

	/**
	 * GMT = 偏移量
	 * FOT = 输出格式
	 * FOR-BOL = 是否格式化
	 */
    public static String getLocalTimeFromU(long gmt, int fot, boolean format){
		long utc = getUtcTimeStr();
		utc = utc + gmt;
		if (format) {
            return simp((Long.valueOf(utc) * 1000L) / 1000,fot);
        }
		return String.valueOf((Long.valueOf(utc) / 1000));
	}
}