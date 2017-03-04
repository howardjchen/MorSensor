package tw.org.cic.control_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tw.org.cic.bluetooth.BluetoothLeService;
import tw.org.cic.bluetooth.SampleGattAttributes;
import tw.org.cic.dataManage.ControlSDCard;
import tw.org.cic.dataManage.DataTransform;
import tw.org.cic.dataManage.MorSensorCommand;
import tw.org.cic.morsenser_example.AlcoholViewActivity;
import tw.org.cic.morsenser_example.ColorViewActivity;
import tw.org.cic.morsenser_example.IMUViewActivity;
import tw.org.cic.morsenser_example.MicViewActivity;
import tw.org.cic.morsenser_example.PIRViewActivity;
import tw.org.cic.morsenser_example.R;
import tw.org.cic.morsenser_example.SpO2ViewActivity;
import tw.org.cic.morsenser_example.THUViewActivity;
import tw.org.cic.morsensor_example_3.CO2ViewActivity_2_1;
import tw.org.cic.morsensor_example_3.COViewActivity_2_1;
import tw.org.cic.morsensor_example_3.IRDViewActivity_2_1;
import tw.org.cic.morsensor_example_3.IRIViewActivity_2_1;
import tw.org.cic.morsensor_example_3.PowerActivity_2_1;
import tw.org.cic.morsensor_example_3.PressureActivity_2_1;
import tw.org.cic.morsensor_example_3.USDViewActivity_2_1;


public class MainActivity extends Activity {
    public static Activity mMainActivity;
    private static final String TAG = "MainActivity";
    private static final boolean D = false;

    /* sensor data and status report */
    public static final int IN_ECHO = 1; //0x01
    public static final int IN_SENSOR_LIST = 2; //0x02 Internal use
    public static final int IN_MORSENSOR_VERSION = 3; //0x03 Internal use
    public static final int IN_FIRMWARE_VERSION = 4; //0x04 Internal use

    public static final int IN_SENSOR_VERSION = 17; //0x11
    public static final int IN_REGISTER_CONTENT = 18; //0x12 Internal use
    public static final int IN_LOST_SENSOR_DATA = 19; //0x13
    public static final int IN_TRANSMISSION_MODE = 20; //0x14 Internal use

    public static final int IN_SET_TRANSMISSION_MODE = 33; //0x21
    public static final int IN_STOP_TRANSMISSION = 34; //0x22
    public static final int IN_SET_REGISTER_CONTENT = 35; //0x23

    public static final int IN_MODIFY_LED_STATE = 49; //0x31
    public static final int IN_IN_MCU_LED_D2 = 1; //0x01
    public static final int IN_IN_MCU_LED_D3 = 2; //0x02
    public static final int IN_IN_COLOR_SENSOR_LED = 3; //0x03


    public static final int IN_FILE_DATA_SIZE = 241 - 256; //0xF1
    public static final int IN_FILE_DATA = 242 - 256; //0xF2
    public static final int IN_SENSOR_DATA = 243 - 256; //0xF3

    short[] MorSensorID = new short[10];
    short[] MorSensorVersion = {0, 0, 0};
    short[] FirmwareVersion = {0, 0, 0};

    //Byte (127 ~ -128)
    public static short TempID = 0;
    private static final short IMUID = (byte) 0xD0;//0xD0 208
    private static final short THID = (byte) 0x80;//0x80 128
    private static final short UVID = (byte) 0xC0;//0xC0 192
    private static final short ColorID = (byte) 0x52;//0x52 82
    private static final short SpO2ID = (byte) 0xA0;//0xA0 160
    public static final short AlcoholID = (byte) 0xA2;//0xA2 162
    private static final short MicID = (byte) 0xA4;//0xA4 164
    private static final short PIRID = (byte) 0xA8;//0xA8 168
    private static final short IRDID = (byte) 0xC4;//0xC4
    private static final short USDID = (byte) 0xC6;//0xC6
    private static final short Pressure_BMPID = (byte) 0xEE;//0xEE
    private static final short Pressure_LPSID = (byte) 0x88;//0x88
    private static final short IrIID = (byte) 0xC8;//0xC8
    private static final short CO2ID = (byte) 0x86;//0x86
    private static final short COID = (byte) 0x84;//0x84
    private static final short PowerID = (byte) 0xAA;//0xAA
    private static final short CurrentID = (byte) 0x02;//0x02
    private static final short WiFiID = (byte) 0xA6;//0xA6
    private static final short NFCID = (byte) 0xAC;//0xAC


