package cn.life3t.life3t.common;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.life3t.life3t.R;
import cn.life3t.life3t.message.StaffInfo;

/**
 * Created by Lei on 2015/6/23.
 */
@EViewGroup(R.layout.staff_select_view)
public class StaffSelectView extends LinearLayout {
    public static interface onClickListener {
        public void onClick();
    }
    @ViewById(R.id.container)
    LinearLayout _container;

    @ViewById(R.id.choose)
    View _chooseView;

    @ViewById(R.id.choose_hint)
    View _chooseHintView;

    @Click(R.id.choose)
    void onSelectStaff() {
        if (_listener != null) {
            _listener.onClick();
        }
    }

    onClickListener _listener;
    Context _context;
    private int _targetCount = 0;
    LayoutInflater _layoutInflater;
    List<StaffInfo> _dataList = new ArrayList<>();
    Map<View, StaffInfo> _map = new HashMap<>();

    public StaffSelectView(Context context) {
        super(context);
        _context = context;
    }

    public StaffSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
    }

    public StaffSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _context = context;
    }

    @AfterViews
    void afterViews() {
        _layoutInflater = LayoutInflater.from(_context);
    }

    public void setOnClickListener(onClickListener listener) {
        _listener = listener;
    }

    public void setTargetCount(int count) {
        _targetCount = count;
    }

    public void addStaff(StaffInfo info) {
        if (isFull()) {
            return;
        }

        final View item = _layoutInflater.inflate(R.layout.view_staff_select, null);
        SimpleDraweeView head = (SimpleDraweeView)item.findViewById(R.id.head);
        if (info.headUrl != null) {
            Uri uri = Uri.parse(info.headUrl);
            head.setImageURI(uri);
        }
        TextView name = (TextView)item.findViewById(R.id.name);
        name.setText(info.name);
        TextView distance = (TextView)item.findViewById(R.id.distance);
        double fDistance = info.distance / 1000.0;
        distance.setText(String.format(getResources().getString(R.string.distance_placeholder), fDistance));
        View remove = item.findViewById(R.id.remove);
        remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StaffInfo staffInfo = _map.remove(item);
                if (staffInfo != null) {
                    _container.removeView(item);
                    _dataList.remove(staffInfo);
                    updateViews();
                }
            }
        });
        _container.addView(item);

        _dataList.add(info);
        _map.put(item, info);
        updateViews();
    }

    public void clear() {
        _dataList.clear();
        _map.clear();
        _container.removeAllViews();
        updateViews();
    }

    public List<StaffInfo> getStaffList() {
        return _dataList;
    }

    private boolean isFull() {
        return _dataList.size() >= _targetCount;
    }

    public void updateViews() {
        int count = _dataList.size();
        if (count > 0) {
            _chooseHintView.setVisibility(View.VISIBLE);
        }
        else {
            _chooseHintView.setVisibility(View.GONE);
        }

        if (count >= _targetCount) {
            _chooseView.setVisibility(View.GONE);
        }
        else {
            _chooseView.setVisibility(View.VISIBLE);
        }
    }
}
