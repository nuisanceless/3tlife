package cn.life3t.life3t.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewpagerindicator.IconPageIndicator;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import cn.life3t.life3t.MyPrefs_;
import cn.life3t.life3t.R;
import cn.life3t.life3t.account.AboutUsActivity_;
import cn.life3t.life3t.account.AddressManagementActivity_;
import cn.life3t.life3t.account.AdviceFeedbackActivity_;
import cn.life3t.life3t.account.CouponListActivity_;
import cn.life3t.life3t.account.LoginActivity_;
import cn.life3t.life3t.account.MyInvitationCodeActivity_;
import cn.life3t.life3t.account.OrderManagementActivity_;
import cn.life3t.life3t.account.QuestionActivity_;
import cn.life3t.life3t.account.ShareActivity_;
import cn.life3t.life3t.account.UserInfoActivity;
import cn.life3t.life3t.account.UserInfoActivity_;
import cn.life3t.life3t.cleaning.CleaningServiceActivity;
import cn.life3t.life3t.cleaning.CleaningServiceInfoActivity_;
import cn.life3t.life3t.cleaning.CleaningServiceOrderSubmitActivity_;
import cn.life3t.life3t.common.CommonDialog;
import cn.life3t.life3t.common.HorizontalListView;
import cn.life3t.life3t.event.CurrentEventActivity_;
import cn.life3t.life3t.common.CommonTitle;
import cn.life3t.life3t.message.request.CheckUpdateRequest;
import cn.life3t.life3t.message.request.LoginRequest;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.CheckUpdateResponse;
import cn.life3t.life3t.message.response.GetActivityListResponse;
import cn.life3t.life3t.message.response.LoginResponse;
import cn.life3t.life3t.message.type.OrderType;
import cn.life3t.life3t.message.type.ServiceType;
import cn.life3t.life3t.order.StaffAvailableTimeDialog;
import cn.life3t.life3t.rest.MyRestClient;
import cn.life3t.life3t.utils.CommonUtils;
import cn.life3t.life3t.utils.Consts;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;


