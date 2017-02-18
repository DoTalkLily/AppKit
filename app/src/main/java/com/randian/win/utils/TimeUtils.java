package com.randian.win.utils;

import android.text.TextUtils;

import com.randian.win.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: zeyiwu
 * Date: 13-9-4
 * Time: 下午5:27
 */
public class TimeUtils {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat dfTopic = new SimpleDateFormat("MM-dd HH:mm:ss");
    private static SimpleDateFormat dfGroup = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat dfChat = new SimpleDateFormat("MM-dd");

    private static final int DATE_LENGTH = 11;
    private static final int TIME_LENGTH = 15;

    public static String getTimebyTimestamp(long timestamp) {
        return df.format(new Date(timestamp));
    }

    public static String getTimeWithFormat(long timestamp,String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date(timestamp));
    }

    public static String makeDateTimeString(String originString, boolean topic) {
        if (topic) {
            try {
                Date date = df.parse(originString);
                Date dateNow = new Date();
                if (dateNow.getYear() != date.getYear()) {
                    return dfGroup.format(date);
                }
                return dfTopic.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            try {
                Date date = df.parse(originString);
                return dfGroup.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public static String getTime(long time) {
        long timePassed = (System.currentTimeMillis() - time) / 1000;
        if (timePassed <= 0) {
            return "刚刚";
        } else if (timePassed < 60) {
            return ((int) timePassed) + "秒前";
        }  else if (timePassed < 60 * 60) {
            return ((int) (timePassed / 60)) + "分钟前";
        } else if (timePassed < 24 * 60 * 60) {
            return ((int) (timePassed / 60 / 60)) + "小时前";
        } else if (timePassed < 10 * 24 * 60 * 60) {
            return ((int) (timePassed / 60 / 60 / 24)) + "天前";
        } else {
            try {
                Date date = new Date(time);
                return dfChat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public static Date revertStringToDate(String time) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String getYMD(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        if (time.length() < DATE_LENGTH) {
            return time;
        }
        return time.substring(0, 10);
    }

    public static String getHMS(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        if (time.length() < TIME_LENGTH) {
            return time;
        }
        return time.substring(11, time.length() - 3);
    }

    public static long getTimeInterval(String time1, String time2) {
        Date date1 = revertStringToDate(time1);
        Date date2 = revertStringToDate(time2);
        if (date1 == null || date2 == null) {
            return -1;
        }
        return Math.abs(date1.getTime() - date2.getTime());
    }

    public static boolean isToday(String time){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = df.parse(time);
            Date current = new Date();
            if (date.getYear() == current.getYear() &&
                    date.getMonth() == current.getMonth() &&
                    date.getDay() == current.getDay()) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Deprecated
    public static String getNow() {
        return df.format(new Date());
    }

    public static String secToTime(int time)
    {
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d:%02d", hour,minute, second);
    }

    public static long getNowTimestamp() {
        return System.currentTimeMillis();
    }

    public static int getDateLength(String time){
        if(isToday(time)){
            return 1;
        }

        long day = 0;
        try {
            Date expire = dfGroup.parse(time);
            day= (expire.getTime()-System.currentTimeMillis())/1000/3600/24 + 2;
        }catch(Exception e){
            LogUtils.e("error",e);
        }

        return (int)day;
    }

}
