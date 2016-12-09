package ptv.cookpad.com.photoviewer.presenter;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import ptv.cookpad.com.photoviewer.R;
import ptv.cookpad.com.photoviewer.activity.BaseActivity;
import ptv.cookpad.com.photoviewer.model.ImageItem;
import ptv.cookpad.com.photoviewer.model.ImageItemList;
import ptv.cookpad.com.photoviewer.service.SearchService;
import ptv.cookpad.com.photoviewer.util.Constants;
import ptv.cookpad.com.photoviewer.util.FileUtils;
import ptv.cookpad.com.photoviewer.util.GeneralUtils;
import ptv.cookpad.com.photoviewer.util.JsonUtils;
import ptv.cookpad.com.photoviewer.util.ValidationUtil;

/**
 * this presenter is used for home activity
 * Created by VS on 12/6/2016.
 */

public class ImageLoadingPresenter {
    private Context mContext;
    private ILViewer viewer;
    private int mMode;
    private long mDownloadId;
    private List<ImageItem> mGoogleItems;
    private String mUrl;
    public static final int IMAGE_SRC_GALLERY = 0;
    public static final int IMAGE_SRC_SEARCH = 1;


    public ImageLoadingPresenter(Context mContext, ILViewer viewer) {
        this.mContext = mContext;
        this.viewer = viewer;
        this.mGoogleItems = new ArrayList<ImageItem>();
    }

    /**
     * load all image from gallery or search
     * @param imageSource type of source 0 for Gallery and 1 for search
     * @param url url if search mode
     */
    public void loadImage(int imageSource, String url){

        switch (imageSource){
            case IMAGE_SRC_SEARCH:
                LoadSearch(url);
                break;
            case IMAGE_SRC_GALLERY:
            default:
                new LoadGalleryTask().execute();
                break;
        }

    }
    private void LoadSearch(String url){
        this.mGoogleItems.clear();
        this.mUrl = url;
        if(ValidationUtil.validateURL(url)){
            mMode = SearchService.MODE_URL;
            ((BaseActivity)viewer).showProgressDialog(GeneralUtils.getStringFromResource(mContext, R.string.loading));
            mDownloadId = SearchService.getInstance(mContext).open(url, SearchService.MODE_URL);
        } else if(url != null && !url.isEmpty()) {
            ((BaseActivity)viewer).showProgressDialog(GeneralUtils.getStringFromResource(mContext, R.string.loading));
            mMode = SearchService.MODE_GOOGLE;
            mDownloadId = SearchService.getInstance(mContext).open(buildURL(url, 1), SearchService.MODE_GOOGLE);
        } else {
            viewer.showError(GeneralUtils.getStringFromResource(mContext, R.string.error) , GeneralUtils.getStringFromResource(mContext, R.string.input_error));
        }
    }

    private class LoadGalleryTask extends AsyncTask<Void, Void, List<ImageItem>>{
        @Override
        protected void onPreExecute() {
            ((BaseActivity)viewer).showProgressDialog(GeneralUtils.getStringFromResource(mContext, R.string.loading));
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<ImageItem> imageItems) {
            super.onPostExecute(imageItems);
            ((BaseActivity)viewer).hideProgressDialog();
            viewer.update(imageItems);
        }

        @Override
        protected List<ImageItem> doInBackground(Void... params) {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.SIZE};
            int projectThumbId = MediaStore.Images.Thumbnails.MICRO_KIND;
            String projectThumbData = MediaStore.Images.Thumbnails.DATA;
            Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
            int columnId = cursor.getColumnIndex(projection[0]);
            int columnData = cursor.getColumnIndex(projection[1]);
            int columnName = cursor.getColumnIndex(projection[2]);
            int columnSize = cursor.getColumnIndex(projection[3]);
            //int columnFolder = cursor.getColumnIndex(projection[3]);

            List<ImageItem> items = new ArrayList<ImageItem>();
            while(cursor.moveToNext()){
                ImageItem item = new ImageItem();
                item.setPath(cursor.getString(columnData));
                item.setName(cursor.getString(columnName));
                item.setSizeOfByte(cursor.getLong(columnSize));

                Cursor cursorThum = MediaStore.Images.Thumbnails.queryMiniThumbnail(mContext.getContentResolver(), cursor.getLong(columnId), projectThumbId, null);
                if(cursorThum != null && cursorThum.getCount() > 0){
                    cursorThum.moveToFirst();
                    item.setThumb(cursorThum.getString(cursorThum.getColumnIndex(projectThumbData)));
                } else {
                    item.setThumb(item.getPath());
                }
                items.add(item);
            }
            return items;
        }
    }

    public void downloadComplete(long id){

        if(id == -1) return;
        Uri path = SearchService.getInstance(mContext).process(id);
        boolean isDone = false;
        if(path != null && (new File(path.getPath())).exists()){
            if(mMode == SearchService.MODE_URL){
                try {
                    FileUtils.notifyMediaStore(mContext, path.getPath(), FileUtils.getFileNameExt(path.getPath()));
                    viewer.onDownloadComplete(path.getPath());

                }catch (Exception e){
                    viewer.showError(GeneralUtils.getStringFromResource(mContext, R.string.error), "INVALID IMAGE URL");
                }
                isDone = true;
            } else if(mMode == SearchService.MODE_GOOGLE){
                try {
                    ImageItemList list = JsonUtils.parseJson(FileUtils.readTxtFile(path));
                    mGoogleItems.addAll(list.getItems());
                    if(mGoogleItems.size() >= 20 || mGoogleItems.size() >= list.getTotal()){
                        viewer.update(mGoogleItems);
                        isDone = true;
                    } else {
                        mDownloadId = SearchService.getInstance(mContext).open(buildURL(mUrl, mGoogleItems.size()), SearchService.MODE_GOOGLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    viewer.showError(GeneralUtils.getStringFromResource(mContext, R.string.error), e.getMessage());
                    isDone = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    viewer.showError(GeneralUtils.getStringFromResource(mContext, R.string.error), e.getMessage());
                    isDone = true;
                }
            }
        } else {
            viewer.showError(GeneralUtils.getStringFromResource(mContext, R.string.result), GeneralUtils.getStringFromResource(mContext, R.string.empty));

            isDone = true;
        }

        if(isDone){
            ((BaseActivity)viewer).hideProgressDialog();
        }


    }

    public void downloadError(String data){
        ((BaseActivity)viewer).hideProgressDialog();
        viewer.showError(GeneralUtils.getStringFromResource(mContext, R.string.error), data);
        SearchService.getInstance(mContext).cancel(mDownloadId);
    }
    public void downloadPause(String data){
        ((BaseActivity)viewer).hideProgressDialog();
        viewer.showError(GeneralUtils.getStringFromResource(mContext, R.string.error), data);
        SearchService.getInstance(mContext).cancel(mDownloadId);
    }

    private String buildURL(String key, int index){
        String url = String.format(Constants.GOOGLE_API, key.replaceAll(" ", "%20"), index, 10);
        return url;
    }


    public interface ILViewer{
        public void update(List<ImageItem> items);
        public void onSelect(int position);
        public void onDownloadComplete(String path);
        public void showError(String title, String msg);
    }
}
