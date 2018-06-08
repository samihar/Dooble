package scriddle.dooble;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class PlayerScoreFragment extends Fragment{
    RelativeLayout mRelativeLayout;
    TextView tvUpdate;
    TextView tvScore;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_player_score, parent, false);
        return mRelativeLayout;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        tvUpdate = (TextView) mRelativeLayout.findViewById(R.id.tvUpdate);
        tvScore = (TextView) mRelativeLayout.findViewById(R.id.tvScore);


    }

    public void updateScore(String update, int score)
    {
        Log.d(TAG, "Update: " + update + " " + score);
        tvUpdate.setText(update);
        tvScore.setText(Integer.toString(score));
    }
}