package scriddle.dooble;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HostWaitFragment extends Fragment{
    RelativeLayout mRelativeLayout;
    TextView tvPlayer1Score;
    TextView tvPlayer2Score;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_host_wait, parent, false);
        return mRelativeLayout;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        tvPlayer1Score = (TextView) mRelativeLayout.findViewById(R.id.tvPlayer1Score);
        tvPlayer2Score = (TextView) mRelativeLayout.findViewById(R.id.tvPlayer2Score);
    }

    public void updateScores(int[] scores){
        tvPlayer1Score.setText(String.valueOf(scores[0]));
        tvPlayer2Score.setText(String.valueOf(scores[1]));
    }
}
