package cn.life3t.life3t.order;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.account.CouponListActivity_;
import cn.life3t.life3t.common.CommonDialog;
import cn.life3t.life3t.common.InfoItemView;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.StaffDetail;
import cn.life3t.life3t.message.request.SetCouponRequest;
import cn.life3t.life3t.message.response.GetAlipayResponse;
import cn.life3t.life3t.message.type.OrderState;
import cn.life3t.life3t.message.type.OrderType;
import cn.life3t.life3t.message.StaffInfo;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.OrderDetailResponse;

/**
 * Created by wuguohao on 15-5-29.
 */
@EActivity(R.layout.activity_order_detail)
public class OrderDetailActivity extends BaseActivity
{
    private static final int REQUEST_CODE_COUPON_LIST = 1;

    List<StaffDetail> mStaffList = null;

    MyListAdapter mAdapter;

    @Extra
    int mOrderId;

    private GetAlipayResponse mPayInfo;
    private boolean mIsCommented;
    private int mPrice;

    @ViewById(R.id.order_state) //订单状态
    InfoItemView mOrderStateView;

    @ViewById(R.id.order_number) //订单编号
    InfoItemView mOrderNumner;

    @ViewById(R.id.order_address) // 服务地址
    InfoItemView mOrderAddress;

    @ViewById(R.id.order_time) //服务时间
    InfoItemView mOrderTime;

    @ViewById(R.id.order_specified) //服务时长、沙发座数、地板面积等
    InfoItemView mOrderSpecified;

    @ViewById(R.id.order_price) //金额
    InfoItemView mOrderPrice;

    @ViewById(R.id.aunt_count)  //阿姨数量
    InfoItemView mAuntCount;

    @ViewById(R.id.phone) // 联系电话
    InfoItemView mPhone;

    @ViewById(R.id.comment) // 备注
    InfoItemView mComment;

    @ViewById(R.id.money) // 支付金额
    TextView mMoneyView;

    @ViewById(R.id.discount)
    TextView mDiscountView;

    @ViewById(R.id.operation_btn)
    TextView mOperationBtn;

    @ViewById(R.id.pay_layout)
    View mPayLayout;

    @ViewById(R.id.staff_layout)
    View mStaffLayout;

    @ViewById(R.id.staff_list)
    ListView mListView;

    @ViewById(R.id.coupon)
    View mCouponView;

    @ViewById(R.id.coupon_use)
    TextView mCouponUseView;

    @ViewById(R.id.pay)
    TextView mPayButton;

    @Click(R.id.coupon)
    void onCouponList() {
        CouponListActivity_.intent(this).mOrderId(mOrderId).startForResult(REQUEST_CODE_COUPON_LIST);
    }

