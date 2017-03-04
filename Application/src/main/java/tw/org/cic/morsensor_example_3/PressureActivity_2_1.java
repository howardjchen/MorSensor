package tw.org.cic.morsensor_example_3;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import tw.org.cic.dataManage.DataTransform;
import tw.org.cic.morsenser_example.R;

public class PressureActivity_2_1 extends Activity {
    private static final String TAG = "PressureActivity_2_1";
    public static Activity mPressureActivity;
    static TextView tv_Pressure, tv_Temp, tv_altitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure_view_2_1);

        mPressureActivity = this;

        tv_Pressure = (TextView) findViewById(R.id.tv_Pressure);
        tv_Temp = (TextView) findViewById(R.id.tv_Temp);
        tv_altitude = (TextView) findViewById(R.id.tv_altitude);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    static float height_ft, pressure_kpa, temperature_F;
    private static final float PSEA_MMHG_AVG = 760.037f;
    static float pressure_mmHg, height_m, temperature_c;
    static float data[] = new float[3];
    public static void DisplayPressureData(){
        data = DataTransform.getData();
        pressure_mmHg = data[0];

        //height_m = 12 * (763 - pressure_mmHg); //海拔 = 12 * (763 - 壓力(mmHg))
        //高度 = -7924 * ln(Pmea. / Psea);  Psea.感測器氣壓、Psea海平面氣壓
        height_m = (float)(-7924f * Math.log(pressure_mmHg / PSEA_MMHG_AVG));

        pressure_kpa = (int)(pressure_mmHg * 0.133322 * 100) / 100.0f;
        tv_Pressure.setText("Pressure: \n"+pressure_mmHg + "mmHg  \n"+pressure_kpa+"Kpa");

        height_ft = (int)(height_m * 3.28 * 100) / 100.0f;
        tv_altitude.setText("Altitude:\n"+height_m + "m  \n" + height_ft + "ft");

        if(data[1] != 0) {
            temperature_c = (int) (data[1] * 10) / 10.0f;
            temperature_F = temperature_c * 9f / 5f + 32f;
            tv_Temp.setText("temp:\n" + temperature_c + "℃  \n" + temperature_F + "℉");
        }

    }
}
