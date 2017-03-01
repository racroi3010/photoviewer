package ptv.me.com.photoviewer;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ptv.me.com.photoviewer.util.Constants;

/**
 * Created by my on 17. 3. 1.
 */
public class PhotoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // load data
        String root = this.getFilesDir().getAbsolutePath();
        String pathFace = root + File.separator + Constants.faceFile;
        String pathEye = root + File.separator + Constants.eyeFile;


        File fileFace = new File(pathFace);
        File fileEye = new File(pathEye);

        Log.d("COPY_DATA", "check data");
        if(!fileFace.exists()){
            copy(Constants.faceFile, fileFace);
            Log.d("COPY_DATA", Constants.faceFile);
        }

        if(!fileEye.exists()){
            copy(Constants.eyeFile, fileEye);
            Log.d("COPY_DATA", Constants.eyeFile);
        }

    }

    private void copy(String assetPath, File file){
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = getAssets().open(assetPath);
            out = new FileOutputStream(file);
            byte[] buff = new byte[1024];
            int read = 0;

            while((read = in.read(buff)) > 0){
                out.write(buff, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in != null){
                try {
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
