package scriddle.dooble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;


public class HostGameActivity extends AppCompatActivity
        implements
        HostBeginFragment.BeginScreenListener {

    private static final String TAG = "HostGameActivity";
    private android.support.v4.app.FragmentManager fragmentManager;
    HostBeginFragment beginFragment;
    HostMainFragment mainFragment;

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
        mainFragment = new HostMainFragment();
        Players = new ArrayList<>();

        beginScreen();

    }

    public void onReadySelected() {
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
            if (Players.size() < 1)
                mGameConnection = new GameConnection.Builder()
                        .setUUID(mUUID)
                        .setContext(HostGameActivity.this)
                        .create();
            else
                mGameConnection = new GameConnection.Builder()
                        .setUUID(m2UUID)
                        .setContext(HostGameActivity.this)
                        .create();
            Log.d(TAG, "Added a connection");
            Players.add(mGameConnection);
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
}
