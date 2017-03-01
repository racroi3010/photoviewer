package ptv.me.com.photoviewer.presenter;

import android.content.Context;
import android.provider.MediaStore;

/**
 * this presenter is used for view activity
 * Created by VS on 12/7/2016.
 */

public class ImageViewPresenter {
    private Context mContext;
    private IVViewer viewer;

    public ImageViewPresenter(Context mContext, IVViewer viewer) {
        this.mContext = mContext;
        this.viewer = viewer;
    }

    public void deleteImage(String path){
        int row = mContext.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + path + "'", null);
        if(row < 0){
            viewer.showError("Failed");
        } else {
            viewer.onRemoveOK();
        }
    }
    public interface IVViewer{
        public void onRemoveOK();
        public void showError(String msg);
    }
}


