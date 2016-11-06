package cn.life3t.life3t.account;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.QuestionAnswer;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.QuestionListResponse;

/**
 * Created by Lei on 2015/8/20.
 */
@EActivity(R.layout.activity_question)
public class QuestionActivity extends BaseActivity {
    List<QuestionAnswer> mDataList;
    MyAdapter myAdapter = new MyAdapter();

    @ViewById(R.id.list)
    ListView mListView;

    @AfterViews
    void afterViews() {
        mListView.setAdapter(myAdapter);
        getQuestionList();
    }

    @Background
    void getQuestionList() {
        QuestionListResponse response = _restClient.getQuestionList();
        afterGetQuestionList(response);
    }

    @UiThread
    void afterGetQuestionList(QuestionListResponse response) {
        if (BaseResponse.hasError(response)) {
            _activityHelper.showToast(BaseResponse.getErrorMessage(response));
        }
        else {
            mDataList = response.list;
            myAdapter.notifyDataSetChanged();
        }
    }

    class MyAdapter extends BaseAdapter {

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
                convertView = _layoutInflater.inflate(R.layout.list_item_question, null);

                holder.question   = (TextView)convertView.findViewById(R.id.question);
                holder.answer = (TextView)convertView.findViewById(R.id.answer);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            QuestionAnswer item = mDataList.get(position);
            holder.question.setText(item.title);
            holder.answer.setText(item.content);

            return convertView;
        }

        class ViewHolder
        {
            public TextView question;
            public TextView answer;
        }
    }
}
