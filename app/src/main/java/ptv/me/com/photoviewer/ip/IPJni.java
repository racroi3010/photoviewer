package ptv.me.com.photoviewer.ip;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;

/**
 * Created by my on 17. 2. 27.
 */
public class IPJni {

    static{
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        } else {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.loadLibrary("gnustl_shared");
//            System.loadLibrary("opencv_java3");
//        System.loadLibrary("opencv_core");
            System.loadLibrary("IPJni");
        }


    }
    public native String helloJNI(String name);
    public native void processImage(long input, long output);
    public native void histEqualize(long input, long output);
    public native void faceDetect(long input, long output
            , String face_cascade_path, String eye_cascade_path);
    public native void denoising(long input, long output);

}
