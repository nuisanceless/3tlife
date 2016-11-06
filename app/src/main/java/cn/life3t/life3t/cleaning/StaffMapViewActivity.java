package cn.life3t.life3t.cleaning;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import com.baidu.mapapi.utils.DistanceUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.StaffInfo;
import cn.life3t.life3t.message.StaffRequestInfo;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.StaffListResponse;
import cn.life3t.life3t.order.StaffDetailActivity_;

/**
 * Created by Lei on 2015/6/1.
 */
@EActivity
public class StaffMapViewActivity extends BaseActivity implements BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener {
    private static final int[] SCALE = { 20000000, 10000000, 5000000, 2000000, 1000000, 500000, 200000, 100000, 50000, 25000, 20000, 10000, 5000, 2000, 1000, 500, 200, 100, 50, 20, 10, 5, 2, 1};

    MapView _mapView;
    BaiduMap _baiduMap;
    BitmapDescriptor _bdAddress;
    BitmapDescriptor _bdAunt;
    BitmapDescriptor _bdAuntSelected;
    BitmapDescriptor _bdAuntChecked;

    View _infoView;
    SimpleDraweeView _headView;
    ImageView _verifiedView;
    TextView _nameView;
    RatingBar _ratingView;
    TextView _distanceView;
    TextView _durationView;
    CheckBox _checkView;

    @Extra
    double _initRadius;

