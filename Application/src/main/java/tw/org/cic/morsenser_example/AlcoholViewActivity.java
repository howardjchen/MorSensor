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


public class AlcoholViewActivity extends Activity {

    public static Activity mAlcoholViewActivity;
    static TextView tv_Alcohol, tv_Alcohol_Status, tv_ADC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_alcohol_view);

        mAlcoholViewActivity = this;
        tv_Alcohol = (TextView)findViewById(R.id.tv_Alcohol);
        tv_Alcohol_Status = (TextView)findViewById(R.id.tv_Alcohol_Status);
        tv_ADC = (TextView)findViewById(R.id.tv_ADC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alcohol_view, menu);
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
    public static void DisplayAlcoholData(){
        data = DataTransform.getData();
        if(data[0] < 0) data[0] = 0;
        tv_Alcohol.setText((int)(data[0] * 10000) / 10000.0 + " %"); //Alcohol
        tv_ADC.setText((int)(data[1] * 10000) / 10000.0 + " V");

        if(data[0] < 0.25) tv_Alcohol_Status.setText("酒精反應低於標準值");
        else if(data[0] >= 0.25 && data[0] < 0.5) tv_Alcohol_Status.setText("微醉 症狀：顏面色紅、輕度血壓上升、亦有人無症狀。");
        else if(data[0] >= 0.5 && data[0] < 0.75) tv_Alcohol_Status.setText("輕醉 症狀：輕度酩酊、解除抑制、多辯、決斷快。");
        else if(data[0] >= 0.75 && data[0] < 1.25) tv_Alcohol_Status.setText("茫醉 症狀：中度酩酊、興奮狀、合併麻痺症狀、語言略不清楚、運動失調、平衡障礙、判斷力遲鈍。");
        else if(data[0] >= 1.25 && data[0] < 1.75) tv_Alcohol_Status.setText("深醉 症狀：強度酩酊、噁心、嘔吐、意識混亂、步行困難、言語不清、易進入睡眠狀態。");
        else if(data[0] >= 1.75 && data[0] < 2.25) tv_Alcohol_Status.setText("泥醉 症狀：昏睡期、意識完全消失、時有呼吸困難，棄之不顧可能導致死亡。");
        else if(data[0] >= 2.25) tv_Alcohol_Status.setText("死亡 症狀：呼吸麻痺或心臟機能不全而死。");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("AlcoholViewActivity", "--- ON DESTROY  ---");
        for(int i=0;i<3;i++)
            MainActivity.SendMorSensorStop();
    }
}
