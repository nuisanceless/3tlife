package cn.life3t.life3t.main;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import com.viewpagerindicator.IconPageIndicator;

import java.util.ArrayList;
import java.util.List;

import cn.life3t.life3t.MyApplication;
import cn.life3t.life3t.MyApplication_;
import cn.life3t.life3t.R;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.GetActivityListResponse;
import cn.life3t.life3t.rest.MyRestClient;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by wuguohao on 15-8-5.
 */
public class ActionViewPagerDialog extends Dialog
{
    private MyApplication _app;
    private MyRestClient _restClient;
    private Context _context;
    private GetActivityListResponse _response;

    private AutoScrollViewPager _viewPager;
    private IconPageIndicator _indicator;
    ActionImagePagerAdapter pagerAdapter;

    public ActionViewPagerDialog(Context context, GetActivityListResponse resp)
    {
        super(context, R.style.add_dialog);
        _context = context;
        _response = resp;
        _app = MyApplication_.getInstance();
        _restClient = _app.restClient();
        initView();
    }

    private void initView ()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(_context);
        View view = layoutInflater.inflate(R.layout.dialog_action_view_pager, null);
        _viewPager = (AutoScrollViewPager)view.findViewById(R.id.view_pager);
        _indicator = (IconPageIndicator)view.findViewById(R.id.indicator);
        updateActionViewPager();

        getWindow().setContentView(view);
    }

    private void updateActionViewPager ()
    {
        List<ActionImagePagerAdapter.ItemData> dataList = new ArrayList<ActionImagePagerAdapter.ItemData>();
        for (int i = 0; i < _response.list.size(); i++){
            ActionImagePagerAdapter.ItemData data = new ActionImagePagerAdapter.ItemData();
            GetActivityListResponse.ActivityListItem activityItem = _response.list.get(i);


            data.id = activityItem.id;
            data.mImageUrl = activityItem.largeImageUrl;
            dataList.add(data);
        }

        pagerAdapter = new ActionImagePagerAdapter(getContext(), dataList);
        _viewPager.setAdapter(pagerAdapter);
        _indicator.setViewPager(_viewPager);
        if (dataList.size() > 1) {
            _indicator.setVisibility(View.VISIBLE);
            _indicator.notifyDataSetChanged();
        }
        else {
            _indicator.setVisibility(View.INVISIBLE);
        }
        _viewPager.setCycle(true);
        _viewPager.setInterval(5000);
//        _viewPager.startAutoScroll();
    }

}
