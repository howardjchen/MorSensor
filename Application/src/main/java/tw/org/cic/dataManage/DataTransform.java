package tw.org.cic.dataManage;

import android.util.Log;
import android.widget.Toast;

import tw.org.cic.control_activity.MainActivity;
import tw.org.cic.morsenser_example.AlcoholViewActivity;
import tw.org.cic.morsenser_example.ColorViewActivity;
import tw.org.cic.morsenser_example.IMUViewActivity;
import tw.org.cic.morsenser_example.IMUViewPlusActivity;
import tw.org.cic.morsenser_example.PIRViewActivity;
import tw.org.cic.morsenser_example.SpO2ViewActivity;
import tw.org.cic.morsenser_example.THUViewActivity;
import tw.org.cic.morsensor_example_3.CO2ViewActivity_2_1;
import tw.org.cic.morsensor_example_3.COViewActivity_2_1;
import tw.org.cic.morsensor_example_3.IRDViewActivity_2_1;
import tw.org.cic.morsensor_example_3.PowerActivity_2_1;
import tw.org.cic.morsensor_example_3.PressureActivity_2_1;
import tw.org.cic.morsensor_example_3.USDViewActivity_2_1;

/**
 * Created by 1404011 on 2015/5/4.
 */
public class DataTransform {
    private static final String TAG = "DataTransform";
    static float data[] = new float[12];
    static short RawData[] = new short[20];

    public static float[] getData() {
        return data;
    }

    private static void CheckNegative(byte[] value) {
        for (int i = 2; i < 20; i++) {
            RawData[i] = (short) (value[i] & 0x00FF);
//            RawData[i] = (short)value[i];
//            if(value[i] < 0) { RawData[i] = (short)(value[i] + 256); }
        }
    }

    public static boolean PowerChargingStatus = false;
    public static short PowerPercentage = 0;

    public static void PowerPercentage(byte[] value) {
        data[0] = (short) value[6];
        PowerActivity_2_1.DisplayPowerData();
        Log.e(TAG, "Power Percentage:" + PowerPercentage);
    }

    public static void PowerChargingStatus(byte[] value, String hex) {
        if (MainActivity.TempID == MainActivity.AlcoholID) {
            Log.e(TAG, "Power Current:" + (value[5] << 8 | value[6] & 0xFF));
            if ((short) (value[5] << 8 | value[6] & 0xFF) >= -235) {
                PowerChargingStatus = true;
            } else
                PowerChargingStatus = false;
        } else {
            if ((short) (value[5] << 8 | value[6] & 0xFF) >= 0) {
                PowerChargingStatus = true;
            } else
                PowerChargingStatus = false;
        }
        if (PowerChargingStatus) data[1] = 1;
        else data[1] = 0;

        PowerActivity_2_1.DisplayPowerData();
    }

    public static void TransformIMU(byte[] value) {
        //Gryo: value[2][3] / 32.8 (+-1000 °/sec)
        data[0] = convertTwoBytesToShortsigned(value[2], value[3]) / 32.8f; //Gryo x
        data[1] = convertTwoBytesToShortsigned(value[4], value[5]) / 32.8f; //Gryo y
        data[2] = convertTwoBytesToShortsigned(value[6], value[7]) / 32.8f; //Gryo z

        //Acc: value[8][9] / 4096 (+-8 g)
        data[3] = convertTwoBytesToShortsigned(value[8], value[9]) / 4096f; //Acc x
        data[4] = convertTwoBytesToShortsigned(value[10], value[11]) / 4096f; //Acc y
        data[5] = convertTwoBytesToShortsigned(value[12], value[13]) / 4096f; //Acc z

        //Mag: value[15][14] / 3.41 / 100 (??:MagZ ???-1value (+-1200 μT,μ=10^-6)
        data[7] = convertTwoBytesToShortsigned(value[15], value[14]) / 3.41f / 100f; //Mag x
        data[6] = convertTwoBytesToShortsigned(value[17], value[16]) / 3.41f / 100f; //Mag y
        data[8] = convertTwoBytesToShortsigned(value[19], value[18]) / 3.41f / -100f; //Mag z

        data[9] = 1;
        data[10] = 2;
        data[11] = 3;


//        IMUViewActivity.DisplayIMUData();
          IMUViewPlusActivity.DisplayIMUData();
    }

