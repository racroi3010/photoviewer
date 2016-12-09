package ptv.cookpad.com.photoviewer.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ptv.cookpad.com.photoviewer.R;
import ptv.cookpad.com.photoviewer.presenter.ImageLoadingPresenter;
import ptv.cookpad.com.photoviewer.util.LocaleUtils;
import ptv.cookpad.com.photoviewer.util.PreferenceHandler;

/**
 * This is base activity
 * Created by VS on 12/7/2016.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    protected  Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        int position = PreferenceHandler.getLanguagePositionPreference(mContext);
        LocaleUtils.setLocale(mContext, position);
        setContentView(getViewSourceLayout());


        initeViews();
        if(savedInstanceState == null){
            loadDatas();
        } else {
            recoverDatas(savedInstanceState);
        }



    }

    /**
     * get layout resource id for activity
     * @return layout resource id
     */
    protected  abstract int getViewSourceLayout();

    /**
     * initialize view for activity
     */
    protected abstract void initeViews();

    /**
     * load data on view
     */
    protected abstract void loadDatas();

    /**
     * recover data if the device rotate
     * @param data stored data on onSaveState
     */
    protected abstract void recoverDatas(Bundle data);

    /**
     * show process dialog
     * @param msg message
     */
    public void showProgressDialog(String msg){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    /**
     * hide process dialog
     */
    public void hideProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.hide();
        }
    }
}
