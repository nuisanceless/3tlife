package cn.life3t.life3t.order;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.StaffDetail;
import cn.life3t.life3t.message.UserComment;
import cn.life3t.life3t.message.request.OrderCommentRequest;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.OrderDetailResponse;

/**
 * Created by wuguohao on 15-6-3.
 */
@EActivity(R.layout.activity_comment)
public class CommentActivity extends BaseActivity
{
    private List<ViewHolder> mViewList = new ArrayList<>();
    private MyListAdapter mAdapter;
    private int mOrderId;

    @ViewById(R.id.list_view)
    ListView mListView;

    @Click(R.id.submit)
    void onSubmit() {
        onComment();
    }

    @Background
    void onComment() {
        _activityHelper.showLoadingDialog(null);
        OrderCommentRequest request = new OrderCommentRequest();
        request.orderId = mOrderId;
        request.comments = new ArrayList<>();
        for (ViewHolder holder : mViewList) {
            UserComment comment = new UserComment();
            comment.auntId = holder.staffId;
            comment.star = holder.ratingBar.getRating();
            comment.text_comment = holder.content.getText().toString();
            List<Integer> list = new ArrayList<>(10);
            if (holder.on_time.isSelected()) {
                list.add(0);
            }
            if (holder.earnest.isSelected()) {
                list.add(1);
            }
            if (holder.good_job.isSelected()) {
                list.add(2);
            }
            if (holder.friendly.isSelected()) {
                list.add(3);
            }
            if (holder.skilled.isSelected()) {
                list.add(4);
            }
            if (holder.no_on_time.isSelected()) {
                list.add(5);
            }
            if (holder.no_earnest.isSelected()) {
                list.add(6);
            }
            if (holder.no_good_job.isSelected()) {
                list.add(7);
            }
            if (holder.no_friendly.isSelected()) {
                list.add(8);
            }
            if (holder.no_skilled.isSelected()) {
                list.add(9);
            }
            if (!list.isEmpty())
                comment.labelIndexs = list;
            request.comments.add(comment);
        }
        BaseResponse response = _restClient.commentOrder(request);
        _activityHelper.dismissLoadingDialog();
        onCommentResult(response);
    }

