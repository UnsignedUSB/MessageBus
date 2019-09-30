package kr.sdusb.libs.messagebussample;

import android.util.Log;

import kr.sdusb.libs.messagebus.Subscribe;

/**
 * Created by Yoosub-Song on 2018-12-13.
 */
public class BaseTestModel<T> {
    public static final int MESSAGE_SUPER = 10000;

    @Subscribe(events={ MESSAGE_SUPER  })
    public void onSuper() {
        Log.d("BaseTestModel", "onSuper: ");
    }
}
