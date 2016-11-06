package cn.life3t.life3t.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;

/**
 * Created by Lei on 2015/5/27.
 */
@EViewGroup(R.layout.view_numeric_text)
public class NumericTextView extends RelativeLayout {
    public static interface NumberChangedListener {
        public void onNumberChanged(int value);
    }
    int _val = 0;
    int _min = Integer.MIN_VALUE;
    int _max = Integer.MAX_VALUE;
    NumberChangedListener _listener;

    @ViewById(R.id.add)
    View _addView;

    @ViewById(R.id.minus)
    View _minusView;

    @ViewById(R.id.number)
    TextView _numberView;

    @Click(R.id.add)
    void onAddButtonClick() {
        setValue(_val + 1);

        if (_listener != null) {
            _listener.onNumberChanged(getValue());
        }
    }

    @Click(R.id.minus)
    void onMinusButtonClick() {
        setValue(_val - 1);

        if (_listener != null) {
            _listener.onNumberChanged(getValue());
        }
    }

    public NumericTextView(Context context) {
        super(context);
    }

    public NumericTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumericTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(NumberChangedListener listener) {
        _listener = listener;
    }

    public void setValue(int val) {
        if (val < _min || val > _max)
            return;

        _val = val;
        updateView();
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
