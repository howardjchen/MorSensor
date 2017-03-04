/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tw.org.cic.morsenser_example;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import tw.org.cic.control_activity.MainActivity;
import tw.org.cic.mic.audiofilefunc;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class MicViewActivity extends Activity {
    private static boolean timerup = true;
    public static Activity mMicViewActivity;

    private static Handler mHandler;
    //    private ListView mListview;
    private static ArrayList<Integer> lostlist = new ArrayList<Integer>();
    Vibrator mVibrator;
    // Stops scanning after 10 seconds.
    static int useconuter = 0;
    private String temp;
    static int count2 = 0;
    private static final String TAG = "ActivityDemo";
    private Handler handlerTimer = new Handler();
    //    private static ArrayAdapter<String> mConversationArrayAdapter;
    private static TextView tv_count, tv_dB;
    //    private static TextView counter_200;
    private static Button btnStart, btnPlay, btn_dB_Start, btn_dB_Stop;
    static String filename;
    static byte[] wavedata = null;
    static int filesize = 0;


    private static void writeDateTOFile() {
        Log.e(TAG, "writeDateTOFile");
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        // byte[] audiodata = new byte[16];
        FileOutputStream fos = null;
        int readsize = 0;
        try {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        // while (isRecord == true) {
        if (fos != null) {
            try {
                fos.write(wavedata);

            } catch (IOException e) {
                e.printStackTrace();
            }
            //   }
        }
        try {
            if (fos != null)
                fos.close();// 关闭写入流
            tv_count.setText("Finish");
            btnPlay.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_mic_view_2_1);
        getActionBar().setTitle(R.string.title_activity_mic_view);

        mMicViewActivity = this;

        filename = audiofilefunc.getWavFilePath();
        mHandler = new Handler();
        handlerTimer.postDelayed(updateTimer, 1000);
        tv_count = (TextView) findViewById(R.id.tv_count);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPlay.setEnabled(false);
        tv_dB = (TextView) findViewById(R.id.tv_dB);

        btn_dB_Start = (Button) findViewById(R.id.btn_dB_Start);
        btn_dB_Stop = (Button) findViewById(R.id.btn_dB_Stop);

        btn_dB_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_SENSOR_DATA);
            }
        });
        btn_dB_Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 3; i++)
                    MainActivity.SendMorSensorStop();
            }
        });

        if (savedInstanceState != null) {
            temp = savedInstanceState.getString("temp");
            System.out.println("onCreate: temp = " + temp);
        }
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mHandler.postDelayed(mRepeatTask2, 1000);
                btnStart.setEnabled(false);
                btnPlay.setEnabled(false);
            }

        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e(TAG, filename);
                MediaPlayer mp = new MediaPlayer();
                try {
//                    mp.reset();
                    mp.setDataSource(filename);
                    mp.prepare();
                    mp.start();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
                btnStart.setEnabled(true);
            }
        });
    }

    public static void showdblist(byte[] data) {
        Log.e(TAG, "data[0]:" + data[0] + " data[1]:" + data[1] + " data[2]:" + data[2] + " data[3]:" + data[3]);

        if (timerup) {
            timerup = false;
            useconuter++;

            int index;
            int head;
            head = (((int) data[0] & 0x00ff) << 8 | ((int) (data[1]) & 0x00ff));
            if (head == 0xf1a4) {
                index = (((int) data[2] & 0x00ff) << 8 | ((int) (data[3]) & 0x00ff));

                if (index > filesize) {
                    filesize = index;
                    wavedata = new byte[filesize * 16];
                    timerup = true;
                    return;
                }
            }


            if (head == 0xf2a4) {
                index = (((int) data[2] & 0x00ff) << 8 | ((int) (data[3]) & 0x00ff));

                if (index != count2) {
                    for (int lost = count2; lost < index; lost++) {
//                        mConversationArrayAdapter.add(String.format("%d", lost));
                        lostlist.add(lost);
                    }
                    System.arraycopy(data, 4, wavedata, index * 16, 16);
                    count2 = index + 1;
                } else {
                    System.arraycopy(data, 4, wavedata, count2 * 16, 16);
                    tv_count.setText(String.format("%02d%%", (int) (((float) count2 / (float) filesize) * 100)));
                    count2++;
                }
                if ((count2 > filesize) || (count2 == filesize)) {
                    count2 = 0;
                    filesize = 0;
                    mHandler.postDelayed(mRepeatTask, 500);
                }
            }

            if (head == 0x13a4) {
                if ((int) (data[1] & 0x00ff) == 0xa4) {
                    System.arraycopy(data, 3, wavedata, (lostlist.get(0)) * 16, 16);
                    lostlist.remove(0);
                    mHandler.postDelayed(mRepeatTask, 10);
                }
            }
            timerup = true;
            Log.e(TAG, "update out");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, ".onResume()");

        temp = "xing";
        System.out.println("onResume: temp = " + temp);
        // 切换屏幕方向会导致activity的摧毁和重建
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            System.out.println("屏幕切换");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, ".onSaveInstanceState()");
        outState.putString("temp", temp);
    }

    @Override
    protected void onStop() {
// TODO Auto-generated method stub
        super.onStop();
        Log.e(TAG, "onStop");
        handlerTimer.removeCallbacks(updateTimer);
    }


    //Lost
    private static Runnable mRepeatTask = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "Lost in");
            byte[] a = new byte[20];
            int lost = 0;
            if (!lostlist.isEmpty())
                lost = lostlist.get(0);
            else {
                writeDateTOFile();//write to file
                return;
            }

            a[0] = 0x13;
            a[1] = (byte) (0xa4 & 0xff);
            a[2] = (byte) ((lost & 0x00000ff00) >> 8);
            a[3] = ((byte) (lost & 0x0000000ff));
            for (int i = 4; i < 20; i++)
                a[i] = 0x00;

            MainActivity.lost1 = (byte) ((lost & 0x00000ff00) >> 8);
            MainActivity.lost2 = ((byte) (lost & 0x0000000ff));
            MainActivity.SendMorSensorCommands(106);
            Log.e(TAG, "Lost out");
        }
    };

    //Start
    private Runnable mRepeatTask2 = new Runnable() {
        public void run() {
            Log.e(TAG, "Start in");
            byte[] a = new byte[20];
            a[0] = (byte) (0xf1 & 0xff);
            a[1] = (byte) (0xa4 & 0xff);
            for (int i = 2; i < 20; i++)
                a[i] = 0x00;

            MainActivity.SendMorSensorCommands(108);
            Log.e(TAG, "Start out");
        }
    };

    static int jump = 1;
    static long timelest = 0, ratetotal = 0;
    static int ratecounter = 0;
    public Runnable updateTimer;
    {
        updateTimer = new Runnable() {
            public void run() {

                if (timerup) {
                    timerup = false;
                    Calendar cal = Calendar.getInstance();
                    Long timenew = cal.getTimeInMillis();
                    Long rate = useconuter / ((timenew - timelest) / 1000);
                    ratetotal += rate;
                    if (ratecounter % 10 == 0) {
                        ratetotal = 0;
                    }
//                    counter_200.setText(String.valueOf(rate));
                    timelest = timenew;
                    jump = 1;
                    useconuter = 0;
                    timerup = true;
                    ratecounter++;
                } else {
                    ratecounter++;
                    jump++;
                }
                handlerTimer.postDelayed(this, 1000);
            }
        };
    }

    static short rawMic = 0;

    public static void displaydBData(byte[] value) {
        float rawdata = 0f;
        rawMic = (short) (value[2] << 8 | (value[3] & 0xFF));
        switch (rawMic) {
            case -41:
                rawdata = 39;
                break;
            case -40:
                rawdata = 42;
                break;
            case -39:
                rawdata = 46;
                break;
            case -38:
                rawdata = 50;
                break;
            case -37:
                rawdata = 53;
                break;
            case -36:
                rawdata = 55.4f;
                break;
            case -35:
                rawdata = 57.4f;
                break;
            case -34:
                rawdata = 60.1f;
                break;
            case -33:
                rawdata = 62.3f;
                break;
            case -32:
                rawdata = 64;
                break;
            case -31:
                rawdata = 66.5f;
                break;
            case -30:
                rawdata = 68.4f;
                break;
            case -29:
                rawdata = 70;
                break;
            case -28:
                rawdata = 71.2f;
                break;
            case -27:
                rawdata = 73.4f;
                break;
            case -26:
                rawdata = 75.6f;
                break;
            case -25:
                rawdata = 77.3f;
                break;
            case -24:
                rawdata = 79.8f;
                break;
            case -23:
                rawdata = 82.3f;
                break;
            case -22:
                rawdata = 84.6f;
                break;
            case -21:
                rawdata = 85.2f;
                break;
            case -20:
                rawdata = 87.5f;
                break;
            case -19:
                rawdata = 89.1f;
                break;
            case -18:
                rawdata = 91;
                break;
            case -17:
                rawdata = 93;
                break;
            case -16:
                rawdata = 95;
                break;
            case -13:
                rawdata = 101;
                break;
        }
        tv_dB.setText("dB: " + rawdata);
    }

}