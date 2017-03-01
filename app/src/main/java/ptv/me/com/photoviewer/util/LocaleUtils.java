package ptv.me.com.photoviewer.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by VS on 12/8/2016.
 */

public class LocaleUtils {

    public static void setLocale(Context context, int position){
        String language = "en";
        switch (position) {
            case 0:
                language = "en";
                break;
            case 1:
                language = "ko";
                break;
            default:
                language = "en";
                break;
        }
        Locale myLocale = new Locale(language);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        PreferenceHandler.setLanguagePositionPreference(context, position);
    }
}
