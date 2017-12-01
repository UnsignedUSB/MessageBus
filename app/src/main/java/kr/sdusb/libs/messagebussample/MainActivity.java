package kr.sdusb.libs.messagebussample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import kr.sdusb.libs.messagebus.MessageBus;
import kr.sdusb.libs.messagebus.Subscribe;
import kr.sdusb.libs.messagebus.ThreadType;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MessageBus.getInstance().register(this);
        setContentView(R.layout.activity_main);
    }

    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_WORKER_THREAD, SampleFragment1.MESSAGE_TEXT_UI_THREAD}, thread = ThreadType.MAIN, priority = 2)
    public void onMessageReceived() {
        Toast.makeText(this, "Message Received", Toast.LENGTH_SHORT).show();
    }
    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.NEW, priority = 2)
    public void testNewThread() throws Exception{
        Toast.makeText(this, "Message Received", Toast.LENGTH_SHORT).show();
    }
    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.NEW, priority = 3, ignoreCastException = true)
    public void ignoreClassCastException(Thread thread) {
        Toast.makeText(this, "Message Received", Toast.LENGTH_SHORT).show();
    }
}
