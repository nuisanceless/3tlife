package cn.life3t.life3t.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;
import cn.life3t.life3t.common.ClearEditText;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.AddressInfo;
import cn.life3t.life3t.message.request.AddAddressRequest;
import cn.life3t.life3t.message.response.BaseResponse;

/**
 * Created by wuguohao on 15-6-5.
 */
@EActivity(R.layout.activity_add_address)
public class AddAddressActivity extends BaseActivity
{
    public static final int REQUEST = 1;
    AddAddressRequest mNewAddress = new AddAddressRequest();

    @ViewById(R.id.street)
    TextView mStreetAddress;

    @ViewById(R.id.detail)
    ClearEditText mDetail;

    @ViewById(R.id.if_default)
    CheckBox mIfDefault;

    @Click(R.id.address_layout)
    void toSelectAddress()
    {
        Intent intent = new Intent(this, AddressSelectActivity.class);
        startActivityForResult(intent, REQUEST);
    }

    @Click(R.id.affirm)
    void onAddAddress ()
    {
        if (mStreetAddress.getText().toString() == null || mStreetAddress.getText().toString().equals(""))
        {
            _activityHelper.showToast("请选择地址");
            return;
        }
        if (mDetail.getText().toString() == null || mDetail.getText().toString().equals(""))
        {
            _activityHelper.showToast("请输入" + getString(R.string.hint_detail_address));
            return;
        }
        mNewAddress.addressSub = mDetail.getText().toString();
        mNewAddress.isdefault = mIfDefault.isChecked() ? 1 : 0;
        addAddress();
    }

    @Background
    void addAddress ()
    {
        BaseResponse resp = _restClient.addAddress(mNewAddress);
        uiAfterAddAddress(resp);
    }

    @UiThread
    void uiAfterAddAddress (BaseResponse resp)
    {
        if (BaseResponse.hasError(resp))
        {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else
        {
            _activityHelper.showToast(getString(R.string.add_address_success));
            setResult(RESULT_OK);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Bundle b = data.getExtras();
                AddressInfo info = (AddressInfo)b.get("address");

                mNewAddress.name = info.name;
                mNewAddress.addressProvince = info.addressProvince;
                mNewAddress.addressCity = info.addressCity;
                mNewAddress.addressDistrict = info.addressDistrict;
                mNewAddress.addressStreet = info.addressStreet;
                mNewAddress.addressMain = info.addressMain;
                mNewAddress.isdefault = info.isdefault;
                mNewAddress.contact = info.contact;
                mNewAddress.phone = info.phone;
                mNewAddress.longitude = info.longitude;
                mNewAddress.latitude = info.latitude;

                mStreetAddress.setText(info.name);
                break;
            default:
                break;
        }
    }
}
