package cn.life3t.life3t.order;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.life3t.life3t.ActivityHelper_;
import cn.life3t.life3t.R;
import cn.life3t.life3t.common.ActionSheetDialog;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.Comment;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.CommentListResponse;
import cn.life3t.life3t.message.response.StaffDetailResponse;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by wuguohao on 15-6-1.
 */
@EActivity(R.layout.activity_staff_detail)
public class StaffDetailActivity extends BaseActivity implements PlatformActionListener
{
    int mPage = 1;
    int mPageSize = 10;

    private String mStaffName = "";

    View mHeadView;

    EvaluateAdapter mAdapter;
    List<Comment> mDataList = new ArrayList<Comment>();

    private File _shareImage = new File(Environment.getExternalStorageDirectory(), "3tlife.png");

    @Extra
    int mStaffId;

    @ViewById(R.id.evaluate_listview)
    PullToRefreshListView mListView;

    @Click(R.id.btn_custom)
    void onShare ()
    {
        ActionSheetDialog dialog = new ActionSheetDialog(StaffDetailActivity.this);
        View shareView = LayoutInflater.from(StaffDetailActivity.this).inflate(R.layout.view_share, null);
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

        mAdapter = new EvaluateAdapter(StaffDetailActivity.this);


        mHeadView = _layoutInflater.inflate(R.layout.user_evaluate_list_head_view, null);
        View orderTimeView = mHeadView.findViewById(R.id.order_time);
        orderTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaffAvailableTimeDialog dlg = new StaffAvailableTimeDialog(StaffDetailActivity.this, mStaffId);
                dlg.show();
            }
        });

        mListView.getRefreshableView().addHeaderView(mHeadView);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setScrollingWhileRefreshingEnabled(false);

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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        mListView.setAdapter(mAdapter);
        mListView.setRefreshing(false);
        getStaffDetailInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        deleteTempImage();
        ShareSDK.stopSDK();
    }

    @Background
    void getStaffDetailInfo ()
    {
        StaffDetailResponse resp = _restClient.getStaffDetail(mStaffId);
        updateStaffDetail(resp);
    }

    @UiThread
    void updateStaffDetail (StaffDetailResponse resp)
    {
        if (BaseResponse.hasError(resp))
        {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else
        {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int serveYear = 1;
            try {
                if (resp.workExperience != null) {
                    Date joinDate = sdf.parse(resp.workExperience);
                    serveYear = (int)Math.ceil((Calendar.getInstance().getTimeInMillis() - joinDate.getTime()) / (1000*3600*24*365.0));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //头像
            if (resp.headUrl != null || !resp.headUrl.isEmpty()) {
                ((SimpleDraweeView)mHeadView.findViewById(R.id.portrait)).setImageURI(Uri.parse(resp.headUrl));
            }
            //验证
            ((ImageView)mHeadView.findViewById(R.id.verified)).setVisibility(resp.isVerified ? View.VISIBLE : View.GONE);
            //名字
            ((TextView)mHeadView.findViewById(R.id.name)).setText(resp.name);
            //省份
            ((TextView)mHeadView.findViewById(R.id.province)).setText(resp.nativeAddress);
            //服务年限
            ((TextView)mHeadView.findViewById(R.id.service_year)).setText(String.format(getString(R.string.service_year), serveYear));
            //服务时长
            ((TextView)mHeadView.findViewById(R.id.service_duration)).setText(String.format(getString(R.string.order_hour), resp.serviceTime/60));
            //用户评分
            ((RatingBar)mHeadView.findViewById(R.id.ratingBar)).setRating(resp.score);

            mStaffName = resp.name;
            //多少条评论
//            ((TextView)mHeadView.findViewById(R.id.evaluateCount)).setText(String.format("(%d)条", resp.evaluate_count));
        }
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
        CommentListResponse resp = _restClient.getStaffComments(mStaffId, mPage, mPageSize);

        getDataResult(fromStart, resp);
    }

    /**
     * @param fromStart true:mStart = 0; false:mStart = 加载更多
     * */
    @UiThread
    void getDataResult (boolean fromStart, CommentListResponse resp)
    {
        mListView.onRefreshComplete();
        if (BaseResponse.hasError(resp)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else {

            //多少条评论
            ((TextView)mHeadView.findViewById(R.id.evaluateCount)).setText(String.format("(%d)条", resp.totalItem));

            if (resp.auntCommentList != null) {
                if (fromStart)
                    mDataList.clear();
                mDataList.addAll(resp.auntCommentList);

                mAdapter.notifyDataSetChanged();

                if (resp.auntCommentList.size() < mPageSize) {
                    mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mListView.setMode(PullToRefreshBase.Mode.BOTH);
                }
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
        String content = String.format(getString(R.string.staff_detail_share_content), mStaffName, mStaffName);
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


    public class EvaluateAdapter extends BaseAdapter
    {
        Context context;

        public EvaluateAdapter (Context context)
        {
            this.context = context;
        }

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
                convertView = _layoutInflater.inflate(R.layout.list_item_evaluate, null);

                holder.head_img   = (ImageView)convertView.findViewById(R.id.head);
                holder.phone = (TextView)convertView.findViewById(R.id.phone);
                holder.rating     = (RatingBar)convertView.findViewById(R.id.rating);
                holder.time       = (TextView)convertView.findViewById(R.id.time);
                holder.gridView   = (GridView)convertView.findViewById(R.id.grid_view);
                holder.comment_text = (TextView)convertView.findViewById(R.id.comment_text);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }

            Comment item = mDataList.get(position);

            if (item.nickname.equals(item.memberPhone))
            {
                StringBuffer sb = new StringBuffer(item.nickname);
                sb.delete(3, 7);
                sb.insert(3, "****");
                holder.phone.setText(sb.toString());
            }
            else
            {
                holder.phone.setText(item.nickname);
            }
            holder.rating.setRating(item.star);
            holder.time.setText(item.createtime);
            List<Integer> gridViewData = item.labelIndexs;
            MyGridAdapter gridViewAdapter = new MyGridAdapter(gridViewData);
            holder.gridView.setAdapter(gridViewAdapter);
            if (item.text_comment == null || item.text_comment.isEmpty())
            {
                holder.comment_text.setVisibility(View.GONE);
            }
            else
            {
                holder.comment_text.setText(item.text_comment);
                holder.comment_text.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        private final class ViewHolder
        {
            public ImageView head_img;
            public TextView phone;
            public RatingBar rating;
            public TextView time;
            public GridView gridView;
            public TextView comment_text;

        }

    }

    class MyGridAdapter extends BaseAdapter
    {
        public List<Integer> gridViewData;

        public MyGridAdapter (List<Integer> data)
        {
            gridViewData = data;
        }

        @Override
        public int getCount() {
            return (gridViewData == null ? 0 : gridViewData.size());
        }

        @Override
        public Object getItem(int position) {
            return gridViewData == null ? null : gridViewData.get(position);
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
                convertView = _layoutInflater.inflate(R.layout.grid_item_comment, null);

                holder.commentType = (TextView)convertView.findViewById(R.id.comment_type);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }


            getCommentTypeString(gridViewData.get(position), holder);

            return convertView;
        }

        class ViewHolder {
            TextView commentType;
        }

        private void getCommentTypeString (int type, MyGridAdapter.ViewHolder holder)
        {
            String commentTypeString = "";
            switch (type)
            {
                case 0:
                    commentTypeString = getString(R.string.on_time);
                    break;
                case 1:
                    commentTypeString = getString(R.string.earnest);
                    break;
                case 2:
                    commentTypeString = getString(R.string.good_job);
                    break;
                case 3:
                    commentTypeString = getString(R.string.friendly);
                    break;
                case 4:
                    commentTypeString = getString(R.string.skilled);
                    break;
                case 5:
                    commentTypeString = getString(R.string.no_on_time);
                    holder.commentType.setSelected(true);
                    break;
                case 6:
                    commentTypeString = getString(R.string.no_earnest);
                    holder.commentType.setSelected(true);
                    break;
                case 7:
                    commentTypeString = getString(R.string.no_good_job);
                    holder.commentType.setSelected(true);
                    break;
                case 8:
                    commentTypeString = getString(R.string.no_friendly);
                    holder.commentType.setSelected(true);
                    break;
                case 9:
                    commentTypeString = getString(R.string.no_skilled);
                    holder.commentType.setSelected(true);
                    break;
                default:
                    break;
            }

            holder.commentType.setText(commentTypeString);
        }
    }

}
