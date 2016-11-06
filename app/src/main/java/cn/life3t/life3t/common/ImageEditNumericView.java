package cn.life3t.life3t.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;

/**
 * Created by Lei on 2015/5/20.
 */
@EViewGroup(R.layout.view_image_edit_numeric)
public class ImageEditNumericView extends RelativeLayout {
    @ViewById(R.id.image)
    ImageView _image;

    @ViewById(R.id.content)
    TextView _content;

    NumericTextView.NumberChangedListener _listener;

    int _attrImage;
    String _attrContentText;
    String _attrContentHint;

    int _val = 0;
    int _min = Integer.MIN_VALUE;
    int _max = Integer.MAX_VALUE;

    @ViewById(R.id.add)
    View _addView;

    @ViewById(R.id.minus)
    View _minusView;

    @ViewById(R.id.number)
    EditText _numberView;

    @Click(R.id.add)
    void onAddButtonClick() {
        setValue(Integer.parseInt(_numberView.getText().toString()) + 1);

        if (_listener != null) {
            _listener.onNumberChanged(getValue());
        }
    }

    @Click(R.id.minus)
    void onMinusButtonClick() {
        setValue(Integer.parseInt(_numberView.getText().toString()) - 1);

        if (_listener != null) {
            _listener.onNumberChanged(getValue());
        }
    }

    public ImageEditNumericView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(attrs);
    }

    private void initialise( AttributeSet attrs ) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ImageEditNumericView);

        _attrImage = a.getResourceId(R.styleable.ImageEditNumericView_image, 0);
        _attrContentText = a.getString(R.styleable.ImageEditNumericView_content_text);
        _attrContentHint = a.getString(R.styleable.ImageEditNumericView_content_hint);

        a.recycle();
    }

    public void setListener(NumericTextView.NumberChangedListener listener) {
        _listener = listener;
    }
    @AfterViews
    void afterViews() {
        setImage(_attrImage);
        setContentText(_attrContentText);
        setContentHint(_attrContentHint);

        _numberView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String text = _numberView.getText().toString();
                    if (text.length() == 0) {
                        setValue(_min);
                    }
                    else {
                        int val = Integer.parseInt(text);
                        if (val < _min) {
                            setValue(_min);
                        }
                        else {
                            _val = val;
                        }
                    }
                }
            }
        });
    }

    public void setImage(int resId) {
        if (resId != 0)
            _image.setImageResource(resId);
    }

    public void setContentText(String text) {
        if (text != null && !text.isEmpty())
            _content.setText(text);
    }

    public void setContentHint(String text) {
        if (text != null && !text.isEmpty())
            _content.setHint(text);
    }

    public void setValue(int val) {
        if (val < _min || val > _max)
            return;

        _val = val;
        updateView();

        if (val != val) {

            if (_listener != null) {
                _listener.onNumberChanged(_val);
            }
        }
    }

    void updateView() {
        _numberView.setText(Integer.toString(_val));
        _addView.setEnabled(_val >= _max ? false : true);
        _minusView.setEnabled(_val <= _min ? false : true);
    }

    public int getValue() {
        return _val;
    }

    public void setMinValue(int min) {
        _min = min;
    }

    public int getMinValue() {
        return _min;
    }

    public void setMaxValue(int max) {
        _max = max;
    }

    public int getMaxValue() {
        return _max;
    }
}
