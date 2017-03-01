package ptv.me.com.photoviewer.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by VS on 12/7/2016.
 */

public class FileUtils {
    public static String getFileName(String url){
        if(url != null){
            return url.substring(url.lastIndexOf("/"), url.lastIndexOf("."));
        }
        return null;
    }
    public static String getFileNameExt(String url){
        if(url != null){
            return url.substring(url.lastIndexOf("/"));
        }
        return null;
    }
    public static String readTxtFile(Uri uri) throws IOException {
        StringBuilder sb = new StringBuilder();
        if(uri == null || uri.getPath().isEmpty()) return sb.toString();

        BufferedReader buf = new BufferedReader(new FileReader(new File(uri.getPath())));
        String txt = null;
        while((txt = buf.readLine()) != null){
            sb.append(txt);
        }
        buf.close();
        return sb.toString();
    }
    public static Uri saveImageToGallery(Context mContext, String imgType, String name, String path){
        ContentResolver cr = mContext.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, path);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + imgType);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
        return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
    public static boolean saveImageToFile(Bitmap bmp, String path){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(path));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ;
            }
        }
        return false;
    }
    public static void notifyMediaStore(Context mContext, String path, String name){
        try {
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(), path, name, "");
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
