package ptv.me.com.photoviewer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import ptv.me.com.photoviewer.R;
import ptv.me.com.photoviewer.util.LocaleUtils;
import ptv.me.com.photoviewer.util.PreferenceHandler;

/**
 * this activity is used for setting
 */
public class SettingActivity extends BaseActivity {
    private boolean userIsInteracting;
    @Override
    protected int getViewSourceLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initeViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spLanguage = (Spinner) findViewById(R.id.sp_language);
        spLanguage.setSelection(PreferenceHandler.getLanguagePositionPreference(mContext));
        spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(userIsInteracting){
                    LocaleUtils.setLocale(mContext, position);

                    Intent intent = new Intent(mContext, SettingActivity.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP|intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }
    @Override
    protected void loadDatas() {

    }

    @Override
    protected void recoverDatas(Bundle data) {

    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_action_home:
                Intent  intent = new Intent(mContext, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent  intent = new Intent(mContext, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
