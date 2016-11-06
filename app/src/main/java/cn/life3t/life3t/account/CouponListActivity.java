package cn.life3t.life3t.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.common.ActionSheetDialog;
import cn.life3t.life3t.common.ClearEditText;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.Coupon;
import cn.life3t.life3t.message.request.ExchangeCouponRequest;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.GetCouponListResponse;
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
@EActivity(R.layout.activity_coupon_list)
public class CouponListActivity extends BaseActivity implements PlatformActionListener {

    private File _shareImage = new File(Environment.getExternalStorageDirectory(), "3tlife.png");
    private GetInviteCodeResponse mResponse;

    MyAdapter mAdapter = new MyAdapter();
    List<Coupon> mDataList;

    @Extra
    int mOrderId = -1;

    @ViewById(R.id.code)
    ClearEditText mCodeView;

    @ViewById(R.id.my_invitation_code)
    TextView mMyInvitationCodeView;

    @ViewById(R.id.list)
    ListView mListView;

    @Click(R.id.exchange)
    void onExchange() {
        String code = mCodeView.getText().toString();
        if (code.isEmpty()) {
            _activityHelper.showToast(getString(R.string.hint_input_coupon));
            return;
        }

        exchange(code);
    }

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

        if (mOrderId != -1) {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Coupon coupon = mDataList.get(position);
                    if (!coupon.isExpired) {
                        Intent intent = new Intent();
                        intent.putExtra("coupon", coupon.id);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
        }

        mListView.setAdapter(mAdapter);
        getCouponList();
        getMyInvitationCode();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        deleteTempImage();
        ShareSDK.stopSDK();
    }

    @Background
    void exchange(String code) {
        _activityHelper.showLoadingDialog(null);
        ExchangeCouponRequest request = new ExchangeCouponRequest();
        request.code = code;
        BaseResponse response = _restClient.exchangeCoupon(request);
        _activityHelper.dismissLoadingDialog();
        afterExchange(response);
    }

    @UiThread
    void afterExchange(BaseResponse response) {
        if (BaseResponse.hasError(response)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else {
            _activityHelper.showToast(getString(R.string.coupon_exchange_success));
            getCouponList();
        }
    }

    @Background
    void getCouponList() {
        _activityHelper.showLoadingDialog(null);
        GetCouponListResponse response = null;
        if (mOrderId == -1) {
            response = _restClient.getCouponList();
        }
        else {
            response = _restClient.getOrderCouponList(mOrderId);
        }
        _activityHelper.dismissLoadingDialog();
        afterGetCouponList(response);
    }

    @UiThread
    void afterGetCouponList(GetCouponListResponse response) {
        if (BaseResponse.hasError(response)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else {
            mDataList = response.list;
            mAdapter.notifyDataSetChanged();
        }
    }

    @Background
    void getMyInvitationCode() {
        GetInviteCodeResponse response = _restClient.getMyInvitationCode();
        afterGetMyInvitationCode(response);
    }

    @UiThread
    void afterGetMyInvitationCode(GetInviteCodeResponse response) {
        if (BaseResponse.hasError(response)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else {
            mResponse = response;
            mMyInvitationCodeView.setText(mResponse.code);
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


    public class MyAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return mDataList == null ? 0 : mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList == null ? null : mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(CouponListActivity.this).inflate(R.layout.list_item_coupon, null);

                holder.value   = (TextView)convertView.findViewById(R.id.value);
                holder.type = (TextView)convertView.findViewById(R.id.type);
                holder.limit     = (TextView)convertView.findViewById(R.id.limit);
                holder.left       = (TextView)convertView.findViewById(R.id.left);
                holder.expire   = (TextView)convertView.findViewById(R.id.expire_date);
                holder.divider = (ImageView)convertView.findViewById(R.id.divider);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }

            Coupon item = mDataList.get(position);
            String value = String.format(getString(R.string.coupon_value), item.price/100);
            holder.value.setText(value);
            holder.type.setText(item.name);
            holder.limit.setText("限手机客户端使用");
            String expire_date = String.format(getString(R.string.coupon_expire_date), item.startDate, item.expiredDate);
            holder.expire.setText(expire_date);
            if (item.isExpired || item.isUsed) {
                int color = getResources().getColor(R.color.light_gray);
                if (item.isExpired) {
                    holder.left.setText(R.string.coupon_expired);
                }
                else {
                    holder.left.setText(R.string.coupon_used);
                }
                holder.left.setTextColor(color);
                holder.value.setBackgroundColor(Color.TRANSPARENT);
                holder.expire.setTextColor(color);
                holder.value.setTextColor(color);
                holder.type.setTextColor(color);
                holder.limit.setTextColor(color);
                holder.divider.setVisibility(View.VISIBLE);
            }
            else {
                holder.left.setText(String.format(getString(R.string.coupon_expire_left), item.expiredDays));
                int dark_gray = getResources().getColor(R.color.dark_gray);
                int gray = getResources().getColor(R.color.gray);

                holder.value.setTextColor(Color.WHITE);
                holder.value.setBackgroundColor(getResources().getColor(R.color.coupon_red));
                holder.type.setTextColor(dark_gray);
                holder.limit.setTextColor(dark_gray);
                holder.left.setTextColor(gray);
                holder.expire.setTextColor(gray);
                holder.divider.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        private final class ViewHolder
        {
            public TextView value;
            public TextView type;
            public TextView limit;
            public TextView left;
            public TextView expire;
            public ImageView divider;
        }

    }
}
