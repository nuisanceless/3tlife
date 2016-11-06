package cn.life3t.life3t.account;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import cn.life3t.life3t.R;
import cn.life3t.life3t.common.CommonDialog;
import cn.life3t.life3t.common.swipemenulistview.SwipeMenu;
import cn.life3t.life3t.common.swipemenulistview.SwipeMenuCreator;
import cn.life3t.life3t.common.swipemenulistview.SwipeMenuItem;
import cn.life3t.life3t.common.swipemenulistview.SwipeMenuListView;
import cn.life3t.life3t.main.BaseActivity;
import cn.life3t.life3t.message.AddressInfo;
import cn.life3t.life3t.message.response.AddressListResponse;
import cn.life3t.life3t.message.response.BaseResponse;

/**
 * Created by wuguohao on 15-5-18.
 */
@EActivity(R.layout.activity_address_management)
public class AddressManagementActivity extends BaseActivity
{
    private static final int REQUEST_CODE_MODIFY_ADDRESS = 1;
    private static final int REQUEST_CODE_ADD_ADDRESS = 2;

    private List<AddressInfo> mDataList;
    private AddressAdapter mAdapter;
    private boolean mTargetAddressDeleted = false;

    AddressInfo mAddressModified;

    @Extra
    boolean mChooseAddress = false;

    @Extra
    int mAddressSelectedId = -1;


    @ViewById(R.id.listView)
    SwipeMenuListView mListView;

    @Click(R.id.add_address)
    void addAddress ()
    {
        AddAddressActivity_.intent(AddressManagementActivity.this).startForResult(REQUEST_CODE_ADD_ADDRESS);
    }

    @Click(R.id.btn_back)
    void onBack() {
        setCancelResult();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        setCancelResult();
    }

    private void setCancelResult() {
        Intent intent = new Intent();
        if (mAddressModified != null) {
            intent.putExtra("same", true);
            intent.putExtra("address", mAddressModified);
        }
        else if (mTargetAddressDeleted) {
            intent.putExtra("delete", true);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            getAddressList();
            if (requestCode == REQUEST_CODE_MODIFY_ADDRESS) {
                AddressInfo address = (AddressInfo)data.getSerializableExtra("address");
                if (address.id == mAddressSelectedId)
                    mAddressModified = address;
            }
        }
    }

    @AfterViews
    void init ()
    {
        mListView.addHeaderView(new View(this));//第一条item上面的divider一直都在，只是不显示，加一个 head view 就显示出来了，所以加一个空的。
        mAdapter = new AddressAdapter();
        mListView.setAdapter(mAdapter);
        getAddressList();

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem modifyBtn = new SwipeMenuItem(getApplicationContext());
                modifyBtn.setBackground(new ColorDrawable(Color.rgb(0xd9 ,0xd9, 0xd9)));
                modifyBtn.setWidth(160);
                modifyBtn.setTitle(getString(R.string.modify));
                modifyBtn.setTitleSize(18);
                modifyBtn.setTitleColor(Color.rgb(0x23, 0xc4, 0xec));
                menu.addMenuItem(modifyBtn);


                SwipeMenuItem deleteBtn = new SwipeMenuItem(getApplicationContext());
                deleteBtn.setBackground(new ColorDrawable(Color.rgb(0x23, 0xc4, 0xec)));
                deleteBtn.setWidth(160);
                deleteBtn.setTitle(getString(R.string.delete));
                deleteBtn.setTitleSize(18);
                deleteBtn.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteBtn);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    // 修改
                    case 0:
                        AddressInfo address = (AddressInfo)mAdapter.getItem(position);
                        ModifyAddressActivity_.intent(AddressManagementActivity.this)._addressInfo(address).startForResult(REQUEST_CODE_MODIFY_ADDRESS);
//                        open(item);
                        break;
                    // 删除
                    case 1:
                        final CommonDialog dialog = new CommonDialog(AddressManagementActivity.this)
                                .builder().setContent(getString(R.string.affirm_to_delete_address));
                        dialog.setOnCancelClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setOnConfirmClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteAddress(position);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        // other setting
//		listView.setCloseInterpolator(new BounceInterpolator());

        if (mChooseAddress) {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AddressInfo address = mDataList.get(position - mListView.getHeaderViewsCount());
                    Intent intent = new Intent();
                    intent.putExtra("address", address);
                    if (address.id == mAddressSelectedId)
                        intent.putExtra("same", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }


//    @Override
//    protected void onResume ()
//    {
//        super.onResume();
//        getAddressList();
//    }

    @Background
    void getAddressList ()
    {
        _activityHelper.showLoadingDialog(null);
        AddressListResponse resp = _restClient.getAddressList();
        _activityHelper.dismissLoadingDialog();
        updateAddressList(resp);
    }

    @UiThread
    void updateAddressList (AddressListResponse resp)
    {
        if (BaseResponse.hasError(resp))
        {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else
        {
            mDataList = resp.addresslist;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Background
    void deleteAddress (int position)
    {
        BaseResponse resp = _restClient.deleteAddress(mDataList.get(position).id);
        updateDeleteAddress(resp, position, mDataList.get(position).id);
    }

    @UiThread
    void updateDeleteAddress(BaseResponse resp, int position, int deletedId)
    {
        if (BaseResponse.hasError(resp))
        {
            _activityHelper.showToast(BaseResponse.getErrorMessage(resp));
        }
        else
        {
//            mDataList.remove(position);
//            mAdapter.notifyDataSetChanged();
            if (mAddressSelectedId == deletedId) {
                mTargetAddressDeleted = true;
            }
            getAddressList();
        }
    }




    class AddressAdapter extends BaseAdapter {

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
                convertView = _layoutInflater.inflate(R.layout.list_item_address, null);

                holder.isDefault   = (ImageView)convertView.findViewById(R.id.is_default);
                holder.address = (TextView)convertView.findViewById(R.id.address);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            AddressInfo item = mDataList.get(position);

            if (item.isdefault == 1)
                holder.isDefault.setVisibility(View.VISIBLE);
            else if (item.isdefault == 0)
                holder.isDefault.setVisibility(View.GONE);
            String address = "";
//            if (item.addressProvince != null) address += item.addressProvince;
//            if (item.addressCity != null) address += item.addressCity;
//            if (item.addressDistrict != null) address += item.addressDistrict;
//            if (item.addressStreet != null) address += item.addressStreet;
            if (item.addressMain != null) address += item.addressMain;
            if (item.addressSub != null) address += item.addressSub;
            holder.address.setText(address);

            return convertView;
        }

        class ViewHolder
        {
            public ImageView isDefault;
            public TextView address;
        }
    }
}
