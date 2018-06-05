package scriddle.dooble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

public class HostGameActivity extends AppCompatActivity
        implements
        HostBeginFragment.BeginScreenListener,
        HostSelectTopicFragment.TopicListener,
        HostJudgeFragment.JudgeListener {

    //THE CODES FOR THE STRING TEXTS
    public static final String IMAGE_CODE = "hdfgwekkjfer;nferfjdwjdwdk";
    public static final String TEXT_CODE = "765e67it732yieuhfknnpb83yuf3d";

    private static final String mSpecialCode1 = "MdukgsfHDSJFVkbkadfh";
    private static final String mSpecialCode2 = "SIGFDYUEbcdnshdgSYDF";

    private final static int TEXT_MODE = 1;
    private final static int IMAGE_MODE = 2;

    Bitmap[] Bitmaps;
    int countOfImgsRecieved;

    boolean Player1Sending;
    boolean Player2Sending;

    int mode;

    private static final String TAG = "HostGameActivity";
    private android.support.v4.app.FragmentManager fragmentManager;
    HostBeginFragment beginFragment;
    HostSelectTopicFragment selectTopicFragment;
    HostJudgeFragment judgeFragment;

    BluetoothAdapter mBluetoothAdapter;
    GameConnection mGameConnection;

    private static final UUID mUUID  = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static final UUID m2UUID = UUID.fromString("fdd51efb-0a10-4d27-bd82-1ef5451d8b8a");

    public ArrayList <GameConnection> Players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        fragmentManager = getSupportFragmentManager();
        beginFragment = new HostBeginFragment();
        selectTopicFragment = new HostSelectTopicFragment();
        judgeFragment = new HostJudgeFragment();
        Players = new ArrayList<>();
        Bitmaps = new Bitmap[2];
        Bitmaps[0] = null;
        Bitmaps[1] = null;

        beginScreen();
        mode = TEXT_MODE;
        Player1Sending = false;
        Player2Sending = false;
        countOfImgsRecieved = 0;

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));
        /*Intent imageIntent = new Intent();
        imageIntent.setDataAndType(); */
        LocalBroadcastManager.getInstance(this).registerReceiver(mP1ImageReceiver, new IntentFilter("Player1Image"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mP2ImageReceiver, new IntentFilter("Player2Image"));
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

    public void setSelectTopicFragment()
    {
        // begin transaction
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace container with new fragment
        fragmentTransaction.replace(R.id.placeholder, selectTopicFragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
        while(!selectTopicFragment.isAdded()){}
    }

    public void onReadySelected() {

        //cancel discovery
        if(mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();

        setSelectTopicFragment();
    }

    public void deviceSelected(String deviceName, String deviceAddress, BluetoothDevice device) {
        Log.d(TAG, "onItemClick: You Clicked on a device.");
        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){

            Log.d(TAG, "Trying to pair with " + deviceName);
            device.createBond();
            if (Players.size() < 1)
                mGameConnection = new GameConnection.Builder()
                        .setUUID(mUUID)
                        .setContext(HostGameActivity.this)
                        .setIdentity(0)
                        .create();
            else
                mGameConnection = new GameConnection.Builder()
                        .setUUID(m2UUID)
                        .setContext(HostGameActivity.this)
                        .setIdentity(0)
                        .create();
            Log.d(TAG, "Added a connection");
            Players.add(mGameConnection);
        }

    }

    public void sendWinner (int winner)
    {
        String sendMe = "winner" + String.valueOf(winner);

        byte[] textModeBytes = TEXT_CODE.getBytes(Charset.defaultCharset());
        int ret0 = 0;
        int ret1 = 0;

        ret0 = Players.get(0).write(textModeBytes);
        ret1 = Players.get(1).write(textModeBytes);

        while ((ret0 != 1) && (ret1 != 1)){ ; }
        //while (ret0 != 1){ ; }

        mode = TEXT_MODE;
        byte[] bytes = sendMe.getBytes(Charset.defaultCharset());

        ret0 = 0;
        ret1 = 0;
        ret0 = Players.get(0).write(bytes);
        ret1 = Players.get(1).write(bytes);

        while ((ret0 != 1) && (ret1 != 1)){ ; }
        setSelectTopicFragment();
    }

    public void onSendTopic(String topic)
    {
        String sendMe = "topic" + topic;

        byte[] textModeBytes = TEXT_CODE.getBytes(Charset.defaultCharset());
        int ret0 = 0;
        int ret1 = 0;

        ret0 = Players.get(0).write(textModeBytes);
        ret1 = Players.get(1).write(textModeBytes);

        while ((ret0 != 1) && (ret1 != 1)){ ; }
        //while (ret0 != 1){ ; }

        mode = TEXT_MODE;
        byte[] bytes = sendMe.getBytes(Charset.defaultCharset());

        ret0 = 0;
        ret1 = 0;
        ret0 = Players.get(0).write(bytes);
        ret1 = Players.get(1).write(bytes);

        while ((ret0 != 1) && (ret1 != 1)){ ; }
        //while (ret0 != 1){ ; }

        /*
        for (int i = 0; i < Players.size(); i++)
        {
            Players.get(i).write(bytes);
        }*/
    }

    // sets screen to initial fragment
    void beginScreen() {
        // begin transaction
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace container with new fragment
        fragmentTransaction.replace(R.id.placeholder, beginFragment);
        fragmentTransaction.commit();
    }

    //RECEIVER
    BroadcastReceiver mP1ImageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] bytes = intent.getByteArrayExtra("p1Image");
            Log.d(TAG, "Received image from Player1: " + bytes.length);
            Log.d(TAG, String.valueOf(bytes));
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmaps[0] = bm;
            Player1Sending = false;
            countOfImgsRecieved++;

        }
    };

    BroadcastReceiver mP2ImageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] bytes = intent.getByteArrayExtra("p2Image");
            Log.d(TAG, "Received image from Player2: " + bytes.length);
            Log.d(TAG, String.valueOf(bytes));
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            Bitmaps[1] = bm;
            Player2Sending = false;
            countOfImgsRecieved++;
            goToJudgingMode();
        }
    };

    public void goToJudgingMode()
    {
        //if (countOfImgsRecieved != 2) return;

        setJudgingFragment();
        while(!judgeFragment.isAdded()){}
        judgeFragment.setJudgeImages(Bitmaps);

    }

    public void setJudgingFragment()
    {
        // begin transaction
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace container with new fragment
        fragmentTransaction.replace(R.id.placeholder, judgeFragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }


    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");
            if (!text.equals("Special Text Mode")){
                if (text.equals(mSpecialCode1))
                {
                    Player1Sending = true;
                    Log.d(TAG, "Player 1 sending");
                }
                else if (text.equals(mSpecialCode2))
                {
                    Player2Sending = true;
                    Log.d(TAG, "Player 2 sending");
                }
                else
                    Log.d(TAG, "Text was: " + text);
                //messages.append(text + '\n');
                //incomingMessages.setText(messages);
            } else{
                Log.d(TAG, "Text was: " + text);
            }

        }
    };
}
