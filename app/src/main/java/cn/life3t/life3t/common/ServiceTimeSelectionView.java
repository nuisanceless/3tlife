package cn.life3t.life3t.common;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.message.ServiceTimeItem;
import cn.life3t.life3t.message.ServiceTimeSelectionItem;

/**
 * Created by Lei on 2015/5/28.
 */
@EViewGroup(R.layout.view_service_time_selection)
public class ServiceTimeSelectionView extends LinearLayout {
    public static interface ServiceTimeSelectionListener {
        public void onDurationChanged(int duration);
        public void onCountChanged(int count);
        public void onDateChanged(String date);
        public void onInit(int duration, int count);
        public void onFinish(int duration, int count, String date, String time);
    }
    Context _context;
    ServiceTimeSelectionListener _listener;
    ServiceTimePicker _timePicker;

    @ViewById(R.id.list)
    HorizontalListView _listView;

    @ViewById(R.id.result)
    TextView _resultView;

    List<ServiceTimeSelectionItem> _dataList;
    MyListAdapter _listAdapter = new MyListAdapter();
    int _lastDateIndexSelected = 0;
    int _lastTimeIndexSelected = 0;
    int _indexSelected = -1;
    int _lastDuration = 0;
    int _lastCount = 0;

    public ServiceTimeSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _context = context;
    }

    public ServiceTimeSelectionView(Context context) {
        super(context);
        _context = context;
    }

    public ServiceTimeSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
    }

    public void setListener(ServiceTimeSelectionListener listener) {
        _listener = listener;
    }

    void initData() {
        _dataList = new ArrayList<>();
        String[] squares = getResources().getStringArray(R.array.square_selection);
        int[] durations = getResources().getIntArray(R.array.service_duration);
        int[] counts = getResources().getIntArray(R.array.servant_count);
        for (int i = 0; i < squares.length; i++) {
            ServiceTimeSelectionItem item = new ServiceTimeSelectionItem();
            item.description = squares[i];
            item.duration = durations[i];
            item.count = counts[i];
            _dataList.add(item);
        }
    }

    public void setTimeArray( List<ServiceTimeItem> times) {
        _timePicker.setTimeArray(_lastTimeIndexSelected, times);
    }

 	public void setSelection() {
        _listAdapter.notifyDataSetChanged();
    }
    @AfterViews
    void afterViews() {
        initData();
        _listView.setAdapter(_listAdapter);
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActionSheetDialog dialog = new ActionSheetDialog(_context);
                View custom = LayoutInflater.from(_context).inflate(R.layout.dialog_select_service_time, null);
                final NumericTextView durationView = (NumericTextView)custom.findViewById(R.id.numDuration);
                durationView.setListener(new NumericTextView.NumberChangedListener() {
                    @Override
                    public void onNumberChanged(int value) {
                        if (_listener != null) {
                            _listener.onDurationChanged(value);
                        }
                    }
                });
                final NumericTextView countView = (NumericTextView)custom.findViewById(R.id.numServant);
                countView.setListener(new NumericTextView.NumberChangedListener() {
                    @Override
                    public void onNumberChanged(int value) {
                        if (_listener != null) {
                            _listener.onCountChanged(value);
                        }
                    }
                });
                _timePicker = (ServiceTimePicker)custom.findViewById(R.id.timePicker);
                final ServiceTimePicker timePicker = _timePicker;
                timePicker.setListener(new ServiceTimePicker.DateSelectionChangeListener() {
                    @Override
                    public void onDateSelectionChanged(String date) {
                        if (_listener != null) {
                            _listener.onDateChanged(date);
                        }
                    }
                });
                durationView.setMinValue(3);
                countView.setMinValue(1);

                dialog.setCustomView(custom);
                dialog.setClickListener(new ActionSheetDialog.DialogClickListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onDone(DialogInterface dialog) {
                        int count = countView.getValue();
                        int duration = durationView.getValue();
                        String date = timePicker.getDate();
                        String time = timePicker.getTime();
                        if (time != null && _listener != null)  {
                            _lastCount = count;
                            _lastDuration = duration;

                            _lastDateIndexSelected = timePicker.getDateIndex();
                            _lastTimeIndexSelected = timePicker.getTimeIndex();
                            _listener.onFinish(duration, count, date, time);
                            _resultView.setText(date + " " + time);
                        }
                        dialog.dismiss();
                    }
                });

                if (_indexSelected != position) {
                    _indexSelected = position;
                    ServiceTimeSelectionItem data = _dataList.get(position);

                    durationView.setValue(data.duration);
                    countView.setValue(data.count);
                    _lastDuration = data.duration;
                    _lastCount = data.count;
                }
                else {
                    durationView.setValue(_lastDuration);
                    countView.setValue(_lastCount);
                }

                if (_listener != null) {
                    int duration = durationView.getValue();
                    int count = countView.getValue();
                    _lastCount = count;
                    _lastDuration = duration;
                    _listener.onInit(duration, count);
                }

                _timePicker.setDateSelection(_lastDateIndexSelected);
                dialog.show();
            }
        });
    }

    class MyListAdapter extends BaseAdapter {
        class ViewHolder {
            public TextView text;
        }
        @Override
        public int getCount() {
            return (_dataList == null ? 0 : _dataList.size());
        }

        @Override
        public Object getItem(int i) {
            return _dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(_context).inflate(R.layout.list_item_service_time, null);
                holder = new ViewHolder();
                holder.text = (TextView)view.findViewById(R.id.text);
                view.setTag(holder);
            } else {
                holder = (ViewHolder)view.getTag();
            }

            ServiceTimeSelectionItem item = _dataList.get(i);
            holder.text.setText(item.description);

            if (_indexSelected == i) {
                holder.text.setSelected(true);
            }
            else {
                holder.text.setSelected(false);
            }
            return view;
        }
    }
}
