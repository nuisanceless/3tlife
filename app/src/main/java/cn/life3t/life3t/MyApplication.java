package cn.life3t.life3t;

/**
 * Created by Lei on 2015/5/12.
 */

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import cn.life3t.life3t.account.LoginActivity_;
import cn.life3t.life3t.main.WelcomeActivity_;
import cn.life3t.life3t.rest.MyErrorHandler;
import cn.life3t.life3t.rest.MyInterceptor;
import cn.life3t.life3t.rest.MyResponseErrorHandler;
import cn.life3t.life3t.rest.MyRestClient;
import cn.life3t.life3t.rest.MyRestTemplate;

/**
 * Created by RexQian on 2014/10/14.
 */
@EApplication
public class MyApplication extends Application implements MyResponseErrorHandler.ErrorListener {

    public String getVersion()
    {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getVersionCode()
    {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public MyRestClient restClient()
    {
        return mRestClient;
    }

    @Pref
    MyPrefs_ mPrefs;

    @RestService
    MyRestClient mRestClient;

    @Bean
    MyErrorHandler mErrorHandler;

    @Bean
    MyInterceptor mInterceptor;

    @Bean
    MyRestTemplate mMyRestTemplate;

    MyResponseErrorHandler mErrHandler = new MyResponseErrorHandler();


    @AfterInject
    void afterInject()
    {
        mMyRestTemplate.setMessageConverters(mRestClient.getRestTemplate().getMessageConverters());
        mRestClient.setRestTemplate(mMyRestTemplate);
        mRestClient.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        mRestClient.setRestErrorHandler(mErrorHandler);
        mRestClient.getRestTemplate().setErrorHandler(mErrHandler);
        mInterceptor.setRestClient(mRestClient);
        mRestClient.getRestTemplate().getInterceptors().add(mInterceptor);
        mErrHandler.setErrorListener(this);
        Fresco.initialize(getBaseContext());
    }

    @Override
    public void onError(HttpStatus status) {
        if(status == HttpStatus.UNAUTHORIZED) {
            LoginActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .start();
        }
    }

    int mActiveActivityCount = 0;
    long mEnterBackgroundTime = 0;
    long mShowBackScreenTime = 5*60*1000; //5 min

    public void onActivityStart()
    {
        mActiveActivityCount++;
        if(mActiveActivityCount == 1)
        {
            //enter foreground
            if(System.currentTimeMillis() - mEnterBackgroundTime > mShowBackScreenTime)
            {
                WelcomeActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .start();
            }
        }
    }

    public void onActivityStop()
    {
        mActiveActivityCount--;
        if(mActiveActivityCount == 0)
        {
            //enter background
            mEnterBackgroundTime = System.currentTimeMillis();
        }
    }
}
