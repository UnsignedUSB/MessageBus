package kr.sdusb.libs.messagebussample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import kr.sdusb.libs.messagebus.MessageBus;

/**
 *
 * Created by Yoosub-Song on 2017-07-30 오후 2:43.
 */
public class SampleFragment1 extends Fragment {

    public static final int MESSAGE_TEXT_UI_THREAD = 0x00000001;
    public static final int MESSAGE_TEXT_WORKER_THREAD = 0x00000002;

    private EditText editText;
    private Button mainThreadButton;
    private Button workerThreadButton;

    public SampleFragment1() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_sample_fragment_1, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editText = getView().findViewById(R.id.editText);
        mainThreadButton = getView().findViewById(R.id.button);
        workerThreadButton = getView().findViewById(R.id.button2);

        mainThreadButton.setOnClickListener(onClickListener);
        workerThreadButton.setOnClickListener(onClickListener);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(editText.getText() == null) {
                return;
            }
            switch (view.getId()) {
                case R.id.button:
                    String text = editText.getText().toString();
                    MessageBus.getInstance().handle(MESSAGE_TEXT_UI_THREAD, text);
                    break;
                case R.id.button2:
                    new Thread() {
                        @Override
                        public void run() {
                            MessageBus.getInstance().handle(MESSAGE_TEXT_WORKER_THREAD, editText.getText().toString());
                        }
                    }.start();
                    break;
            }
        }
    };
}
