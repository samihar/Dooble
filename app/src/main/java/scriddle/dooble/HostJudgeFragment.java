package scriddle.dooble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.nio.charset.Charset;

public class HostJudgeFragment extends Fragment {

    JudgeListener mCallback;

    public interface JudgeListener {
        void sendWinner(int winner);
    }

    RelativeLayout mRelativeLayout;

    ImageButton player1;
    ImageButton player2;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_host_judge, parent, false);
        return mRelativeLayout;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        player1 = (ImageButton) mRelativeLayout.findViewById(R.id.imgBtnPlayer1);
        player2 = (ImageButton) mRelativeLayout.findViewById(R.id.imgBtnPlayer2);

        player1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.sendWinner(1);
            }
        });

        player2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.sendWinner(2);
            }
        });
    }

    public void setJudgeImages(Bitmap imgs[])
    {
        Bitmap defaultIm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        //player1.setImageBitmap(defaultIm);
        player1.setImageBitmap(imgs[0]);
        player2.setImageBitmap(imgs[1]);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (JudgeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}