    public static void TransformTempHumi(byte[] value) {
        float Temp_data = convertTwoBytesToIntUnsigned(value[2], value[3]);
        float Hemi_data = convertTwoBytesToIntUnsigned(value[4], value[5]);

        data[1] = (float) (Temp_data * 175.72 / 65536.0 - 46.85); //Temp
        data[2] = (float) (Hemi_data * 125.0 / 65536.0 - 6.0); //RH

        THUViewActivity.DisplayTHUData();
    }

    public static void TransformUV(byte[] value) {
        float UV_data = convertTwoBytesToIntUnsigned(value[3], value[2]);

        data[0] = (float) (UV_data / 100.0); //UV

        THUViewActivity.DisplayTHUData();
    }

    public static float RedCalibration = 1;
    public static float GreenCalibration = 1;
    public static float BlueCalibration = 1;

    public static String Red = "FF";
    public static String Green = "FF";
    public static String Blue = "FF";

    public static void TransformColor(byte[] value) {
        data[0] = (value[3] & 0xFF);
        data[1] = (value[5] & 0xFF);
        data[2] = (value[7] & 0xFF);

        if (ColorViewActivity.Calibration) {
            RedCalibration = 255f / data[0];
            GreenCalibration = 255f / data[1];
            BlueCalibration = 255f / data[2];

            ColorViewActivity.Calibration = false;
        }

        data[0] *= RedCalibration;
        data[1] *= GreenCalibration;
        data[2] *= BlueCalibration;
        if (data[0] > 255) {
            data[0] = 255;
        }
        if (data[1] > 255) {
            data[1] = 255;
        }
        if (data[2] > 255) {
            data[2] = 255;
        }

        Red = Integer.toHexString((int) data[0] & 0xFF);
        Green = Integer.toHexString((int) data[1] & 0xFF);
        Blue = Integer.toHexString((int) data[2] & 0xFF);

        if (Red.length() == 1) {
            Red = "0" + Red;
        }
        if (Green.length() == 1) {
            Green = "0" + Green;
        }
        if (Blue.length() == 1) {
            Blue = "0" + Blue;
        }

//        Log.d(TAG, "Red:" + Red + " Green:" + Green + " Blue:" + Blue);
        ColorViewActivity.DisplayColorData();
    }

    public static void TransformPIR(byte[] value) {
        // <10cm ALC_out=3.3 ; >10cm ALC_out=0
        data[0] = convertTwoBytesToIntUnsigned(value[2], value[3]) / 4096.0f * 3.3f;

        PIRViewActivity.DisplayPIRData();
    }

    static float a, b, a_p, ReadConter = 0;

    public static void TransformAlcohol(byte[] value) {
        ReadConter++;
        CheckNegative(value);

        float Alc_out;
        Alc_out = convertTwoBytesToIntUnsigned(value[2], value[3]) / 4096.0f * 3.3f;

        //歸零校正
        if (ReadConter == 1) {
            a = 1f / (1.8f - Alc_out);
            b = 1.8f * (1f - a);
            data[2] = a;
            data[3] = b;
        }

        //Check Voltage
        data[1] = Alc_out;
        a_p = a * Alc_out + b;
        data[4] = a_p;

        if (Alc_out >= 1.8) { //y = -2.9427*Alc_out^2 + 15.225*Alc_out - 17.551
            data[0] = -2.9427f * a_p * a_p + 15.225f * a_p - 17.551f;
        } else { //y = 0.1586*Alc_out^2 - 0.1199*Alc_out
            data[0] = 0.1586f * a_p * a_p - 0.1199f * a_p;
        }

        AlcoholViewActivity.DisplayAlcoholData();
    }

