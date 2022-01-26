package com.example.finallieb_c4rdee4;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import android.media.Ringtone;
import android.media.RingtoneManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextClock;
import android.widget.TimePicker;
import java.util.Timer;
import java.util.TimerTask;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    final static int MESSAGE_READ = 5;

    private static final String TAG = "MainActivity";

    private BluetoothConnection bluetoothSerial;

    private String deviceName = "HC-05";

    private String messageReceived;

    private int inputCounter = 0;

    private boolean inputClear = false;

    public int setTime = 5;

    public int xCounter = 0;

    public int postREMcOunt = 0;

    public boolean REMsLeep = false;

    public boolean wakeTime = false;

    public int resetAllCounter;

    public boolean alarmActive = false;

    //public String wakeUpTimethruBtn = "NoTime"; Absolute failure



    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    // construct a string from the valid bytes in the buffer.
                    messageReceived = new String(readBuf, 0, msg.arg1);

                    inputCounter++;

                    if (inputCounter > 4)
                        inputClear = true;

                    if(inputClear)
                        Log.e(TAG, messageReceived);

                    if(REMsLeep && inputClear){
                        if(Integer.parseInt(messageReceived) < 15){
                            postREMcOunt++;
                            if(postREMcOunt > 3)
                            {
                                wakeTime = true;
                                REMsLeep = false;
                                resetAllCounter = inputCounter;
                            }

                            Log.e(TAG,Boolean.toString(wakeTime));
                        }
                    } else {
                        postREMcOunt = 0;
                    }

                    if (Integer.parseInt(messageReceived) > 18 && inputClear){
                        xCounter++;

                        if (xCounter > 3)
                            REMsLeep = true;

                    } else {
                        xCounter = 0;
                    }

                    if ((inputCounter - resetAllCounter) > 3)
                        wakeTime = false;

                    break;
            }
        }
    };

    //Alarm variables init
    TimePicker alarmTime;
    TextClock currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bluetoothSerial = new BluetoothConnection(this, new BluetoothConnection.MessageHandler() {
            @Override
            public int read(int bufferSize, byte[] buffer) {

                int sizeOfBuffer = buffer.length;

                handler.obtainMessage(MainActivity.MESSAGE_READ, bufferSize, -1, buffer).sendToTarget();

                try{
                    bluetoothSerial.write("y");
                    Log.e(TAG,"SENDING - final PROTOCOL");
                } catch(IOException e){
                    Log.e(TAG,"ISSUES");
                }


                return sizeOfBuffer;  //doRead(bufferSize, buffer);
            }

        }, deviceName);

        bluetoothSerial.connectBt();


        alarmTime = findViewById(R.id.timePicker);
        currentTime = findViewById(R.id.textClock);
        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int numbertest = timeToNumber(AlarmTime());
                if (alarmActive && (currentTime.getText().toString().equals(AlarmTime()) || wakeTime) && (timeToNumber(currentTime.getText().toString()) > (numbertest-50))){ //|| (((numbertest - 100) < timeToNumber(currentTime.getText().toString()))) && wakeTime) {
                    r.play();
                    Log.e(TAG,"GOOD");
                } else{
                    r.stop();
                    //Log.e(TAG,"error " + String.valueOf(timeToNumber(currentTime.getText().toString())));// + " " + ". EWT: " + (numbertest - 100));// + " --- " + timeToNumber(currentTime.getText().toString()) + wakeTime);
                }
            }
        }, 0, 1000);

    }

    protected void onResume() {
        super.onResume();

    }

    protected void onPause() {
        super.onPause();

    }

    public String AlarmTime() {

        Integer alarmHours = alarmTime.getCurrentHour();
        Integer alarmMinutes = alarmTime.getCurrentMinute();
        String stringAlarmMinutes;

        if (alarmMinutes < 10) {
            stringAlarmMinutes = "0";
            stringAlarmMinutes = stringAlarmMinutes.concat(alarmMinutes.toString());
        } else {
            stringAlarmMinutes = alarmMinutes.toString();
        }

        String stringAlarmTime;

        if (alarmHours > 9) {
            //alarmHours = alarmHours - 12;
            stringAlarmTime = alarmHours.toString().concat(":").concat(stringAlarmMinutes); //.concat(" PM");
        } else {
            stringAlarmTime = ("0".concat(alarmHours.toString())).concat(":").concat(stringAlarmMinutes); //.concat(" AM");
        }

        return stringAlarmTime;

    }

    public int timeToNumber(String time){

        String segments[] = time.split(":");
        String earliestWakeTimeMinutes = segments[segments.length - 1]; //MINUTES
        String earliestWakeTimeHours = segments[segments.length - 2]; //HOURS

        int numHours = Integer.parseInt(earliestWakeTimeHours);
        int numMinutes = Integer.parseInt(earliestWakeTimeMinutes);

        return (numHours*100 + numMinutes);
    }

    public void setWakeTime(View view){

        alarmActive = true;
        TextView tv1 = (TextView)findViewById(R.id.textView);
        tv1.setText("Alarm is set for time in the clock!");

    }

    public void unsetTime(View view){

        alarmActive = false;
        TextView tv1 = (TextView)findViewById(R.id.textView);
        tv1.setText("No alarm is set.");

    }

}
