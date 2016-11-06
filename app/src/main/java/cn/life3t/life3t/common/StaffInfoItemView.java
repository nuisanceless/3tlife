package cn.life3t.life3t.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;

/**
 * Created by wuguohao on 15-6-1.
 */
@EViewGroup(R.layout.view_staff_info)
public class StaffInfoItemView extends RelativeLayout
{
    @ViewById(R.id.arrow)
    ImageView mArrowImg;

    int _attrArrowImg;

    public StaffInfoItemView (Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialise(attrs);
    }

    private void initialise (AttributeSet attrs)
    {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StaffInfoItemView);

        _attrArrowImg = a.getResourceId(R.styleable.StaffInfoItemView_arrow, 0);

        a.recycle();
    }

    @AfterViews
    void afterViews ()
    {
        setArrowImg(_attrArrowImg);
    }

    public void setArrowImg (int resId)
    {
        if (resId != 0)
        {
            mArrowImg.setImageResource(resId);
        }
    }
}
