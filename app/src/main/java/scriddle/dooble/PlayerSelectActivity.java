package scriddle.dooble;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class PlayerSelectActivity extends AppCompatActivity {

    private int mplayer = 1;
    private Button btnPlayer1;
    private Button btnPlayer2;
    boolean selected1 = false;
    boolean selected2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_select);

        btnPlayer1 = (Button) findViewById(R.id.btnPlayer1);
        btnPlayer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected1 = !selected1;
                v.setSelected(selected1);
            }
        });

        btnPlayer2 = (Button) findViewById(R.id.btnPlayer2);
        btnPlayer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected2 = !selected2;
                v.setSelected(selected2);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // HIDE STATUS BAR
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else
        {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void findHost(View view) {
        if (selected1)
            mplayer = 1;
        else
            mplayer = 2;
        Intent i =new Intent(PlayerSelectActivity.this, PlayerGameActivity.class);
        i.putExtra("player_num", mplayer);
        startActivity(i);


    }
}
