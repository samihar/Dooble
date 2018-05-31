package scriddle.dooble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.UUID;


public class PlayerGameActivity extends AppCompatActivity
        implements
        PlayerBeginFragment.BeginScreenListener,
        PlayerMainFragment.PaintScreenListener {

    private static final String TAG = "PlayerGameActivity";
    private android.support.v4.app.FragmentManager fragmentManager;
    PlayerBeginFragment beginFragment;
    PlayerMainFragment mainFragment;

    GameConnection mGameConnection;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mDevice;

    private static UUID mUUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static final UUID m1UUID  = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static final UUID m2UUID = UUID.fromString("fdd51efb-0a10-4d27-bd82-1ef5451d8b8a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_game);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        int mplayer = getIntent().getIntExtra("player_num", 1);
        if (mplayer == 1) {
            mUUID = m1UUID;
            Log.d(TAG, "using UUID 1");
        }
        else {
            mUUID = m2UUID;
            Log.d(TAG, "using UUID 2");

        }

        fragmentManager = getSupportFragmentManager();
        beginFragment = new PlayerBeginFragment();
        mainFragment = new PlayerMainFragment();

        beginScreen();
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

        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        //mGameConnection.startClient(mDevice,mUUID);

        // begin transaction
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace container with new fragment
        fragmentTransaction.replace(R.id.placeholder, mainFragment);
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
                    .create();
        }

    }

    public void onSendSelected()
    {

    }
}
