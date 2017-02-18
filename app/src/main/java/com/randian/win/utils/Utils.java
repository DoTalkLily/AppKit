package com.randian.win.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.randian.win.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by a42 on 14-4-13.
 */
public class Utils {

    public static final String getDeviceId(Context context) {
         return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static final String generateDeviceID(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (deviceId == null) {
            deviceId = "";
        }
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId == null) {
            androidId = "";
        }
        String serialId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            serialId = Build.SERIAL;
            if (serialId == null) {
                serialId = "";
            }
        } else {
            serialId = getDeviceSerial();
        }


        String macAddress = "";
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wm.getConnectionInfo();
        if (wifiInfo != null) {
            macAddress = wifiInfo.getMacAddress();
            if (macAddress == null) {
                macAddress = "";
            }
        }
        try {
            return getSHAString(deviceId + androidId + serialId + macAddress);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String getDeviceSerial() {
        String serial = "";
        try {
            Class clazz = Class.forName("android.os.Build");
            Class paraTypes = Class.forName("java.lang.String");
            Method method = clazz.getDeclaredMethod("getString", paraTypes);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            serial = (String) method.invoke(new Build(), "ro.serialno");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * 获得sha-1
     * @param value
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static final String getSHAString(String value) throws NoSuchAlgorithmException {
        byte[] hash = MessageDigest.getInstance("SHA-1").digest(value.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    /**
     * 字符串转md5
     * @param value
     * @return
     */
    public final static String getMD5String(String value) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            byte[] btInput = value.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 检查油箱格式
     * @param email
     * @return
     */
    public static final boolean checkEmailFormat(String email){
        if(TextUtils.isEmpty(email)){
            return false;
        }

        String check = "^([a-z0-9A-Z]+[_-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }

    /**
     * 检查手机号格式
     * @param phone
     * @return
     */
    public static final boolean checkPhoneFormat(String phone){
        if(TextUtils.isEmpty(phone)){
            return false;
        }

        String check = "^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(phone);
        return matcher.matches();
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static void hideKeyboard(Activity activity) {
        ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static void setBtnDisable(TextView btn,int word,Resources resource){
        btn.setEnabled(false);
        btn.setText(resource.getString(word));
        btn.setBackgroundColor(resource.getColor(R.color.f4f4f4));
        btn.setTextColor(resource.getColor(R.color.feedback));
    }

    public static void setBtnEnable(TextView btn,int word,Resources resource){
        btn.setEnabled(true);
        btn.setText(resource.getString(word));
        btn.setBackgroundColor(resource.getColor(R.color.orange));
        btn.setTextColor(resource.getColor(R.color.white));
    }

    public static void commentStar(float score,ViewGroup container,Context context){
        for(int i = 0;i<5;i++){
            if(i+0.8<=score){
                container.addView(createImage(R.drawable.star_yellow,context));
            }else if(i+0.3 <= score){
                container.addView(createImage(R.drawable.star_half,context));
            }else{
                container.addView(createImage(R.drawable.star_gray,context));
            }
        }
    }

    public static ImageView createImage(int url,Context context){
        ImageView image = new ImageView(context);
        image.setImageResource(url);
        image.setAdjustViewBounds(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(25,25);
        layoutParams.setMargins(0, 0, 10, 0);
        image.setLayoutParams(layoutParams);
        return image;
    }
}
