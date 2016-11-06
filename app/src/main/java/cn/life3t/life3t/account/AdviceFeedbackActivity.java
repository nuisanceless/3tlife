package cn.life3t.life3t.account;

import android.app.Activity;
import android.widget.EditText;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.request.FeedbackRequest;
import cn.life3t.life3t.message.response.BaseResponse;

/**
 * Created by wuguohao on 15-5-18.
 */
@EActivity(R.layout.activity_advice_feedback)
public class AdviceFeedbackActivity extends BaseActivity
{
    @ViewById(R.id.content)
    EditText mContent;

    @Click(R.id.submit)
    void onSubmit ()
    {
        String content = mContent.getText().toString();
        if (content == null || content.isEmpty())
        {
            _activityHelper.showToast(getString(R.string.error_feedback_empty));
            return;
        }

        sendFeedback(content);
    }

    @Background
    void sendFeedback(String feedback)
    {
        _activityHelper.showLoadingDialog("");
        FeedbackRequest request = new FeedbackRequest();
        request.remark = feedback;
        BaseResponse response = mApp.restClient().sendFeedback(request);
        _activityHelper.dismissLoadingDialog();
        sendFeedbackResult(response);
    }

    @UiThread
    void sendFeedbackResult(BaseResponse response) {
        if(BaseResponse.hasError(response))
        {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else
        {
            _activityHelper.showToast(getString(R.string.submit_success));
            finish();
        }
    }
}