@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity/* implements OnGestureListener, View.OnTouchListener*/
{
    public static final String BROADCAST_ACTION_LOGIN = "BROADCAST_ACTION_LOGIN";
    private static final String EVENT_LIST_CALL_ACTIVITY = "EVENT_LIST_CALL_ACTIVITY";
    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_USERINFO = 2;

    private static final int PENDING_INTENT_NONE = 0;
    private static final int PENDING_INTENT_ORDER_NORMAL = 1;
    private static final int PENDING_INTENT_ORDER_DEEP = 2;
    private static final int PENDING_INTENT_ORDER_SOFA = 3;
    private static final int PENDING_INTENT_ORDER_FLOOR= 4;
    private static final int PENDING_INTENT_ORDER_HOOD = 5;
    private static final int PENDING_INTENT_USER_INFO = 6;
    private static final int PENDING_INTENT_ORDER_MANAGEMENT = 7;
    private static final int PENDING_INTENT_ADDRESS_MANAGEMENT = 8;
    private static final int PENDING_INTENT_FEEDBACK = 9;
    private static final int PENDING_INTENT_COUPON = 10;
    private static final int PENDING_INVITATION_CODE = 11;

    private int _pendingIntent = PENDING_INTENT_NONE;

    private long mEnterBackgroundTime = 0;
    private long mCheckUpdateDuration = 24*60*60*1000; //24 hours


//    GestureDetector mGestureDetector;
    MenuDrawer mDrawer;
    boolean mIsLogin = false;
    boolean mHasActivities = false;
    LoginBroadcastReciver mReceiver = new LoginBroadcastReciver();

    @Pref
    MyPrefs_ mPrefs;

    @ViewById(R.id.view_pager)
    AutoScrollViewPager mViewPager;

    @ViewById(R.id.indicator)
    IconPageIndicator mIndicator;

    TextView mNickName;


    @Click(R.id.btn_custom)
    void onUserInfo() {
        mDrawer.toggleMenu();
    }

    @Click(R.id.normal_clean)
    void onNormalCleaningService() {
        if (checkLogin()) {
            CleaningServiceOrderSubmitActivity_.intent(this)._serviceType(OrderType.NORMAL).start();
        }
        else {
            _pendingIntent = PENDING_INTENT_ORDER_NORMAL;
        }
    }

    @Click(R.id.deep_clean)
    void onDeepCleaningService() {
        if (checkLogin()) {
            CleaningServiceOrderSubmitActivity_.intent(this)._serviceType(OrderType.DEEP).start();
        }
        else {
            _pendingIntent = PENDING_INTENT_ORDER_DEEP;
        }
    }

    @Click(R.id.sofa_clean)
    void onSofaCleaningService() {
        if (checkLogin()) {
            CleaningServiceOrderSubmitActivity_.intent(this)._serviceType(OrderType.SOFA).start();
        }
        else {
            _pendingIntent = PENDING_INTENT_ORDER_SOFA;
        }
    }

    @Click(R.id.floor_clean)
    void onFloorCleaningService() {
        if (checkLogin()) {
            CleaningServiceOrderSubmitActivity_.intent(this)._serviceType(OrderType.FLOOR).start();
        }
        else {
            _pendingIntent = PENDING_INTENT_ORDER_FLOOR;
        }
    }

    @Click(R.id.rangehood_clean)
    void onHoodCleaningService() {
        if (checkLogin()) {
            CleaningServiceOrderSubmitActivity_.intent(this)._serviceType(OrderType.HOOD).start();
        }
        else {
            _pendingIntent = PENDING_INTENT_ORDER_HOOD;
        }
    }

    @Click(R.id.order_management)
    void onOrderManagement() {
        if (checkLogin())
            OrderManagementActivity_.intent(this).start();
        else {
            _pendingIntent = PENDING_INTENT_ORDER_MANAGEMENT;
        }
    }

    @Click(R.id.questions)
    void onQuestions() {
        QuestionActivity_.intent(this).start();
    }

    @Click(R.id.feedback)
    void onFeedback() {
        if (checkLogin())
            AdviceFeedbackActivity_.intent(this).start();
        else {
            _pendingIntent = PENDING_INTENT_FEEDBACK;
        }
    }

    @Click(R.id.coupon)
    void onCoupon() {
        if (checkLogin())
            CouponListActivity_.intent(this).start();
        else {
            _pendingIntent = PENDING_INTENT_COUPON;
        }
    }

//    @Click(R.id.clean_service_info)
//    void onCleanServiceInfo() {
//        CleaningServiceInfoActivity_.intent(this).start();
//    }
//
//    @Click(R.id.event)
//    void onEvent() {
//        CurrentEventActivity_.intent(this).start();
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @AfterViews
    void init ()
    {
//        mGestureDetector = new GestureDetector(this, this);
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION_LOGIN);
        registerReceiver(mReceiver, intentFilter);

        // 初始化侧边栏
        mDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY, Position.RIGHT, MenuDrawer.MENU_DRAG_WINDOW);
        mDrawer.setMenuView(R.layout.drawer_portrait);
        mDrawer.setMenuSize(CommonUtils.getScreenWidth(this) * 760 / 1080);
        mDrawer.setDropShadowEnabled(true); // 边阴影
        mDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_BEZEL);
        mDrawer.setDrawOverlay(false); // 覆盖在上面的阴影
        ((ViewGroup)mDrawer.getMenuView().getParent()).setBackgroundColor(0);

        View headImage = findViewById(R.id.head_image);
        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

        View menuUserInfo = findViewById(R.id.item_user_info);
        menuUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin())
                    UserInfoActivity_.intent(MainActivity.this).startForResult(REQUEST_CODE_USERINFO);
                else {
                    _pendingIntent = PENDING_INTENT_USER_INFO;
                }
            }
        });

//        View menuOrderManagement = findViewById(R.id.item_order_management);
//        menuOrderManagement.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (checkLogin())
//                    OrderManagementActivity_.intent(MainActivity.this).start();
//                else {
//                    _pendingIntent = PENDING_INTENT_ORDER_MANAGEMENT;
//                }
//            }
//        });

        View menuAddressManagement = findViewById(R.id.item_address_management);
        menuAddressManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin())
                    AddressManagementActivity_.intent(MainActivity.this).start();
                else {
                    _pendingIntent = PENDING_INTENT_ADDRESS_MANAGEMENT;
                }
            }
        });

        View menuShare = findViewById(R.id.item_share);
        menuShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareActivity_.intent(MainActivity.this).start();
            }
        });