    private static final int NULL_COMMAND = 200;
    private static final int SEND_MORSENSOR_ID = 1;
    private static final int SEND_MORSENSOR_VERSION = 2;
    private static final int SEND_FIRMWAVE_VERSION = 3;
    private static final int SEND_MORSENSOR_CONTINUOUS_UV = 4;
    private static final int SEND_MORSENSOR_CONTINUOUS_COLOR = 5;
    public static final int SEND_MORSENSOR_SENSOR_DATA = 109;
    public static final int SEND_MORSENSOR_FILE_DATA_SIZE = 108;
    private static final int SEND_MORSENSOR_FILE_DATA = 107;
    private static final int SEND_MORSENSOR_REGISTER = 106;
    public static final int SEND_MORSENSOR_POWER_PERCENTAGE = 111;
    public static final int SEND_MORSENSOR_POWER_CHARGING = 110;
    private static int SendCommands = SEND_MORSENSOR_ID;

    private static boolean UVStart = false;

    private static BluetoothLeService mBluetoothLeService = null;
    // Local Bluetooth adapter
    private static BluetoothAdapter mBluetoothAdapter = null;
    private static ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private static BluetoothGattCharacteristic mReadCharacteristic,mWriteCharacteristic;
    public static boolean mConnected = false;

    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static String mDeviceAddress="123",mDeviceName="",mDeviceData="";
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    Button btIMU, btTH, btColor, btSpO2, btAlcohol, btMic, btPIR, btIRI, btIRD, btUSD, btCO, btCO2, btPressureBMP, btPressureLPS, btPower;
    TextView tv_MorSensorVersion, tv_FirmwaveVersion, tv_MorSensorID;
    static String MorSensor_Version = "", Firmwave_Version = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, ".onCreate()");

        mMainActivity = this;

        //Receive DeviceScanActivity DeviceAddress.
        final Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // 取得資源類別檔中的介面元件
        btIMU = (Button)findViewById(R.id.btIMU);
        btTH = (Button)findViewById(R.id.btTH);
        btColor = (Button)findViewById(R.id.btColor);
        btSpO2 = (Button)findViewById(R.id.btSpO2);
        btAlcohol = (Button)findViewById(R.id.btAlcohol);
        btMic = (Button)findViewById(R.id.btMic);
        btPIR = (Button)findViewById(R.id.btPIR);
        btIRI = (Button)findViewById(R.id.btIRI);
        btIRD = (Button)findViewById(R.id.btIRD);
        btUSD = (Button)findViewById(R.id.btUSD);
        btCO = (Button)findViewById(R.id.btCO);
        btCO2 = (Button)findViewById(R.id.btCO2);
        btPressureBMP = (Button)findViewById(R.id.btPressureBMP);
        btPressureLPS = (Button)findViewById(R.id.btPressureLPS);
        btPower = (Button)findViewById(R.id.btPower);

        tv_MorSensorVersion = (TextView)findViewById(R.id.MorSensor_Version);
        tv_FirmwaveVersion = (TextView)findViewById(R.id.Firmwave_Version);
        tv_MorSensorID = (TextView)findViewById(R.id.MorSensor_ID);

        // 設定 button 元件 Click 事件共用   myListner
        btIMU.setOnClickListener(myListner);
        btTH.setOnClickListener(myListner);
        btColor.setOnClickListener(myListner);
        btSpO2.setOnClickListener(myListner);
        btAlcohol.setOnClickListener(myListner);
        btMic.setOnClickListener(myListner);
        btPIR.setOnClickListener(myListner);
        btIRI.setOnClickListener(myListner);
        btIRD.setOnClickListener(myListner);
        btUSD.setOnClickListener(myListner);
        btCO.setOnClickListener(myListner);
        btCO2.setOnClickListener(myListner);
        btPressureBMP.setOnClickListener(myListner);
        btPressureLPS.setOnClickListener(myListner);
        btPower.setOnClickListener(myListner);

        tv_MorSensorID.setText(SensorName);
        tv_MorSensorVersion.setText(MorSensor_Version);
        tv_FirmwaveVersion.setText(Firmwave_Version);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, ".onResume()");
        SendCommands = SEND_MORSENSOR_ID;

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        this.getApplicationContext().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //Register BluetoothLe Receiver
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    static byte[] RawCommand = new byte[20];
    private static short[] command={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    // 定義  onClick() 方法
    private Button.OnClickListener myListner=new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId())
            {
                case R.id.btIMU:
                    TempID = IMUID;
                    command = MorSensorCommand.GetSensorData(IMUID);

                    intent.setClass(mMainActivity, IMUViewActivity.class);
//                    intent.setClass(mMainActivity, IMUViewPlusActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btTH:
                    TempID = THID;
                    command = MorSensorCommand.GetSensorData(THID);
                    SendCommands = SEND_MORSENSOR_CONTINUOUS_UV;

                    intent.setClass(mMainActivity, THUViewActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btColor:
                    TempID = ColorID;
                    command = MorSensorCommand.SetLEDOn((short)IN_IN_COLOR_SENSOR_LED);
                    SendCommands = NULL_COMMAND;

                    intent.setClass(mMainActivity, ColorViewActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btSpO2:
                    TempID = SpO2ID;
                    command = MorSensorCommand.SetSpO2SensorLEDOn(SpO2ID);
                    SendCommands = NULL_COMMAND;

                    intent.setClass(mMainActivity, SpO2ViewActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btAlcohol:
                    TempID = AlcoholID;
                    command = MorSensorCommand.GetSensorData(AlcoholID);
                    SendCommands = NULL_COMMAND;

                    intent.setClass(mMainActivity, AlcoholViewActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btMic:
                    TempID = MicID;
                    SendCommands = NULL_COMMAND;

                    intent.setClass(mMainActivity, MicViewActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btPIR:
                    TempID = PIRID;
                    command = MorSensorCommand.GetSensorData(PIRID);
                    SendCommands = NULL_COMMAND;

                    intent.setClass(mMainActivity, PIRViewActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btIRI:
                    TempID = IrIID;
                    SendCommands = NULL_COMMAND;

                    intent.setClass(mMainActivity, IRIViewActivity_2_1.class);
                    startActivity(intent);
                    break;
                case R.id.btIRD:
                    TempID = IRDID;
                    command = MorSensorCommand.GetSensorData(IRDID);
                    SendCommands = NULL_COMMAND;

                    intent.setClass(mMainActivity, IRDViewActivity_2_1.class);
                    startActivity(intent);
                    break;
                case R.id.btUSD:
                    TempID = USDID;
                    command = MorSensorCommand.GetSensorData(USDID);
                    SendCommands = NULL_COMMAND;

                    intent.setClass(mMainActivity, USDViewActivity_2_1.class);
                    startActivity(intent);
                    break;
                case R.id.btCO:
                    TempID = COID;
                    command = MorSensorCommand.GetSensorData(COID);
                    SendCommands = NULL_COMMAND;

                    intent.setClass(mMainActivity, COViewActivity_2_1.class);
                    startActivity(intent);
                    break;
                case R.id.btCO2:
                    TempID = CO2ID;
                    command = MorSensorCommand.GetSensorData(CO2ID);
                    SendCommands = NULL_COMMAND;

                    intent.setClass(mMainActivity, CO2ViewActivity_2_1.class);
                    startActivity(intent);
                    break;
                case R.id.btPressureBMP:
                    TempID = Pressure_BMPID;
                    command = MorSensorCommand.GetSensorData(Pressure_BMPID);
                    SendCommands = NULL_COMMAND;

                    intent.setClass(mMainActivity, PressureActivity_2_1.class);
                    startActivity(intent);
                    break;
                case R.id.btPressureLPS:
                    TempID = Pressure_LPSID;
                    command = MorSensorCommand.GetSensorData(Pressure_LPSID);
                    SendCommands = NULL_COMMAND;

                    intent.setClass(mMainActivity, PressureActivity_2_1.class);
                    startActivity(intent);
                    break;
                case R.id.btPower:
                    TempID = PowerID;
//                    MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_POWER_PERCENTAGE);
//                    MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_POWER_CHARGING);

                    intent.setClass(mMainActivity, PowerActivity_2_1.class);
                    startActivity(intent);
                    break;
            }
            for(int i=0;i<20;i++)
                RawCommand[i]=(byte)command[i];

            // Send Command
            mWriteCharacteristic.setValue(RawCommand);
            mBluetoothLeService.writeCharacteristic(mWriteCharacteristic);
            Log.e(TAG, "OnClickListener " + command[0] + "," + command[1] + "," + command[2] + "," + command[3]);
        }
    };

    public static void SendMorSensorStop() {
        try {
            Thread.sleep(300);
            command = MorSensorCommand.SetStopTransmission(TempID);

            for (int i = 0; i < 20; i++)
                RawCommand[i] = (byte) command[i];

            // Send Command
            mWriteCharacteristic.setValue(RawCommand);
            mBluetoothLeService.writeCharacteristic(mWriteCharacteristic);
            Log.e(TAG,"SendMorSensorStop " + TempID);

            if(TempID == THID){
                Thread.sleep(300);
                command = MorSensorCommand.SetStopTransmission(UVID);

                for (int i = 0; i < 20; i++)
                    RawCommand[i] = (byte) command[i];

                // Send Command
                mWriteCharacteristic.setValue(RawCommand);
                mBluetoothLeService.writeCharacteristic(mWriteCharacteristic);
                Log.e(TAG,"SendMorSensorStop " + TempID);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static short lost1, lost2;
    public static void SendMorSensorCommands(int SendCommand) {
        if(SendCommand == NULL_COMMAND) return;

        try {
            Thread.sleep(300); //Send command delay 300ms            
            for(int i=0;i<command.length;i++){ command[i]=0; } //Initialize command.

            switch (SendCommand){
                case SEND_MORSENSOR_ID: //1 Send MorSensor ID
                    command = MorSensorCommand.GetSensorID();
                    SendCommands = SEND_MORSENSOR_VERSION;
                    break;
                case SEND_MORSENSOR_VERSION: //2 Send MorSensor Version
                    command = MorSensorCommand.GetMorSensorVersion();
                    SendCommands = SEND_FIRMWAVE_VERSION;
                    break;
                case SEND_FIRMWAVE_VERSION: //3 Send Firmwave Version
                    command = MorSensorCommand.GetFirmwareVersion();
                    SendCommands = NULL_COMMAND;
                    break;
                case SEND_MORSENSOR_CONTINUOUS_UV: //4 Send Transmission Mode UV
                    command = MorSensorCommand.GetSensorData(UVID);
                    SendCommands = NULL_COMMAND;
                    break;
                case SEND_MORSENSOR_CONTINUOUS_COLOR: //5 Send Transmission Mode Color
                    command = MorSensorCommand.GetSensorData(ColorID);
                    SendCommands = NULL_COMMAND;
                    break;
                case SEND_MORSENSOR_REGISTER: //106 Send Get Lost Sensor File Data
                    command = MorSensorCommand.GetLostSensorFileData(MicID, lost1, lost2);
                    SendCommands = NULL_COMMAND;
                    break;
                case SEND_MORSENSOR_FILE_DATA: //107 Get File Data (Temp ID) (Mic SpO2)
                    command = MorSensorCommand.GetFileData(TempID);
                    SendCommands = NULL_COMMAND;
                    break;
                case SEND_MORSENSOR_SENSOR_DATA: //109 Send Get Sensor Data (Temp ID)
                    command = MorSensorCommand.GetSensorData(TempID);
                    SendCommands = NULL_COMMAND;
                    break;
                case SEND_MORSENSOR_FILE_DATA_SIZE: //108 Send Get File Data Size (Temp ID)
                    command = MorSensorCommand.GetFileDataSize(TempID);
                    SendCommands = SEND_MORSENSOR_FILE_DATA;
                    break;
                case SEND_MORSENSOR_POWER_PERCENTAGE: //109 Get Power Percentage
                    command = MorSensorCommand.GetPowerPercentage();
                    SendCommands =  NULL_COMMAND;
                    Log.e(TAG, "SEND_MORSENSOR_POWER_PERCENTAGE " + command[0] + "," + command[1] + "," + command[2] + "," + command[3]);
                    break;
                case SEND_MORSENSOR_POWER_CHARGING: //110 Get Power Charging Status
                    command = MorSensorCommand.GetPowerChargingStatus();
                    SendCommands =  NULL_COMMAND;
                    Log.e(TAG, "SEND_MORSENSOR_POWER_CHARGING " + command[0] + "," + command[1] + "," + command[2] + "," + command[3]);
                    break;
            }
            for(int i=0;i<20;i++)
                RawCommand[i]=(byte)command[i];

            // Send Command
            mWriteCharacteristic.setValue(RawCommand);
            mBluetoothLeService.writeCharacteristic(mWriteCharacteristic);
            Log.d(TAG, "SendMorSensorCommands " + command[0] + "," + command[1] + "," + command[2] + "," + command[3]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void BtDisConnect(){
        if(mConnected)
        {
            mConnected = false;
            mDeviceAddress="123";
            if(mBluetoothLeService == null || mWriteCharacteristic == null) return;
            if(receviewData){
                for(int i=0;i<3;i++)
                    SendMorSensorStop();
                receviewData = false;
            }

            mBluetoothLeService.disconnect();
            unregisterReceiver(mGattUpdateReceiver);
//                unbindService(mServiceConnection);
            mBluetoothLeService = null;
            this.finish();
            Log.i(TAG, "BluetoothLe Disconnected.");
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                invalidateOptionsMenu();
                BtDisConnect();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                SendMorSensorCommands(SendCommands);
                Log.e(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                mDeviceData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);

                Decode(DataTransform.hexToBytes(mDeviceData));
                SendMorSensorCommands(SendCommands);
//                Log.e(TAG,"ACTION_DATA_AVAILABLE " +mDeviceData);
            }
        }
    };

    private void setButtonEnabled(boolean mEnabled){
        for(int i=0;i < MorSensorID.length;i++){
            switch (MorSensorID[i]){
                case IMUID:
                    SensorName = "IMUSensor ";
                    btIMU.setEnabled(mEnabled);
                    break;
                case THID:
                    SensorName += "THUSensor ";
                    btTH.setEnabled(mEnabled);
                    break;
                case UVID:
                    btTH.setEnabled(mEnabled);
                    break;
                case ColorID:
                    SensorName += "ColorSensor ";
                    btColor.setEnabled(mEnabled);
                    break;
                case SpO2ID:
                    SensorName += "SpO2Sensor ";
                    btSpO2.setEnabled(mEnabled);
                    break;
                case AlcoholID:
                    SensorName += "AlcoholSensor  ";
                    btAlcohol.setEnabled(mEnabled);
                    break;
                case MicID:
                    SensorName += "MicSensor  ";
                    btMic.setEnabled(mEnabled);
                    break;
                case PIRID:
                    SensorName += "PIRSensor  ";
                    btPIR.setEnabled(mEnabled);
                    break;
                case Pressure_BMPID:
                    SensorName += "BMP_PressureSensor  ";
                    btPressureBMP.setEnabled(mEnabled);
                    break;
                case Pressure_LPSID:
                    SensorName += "LPS_PressureSensor  ";
                    btPressureLPS.setEnabled(mEnabled);
                    break;
                case IrIID:
                    SensorName += "IRISensor  ";
                    btIRI.setEnabled(mEnabled);
                    break;
                case IRDID:
                    SensorName += "IRDSensor  ";
                    btIRD.setEnabled(mEnabled);
                    break;
                case USDID:
                    SensorName += "USDSensor  ";
                    btUSD.setEnabled(mEnabled);
                    break;
                case COID:
                    SensorName += "COSensor  ";
                    btCO.setEnabled(mEnabled);
                    break;
                case CO2ID:
                    SensorName += "CO2Sensor  ";
                    btCO2.setEnabled(mEnabled);
                    break;
                case PowerID:
                    SensorName += "Power  ";
                    btPower.setEnabled(mEnabled);
                    break;
                case CurrentID:
                    SensorName += "Current  ";
                    btPower.setEnabled(mEnabled);
                    break;
                case WiFiID:
                    SensorName += "WiFi  ";
                    break;
                case NFCID:
                    SensorName += "NFC  ";
                    break;
            }
        }
        tv_MorSensorID.setText(SensorName);
    }

    static boolean receviewData = false;
    static String SensorID = "", SensorName = "";
    public void Decode(byte[] values) {
        Intent intent = new Intent();
        String note;
        switch ((short)values[0]) {
            case IN_ECHO:
                note = "i0x01:Echo";
                Log.i(TAG, note);
                break;
            case IN_SENSOR_LIST:
                note = "i0x02:"+values[1]+" sensors discovered";
                Log.i(TAG, note);
                MorSensorID = new short[values[1]];
                for(int i=0;i < MorSensorID.length;i++){
                    MorSensorID[i] = values[i+2];//[discover][number][ID][ID][ID]....
                    if(MorSensorID[i]<0){ SensorID += ((MorSensorID[i]+256) + " "); }
                    else{ SensorID += (MorSensorID[i] + " "); }
                }
                break;
            case IN_MORSENSOR_VERSION:
                note = "i0x03:MorSensor Version "+values[1]+"."+values[2]+"."+values[3];
                Log.i(TAG, note);

                MorSensorVersion[0] = values[1];
                MorSensorVersion[1] = values[2];
                MorSensorVersion[2] = values[3];
                tv_MorSensorVersion.setText(MorSensorVersion[0]+"."+MorSensorVersion[1]+"."+MorSensorVersion[2]);
                MorSensor_Version = MorSensorVersion[0]+"."+MorSensorVersion[1]+"."+MorSensorVersion[2];
                break;
            case IN_FIRMWARE_VERSION:
                note = "i0x04:Firmware Version "+values[1]+"."+values[2]+"."+values[3];
                Log.i(TAG, note);

                FirmwareVersion[0] = values[1];
                FirmwareVersion[1] = values[2];
                FirmwareVersion[2] = values[3];
                tv_FirmwaveVersion.setText(FirmwareVersion[0] + "." + FirmwareVersion[1] + "." + FirmwareVersion[2]);
                Firmwave_Version = FirmwareVersion[0] + "." + FirmwareVersion[1] + "." + FirmwareVersion[2];
                setButtonEnabled(true);
                break;
            case IN_SENSOR_VERSION:
                note = "i0x11:Sensor ID " + values[1] +" SensorVersion "+values[2]+"."+values[3]+"."+values[4];
                Log.i(TAG, note);
                break;
            case IN_REGISTER_CONTENT:
                note = "i0x12:RetrieveRegisterContent - Sensor ID " + values[1] + " Register[H] " + values[2] + " Register[L] " + values[3];
                Log.i(TAG, note);
                switch (values[3]) {
                    case 0x2C: //Power percentage
                        DataTransform.PowerPercentage(values);
                        break;
                    case 0x14: //Charging status
                        DataTransform.PowerChargingStatus(values,mDeviceData);
                        break;
                }
                break;
            case IN_LOST_SENSOR_DATA:
                Log.i(TAG, "i0x13:Send a GetLostSensorData - GetRegisterContent!");
                switch (values[1]){
                    case MicID: //0x13 + SensorID + RegisterAddr(Data)
                        note = "i0x13 :Sensor ID "+values[1]+" Register address "+values[2]+" ... ";
                        Log.i(TAG, note);
                        MicViewActivity.showdblist(values);
                        break;
                    default:
                        Log.i(TAG, "Warning: Unrecognized SensorParameterReport report command!");
                        break;
                }
                break;
            case IN_TRANSMISSION_MODE:
                note = "i0x14:Retrieve Transmission - Sensor ID "+values[1]+" Transmit mode "+values[2];
                Log.i(TAG, note);
                break;
            case IN_SET_TRANSMISSION_MODE:
                note = "i0x21:SetTransmission - Sensor ID "+values[1]+" Transmit mode "+values[2];
                Log.i(TAG, note);
                break;
            case IN_STOP_TRANSMISSION:
                note = "i0x22:StopTransmission - Sensor ID "+values[1]+" Transmit mode "+values[2];
                Log.i(TAG, note);
                setButtonEnabled(true);
                receviewData = false;
                break;
            case IN_SET_REGISTER_CONTENT:
                note = "i0x23:SetregisterContent - Sensor ID "+values[1]+" Register[H]:"+values[2]+" Register[L]:"+values[3];
                Log.i(TAG, note);
                break;
            case IN_MODIFY_LED_STATE:
                note = "i0x31:Set LED State - LED ID "+values[1]+" On/Off " + values[2]; //0x00_Off 0x01_On
                Log.i(TAG, note);

                switch (values[1]){
                    case IN_IN_MCU_LED_D2:
                        break;
                    case IN_IN_MCU_LED_D3:
                        break;
                    case IN_IN_COLOR_SENSOR_LED:
                        SendMorSensorCommands(SEND_MORSENSOR_CONTINUOUS_COLOR);
                        break;
                    default:
                        Log.i(TAG, "Warning: Unrecognized SensorParameterReport report command!");
                        break;
                }
                break;
            case IN_FILE_DATA_SIZE:
                Log.i(TAG, "0xF1:File Data Size -  Sensor ID " + values[1] + "DataSize[H] " + values[2] + "DataSize[L] " + values[3]);

                switch (values[1]){
                    case SpO2ID: //0xF1 + 0xA0 + DataSize[H] + DataSize[L]
                        SendMorSensorCommands(SEND_MORSENSOR_FILE_DATA);
                        DataTransform.TransformSpO2(mDeviceData);
                        break;
                    case MicID: //0xF1 + 0xA4 + DataSize[H] + DataSize[L]
                        SendMorSensorCommands(SEND_MORSENSOR_FILE_DATA);
                        MicViewActivity.showdblist(values);
                        break;
                    case IrIID:
                        IRIViewActivity_2_1.DisplayPicture(values);
                        break;
                    default:
                        Log.i(TAG, "Warning: Unrecognized SensorParameterReport report command!");
                        break;
                }
                break;
            case IN_FILE_DATA:
                Log.i(TAG, "0xF2:File Data Received - Sensor ID " + values[1] + " Index:" + ((values[2] & 0xFF)<<8 | (values[3] & 0xFF)) + " Seq[H]:" + values[2] + " Seq[L]:" + values[3]);

                switch (values[1]){
                    case SpO2ID: //0xF2 + 0xA0 + Seq[H] + Seq[L] + Data ...
                        DataTransform.TransformSpO2(mDeviceData);
                        break;
                    case MicID: //0xF2 + 0xA4 + Seq[H] + Seq[L] + Data ...
                        MicViewActivity.showdblist(values);
                        break;
                    case IrIID:
                        IRIViewActivity_2_1.DisplayPicture(values);
                        break;
                    default:
                        Log.i(TAG, "Warning: Unrecognized SensorParameterReport report command!");
                        break;
                }
                break;
            case IN_SENSOR_DATA: //0xF3+ sensorID + sensorData
                note = "0xF3:Sensor data received " + values[1];
                Log.i(TAG, note);
                receviewData = true;
                setButtonEnabled(false);

                tempmDeviceData+=mDeviceData+",";
                receviewCount++;
                if(receviewCount == 500){
                    ControlSDCard.WriteSDCard(tempmDeviceData);
                    receviewCount = 0;
                    tempmDeviceData = "";
                }

                if(values[1] == IMUID){ DataTransform.TransformIMU(values);  }
                else if(values[1] == THID){
                    if(!UVStart){ SendMorSensorCommands(SEND_MORSENSOR_CONTINUOUS_UV); }
                    DataTransform.TransformTempHumi(values); }
                else if(values[1] == UVID){  UVStart = true; DataTransform.TransformUV(values); }
                else if(values[1] == ColorID){ DataTransform.TransformColor(values); }
                else if(values[1] == PIRID){ DataTransform.TransformPIR(values); }
                else if(values[1] == AlcoholID){ DataTransform.TransformAlcohol(values); }
                else if(values[1] == IRDID){ DataTransform.TransformIrD(values); }
                else if(values[1] == USDID){ DataTransform.TransformUSD(values); }
                else if(values[1] == Pressure_BMPID){ DataTransform.TransformBMPPressure(values); }
                else if(values[1] == Pressure_LPSID){ DataTransform.TransformLPSPressure(values); }
                else if(values[1] == COID){ DataTransform.TransformCO(values); }
                else if(values[1] == CO2ID){ DataTransform.TransformCO2(values); }
                else if(values[1] == MicID){ MicViewActivity.displaydBData(values); }

                break;
            default:
                Log.e(TAG, "Warning:Incorrect output command format! " + mDeviceData);
                break;
        }
        Log.d(TAG,"mDeviceData:"+mDeviceData);
    }
    static String tempmDeviceData = "";
    int receviewCount = 0;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            if(!mConnected){
                mBluetoothLeService.connect(mDeviceAddress);
                mConnected = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mConnected = false;
            mBluetoothLeService = null;
        }
    };


    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);

                // Read
                if(gattCharacteristic.getUuid().toString().contains("00002a37-0000-1000-8000-00805f9b34fb"))
                {
                    final int charaProp = gattCharacteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
                        if (mReadCharacteristic != null) {
                            mBluetoothLeService.setCharacteristicNotification(
                                    mReadCharacteristic, false);
                            mReadCharacteristic = null;
                        }
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mReadCharacteristic = null;
                        mReadCharacteristic = gattCharacteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                gattCharacteristic, true);
                    }
                }

                // SendCommands
                if(gattCharacteristic.getUuid().toString().contains("00001525-1212-efde-1523-785feabcd123"))
                {
                    final int charaProp = gattCharacteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
                        if (mWriteCharacteristic != null) {
                            mWriteCharacteristic = null;
                        }
                        mWriteCharacteristic = gattCharacteristic;
                    }
                }
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    public  boolean  onKeyDown ( int  keyCode ,  KeyEvent event )  {
        if  ( keyCode  ==  KeyEvent. KEYCODE_BACK )  {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //創建訊息方塊
            builder.setMessage("確定要離開？");
            builder.setTitle("離開");
            builder.setPositiveButton("確認", new DialogInterface.OnClickListener()  {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); //dismiss為關閉dialog,Activity還會保留dialog的狀態
                    if(DeviceScanActivity.mDeviceScanActivity!=null)
                        DeviceScanActivity.mDeviceScanActivity.finish();

                    BtDisConnect();
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener()  {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); //dismiss為關閉dialog,Activity還會保留dialog的狀態
                }
            });
            builder.create().show();
            return  false ;
        }

        return  super.onKeyDown ( keyCode ,  event );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
