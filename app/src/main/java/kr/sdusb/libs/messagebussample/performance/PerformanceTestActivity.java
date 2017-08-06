package kr.sdusb.libs.messagebussample.performance;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import kr.sdusb.libs.messagebus.MessageBus;
import kr.sdusb.libs.messagebus.Subscribe;
import kr.sdusb.libs.messagebussample.R;

/**
 *
 * Created by Yoosub-Song on 2017-08-01 오후 4:10.
 */
public class PerformanceTestActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int MESSAGE_TEST = 0x00010001;

    private Button messageBusTest;
    private Button eventBusTest;

    private Handler handler;

    private long deltaSum = 0;
    private int currentCount = 0;
    private final int testCount = 5000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_test);

        messageBusTest = (Button) findViewById(R.id.messageBusTest);
        eventBusTest = (Button) findViewById(R.id.eventBusTest);

        messageBusTest.setOnClickListener(this);
        eventBusTest.setOnClickListener(this);

        handler = new Handler();
        MessageBus.getInstance().register(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MessageBus.getInstance().unregister(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        deltaSum = 0;
        currentCount = 0;

        messageBusTest.setEnabled(false);
        eventBusTest.setEnabled(false);
        switch (view.getId()) {
            case R.id.messageBusTest:
                sendMessageToMessageBus();
                break;
            case R.id.eventBusTest:
                sendMessageToEventBus();
                break;
        }
    }


    @Subscribe(events = MESSAGE_TEST)
    public void onMessageBusReceived(EventBusModel model) {
        deltaSum += System.nanoTime() - model.startTimeNano;
        currentCount++;
        if(currentCount < testCount) {
            sendMessageToMessageBus();
            ((TextView)findViewById(R.id.textView)).setText("MessageBus : " + currentCount + ",  " + deltaSum);
        } else {
            ((TextView)findViewById(R.id.resultTV)).append("MessageBus : " + deltaSum + "\n");
            messageBusTest.setEnabled(true);
            eventBusTest.setEnabled(true);
        }
    }
    private void sendMessageToMessageBus() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBusModel model = new EventBusModel();
                model.startTimeNano = System.nanoTime();
                MessageBus.getInstance().handle(MESSAGE_TEST, model);
            }
        }, 20);
    }


    @org.greenrobot.eventbus.Subscribe
    public void onEventBusReceived(EventBusModel model) {
        deltaSum += System.nanoTime() - model.startTimeNano;
        currentCount++;
        if(currentCount < testCount) {
            sendMessageToEventBus();
            ((TextView)findViewById(R.id.textView)).setText("EventBus : " + currentCount + ",  " + deltaSum);
        } else {
            ((TextView)findViewById(R.id.resultTV)).append("EventBus : " + deltaSum + "\n");
            messageBusTest.setEnabled(true);
            eventBusTest.setEnabled(true);
        }
    }
    private void sendMessageToEventBus() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBusModel model = new EventBusModel();
                model.startTimeNano = System.nanoTime();
                EventBus.getDefault().post(model);
            }
        }, 20);
    }
}
