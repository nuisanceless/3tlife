package cn.life3t.life3t.account;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import cn.life3t.life3t.ActivityHelper_;
import cn.life3t.life3t.MyApplication;
import cn.life3t.life3t.MyPrefs_;
import cn.life3t.life3t.R;
import cn.life3t.life3t.common.ClearEditText;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.main.MainActivity;
import cn.life3t.life3t.message.request.LoginRequest;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.LoginResponse;
import cn.life3t.life3t.rest.MyRestClient;
import cn.life3t.life3t.utils.CommonUtils;

/**
 * Created by Lei on 2015/5/12.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
    private static final int REQUEST_CODE_REGISTER = 1;

    String _account;
    String _password;

    @Pref
    MyPrefs_ _pref;

    @ViewById(R.id.account)
    ClearEditText _editAccount;

    @ViewById(R.id.password)
    ClearEditText _editPassword;

    @Click(R.id.btn_custom_tv)
    void onRegister() {
        RegisterActivity_.intent(this).startForResult(REQUEST_CODE_REGISTER);
    }

    @Click(R.id.forget_password)
    void onForgetPassword() {
        ForgetPasswordActivity_.intent(this).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Click(R.id.login)
    void onLogin() {
        if (!checkAccount())
            return;

        if (!checkPassword())
            return;

        loginBackground();
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

    boolean checkPassword() {
        String password = _editPassword.getText().toString();

        if (password.isEmpty()) {
            _activityHelper.showToast(getString(R.string.error_password_empty));
            _editPassword.requestFocus();
            return false;
        }

        _password = password;
        return true;
    }

    @Background
    void loginBackground() {
        _activityHelper.showLoadingDialog(getString(R.string.login_wait));

        LoginRequest request = new LoginRequest();
        request.phone = _account;
        request.password = _password;
        LoginResponse resp = _restClient.login(request);

        _activityHelper.dismissLoadingDialog();
        if (BaseResponse.hasError(resp)) {
            onLoginFail(BaseResponse.getErrorMessage(resp));
        }
        else {
            onLoginSuccess(resp);
        }
    }

    @UiThread
    void onLoginSuccess(LoginResponse response) {
        saveUserInfo(response);
        setResult(RESULT_OK);
        finish();
    }

    void saveUserInfo(LoginResponse response) {
        _pref.userId().put(response.userId);
        _pref.account().put(response.phone);
        _pref.password().put(_password);
        _pref.nickName().put(response.nickName);

        Intent intent = new Intent(MainActivity.BROADCAST_ACTION_LOGIN);
        sendBroadcast(intent);
    }

    @UiThread
    void onLoginFail(String err) {
        _activityHelper.showToast(err);
    }
}
