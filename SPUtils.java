package com.fuyekeji.www.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;

import com.fuyekeji.www.FYTApplication;
import com.fuyekeji.www.constant.Constant;

import java.util.Map;
import java.util.Set;

public final class SPUtils {
    public static final String SP_ATTENDANCE = "sp_ocr";
    public final static String name = "config";
    public final static int mode = Context.MODE_PRIVATE;
    public static final String SCAN_ORIENTATION = "scan_orientation";
    public static final String SHOW_SCAN_CURSOR = "show_scan_cursor";

    public static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(name, mode);
    }

    /**
     * 保存首选项
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public static void saveBoolean(String key, boolean value) {
        saveBoolean(FYTApplication.context, key, value);
    }

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        Editor edit = sp.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static boolean saveString(String key, String value) {
        return saveString(FYTApplication.context, key, value);
    }

    public static boolean saveString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        Editor edit = sp.edit();
        edit.putString(key, value);
        return edit.commit();
    }

    public static boolean saveString(Context context, Map<String, String> map) {
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        Editor edit = sp.edit();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            edit.putString(key, map.get(key));
        }
        return edit.commit();
    }

    public static boolean saveDouble(Context context, String key, double value) {
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        Editor edit = sp.edit();
        edit.putLong(key, Double.doubleToRawLongBits(value));
        return edit.commit();
    }

    /**
     * 获取首选项
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        return sp.getBoolean(key, defValue);
    }

    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        return sp.getInt(key, defValue);
    }

    public static int getInt(@NonNull SharedPreferences sharedPreferences, String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static boolean getBoolean(@NonNull SharedPreferences sharedPreferences, String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static double getDouble(Context context, String key, double defValue) {
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        return Double.longBitsToDouble(sp.getLong(key,
                Double.doubleToRawLongBits(defValue)));
    }

    public static String getString(String key, String defValue) {
        return getString(FYTApplication.context, key, defValue);
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        return sp.getString(key, defValue);
    }

    public static String getString(@NonNull SharedPreferences sharedPreferences, String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public static boolean isLogin(Context context) {
        return SPUtils.getBoolean(context, Constant.SP.IS_LOGIN, Constant.test);
    }

    public static String getTraderPwd(Context context) {
        return SPUtils.getString(context, Constant.SP.DEAL_PASSWORD, null);
    }

    /**
     * ocr扫描方向
     */
    public static int getScanOrientation(@NonNull Context context, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_ATTENDANCE, Activity.MODE_PRIVATE);
        return getInt(sharedPreferences, SCAN_ORIENTATION, defaultValue);
    }

    /**
     * 是否有显示扫描指针
     */
    public static boolean getIsShowScanCursor(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_ATTENDANCE, Activity.MODE_PRIVATE);
        return getBoolean(sharedPreferences, SHOW_SCAN_CURSOR, true);
    }

    /**
     * 退出账号
     */
    public static void exitAccount() {
        SPUtils.saveString(Constant.SP.TOKEN, "");
        SPUtils.saveBoolean(Constant.SP.IS_LOGIN, false);
        SPUtils.saveBoolean(Constant.SP.IS_VIP, false);
        SPUtils.saveString(Constant.SP.PWD, null);
        SPUtils.saveString(Constant.SP.USER_NAME, "");
    }
}
