package cn.life3t.life3t.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.life3t.life3t.ActivityHelper_;
import cn.life3t.life3t.R;
import cn.life3t.life3t.common.ClearEditText;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.AddressInfo;

/**
 * Created by Lei on 2015/6/3.
 */
public class AddressSelectActivity extends BaseActivity implements BDLocationListener, OnGetPoiSearchResultListener, BaiduMap.OnMapStatusChangeListener, OnGetGeoCoderResultListener {
    ClearEditText _textAddress;
    View _btnBack;
    View _btnConfirm;
    ListView _suggestListView;
    MapView _mapView;
    BaiduMap _baiduMap;
    LocationClient _locClient;
    boolean _isFirstLoc = true;// 是否首次定位
    boolean _needSuggest = true;
    SuggestResultAdapter _sugAdapter;
    List<PoiInfo> _dataList = new ArrayList<>();
    BitmapDescriptor _bmDescriptor;
    PoiSearch _poiSearch;
    GeoCoder _geoCoder;
    PoiInfo _selectedPoiInfo;
    AddressInfo _addressInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_address_select);

        _bmDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker);

        _poiSearch = PoiSearch.newInstance();
        _poiSearch.setOnGetPoiSearchResultListener(this);
        _geoCoder = GeoCoder.newInstance();
        _geoCoder.setOnGetGeoCodeResultListener(this);

        _sugAdapter = new SuggestResultAdapter(_dataList);

        _suggestListView = (ListView) findViewById(R.id.suggestList);
        _suggestListView.setAdapter(_sugAdapter);
        _suggestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiInfo info = (PoiInfo)_sugAdapter.getItem(position);
                _selectedPoiInfo = info;
                _needSuggest = false;
                _textAddress.setText(info.name);
                _textAddress.setSelection(info.name.length());
                _btnConfirm.setVisibility(View.VISIBLE);

                LatLng ll = new LatLng(info.location.latitude,
                        info.location.longitude);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                _baiduMap.animateMapStatus(u);
            }
        });

        _textAddress = (ClearEditText) findViewById(R.id.address);
        _textAddress.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                _btnConfirm.setVisibility(View.GONE);
                if (cs.length() <= 0 || !_needSuggest) {
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                _poiSearch.searchInCity(new PoiCitySearchOption().city("杭州").keyword(cs.toString()).pageNum(0));
            }
        });
        _btnBack = findViewById(R.id.btn_back);
        _btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        _btnConfirm = findViewById(R.id.confirm);
        _btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressInfo info = new AddressInfo();
                info.name = _selectedPoiInfo.name;
                info.longitude = _selectedPoiInfo.location.longitude;
                info.latitude = _selectedPoiInfo.location.latitude;
                info.addressCity = _selectedPoiInfo.city;
                info.addressStreet = _selectedPoiInfo.address;
                info.addressMain = _selectedPoiInfo.name;
                Intent intent = new Intent();
                intent.putExtra("address", info);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        _mapView = (MapView) findViewById(R.id.mapView);
        _baiduMap = _mapView.getMap();

        _baiduMap.setOnMapStatusChangeListener(this);

        Object extra = getIntent().getSerializableExtra("address");
        if (extra != null) {
            _addressInfo = (AddressInfo) extra;

            PoiInfo info = new PoiInfo();
            info.location = new LatLng(_addressInfo.latitude, _addressInfo.longitude);
            info.name = _addressInfo.addressMain;
            info.address = _addressInfo.addressStreet;
            _selectedPoiInfo = info;
            _textAddress.setText(info.name);
            _textAddress.setSelection(info.name.length());
            _btnConfirm.setVisibility(View.VISIBLE);

//            _geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(info.location));

            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(info.location, _baiduMap.getMaxZoomLevel() - 4);
            _baiduMap.animateMapStatus(u);
            _geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(info.location));
            OverlayOptions oo = new MarkerOptions().position(info.location).icon(_bmDescriptor).draggable(false);
            _baiduMap.addOverlay(oo);

        }
        else {
            _baiduMap.setMyLocationEnabled(true);
            _baiduMap
                    .setMyLocationConfigeration(new MyLocationConfiguration(
                            MyLocationConfiguration.LocationMode.NORMAL, true, null));

            _locClient = new LocationClient(getApplicationContext());
            _locClient.registerLocationListener(this);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);// 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(50000);
            _locClient.setLocOption(option);
            _locClient.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        _mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        _geoCoder.destroy();
        _poiSearch.destroy();
        // 退出时销毁定位
        if (_locClient != null)
            _locClient.stop();
        // 关闭定位图层
        _baiduMap.setMyLocationEnabled(false);
        _mapView.onDestroy();
        if (_bmDescriptor != null)
            _bmDescriptor.recycle();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation == null || _mapView == null)
            return;
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(bdLocation.getDirection()).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        _baiduMap.setMyLocationData(locData);
        if (_isFirstLoc) {
            _isFirstLoc = false;
            LatLng ll = new LatLng(bdLocation.getLatitude(),
                    bdLocation.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, _baiduMap.getMaxZoomLevel() - 4);
            _baiduMap.animateMapStatus(u);
            _locClient.stop();
        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null
                || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND || poiResult.getAllPoi() == null) {
            return;
        }

        performSuggestion(poiResult.getAllPoi());
    }

    void performSuggestion(List<PoiInfo> data) {
        _sugAdapter.clear();
        _sugAdapter.addAll(data);
        _sugAdapter.notifyDataSetChanged();
        _suggestListView.smoothScrollToPosition(0);

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        if (_needSuggest)
            _geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
        else
            _needSuggest = true;

        _baiduMap.clear();
        OverlayOptions oo = new MarkerOptions().position(mapStatus.target).icon(_bmDescriptor).draggable(false);
        _baiduMap.addOverlay(oo);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR || reverseGeoCodeResult.getPoiList() == null) {
            return;
        }

        performSuggestion(reverseGeoCodeResult.getPoiList());
    }

    class SuggestResultAdapter extends BaseAdapter {
        private List<PoiInfo> mObjects;
        private final Object mLock = new Object();

        public SuggestResultAdapter(List<PoiInfo> data) {
            mObjects = data;
        }

        public void add(PoiInfo object) {
            synchronized (mLock) {
                mObjects.add(object);
            }
        }

        /**
         * Adds the specified Collection at the end of the array.
         *
         * @param collection The Collection to add at the end of the array.
         */
        public void addAll(Collection<? extends PoiInfo> collection) {
            synchronized (mLock) {
                mObjects.addAll(collection);
            }
        }

        /**
         * Adds the specified items at the end of the array.
         *
         * @param items The items to add at the end of the array.
         */
        public void addAll(PoiInfo ... items) {
            synchronized (mLock) {
                Collections.addAll(mObjects, items);
            }
        }

        /**
         * Inserts the specified object at the specified index in the array.
         *
         * @param object The object to insert into the array.
         * @param index The index at which the object must be inserted.
         */
        public void insert(PoiInfo object, int index) {
            synchronized (mLock) {
                mObjects.add(index, object);
            }
        }

        /**
         * Remove all elements from the list.
         */
        public void clear() {
            synchronized (mLock) {
                mObjects.clear();
            }
        }

        @Override
        public int getCount() {
            return mObjects == null? 0 : mObjects.size();
        }

        @Override
        public Object getItem(int position) {
            return mObjects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_addr_suggestion, null);

                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.text1);
                holder.address = (TextView) convertView.findViewById(R.id.text2);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            PoiInfo info = mObjects.get(position);
            holder.name.setText(info.name);
            holder.address.setText(info.address);

            return convertView;
        }

        private class ViewHolder {
            public TextView name;
            public TextView address;
        }
    }
    

}
