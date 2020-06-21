package com.saravana.befit.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static final String SP_DAY= "DAY";
    public static final String SP_LAST_DONE = "LAST_DONE";

    //DB table columns
    public static String ID="ID";
    public static String DAY="DAY";
    public static String DATE= "DATE";
    public static final String INTERVAL_MIN_KEYS="INTERVAL_MINUTES";
    public static final String TOTAL_MIN = "total_minutes";

    public static final String NOTIF_SP = "notification_shared_pref";
    public static final String NOTIF_SP_DAY_RANGE = "notification_day_range";
    public static final String NOTIF_SP_TIME_HOUR = "notification_hour";
    public static final String NOTIF_SP_TIME_MINUTE = "notification_hour_minute";
    public static final String NOTIF_SP_ON = "notification_on_status";
    public static final String SETTINGS_MUSIC_ON = "music_on";

    public static final String NOTIF_STATUS = "notification_status";


    public static boolean contains(String[] arr, String key){
        for(String k : arr){
            if(k.equals(key))
                return true;
        }
        return false;
    }
    public static String getDateString(Date d,boolean isIso8601Format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(d) + (isIso8601Format ? "00:00:00" : "");
        /*sql Data datatype accepts
        yYYY-MM-DD
        YYYY-MM-DD HH:MM
        YYYY-MM-DD HH:MM:SS
        YYYY-MM-DD HH:MM:SS.SSS
        YYYY-MM-DDTHH:MM
        YYYY-MM-DDTHH:MM:SS
        YYYY-MM-DDTHH:MM:SS.SSS
        HH:MM
        HH:MM:SS
        HH:MM:SS.SSS
        now
        DDDDDDDDDD

         */
    }
    public static Date getDateFromString(String d,String format){
        if(format==null)
            format = "yyyy-MM-dd";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        } catch(NullPointerException ne){
            ne.printStackTrace();
            return new Date();
        }
    }
    public static Date addToDate(Date date,int field,int daysTobeAdded){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field,daysTobeAdded);
        return calendar.getTime();
    }
    public  static  int getDay(Date date,String dateStr,String dateFormat){
        //dateStr format yyyy-MM-dd
        if(date==null)
            date = getDateFromString(dateStr,dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    public static int getMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }
    public  static  int getYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMaximumDayInMonth(int month,int year){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.YEAR,year);

        return calendar.getActualMaximum(calendar.DAY_OF_MONTH);
    }


}
