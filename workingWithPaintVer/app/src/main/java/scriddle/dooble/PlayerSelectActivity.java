package scriddle.dooble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
                mplayer = 1;
            }
        });

        btnPlayer2 = (Button) findViewById(R.id.btnPlayer2);
        btnPlayer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected2 = !selected2;
                v.setSelected(selected2);
                mplayer = 2;
            }
        });
    }

    public void findHost(View view) {
        Intent i =new Intent(PlayerSelectActivity.this, PlayerGameActivity.class);
        i.putExtra("player_num", mplayer);
        startActivity(i);
    }
}
