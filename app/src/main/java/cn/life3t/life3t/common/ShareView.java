package cn.life3t.life3t.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;

import cn.life3t.life3t.R;

/**
 * Created by wuguohao on 15-6-3.
 */
@EViewGroup(R.layout.view_share)
public class ShareView extends LinearLayout
{
    public ShareView (Context context)
    {
        super(context);
    }

    public ShareView (Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialise(attrs);
    }

    private void initialise (AttributeSet attrs)
    {

    }

    @AfterViews
    void afterViews ()
    {

    }
}
