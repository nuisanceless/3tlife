package cn.life3t.life3t.cleaning;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.CleaningServiceExplainInfo;
import cn.life3t.life3t.utils.ListViewForScrollView;

/**
 * Created by wuguohao on 15-6-9.
 */
@EActivity(R.layout.activity_cleaning_service_info)
public class CleaningServiceInfoActivity extends BaseActivity
{
    List<CleaningServiceExplainInfo> mDataList = new ArrayList<CleaningServiceExplainInfo>();
    MyAdapter _adapter = new MyAdapter();

    @ViewById(R.id.list_view)
    ListView _listView;

    @AfterViews
    void init ()
    {
        mDataList = getData();
        _listView.addHeaderView(_layoutInflater.inflate(R.layout.service_explain_head, null));
        View footerView = new View(this);
        footerView.setMinimumHeight(20);
        _listView.addFooterView(footerView);
        _listView.setAdapter(_adapter);
    }

    private List<CleaningServiceExplainInfo> getData ()
    {
        List<CleaningServiceExplainInfo> data = new ArrayList<CleaningServiceExplainInfo>();
        String[] service_types = getResources().getStringArray(R.array.cleaning_service_types);
        String[] explains = getResources().getStringArray(R.array.cleaning_service_explain);
        String[] prices = getResources().getStringArray(R.array.cleaning_service_price);

        for (int i = 0; i < service_types.length; i++)
        {
            List<String> listPrice = new ArrayList<>();
            listPrice.add(prices[i]);
            CleaningServiceExplainInfo item = new CleaningServiceExplainInfo();
            item.service_type = service_types[i];
            item.explain = explains[i];
            item.prices = listPrice;
            data.add(i, item);
        }


        return data;
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
                convertView = _layoutInflater.inflate(R.layout.list_item_cleaning_service_info, null);

                holder.service_type = (TextView)convertView.findViewById(R.id.service_type);
                holder.explain = (TextView)convertView.findViewById(R.id.explain);
                holder.price_list = (ListViewForScrollView)convertView.findViewById(R.id.price_list);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            CleaningServiceExplainInfo item = mDataList.get(position);

            holder.service_type.setText(item.service_type);
            holder.explain.setText(item.explain);
            List<String> prices = item.prices;
            PriceAdapter priceAdapter = new PriceAdapter(prices);
            holder.price_list.setAdapter(priceAdapter);



            return convertView;
        }

        private final class ViewHolder
        {
            public TextView service_type;
            public TextView explain;
            public ListViewForScrollView price_list;
        }
    }

    public class PriceAdapter extends BaseAdapter
    {
        public List<String> priceData;

        public PriceAdapter (List<String> data)
        {
            priceData = data;
        }

        @Override
        public int getCount() {
            return priceData == null ? 0 : priceData.size();
        }

        @Override
        public Object getItem(int position) {
            return priceData == null ? null : priceData.get(position);
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
                convertView = _layoutInflater.inflate(R.layout.list_item_price_reference, null);

                holder.price = (TextView)convertView.findViewById(R.id.price);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            String item = priceData.get(position);

            holder.price.setText(item);

            return convertView;
        }

        private final class ViewHolder
        {
            public TextView price;

        }
    }
}
