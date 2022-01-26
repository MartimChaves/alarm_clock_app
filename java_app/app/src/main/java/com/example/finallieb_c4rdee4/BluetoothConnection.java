package com.example.finallieb_c4rdee4;

//Bluetooth Libraries
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent; //To use intents
import android.os.Message;
import android.util.Log; //To use Logs
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method; //To use connection via reflection
import java.util.Set;
import java.util.logging.Handler;


public class BluetoothConnection {

    private static final String TAG = "blueToothConnection";

    //Init

    Context context;

    //Handler handler;

    String deviceName; //HC-05

    boolean connected = false;

    //final static int MESSAGE_READ = 5; //handler TAG

    MessageHandler messageHandler;

    BluetoothDevice serverBtDevice; //MAC address

    BluetoothSocket serialBtSocket;

    InfoSerialReader infoReader;

    //Info Streams
    InputStream serialInputStream;
    OutputStream serialOutputStream;



    public BluetoothConnection(Context context, MessageHandler messageHandler, String deviceName) {

        this.context = context;
        this.messageHandler = messageHandler;
        this.deviceName = deviceName;

    }

    public void connectBt(){

        //Init bluetooth adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Client device does not support bluetooth
        if (bluetoothAdapter== null) {
            Log.e(TAG, "Device does not support bluetooth.");
            return;
        } else if (!bluetoothAdapter.isEnabled()) {

            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE); //Request Client discoverable
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120); //Make Client discoverable for 120 secs
            context.startActivity(discoverableIntent); //Start activity in context (aka Main Activity)

            Log.e(TAG,"Client discoverable!"); //if (client.isDiscoverable) then bluetooth is on
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices(); //Set of bonded bluetooth devices

        if (pairedDevices.size() > 0){  //bt devices found

            bluetoothAdapter.cancelDiscovery(); //speeds up the process

            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(deviceName)){
                    serverBtDevice = device;
                }
                else {
                    Log.e(TAG,"Device not found.");
                }
            }
        }

        BluetoothSocket tmpSocket = null;
        try {
            tmpSocket = connectViaReflection(serverBtDevice);
        } catch(Exception e) {
            Log.e(TAG,"Unable to establish socket.");
        }

        serialBtSocket = tmpSocket;

        try {
            serialBtSocket.connect();
        } catch(IOException connectException){
            Log.e(TAG, "Unable to establish connection.");
        }

        try {
            serialInputStream = serialBtSocket.getInputStream();
            serialOutputStream = serialBtSocket.getOutputStream();
            connected = true;
        } catch(Exception e){
            Log.e(TAG,"Unable to establish IO streams.");
        }

        infoReader = new InfoSerialReader();
        infoReader.start();


    }

    private BluetoothSocket connectViaReflection(BluetoothDevice device) throws Exception {
        Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
        return (BluetoothSocket) m.invoke(device, 1);
    }

    private class InfoSerialReader extends Thread {

        private static final int MAX_BYTES = 125;

        byte[] buffer = new byte[MAX_BYTES];

        int bufferSize = 0;

        public void run(){

            while(!isInterrupted()){
                try{

                    if(serialInputStream.available() > 0){

                        try{
                            int newBytes = read(buffer, bufferSize, MAX_BYTES - bufferSize);
                            if (newBytes > 0)
                                bufferSize += newBytes;
                            Log.e(TAG, "available() > 0");
                        } catch(Exception e){
                            Log.e(TAG,"Unable to read from inputStream.");
                        }

                    }

                    if (bufferSize > 0){

                        int read = messageHandler.read(bufferSize, buffer);

                        //NEW CODE HERE FOR PROTOCOL
                        /*char sending = 'y';
                        byte[] sendingByte = new byte[1];
                        sendingByte[1] = (byte) sending;


                        serialOutputStream.write(sendingByte,0,1);*/

                        if (read > 0) {
                            int index = 0;
                            for (int i = read; i < bufferSize; i++) {
                                buffer[index++] = buffer[i];
                            }
                            bufferSize = index;
                        }

                    } else {

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ie) {
                            break;
                        }

                    }

                } catch(Exception e){
                    Log.e(TAG,"Error reading bluetooth data.");
                }
            }

        }


    }

    //Reading/writing classes - "prepared" for exceptions
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException{
        if (connected)
            return serialInputStream.read(buffer, byteOffset, byteCount);

        throw new RuntimeException("Connection lost, reconnecting now.");
    }

    /*public void write(byte[] buffer) throws IOException{
        if (connected)
            serialOutputStream.write(buffer);

        throw new RuntimeException("Connection lost, reconnecting now.");
    }*/

    public void write(String s) throws IOException {
        serialOutputStream.write(s.getBytes());
    }


    //MESSAGE HANDLER INTERFACE

    public static interface MessageHandler {
        public int read(int bufferSize, byte[] buffer);
        //public void write(String s);
    }






}


