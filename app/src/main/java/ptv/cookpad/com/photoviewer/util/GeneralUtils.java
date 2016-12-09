package ptv.cookpad.com.photoviewer.util;

import android.content.Context;

/**
 * Created by VS on 12/7/2016.
 */

public class GeneralUtils {
    public static String getStringFromResource(Context mContext, int id){
        return mContext.getResources().getString(id);
    }
}
