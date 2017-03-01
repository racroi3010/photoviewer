package ptv.me.com.photoviewer.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

import ptv.me.com.photoviewer.R;
import ptv.me.com.photoviewer.activity.BaseActivity;
import ptv.me.com.photoviewer.activity.EditActivity;
import ptv.me.com.photoviewer.ip.IPJni;
import ptv.me.com.photoviewer.util.Constants;
import ptv.me.com.photoviewer.util.GeneralUtils;
import ptv.me.com.photoviewer.util.ImageProcessingUtils;

/**
 * this presenter is used for edit activity
 * Created by VS on 12/7/2016.
 */

public class ImageEditPresenter {
    private Context mContext;
    private IEViewer viewer;
    private Bitmap mBmp;

    public ImageEditPresenter(Context mContext, IEViewer viewer) {
        this.mContext = mContext;
        this.viewer = viewer;
    }
    public void setImage(String path){
        new UndoTask().execute(path);
    }

    public void setmBmp(Bitmap mBmp) {
        this.mBmp = mBmp;
    }

    public void saveImage(String name){
        if(mBmp != null && name != null && !name.isEmpty()){
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(), mBmp, name, "");
        } else if(name == null || name.isEmpty()) {
            viewer.showError(GeneralUtils.getStringFromResource(mContext, R.string.error), GeneralUtils.getStringFromResource(mContext, R.string.title_save_error));
        }
    }
    public void rotateImage(float degree){
        new IPTask().execute(degree);
    }
    public void faceDetection(){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                if(mBmp != null) {
                    IPJni ip = new IPJni();
                    Mat in = new Mat();
                    Mat out = new Mat();

                    Utils.bitmapToMat(mBmp, in);



                    Log.e("TEST", "before");
                    try {
                        String root = mContext.getFilesDir().getAbsolutePath();
                        String pathFace = root + File.separator + Constants.faceFile;
                        String pathEye = root + File.separator + Constants.eyeFile;
                        ip.faceDetect(in.getNativeObjAddr(), out.getNativeObjAddr(), pathFace, pathEye);
                    }catch (Exception ex){
                        Log.e("TEST", ex.getMessage());
                    }

                    Log.e("TEST", "after");
                    Utils.matToBitmap(out, mBmp);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                viewer.update(mBmp);
                ((EditActivity)viewer).hideProgressDialog();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((EditActivity)viewer).showProgressDialog(GeneralUtils.getStringFromResource(mContext, R.string.processing));
            }
        }.execute();
    }
    public void histEqualization(){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                if(mBmp != null) {
                    IPJni ip = new IPJni();
                    Mat in = new Mat();
                    Mat out = new Mat();

                    Utils.bitmapToMat(mBmp, in);



                    Log.e("TEST", "before");
                    try {

                        ip.histEqualize(in.getNativeObjAddr(), out.getNativeObjAddr());
                    }catch (Exception ex){
                        Log.e("TEST", ex.getMessage());
                    }

                    Log.e("TEST", "after");
                    Utils.matToBitmap(out, mBmp);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                viewer.update(mBmp);
                ((EditActivity)viewer).hideProgressDialog();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((EditActivity)viewer).showProgressDialog(GeneralUtils.getStringFromResource(mContext, R.string.processing));
            }
        }.execute();
    }
    public void denosing(){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                if(mBmp != null) {
                    IPJni ip = new IPJni();
                    Mat in = new Mat();
                    Mat out = new Mat();

                    Utils.bitmapToMat(mBmp, in);



                    Log.e("TEST", "before");
                    try {

                        ip.denoising(in.getNativeObjAddr(), out.getNativeObjAddr());
                    }catch (Exception ex){
                        Log.e("TEST", ex.getMessage());
                    }

                    Log.e("TEST", "after");
                    Utils.matToBitmap(out, mBmp);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                viewer.update(mBmp);
                ((EditActivity)viewer).hideProgressDialog();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((EditActivity)viewer).showProgressDialog(GeneralUtils.getStringFromResource(mContext, R.string.processing));
            }
        }.execute();
    }
    private class IPTask extends AsyncTask<Float, Void, Void> {

        @Override
        protected Void doInBackground(Float... params) {
            if(mBmp != null) {
                mBmp = ImageProcessingUtils.rotate(mBmp, params[0]);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            viewer.update(mBmp);
            ((EditActivity)viewer).hideProgressDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((EditActivity)viewer).showProgressDialog(GeneralUtils.getStringFromResource(mContext, R.string.processing));
        }
    }
    private class UndoTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            mBmp = BitmapFactory.decodeFile(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ((EditActivity)viewer).hideProgressDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((EditActivity)viewer).showProgressDialog(GeneralUtils.getStringFromResource(mContext, R.string.processing));
        }
    }
    public interface IEViewer{
        public void showError(String title, String msg);
        public void update(Bitmap bmp);
    }
}
