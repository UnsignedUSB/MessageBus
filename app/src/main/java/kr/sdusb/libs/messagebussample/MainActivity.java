package kr.sdusb.libs.messagebussample;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import kr.sdusb.libs.messagebus.MessageBus;
import kr.sdusb.libs.messagebus.Subscribe;
import kr.sdusb.libs.messagebus.ThreadType;

public class MainActivity extends AppCompatActivity {

    public static final int GROUP_MAIN = 10;
    public static final int TEST_MESSSAGE_FOR_BLOCK = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MessageBus.getInstance().register(this);
        setContentView(R.layout.activity_main);

        MessageBus.getInstance().takeFirstOnlyWithDuration(GROUP_MAIN, 300);

        Message msg = new Message();
        msg.what = 0;
        msg.arg1 = 0;
        new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                MessageBus.getInstance().handle(GROUP_MAIN, TEST_MESSSAGE_FOR_BLOCK, msg.arg1);
                if(msg.arg1 < 100) {
                    sendMessageDelayed(obtainMessage(0, msg.arg1+1, 0), 20);
                }
            }
        }.sendMessage( msg );
    }

    @Subscribe(events={ TEST_MESSSAGE_FOR_BLOCK }, groups = GROUP_MAIN)
    public void groupBlockTest(int count) {
        Log.d("MainActivity", "groupBlockTest: " + count);
    }

    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.CURRENT)
    public void testCurrentThread() {

    }
    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.MAIN)
    public void testMainThread() {

    }
    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.NEW)
    public void testNewThread() {

    }



    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.CURRENT, ignoreCastException = true)
    public void testCurrentThread_IgnoreCastException(Thread tt) {

    }
    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.MAIN, ignoreCastException = true)
    public void testMainThread_IgnoreCastException(Thread tt) {

    }
    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.NEW, ignoreCastException = true)
    public void testNewThread_IgnoreCastException(Thread tt) {

    }



    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.CURRENT, withEventType=true)
    public void testCurrentThread_withEventType(int what) {

    }
    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.MAIN, withEventType=true)
    public void testMainThread_withEventType(int what) {

    }
    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.NEW, withEventType=true)
    public void testNewThread_withEventType(int what) {

    }



    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.CURRENT, withEventType=true)
    public void testCurrentThread_withEventType_Object(int what, Thread tt) {

    }
    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.MAIN, withEventType=true)
    public void testMainThread_withEventType_Object(int what, Thread tt) {

    }
    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.NEW, withEventType=true)
    public void testNewThread_withEventType_Object(int what, Thread tt) {

    }





    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.CURRENT, withEventType=true, ignoreCastException = true)
    public void testCurrentThread_withEventType_IgnoreCastException(int what, Thread tt) {

    }
    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.MAIN, withEventType=true, ignoreCastException = true)
    public void testMainThread_withEventType_IgnoreCastException(int what, Thread tt) {

    }
    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.NEW, withEventType=true, ignoreCastException = true)
    public void testNewThread_withEventType_IgnoreCastException(int what, Thread tt) {

    }


//    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_WORKER_THREAD, SampleFragment1.MESSAGE_TEXT_UI_THREAD}, thread = ThreadType.MAIN, priority = 2)
//    public void onMessageReceived() {
//        Toast.makeText(this, "Message Received", Toast.LENGTH_SHORT).show();
//    }
//    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, thread = ThreadType.NEW, priority = 3, ignoreCastException = true)
//    public void ignoreClassCastException(Thread thread) {
//        Toast.makeText(this, "Message Received", Toast.LENGTH_SHORT).show();
//    }
//    @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_NEW_THREAD}, withEventType=true)
//    public void withEventType(int what, Thread thread) {
//        Toast.makeText(this, "Message Received", Toast.LENGTH_SHORT).show();
//    }
}
