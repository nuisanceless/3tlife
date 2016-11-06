package cn.life3t.life3t.account;

import android.app.Activity;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import cn.life3t.life3t.ActivityHelper_;
import cn.life3t.life3t.MyApplication;
import cn.life3t.life3t.MyPrefs_;
import cn.life3t.life3t.R;
import cn.life3t.life3t.common.ClearEditText;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.LoginResponse;
import cn.life3t.life3t.rest.MyRestClient;
import cn.life3t.life3t.utils.Consts;

/**
 * Created by Lei on 2015/5/14.
 */
@EActivity(R.layout.activity_set_password)
public class SetPasswordActivity extends BaseActivity {
    String _password;

    @Pref
    MyPrefs_ _pref;

    @Extra
    String _phone;

    @Extra
    boolean _register;

    @ViewById(R.id.password)
    ClearEditText _editPassword;

    @ViewById(R.id.confirm_password)
    ClearEditText _editConfirmPassword;

    @Click(R.id.confirm)
    void onRegister() {
        if (!checkPassword())
            return;

        if (_register)
            register();
        else
            setPassword();
    }

    boolean checkPassword() {
        String password = _editPassword.getText().toString();
        if (password.isEmpty()) {
            _activityHelper.showToast(getString(R.string.error_password_empty));
            _editPassword.requestFocus();
            return false;
        }
        if (password.length() < Consts.PASSWORD_LENGTH_MIN || password.length() > Consts.PASSWORD_LENGTH_MAX) {
            _activityHelper.showToast(getString(R.string.error_password_length));
            _editPassword.requestFocus();
            return false;
        }

        String confirmPassword = _editConfirmPassword.getText().toString();
        if (confirmPassword.isEmpty()) {
            _activityHelper.showToast(getString(R.string.error_confirm_password_empty));
            _editConfirmPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            _activityHelper.showToast(getString(R.string.error_password_not_match));
            _editConfirmPassword.requestFocus();
            return false;
        }


        return true;
    }

    @Background
    void setPassword() {
        String password = _editPassword.getText().toString();
        BaseResponse response = _restClient.setPassword(password);
        if (BaseResponse.hasError(response)) {
            onFailed(BaseResponse.getErrorMessage(response));
        } else {
            _password = password;
            onSetPasswordSuccessful();
        }
    }

    @Background
    void register() {
        String password = _editPassword.getText().toString();
        LoginResponse response = _restClient.register(password);
        if (BaseResponse.hasError(response)) {
            onFailed(BaseResponse.getErrorMessage(response));
        } else {
            _password = password;
            onRegisterSuccessful(response);
        }
    }

    @UiThread
    void onFailed(String err) {
        _activityHelper.showToast(err);
    }

    @UiThread
    void onRegisterSuccessful(LoginResponse response) {
        saveUserInfo(response);
        setResult(RESULT_OK);
        finish();
    }

    @UiThread
    void onSetPasswordSuccessful() {
        _pref.password().put(_password);
        setResult(RESULT_OK);
        finish();
    }

    void saveUserInfo(LoginResponse response) {
        _pref.userId().put(response.userId);
        _pref.account().put(response.phone);
        _pref.password().put(_password);
        _pref.nickName().put(response.nickName);
    }
}
