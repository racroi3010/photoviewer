package ptv.me.com.photoviewer.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.io.File;

import ptv.me.com.photoviewer.R;
import ptv.me.com.photoviewer.model.ImageItem;
import ptv.me.com.photoviewer.presenter.ImageEditPresenter;
import ptv.me.com.photoviewer.util.Constants;
import ptv.me.com.photoviewer.util.GeneralUtils;

/**
 * this activity is used for editing the image such as left rotation, right rotation and saving
 */
public class EditActivity extends BaseActivity implements ImageEditPresenter.IEViewer{
    /**
     * image view
     */
    private ImageView mImgView;

    /**
     * image item
     */
    private ImageItem mItem;

    /**
     * presenter object
     */
    private ImageEditPresenter mPresenter;

    @Override
    protected int getViewSourceLayout() {
        return R.layout.activity_edit;
    }

    @Override
    protected void initeViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImgView = (ImageView) findViewById(R.id.img_view);
        mPresenter = new ImageEditPresenter(mContext, this);
    }

    @Override
    protected void loadDatas() {
        if(mImgView == null) return;
        Intent intent = getIntent();
        if(intent != null){
            mItem = intent.getParcelableExtra(Constants.IMAGE_ITEM);
            if(mItem != null){
                //Toast.makeText(mContext, mItem.getPath(), Toast.LENGTH_LONG).show();
                Picasso.with(mContext).load(new File(mItem.getPath())).into(mImgView);
                mPresenter.setImage(mItem.getPath());
            }
        }
    }

    @Override
    protected void recoverDatas(Bundle data) {
        if(mImgView == null) return;
        if(data != null){
            mItem = data.getParcelable(Constants.BUNDLE_STATE);
            if(mItem != null){
                //Toast.makeText(mContext, mItem.getPath(), Toast.LENGTH_LONG).show();
                Picasso.with(mContext).load(new File(mItem.getPath())).into(mImgView);
                mPresenter.setImage(mItem.getPath());
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
            case R.id.btn_action_home:
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btn_action_undo:
                if(mItem != null){
                    Picasso.with(mContext).load(new File(mItem.getPath())).into(mImgView);
                    mPresenter.setImage(mItem.getPath());
                }
                break;
            case R.id.btn_action_save:
                save();
                break;
            case R.id.btn_rotate_left:
                //showProgressDialog();
                mPresenter.rotateImage(-90);
                break;
            case R.id.btn_rotate_right:
                //showProgressDialog();
                mPresenter.rotateImage(90);
                break;
            case R.id.btn_hist_equalize:
                mPresenter.histEqualization();
                break;
            case R.id.btn_face_detect:
                mPresenter.faceDetection();
                break;
            case R.id.btn_hist_denoising:
                mPresenter.denosing();
                break;
        }
    }

    /**
     * open saving dialog
     */
    private void save(){
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle(GeneralUtils.getStringFromResource(mContext, R.string.title_save));
        alertDialog.setMessage(GeneralUtils.getStringFromResource(mContext, R.string.title_save_msg));
        final EditText input = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, GeneralUtils.getStringFromResource(mContext, R.string.btn_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();
                        mPresenter.saveImage(name);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, GeneralUtils.getStringFromResource(mContext, R.string.btn_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
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

    @Override
    public void update(Bitmap bmp) {
        //hideProgressDialog();
        if(bmp != null) mImgView.setImageBitmap(bmp);
    }
}
