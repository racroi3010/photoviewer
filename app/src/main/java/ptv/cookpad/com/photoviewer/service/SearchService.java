package ptv.cookpad.com.photoviewer.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import java.util.Timer;
import java.util.TimerTask;

import ptv.cookpad.com.photoviewer.R;
import ptv.cookpad.com.photoviewer.util.Constants;
import ptv.cookpad.com.photoviewer.util.FileUtils;
import ptv.cookpad.com.photoviewer.util.GeneralUtils;
import ptv.cookpad.com.photoviewer.util.ValidationUtil;

/**
 * this is download service
 * Created by VS on 12/7/2016.
 */

public class SearchService {
    private Context mContext;
    private DownloadManager mDownloadManager;
    public static final int MODE_GOOGLE = 0;
    public static final int MODE_URL = 1;

    public static final String ACTION_FAILED = "ACTION_FAILED";
    public static final String ACTION_PAUSED = "ACTION_PAUSED";
    public static final String ACTION_COMPLETED = "ACTION_COMPLETED";
    public static final String DATA = "DATA";

    private Timer mTimer;

    public static SearchService instance;
    public static SearchService getInstance(Context mContext){
        if(instance == null) {
            instance = new SearchService(mContext);

        }
        return instance;
    }
    private SearchService(Context mContext) {
        this.mContext = mContext;

        mDownloadManager = (DownloadManager) mContext.getSystemService(Service.DOWNLOAD_SERVICE);
    }

    /**
     * open a connection to download
     * @param key url
     * @param mode url or google search
     * @return id of download process
     */
    public long open(String key, int mode){
        if(key == null || key.isEmpty()) return -1;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(key));
        request.setTitle(GeneralUtils.getStringFromResource(mContext, R.string.download));

        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_PICTURES, FileUtils.getFileNameExt(key));
        if(mode == MODE_URL) {
            request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_PICTURES, FileUtils.getFileNameExt(key));
        }
        else {
            request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, Constants.FILE_TEMP);
        }
        final long downloadId = mDownloadManager.enqueue(request);

        // open a timer to check status
        this.mTimer = new Timer();
        this.mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkProcess(downloadId);
            }
        }, 0, 3000);
        return downloadId;
    }

    /**
     * find uri of downloaded file
     * @param id id of download process
     * @return uri of downloaded file
     */
    public Uri process(long id){
        Uri uri = mDownloadManager.getUriForDownloadedFile(id);
        return uri;
    }

    /**
     * cancel download process
     * @param id id of download process
     */
    public void cancel(long id){
        if(this.mTimer != null) this.mTimer.cancel();
        mDownloadManager.remove(id);
    }

    /**
     * check status of download process
     * @param id id of download process
     */
    private void checkProcess(long id){
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = mDownloadManager.query(query);
        if(cursor != null && cursor.moveToFirst()){
            int columnStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);

            int status = cursor.getInt(columnStatus);
            int reason = cursor.getInt(columnReason);

            String reasonText = null;
            switch (status){
                case DownloadManager.STATUS_FAILED:
                    switch(reason){
                        case DownloadManager.ERROR_CANNOT_RESUME:
                            reasonText = "ERROR_CANNOT_RESUME";
                            break;
                        case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                            reasonText = "ERROR_DEVICE_NOT_FOUND";
                            break;
                        case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                            reasonText = "ERROR_FILE_ALREADY_EXISTS";
                            break;
                        case DownloadManager.ERROR_FILE_ERROR:
                            reasonText = "ERROR_FILE_ERROR";
                            break;
                        case DownloadManager.ERROR_HTTP_DATA_ERROR:
                            reasonText = "ERROR_HTTP_DATA_ERROR";
                            break;
                        case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                            reasonText = "ERROR_INSUFFICIENT_SPACE";
                            break;
                        case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                            reasonText = "ERROR_TOO_MANY_REDIRECTS";
                            break;
                        case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                            reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                            break;
                        case DownloadManager.ERROR_UNKNOWN:
                            reasonText = "ERROR_UNKNOWN";
                            break;
                    }
                    Intent intent = new Intent();
                    intent.setAction(ACTION_FAILED);
                    intent.putExtra(DATA, reasonText);
                    mContext.sendBroadcast(intent);

                    if(this.mTimer != null) this.mTimer.cancel();
                    break;
                case DownloadManager.STATUS_PAUSED:
                    switch(reason){
                        case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                            reasonText = "PAUSED_QUEUED_FOR_WIFI";
                            break;
                        case DownloadManager.PAUSED_UNKNOWN:
                            reasonText = "PAUSED_UNKNOWN";
                            break;
                        case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                            reasonText = "PAUSED_WAITING_FOR_NETWORK";
                            break;
                        case DownloadManager.PAUSED_WAITING_TO_RETRY:
                            reasonText = "PAUSED_WAITING_TO_RETRY";
                            break;
                    }
                    intent = new Intent();
                    intent.setAction(ACTION_PAUSED);
                    intent.putExtra(DATA, reasonText);
                    mContext.sendBroadcast(intent);

                    if(this.mTimer != null) this.mTimer.cancel();
                    break;
                case DownloadManager.STATUS_PENDING:

                    break;
                case DownloadManager.STATUS_RUNNING:

                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    intent = new Intent();
                    intent.setAction(ACTION_COMPLETED);
                    intent.putExtra(DATA, reason);
                    mContext.sendBroadcast(intent);

                    if(this.mTimer != null) this.mTimer.cancel();
                    break;
            }

        } else {
            if(this.mTimer != null) this.mTimer.cancel();
        }
    }

}
