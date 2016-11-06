package com.gghl.view.wheelcity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.gghl.view.wheelcity.adapters.ArrayWheelAdapter;

import cn.life3t.life3t.R;

/**
 * Created by wuguohao on 15-5-24.
 */
public class CityPickerBuilder
{
    private String cityTxt;
    private Context mContext;
    private static CityPickerBuilder cityPickerBuilder;

    private CityPickerBuilder(Context context)
    {
        this.mContext = context;
    }

    public static CityPickerBuilder getInstance (Context context)
    {
        if (cityPickerBuilder == null)
        {
            cityPickerBuilder = new CityPickerBuilder(context);
        }
        return cityPickerBuilder;
    }

    public View initCityPicker ()
    {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.wheelcity_cities_layout, null);


        final String provinces[] = AddressData.PROVINCES;
        final String cities[][] = AddressData.CITIES;
        final String ccities[][][] = AddressData.COUNTIES;

        final WheelView provinceView = (WheelView) contentView.findViewById(R.id.wheelcity_country);
        updateProvinces(provinceView, provinces);

        final WheelView cityView = (WheelView) contentView.findViewById(R.id.wheelcity_city);

        final WheelView ccityView = (WheelView) contentView.findViewById(R.id.wheelcity_ccity);

        provinceView.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateCities(cityView, cities, newValue);
                cityTxt = AddressData.PROVINCES[provinceView.getCurrentItem()]
                        + " | "
                        + AddressData.CITIES[provinceView.getCurrentItem()][cityView.getCurrentItem()]
                        + " | "
                        + AddressData.COUNTIES[provinceView.getCurrentItem()][cityView.getCurrentItem()][ccityView.getCurrentItem()];
            }
        });

        cityView.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateCcities(ccityView, ccities, provinceView.getCurrentItem(), newValue);
                cityTxt = AddressData.PROVINCES[provinceView.getCurrentItem()]
                        + " | "
                        + AddressData.CITIES[provinceView.getCurrentItem()][cityView.getCurrentItem()]
                        + " | "
                        + AddressData.COUNTIES[provinceView.getCurrentItem()][cityView.getCurrentItem()][ccityView.getCurrentItem()];
            }
        });

        ccityView.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                cityTxt = AddressData.PROVINCES[provinceView.getCurrentItem()]
                        + " | "
                        + AddressData.CITIES[provinceView.getCurrentItem()][cityView.getCurrentItem()]
                        + " | "
                        + AddressData.COUNTIES[provinceView.getCurrentItem()][cityView.getCurrentItem()][ccityView.getCurrentItem()];
            }
        });

        provinceView.setCurrentItem(1);// 设置北京
        cityView.setCurrentItem(1);
        ccityView.setCurrentItem(1);
        return contentView;
    }

    /**
     * Updates the provinces wheel
     */
    private void updateProvinces (WheelView province, String provinces[])
    {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(mContext, provinces);
        adapter.setTextSize(18);
        province.setViewAdapter(adapter);
        province.setCurrentItem(0);
    }

    /**
     * Updates the city wheel
     */
    private void updateCities(WheelView city, String cities[][], int index) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(mContext, cities[index]);
        adapter.setTextSize(18);
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    /**
     * Updates the ccity wheel
     */
    private void updateCcities(WheelView ccity, String ccities[][][], int index, int index2) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(mContext, ccities[index][index2]);
        adapter.setTextSize(18);
        ccity.setViewAdapter(adapter);
        ccity.setCurrentItem(0);
        // 广东潮州
    }
}
