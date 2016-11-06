package cn.life3t.life3t.main;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import cn.life3t.life3t.R;

/**
 * Created by Lei on 2015/7/2.
 */
@EActivity(R.layout.activity_welcome)
public class WelcomeActivity extends Activity {

    @ViewById(R.id.windmill)
    ImageView mWindmill;

    @AfterViews
    void afterViews() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.windmill_rotate);
        LinearInterpolator li = new LinearInterpolator();
        animation.setInterpolator(li);
        mWindmill.setAnimation(animation);
        finishDelay();
    }

    @UiThread(delay = 3000)
    void finishDelay() {
        finish();
    }
}