    // Byte[] to HexString
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        for (byte byteChar : bytes) {
            sb.append(String.format("%02X", byteChar));
        }
        return sb.toString();
    }

    // HexString to Byte[]
    public static byte[] hexToBytes(String hexString) {
        char[] hex = hexString.toCharArray();
        //轉rawData長度減半
        int length = hex.length / 2;
        byte[] rawData = new byte[length];
        for (int i = 0; i < length; i++) {
            //先將hex資料轉10進位數值
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            //將第一個值的二進位值左平移4位,ex: 00001000 => 10000000 (8=>128)
            //然後與第二個值的二進位值作聯集ex: 10000000 | 00001100 => 10001100 (137)
            int value = (high << 4) | low;
            //與FFFFFFFF作補集
            if (value > 127)
                value -= 256;
            //最後轉回byte就OK
            rawData[i] = (byte) value;
        }
        return rawData;
    }

    static int count = 0;
    static int[] LED = new int[2];
    static float IR = 0, RED = 0, SpO2Data = 0, HeartRateData = 0;
    static float Red_DC = 0, Red_AC = 0, IR_DC = 0, IR_AC = 0;
    static float Red_DC_Raw = 0, Red_AC_Raw_Max = 0, Red_AC_Raw_Min = 0;
    static float IR_DC_Raw = 0, IR_AC_Raw_Max = 0, IR_AC_Raw_Min = 0;
    static double[] RawIRdata = new double[1024];

    public static void TransformSpO2(String value) {
        if (value.equals("F1A0080000000000000000000000000000000000")) { //1024
            Log.i(TAG, "Initial");
            count = 0;
            IR = 0;
            RED = 0;
            SpO2Data = 0;
            HeartRateData = 0;
            Red_DC = 0;
            Red_AC = 0;
            IR_DC = 0;
            IR_AC = 0;
            Red_DC_Raw = 0;
            Red_AC_Raw_Max = 0;
            Red_AC_Raw_Min = 0;
            IR_DC_Raw = 0;
            IR_AC_Raw_Max = 0;
            IR_AC_Raw_Min = 0;

            for (int i = 0; i < 1024; i++) {
                RawIRdata[i] = 0;
            }
            return;
        }

        if (value.substring(8, 36).equals("00000000000000000000000000000000")) {
            if (count < 3)
                Toast.makeText(MainActivity.mMainActivity, "MorSensor not find the SD card.", Toast.LENGTH_SHORT).show();
            return;
        }

        LED = SpO2Transform.RawDataToData(SpO2Transform.ASCIItoHex(value, 8));

        count++;

        if (count % 2 == 0) { //LED1 = IR
            data[2] = SpO2Transform.FixedToFloatLED(LED[0]); //IR
            data[3] = SpO2Transform.FixedToFloatLED(LED[1]); //IR A

            IR_DC_Raw += data[2];
            if (count == 2) {
                IR_AC_Raw_Max = data[2];
                IR_AC_Raw_Min = data[2];
            }

            if (IR_AC_Raw_Max < data[2]) {
                IR_AC_Raw_Max = data[2];
            }
            if (IR_AC_Raw_Min > data[2]) {
                IR_AC_Raw_Min = data[2];
            }

            // 均方根、四捨五入小數第五位( round(sqrt(value/count) * x) / x)
            // round:四捨五入 sqrt:開根號 count:資料數 x:小數第幾位(10的倍數)
            Red_AC = SpO2Transform.FixedToFloatAC(Red_AC_Raw_Max, Red_AC_Raw_Min);
            Red_DC = SpO2Transform.FixedToFloatDC(Red_DC_Raw, (count / 2 + 1));
            IR_AC = SpO2Transform.FixedToFloatAC(IR_AC_Raw_Max, IR_AC_Raw_Min);
            IR_DC = SpO2Transform.FixedToFloatDC(IR_DC_Raw, (count / 2 + 1));

            IR = IR_AC / IR_DC;
            RED = Red_AC / Red_DC;

            RawIRdata[count / 2 - 1] = data[2]; //IR
            if (data[2] == 0) {
                RawIRdata[count / 2 - 1] = 0;
            }

            data[4] = SpO2Data = (int) (SpO2Transform.FixedToFloatSpO2(RED, IR) * 100) / 100f;
//            data[4] = SpO2Data = (int)(SpO2Transform.FixedToFloatSpO2(IR, RED) * 100) / 100f;
            data[5] = HeartRateData = 0; //Heart Rate
            data[6] = count / 2;

            if (data[6] == 1024) {
                data[5] = HeartRateData = (int) (SpO2Transform.FixedToFloatHeartRate(RawIRdata) * 10) / 10f;
                Log.d(TAG, "count:" + data[6] + " SpO2Data:" + SpO2Data + " HeartRateData:" + HeartRateData + " IR:" + IR + " RED:" + RED + " RawIRdata[0]" + RawIRdata[0] + " RawIRdata[1]" + RawIRdata[1]);
            }

            SpO2ViewActivity.DisplaySpO2Data();

        } else { //LED2 = RED
            data[0] = (float) LED[0] * 1.2f / (float) Math.pow(2, 21); //RED
            data[1] = (float) LED[1] * 1.2f / (float) Math.pow(2, 21); //RED A

            Red_DC_Raw += data[0];
            if (count == 1) {
                Red_AC_Raw_Max = data[0];
                Red_AC_Raw_Min = data[0];
            }

            if (Red_AC_Raw_Max < data[0]) {
                Red_AC_Raw_Max = data[0];
            }
            if (Red_AC_Raw_Min > data[0]) {
                Red_AC_Raw_Min = data[0];
            }
        }
    }

    /**
     * cm - (mV_Raw - mV)/Interval
     * cm　　 mV   Interval
     * 10　　1886　　832
     * 20　　1054　　286
     * 30　　 768　  126
     * 40　　 642　　 81
     * 50　　 561　　 49
     * 60　　 512　　 40
     * 70　　 472　　 31
     * 80　　 441　　 27
     * 90　　 414　　114
     * 150　　 300
     */
    static float Voltage = 0, Data;

    public static void TransformIrD(byte[] value) {
        Voltage = convertTwoBytesToIntUnsigned(value[2], value[3]) / 4096f * 3300f;
//        Voltage = (float) (values[2] << 8 | values[3] & 0xFF);

        if (Voltage >= 1886)
            Data = 10;
        else if (Voltage >= 1054)
            Data = 20 - (Voltage - 1054) / 832 * 10;
        else if (Voltage >= 768)
            Data = 30 - (Voltage - 768) / 286 * 10;
        else if (Voltage >= 642)
            Data = 40 - (Voltage - 642) / 126 * 10;
        else if (Voltage >= 561)
            Data = 50 - (Voltage - 561) / 81 * 10;
        else if (Voltage >= 512)
            Data = 60 - (Voltage - 512) / 49 * 10;
        else if (Voltage >= 472)
            Data = 70 - (Voltage - 472) / 40 * 10;
        else if (Voltage >= 441)
            Data = 80 - (Voltage - 441) / 31 * 10;
        else if (Voltage >= 414)
            Data = 90 - (Voltage - 414) / 27 * 10;
        else if (Voltage >= 300)
            Data = 150 - (Voltage - 300) / 114 * 60;
        else if (Voltage < 300)
            Data = 150;

        data[0] = Voltage;
        data[1] = Data;

        IRDViewActivity_2_1.DisplayIrDData();
    }

    public static void TransformUSD(byte[] value) {
        Voltage = convertTwoBytesToIntUnsigned(value[2], value[3]);
        Data = Voltage / 6.4f * 2.54f; //USD 6.4mV = 1in; 1in * 2.54 = ?cm

        data[0] = Voltage;
        data[1] = Data;

        USDViewActivity_2_1.DisplayUSDData();
    }

    public static void TransformBMPPressure(byte[] value) {
        ReadConter++;
        float Alc_out;
        Alc_out = (float) (convertTwoBytesToIntUnsigned(value[2], value[3]) / 4096.0 * 3.3);
        if (value[2] < 0 && value[3] < 0) //check overflow(ALC_out = 3.3 * raw_data / 4096)
            Alc_out = (float) (convertTwoBytesToIntUnsigned(value[2], value[3]) / 4096.0 * 3.3);
        else if (value[2] < 0) //check overflow(ALC_out = 3.3 * raw_data / 4096)
            Alc_out = (float) (convertTwoBytesToIntUnsigned(value[2], value[3]) / 4096.0 * 3.3);
        else if (value[3] < 0) //check overflow(ALC_out = 3.3 * raw_data / 4096)
            Alc_out = (float) (convertTwoBytesToIntUnsigned(value[2], value[3]) / 4096.0 * 3.3);

        //歸零校正
        if (ReadConter == 1) {
            a = 1f / (1.8f - Alc_out);
            b = 1.8f * (1f - a);
        }
        a_p = a * Alc_out + b;

        data[0] = Alc_out;
        if (data[0] < 0) data[0] = 0;

        if (PressureActivity_2_1.mPressureActivity != null)
            PressureActivity_2_1.DisplayPressureData();
    }

    public static void TransformLPSPressure(byte[] value) {
        //pressure = value[4][3][2] / 4096 * 0.75
        data[0] = (float) ((value[4] & 0xFF) << 16 | (value[3] & 0xFF) << 8 | (value[2] & 0xFF)) / 4096f * 0.75f; //壓力
        data[1] = convertTwoBytesToShortUnsigned(value[6], value[5]) / 480f + 42.5f; //溫度
        if (PressureActivity_2_1.mPressureActivity != null)
            PressureActivity_2_1.DisplayPressureData();
    }

    public static void TransformCO(byte[] value) {
        //Voltage = 76.294 * value / 1000000;
        //CO = (Voltage * 1000 - 508.63) / 1.0424
        Voltage = 76.294f * convertTwoBytesToIntUnsigned(value[2], value[3]) / 1000000f;
        data[0] = ((76.294f * convertTwoBytesToIntUnsigned(value[2], value[3]) / 1000f) - 508.63f) / 1.0424f;

        COViewActivity_2_1.DisplayCOData();
    }

    public static void TransformCO2(byte[] value) { //
        Voltage = convertTwoBytesToIntUnsigned(value[2], value[3]) * 4096f / 3300f;
        if (Voltage <= 1800)
            data[0] = (float) (1.18 * Math.pow(10, 6) * Math.exp(-0.00427 * Voltage));
        else if (Voltage > 1800 && Voltage <= 2200)
            data[0] = (float) (3.118 * Math.pow(10, 6) * Math.exp(-0.004099 * Voltage));
        else if (Voltage > 2200 && Voltage <= 2700)
            data[0] = (float) (1.147 * Math.pow(10, 7) * Math.exp(-0.003789 * Voltage));
        else if (Voltage > 2700 && Voltage <= 2900)
            data[0] = (float) (2.884 * Math.pow(10, 7) * Math.exp(-0.003787 * Voltage));
        else if (Voltage > 2900)
            data[0] = (float) (1.43 * Math.pow(10, 8) * Math.exp(-0.004074 * Voltage));

        CO2ViewActivity_2_1.DisplayCO2Data();
    }


    public static int convertTwoBytesToIntUnsigned(byte b1, byte b2)      // unsigned
    {
        return (b1 & 0xFF) << 8 | (b2 & 0xFF);
    }
    public static short convertTwoBytesToShortUnsigned(byte b1, byte b2)      // unsigned
    {
        return (short)((b1 & 0xFF) << 8 | (b2 & 0xFF));
    }
    public static short convertTwoBytesToShortsigned(byte b1, byte b2)      // signed
    {
        return (short)(b1 << 8 | (b2 & 0xFF));
    }
}
