package cn.life3t.life3t.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;

/**
 * Created by wuguohao on 15-5-29.
 */
@EViewGroup(R.layout.view_info_item)
public class InfoItemView extends LinearLayout
{

    @ViewById(R.id.info_item_img)
    ImageView mImage;

    @ViewById(R.id.info_item_type_text)
    TextView mTypeText;

    @ViewById(R.id.info_item_content)
    TextView mContent;

    int _attrImage;
    String _attrTypeText;
    String _attrContent;
    int _attrContentColor;
    int _attrContentGravity;

    
    public InfoItemView (Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialise(attrs);
    }

    private void initialise( AttributeSet attrs )
    {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.InfoItemView);

        _attrImage = a.getResourceId(R.styleable.InfoItemView_image, 0);
        _attrTypeText = a.getString(R.styleable.InfoItemView_type_text);
        _attrContent = a.getString(R.styleable.InfoItemView_content_text);
        _attrContentColor = a.getColor(R.styleable.InfoItemView_content_color, 0xFFFFFFFF);
        _attrContentGravity = a.getInt(R.styleable.InfoItemView_content_gravity, 0);

        a.recycle();
    }

    @AfterViews
    void afterViews ()
    {
        setImage(_attrImage);
        setTypeText(_attrTypeText);
        setContent(_attrContent);
        setContentColor(_attrContentColor);
        setContentGravity(_attrContentGravity);
    }

    public void setImage(int resId) {
        if (resId != 0)
            mImage.setImageResource(resId);
    }

    public void setTypeText(String text) {
        if (text != null && !text.isEmpty())
            mTypeText.setText(text);
    }

    public void setContent(String text) {
        if (text != null && !text.isEmpty())
            mContent.setText(text);
    }

    public void setContentColor(int color)
    {
        mContent.setTextColor(color);
    }

    public void setContentGravity (int gravity)
    {
        switch (gravity)
        {
            case 0:
                mContent.setGravity(Gravity.RIGHT);
                break;
            case 1:
                mContent.setGravity(Gravity.LEFT);
                break;
            case 2:
                mContent.setGravity(Gravity.CENTER);
                break;
            default:
                break;
        }
    }
}
