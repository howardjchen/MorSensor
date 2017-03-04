package tw.org.cic.morsenser_example;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tw.org.cic.control_activity.MainActivity;
import tw.org.cic.dataManage.DataTransform;


public class IMUViewPlusActivity extends Activity {
    private static final String TAG = "IMUViewPlusActivity";

    private static Context context;
    static ImageView imgGyro;
    static TextView tv_Angle, tv_GryoX, tv_GryoY, tv_GryoZ, tv_AccX, tv_AccY, tv_AccZ, tv_MagX, tv_MagY, tv_MagZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imuview_plus);

        context = this;

        imgGyro = (ImageView) findViewById(R.id.imgGyro);

        tv_Angle = (TextView) findViewById(R.id.tv_Angle);
        tv_GryoX = (TextView) findViewById(R.id.tv_GyroX);
        tv_GryoY = (TextView) findViewById(R.id.tv_GyroY);
        tv_GryoZ = (TextView) findViewById(R.id.tv_GyroZ);
        tv_AccX = (TextView) findViewById(R.id.tv_AccX);
        tv_AccY = (TextView) findViewById(R.id.tv_AccY);
        tv_AccZ = (TextView) findViewById(R.id.tv_AccZ);
        tv_MagX = (TextView) findViewById(R.id.tv_MagX);
        tv_MagY = (TextView) findViewById(R.id.tv_MagY);
        tv_MagZ = (TextView) findViewById(R.id.tv_MagZ);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_imuview_plus, menu);
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

    private static void MyImageViewSize(ImageView imgid, int evenWidth, int evenHight) {
        ViewGroup.LayoutParams params = imgid.getLayoutParams();  //import android.view.ViewGroup.LayoutParams;
        params.width = evenWidth;
        params.height = evenHight;
        imgid.setLayoutParams(params);
    }

    static boolean preMode = true, mode;
    static float Gyro, Acc = 0, shake;
    static float data[] = new float[9];

    public static void DisplayIMUData() {
        data = DataTransform.getData();


        /** Gyro: Shake Zoom In*/
        Gyro = (Math.abs(data[0])+Math.abs(data[1])+Math.abs(data[2]) / 3) / 500;
        if (Gyro < 0.1)
            Gyro = 0.1f;
        MyImageViewSize(imgGyro, (int) (1244 * Gyro), (int) (411 * Gyro));


        /** Gyro: Shake Zoom In --> Stop --> Shake Zoom Out */
//        Gyro = (Math.abs(data[0]) + Math.abs(data[1]) + Math.abs(data[2]) / 3);
//        if (Gyro < 10) { //自行觀察停止時的數值
//            if (preMode != mode) { //preMode:前一次狀態 , mode:當前狀態
//                preMode = mode;
//                mode = !mode;
//            }
//        } else {
//            if (mode) {
//                shake += 0.001; //放大倍數
//                mode = true;
//            } else {
//                shake -= 0.001; //縮小倍數
//                mode = false;
//            }
//        }
//        if (shake < 0.1)
//            shake = 0.1f;
//        MyImageViewSize(imgGyro, (int) (1050 * shake), (int) (180 * shake));


        /** Acc: Angle*/
        float Angle = 0f, AccX, AccY;
        AccX = data[3];
        AccY = data[4];
        if(AccX < 0){
            if(AccY > 0)
                Angle = 270 + Math.abs(AccX) * 90f; //(270 ~ 360)
            else
                Angle = Math.abs(AccY) * 90f; //(0 ~ 90)
        }else {
            if(AccY > 0)
                Angle = 180 + Math.abs(AccY) * 90f; //(180 ~ 270)
            else
                Angle = 90 + Math.abs(AccX) * 90f; //(90 ~ 180)
        }
        tv_Angle.setText("Angle:"+Angle);


        /** Acc: Angle < 180 show morsensor_logo else narlabs */
        if(Angle<180)
            imgGyro.setImageDrawable(context.getDrawable(R.drawable.morsensor_logo));
        else imgGyro.setImageDrawable(context.getDrawable(R.drawable.narlabs));


        //imgGyro.getWidth() = 1050(350)
        //imgGyro.getHeight() = 180(60)

        tv_GryoX.setText("Gyro X: " + (int) (data[0] * 1000) / 1000.0); //Gryo x
        tv_GryoY.setText("Gyro Y: " + (int) (data[1] * 1000) / 1000.0); //Gryo y
        tv_GryoZ.setText("Gyro Z: " + (int) (data[2] * 1000) / 1000.0); //Gryo z

        tv_AccX.setText("Acc X: " + (int) (data[3] * 1000) / 1000.0); //Acc x
        tv_AccY.setText("Acc Y: " + (int) (data[4] * 1000) / 1000.0); //Acc y
        tv_AccZ.setText("Acc Z: " + (int) (data[5] * 1000) / 1000.0); //Acc z

        tv_MagX.setText("Mag X: " + (int) (data[6] * 1000) / 1000.0); //Mag x
        tv_MagY.setText("Mag Y: " + (int) (data[7] * 1000) / 1000.0); //Mag y
        tv_MagZ.setText("Mag Z: " + (int) (data[8] * 1000) / 1000.0); //Mag z
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "--- ON DESTROY IMUViewActivity ---");
        for (int i = 0; i < 3; i++)
            MainActivity.SendMorSensorStop();
    }
}
















