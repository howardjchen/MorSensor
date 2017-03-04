package tw.org.cic.morsensor_example_3;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import tw.org.cic.dataManage.DataTransform;
import tw.org.cic.control_activity.MainActivity;
import tw.org.cic.morsenser_example.R;


public class CO2ViewActivity_2_1 extends Activity {

    public static Activity mCO2ViewActivity_2_1;
    static TextView tv_CO2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_co2_view);

        mCO2ViewActivity_2_1 = this;
        tv_CO2 = (TextView) findViewById(R.id.tv_CO2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pirview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static float data[] = new float[1];
    static String mColor = "";
    public static void DisplayCO2Data(){
        data = DataTransform.getData();
        tv_CO2.setText((int)(data[0] * 1000) / 1000.0 + ""); //CO2

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("CO2ViewActivity_2_1", "--- ON DESTROY ---");
        for(int i=0;i<3;i++)
            MainActivity.SendMorSensorStop();
    }
}
