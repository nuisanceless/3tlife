package cn.life3t.life3t.order;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.life3t.life3t.ActivityHelper_;
import cn.life3t.life3t.MyApplication;
import cn.life3t.life3t.MyApplication_;
import cn.life3t.life3t.R;
import cn.life3t.life3t.common.HorizontalListView;
import cn.life3t.life3t.message.ServiceTimeItem;
import cn.life3t.life3t.message.Time;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.StaffScheduleResponse;
import cn.life3t.life3t.rest.MyRestClient;

/**
 * Created by Lei on 2015/7/14.
 */
public class StaffAvailableTimeDialog extends Dialog {
    private String[] _ARRAY_DAY_OF_WEEK;
    private String[] _ARRAY_DAY_OFFSET;
    private Context _context;
    private MyApplication _app;
    private MyRestClient _restClient;
    private ActivityHelper_ _activityHelper;

    private ProgressBar _progressBar;
    private HorizontalListView _list;
    private GridView _grid;
    private MyListAdapter _listAdapter;
    private MyGridAdapter _gridAdapter;

    private int _staffId = -1;
    private int _dayCount = 7;
    private int _indexDateSelected = 0;
    private List<ServiceDateItem> _dateList;
    private List<ServiceTimeItem> _timeList;

    public StaffAvailableTimeDialog(Context context, int staffId) {
        super(context, R.style.add_dialog);
        _context = context;
        _staffId = staffId;
        _app = MyApplication_.getInstance();
        _restClient = _app.restClient();
        _activityHelper = ActivityHelper_.getInstance_(context);

        _ARRAY_DAY_OF_WEEK = _context.getResources().getStringArray(R.array.day_of_week);
        _ARRAY_DAY_OFFSET = _context.getResources().getStringArray(R.array.day_offset);

        _listAdapter = new MyListAdapter();
        _gridAdapter = new MyGridAdapter();
        initDayList();
        initView();

        ServiceDateItem item = _dateList.get(_indexDateSelected);
        getAvailableTime(item.dateOfYear);
    }

    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(_context);
        View view = layoutInflater.inflate(R.layout.dialog_staff_available_time, null);
        _progressBar = (ProgressBar) view.findViewById(R.id.progress);
        _list = (HorizontalListView) view.findViewById(R.id.list);
        _grid = (GridView) view.findViewById(R.id.grid);
        _list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (_indexDateSelected != position) {
                    _indexDateSelected = position;
                    _listAdapter.notifyDataSetChanged();

                    ServiceDateItem item = (ServiceDateItem)_listAdapter.getItem(position);
                    getAvailableTime(item.dateOfYear);
                }
            }
        });
        _list.setAdapter(_listAdapter);
        _grid.setAdapter(_gridAdapter);

        getWindow().setContentView(view);
    }

    private void getAvailableTime(String date) {
        _progressBar.setVisibility(View.VISIBLE);
        new TimeAsyncTask().execute(date);
    }

    private void initDayList() {
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

    private class TimeAsyncTask extends AsyncTask<String, String, StaffScheduleResponse> {

        @Override
        protected StaffScheduleResponse doInBackground(String... params) {
            String date = params[0];
            StaffScheduleResponse response = _restClient.getStaffSchedule(_staffId, date);
            return response;
        }

        @Override
        protected void onPostExecute(StaffScheduleResponse response) {
            super.onPostExecute(response);
            _progressBar.setVisibility(View.GONE);
            if (BaseResponse.hasError(response)) {
                _activityHelper.showToast(BaseResponse.getErrorMessage(response));
            }
            else {
                if (response.list != null) {
                    String[] times = _context.getResources().getStringArray(R.array.schedule_time);
                    List<ServiceTimeItem> items = new ArrayList<>();
                    for (String time : times) {
                        ServiceTimeItem item = new ServiceTimeItem();
                        item.time = time;
                        item.available = false;
                        int timeIntStart = timeStringToInt(time);
                        for (Time t : response.list) {
                            if (t.startMin <= timeIntStart && t.endMin >= timeIntStart) {
                                item.available = true;
                                break;
                            }
                        }
                        items.add(item);
                    }
                    _timeList = items;
                    _gridAdapter.notifyDataSetChanged();
                }
            }
        }


        private int timeStringToInt(String time) {
            String[] t = time.split(":");
            int h = Integer.parseInt(t[0]);
            int m = Integer.parseInt(t[1]);
            return h*60+m;
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
                view = LayoutInflater.from(_context).inflate(R.layout.grid_item_available_time, null);
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

            return view;
        }
    }

    class ServiceDateItem {
        public String date;
        public String dateOfYear;
        public String dayOfWeek;
    }
}
