package com.example.dell.mytest.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dell on 2018/3/26.
 */

public class TimeUtils {
    public static synchronized TimeUtils getInstance() {
        return new TimeUtils();
    }

    public static String getDateBefore(Date d,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)-day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(now.getTime());
    }

    public static String timeToStr(short time) {

        int min = time / 60;
        int hour = min/60;
        int sec = time % 60;

        String strTime = String.format("%s:%s:%s",String.format("%02d",hour), String.format("%02d", min),
                String.format("%02d", sec));

        return strTime;
    }

    public static String timeforStr(short time){
        int min = (time / 60)+1;

        int hour = min / 60;
        if (hour!=0){
            min = min % 60;
        }
        String strTime = String.format("%s:%s",String.format("%02d",hour), String.format("%02d", min));
        return strTime;
    }

    public static String timeStr(short time){
        int min = time / 60;
        int hour = min/60;
        String strTime = String.format("%s:%s",String.format("%02d",hour), String.format("%02d", min));
        return strTime;
    }

    public static String getNowTime(Date d){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(d);
    }

    public static boolean getTimeCompare(String time1,String time2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(time1);
            dt2 = sdf.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtils.i("20180525","dt1:"+dt1.getTime()+" dt2:"+dt2.getTime());
        if (dt1.getTime()<dt2.getTime()){
            return true;
        }else{
            return false;
        }
    }
}
