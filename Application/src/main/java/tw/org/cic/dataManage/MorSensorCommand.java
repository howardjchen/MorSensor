package tw.org.cic.dataManage;


import android.util.Log;

public class MorSensorCommand {
    protected static final String TAG = "MorSensorCommands";
    public static final int MAX_COMMAND_LENGTH = 20;

    /* output commands */
    public static final int OUT_ECHO = 1; //0x01
    public static final int OUT_SENSOR_LIST = 2; //0x02 Internal use
    public static final int OUT_MORSENSOR_VERSION = 3; //0x03 Internal use
    public static final int OUT_FIRMWARE_VERSION = 4; //0x04 Internal use
    public static final int OUT_SENSOR_VERSION = 17; //0x11
    public static final int OUT_REGISTER_CONTENT = 18; //0x12 Internal use
    public static final int OUT_LOST_SENSOR_DATA = 19; //0x13
    public static final int OUT_TRANSMISSION_MODE = 20; //0x14 Internal use
    public static final int OUT_SET_TRANSMISSION_MODE = 33; //0x21
            //Third level commands for 0x21
            public static final int OUT_OUT_SET_TRANSMISSION_SINGLE = 0; //0x00
            public static final int OUT_OUT_SET_TRANSMISSION_CONTINUOUS = 1; //0x01
    public static final int OUT_STOP_TRANSMISSION = 34; //0x22
    public static final int OUT_SET_REGISTER_CONTENT = 35; //0x23
    public static final int OUT_MODIFY_LED_STATE = 49; //0x31
        //Second level commands for 0x31
        public static final int OUT_OUT_MODIFY_MCU_LED_D2 = 1; //0x01
        public static final int OUT_OUT_MODIFY_MCU_LED_D3 = 2; //0x02
        public static final int OUT_OUT_MODIFY_COLOR_SENSOR_LED = 3; //0x03
            //Third level commands for 0x01~0x03
            public static final int OUT_OUT_OUT_MODIFY_LED_OFF = 0; //0x00
            public static final int OUT_OUT_OUT_MODIFY_LED_ON = 1; //0x01
    public static final int OUT_FILE_DATA_SIZE = 241 - 256; //0xF1
    public static final int OUT_FILE_DATA = 242 - 256; //0xF2
    public static final int OUT_SENSOR_DATA = 243 - 256; //0xF3

    public static short[] command={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    //Encode
    public static short[] GetEcho(){
        Log.i(TAG, "o0x01:Echo command!");
        command[0] = OUT_ECHO;
        return command;
    }

    public static short[] GetSensorID(){
        Log.i(TAG, "o0x02:Send a GetAllSensorID command!");
        command[0] = OUT_SENSOR_LIST;
        return command;
    }

    public static short[] GetMorSensorVersion(){
        Log.i(TAG, "o0x03:Send a GetMorSensorVersion command!");
        command[0] = OUT_MORSENSOR_VERSION;
        return command;
    }

    public static short[] GetFirmwareVersion(){
        Log.i(TAG, "o0x04:Send a GetFirmwareVersion command!");
        command[0] = OUT_FIRMWARE_VERSION;
        return command;
    }

    public static short[] GetLostSensorFileData(short SensorID, short lost1, short lost2){
        Log.i(TAG, "o0x13 SensorID:Send a GetLostSensorData - GetRegisterContent command!");
        command[0] = OUT_LOST_SENSOR_DATA;
        command[1] = SensorID;
        command[2] = lost1;
        command[3] = lost2;
        return command;
    }

    public static short[] SetTransmissionSingle(short SensorID){
        Log.i(TAG, "o0x21 0x00:Send a SetTransmission - SetTransmitMode Single mode command!");
        command[0] = OUT_SET_TRANSMISSION_MODE;
        command[1] = SensorID;
        command[2] = OUT_OUT_SET_TRANSMISSION_SINGLE;
        return command;
    }

    public static short[] SetTransmissionContinuous(short SensorID){
        Log.i(TAG, "o0x21 0x01:Send a SetTransmission - SetTransmitMode Continuous mode command!");
        command[0] = OUT_SET_TRANSMISSION_MODE;
        command[1] = SensorID;
        command[2] = OUT_OUT_SET_TRANSMISSION_CONTINUOUS;
        return command;
    }

    public static short[] SetStopTransmission(short SensorID){
        Log.i(TAG, "o0x22:Send a SetStopTransmission - SetTransmitMode Stop mode command!");
        command[0] = OUT_STOP_TRANSMISSION;
        command[1] = SensorID;
        return command;
    }

    public static short[] SetSpO2SensorLEDOn(short SensorID){
        Log.i(TAG, "o0x23:Send a SetRegisterContent - SpO2 LED Open command!");
        command[0] = OUT_SET_REGISTER_CONTENT; //0x23
        command[1] = SensorID;
        command[2] = 0;  //0x00
        command[3] = 34; //0x22
        command[4] = 3;  //0x03
        command[5] = 1;  //0x01
        command[6] = 9;  //0x09
        command[7] = 9;  //0x09
        return command;
    }

    public static short[] SetLEDOff(short LEDID){
        Log.i(TAG, "o0x31 0xID 0x00:Modify LED State - Set LED Off command!");
        command[0] = OUT_MODIFY_LED_STATE;
        command[1] = LEDID;
        command[2] = OUT_OUT_OUT_MODIFY_LED_OFF;
        return command;
    }

    public static short[] SetLEDOn(short LEDID){
        Log.i(TAG, "o0x31 0xID 0x01:Modify LED State - Set LED On command!");
        command[0] = OUT_MODIFY_LED_STATE;
        command[1] = LEDID;
        command[2] = OUT_OUT_OUT_MODIFY_LED_ON;
        return command;
    }

    public static short[] GetFileDataSize(short SensorID){
        Log.i(TAG, "o0xF2 0x02:Get File Data command!");
        command[0] = OUT_FILE_DATA_SIZE;
        command[1] = SensorID;
        return command;
    }

    public static short[] GetFileData(short SensorID){
        Log.i(TAG, "o0xF2 0x02:Get File Data command!");
        command[0] = OUT_FILE_DATA;
        command[1] = SensorID;
        return command;
    }

    public static short[] GetSensorData(short SensorID){
        Log.i(TAG, "o0xF3:Get Sensor Data command!");
        command[0] = OUT_SENSOR_DATA;
        command[1] = SensorID;
        return command;
    }

    public static short[] GetPowerPercentage(){
        Log.i(TAG, "o0x12:Get Register Content - Power Percentage command!");
        command[0] = OUT_REGISTER_CONTENT; //0x12
        command[1] = 0xAA; //0xAA
        command[2] = 0x00;  //0x00
        command[3] = 0x2C; //0x22
        return command;
    }
    public static short[] GetPowerChargingStatus(){
        Log.i(TAG, "o0x12:Get Register Content - Power Charging Status command!");
        command[0] = OUT_REGISTER_CONTENT; //0x12
        command[1] = 0xAA; //0xAA
        command[2] = 0x00;  //0x00
        command[3] = 0x14; //0x14
        return command;
    }
}
