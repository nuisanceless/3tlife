package cn.life3t.life3t.cleaning;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.life3t.life3t.ActivityHelper_;
import cn.life3t.life3t.MyApplication;
import cn.life3t.life3t.R;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.StaffInfo;
import cn.life3t.life3t.message.StaffRequestInfo;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.StaffListResponse;
import cn.life3t.life3t.order.StaffDetailActivity_;
import cn.life3t.life3t.rest.MyRestClient;

/**
 * Created by Lei on 2015/5/28.
 */
@EActivity(R.layout.activity_staff_list)
public class ServiceStaffListActivity extends BaseActivity {
    private static final int REQUEST_CODE_MAP = 1;

    SparseArray<StaffInfo> _isCheckMap =  new SparseArray<StaffInfo>();

    List<StaffInfo> _dataList;
    MyListAdapter _adapter = new MyListAdapter();

    @ViewById(R.id.list)
    PullToRefreshListView _listView;

    @Extra
    int _targetCount = 0;

    @Extra
    StaffRequestInfo _requestInfo;

    @Extra
    int[] _selectedStaffIds;

    @Click(R.id.btn_custom_tv)
    void onMap() {
        double radius = 500;
        if (_dataList != null && _dataList.size() > 0) {
            radius = _dataList.get(_dataList.size() - 1).distance;
        }
        int[] ids = null;
        if (_isCheckMap.size() > 0) {
            int index = 0;
            ids = new int[_isCheckMap.size()];
            for (int i = 0; i < _isCheckMap.size(); i++) {
                ids[index++] = _isCheckMap.keyAt(i);
            }
        }

        StaffMapViewActivity_.intent(this)._requestInfo(_requestInfo)._targetCount(_targetCount)._initRadius(radius)._selectedStaffIds(ids).startForResult(REQUEST_CODE_MAP);
    }

    @AfterViews
    void afterViews() {
        _listView.setMode(PullToRefreshBase.Mode.BOTH);
        _listView.setScrollingWhileRefreshingEnabled(true);

        ILoadingLayout startLabels = _listView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel(getString(R.string.pull_down_to_refresh));
        startLabels.setRefreshingLabel(getString(R.string.hard_loading));
        startLabels.setReleaseLabel(getString(R.string.loosen_to_refresh));

        ILoadingLayout endLabels = _listView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel(getString(R.string.pull_up_to_refresh));
        endLabels.setRefreshingLabel(getString(R.string.hard_loading));
        endLabels.setReleaseLabel(getString(R.string.loosen_to_load_more));

        _listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getStaffList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getStaffList();
            }
        });
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StaffInfo staff = _dataList.get(position - _listView.getRefreshableView().getHeaderViewsCount());
                StaffDetailActivity_.intent(ServiceStaffListActivity.this).mStaffId(staff.id).start();
            }
        });

        _listView.setAdapter(_adapter);

        _listView.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK) {
            _isCheckMap.clear();
            _adapter.notifyDataSetChanged();
            Object result = data.getSerializableExtra("staff");
            if (result != null) {
                Object[] array = (Object[])result;
                for (Object staff : array) {
                    StaffInfo info = (StaffInfo)staff;
                    _isCheckMap.put(info.id, info);
                }
                if (_isCheckMap.size() >= _targetCount) {
                    setResult();
                }
            }
        }
    }

    @Background
    void getStaffList() {
        StaffListResponse response = _restClient.getStaffList(_requestInfo.date, _requestInfo.startMin, _requestInfo.endMin, _requestInfo.businessId, _requestInfo.longitude, _requestInfo.latitude);
        afterGetStaffList(response);
    }

    @UiThread
    void afterGetStaffList(StaffListResponse response) {
        _listView.onRefreshComplete();
        if (BaseResponse.hasError(response)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else {
            _dataList = response.auntList;
            initCheckMap();
            _listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            _adapter.notifyDataSetChanged();
        }
    }

    @Click(R.id.btn_back)
    void onBack() {
        setResult();
    }

    @Override
    public void onBackPressed() {
        setResult();
    }

    private void setResult() {
        Intent intent = new Intent();
        List<StaffInfo> list = new ArrayList<StaffInfo>();
        for (int i = 0; i < _isCheckMap.size(); i++) {
            list.add(_isCheckMap.valueAt(i));
        }
        if (list.size() > 0)
            intent.putExtra("staff", list.toArray());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initCheckMap() {
        if (_selectedStaffIds == null || _dataList == null)
            return;

        _isCheckMap.clear();
        for (StaffInfo staff : _dataList) {
            for (int id : _selectedStaffIds) {
                if (staff.id == id && staff.isIdle) {
                    _isCheckMap.put(id, staff);
                }
            }
        }
    }

    class MyListAdapter extends BaseAdapter {
        class ViewHolder {
            SimpleDraweeView portrait;
            ImageView verified;
            TextView name;
            TextView duration;
            RatingBar rating;
            TextView distance;
            CheckBox selected;
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
                view = LayoutInflater.from(ServiceStaffListActivity.this).inflate(R.layout.list_item_staff, null);
                holder = new ViewHolder();
                holder.portrait = (SimpleDraweeView)view.findViewById(R.id.portrait);
                holder.verified = (ImageView)view.findViewById(R.id.verified);
                holder.name = (TextView)view.findViewById(R.id.name);
                holder.duration = (TextView)view.findViewById(R.id.durationValue);
                holder.rating = (RatingBar)view.findViewById(R.id.ratingBar);
                holder.distance = (TextView)view.findViewById(R.id.distance);
                holder.selected = (CheckBox)view.findViewById(R.id.selected);
                view.setTag(holder);
            }
            else {
                holder = (ViewHolder)view.getTag();
            }
            final StaffInfo staff = _dataList.get(i);

            holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        if (_isCheckMap.size() >= _targetCount) {
                            buttonView.setChecked(false);
                            return;
                        }

                        _isCheckMap.put(staff.id, staff);

                        if (_isCheckMap.size() >= _targetCount) {
                            setResult();
                            return;
                        }
                    }
                    else {
                        _isCheckMap.remove(staff.id);
                    }
                }
            });

            if (staff.headUrl != null && !staff.headUrl.isEmpty()) {
                Uri uri = Uri.parse(staff.headUrl);
                holder.portrait.setImageURI(uri);
            }
            holder.name.setText(staff.name);
            int hour = staff.serviceTime/60;
            holder.duration.setText(Integer.toString(hour));
            holder.rating.setRating(staff.score);
            double fDistance = staff.distance / 1000.0;
            holder.distance.setText(String.format(getResources().getString(R.string.distance_placeholder), fDistance));
            holder.verified.setVisibility(staff.isVerified ? View.VISIBLE : View.INVISIBLE);
            holder.selected.setVisibility(staff.isIdle ? View.VISIBLE : View.INVISIBLE);

            if (staff.isIdle) {
                holder.selected.setVisibility(View.VISIBLE);

                if(_isCheckMap.get(staff.id, null) != null) {
                    holder.selected.setChecked(true);
                }
                else {
                    holder.selected.setChecked(false);
                }
            }
            else {
                _isCheckMap.remove(staff.id);
            }
            view.setEnabled(staff.isIdle);

            return view;
        }
    }
}
