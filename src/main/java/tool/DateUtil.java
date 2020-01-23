package extension.tool;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateUtil {

    public static String timeStamp2Date(String seconds) {  
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){  
            return "";  
        }  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        return sdf.format(new Date(Long.valueOf(seconds+"000")));  
    }  

    public static String date2TimeStamp(String date_str){  
        try {  
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
            return String.valueOf(sdf.parse(date_str).getTime()/1000);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return "";  
    }  

    public static String timeStamp(){  
        long time = System.currentTimeMillis();
        String t = String.valueOf(time/1000);  
        return t;  
    }  

    public static boolean iftime(int a,int b) throws ParseException
    {
        //如果想比较日期则写成"yyyy-MM-dd"就可以了
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //将字符串形式的时间转化为Date类型的时间
        //Date类的一个方法，如果a早于b返回true，否则返回false
        if(a-b<0) {
            System.out.println("A");return true;
        }else{
            System.out.println("BDtime");return false;
}
    }

}