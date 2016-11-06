package cn.life3t.life3t.account;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import cn.life3t.life3t.MyPrefs_;
import cn.life3t.life3t.R;
import cn.life3t.life3t.common.ClearEditText;
import cn.life3t.life3t.common.CommonDialog;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.request.ChangePasswordRequest;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.utils.Consts;

/**
 * Created by wuguohao on 15-5-21.
 */
@EActivity(R.layout.activity_modify_password)
public class ModifyPasswordActivity extends BaseActivity
{
    private static final int RETRY_INTERVAL = 60;
    private static final int EVENT_RETRY_COUNTER = 1;
    private int mRetryTime = RETRY_INTERVAL;

    /** 用户账号，电话号码，用来获取修改密码时的验证码 */
    String mVerifyCodeStr;
    String mPassword;

    @Pref
    MyPrefs_  _prefs;

    @ViewById(R.id.new_password)
    ClearEditText mNewPd;

    @ViewById(R.id.confirm_new_password)
    ClearEditText mConfirmNewPd;

    @ViewById(R.id.verify_code)
    ClearEditText mVerifyCode;

    @ViewById(R.id.get_verify_code)
    TextView mBtnVerifyCode;

    @Click(R.id.get_verify_code)
    void onGetVerifyCode ()
    {
        if (!checkPassword())
            return;
        disableVerifyCodeButton();
        getVerificationCode();
    }

    @Click(R.id.affirm)
    void onAffirm ()
    {
        if (!checkInput())
            return;

        final CommonDialog dialog = new CommonDialog(this)
                .builder()
                .setContent(getString(R.string.confirm_to_modify_password))
                .setCancelString(getString(R.string.cancel))
                .setConfirmString(getString(R.string.confirm));
        dialog.setOnCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        dialog.setOnConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePassword();
                    dialog.dismiss();
                }
            });
        dialog.show();
    }

    boolean checkPassword() {
        String newPd = mNewPd.getText().toString();
        String confirmNewPd = mConfirmNewPd.getText().toString();

        if (newPd.isEmpty())
        {
            _activityHelper.showToast(getString(R.string.error_password_empty));
            mNewPd.requestFocus();
            return false;
        }
        if (newPd.length() < Consts.PASSWORD_LENGTH_MIN || newPd.length() > Consts.PASSWORD_LENGTH_MAX) {
            _activityHelper.showToast(getString(R.string.error_password_length));
            mNewPd.requestFocus();
            return false;
        }
        if (confirmNewPd.isEmpty())
        {
            _activityHelper.showToast(getString(R.string.error_confirm_password_empty));
            mConfirmNewPd.requestFocus();
            return false;
        }
        if (!newPd.equals(confirmNewPd))
        {
            _activityHelper.showToast(getString(R.string.error_password_not_match));
            mConfirmNewPd.requestFocus();
            return false;
        }


        mPassword = newPd;

        return true;
    }

    /** 检查输入 */
    boolean checkInput()
    {
        if (!checkPassword())
            return false;

        String verifyCode = mVerifyCode.getText().toString();

        if (verifyCode.isEmpty())
        {
            _activityHelper.showToast(getString(R.string.hint_verify_code));
            mVerifyCode.requestFocus();
            return false;
        }

        mVerifyCodeStr = verifyCode;

        return true;
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == EVENT_RETRY_COUNTER)
            {
                mRetryTime--;
                if (mRetryTime == 0)
                {
                    enableVerifyCodeButton();
                    mRetryTime = RETRY_INTERVAL;
                }
                else
                {
                    mBtnVerifyCode.setText(String.format(getString(R.string.second_counter), mRetryTime));
                    mHandler.sendEmptyMessageDelayed(EVENT_RETRY_COUNTER, 1000);
                }
            }
        }
    };

    void disableVerifyCodeButton ()
    {
        mBtnVerifyCode.setText(String.format(getString(R.string.second_counter), RETRY_INTERVAL));
        mBtnVerifyCode.setEnabled(false);
        mHandler.sendEmptyMessageDelayed(EVENT_RETRY_COUNTER, 1000);
    }

    void enableVerifyCodeButton() {
        mHandler.removeMessages(EVENT_RETRY_COUNTER);
        mBtnVerifyCode.setText(R.string.get_verify_code_again);
        mBtnVerifyCode.setEnabled(true);
    }

    @Background
    void getVerificationCode() {
        BaseResponse response = _restClient.getChangePasswordVerifyCode();

        if (BaseResponse.hasError(response)) {
            notifyFailed(BaseResponse.getErrorMessage(response));
        }
    }

    @UiThread
    void notifyFailed(String err) {
        _activityHelper.showToast(err);
    }

    @Background
    void changePassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.code = mVerifyCodeStr;
        request.password = mPassword;
        BaseResponse response = _restClient.changePassword(_prefs.userId().get(), request);
        changePasswordResult(response);
    }

    @UiThread
    void changePasswordResult(BaseResponse response) {
        if (BaseResponse.hasError(response)) {
            notifyFailed(BaseResponse.getErrorMessage(response));
        }
        else {
            _prefs.password().put(mPassword);
            setResult(RESULT_OK);
            finish();
        }
    }
}
