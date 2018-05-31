package scriddle.dooble;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BEGIN_DISCOVERY = 1;

    BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        enableDisableBT();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            // Log.i(TAG, "Bluetooth not supported");
            // Show proper message here
            finish();
        }
        while (!mBluetoothAdapter.isEnabled()){}
        enableDiscoverable();

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called");
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver1);
    }


    public void createGame(View v){
        while (!mBluetoothAdapter.isEnabled()){}
        if (mBluetoothAdapter.isEnabled()) {
            Intent i = new Intent(
                    MainActivity.this,
                    HostGameActivity.class);
            startActivity(i);
        }
    }

    public void joinGame(View v){
        while (!mBluetoothAdapter.isEnabled()){}
        if (mBluetoothAdapter.isEnabled()) {
            Intent i = new Intent(
                    MainActivity.this,
                    PlayerSelectActivity.class);
            startActivity(i);
        }
    }

    public void enableDisableBT() {
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            //Log.i(TAG, "Bluetooth not supported");
            // Show proper message here
            finish();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(myBroadcastReceiver1, BTIntent);
        }

    }

    // Create a BroadcastReceiver for enabling Bluetooth
    // Catches state changes while enabling Bluetooth
    private final BroadcastReceiver myBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                // 4 states: turning on, on, turning off, off
                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    public void enableDiscoverable() {
        Log.d(TAG, "Device will be discoverable for 300 seconds");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoverableIntent, REQUEST_BEGIN_DISCOVERY);

        IntentFilter DiscoveryIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(discoverableBroadcastReceiver, DiscoveryIntentFilter);
    }

    // BroadcastReceiver for discovering.
    private final BroadcastReceiver discoverableBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                final int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                // 4 states: turning on, on, turning off, off
                switch(mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "Device is connectable and discoverable");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "Device is disconnected but able to receive connections");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "Device is connecting");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "Device is connected");
                        break;
                }
            }
        }
    };

}