//        View menuAdviceFeedback = findViewById(R.id.item_advice_feedback);
//        menuAdviceFeedback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (checkLogin())
//                    AdviceFeedbackActivity_.intent(MainActivity.this).start();
//                else {
//                    _pendingIntent = PENDING_INTENT_FEEDBACK;
//                }
//            }
//        });

        View menuInvitationCode = findViewById(R.id.item_invitation_code);
        menuInvitationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin())
                    MyInvitationCodeActivity_.intent(MainActivity.this).start();
                else {
                    _pendingIntent = PENDING_INVITATION_CODE;
                }
            }
        });

        View menuAboutUs = findViewById(R.id.item_about_us);
        menuAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUsActivity_.intent(MainActivity.this).start();
            }
        });

        mNickName = (TextView) findViewById(R.id.account_name);

        autoLogin();
    }

    private void showPhoneCallDialog(String serviceName, String phone) {
        final String phone_number = phone;
        final CommonDialog dialog = new CommonDialog(MainActivity.this)
                .builder()
                .setContent(String.format(getString(R.string.call_text), serviceName))
                .setBlueContent(phone_number)
                .setConfirmString(getString(R.string.call));
        dialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone_number));
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    boolean checkLogin() {
        if (!mIsLogin) {
            login();
        }
        return mIsLogin;
    }

    boolean loginOnce() {
        if (mPrefs.userId().get() == -1)
            return false;

        return true;
    }

    void login() {
        LoginActivity_.intent(this).startForResult(REQUEST_CODE_LOGIN);
    }

    @Background
    void autoLogin() {
        if (loginOnce()) {
            LoginRequest request = new LoginRequest();
            request.phone = mPrefs.account().get();
            request.password = mPrefs.password().get();
            LoginResponse response = _restClient.login(request);
            if (BaseResponse.hasError(response)) {
                mIsLogin = false;
                mPrefs.userId().put(-1);
            }
            else {
                updateUserInfo();
            }
        }
    }

    @UiThread
    void updateUserInfo() {
        mIsLogin = true;
        String nickName = mPrefs.nickName().get();
        mNickName.setText(nickName);
    }

    private void logout() {
        mIsLogin = false;
        mNickName.setText(R.string.not_login);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
            updateUserInfo();
            switch (_pendingIntent) {
                case PENDING_INTENT_ORDER_NORMAL:
                    CleaningServiceOrderSubmitActivity_.intent(this)._serviceType(OrderType.NORMAL).start();
                    break;
                case PENDING_INTENT_ORDER_DEEP:
                    CleaningServiceOrderSubmitActivity_.intent(this)._serviceType(OrderType.DEEP).start();
                    break;
                case PENDING_INTENT_ORDER_SOFA:
                    CleaningServiceOrderSubmitActivity_.intent(this)._serviceType(OrderType.SOFA).start();
                    break;
                case PENDING_INTENT_ORDER_FLOOR:
                    CleaningServiceOrderSubmitActivity_.intent(this)._serviceType(OrderType.FLOOR).start();
                    break;
                case PENDING_INTENT_ORDER_HOOD:
                    CleaningServiceOrderSubmitActivity_.intent(this)._serviceType(OrderType.HOOD).start();
                    break;
                case PENDING_INTENT_USER_INFO:
                    UserInfoActivity_.intent(this).startForResult(REQUEST_CODE_USERINFO);
                    break;
                case PENDING_INTENT_ORDER_MANAGEMENT:
                    OrderManagementActivity_.intent(this).start();
                    break;
                case PENDING_INTENT_ADDRESS_MANAGEMENT:
                    AddressManagementActivity_.intent(this).start();
                    break;
                case PENDING_INTENT_FEEDBACK:
                    AdviceFeedbackActivity_.intent(this).start();
                    break;
                case PENDING_INTENT_COUPON:
                    CouponListActivity_.intent(this).start();
                    break;
                case PENDING_INVITATION_CODE:
                    MyInvitationCodeActivity_.intent(this).start();
                    break;
                default:
                    break;
            }
        }
        else if (requestCode == REQUEST_CODE_USERINFO) {
            if (resultCode == UserInfoActivity.RESULT_LOGOUT)
                logout();
            else if (resultCode == UserInfoActivity.RESULT_NICKNAME_CHANGED) {
                String nickName = mPrefs.nickName().get();
                mNickName.setText(nickName);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible())
        {
            mDrawer.closeMenu(true);
            return;
        }
        else
            super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(System.currentTimeMillis() - mEnterBackgroundTime > mCheckUpdateDuration) {
            checkUpdate();
        }
//        if (needActionShow())
        {
            getActionList();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mEnterBackgroundTime = System.currentTimeMillis();
    }



    boolean needActionShow ()
    {
        if (mPrefs.lastActionShowTime().get() == -1 || mPrefs.lastActionShowTime().get() > System.currentTimeMillis())
        {
            return true;
        }
        if (CommonUtils.isBeforToday(mPrefs.lastActionShowTime().get()))
        {
            return true;
        }
        return false;
    }

    @Background
    void getActionList ()
    {
        GetActivityListResponse resp = _restClient.getActivityList();
        updateGetActionList(resp);
    }

    @UiThread
    void updateGetActionList (GetActivityListResponse resp)
    {
        if (BaseResponse.hasError(resp))
        {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else
        {
            if (resp.list.size() > 0) {
//                ActionViewPagerDialog dialog = new ActionViewPagerDialog(MainActivity.this, resp);
//                dialog.show();
//                mPrefs.lastActionShowTime().put(System.currentTimeMillis());
                List<ActionImagePagerAdapter.ItemData> dataList = new ArrayList<ActionImagePagerAdapter.ItemData>();
                for (int i = 0; i < resp.list.size(); i++){
                    ActionImagePagerAdapter.ItemData data = new ActionImagePagerAdapter.ItemData();
                    GetActivityListResponse.ActivityListItem activityItem = resp.list.get(i);
                    data.id = activityItem.id;
                    data.mImageUrl = activityItem.listImageUrl;
                    dataList.add(data);
                }

                ActionImagePagerAdapter pagerAdapter = new ActionImagePagerAdapter(this, dataList);
                mViewPager.setAdapter(pagerAdapter);
                mIndicator.setViewPager(mViewPager);
                if (dataList.size() > 1) {
                    mIndicator.setVisibility(View.VISIBLE);
                    mIndicator.notifyDataSetChanged();

                    mViewPager.setCycle(true);
                    mViewPager.setInterval(5000);
                    mViewPager.startAutoScroll();

                    mHasActivities = true;
                }
                else {
                    mIndicator.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHasActivities)
            mViewPager.startAutoScroll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHasActivities)
            mViewPager.stopAutoScroll();
    }

    @Background
    void checkUpdate() {
        CheckUpdateRequest request = new CheckUpdateRequest();
        request.appName = "3tlife";
        request.version = mApp.getVersion();
        CheckUpdateResponse response = mApp.restClient().checkUpdate(request);
        if (!BaseResponse.hasError(response)) {
            if (response.HasUpdate && response.url != null) {
                hasUpdate(response.NewVersion, response.url);
            }
        }
    }

    @UiThread
    void hasUpdate(String version, final String url) {
        String content = String.format(getString(R.string.update_new_version), version);
        final CommonDialog dialog = new CommonDialog(MainActivity.this)
                .builder().setContent(content);
        dialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(it);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class LoginBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateUserInfo();
        }
    }

/*
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        System.out.println("onDown");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        System.out.println("onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        System.out.println("onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        System.out.println("onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (velocityY > 0)
            return false;
        if (Math.abs(velocityY) > Math.abs(velocityX))
        {
            Intent intent = new Intent(this, CurrentEventActivity_.class);
            intent.putExtra(EVENT_LIST_CALL_ACTIVITY, "MainActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_from_top);
        }
        return true;
    }
*/

}
