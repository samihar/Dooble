package scriddle.dooble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.nio.charset.Charset;

public class HostSelectTopicFragment extends Fragment {

    TopicListener mCallback;

    public interface TopicListener {
        void onSendTopic(String topic);
    }

    RelativeLayout mRelativeLayout;

    EditText etTopic;
    Button btnGo;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_host_select_topic, parent, false);
        return mRelativeLayout;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        etTopic = (EditText) mRelativeLayout.findViewById(R.id.etEnterTopic);
        btnGo = (Button) mRelativeLayout.findViewById(R.id.btnGo);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = etTopic.getText().toString();
                //byte[] bytes = etTopic.getText().toString().getBytes(Charset.defaultCharset());
                Log.d("TAG", topic);
                etTopic.setText("");
                mCallback.onSendTopic(topic);
                //mBluetoothConnection.write(bytes);
            }
        });
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (TopicListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}
