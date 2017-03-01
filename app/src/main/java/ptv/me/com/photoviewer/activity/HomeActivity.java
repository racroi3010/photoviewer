package ptv.me.com.photoviewer.activity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ptv.me.com.photoviewer.R;
import ptv.me.com.photoviewer.adapter.GalleryImageAdapter;
import ptv.me.com.photoviewer.adapter.GridAutofitLayoutManager;
import ptv.me.com.photoviewer.ip.IPJni;
import ptv.me.com.photoviewer.model.ImageItem;
import ptv.me.com.photoviewer.presenter.ImageLoadingPresenter;
import ptv.me.com.photoviewer.service.SearchService;
import ptv.me.com.photoviewer.util.Constants;
import ptv.me.com.photoviewer.util.FileUtils;
import ptv.me.com.photoviewer.util.ValidationUtil;

/**
 * this activity is used for list all image from gallery, google search, open camera, open image view, open setting activity
 */
public class HomeActivity extends BaseActivity implements ImageLoadingPresenter.ILViewer {
    private static final String TAG = "HomeActivity";
    private RecyclerView recyclerView;
    private ImageLoadingPresenter mILPresenter;
    private List<ImageItem> mItems;
    private EditText mEditText;
    private LinearLayout mLayoutSearch;
    private GalleryImageAdapter mAdapter;
    private BroadcastReceiver mReceiver;


    private static int REQUEST_CAPTURE = 1;

    @Override
    protected int getViewSourceLayout() {
        return R.layout.activity_home;
    }

    protected void initeViews(){
        Toolbar mBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mBar);

        mILPresenter = new ImageLoadingPresenter(mContext, this);


        recyclerView = (RecyclerView) findViewById(R.id.list_item);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(mContext, 160);
        recyclerView.setLayoutManager(layoutManager);
        mItems = new ArrayList<ImageItem>();
        mAdapter = new GalleryImageAdapter(mContext, mItems, this);
        recyclerView.setAdapter(mAdapter);

        mLayoutSearch = (LinearLayout) findViewById(R.id.layout_search);
        mEditText = (EditText) findViewById(R.id.edt_search);


    }

    @Override
    protected void loadDatas() {

        mILPresenter.loadImage(ImageLoadingPresenter.IMAGE_SRC_GALLERY, null);
        //mEditText.setText("http://www.ece.rice.edu/~wakin/images/lena512.bmp");
    }

    @Override
    protected void recoverDatas(Bundle data) {
        List<ImageItem> items = data.getParcelableArrayList(Constants.BUNDLE_STATE);
        update(items);
    }


    @Override
    public void update(List<ImageItem> items) {
        hideProgressDialog();
        mItems.clear();
        mItems.addAll(items);
        mAdapter.notifyDataSetChanged();
        //Toast.makeText(mContext, items.size() + " size" + items.get(0).getPath(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onSelect(int position) {
        ImageItem item = mItems.get(position);
        if(ValidationUtil.validateURL(item.getPath())){
            mILPresenter.loadImage(ImageLoadingPresenter.IMAGE_SRC_SEARCH, item.getPath());
        } else {
            Intent intent = new Intent(mContext, ViewActivity.class);
            intent.putExtra(Constants.IMAGE_ITEM, mItems.get(position));
            startActivity(intent);
        }


    }

    @Override
    public void onDownloadComplete(String path) {
        //hideProgressDialog();
        ImageItem item = new ImageItem();
        item.setPath(path);
        item.setThumb(path);

        Intent intent = new Intent(mContext, ViewActivity.class);
        intent.putExtra(Constants.IMAGE_ITEM, item);
        startActivity(intent);
    }

    @Override
    public void showError(String title, String msg) {

        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_search:
                //showProgressDialog();
                String url = mEditText.getText().toString();
                mILPresenter.loadImage(ImageLoadingPresenter.IMAGE_SRC_SEARCH, url);
                break;
            case R.id.btn_close:
                if(mLayoutSearch != null){
                    mLayoutSearch.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_action_add:
                if(mLayoutSearch != null){
                    mLayoutSearch.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_camera:
                Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intentCapture.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intentCapture, REQUEST_CAPTURE);
                }
                break;
            case R.id.btn_action_setting:
                Intent intent = new Intent(mContext, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_action_gallery:
                loadDatas();
                break;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null){
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bmp = (Bitmap) extras.get("data");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String name = "IMG_" + simpleDateFormat.format(new Date()) + ".jpg";
            String path = Environment.getExternalStorageDirectory().getPath() + File.separator + name;
            FileUtils.saveImageToFile(bmp, path);
            FileUtils.notifyMediaStore(mContext, path, name);

            ImageItem item = new ImageItem();
            item.setThumb(path);
            item.setPath(path);
            Intent intent = new Intent(mContext, ViewActivity.class);
            intent.putExtra(Constants.IMAGE_ITEM, item);
            startActivity(intent);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.BUNDLE_STATE, (ArrayList<? extends Parcelable>) mItems);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    mILPresenter.downloadComplete(id);
                } else if(SearchService.ACTION_FAILED.equals(action)){
                    String msg = intent.getStringExtra(SearchService.DATA);
                    mILPresenter.downloadError(msg);
                } else if(SearchService.ACTION_PAUSED.equals(action)){
                    String msg = intent.getStringExtra(SearchService.DATA);
                    mILPresenter.downloadError(msg);
                }

            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(SearchService.ACTION_FAILED);
        filter.addAction(SearchService.ACTION_PAUSED);
        filter.addAction(SearchService.ACTION_COMPLETED);
        // add status

        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
//        if (!OpenCVLoader.initDebug()) {
//            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
//        } else {
//            Log.d(TAG, "OpenCV library found inside package. Using it!");
//            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//
//        }
    }

//    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS:
//                {
//                    Log.i(TAG, "OpenCV loaded successfully");
//                    // any immediate code for using OpenCV
//                } break;
//                default:
//                {
//                    super.onManagerConnected(status);
//                } break;
//            }
//        }
//    };

}