    @Extra
    StaffRequestInfo _requestInfo;
    @Extra
    int _targetCount = 0;
    @Extra
    int[] _selectedStaffIds;
    List<StaffInfo> _dataList;
    SparseArray<StaffInfo> _isCheckMap =  new SparseArray<StaffInfo>();
    StaffInfo _lastStaff = null;
    Marker _lastMarker = null;
    boolean isFirstTime = true;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_staff_mapview);
        //获取地图控件引用
        _mapView = (MapView) findViewById(R.id.mapView);

        _infoView = findViewById(R.id.staffInfo);
        _infoView.setVisibility(View.INVISIBLE);
        _headView = (SimpleDraweeView) findViewById(R.id.portrait);
        _verifiedView = (ImageView) findViewById(R.id.verified);
        _nameView = (TextView) findViewById(R.id.name);
        _ratingView = (RatingBar) findViewById(R.id.ratingBar);
        _distanceView = (TextView) findViewById(R.id.distance);
        _durationView = (TextView) findViewById(R.id.durationValue);
        _checkView = (CheckBox) findViewById(R.id.selected);
        View titleTextBtn = findViewById(R.id.btn_custom_tv);
        View titleBackBtn = findViewById(R.id.btn_back);

        titleTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
            }
        });

        titleBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
            }
        });

        _infoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_lastStaff == null)
                    return;

                StaffDetailActivity_.intent(StaffMapViewActivity.this).mStaffId(_lastStaff.id).start();
            }
        });

        _checkView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Marker marker = (Marker)buttonView.getTag();

                Bundle bundle = marker.getExtraInfo();
                StaffInfo staff = (StaffInfo) bundle.getSerializable("staff");

                if(isChecked) {
                    if (_isCheckMap.size() >= _targetCount) {
                        buttonView.setChecked(false);
                        return;
                    }

                    if (_isCheckMap.indexOfValue(staff) == -1) {
                        _isCheckMap.put(staff.id, staff);

                        if (_isCheckMap.size() >= _targetCount) {
                            setResult();
                            return;
                        }
                    }
                }
                else {
                    _isCheckMap.remove(staff.id);
                }
                checkMarker(marker, isChecked);
            }
        });

        _baiduMap = _mapView.getMap();
        _baiduMap.setOnMapStatusChangeListener(this);
        _baiduMap.setOnMarkerClickListener(this);
        _baiduMap.setOnMapClickListener(this);
        initBitmapDescriptor();

        LatLng ll = new LatLng(_requestInfo.latitude, _requestInfo.longitude);
        float maxLevel = _baiduMap.getMaxZoomLevel();
        float minLevel = _baiduMap.getMinZoomLevel();
        float level = getZoomLevelByRadius(_initRadius, minLevel, maxLevel);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, level);
        _baiduMap.animateMapStatus(u);
        getStaffList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _baiduMap.setOnMapStatusChangeListener(null);
        _mapView.onDestroy();
        recycleBitmapDescriptor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _mapView.onResume();
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        Point ptLeft = mapStatus.targetScreen;
        ptLeft.x = 0;
        LatLng llLeft = _baiduMap.getProjection().fromScreenLocation(ptLeft);
        double radius = DistanceUtil.getDistance(mapStatus.target, llLeft);
        getStaffList();
    }

    private void initBitmapDescriptor() {
        _bdAddress = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        _bdAunt = BitmapDescriptorFactory.fromResource(R.drawable.map_aunt);
        _bdAuntSelected = BitmapDescriptorFactory.fromResource(R.drawable.map_aunt_selected);
        _bdAuntChecked = BitmapDescriptorFactory.fromResource(R.drawable.map_aunt_checked);
    }

    private void recycleBitmapDescriptor() {
        if (_bdAddress != null)
            _bdAddress.recycle();
        if (_bdAunt != null)
            _bdAunt.recycle();
        if (_bdAuntSelected != null)
            _bdAuntSelected.recycle();
        if (_bdAuntChecked != null)
            _bdAuntChecked.recycle();
    }

    private float getZoomLevelByRadius(double radius, float minLevel, float maxLevel) {
        float level = maxLevel;
        for (int i = 1; i < SCALE.length; i++) {
            if (radius > SCALE[i]*2.5) {
                level = i;
                break;
            }
        }

        if (level < minLevel)
            level = minLevel;
        else if (level > maxLevel)
            level = maxLevel;
        return level;
    }

    private void initCheckMap() {
        if (_selectedStaffIds == null || _dataList == null)
            return;

        for (StaffInfo staff : _dataList) {
            for (int id : _selectedStaffIds) {
                if (staff.id == id && staff.isIdle) {
                    _isCheckMap.put(id, staff);
                }
            }
        }
    }

    @Background
    void getStaffList() {
        _activityHelper.showLoadingDialog(null);
        StaffListResponse response = _restClient.getStaffList(_requestInfo.date, _requestInfo.startMin, _requestInfo.endMin, _requestInfo.businessId, _requestInfo.longitude, _requestInfo.latitude);
        _activityHelper.dismissLoadingDialog();
        afterGetStaffList(response);
    }

    @UiThread
    void afterGetStaffList(StaffListResponse response) {
        if (BaseResponse.hasError(response)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else {
            _dataList = response.auntList;
            if (isFirstTime) {
                isFirstTime = false;
                initCheckMap();
            }
            showStaffOnMap();
        }
    }

    private void showStaffOnMap() {
        _baiduMap.clear();
        LatLng llAddress = new LatLng(_requestInfo.latitude, _requestInfo.longitude);
        OverlayOptions ooAddress = new MarkerOptions().position(llAddress).icon(_bdAddress)
                .zIndex(9).draggable(false);
        _baiduMap.addOverlay(ooAddress);
        for (StaffInfo staff : _dataList) {
            LatLng ll = new LatLng(staff.latitude, staff.longitude);
            OverlayOptions oo = null;
            if (_isCheckMap.get(staff.id) == null) {
                oo = new MarkerOptions().position(ll).icon(_bdAunt)
                        .zIndex(9).draggable(false);
            }
            else {
                oo = new MarkerOptions().position(ll).icon(_bdAuntChecked)
                        .zIndex(9).draggable(false);
            }
            Marker marker = (Marker) _baiduMap.addOverlay(oo);
            Bundle bundle = new Bundle();
            bundle.putSerializable("staff", staff);
            marker.setExtraInfo(bundle);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Bundle bundle = marker.getExtraInfo();
        if (bundle == null)
            return false;
        StaffInfo staff = (StaffInfo) bundle.getSerializable("staff");
        showStaffInfo(marker, staff);
        return true;
    }

    private void showStaffInfo(Marker marker, StaffInfo staff) {
        if (staff.headUrl != null && !staff.headUrl.isEmpty())
            _headView.setImageURI(Uri.parse(staff.headUrl));
        _verifiedView.setVisibility(staff.isVerified ? View.VISIBLE : View.INVISIBLE);
        _nameView.setText(staff.name);
        _ratingView.setRating(staff.score);
        double distance = staff.distance / 1000.0;
        _distanceView.setText(String.format(getString(R.string.distance_placeholder), distance));

        int hour = staff.serviceTime/60;
        _durationView.setText(Integer.toString(hour));

        _checkView.setTag(marker);
        if (staff.isIdle) {
            _checkView.setVisibility(View.VISIBLE);

            if(_isCheckMap.get(staff.id, null) != null) {
                _checkView.setChecked(true);
            }
            else {
                _checkView.setChecked(false);
            }
        }
        else {
            _checkView.setVisibility(View.INVISIBLE);
//            _isCheckMap.remove(staff.id);
//            _checkView.setChecked(false);
        }
        if (staff.isIdle) {
            _infoView.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
        else {
            _infoView.setBackgroundResource(R.drawable.ordered);
        }
        _infoView.setVisibility(View.VISIBLE);

        if (!marker.equals(_lastMarker)) {
            unSelectMarker(_lastMarker);
            selectMarker(marker);
        }
        _lastMarker = marker;
        _lastStaff = staff;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        _infoView.setVisibility(View.INVISIBLE);
        unSelectMarker(_lastMarker);
        _lastMarker = null;
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    private void selectMarker(Marker marker) {
        if (marker == null)
            return;
        Bundle bundle = marker.getExtraInfo();
        if (bundle == null)
            return;

        StaffInfo staff = (StaffInfo) bundle.getSerializable("staff");
        if (_isCheckMap.get(staff.id) == null) {
            marker.setIcon(_bdAuntSelected);
        }
    }

    private void unSelectMarker(Marker marker) {
        if (marker == null)
            return;
        Bundle bundle = marker.getExtraInfo();
        if (bundle == null)
            return;

        StaffInfo staff = (StaffInfo) bundle.getSerializable("staff");
        if (_isCheckMap.get(staff.id) == null) {
            marker.setIcon(_bdAunt);
        }
    }

    private void checkMarker(Marker marker, boolean check) {
        if (marker == null)
            return;

        if (check) {
            marker.setIcon(_bdAuntChecked);
        }
        else {
            marker.setIcon(_bdAuntSelected);
        }
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
}
