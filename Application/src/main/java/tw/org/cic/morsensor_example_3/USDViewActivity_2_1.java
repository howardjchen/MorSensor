package tw.org.cic.morsensor_example_3;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tw.org.cic.dataManage.DataTransform;
import tw.org.cic.morsenser_example.R;


public class USDViewActivity_2_1 extends Activity {
    static TextView tv_Voltage, tv_Data, tv_Sensor;
    static RelativeLayout layout_bg;
    static Resources res;
    public static Activity mUSDViewActivity_2_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_distance_view_2_1);
//        setActivityPosition(MicViewActivity_2_1);
        mUSDViewActivity_2_1 = this;

        tv_Sensor = (TextView) findViewById(R.id.tv_Sensor);
        tv_Sensor.setText("USD");
        tv_Voltage = (TextView) findViewById(R.id.tv_Voltage);
        tv_Data = (TextView) findViewById(R.id.tv_Data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    static float data[] = new float[3];
    public static void DisplayUSDData() {
        data = DataTransform.getData();

        tv_Voltage.setText("Voltage(mV):"+data[0]);
        tv_Data.setText("Distance(cm):" + data[1]);
    }
}




















