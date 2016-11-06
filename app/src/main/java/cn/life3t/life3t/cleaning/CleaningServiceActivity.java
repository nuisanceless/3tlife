package cn.life3t.life3t.cleaning;

import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.message.ServiceSubItem;
import cn.life3t.life3t.main.SubServiceBaseActivity;
import cn.life3t.life3t.message.type.OrderType;
import cn.life3t.life3t.utils.Consts;

/**
 * Created by Lei on 2015/5/19.
 */
public class CleaningServiceActivity extends SubServiceBaseActivity {
    private static final int[] _arrImage = {
            R.drawable.cleaning_normal,
            R.drawable.cleaning_deep,
            R.drawable.cleaning_sofa,
            R.drawable.cleaning_floor,
            R.drawable.cleaning_rangehood
    };
    private static final String[] _arrPrice = { "35", "45", "180", "100", "100" };
    private static final int[] _arrPriceSuffix = {
            R.string.suffix_price_per_hour_least,
            R.string.suffix_price_per_hour_least,
            R.string.suffix_price_least,
            R.string.suffix_price_least,
            R.string.suffix_price_least };


    @Override
    protected String getActivityTitle() {
        return getString(R.string.cleaning_service);
    }

    @Override
    protected List<ServiceSubItem> getData() {
        List<ServiceSubItem> data = new ArrayList<>();
        String[] types = getResources().getStringArray(R.array.cleaning_service_types);
        String[] descriptions = getResources().getStringArray(R.array.cleaning_service_descriptions);
        for (int i = 0; i < types.length; i++) {
            ServiceSubItem item = new ServiceSubItem();
            item.image = _arrImage[i];
            item.title = types[i];
            item.price = _arrPrice[i];
            item.priceSuffix = getString(_arrPriceSuffix[i]);
            item.description = descriptions[i];
            data.add(item);
        }
        return data;
    }

    @Override
    protected void onInformation() {
        CleaningServiceInfoActivity_.intent(CleaningServiceActivity.this).start();
    }

    @Override
    protected void onListItemClick(AdapterView<?> parent, View view, int position, long id) {
        int type = OrderType.NORMAL;
        switch (position) {
            case 0:
                type = OrderType.NORMAL;
                break;
            case 1:
                type = OrderType.DEEP;
                break;
            case 2:
                type = OrderType.SOFA;
                break;
            case 3:
                type = OrderType.FLOOR;
                break;
            case 4:
                type = OrderType.HOOD;
                break;
            default:
                break;
        }
        CleaningServiceOrderSubmitActivity_.intent(this)._serviceType(type).start();
    }
}
