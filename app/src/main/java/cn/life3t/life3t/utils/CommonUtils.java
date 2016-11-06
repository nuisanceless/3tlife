package cn.life3t.life3t.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lei on 2015/5/13.
 */
public class CommonUtils {
    public static boolean isValidPhoneNumber(String phoneNo) {
        String expression = "^1(3|4|5|7|8)\\d{9}$";
        CharSequence inputStr = phoneNo;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return (matcher.matches())? true : false;
    }

    public static boolean isValidTelNumber(String number) {
        String expression = "^(0[0-9]{2,3}\\-)?([0-9]{6,8})+(\\-[0-9]{1,8})?$";
        CharSequence inputStr = number;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return (matcher.matches())? true : false;
    }

    public static boolean isValidEmail(String target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static int getScreenWidth (Context context)
    {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        return width;
    }

    public static int getScreenHeight (Context context)
    {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.widthPixels;
        return height;
    }

    public static String getGenderString(int gender) {
        return Consts.GENDER_STRING_ARRAY[gender];
    }

    public static int getGenderInt (String gender)
    {
        if (gender.equals("男"))
            return 0;
        else if (gender.equals("女"))
            return 1;
        else
            return 2;
    }

    public static boolean isBeforToday (long time)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long todayBeginTime = calendar.getTimeInMillis();

        return time < todayBeginTime ? true : false;

    }

}
