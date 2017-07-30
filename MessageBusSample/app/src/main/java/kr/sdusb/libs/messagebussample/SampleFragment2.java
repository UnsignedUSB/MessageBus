package kr.sdusb.libs.messagebussample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kr.sdusb.libs.messagebus.MessageBus;
import kr.sdusb.libs.messagebus.Subscribe;

/**
 *
 * Created by Yoosub-Song on 2017-07-30 오후 2:43.
 */
public class SampleFragment2 extends Fragment {

    private TextView textView;

    public SampleFragment2() {
        MessageBus.getInstance().register(this);
    }

@Override
public void onDestroy() {
    super.onDestroy();
    MessageBus.getInstance().unregister(this);
}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_sample_fragment_2, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textView = getView().findViewById(R.id.textView);
    }

    @Subscribe(events = SampleFragment1.MESSAGE_TEXT_UI_THREAD)
    public void onMessageChanged_UIThread(String message) {
        textView.append(message+"\n");
    }

    @Subscribe(events = SampleFragment1.MESSAGE_TEXT_WORKER_THREAD, thread = Subscribe.Thread.MAIN)
    public void onMessageChanged_WorkerThread(String message) {
        textView.append(message+"\n");
    }
}