    @UiThread
    void onCommentResult(BaseResponse response) {
        if (BaseResponse.hasError(response)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else {
            _activityHelper.showToast(getString(R.string.comment_success));
            finish();
        }
    }

    @AfterViews
    void init ()
    {
        mAdapter = new MyListAdapter();
        mListView.setAdapter(mAdapter);

        Intent intent = getIntent();

        mOrderId = intent.getIntExtra("orderId", -1);
        Object result = intent.getSerializableExtra("staff");
        if (result != null) {
            List<StaffDetail> list = new ArrayList<>();
            Object[] array = (Object[])result;
            for (Object staff : array) {
                list.add((StaffDetail)staff);
            }
            createViews(list);
        }
        else {
            getOrderDetail();
        }
    }

    private void createViews(List<StaffDetail> staffList) {
        for (StaffDetail staff : staffList) {
            ViewHolder holder = new ViewHolder();
            holder.staffId = staff.id;
            holder.whole = _layoutInflater.inflate(R.layout.list_item_comment, null);

            holder.head_img = (SimpleDraweeView)holder.whole.findViewById(R.id.portrait);
            holder.verified = (ImageView)holder.whole.findViewById(R.id.verified);
            holder.name = (TextView)holder.whole.findViewById(R.id.name);
            holder.province = (TextView)holder.whole.findViewById(R.id.province);
            holder.service_year = (TextView)holder.whole.findViewById(R.id.service_year);
            holder.duration = (TextView)holder.whole.findViewById(R.id.service_duration);
            holder.smile_face = (TextView)holder.whole.findViewById(R.id.smile_face);
            holder.cry_face = (TextView)holder.whole.findViewById(R.id.cry_face);
            holder.ratingBar = (RatingBar) holder.whole.findViewById(R.id.ratingBar);
            holder.content = (EditText)holder.whole.findViewById(R.id.content);

            holder.smile_face.setSelected(true);

            holder.on_time = (RadioButton)holder.whole.findViewById(R.id.on_time);
            holder.no_on_time = (RadioButton)holder.whole.findViewById(R.id.no_on_time);
            holder.earnest = (RadioButton)holder.whole.findViewById(R.id.earnest);
            holder.no_earnest = (RadioButton)holder.whole.findViewById(R.id.no_earnest);
            holder.good_job = (RadioButton)holder.whole.findViewById(R.id.good_job);
            holder.no_good_job = (RadioButton)holder.whole.findViewById(R.id.no_good_job);
            holder.friendly = (RadioButton)holder.whole.findViewById(R.id.friendly);
            holder.no_friendly = (RadioButton)holder.whole.findViewById(R.id.no_friendly);
            holder.skilled = (RadioButton)holder.whole.findViewById(R.id.skilled);
            holder.no_skilled = (RadioButton)holder.whole.findViewById(R.id.no_skilled);


            holder.on_time.setOnClickListener(new radioClickListener(holder, holder.no_on_time));
            holder.no_on_time.setOnClickListener(new radioClickListener( holder, holder.on_time));
            holder.earnest.setOnClickListener(new radioClickListener(holder, holder.no_earnest));
            holder.no_earnest.setOnClickListener(new radioClickListener(holder, holder.earnest));
            holder.good_job.setOnClickListener(new radioClickListener(holder, holder.no_good_job));
            holder.no_good_job.setOnClickListener(new radioClickListener(holder, holder.good_job));
            holder.friendly.setOnClickListener(new radioClickListener(holder, holder.no_friendly));
            holder.no_friendly.setOnClickListener(new radioClickListener(holder, holder.friendly));
            holder.skilled.setOnClickListener(new radioClickListener(holder, holder.no_skilled));
            holder.no_skilled.setOnClickListener(new radioClickListener(holder, holder.skilled));

            if (staff.headUrl != null && !staff.headUrl.isEmpty()) {
                holder.head_img.setImageURI(Uri.parse(staff.headUrl));
            }
            holder.verified.setVisibility(staff.isVerified ? View.VISIBLE : View.GONE);
            holder.name.setText(staff.name);
            holder.province.setText(staff.nativeAddress);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int serveYear = 1;
            try {
                if (staff.workExperience != null) {
                    Date joinDate = sdf.parse(staff.workExperience);
                    serveYear = (int)Math.ceil((Calendar.getInstance().getTimeInMillis() - joinDate.getTime()) / (1000*3600*24*365.0));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.service_year.setText(String.format(getString(R.string.service_year), serveYear));
            holder.duration.setText(String.format(getString(R.string.order_hour), staff.serviceTime/60));
//            holder.ratingBar.setRating(staff.score);

            mViewList.add(holder);
        }
        mAdapter.notifyDataSetChanged();
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
            if (resp.auntList != null) {
                createViews(resp.auntList);
            }
        }
    }

    class radioClickListener implements View.OnClickListener
    {
        private ViewHolder holder;
        private RadioButton rb1;
        public radioClickListener(ViewHolder holder, RadioButton rb1) {
            this.holder = holder;
            this.rb1 = rb1;
        }
        public void onClick(View v) {
            RadioButton rb = (RadioButton)v;
            if (rb.isSelected())
            {
                rb.setSelected(false);
            }
            else if (!rb.isSelected())
            {
                rb.setSelected(true);
                rb1.setSelected(false);
            }

            boolean good = true;
            if (holder.no_on_time.isSelected() || holder.no_earnest.isSelected()
                    || holder.no_good_job.isSelected() || holder.no_friendly.isSelected()
                    || holder.no_skilled.isSelected())
            {
                good = false;
            }
            holder.cry_face.setSelected(!good);
            holder.smile_face.setSelected(good);
        }
    }


    private final class ViewHolder
    {
        public int staffId;
        public View whole;

        public SimpleDraweeView head_img;
        public ImageView verified;
        public TextView name;
        public TextView province;
        public TextView service_year;
        public TextView duration;
        public TextView smile_face;
        public TextView cry_face;
        public RatingBar ratingBar;
        public EditText content;

        public RadioButton on_time;
        public RadioButton no_on_time;
        public RadioButton earnest;
        public RadioButton no_earnest;
        public RadioButton good_job;
        public RadioButton no_good_job;
        public RadioButton friendly;
        public RadioButton no_friendly;
        public RadioButton skilled;
        public RadioButton no_skilled;
    }

    private class MyListAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public Object getItem(int position) {
            return mViewList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = mViewList.get(position);
            return holder.whole;
        }



    }
}
