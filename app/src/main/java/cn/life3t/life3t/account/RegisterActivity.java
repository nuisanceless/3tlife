package cn.life3t.life3t.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.ActivityHelper_;
import cn.life3t.life3t.MyApplication;
import cn.life3t.life3t.R;
import cn.life3t.life3t.common.ClearEditText;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.rest.MyRestClient;
import cn.life3t.life3t.utils.CommonUtils;

/**
 * Created by Lei on 2015/5/12.
 */
@EActivity(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {
    private static final int REQUEST_CODE_SET_PASSWORD = 1;
    private static final int EVENT_RETRY_COUNTER = 1;
    private static final int RETRY_INTERVAL = 60;
    private int _retryTime = RETRY_INTERVAL;

    String _account;
    String _verifyCode;

    @ViewById(R.id.verify_code)
    ClearEditText _editVerifyCode;

    @ViewById(R.id.account)
    ClearEditText _editAccount;

    @ViewById(R.id.agreement)
    CheckBox _checkAgreement;

    @ViewById(R.id.get_verify_code)
    TextView _btnVerifyCode;

    @Click(R.id.user_agreement)
    void onAgreement() {
        EulaActivity_.intent(this).start();
    }

    @Click(R.id.get_verify_code)
    void onGetVerifyCode() {
        if (!checkAccount())
            return;

        disableVerifyCodeButton();
        getVerificationCode(_account);
    }

    @Click(R.id.next)
    void onNext() {
        if (!checkAccount())
            return;

        if (!checkVerifyCode())
            return;

        if (!checkAgreement())
            return;

        verifyCode();
    }

    @OnActivityResult(REQUEST_CODE_SET_PASSWORD)
    void onRegisterResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @AfterViews
    void init() {
        _activityHelper = ActivityHelper_.getInstance_(this);
        _restClient = mApp.restClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(EVENT_RETRY_COUNTER);
    }

    boolean checkAccount() {
        String account = _editAccount.getText().toString();
        if (account.isEmpty()) {
            _activityHelper.showToast(getString(R.string.error_account_empty));
            _editAccount.requestFocus();
            return false;
        }

        boolean isPhoneNumber = CommonUtils.isValidPhoneNumber(account);
        if (!isPhoneNumber) {
            _activityHelper.showToast(getString(R.string.error_account_format));
            _editAccount.requestFocus();
            return false;
        }

        _account = account;
        return true;
    }

    boolean checkVerifyCode() {
        String verifyCode = _editVerifyCode.getText().toString();

        if (verifyCode.isEmpty()) {
            _activityHelper.showToast(getString(R.string.error_verify_code_empty));
            _editVerifyCode.requestFocus();
            return false;
        }

        _verifyCode = verifyCode;
        return true;
    }

    boolean checkAgreement() {
        boolean checked = _checkAgreement.isChecked();
        if (!checked)
            _activityHelper.showToast(getString(R.string.error_agreement));

        return checked;
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == EVENT_RETRY_COUNTER) {
                _retryTime--;
                if (_retryTime == 0) {
                    enableVerifyCodeButton();
                    _retryTime = RETRY_INTERVAL;
                } else {
                    _btnVerifyCode.setText(String.format(getString(R.string.second_counter), _retryTime));
                    mHandler.sendEmptyMessageDelayed(EVENT_RETRY_COUNTER, 1000);
                }
            }
        }
    };

    void disableVerifyCodeButton() {
        _btnVerifyCode.setText(String.format(getString(R.string.second_counter), RETRY_INTERVAL));
        _btnVerifyCode.setEnabled(false);
        mHandler.sendEmptyMessageDelayed(EVENT_RETRY_COUNTER, 1000);
    }

    void enableVerifyCodeButton() {
        mHandler.removeMessages(EVENT_RETRY_COUNTER);
        _btnVerifyCode.setText(R.string.get_verify_code_again);
        _btnVerifyCode.setEnabled(true);
    }

    @Background
    void getVerificationCode(String account) {
        BaseResponse response = _restClient.getRegisterVerifyCode(account);

        if (BaseResponse.hasError(response)) {
            notifyFailed(BaseResponse.getErrorMessage(response));
        }
    }

    @UiThread
    void notifyFailed(String err) {
        _activityHelper.showToast(err);
    }

    @Background
    void verifyCode() {
        BaseResponse response = _restClient.verifyRegisterAuthCode(_verifyCode);
        if (BaseResponse.hasError(response)) {
            notifyFailed(BaseResponse.getErrorMessage(response));
        }
        else {
            verifySuccessful();
        }
    }

    @UiThread
    void verifySuccessful() {
        SetPasswordActivity_.intent(this)._phone(_account)._register(true).startForResult(REQUEST_CODE_SET_PASSWORD);
    }

}
