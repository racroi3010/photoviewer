package ptv.me.com.photoviewer.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 */
public class PreferenceHandler extends Constants{
    private static final String PREFERENCE_NAME = "CP";
    private static final String LANGUAGE_POSITION = "LANGUAGE_POSITION";

    private static void removeAllPreference(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();


        editor.apply();
    }

    private static void setBooleanPreference(Context context, String key, boolean value){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);

        editor.apply();
    }
    private static boolean getBooleanPreference(Context context, String key){
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(key, false);
    }
    private static void setStringPreference(Context context, String key, String value){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);

        editor.apply();
    }
    private static String getStringPreference(Context context, String key, String def){
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getString(key, def);
    }
    private static void setIntPreference(Context context, String key, int value){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(key, value);

        editor.apply();
    }
    private static int getIntPreference(Context context, String key){
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(key, 0);
    }
    public static void setLanguagePositionPreference(Context context, int value){
        setIntPreference(context, LANGUAGE_POSITION, value);
    }
    public static int getLanguagePositionPreference(Context context){
        return getIntPreference(context, LANGUAGE_POSITION);
    }
//    public static void setImgDir(Context context, String dir){
//        setStringPreference(context, KEY_DIR_IMG, dir);
//    }
//    public static String getImgDir(Context context){
//        return getStringPreference(context, KEY_DIR_IMG,DIR_IMG);
//    }
}
