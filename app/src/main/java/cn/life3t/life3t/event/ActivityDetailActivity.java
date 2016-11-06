package cn.life3t.life3t.event;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import cn.life3t.life3t.MyPrefs_;
import cn.life3t.life3t.R;
import cn.life3t.life3t.account.LoginActivity_;
import cn.life3t.life3t.cleaning.CleaningServiceOrderSubmitActivity_;
import cn.life3t.life3t.common.ActionSheetDialog;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.GetActivityDetailResponse;
import cn.life3t.life3t.message.type.OrderType;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by May on 2015/8/6.
 */

@EActivity(R.layout.activity_current_activity_detail)
public class ActivityDetailActivity extends BaseActivity implements PlatformActionListener
{
    private static final int REQUEST_CODE_LOGIN = 1;

    GetActivityDetailResponse mResponse;
    private File _shareImage = new File(Environment.getExternalStorageDirectory(), "3tlife.png");

    @Pref
    MyPrefs_ mPrefs;

    @Extra
    long activityId;

    @ViewById(R.id.activity_img)
    SimpleDraweeView activityImg;

    @ViewById(R.id.service_type)
    TextView serviceType;

    @ViewById(R.id.introduce)
    TextView introduce;

    @ViewById(R.id.participation)
    TextView participation;

    @ViewById(R.id.activity_time_limit)
    TextView activityTimeLimit;

    @ViewById(R.id.share_title)
    TextView shareTitle;

    @ViewById(R.id.my_invitation_code)
    TextView myInvitationCode;

    @ViewById(R.id.btn_action)
    TextView btnAction;

    @Click(R.id.btn_action)
    void onAction() {
        if (mResponse != null) {
            switch (mResponse.action) {
                case GetActivityDetailResponse.ACTION_ORDER:
                    onOrderImmediately();
                    break;
                case GetActivityDetailResponse.ACTION_INVITE:
                    onInvite();
                    break;
            }

        }
    }

    private void onOrderImmediately() {
        CleaningServiceOrderSubmitActivity_.intent(this)._serviceType((int) mResponse.businessId).start();
    }

    private void onInvite() {
        ActionSheetDialog dialog = new ActionSheetDialog(this);
        View shareView = LayoutInflater.from(this).inflate(R.layout.view_share, null);
        View wechatCircle = shareView.findViewById(R.id.shareWechatCircle);
        wechatCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWithWechatCircle();
            }
        });
        View wechatFriend = shareView.findViewById(R.id.shareWechatFriend);
        wechatFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWithWechatFriend();
            }
        });
        View weibo = shareView.findViewById(R.id.shareWeibo);
        weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWithWeibo();
            }
        });
        View qq = shareView.findViewById(R.id.shareQQ);
        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWithQQ();
            }
        });
        dialog.setCustomView(shareView);
        dialog.show();
        dialog.setOperationViewVisible(false);
    }



    @AfterViews
    void init() {
        ShareSDK.initSDK(this);
        if (mPrefs.userId().get() == -1) {
            LoginActivity_.intent(this).startForResult(REQUEST_CODE_LOGIN);
        }
        else {
            getActivityDetailInfo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN) {
            if (resultCode == RESULT_OK)
                getActivityDetailInfo();
            else
                finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        deleteTempImage();
        ShareSDK.stopSDK();
    }

    @Background
    void getActivityDetailInfo() {
        GetActivityDetailResponse resp = _restClient.getActivityDetail(activityId);
        mResponse = resp;

        getDataResult(resp);
    }

    @UiThread
    void getDataResult(GetActivityDetailResponse resp)
    {
        if (BaseResponse.hasError(resp)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else
        {
            activityImg.setImageURI(Uri.parse(resp.detailImageUrl));
            serviceType.setText(OrderType.getOrderTypeStringRes((int) resp.businessId));
            introduce.setText(resp.introduce);
            participation.setText(resp.participation);
            activityTimeLimit.setText(resp.startDate + " - " + resp.endDate);
            if (mResponse.action == GetActivityDetailResponse.ACTION_INVITE) {
                shareTitle.setVisibility(View.VISIBLE);
                myInvitationCode.setVisibility(View.VISIBLE);
                myInvitationCode.setText(mResponse.code);
                btnAction.setText(R.string.invite_friend);
            }
        }

    }

    void shareWithWechatCircle() {
        Platform.ShareParams sp = createSharaParams();
        sp.setShareType(Platform.SHARE_TEXT);
        Platform platform = ShareSDK.getPlatform(WechatMoments.NAME);
        platform.setPlatformActionListener(this);
        platform.share(sp);
    }

    void shareWithWechatFriend() {
        saveTempImage();
        String image = _shareImage.getAbsolutePath();
        Platform.ShareParams sp = createSharaParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setImagePath(image);
        Platform platform = ShareSDK.getPlatform(Wechat.NAME);
        platform.setPlatformActionListener(this);
        platform.share(sp);
    }

    void shareWithWeibo() {
        saveTempImage();
        String image = _shareImage.getAbsolutePath();
        Platform.ShareParams sp = createSharaParams();
        sp.setImagePath(image);
        Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
        platform.setPlatformActionListener(this);
        platform.share(sp);
    }

    void shareWithQQ() {
        saveTempImage();
        String image = _shareImage.getAbsolutePath();
        Platform.ShareParams sp = createSharaParams();
        sp.setImagePath(image);
        Platform platform = ShareSDK.getPlatform(QQ.NAME);
        platform.setPlatformActionListener(this);
        platform.share(sp);
    }

    private Platform.ShareParams createSharaParams() {
        String content = mResponse.shareContent;
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle(getString(R.string.app_name));
        sp.setTitleUrl(mResponse.url);
        sp.setText(content);
        sp.setSite(getString(R.string.app_name));
        sp.setSiteUrl(mResponse.url);
        sp.setUrl(mResponse.url);
        return sp;
    }

    void saveTempImage() {
        if (_shareImage.exists())
            return;
        try {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            // Bitmap转byte数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);//png类型
            FileOutputStream out = new FileOutputStream(_shareImage);
            out.write(baos.toByteArray());
            out.flush();
            out.close();
        } catch (Exception e) {

        }
    }

    void deleteTempImage() {
        if (_shareImage.exists()) {
            _shareImage.delete();
        }
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
        _activityHelper.showToast(getString(R.string.share_completed));
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        _activityHelper.showToast(getString(R.string.share_failed));
    }

    @Override
    public void onCancel(Platform platform, int i) {
        _activityHelper.showToast(getString(R.string.share_canceled));
    }
}
