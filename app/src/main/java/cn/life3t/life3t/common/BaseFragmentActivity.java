package cn.life3t.life3t.common;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import cn.life3t.life3t.MyApplication;
import cn.life3t.life3t.MyApplication_;

/**
 * Created by RexQian on 2/13/2015.
 */
public abstract class BaseFragmentActivity extends FragmentActivity {

    MyApplication mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = MyApplication_.getInstance();
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
