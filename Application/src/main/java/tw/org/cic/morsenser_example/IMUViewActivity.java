package tw.org.cic.morsenser_example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import tw.org.cic.control_activity.MainActivity;
import tw.org.cic.dataManage.DataTransform;


public class IMUViewActivity extends Activity {
    public static Activity mIMUViewActivity = null;
    private static final String TAG = "IMUViewActivity";
    private static final boolean D = false;

    static TextView tv_GryoX, tv_GryoY, tv_GryoZ, tv_AccX, tv_AccY, tv_AccZ, tv_MagX, tv_MagY, tv_MagZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_imu_view);
        if (D) Log.e(TAG, "-- IMUViewActivity --");

        mIMUViewActivity = this;
        tv_GryoX = (TextView) findViewById(R.id.GryoX);
        tv_GryoY = (TextView) findViewById(R.id.GryoY);
        tv_GryoZ = (TextView) findViewById(R.id.GryoZ);
        tv_AccX = (TextView) findViewById(R.id.AccX);
        tv_AccY = (TextView) findViewById(R.id.AccY);
        tv_AccZ = (TextView) findViewById(R.id.AccZ);
        tv_MagX = (TextView) findViewById(R.id.MagX);
        tv_MagY = (TextView) findViewById(R.id.MagY);
        tv_MagZ = (TextView) findViewById(R.id.MagZ);
    }

    static float data[] = new float[9];

    public static void DisplayIMUData() {
        data = DataTransform.getData();

        tv_GryoX.setText(" " + (int) (data[0] * 1000) / 1000.0); //Gryo x
        tv_GryoY.setText(" " + (int) (data[1] * 1000) / 1000.0); //Gryo y
        tv_GryoZ.setText(" " + (int) (data[2] * 1000) / 1000.0); //Gryo z

        tv_AccX.setText(" " + (int) (data[3] * 1000) / 1000.0); //Acc x
        tv_AccY.setText(" " + (int) (data[4] * 1000) / 1000.0); //Acc y
        tv_AccZ.setText(" " + (int) (data[5] * 1000) / 1000.0); //Acc z

        tv_MagX.setText(" " + (int) (data[6] * 1000) / 1000.0); //Mag x
        tv_MagY.setText(" " + (int) (data[7] * 1000) / 1000.0); //Mag y
        tv_MagZ.setText(" " + (int) (data[8] * 1000) / 1000.0); //Mag z
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (D) Log.e(TAG, "--- ON DESTROY IMUViewActivity ---");
        for (int i = 0; i < 3; i++)
            MainActivity.SendMorSensorStop();
    }
}

