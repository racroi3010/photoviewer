package ptv.cookpad.com.photoviewer.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

import ptv.cookpad.com.photoviewer.R;
import ptv.cookpad.com.photoviewer.activity.BaseActivity;
import ptv.cookpad.com.photoviewer.util.GeneralUtils;
import ptv.cookpad.com.photoviewer.util.ImageProcessingUtils;

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
            ((BaseActivity)viewer).hideProgressDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((BaseActivity)viewer).showProgressDialog(GeneralUtils.getStringFromResource(mContext, R.string.processing));
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
            ((BaseActivity)viewer).hideProgressDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((BaseActivity)viewer).showProgressDialog(GeneralUtils.getStringFromResource(mContext, R.string.processing));
        }
    }
    public interface IEViewer{
        public void showError(String title, String msg);
        public void update(Bitmap bmp);
    }
}
