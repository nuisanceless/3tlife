package cn.life3t.life3t.order;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.life3t.life3t.ActivityHelper_;
import cn.life3t.life3t.MyApplication;
import cn.life3t.life3t.R;
import cn.life3t.life3t.account.OrderManagementActivity;
import cn.life3t.life3t.cleaning.CleaningServiceOrderSubmitActivity_;
import cn.life3t.life3t.message.Order;
import cn.life3t.life3t.message.response.OrderListResponse;
import cn.life3t.life3t.message.type.OrderState;
import cn.life3t.life3t.message.type.OrderType;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.rest.MyRestClient;

/**
 * Created by wuguohao on 15-5-26.
 */
@EFragment(R.layout.fragment_order)
public class OrderFragment extends Fragment
{
    List<Order> mDataList = new ArrayList<Order>();
    OrderAdapter mAdapter;
    Context mContext;
    MyRestClient mRestClient;
    int mPage = 1;
    int mPageSize = 10;
    /** 区分是进行中订单列表还是已完成订单列表 */
    int mOrderFragmentType;
    @App
    MyApplication mApp;
    ActivityHelper_ mActivityHelper;

    @ViewById(R.id.empty_layout)
    View mEmptyView;

    @ViewById(R.id.order_listview)
    PullToRefreshListView mListView;

    @AfterViews
    void init ()
    {
        mOrderFragmentType = this.getArguments().getInt(OrderManagementActivity.ORDER_FRAGMENT_TYPE);
        mRestClient = mApp.restClient();
        mContext = getActivity();
        mActivityHelper = ActivityHelper_.getInstance_(mContext);
        mAdapter = new OrderAdapter(mContext);

        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setScrollingWhileRefreshingEnabled(true);
        mListView.setEmptyView(mEmptyView);

        ILoadingLayout startLabels = mListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel(getString(R.string.pull_down_to_refresh));
        startLabels.setRefreshingLabel(getString(R.string.hard_loading));
        startLabels.setReleaseLabel(getString(R.string.loosen_to_refresh));

        ILoadingLayout endLabels = mListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel(getString(R.string.pull_up_to_refresh));
        endLabels.setRefreshingLabel(getString(R.string.hard_loading));
        endLabels.setReleaseLabel(getString(R.string.loosen_to_load_more));

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadFromStart(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadFromStart(false);
            }
        });

//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });

        mListView.setAdapter(mAdapter);
        mListView.setRefreshing(false);
    }

    /**
     * @param fromStart true:mStart = 0; false:mStart = 加载更多
     * */
    void loadFromStart (boolean fromStart)
    {
        if (fromStart)
        {
            mPage = 1;
        }
        else {
            mPage++;
        }
        getData(fromStart);
    }

    /**
     * @param fromStart true:mStart = 0; false:mStart = 加载更多
     * */
    @Background
    void getData (boolean fromStart)
    {
        OrderListResponse resp = null;

        if (mOrderFragmentType == OrderManagementActivity.ORDER_FRAGMENT_IN_PROGRESS)
        {
            resp = mRestClient.getProgressingOrders(mPage, mPageSize);
        }
        else if (mOrderFragmentType == OrderManagementActivity.ORDER_FRAGMENT_FINISHED)
        {
            resp = mRestClient.getFinishedOrders(mPage, mPageSize);
        }

        getDataResult(fromStart, resp);
    }

    /**
     * @param fromStart true:mStart = 0; false:mStart = 加载更多
     * */
    @UiThread
    void getDataResult (boolean fromStart, OrderListResponse resp)
    {
        mListView.onRefreshComplete();
        if (BaseResponse.hasError(resp)) {
            mActivityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else {
            if (resp.list != null) {
                if (fromStart)
                    mDataList.clear();
                mDataList.addAll(resp.list);

                mAdapter.notifyDataSetChanged();

                if (resp.list.size() < mPageSize) {
                    mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mListView.setMode(PullToRefreshBase.Mode.BOTH);
                }
            }
        }
    }




    public class OrderAdapter extends BaseAdapter
    {
        Context context;
        LayoutInflater layoutInflater;

        public OrderAdapter (Context context)
        {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
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
                convertView = layoutInflater.inflate(R.layout.list_item_order, null);

                holder.order_main_view  = convertView.findViewById(R.id.main_view);
                holder.order_item_img   = (ImageView)convertView.findViewById(R.id.order_item_image);
                holder.order_item_title = (TextView)convertView.findViewById(R.id.order_item_title);
                holder.order_status     = (TextView)convertView.findViewById(R.id.order_status);
                holder.order_time       = (TextView)convertView.findViewById(R.id.order_time);
                holder.order_address    = (TextView)convertView.findViewById(R.id.order_address);
                holder.order_again      = (TextView)convertView.findViewById(R.id.order_again);
                holder.comments         = (TextView)convertView.findViewById(R.id.comments);
                holder.btn_layout       = (RelativeLayout)convertView.findViewById(R.id.btn_layout);

                convertView.setTag(holder);

            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            final Order item = mDataList.get(position);

            holder.order_item_img.setImageResource(OrderType.getOrderTypeImageRes(item.businessId));
            holder.order_item_title.setText(OrderType.getOrderTypeStringRes(item.businessId));
            holder.order_address.setText(item.address);
            holder.order_status.setText(OrderState.getOrderStateStringRes(item.status));
            holder.order_time.setText(item.date + " " + timeIntToString(item.startMin));
            // 订单单击事件
            holder.order_main_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderDetailActivity_.intent(mContext).mOrderId(item.id).start();
                }
            });
            // 若订单已完成，显示再次预约和评价按钮
            if (item.status == OrderState.ORDER_FINISH) {
                if (item.MemberAlreadyCommented) {
                    holder.comments.setEnabled(false);
                    holder.comments.setText(R.string.comment_finish);
                }
                else {
                    holder.comments.setEnabled(true);
                    holder.comments.setText(R.string.comments);
                }
                holder.btn_layout.setVisibility(View.VISIBLE);
            }
            else {
                holder.btn_layout.setVisibility(View.GONE);
            }
            // 评价
            holder.comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommentActivity_.class);
                    intent.putExtra("orderId", item.id);
                    if (item.auntList != null && !item.auntList.isEmpty()) {
                        intent.putExtra("staff", item.auntList.toArray());
                    }
                    startActivity(intent);
//                    CommentActivity_.intent(mContext).start();
                }
            });
            // 再次预约
            holder.order_again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CleaningServiceOrderSubmitActivity_.intent(mContext)._serviceType(item.businessId).start();
                }
            });

            return convertView;
        }

        private String timeIntToString(int intTime) {
            int h = intTime / 60;
            int m = intTime % 60;
            return String.format("%1$02d:%2$02d", h, m);
        }

        private final class ViewHolder
        {
            public View order_main_view;
            public ImageView    order_item_img;
            public TextView     order_item_title;
            public TextView     order_status;
            public TextView     order_time;
            public TextView     order_address;
            public TextView     order_again;
            public TextView     comments;
            public RelativeLayout btn_layout;
        }
    }

}
