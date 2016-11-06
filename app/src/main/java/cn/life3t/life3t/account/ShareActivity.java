package cn.life3t.life3t.account;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import cn.life3t.life3t.ActivityHelper_;
import cn.life3t.life3t.R;
import cn.life3t.life3t.main.BaseActivity;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by wuguohao on 15-5-18.
 */
@EActivity(R.layout.activity_share)
public class ShareActivity extends BaseActivity implements PlatformActionListener {

    private File _shareImage = new File(Environment.getExternalStorageDirectory(), "3tlife.png");

    @AfterViews
    void afterViews() {
        ShareSDK.initSDK(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteTempImage();
        ShareSDK.stopSDK();
    }

    @Click(R.id.shareWechatCircle)
    void shareWithWechatCircle() {
        Platform.ShareParams sp = createSharaParams();
        sp.setShareType(Platform.SHARE_TEXT);
        Platform platform = ShareSDK.getPlatform(WechatMoments.NAME);
        platform.setPlatformActionListener(this);
        platform.share(sp);
    }

    @Click(R.id.shareWechatFriend)
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

    @Click(R.id.shareWeibo)
    void shareWithWeibo() {
        saveTempImage();
        String image = _shareImage.getAbsolutePath();
        Platform.ShareParams sp = createSharaParams();
        sp.setImagePath(image);
        Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
        platform.setPlatformActionListener(this);
        platform.share(sp);
    }

    @Click(R.id.shareQQ)
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
        String content = getString(R.string.userinfo_share_content);
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle(getString(R.string.app_name));
        sp.setTitleUrl(getString(R.string.app_web_site));
        sp.setText(content);
        sp.setSite(getString(R.string.app_name));
        sp.setSiteUrl(getString(R.string.app_web_site));
        sp.setUrl(getString(R.string.app_web_site));
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
