package kr.sdusb.libs.messagebussample;

import android.util.Log;

import kr.sdusb.libs.messagebus.Subscribe;

/**
 * Created by Yoosub-Song on 2018-12-13.
 */
public class SubTestModel extends BaseTestModel<Integer> {

    public static final int MESSAGE_SUB = 20000;
    @Subscribe(events={ MESSAGE_SUB })
    public void onSub() {
        Log.d("SubTestModel", "onSub: ");
    }
}
