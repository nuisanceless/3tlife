package cn.life3t.life3t.account;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.MyApplication;
import cn.life3t.life3t.R;
import cn.life3t.life3t.main.BaseActivity;

/**
 * Created by wuguohao on 15-5-18.
 */
@EActivity(R.layout.activity_about_us)
public class AboutUsActivity extends BaseActivity
{
    @ViewById(R.id.version_name)
    TextView versionName;

    @ViewById(R.id.official_web_url)
    TextView officialWebUrl;

    @ViewById(R.id.service_tel_num)
    TextView serviceTelNum;


    @AfterViews
    void init ()
    {
        versionName.setText(getString(R.string.version_name) + mApp.getVersion());

        officialWebUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + officialWebUrl.getText().toString()));
                startActivity(intent);
            }
        });

        serviceTelNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + serviceTelNum.getText().toString()));
                startActivity(intent);
            }
        });
    }
}
