package tw.org.cic.morsenser_example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import tw.org.cic.control_activity.MainActivity;
import tw.org.cic.dataManage.DataTransform;


public class THUViewActivity extends Activity {
    public static Activity mTHUViewActivity;

    static TextView tv_Temp,tv_Humi,tv_UV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_thu_view);

        mTHUViewActivity = this;
        tv_UV = (TextView) findViewById(R.id.tv_UV);
        tv_Temp = (TextView) findViewById(R.id.tv_Temp);
        tv_Humi = (TextView) findViewById(R.id.tv_Humi);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thuview, menu);
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

    static float data[] = new float[3];
    public static void DisplayTHUData(){
        data = DataTransform.getData();
        tv_UV.setText((int)(data[0] * 1000) / 1000.0 + ""); //UV
        tv_Temp.setText((int)(data[1] * 1000) / 1000.0 + ""); //Temp
        tv_Humi.setText((int)(data[2] * 1000) / 1000.0 + ""); //Humi
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("THUViewActivity", "--- ON DESTROY ---");
        for(int i=0;i<3;i++)
            MainActivity.SendMorSensorStop();
    }
}
