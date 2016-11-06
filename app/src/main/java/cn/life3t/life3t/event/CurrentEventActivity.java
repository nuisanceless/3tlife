package cn.life3t.life3t.event;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.GetActivityListResponse;

/**
 * Created by wuguohao on 15-5-14.
 */
@EActivity(R.layout.activity_cur_event)
public class CurrentEventActivity extends BaseActivity
{
    private List<GetActivityListResponse.ActivityListItem> mDataList;
    private ActionAdapter mAdapter;

    @ViewById(R.id.list_view)
    ListView mListView;

    @AfterViews
    void init ()
    {
        mListView.addHeaderView(new View(this));//第一条item上面的divider一直都在，只是不显示，加一个 head view 就显示出来了，所以加一个空的。
        mAdapter = new ActionAdapter();
        mListView.setAdapter(mAdapter);
        getActionList();
    }

    @Background
    void getActionList ()
    {
        GetActivityListResponse resp = new GetActivityListResponse();
        resp = _restClient.getActivityList();

        updateGetActionList(resp);
    }

    @UiThread
    void updateGetActionList(GetActivityListResponse resp)
    {
        if (BaseResponse.hasError(resp))
        {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else
        {
            mDataList = resp.list;
        }
        mAdapter.notifyDataSetChanged();
    }

    class ActionAdapter extends BaseAdapter
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
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = _layoutInflater.inflate(R.layout.list_item_action, null);

                holder.actionImg   = (SimpleDraweeView)convertView.findViewById(R.id.action_image);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            final GetActivityListResponse.ActivityListItem item = mDataList.get(position);

            holder.actionImg.setImageURI(Uri.parse(item.listImageUrl));
            holder.actionImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityDetailActivity_.intent(CurrentEventActivity.this).activityId(item.id).start();
                }
            });

            return convertView;
        }
        class ViewHolder
        {
            public SimpleDraweeView actionImg;
        }
    }












//    @Click(R.id.btn_back)
//    void onBack() {
//        if (getIntent().getStringExtra(MainActivity.EVENT_LIST_CALL_ACTIVITY).equals("MainActivity")) {
//            finishByAnim();
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (getIntent().getStringExtra(MainActivity.EVENT_LIST_CALL_ACTIVITY).equals("MainActivity")) {
//            finishByAnim();
//        }
//    }

    /** 从首页跳转，按照自定义动画跳转回去 */
    private void finishByAnim ()
    {
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
    }
}
