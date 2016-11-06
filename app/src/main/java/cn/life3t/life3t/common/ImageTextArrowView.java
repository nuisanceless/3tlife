package cn.life3t.life3t.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;

/**
 * Created by Lei on 2015/5/20.
 */
@EViewGroup(R.layout.view_image_text_arrow)
public class ImageTextArrowView extends RelativeLayout {
    @ViewById(R.id.image)
    ImageView _image;

    @ViewById(R.id.content)
    TextView _content;

    int _attrImage;
    String _attrContentText;
    String _attrContentHint;

    public ImageTextArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(attrs);
    }

    private void initialise( AttributeSet attrs ) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ImageTextArrowView);

        _attrImage = a.getResourceId(R.styleable.ImageTextArrowView_image, 0);
        _attrContentText = a.getString(R.styleable.ImageTextArrowView_content_text);
        _attrContentHint = a.getString(R.styleable.ImageTextArrowView_content_hint);

        a.recycle();
    }

    @AfterViews
    void afterViews() {
        setImage(_attrImage);
        setContentText(_attrContentText);
        setContentHint(_attrContentHint);
    }

    public void setImage(int resId) {
        if (resId != 0)
            _image.setImageResource(resId);
    }

    public void setContentText(String text) {
        if (text != null)
            _content.setText(text);
    }

    public void setContentHint(String text) {
        if (text != null )
            _content.setHint(text);
    }
}
