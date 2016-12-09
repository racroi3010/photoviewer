package ptv.cookpad.com.photoviewer.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by VS on 12/7/2016.
 */

public class ImageProcessingUtils {
    public static Bitmap rotate(Bitmap bmp, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }
}
