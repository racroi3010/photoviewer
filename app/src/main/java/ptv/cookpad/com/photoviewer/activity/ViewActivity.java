package ptv.cookpad.com.photoviewer.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import ptv.cookpad.com.photoviewer.R;
import ptv.cookpad.com.photoviewer.model.ImageItem;
import ptv.cookpad.com.photoviewer.presenter.ImageViewPresenter;
import ptv.cookpad.com.photoviewer.util.Constants;
import ptv.cookpad.com.photoviewer.util.GeneralUtils;
import ptv.cookpad.com.photoviewer.util.ValidationUtil;

/**
 * this activity is used for display image, sharing image or removing image
 */
public class ViewActivity extends BaseActivity implements ImageViewPresenter.IVViewer{
    private ImageView imgView;
    private ImageViewPresenter mPresenter;
    //private String mPath;
    private ImageItem mItem;

    @Override
    protected int getViewSourceLayout() {
        return R.layout.activity_view;
    }

    @Override
    protected void initeViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imgView = (ImageView) findViewById(R.id.img_view);
        mPresenter = new ImageViewPresenter(mContext, this);
    }

    @Override
    protected void loadDatas() {
        if(imgView == null) return;
        Intent intent = getIntent();
        if(intent != null){
            mItem = intent.getParcelableExtra(Constants.IMAGE_ITEM);
            if(mItem != null){
                //Toast.makeText(mContext, mItem.getPath(), Toast.LENGTH_LONG).show();
                Picasso.with(mContext).load(new File(mItem.getPath())).into(imgView);
            }


        }
    }

    @Override
    protected void recoverDatas(Bundle data) {
        if(imgView == null) return;
        if(data != null){
            mItem = data.getParcelable(Constants.BUNDLE_STATE);
            if(mItem != null){
                Toast.makeText(mContext, mItem.getPath(), Toast.LENGTH_LONG).show();
                Picasso.with(mContext).load(new File(mItem.getPath())).into(imgView);
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BUNDLE_STATE, mItem);
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_action_edit:
                Intent intent = new Intent(mContext, EditActivity.class);
                intent.putExtra(Constants.IMAGE_ITEM, mItem);
                startActivity(intent);
                break;
            case R.id.btn_action_home:
                intent = new Intent(mContext, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btn_action_delete:
                if(mItem != null) mPresenter.deleteImage(mItem.getPath());
                break;
            case R.id.btn_action_share:
                if(mItem != null){
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse(mItem.getPath()));
                    if(share.resolveActivity(getPackageManager()) != null){
                        startActivity(Intent.createChooser(share, GeneralUtils.getStringFromResource(mContext, R.string.title_share)));
                    }
                }
                break;
        }
    }

    @Override
    public void onRemoveOK() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void showError(String msg) {
        new AlertDialog.Builder(mContext)
                .setTitle("Error")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
