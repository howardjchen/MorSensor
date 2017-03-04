package tw.org.cic.morsensor_example_3;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import tw.org.cic.control_activity.MainActivity;
import tw.org.cic.dataManage.DataTransform;
import tw.org.cic.morsenser_example.R;
import tw.org.cic.view.ThermalView;

public class IRIViewActivity_2_1 extends Activity {
    private static Activity mThermalActivity;
    private final static String TAG = "IRIViewActivity_2_1";
    static boolean drawInit = false;
    static TextView tv_index, tv_lost, tv_progress;
    static Button btn_start, btnReset;
    static LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermal_view_2_1);
//        DrawPicture();
        mThermalActivity = this;

        layout = (LinearLayout) mThermalActivity.findViewById(R.id.Thermal);

        tv_index = (TextView) findViewById(R.id.tv_index);
        tv_lost = (TextView) findViewById(R.id.tv_lost);
        tv_progress = (TextView) findViewById(R.id.tv_progress);

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
                MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_FILE_DATA_SIZE);
            }
        });
        btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                clearData();
                btn_start.setEnabled(true);
            }
        });
    }

    private void clearData() {
        count = 0;
        count2 = 0;
        LostData = "";
        lostCount = 0;
        tv_index.setText("packet:0  index1:0  index2:0");
        tv_lost.setText("LostCount:0\nLostData:");
        tv_progress.setText("  0%");
        pixels = null;

        layout.removeAllViews();
        btn_start.setEnabled(false);
    }

    static String LostData = "";
    static int packet = 0, lostCount, index = 0;
    static int count = 0, count2 = 0;
    public static short[] pixels; //80*60

    public static void DisplayPicture(byte[] values) {
        if(values[0] == (byte)0xF1 && values[1] == (byte)0xC8){
            packet = DataTransform.convertTwoBytesToIntUnsigned(values[2], values[3]);
            pixels = new short[4800];
            Log.i(TAG, "index:"+index+" packet:"+packet+" count2:"+count2+" values[2]:"+values[2] + " values[3]:"+values[3]);
            return;
        }

        btn_start.setEnabled(false);
        index = values[2] * 10 + values[3];
        Log.i(TAG, "index:"+index+" packet:"+packet+" count2:"+count2+" values[2]:"+values[2] + " values[3]:"+values[3]);

        if (count2 == index) {
            for (int i = 0; i < 8; i++) {
                pixels[count] = (short) ((values[i * 2 + 4] & 0xFF) << 8 | (values[i * 2 + 5] & 0xFF));
                count++;
            }
        } else { //Lost
            while (count2 < index) {
                for (int jj = 0; jj < 8; jj++) {
                    pixels[count] = 0;
                    count++;
                }
                LostData += (count2 + " ");
                lostCount++;
                count2++;
            }
        }

        tv_index.setText("packet:" + packet + "\nindex:"+ index + "  index1:" + values[2] + "  index2:" + values[3]);
        tv_lost.setText("LostCount:" + lostCount + "\nLostData:" + LostData);
        tv_progress.setText("  " + (int) (index / 600.0 * 100) + "%");

        if ((count2+1) == packet) {//0~59 0~9(600packet)
            Drawinit();
            tv_progress.setText("  100%");
            btn_start.setEnabled(true);
        }
        count2++;
    }

    private static void Drawinit() {
        layout.addView(new ThermalView(mThermalActivity), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        Log.d(TAG, "Draw init finish.");
    }
}
