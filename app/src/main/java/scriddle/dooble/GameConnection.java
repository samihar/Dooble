package scriddle.dooble;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;
import java.util.UUID;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;
import android.app.ProgressDialog;
import android.support.v4.content.LocalBroadcastManager;

public class GameConnection {

    private static final int bufSize = 5000;

    private static final String TAG = "GameConnection";
    private static UUID mUUID;//  = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    //private static final UUID m2UUID = UUID.fromString("88d30118-60d2-4bc2-a54f-238b1a369fc8");

    //private static final UUID m1UUID  = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    //private static final UUID m2UUID = UUID.fromString("fdd51efb-0a10-4d27-bd82-1ef5451d8b8a");

    //THE CODES FOR THE STRING TEXTS
    public static final String IMAGE_CODE = "hdfgwekkjfer;nferfjdwjdwdk";
    public static final String TEXT_CODE = "765e67it732yieuhfknnpb83yuf3d";

    private static final String mSpecialCode1 = "MdukgsfHDSJFVkbkadfh";
    private static final String mSpecialCode2 = "SIGFDYUEbcdnshdgSYDF";

    private static final String appName = "dooble";

    private int mIdentity;


    public boolean success;

    Context mContext;
    ProgressDialog mProgressDialog;
    private UUID deviceUUID;

    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;

    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    //MODES
    private final static int NO_MODE = 0;
    private final static int TEXT_MODE = 1;
    private final static int IMAGE_MODE = 2;
    int send_mode;

    private final static int HOST = 3;
    private final static int PLAYER1 = 4;
    private final static int PLAYER2 = 5;
    int sender;
    /*
    private ArrayList<String> mDeviceAddresses;
    private ArrayList<ConnectedThread> mThreads;
    private ArrayList<BluetoothSocket> mSockets;
    private ArrayList<UUID> mUUIDs;
    */

    static class Builder {
        private UUID mmUUID;
        private Context mmContext;
        private int mmIdentity;

        public Builder setUUID(final UUID bUUID) {
            this.mmUUID = bUUID;
            return this;
        }
        public Builder setContext(final Context bContext) {
            this.mmContext = bContext;
            return this;
        }
        public Builder setIdentity(final int bIdentity) {
            this.mmIdentity = bIdentity;
            return this;
        }

        public GameConnection create() {
            return new GameConnection(this);
        }
    }

    private GameConnection(final Builder builder) {
        mUUID = builder.mmUUID;
        mContext = builder.mmContext;
        mIdentity = builder.mmIdentity;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        success = false;
        send_mode = TEXT_MODE;
        sender= 0;
        start();
    }

    public void setMode(int m_set){
        send_mode = m_set;
    }

    public int getMode(){
        return send_mode;
    }

