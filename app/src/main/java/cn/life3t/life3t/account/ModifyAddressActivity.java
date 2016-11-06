package cn.life3t.life3t.account;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gghl.view.wheelcity.CityPickerBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;
import cn.life3t.life3t.common.ActionDialog;
import cn.life3t.life3t.common.ClearEditText;
import cn.life3t.life3t.common.CommonDialog;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.AddressInfo;
import cn.life3t.life3t.message.request.ModifyAddressRequest;
import cn.life3t.life3t.message.response.BaseResponse;

/**
 * Created by wuguohao on 15-5-23.
 */
@EActivity(R.layout.activity_modify_address)
public class ModifyAddressActivity extends BaseActivity
{
    private static final int REQUEST = 1;

    @Extra
    AddressInfo _addressInfo;

    @ViewById(R.id.addressMain)
    TextView _addressMainView;

    @ViewById(R.id.addressSub)
    ClearEditText _addressSubView;

    @ViewById(R.id.setDefault)
    CheckBox _defaultView;

    @Click(R.id.affirm)
    void onAffirm ()
    {
        if (_addressSubView.getText().length() == 0) {
            _activityHelper.showToast(getString(R.string.error_detail_address_empty));
            _addressSubView.requestFocus();
            return;
        }
        final CommonDialog dialog = new CommonDialog(ModifyAddressActivity.this)
                .builder()
                .setContent(getString(R.string.affirm_to_modify_address));
        dialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onModifyAddress();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Click(R.id.addressMain)
    void onChangeAddress() {
        Intent intent = new Intent(this, AddressSelectActivity.class);
        intent.putExtra("address", _addressInfo);
        startActivityForResult(intent, REQUEST);
    }

    @AfterViews
    void afterViews() {
        updateViews();
    }

    private void updateViews() {
        _addressMainView.setText(_addressInfo.addressMain);
        _addressSubView.setText(_addressInfo.addressSub);
        _defaultView.setChecked(_addressInfo.isdefault == 1 ? true : false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST && resultCode == RESULT_OK) {
            AddressInfo address = (AddressInfo)data.getSerializableExtra("address");

            _addressInfo.name = address.name;
            _addressInfo.longitude = address.longitude;
            _addressInfo.latitude = address.latitude;
            _addressInfo.addressCity = address.addressCity;
            _addressInfo.addressStreet = address.addressStreet;
            _addressInfo.addressMain = address.addressMain;
            updateViews();
        }
    }

    @Background
    void onModifyAddress() {
        _activityHelper.showLoadingDialog(null);
        ModifyAddressRequest request = new ModifyAddressRequest();
        request.id = _addressInfo.id;
        request.name = _addressInfo.name;
        request.addressProvince = _addressInfo.addressProvince;
        request.addressCity = _addressInfo.addressCity;
        request.addressDistrict = _addressInfo.addressDistrict;
        request.addressStreet = _addressInfo.addressStreet;
        request.addressMain = _addressInfo.addressMain;
        request.addressSub = _addressSubView.getText().toString();
        request.isdefault = _defaultView.isChecked() ? 1 : 0;
        request.contact = _addressInfo.contact;
        request.phone = _addressInfo.phone;
        request.longitude = _addressInfo.longitude;
        request.latitude = _addressInfo.latitude;
        BaseResponse response = _restClient.modifyAddress(request);
        _activityHelper.dismissLoadingDialog();
        afterModifyAddress(response);
    }

    @UiThread
    void afterModifyAddress(BaseResponse response) {
        if (BaseResponse.hasError(response)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else {
            Intent intent = new Intent();
            _addressInfo.addressSub = _addressSubView.getText().toString();
            intent.putExtra("address", _addressInfo);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
