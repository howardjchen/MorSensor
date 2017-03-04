package tw.org.cic.morsensor_example_3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tw.org.cic.control_activity.MainActivity;
import tw.org.cic.dataManage.DataTransform;
import tw.org.cic.morsenser_example.R;

public class PowerActivity_2_1 extends Activity {
    private static final String TAG = "PressureActivity_2_1";
    public static Activity mPressureActivity;
    static TextView tv_power_voltage, tv_power_status;
    static Button btGetPower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_view_2_1);

        mPressureActivity = this;

        tv_power_voltage = (TextView) findViewById(R.id.tv_power_voltage);
        tv_power_status = (TextView) findViewById(R.id.tv_power_status);
        btGetPower = (Button) findViewById(R.id.btGetPower);
        btGetPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_POWER_PERCENTAGE);
                MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_POWER_CHARGING);
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    static float data[] = new float[3];
    public static void DisplayPowerData(){
        data = DataTransform.getData();

        tv_power_voltage.setText("電量: "+ (int)data[0] + "%");
        if(data[1] == 0)
            tv_power_status.setText("充電狀態: 未充電");
        else if(data[1] == 1)
            tv_power_status.setText("充電狀態: 充電中");
    }
}
