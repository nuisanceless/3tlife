package cn.life3t.life3t.rest;

import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

/**
 * Created by RexQian on 2014/12/9.
 */
@EBean
public class MyErrorHandler implements RestErrorHandler {

    final static String TAG = MyErrorHandler.class.getSimpleName();
    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e)
    {
        Log.d(TAG, e.toString());
        e.printStackTrace();
    }
}
