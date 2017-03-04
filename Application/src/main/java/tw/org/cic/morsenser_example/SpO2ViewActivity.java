package tw.org.cic.morsenser_example;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import tw.org.cic.control_activity.MainActivity;
import tw.org.cic.dataManage.DataTransform;


public class SpO2ViewActivity extends Activity {

    static TextView tv_spo2, tv_heartrate, tv_count;
    static Button btnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sp_o2_view);

        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_spo2 = (TextView) findViewById(R.id.tv_spo2);
        tv_heartrate = (TextView) findViewById(R.id.tv_HeartRate);

        btnStart = (Button) findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_FILE_DATA_SIZE); //SEND_MORSENSOR_FILE_DATA_SIZE
                btnStart.setEnabled(false);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sp_o2_view, menu);
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


    static float data[] = new float[6];
    public static void DisplaySpO2Data(){
        data = DataTransform.getData();
        tv_spo2.setText(data[4] + ""); //SpO2
        tv_heartrate.setText(data[5] + ""); //Heart Rate
        tv_count.setText((int)data[6] + ""); //count

        if(data[6] == 1024){
            btnStart.setEnabled(true);
        }

    }

}
