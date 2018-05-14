package scriddle.dooble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;


/**
 * Created by gracey on 5/13/18.
 */

public class BluetoothChatService {
    private static final String TAG = "BluetoothChatServ";

    private static final String appName = "Dooble";

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter myBluetoothAdapter;
    Context myContext;

    private AcceptThread myInsecureAcceptThread;

    public BluetoothChatService(Context context) {
        //this.myBluetoothAdapter = ;
        //this.myContext = context;
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        myContext = context;
    }

    // thread that sits there waiting for someone to connect
    private class AcceptThread extends Thread {
        // make a local server socket
        private final BluetoothServerSocket myServerSocket;

        public AcceptThread () {
            BluetoothServerSocket temp = null;

            // make a listening server socket
            try {
                temp = myBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }
            myServerSocket = temp;
        }

        public void run () {
            Log.d(TAG, "run: AcceptThread is running!!");

            BluetoothSocket socket = null;

            try {
                // this call will block the thread until a successful connection/exception, where it will return
                Log.d(TAG, "run: RFCOM server socket start haha");
                socket = myServerSocket.accept();

                // this won't print until a connection is accepted
                Log.d(TAG, "run: RFCOM server socket accepted a connection");

            } catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            // ADDRESS IN THIRD VIDEO
            if (socket != null) {
                //connected(socket, myDevice);
            }

            Log.i(TAG, "END acceptThread");

        }

        public void cancel () {
            Log.d(TAG, "cancel: cancelling acceptThread");
            try {
                myServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }
        }
    }
}