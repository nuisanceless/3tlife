package cn.life3t.life3t.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.message.ServiceSubItem;

/**
 * Created by Lei on 2015/5/19.
 */

public abstract class SubServiceBaseActivity extends BaseActivity {
    TextView _titleView;
    ListView _listView;
    ImageView _titleButton;
    MyListAdapter _adapter = new MyListAdapter();
    List<ServiceSubItem> _dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_service);

        _titleView = (TextView)findViewById(R.id.title);
        _titleView.setText(getActivityTitle());

        _dataList = getData();

        _listView = (ListView)findViewById(R.id.list);
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(parent, view, position, id);
            }
        });
        _listView.setAdapter(new MyListAdapter());

        _titleButton = (ImageView)findViewById(R.id.btn_custom);
        _titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInformation();
            }
        });
    }
    protected abstract String getActivityTitle();
    protected abstract List<ServiceSubItem> getData();
    protected abstract void onListItemClick(AdapterView<?> parent, View view, int position, long id);
    protected abstract void onInformation();

    class MyListAdapter extends BaseAdapter {
        class ViewHolder {
            ImageView image;
            TextView title;
            TextView price;
            TextView priceSuffix;
            TextView description;
        }
        @Override
        public int getCount() {
            return (_dataList == null ? 0 : _dataList.size());
        }

        @Override
        public Object getItem(int i) {
            return _dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(SubServiceBaseActivity.this).inflate(R.layout.list_item_service, null);
                holder = new ViewHolder();
                holder.image = (ImageView)view.findViewById(R.id.service_item_image);
                holder.title = (TextView)view.findViewById(R.id.service_item_title);
                holder.price = (TextView)view.findViewById(R.id.service_item_price);
                holder.priceSuffix = (TextView)view.findViewById(R.id.service_item_price_suffix);
                holder.description = (TextView)view.findViewById(R.id.service_item_description);
                view.setTag(holder);
            }
            else {
                holder = (ViewHolder)view.getTag();
            }

            ServiceSubItem item = _dataList.get(i);
            holder.image.setImageResource(item.image);
            holder.title.setText(item.title);
            holder.description.setText(item.description);
            if (item.price == null) {
                holder.price.setVisibility(View.INVISIBLE);
                holder.priceSuffix.setVisibility(View.INVISIBLE);
            }
            else {
                holder.price.setVisibility(View.VISIBLE);
                holder.priceSuffix.setVisibility(View.VISIBLE);
                holder.price.setText(item.price);
                holder.priceSuffix.setText(item.priceSuffix);
            }

            return view;
        }
    }
}
