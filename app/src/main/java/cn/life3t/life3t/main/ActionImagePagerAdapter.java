package cn.life3t.life3t.main;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.salvage.RecyclingPagerAdapter;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.event.ActivityDetailActivity_;

/**
 * Created by wgh on 2015/1/23.
 */
public class ActionImagePagerAdapter extends RecyclingPagerAdapter implements IconPagerAdapter
{
    private Context context;
    private List<ItemData>   mDataList;

    public static class ItemData
    {
        public long id;
        public String mImageUrl;
    }

    private boolean       isInfiniteLoop;

    public ActionImagePagerAdapter(Context context, List<ItemData> dataList)
    {
        this.context = context;
        this.mDataList = dataList;
        isInfiniteLoop = false;
    }

    @Override
    public int getIconResId(int index) {
        return R.drawable.pager_indicator;
    }

    @Override
    public int getCount() {
        return isInfiniteLoop ? Integer.MAX_VALUE : mDataList.size();
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % mDataList.size() : position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_action_image_pager, null);
        }

        SimpleDraweeView actionImg = (SimpleDraweeView)convertView.findViewById(R.id.image);

        final ItemData data = mDataList.get(getPosition(position));
        actionImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        actionImg.setImageURI(Uri.parse(data.mImageUrl));
        actionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityDetailActivity_.intent(context).activityId(data.id).start();
            }
        });

        return convertView;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public ActionImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }
}
