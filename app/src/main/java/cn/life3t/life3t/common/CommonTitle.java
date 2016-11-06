package cn.life3t.life3t.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;

/**
 * Created by wuguohao on 15-5-12.
 */
@EViewGroup(R.layout.common_title)
public class CommonTitle extends RelativeLayout {

    @ViewById(R.id.title)
    TextView mTitleTextView;

    @ViewById(R.id.title_img)
    ImageView mTitleImgView;

    @ViewById(R.id.btn_back)
    ImageView mBackButton;

    @ViewById(R.id.btn_location_tv)
    TextView mLocationTextBtn;

    @ViewById(R.id.btn_custom)
    ImageView mCustomButton;

    @ViewById(R.id.btn_custom_tv)
    TextView mCustomTextButton;

    @ViewById(R.id.common_title_layout)
    View mCommonTitleLayout;

    public CommonTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(attrs);
    }

    String mAttrTitle;
    int mAttrBackBg;
    int mAttrCustomBg;
    int mAttrTitleBg;   // title text or title img
    String mAttrCustomText;
    String mAttrLocationText;
    int mBgColor;
    int mBgImg;

    private void initialise( AttributeSet attrs )
    {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CommonTitle);
        mAttrTitleBg = a.getResourceId(R.styleable.CommonTitle_title_img, 0);
        mAttrTitle = a.getString(R.styleable.CommonTitle_title_text);

        mAttrBackBg = a.getResourceId(R.styleable.CommonTitle_enable_back, 0);
        mAttrLocationText = a.getString(R.styleable.CommonTitle_location_btn_text);

        mAttrCustomBg = a.getResourceId(R.styleable.CommonTitle_custom_btn_bg, 0);
        mAttrCustomText = a.getString(R.styleable.CommonTitle_custom_btn_text);
        mBgColor = a.getColor(R.styleable.CommonTitle_bg_color, 0);
        mBgImg = a.getResourceId(R.styleable.CommonTitle_bg_img, 0);
        a.recycle();
    }

    @AfterViews
    void afterViews()
    {

        //中间
        if (mAttrTitleBg == 0)
        {
            mTitleImgView.setVisibility(INVISIBLE);
            if (mAttrTitle != null && !mAttrTitle.isEmpty())
            {
                mTitleTextView.setVisibility(VISIBLE);
                mTitleTextView.setText(mAttrTitle);
            }
        }
        else
        {
            mTitleImgView.setImageResource(mAttrTitleBg);
            mTitleImgView.setVisibility(VISIBLE);
        }

        //左边
        if (mAttrBackBg == 0)
        {
            mBackButton.setVisibility(INVISIBLE);
            if (mAttrLocationText != null && !mAttrLocationText.isEmpty())
            {
                mLocationTextBtn.setVisibility(VISIBLE);
                mLocationTextBtn.setText(mAttrLocationText);
            }
        }
        else
        {
            mBackButton.setImageResource(mAttrBackBg);
            mBackButton.setVisibility(VISIBLE);
            mBackButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = getContext();
                    if(context instanceof Activity)
                    {
                        ((Activity) context).finish();
                    }
                }
            });
        }

        //右边
        if(mAttrCustomBg == 0)
        {
            mCustomButton.setVisibility(INVISIBLE);
            if(mAttrCustomText != null && !mAttrCustomText.isEmpty())
            {
                mCustomTextButton.setVisibility(VISIBLE);
                mCustomTextButton.setText(mAttrCustomText);
            }
        }
        else
        {
            mCustomButton.setImageResource(mAttrCustomBg);
            mCustomButton.setVisibility(VISIBLE);
        }

        //背景色
        if (mBgColor != 0)
        {
            mCommonTitleLayout.setBackgroundColor(mBgColor);
        }
    }

    public void setCustomOnClickListener(OnClickListener l)
    {
        mCustomButton.setOnClickListener(l);
        mCustomTextButton.setOnClickListener(l);
    }

    public void setTitle(String text)
    {
        mTitleTextView.setText(text);
    }

    public void setCustomButtonEnable(boolean enable)
    {
        mCustomButton.setEnabled(enable);
        mCustomTextButton.setEnabled(enable);
    }

    public void showCustomButton(boolean show)
    {
        if(mAttrCustomBg != 0)
        {
            mCustomButton.setVisibility(show? VISIBLE : INVISIBLE);
        }

        if(mAttrCustomText != null && !mAttrCustomText.isEmpty())
        {
            mCustomTextButton.setVisibility(show? VISIBLE : INVISIBLE);
        }
    }
}
