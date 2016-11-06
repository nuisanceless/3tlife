package cn.life3t.life3t.account;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;
import cn.life3t.life3t.common.BaseFragmentActivity;
import cn.life3t.life3t.order.OrderFragment_;

/**
 * Created by wuguohao on 15-5-18.
 */
@EActivity(R.layout.activity_order_management)
public class OrderManagementActivity extends BaseFragmentActivity
{
    public static final String ORDER_FRAGMENT_TYPE = "in_progress_or_finished";
    public static final int ORDER_FRAGMENT_IN_PROGRESS = 1;
    public static final int ORDER_FRAGMENT_FINISHED = 2;


    Fragment mFragmentCur;
    Fragment mInProgressFragment = new OrderFragment_();
    Fragment mFinishedFragment = new OrderFragment_();

    FragmentManager mFragmentManager;



    @ViewById(R.id.radioGroup)
    RadioGroup mRadioGroup;

    @Click(R.id.btn_back)
    void back ()
    {
        finish();
    }

    @AfterViews
    void init ()
    {
        Bundle inProgressBundle = new Bundle();
        inProgressBundle.putInt(ORDER_FRAGMENT_TYPE, ORDER_FRAGMENT_IN_PROGRESS);
        mInProgressFragment.setArguments(inProgressBundle);

        Bundle finishedBundle = new Bundle();
        finishedBundle.putInt(ORDER_FRAGMENT_TYPE, ORDER_FRAGMENT_FINISHED);
        mFinishedFragment.setArguments(finishedBundle);

        mFragmentCur = mInProgressFragment;
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().add(R.id.frame_container, mInProgressFragment).commit();


        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment to = getFragmentByCheckId(checkedId);
                switchFragment(mFragmentCur, to);
            }
        });
    }

    Fragment getFragmentByCheckId (int checkedId)
    {
        switch (checkedId)
        {
            case R.id.in_progress_rbtn:
                return mInProgressFragment;
            case R.id.finished_rbtn:
                return mFinishedFragment;
            default:
                return null;
        }
    }

    void switchFragment (Fragment from, Fragment to)
    {
        if (mFragmentCur != to)
        {
            mFragmentCur = to;
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (!to.isAdded()) {
                transaction.hide(from).add(R.id.frame_container, to).commit();
            }
            else {
                transaction.hide(from).show(to).commit();
            }
        }
    }
}
