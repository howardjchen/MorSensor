package tw.org.cic.morsenser_example;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import tw.org.cic.control_activity.MainActivity;
import tw.org.cic.dataManage.DataTransform;


public class ColorViewActivity extends Activity {
    public static Activity mColorViewActivity;

    static Button btn_Redress;
    static TextView tv_Red,tv_Green,tv_Blue,tv_redress;
    static LinearLayout mLinearColor;
    public static boolean Calibration = false;
    static int CalibrationCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_color_view);

        mColorViewActivity = this;
        btn_Redress = (Button)findViewById(R.id.btn_Redress);
        tv_redress = (TextView) findViewById(R.id.tv_redress);
        tv_Red = (TextView) findViewById(R.id.tv_Red);
        tv_Green = (TextView) findViewById(R.id.tv_Green);
        tv_Blue = (TextView) findViewById(R.id.tv_Blue);
        mLinearColor = (LinearLayout) findViewById(R.id.mLinearColor);

        btn_Redress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calibration = true;
                CalibrationCount++;
                tv_redress.setText("校正成功 "+CalibrationCount+" 次");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_color_view, menu);
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
    static String mColor = "";
    public static void DisplayColorData(){
        data = DataTransform.getData();
        tv_Red.setText((int)data[0] + ""); //Red
        tv_Green.setText((int)data[1] + ""); //Green
        tv_Blue.setText((int)data[2] + ""); //Blue

        mColor = "#" + DataTransform.Red + DataTransform.Green + DataTransform.Blue;
        mLinearColor.setBackgroundColor(Color.parseColor(mColor));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("ColorViewActivity", "--- ON DESTROY ---");
        for(int i=0;i<3;i++)
            MainActivity.SendMorSensorStop();
    }
}
