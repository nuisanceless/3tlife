package cn.life3t.life3t.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.message.ServiceTimeItem;

/**
 * Created by Lei on 2015/5/25.
 */

@EViewGroup(R.layout.view_service_time_picker)
public class ServiceTimePicker extends RelativeLayout {
    public static interface DateSelectionChangeListener {
        public void onDateSelectionChanged(String date);
    }
    private String[] _ARRAY_DAY_OF_WEEK;
    private String[] _ARRAY_DAY_OFFSET;
    private Context _context;
    MyListAdapter _listAdapter;
    MyGridAdapter _gridAdapter;

    @ViewById(R.id.list)
    HorizontalListView _list;

    @ViewById(R.id.grid)
    GridView _grid;

    int _indexDateSelected = -1;
    int _indexTimeSelected = -1;
    int _dayCount = 7;
    List<ServiceDateItem> _dateList;
    List<ServiceTimeItem> _timeList;
    DateSelectionChangeListener _listener;

    public ServiceTimePicker(Context context) {
        super(context);
        _context = context;
    }

    public ServiceTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
    }

    public void setListener(DateSelectionChangeListener listener) {
        _listener = listener;
    }

    @AfterViews
    void afterViews() {
        _ARRAY_DAY_OF_WEEK = _context.getResources().getStringArray(R.array.day_of_week);
        _ARRAY_DAY_OFFSET = _context.getResources().getStringArray(R.array.day_offset);
        initDayList();
        _listAdapter = new MyListAdapter();
        _gridAdapter = new MyGridAdapter();

        _list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setDateSelection(position);
            }
        });

        _grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServiceTimeItem item = _timeList.get(position);
                if (!item.available)
                    return;

                if (_indexTimeSelected != position) {
                    _indexTimeSelected = position;
                    _gridAdapter.notifyDataSetChanged();
                }
            }
        });
        _list.setAdapter(_listAdapter);
        _grid.setAdapter(_gridAdapter);
    }

    public void setDateSelection(int position) {
        if (_indexDateSelected != position) {
            _indexDateSelected = position;
            _listAdapter.notifyDataSetChanged();

            if (_listener != null) {
                String date = getDate();
                _listener.onDateSelectionChanged(date);
            }
        }
    }

    public void setTimeArray(int position, List<ServiceTimeItem> times) {
        _timeList = times;
        if (times.size() <= position || !times.get(position).available) {
            int index = 0;
            for (ServiceTimeItem time : times) {
                if (time.available) {
                    _indexTimeSelected = index;
                    break;
                }
                index++;
            }
        }
        else {
            _indexTimeSelected = position;
        }
        _gridAdapter.notifyDataSetChanged();
    }

    public String getDate() {
        if (_indexDateSelected >= 0)
            return _dateList.get(_indexDateSelected).dateOfYear;
        return null;
    }

    public int getDateIndex() {
        return _indexDateSelected;
    }

    public String getTime() {
        if (_indexTimeSelected >= 0)
            return _timeList.get(_indexTimeSelected).time;
        return null;
    }

    public int getTimeIndex() {
        return _indexTimeSelected;
    }

    void initDayList() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        _dateList = new ArrayList<>();
        for (int i = 0; i < _dayCount; i++) {
            calendar.add(Calendar.DATE, 1);
            ServiceDateItem item = new ServiceDateItem();
            item.date = sdf.format(calendar.getTime());
            item.dateOfYear = sdfYear.format(calendar.getTime());
            if (i < 2) {
                item.dayOfWeek = _ARRAY_DAY_OFFSET[i];
            }
            else {
                item.dayOfWeek = _ARRAY_DAY_OF_WEEK[(i+dayOfWeek)%7];
            }
            _dateList.add(item);
        }
    }

    class MyListAdapter extends BaseAdapter {
        class ViewHolder {
            TextView date;
            TextView dayOfWeek;
        }
        @Override
        public int getCount() {
            return (_dateList == null ? 0 : _dateList.size());
        }

        @Override
        public Object getItem(int i) {
            return _dateList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(_context).inflate(R.layout.list_item_service_date, null);
                holder = new ViewHolder();
                holder.date = (TextView)view.findViewById(R.id.date);
                holder.dayOfWeek = (TextView)view.findViewById(R.id.dayOfWeek);
                view.setTag(holder);
            }
            else {
                holder = (ViewHolder)view.getTag();
            }

            ServiceDateItem item = _dateList.get(i);
            holder.date.setText(item.date);
            holder.dayOfWeek.setText(item.dayOfWeek);

            if (_indexDateSelected == i) {
                view.setSelected(true);
            }
            else {
                view.setSelected(false);
            }

            return view;
        }
    }

    class MyGridAdapter extends BaseAdapter {
        class ViewHolder {
            TextView time;
        }
        @Override
        public int getCount() {
            return (_timeList == null ? 0 : _timeList.size());
        }

        @Override
        public Object getItem(int i) {
            return _timeList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(_context).inflate(R.layout.grid_item_time, null);
                holder = new ViewHolder();
                holder.time = (TextView)view.findViewById(R.id.time);
                view.setTag(holder);
            }
            else {
                holder = (ViewHolder)view.getTag();
            }

            ServiceTimeItem item = _timeList.get(i);
            holder.time.setText(item.time);
            holder.time.setEnabled(item.available);

            if (_indexTimeSelected == i) {
                holder.time.setSelected(true);
            }
            else {
                holder.time.setSelected(false);
            }
            return view;
        }
    }

    class ServiceDateItem {
        public String date;
        public String dateOfYear;
        public String dayOfWeek;
    }
}
