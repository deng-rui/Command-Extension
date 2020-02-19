package extension.tool;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;


public class DateUtil {
	public static long getUTCTimeStr() {
		Calendar cal = Calendar.getInstance(); 
		//2、取得时间偏移量：  
		final int zoneOffset = cal.get(Calendar.ZONE_OFFSET); 
		//3、取得夏令时差：  
		final int dstOffset = cal.get(Calendar.DST_OFFSET);  
		//4、从本地时间里扣除这些差量，即可以取得UTC时间：  
		cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		return cal.getTimeInMillis();
	}
 
	public static String getLocalTimeFromUTC(long GMT, int fot){
		String[] ft=new String[]{"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd'T'HH:mm:ss'Z'","dd-MM-yyyy HH:mm:ss","MM-dd-yyyy HH:mm:ss"};
		long UTC = getUTCTimeStr();
		System.out.println(UTC);
		UTC = UTC + GMT;
		SimpleDateFormat sdf=new SimpleDateFormat(ft[fot]);
		long date_temp = Long.valueOf(UTC/1000);    
		return sdf.format(new Date(date_temp * 1000L));
	}
}