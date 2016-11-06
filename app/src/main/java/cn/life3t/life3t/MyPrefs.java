package cn.life3t.life3t;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by RexQian on 2014/12/9.
 */
@SharedPref(value= SharedPref.Scope.UNIQUE)
public interface MyPrefs {
    String cookie();

    @DefaultInt(-1)
    int userId();

    String account();

    String password();

    String nickName();

    @DefaultLong(-1)
    long lastActionShowTime();

}