    @Click(R.id.pay)
    void pay() {
        payInBackground();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COUPON_LIST == requestCode && resultCode == RESULT_OK) {
            int id = data.getIntExtra("coupon", -1);
            getUseCouponPayInfo(id);
        }
    }

    @Background
    void payInBackground() {
        if (mPayInfo.price > 0) {
            PayTask alipay = new PayTask(this);
            // 调用支付接口，获取支付结果
            String result = alipay.pay(mPayInfo.alipayInfo);
            payResult(result);
        }
        else {
            _activityHelper.showLoadingDialog(null);
            BaseResponse response = _restClient.payFree(mOrderId);
            _activityHelper.dismissLoadingDialog();
            payFreeResult(response);
        }
    }

    @UiThread
    void payFreeResult(BaseResponse response) {
        if (BaseResponse.hasError(response)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else {
            updateViewByState(OrderState.ORDER_FINISH);
            _activityHelper.showToast(getString(R.string.pay_success));
        }
    }

    @UiThread
    void payResult(String result) {
        PayResult payResult = new PayResult(result);

        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
        String resultStatus = payResult.getResultStatus();

        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
        if (TextUtils.equals(resultStatus, "9000")) {
            updateViewByState(OrderState.ORDER_FINISH);
            _activityHelper.showToast(getString(R.string.pay_success));
        } else {
            // 判断resultStatus 为非“9000”则代表可能支付失败
            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
            if (TextUtils.equals(resultStatus, "8000")) {
                _activityHelper.showToast(getString(R.string.pay_inProgress));
            } else {
                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                _activityHelper.showToast(getString(R.string.pay_fail));
            }
        }
    }


    @AfterViews
    void init ()
    {
        mAdapter = new MyListAdapter(OrderDetailActivity.this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int staffId = mStaffList.get(position).id;
                StaffDetailActivity_.intent(OrderDetailActivity.this).mStaffId(staffId).start();
            }
        });

        getOrderDetail();
    }

    void updateViewByState(int state)
    {
        mOrderStateView.setContent(getString(OrderState.getOrderStateStringRes(state)));
        switch (state)
        {
            //等待接单
            case OrderState.NEW_ORDER:
                mPayLayout.setVisibility(View.GONE);
                mOperationBtn.setVisibility(View.VISIBLE);
                mOperationBtn.setText(getString(R.string.cancel_order));
                mOperationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CommonDialog dialog = new CommonDialog(OrderDetailActivity.this)
                                .builder().setContent(getString(R.string.affirm_to_cancel_order));
                        dialog.setOnCancelClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setOnConfirmClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCancelOrder(mOrderId);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                break;
            //已接单
            case OrderState.ORDER_CONFIRM:
                mPayLayout.setVisibility(View.GONE);
                mOperationBtn.setVisibility(View.VISIBLE);
                mOperationBtn.setText(getString(R.string.cancel_order));
                mOperationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CommonDialog dialog = new CommonDialog(OrderDetailActivity.this)
                                .builder()
                                .setContent(getString(R.string.affirm_to_cancel_order_with_staff))
                                .setConfirmString(getString(R.string.call_service_number));
                        dialog.setOnCancelClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setOnConfirmClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString(R.string.service_tel_number)));
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                break;
            //服务进行中
            case OrderState.SERVICE_START:
                mPayLayout.setVisibility(View.GONE);
                mOperationBtn.setVisibility(View.GONE);
                break;
            //等待付款
            case OrderState.SERVICE_FINISH:
                mPayLayout.setVisibility(View.GONE);
                mOperationBtn.setVisibility(View.VISIBLE);
                mOperationBtn.setText(getString(R.string.pay));
                mOperationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getPayInfo();
                    }
                });
                mOrderPrice.setVisibility(View.VISIBLE);
                mOrderPrice.setContent(String.format(getString(R.string.price_format), mPrice/100.0));
                break;
            //支付成功
            case OrderState.ONLINE_PAID:
                mPayLayout.setVisibility(View.GONE);
                mOperationBtn.setVisibility(View.GONE);
                mOrderPrice.setVisibility(View.VISIBLE);
                mOrderPrice.setContent(String.format(getString(R.string.price_format), mPrice/100.0));
                break;
            //服务结束
            case OrderState.ORDER_FINISH:
                mPayLayout.setVisibility(View.GONE);
                if (mIsCommented) {
                    mOperationBtn.setVisibility(View.GONE);
                }
                else {
                    mOperationBtn.setVisibility(View.VISIBLE);
                    mOperationBtn.setText(getString(R.string.comments));
                    mOperationBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(OrderDetailActivity.this, CommentActivity_.class);
                            intent.putExtra("orderId", mOrderId);
                            if (mStaffList != null && !mStaffList.isEmpty()) {
                                intent.putExtra("staff", mStaffList.toArray());
                            }
                            startActivity(intent);
//                            CommentActivity_.intent(OrderDetailActivity.this).start();
                        }
                    });
                }
                mOrderPrice.setVisibility(View.VISIBLE);
                mOrderPrice.setContent(String.format(getString(R.string.price_format), mPrice/100.0));
                break;
            //已取消
            case OrderState.ORDER_CANCELED:
                mPayLayout.setVisibility(View.GONE);
                mOperationBtn.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Background
    void getPayInfo() {
        _activityHelper.showLoadingDialog(null);
        GetAlipayResponse resp = _restClient.getAlipay(mOrderId);
        _activityHelper.dismissLoadingDialog();
        afterGetPayInfo(resp);
    }

    @UiThread
    void afterGetPayInfo(GetAlipayResponse resp) {

        if (BaseResponse.hasError(resp)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else {
            mPayInfo = resp;
            mOperationBtn.setVisibility(View.GONE);
            mPayLayout.setVisibility(View.VISIBLE);
            mPrice = resp.price;
            mMoneyView.setText(String.format("%1$.2f", resp.price/100.0));
            if (resp.discount > 0) {
                if (resp.price <= 0) {
                    mPayButton.setText(R.string.pay_free);
                }
                mCouponView.setVisibility(View.VISIBLE);
                mDiscountView.setText(String.format(getString(R.string.coupon_discount), resp.discount/100));
                mCouponUseView.setText(String.format(getString(R.string.coupon_use), resp.discount/100));
            }
            else {
                if (resp.couponCount > 0) {
                    mCouponUseView.setText(String.format(getString(R.string.coupon_available_count), resp.couponCount));
                    mCouponView.setVisibility(View.VISIBLE);
                }
                else {
                    mCouponView.setVisibility(View.GONE);
                }
            }
        }
    }

    @Background
    void getUseCouponPayInfo(int couponId) {
        _activityHelper.showLoadingDialog(null);
        SetCouponRequest request = new SetCouponRequest();
        request.couponId = couponId;
        GetAlipayResponse resp = _restClient.payUseCoupon(mOrderId, request);
        _activityHelper.dismissLoadingDialog();
        afterGetPayInfo(resp);
    }

    @Background
    void onCancelOrder (int orderId)
    {
        _activityHelper.showLoadingDialog(null);
        BaseResponse resp = _restClient.cancelOrder(orderId);
        _activityHelper.dismissLoadingDialog();
        updateCancelOrder(resp);
    }

    @UiThread
    void updateCancelOrder(BaseResponse resp)
    {
        if (BaseResponse.hasError(resp))
        {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else
        {
            _activityHelper.showToast(getString(R.string.cancel_order_success));
            updateViewByState(OrderState.ORDER_CANCELED);
        }
    }

    @Background
    void getOrderDetail() {
        _activityHelper.showLoadingDialog(null);
        OrderDetailResponse resp = _restClient.getOrderDetail(mOrderId);
        _activityHelper.dismissLoadingDialog();
        updateOrderDetail(resp);
    }

    @UiThread
    void updateOrderDetail (OrderDetailResponse resp) {
        if (BaseResponse.hasError(resp)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else
        {
            mIsCommented = resp.MemberAlreadyCommented;
            mStaffList = resp.auntList;
            //订单编号
            mOrderNumner.setContent(resp.orderNum);
            //服务地址
            mOrderAddress.setContent(resp.address);
            //服务时间
            mOrderTime.setContent(resp.date + " " + timeIntToString(resp.startMin));
            //阿姨数量
            mAuntCount.setContent(String.format(getString(R.string.aunt_count_placeholder), resp.auntCount));
            //服务特殊字段
            switch (resp.businessId) {
                case OrderType.NORMAL:
                case OrderType.DEEP:
                    mOrderSpecified.setContent(String.format(getString(R.string.order_hour_float), resp.businessDetail.serviceMin/60.0));
                    break;
                case OrderType.FLOOR:
                    mOrderSpecified.setImage(R.drawable.floor);
                    mOrderSpecified.setTypeText(getString(R.string.hint_floor_semi));
                    mOrderSpecified.setContent(Integer.toString(resp.businessDetail.area));
                    break;
                case OrderType.SOFA:
                    mOrderSpecified.setImage(R.drawable.sofa);
                    mOrderSpecified.setTypeText(getString(R.string.hint_sofa_semi));
                    mOrderSpecified.setContent(Integer.toString(resp.businessDetail.sofaCount));
                    break;
                case OrderType.HOOD:
                    mOrderSpecified.setVisibility(View.GONE);
                    break;
            }

            //联系电话
            mPhone.setContent(resp.phone);
            //备注
            mComment.setContent(resp.comment);

            mPrice = resp.price;

            updateViewByState(resp.status);
            updateStaffList();

        }
    }

    private void updateStaffList() {
        if (mStaffList != null) {
            mStaffLayout.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
        }
        else {
            mStaffLayout.setVisibility(View.GONE);
        }
    }

    private String timeIntToString(int intTime) {
        int h = intTime / 60;
        int m = intTime % 60;
        return String.format("%1$02d:%2$02d", h, m);
    }


    class MyListAdapter extends BaseAdapter
    {
        Context context;
        LayoutInflater layoutInflater;

        public MyListAdapter (Context context)
        {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return (mStaffList == null ? 0 : mStaffList.size());
        }

        @Override
        public Object getItem(int position) {
            return mStaffList.get(position);
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
                convertView = layoutInflater.inflate(R.layout.list_item_staff_info, null);

                holder.head_img = (SimpleDraweeView)convertView.findViewById(R.id.portrait);
                holder.verified = (ImageView)convertView.findViewById(R.id.verified);
                holder.name = (TextView)convertView.findViewById(R.id.name);
                holder.duration = (TextView)convertView.findViewById(R.id.durationValue);
                holder.rating = (RatingBar)convertView.findViewById(R.id.ratingBar);

                convertView.setTag(holder);

            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            StaffDetail item = mStaffList.get(position);

            if (item.headUrl != null && !item.headUrl.isEmpty())
                holder.head_img.setImageURI(Uri.parse(item.headUrl));
            holder.verified.setVisibility(item.isVerified ? View.VISIBLE : View.GONE);
            holder.name.setText(item.name);
            holder.duration.setText(String.format(getString(R.string.order_hour), item.serviceTime/60));
            holder.rating.setRating(item.score);

            return convertView;
        }


        private final class ViewHolder
        {
            public SimpleDraweeView head_img;
            public ImageView verified;
            public TextView name;
            public TextView duration;
            public RatingBar rating;
        }
    }
}
