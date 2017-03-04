package tw.org.cic.morsensor_example_3;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tw.org.cic.dataManage.DataTransform;
import tw.org.cic.morsenser_example.R;


public class IRDViewActivity_2_1 extends Activity {
    static TextView tv_Voltage, tv_Data, tv_Sensor;
    static RelativeLayout layout_bg;
    static Resources res;
    public static Activity mIrDViewActivity_2_1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_distance_view_2_1);
//        setActivityPosition(MicViewActivity_2_1);
        mIrDViewActivity_2_1 = this;

        tv_Sensor = (TextView) findViewById(R.id.tv_Sensor);
        tv_Sensor.setText("IrD");
        tv_Voltage = (TextView) findViewById(R.id.tv_Voltage);
        tv_Data = (TextView) findViewById(R.id.tv_Data);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

/**
 *  cm - (mV_Raw - mV)/Interval
 *  cm　　 mV   Interval
 *  10　　1886　　832
 *  20　　1054　　286
 *  30　　 768　  126
 *  40　　 642　　 81
 *  50　　 561　　 49
 *  60　　 512　　 40
 *  70　　 472　　 31
 *  80　　 441　　 27
 *  90　　 414　　114
 * 150　　 300
 */
    static float data[] = new float[3];
    public static void DisplayIrDData() {
        data = DataTransform.getData();

        tv_Voltage.setText("Voltage(mV):"+data[0]);
        tv_Data.setText("Distance(cm):" + data[1]);
    }
}




















