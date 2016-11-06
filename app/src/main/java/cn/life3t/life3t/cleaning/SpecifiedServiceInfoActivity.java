package cn.life3t.life3t.cleaning;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;
import cn.life3t.life3t.message.type.OrderType;

/**
 * Created by Lei on 2015/8/5.
 */
@EActivity(R.layout.activity_specified_service_info)
public class SpecifiedServiceInfoActivity extends Activity {
    @Extra
    int _serviceType;

    @ViewById(R.id.icon)
    ImageView _iconImage;

    @ViewById(R.id.type)
    TextView _typeText;

    @ViewById(R.id.price)
    TextView _priceText;

    @ViewById(R.id.info)
    TextView _infoText;

    @ViewById(R.id.reference)
    View _refView;

    @AfterViews
    void afterViews() {
        _iconImage.setImageResource(OrderType.getOrderTypeLargeImageRes(_serviceType));
        _typeText.setText(OrderType.getOrderTypeStringRes(_serviceType));

        switch (_serviceType) {
            case OrderType.NORMAL:
                _priceText.setText(R.string.price_normal_clean_2);
                _infoText.setText(R.string.info_normal_clean);
                _refView.setVisibility(View.VISIBLE);
                break;
            case OrderType.DEEP:
                _priceText.setText(R.string.price_deep_clean);
                _infoText.setText(R.string.info_deep_clean);
                _refView.setVisibility(View.VISIBLE);
                break;
            case OrderType.FLOOR:
                _priceText.setText(R.string.price_floor);
                _infoText.setText(R.string.info_floor);
                break;
            case OrderType.SOFA:
                _priceText.setText(R.string.price_sofa_2);
                _infoText.setText(R.string.info_sofa);
                break;
            case OrderType.HOOD:
                _priceText.setText(R.string.price_hood);
                _infoText.setText(R.string.info_hood);
                break;
        }
    }
}
