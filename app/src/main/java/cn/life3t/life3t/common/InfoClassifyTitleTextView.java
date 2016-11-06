package cn.life3t.life3t.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;

/**
 * Created by wuguohao on 15-5-29.
 */
@EViewGroup(R.layout.view_info_classify_title)
public class InfoClassifyTitleTextView extends RelativeLayout
{
    String infoClassifyTitleText;
    int textColor;

    @ViewById(R.id.title)
    TextView mTitle;

    public InfoClassifyTitleTextView (Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialise(attrs);
    }

    public void initialise (AttributeSet attrs)
    {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.InfoClassifyTitleTextView);

        infoClassifyTitleText = array.getString(R.styleable.InfoClassifyTitleTextView_text);
        textColor = array.getColor(R.styleable.InfoClassifyTitleTextView_textColor, 0xFFFFFFFF);

        array.recycle();
    }

    @AfterViews
    void afterViews ()
    {
        setInfoClassifyTitleText(infoClassifyTitleText);
        setInfoClassifyTitleColor(textColor);
    }

    public void setInfoClassifyTitleText (String text)
    {
        if (text != null && !text.isEmpty())
        {
            mTitle.setText(text);
        }
    }

    public void setInfoClassifyTitleColor (int color)
    {
        mTitle.setTextColor(color);
    }
}
