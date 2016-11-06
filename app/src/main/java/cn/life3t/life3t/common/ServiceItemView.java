package cn.life3t.life3t.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;

/**
 * Created by Lei on 2015/5/18.
 */
@EViewGroup(R.layout.service_item_view)
public class ServiceItemView extends RelativeLayout {
    @ViewById(R.id.service_item_image)
    ImageView _image;

    @ViewById(R.id.service_item_title)
    TextView _title;

    @ViewById(R.id.service_item_price)
    TextView _price;

    @ViewById(R.id.service_item_price_suffix)
    TextView _priceSuffix;

    @ViewById(R.id.service_item_description)
    TextView _description;

    int _attrImage;
    String _attrTitle;
    String _attrPrice;
    String _attrPriceSuffix;
    String _attrDescription;

    public ServiceItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(attrs);
    }

    private void initialise( AttributeSet attrs )
    {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ServiceItemView);

        _attrImage = a.getResourceId(R.styleable.ServiceItemView_image, 0);
        _attrTitle = a.getString(R.styleable.ServiceItemView_title_text);
        _attrPrice = a.getString(R.styleable.ServiceItemView_price);
        _attrPriceSuffix = a.getString(R.styleable.ServiceItemView_price_suffix);
        _attrDescription = a.getString(R.styleable.ServiceItemView_description);

        a.recycle();
    }

    @AfterViews
    void afterViews() {
        setImage(_attrImage);
        setTitle(_attrTitle);
        setPrice(_attrPrice);
        setPriceSuffix(_attrPriceSuffix);
        setDescription(_attrDescription);
    }

    public void setImage(int resId) {
        if (resId != 0)
            _image.setImageResource(resId);
    }

    public void setImage(Bitmap bm) {
        if (bm != null)
            _image.setImageBitmap(bm);
    }

    public void setTitle(int resId) {
        if (resId != 0)
            _title.setText(resId);
    }

    public void setTitle(String text) {
        if (text != null && !text.isEmpty())
            _title.setText(text);
    }

    public void setPrice(int resId) {
        if (resId != 0)
            _price.setText(resId);
    }

    public void setPrice(String text) {
        if (text != null && !text.isEmpty())
            _price.setText(text);
    }
    public void setPriceSuffix(int resId) {
        if (resId != 0)
            _priceSuffix.setText(resId);
    }

    public void setPriceSuffix(String text) {
        if (text != null && !text.isEmpty())
            _priceSuffix.setText(text);
    }
    public void setDescription(int resId) {
        if (resId != 0)
            _description.setText(resId);
    }

    public void setDescription(String text) {
        if (text != null && !text.isEmpty())
            _description.setText(text);
    }
}
