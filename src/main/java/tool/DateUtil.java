package extension.tool;



public class DateUtil {
	public static String getUTCTimeStr() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 
		StringBuffer UTCTimeBuffer = new StringBuffer();
		Calendar cal = Calendar.getInstance();
		int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
		int dstOffset = cal.get(Calendar.DST_OFFSET);
		cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		UTCTimeBuffer.append(year).append("-").append(month).append("-").append(day);
		UTCTimeBuffer.append(" ").append(hour).append(":").append(minute).append(":").append(second );
		try {
			format.parse(UTCTimeBuffer.toString());
			return UTCTimeBuffer.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 
		return null;
	}
 
	   public static String getLocalTimeFromUTC(String UTCTime, String ){
		Date UTCDate;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 
		String localTimeStr = null ;
		try {
			UTCDate = format.parse(UTCTime);
			format.setTimeZone(TimeZone.getTimeZone("GMT-8")) ;
			localTimeStr = format.format(UTCDate) ;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 
		return localTimeStr ;
	}
}