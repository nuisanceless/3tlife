package cn.life3t.life3t.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;

import cn.life3t.life3t.ActivityHelper_;
import cn.life3t.life3t.MyApplication;
import cn.life3t.life3t.MyApplication_;
import cn.life3t.life3t.rest.MyRestClient;

/**
 * Created by Lei on 2015/7/2.
 */
public abstract class BaseActivity extends Activity {

    protected MyApplication mApp;
    protected ActivityHelper_ _activityHelper;
    protected MyRestClient _restClient;
    protected LayoutInflater _layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = MyApplication_.getInstance();
        _activityHelper = ActivityHelper_.getInstance_(this);
        _restClient = mApp.restClient();
        _layoutInflater = this.getLayoutInflater();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mApp.onActivityStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mApp.onActivityStop();
    }
}
