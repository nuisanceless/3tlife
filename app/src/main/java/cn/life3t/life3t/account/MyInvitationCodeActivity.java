package cn.life3t.life3t.account;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import cn.life3t.life3t.R;
import cn.life3t.life3t.common.ActionSheetDialog;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.GetInviteCodeResponse;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by Lei on 2015/8/25.
 */
@EActivity(R.layout.activity_my_invitation_code)
public class MyInvitationCodeActivity extends BaseActivity implements PlatformActionListener {
    private File _shareImage = new File(Environment.getExternalStorageDirectory(), "3tlife.png");
    private GetInviteCodeResponse mResponse;

    @ViewById(R.id.my_invitation_code)
    TextView mInvitationCodeView;

    @ViewById(R.id.content)
    TextView mInvitationContentView;

    @Click(R.id.share)
    void onShare() {
        if (mResponse == null)
            return;
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
    void init ()
    {
        ShareSDK.initSDK(this);
        getMyInvitationCode();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        deleteTempImage();
        ShareSDK.stopSDK();
    }

    @Background
    void getMyInvitationCode() {
        _activityHelper.showLoadingDialog(null);
        GetInviteCodeResponse response = _restClient.getMyInvitationCode();
        _activityHelper.dismissLoadingDialog();
        afterGetMyInvitationCode(response);
    }

    @UiThread
    void afterGetMyInvitationCode(GetInviteCodeResponse response) {
        if (BaseResponse.hasError(response)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else {
            mResponse = response;
            mInvitationContentView.setText(mResponse.description);
            mInvitationCodeView.setText(mResponse.code);
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
