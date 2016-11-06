package cn.life3t.life3t.account;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gghl.view.wheelcity.WheelView;
import com.gghl.view.wheelcity.adapters.ArrayWheelAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import cn.life3t.life3t.MyPrefs;
import cn.life3t.life3t.MyPrefs_;
import cn.life3t.life3t.R;
import cn.life3t.life3t.common.ActionDialog;
import cn.life3t.life3t.common.ClearEditText;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.request.ChangeUserInfoRequest;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.UserInfoResponse;
import cn.life3t.life3t.utils.CommonUtils;
import cn.life3t.life3t.utils.Consts;

/**
 * Created by wuguohao on 15-5-18.
 */
@EActivity(R.layout.activity_user_info)
public class UserInfoActivity extends BaseActivity
{
    public static final int RESULT_NICKNAME_CHANGED = 1000;
    public static final int RESULT_LOGOUT = 1001;

    ActionDialog asd;
    UserInfoResponse mUserInfoResp;

    @Pref
    MyPrefs_ mPrefs;

    @Click(R.id.btn_back)
    void onBack() {
        modifyUserInfo();
    }

    @ViewById(R.id.nickname)
    ClearEditText mNickName;

    @ViewById(R.id.gender_tv)
    TextView mGenderTextView;

    @ViewById(R.id.email)
    ClearEditText mEmail;

    @ViewById(R.id.phone)
    TextView mPhone;

    @Click(R.id.modify_password)
    void startModifyPasswordActivity ()
    {
        ModifyPasswordActivity_.intent(UserInfoActivity.this).start();
    }

    @Click(R.id.logout)
    void logout() {
        Long lastActionShowTime = mPrefs.lastActionShowTime().get();
        mPrefs.clear();
        mPrefs.lastActionShowTime().put(lastActionShowTime);
        setResult(RESULT_LOGOUT);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        modifyUserInfo();
    }



    @AfterViews
    void init ()
    {
        getUserInfo();
    }

    @Background
    void getUserInfo ()
    {
        int userId = mPrefs.userId().get();
        _activityHelper.showLoadingDialog(null);
        mUserInfoResp = _restClient.getUserInfo(userId);
        _activityHelper.dismissLoadingDialog();
        updateUserInfo(mUserInfoResp);
    }

    /** 在ui上显示用户数据 */
    @UiThread
    void updateUserInfo (UserInfoResponse resp)
    {
        if (BaseResponse.hasError(resp))
        {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else
        {
            mNickName.setText(resp.nickname);
            mGenderTextView.setText(CommonUtils.getGenderString(resp.gender));
            mEmail.setText(resp.email);
            mPhone.setText(resp.phone);
        }
    }

    /** 提交修改后的用户信息 */
    @Background
    void modifyUserInfo ()
    {
        if (mUserInfoResp == null ||
                (mNickName.getText().toString().equals(mUserInfoResp.nickname)
                && mGenderTextView.getText().equals(CommonUtils.getGenderString(mUserInfoResp.gender))
                && mEmail.getText().toString().equals(mUserInfoResp.email)))
        {
            finish();
            return;
        }
        ChangeUserInfoRequest request = new ChangeUserInfoRequest();
        request.nickname = mNickName.getText().toString();
        request.email = mEmail.getText().toString();
        request.gender = CommonUtils.getGenderInt(mGenderTextView.getText().toString());

        boolean changeNickName = !mNickName.getText().toString().equals(mUserInfoResp.nickname);
        BaseResponse resp = _restClient.changeUserInfo(mPrefs.userId().get(), request);
        updateModifyUserInfo(resp, changeNickName);
    }

    @UiThread
    void updateModifyUserInfo(BaseResponse resp, boolean changeNickName)
    {
        if (BaseResponse.hasError(resp)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        } else {
            _activityHelper.showToast("修改个人信息成功");
            mPrefs.nickName().put(mNickName.getText().toString());
            setResult(RESULT_NICKNAME_CHANGED);
        }
        finish();
    }





    @Click(R.id.gender_tv)
    void pickGender ()
    {
        LayoutInflater inflater = LayoutInflater.from(UserInfoActivity.this);
        View genderPickerLayout = inflater.inflate(R.layout.gender_picker_layout, null);
        WheelView genderPickerView = (WheelView)genderPickerLayout.findViewById(R.id.gender_picker);

        ArrayWheelAdapter<String> genderAdapter = new ArrayWheelAdapter<String>(this, Consts.GENDER_STRING_ARRAY);
        genderAdapter.setTextSize(20);

        genderPickerView.setViewAdapter(genderAdapter);
        genderPickerView.setCurrentItem(0);
        genderPickerView.setVisibleItems(3);
        genderPickerView.setCyclic(false);

        asd = new ActionDialog(UserInfoActivity.this);
        asd.builder().setCancelable(true).setCanceledOnTouchOutside(true).setPickerView(genderPickerLayout)
                .setAccomplishClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGenderTextView.setText(Consts.GENDER_STRING_ARRAY[asd.getSelectedItemValue()]);
                        asd.dismissDialog();
                    }
                }).show();

    }

}
