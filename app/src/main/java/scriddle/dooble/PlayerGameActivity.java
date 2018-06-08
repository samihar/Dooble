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
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.nio.charset.Charset;
import java.util.UUID;

public class PlayerGameActivity extends AppCompatActivity
        implements
        PlayerBeginFragment.BeginScreenListener,
        PlayerMainFragment.PaintScreenListener {

    private static final String TAG = "PlayerGameActivity";
    private android.support.v4.app.FragmentManager fragmentManager;
    PlayerBeginFragment beginFragment;
    PlayerMainFragment mainFragment;
    PlayerScoreFragment scoreFragment;
    PlayerWaitFragment waitFragment;

    GameConnection mGameConnection;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mDevice;

    private static UUID mUUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static final UUID m1UUID  = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static final UUID m2UUID = UUID.fromString("fdd51efb-0a10-4d27-bd82-1ef5451d8b8a");

    private String mSpecialCode;
    private static final String mSpecialCode1 = "MdukgsfHDSJFVkbkadfh";
    private static final String mSpecialCode2 = "SIGFDYUEbcdnshdgSYDF";

    public static final String IMAGE_CODE = "hdfgwekkjfer;nferfjdwjdwdk";
    public static final String TEXT_CODE = "765e67it732yieuhfknnpb83yuf3d";
    int mplayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_game);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));
        /*Intent imageIntent = new Intent();
        imageIntent.setDataAndType(); */
        LocalBroadcastManager.getInstance(this).registerReceiver(mImageReceiver, new IntentFilter("incomingImage"));

        mScore = 0;
        mplayer = getIntent().getIntExtra("player_num", 1);
        if (mplayer == 1) {
            mUUID = m1UUID;
            mSpecialCode = mSpecialCode1;
            Log.d(TAG, "using UUID 1");
        }
        else {
            mUUID = m2UUID;
            mSpecialCode = mSpecialCode2;
            Log.d(TAG, "using UUID 2");
        }

        fragmentManager = getSupportFragmentManager();
        beginFragment = new PlayerBeginFragment();
        mainFragment = new PlayerMainFragment();
        scoreFragment = new PlayerScoreFragment();
        waitFragment = new PlayerWaitFragment();

        beginScreen();
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

    // sets screen to initial fragment
    void beginScreen() {
        // begin transaction
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace container with new fragment
        fragmentTransaction.replace(R.id.placeholder, beginFragment);
        fragmentTransaction.commit();
    }

    public void onConnectSelected() {
        //cancel discovery
        if(mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        mGameConnection.startClient(mDevice,mUUID);

        setWaitFragment();

    }

    public void setWaitFragment() {
        // begin transaction
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace container with new fragment
        fragmentTransaction.replace(R.id.placeholder, waitFragment);
        fragmentTransaction.commit();
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
            mDevice=device;
            mGameConnection = new GameConnection.Builder()
                    .setUUID(mUUID)
                    .setContext(PlayerGameActivity.this)
                    .setIdentity(mplayer)
                    .create();
        }

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called");
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterReceiver(mImageReceiver);
    }

    public void onSendSelected(byte[] imgSend)
    {

        int ret = 0;

        /*
        byte[] textModeBytes = TEXT_CODE.getBytes(Charset.defaultCharset());
        ret = mGameConnection.write(textModeBytes);
        while (ret != 1){ ; }

        ret = 0;
        byte[] codeBytes = mSpecialCode.getBytes(Charset.defaultCharset());
        ret = mGameConnection.write(codeBytes);
        while (ret != 1){ ; }
        */



        ret = 0;
        byte[] imageModeBytes = (IMAGE_CODE + mSpecialCode).getBytes(Charset.defaultCharset());
        ret = mGameConnection.write(imageModeBytes);
        while (ret != 1){ ; }


        ret = 0;
        Log.d(TAG, String.valueOf(imgSend.length));
        Log.d(TAG, String.valueOf(imgSend));
        ret = mGameConnection.write(imgSend);
        while (ret != 1){ ; }
        setWaitFragment();


    }

    //RECIVER
    BroadcastReceiver mImageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] bytes = intent.getByteArrayExtra("theImage");
            Log.d(TAG, "Received image: " + bytes.length);
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            //incomingImage.setImageBitmap(bm);

            /*messages.append(text+'\n');
            incomingMessages.setText(messages);*/
        }
    };

    int winner;
    int mScore;

    public void setScore() {
        String updateWinner = "Awww, get 'em next time!";
        if (winner == mplayer) {
            mScore++;
            updateWinner = "You Won!";
        }
        // begin transaction
        Log.d(TAG, updateWinner + " " + mScore);
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace container with new fragment
        fragmentTransaction.replace(R.id.placeholder, scoreFragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
        while (!scoreFragment.isAdded()){}
        scoreFragment.updateScore(updateWinner, mScore);

    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");
            String topic;
            Log.d(TAG, "Received message: " + text);
            if (!text.equals("Special Text Mode")){
                if (text.contains("topic"))
                {
                    topic = text.substring(text.lastIndexOf("topic")+(5));
                    setMainFragment();
                    setTopic(topic);
                }
                else if (text.contains("winner")){
                    winner = Integer.valueOf(text.substring(text.lastIndexOf("winner")+(6)));
                    setScore();
                }
            } else{
                Log.d(TAG, "Text was: " + text);
            }

        }
    };

    public void setMainFragment() {
        // begin transaction
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace container with new fragment
        fragmentTransaction.replace(R.id.placeholder, mainFragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
        while(!mainFragment.isAdded()){}
    }


    public void setTopic(String topic) {
        mainFragment.settvScore("Score: " + String.valueOf(mScore));
        mainFragment.settvTopc(topic);
    }
}