    /*
    public GameConnection(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //mUUID = inUUID;

        /*
        mDeviceAddresses = new ArrayList<String>();
        mThreads = new ArrayList<ConnectedThread>();
        mSockets = new ArrayList<BluetoothSocket>();
        mUUIDs = new ArrayList<UUID>();

        mUUIDs.add(UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc"));
        mUUIDs.add(UUID.fromString("2d64189d-5a2c-4511-a074-77f199fd0834"));
        mUUIDs.add(UUID.fromString("e442e09a-51f3-4a7b-91cb-f638491d1412"));

        start();
    }
*/
    // Thread will run while listening for incoming connections
    // Runs until a connection is accepted/cancelled
    private class AcceptThread extends Thread {

        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, mUUID);
                Log.d(TAG, "AcceptThread: Setting up Server using: " + mUUID);
            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            mmServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "run: AcceptThread Running.");

            BluetoothSocket socket = null;

            try{
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.d(TAG, "run: RFCOM server socket start.....");

                socket = mmServerSocket.accept();
                success = true;

                Log.d(TAG, "run: RFCOM server socket accepted connection.");

            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            //talk about this is in the 3rd
            if(socket != null){
                connected(socket,mDevice);
            }

            Log.i(TAG, "END mAcceptThread ");
        }

        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage() );
            }
        }

    }


    // Runs while attempting to make an outgoing connection with a device
    // Runs straight through, connection either succeeds or fails
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        + mUUID );
                tmp = mDevice.createRfcommSocketToServiceRecord(mUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
                success = true;

                Log.d(TAG, "run: ConnectThread connected.");
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + mUUID );
            }

            //will talk about this in the 3rd video
            connected(mmSocket,mDevice);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }


    // Starts the connections service
    // Start AcceptThread to begin listening in server mode
    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    // AcceptThread starts and sit waiting for a connection
    // Then ConnectThread starts & attempts to make a connection w/other diveces AcceptThread
    public void startClient(BluetoothDevice device,UUID uuid){
        Log.d(TAG, "startClient: Started.");

        //initprogress dialog
        mProgressDialog = ProgressDialog.show(mContext,"Connecting Bluetooth"
                ,"Please Wait...",true);

        mConnectThread = new ConnectThread(device, mUUID);
        mConnectThread.start();
    }

    // Connected Thread Class
    // Responsible for mainting BT Conncetion, sending data, & recieving data
    // throough inout output streams
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            //dismiss the progressdialog when connection is established
            try{
                mProgressDialog.dismiss();
            }catch (NullPointerException e){
                e.printStackTrace();
            }


            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            byte[] imgBuffer = new byte[bufSize * bufSize];
            byte[] buffer = new byte[bufSize];
            int pos = 0;



            int bytes = 0; // bytes returned from read()
            while (true) {
                try {
                    if (send_mode == IMAGE_MODE) {
                        Log.d(TAG, "Mode is Image Mode: " +send_mode);
                        bytes = mmInStream.read(buffer);
                        String incomingMessage = new String(buffer, 0, bytes);
                        Log.d(TAG, "InputStream: " + incomingMessage);

                        if (incomingMessage.contains(IMAGE_CODE.substring(3))){
                            imgBuffer = new byte[bufSize * bufSize];
                            pos = 0;
                            if(incomingMessage.contains(mSpecialCode1)){
                                sender = PLAYER1;
                            } else if (incomingMessage.contains(mSpecialCode2)) {
                                sender = PLAYER2;
                            } else {
                                sender = HOST;
                            }
                            send_mode = IMAGE_MODE;
                            Log.d(TAG, "Mode changed to: " + send_mode);
                            int start = 0;
                            int new_bytes = bytes - IMAGE_CODE.length();
                            if(incomingMessage.contains(mSpecialCode1)){
                                sender = PLAYER1;
                                start= incomingMessage.lastIndexOf(mSpecialCode1.substring(3)) + mSpecialCode1.substring(3).length();
                                new_bytes -= mSpecialCode1.length();
                            } else if (incomingMessage.contains(mSpecialCode2)) {
                                sender = PLAYER2;
                                start= incomingMessage.lastIndexOf(mSpecialCode2.substring(3)) + mSpecialCode2.substring(3).length();
                                new_bytes -= mSpecialCode2.length();
                            } else {
                                sender = HOST;
                            }
                            byte[] new_buffer = incomingMessage.substring(start).getBytes();
                            System.arraycopy(new_buffer, 0, imgBuffer, pos, new_bytes);
                            pos += new_bytes;

                            //IMAGEEEE
                            if (sender == PLAYER1){
                                Intent incomingImageIntent = new Intent("Player1Image");
                                incomingImageIntent.putExtra("p1Image", imgBuffer);
                                if (new_bytes > 0) LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingImageIntent);
                            } else if (sender == PLAYER2){
                                Intent incomingImageIntent = new Intent("Player2Image");
                                incomingImageIntent.putExtra("p2Image", imgBuffer);
                                if (new_bytes > 0) LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingImageIntent);
                            } else{
                                Intent incomingImageIntent = new Intent("incomingImage");
                                incomingImageIntent.putExtra("theImage", imgBuffer);
                                if (new_bytes > 0) LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingImageIntent);
                            }
                        } else if (incomingMessage.contains(TEXT_CODE.substring(3))){
                            incomingMessage = incomingMessage.substring(incomingMessage.lastIndexOf(TEXT_CODE.substring(3)) +(TEXT_CODE.substring(3).length()));
                            Log.d(TAG, "InputStream: " + incomingMessage);
                            Intent incomingMessageIntent = new Intent("incomingMessage");
                            incomingMessageIntent.putExtra("theMessage", incomingMessage);
                            if (incomingMessage.length() > 0)LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);
                            send_mode = TEXT_MODE;
                            Log.d(TAG, "Mode changed to: " + send_mode);
                        } else {
                            System.arraycopy(buffer, 0, imgBuffer, pos, bytes);
                            pos += bytes;

                            //IMAGEEEE
                            if (sender == PLAYER1){
                                Intent incomingImageIntent = new Intent("Player1Image");
                                incomingImageIntent.putExtra("p1Image", imgBuffer);
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingImageIntent);
                            } else if (sender == PLAYER2){
                                Intent incomingImageIntent = new Intent("Player2Image");
                                incomingImageIntent.putExtra("p2Image", imgBuffer);
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingImageIntent);
                            } else{
                                Intent incomingImageIntent = new Intent("incomingImage");
                                incomingImageIntent.putExtra("theImage", imgBuffer);
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingImageIntent);
                            }
                        }
                    } else {
                        Log.d(TAG, "Mode is Text Mode: " +send_mode);
                        bytes = mmInStream.read(buffer);
                        String incomingMessage = new String(buffer, 0, bytes);
                        Log.d(TAG, "InputStream: " + incomingMessage);

                        if (incomingMessage.contains(IMAGE_CODE.substring(3))){
                            send_mode = IMAGE_MODE;
                            imgBuffer = new byte[bufSize * bufSize];
                            pos = 0;
                            Log.d(TAG, "Mode changed to: " + send_mode);
                            int start = 0;
                            int new_bytes = bytes - IMAGE_CODE.length();
                            if(incomingMessage.contains(mSpecialCode1)){
                                sender = PLAYER1;
                                start= incomingMessage.lastIndexOf(mSpecialCode1.substring(3)) + mSpecialCode1.substring(3).length();
                                new_bytes -= mSpecialCode1.length();
                            } else if (incomingMessage.contains(mSpecialCode2)) {
                                sender = PLAYER2;
                                start= incomingMessage.lastIndexOf(mSpecialCode2.substring(3)) + mSpecialCode2.substring(3).length();
                                new_bytes -= mSpecialCode2.length();
                            } else {
                                sender = HOST;
                            }
                            byte[] new_buffer = incomingMessage.substring(start).getBytes();
                            System.arraycopy(new_buffer, 0, imgBuffer, pos, new_bytes);
                            pos += new_bytes;

                            //IMAGEEEE
                            if (sender == PLAYER1){
                                Intent incomingImageIntent = new Intent("Player1Image");
                                incomingImageIntent.putExtra("p1Image", imgBuffer);
                                if (new_bytes > 0) LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingImageIntent);
                            } else if (sender == PLAYER2){
                                Intent incomingImageIntent = new Intent("Player2Image");
                                incomingImageIntent.putExtra("p2Image", imgBuffer);
                                if (new_bytes > 0) LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingImageIntent);
                            } else{
                                Intent incomingImageIntent = new Intent("incomingImage");
                                incomingImageIntent.putExtra("theImage", imgBuffer);
                                if (new_bytes > 0) LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingImageIntent);
                            }

                        } else if (incomingMessage.contains(TEXT_CODE.substring(3))){
                            send_mode = TEXT_MODE;
                            incomingMessage = incomingMessage.substring(incomingMessage.lastIndexOf(TEXT_CODE.substring(3))+(TEXT_CODE.substring(3).length()));
                            Log.d(TAG, "InputStream1: " + incomingMessage);
                            Intent incomingMessageIntent = new Intent("incomingMessage");
                            incomingMessageIntent.putExtra("theMessage", incomingMessage);
                            if (incomingMessage.length() > 0)LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);
                        } else{
                            Log.d(TAG, "InputStream: " + incomingMessage);
                            Intent incomingMessageIntent = new Intent("incomingMessage");
                            incomingMessageIntent.putExtra("theMessage", incomingMessage);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                    break;
                }

            }

        }

        //Call this from the main activity to send data to the remote device
        //===========================================================================
        //FYI STUFF
        public void write(byte[] bytes) {
            if (send_mode == IMAGE_MODE){
                Log.d(TAG, "write: Writing image to outputstream...length " + bytes.length);
            } else if (send_mode == TEXT_MODE){
                String text = new String(bytes, Charset.defaultCharset());
                Log.d(TAG, "write: Writing to outputstream: ");
            }
            //String text = new String(bytes, Charset.defaultCharset());
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private synchronized void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }


    // write to the connectedThread in an unsycnrhocnized manner
    // out - bytes to write (ConnectedThread write(byte[]))
    public int write(byte[] out) {
        /*
        for (int i = 0; i <mThreads.size(); i++) {
            try {
                // Create temporary object
                ConnectedThread r;

                // Synchronize a copy of the ConnectedThread
                synchronized (this) {
                    r = mThreads.get(i);
                }

                r.write(out);


            } catch (Exception e) {

            }
        }*/

        // Create temporary object
        ConnectedThread r; // lmao what is this for

        // Synchronize a copy of the ConnectedThread
        Log.d(TAG, "write: Write Called.");
        //perform the write
        mConnectedThread.write(out);
        return 1;
    }


}



