package tw.org.cic.dataManage;


import android.util.Log;

/**
 * Created by 1404011 on 2015/4/23.
 */

public class SpO2Transform {
    private static final String TAG = "SpO2Transform";

    public static String[] ASCIItoHex(String ASCII, int j){
        String[] data_Raw = new String[13];
        for(int i=0;i<13;i++){
            if(ASCII.substring(j,j+2).equals("30")){//ASCII (Hex:30 = 0)
                data_Raw[i] = "0";
            }else if(ASCII.substring(j,j+2).equals("31")){//ASCII (Hex:31 = 1)
                data_Raw[i] = "1";
            }else if(ASCII.substring(j,j+2).equals("32")){//ASCII (Hex:32 = 2)
                data_Raw[i] = "2";
            }else if(ASCII.substring(j,j+2).equals("33")){//ASCII (Hex:33 = 3)
                data_Raw[i] = "3";
            }else if(ASCII.substring(j,j+2).equals("34")){//ASCII (Hex:34 = 4)
                data_Raw[i] = "4";
            }else if(ASCII.substring(j,j+2).equals("35")){//ASCII (Hex:35 = 5)
                data_Raw[i] = "5";
            }else if(ASCII.substring(j,j+2).equals("36")){//ASCII (Hex:36 = 6)
                data_Raw[i] = "6";
            }else if(ASCII.substring(j,j+2).equals("37")){//ASCII (Hex:37 = 7)
                data_Raw[i] = "7";
            }else if(ASCII.substring(j,j+2).equals("38")){//ASCII (Hex:38 = 8)
                data_Raw[i] = "8";
            }else if(ASCII.substring(j,j+2).equals("39")){//ASCII (Hex:39 = 9)
                data_Raw[i] = "9";
            }else if(ASCII.substring(j,j+2).equals("41")){//ASCII (Hex:41 = A)
                data_Raw[i] = "A";
            }else if(ASCII.substring(j,j+2).equals("42")){//ASCII (Hex:42 = B)
                data_Raw[i] = "B";
            }else if(ASCII.substring(j,j+2).equals("43")){//ASCII (Hex:43 = C)
                data_Raw[i] = "C";
            }else if(ASCII.substring(j,j+2).equals("44")){//ASCII (Hex:44 = D)
                data_Raw[i] = "D";
            }else if(ASCII.substring(j,j+2).equals("45")){//ASCII (Hex:45 = E)
                data_Raw[i] = "E";
            }else if(ASCII.substring(j,j+2).equals("46")){//ASCII (Hex:46 = F)
                data_Raw[i] = "F";
            }else if(ASCII.substring(j,j+2).equals("2C")){//ASCII (Hex:2C = ,)
                data_Raw[i] = ",";
            }else if(ASCII.substring(j,j+2).equals("00")){//ASCII (Hex:00 = null)
                data_Raw[i] = "null null null null";
            }
            j+=2;
        }
        return data_Raw;
    }

    public static int[] RawDataToData(String[] RawData){
        String data_value="",data_value2="";
        for(int i=0;i<6;i++){
            data_value += RawData[i];
            data_value2 += RawData[i+7];
        }

        int LED=0,LEDA=0;
        if(data_value!=null && data_value2!=null && data_value.length()<=6 && data_value2.length()<=6){
            LED = Integer.parseInt(data_value, 16);
            LEDA = Integer.parseInt(data_value2, 16);
            //Check negative
            if (Integer.parseInt(data_value.substring(0, 1), 16) >= 8) { LED -= 16777216; }
            if (Integer.parseInt(data_value2.substring(0, 1), 16) >= 8) { LEDA -= 16777216; }
        }
        return new int[]{ LED, LEDA };
    }

    //LED = V * 1.2 / 2^21;
    public static float FixedToFloatLED(float fixedValue) {
        return (float) Math.round((float) (fixedValue * 1.2 / (float) Math.pow(2, 21)) * 100000f) / 100000f;
//        return (float) Math.round((float) (fixedValue * 1.2 * 100000000f)) / 100000000f;
    }

    // AC(round((Max-Min)/sqrt(2) * x) / x)
    // round:四捨五入 sqrt:開根號 x:小數位數(10的倍數)
    public static float FixedToFloatAC(float fixedValueMax, float fixedValueMin){
        return (float) Math.round((fixedValueMax - fixedValueMin) / (float) Math.sqrt(2) * 100000f) / 100000f;
//        return (float) Math.round((fixedValueMax - fixedValueMin) * 100000000f) / 100000000f;
    }

    // 取平均數
    public static float FixedToFloatDC(float fixedValue, int counter){
        return fixedValue / (float)(counter / 2);
    }

    // (1 / (((RED / IR) * 2.578 - 1) / (10 - (RED / IR) * 1.031) + 1) * 100)
    public static float FixedToFloatSpO2(float fixedValueRED, float fixedValueIR) {
//        return 1f / ((fixedValueRED / fixedValueIR * 2.578f - 1f) / (10f - fixedValueRED / fixedValueIR * 1.031f) + 1f) * 100f;
        return 110f - fixedValueRED / fixedValueIR * 25f; //SpO2 = 110 - R(RED/IR) * 25
    }

    static int windows_size = 150;
    static int data_size = 1024;
    static int sample_rate = 100;
    static double[][] Dif = new double[data_size - windows_size][data_size];
    static double Dif_v = 0;
    static double[] Dif_N = new double[data_size - windows_size];
    static double[] SD = new double[data_size - windows_size];
    static double SD_v = 0;
    static double[] Temp_1 = new double[data_size];
    static int time_value = 0;
    public static double FixedToFloatHeartRate(double[] fixedValueIR)
    {
        int i = 0, j = 0;
        for (time_value = 0; time_value < (data_size - windows_size); time_value++)
        {
            for (i = 0; i < windows_size; i++)
            {
                Dif[time_value][i] = fixedValueIR[i] - fixedValueIR[i + time_value];
                Dif_v = Dif_v + Dif[time_value][i];
            }
            Dif_N[time_value] = Dif_v / windows_size;
            Dif_v = 0;
            for (i = 0; i < windows_size; i++)
            {
                Temp_1[i] = Math.pow(Math.abs(Dif[time_value][i] - Dif_N[time_value]), 2);
                SD_v = SD_v + Temp_1[i];
            }
            SD[time_value] = Math.sqrt((SD_v / (windows_size - 1)));
            SD_v = 0;
        }

//        double[][] SDDif = new double[(data_size - windows_size) - windows_size][data_size - windows_size];
        double SDDif_v = 0;
        double[] SDDif_N = new double[(data_size - windows_size) - windows_size];
        double[] SSD = new double[(data_size - windows_size) - windows_size];
        double SSD_v = 0;
        double[] Temp_2 = new double[data_size - windows_size];


        for(i = 0; i<874;i++){
            for(j = 0; j<1024;j++) {
                Dif[i][j] = 0;
            }
        }

        for (time_value = 0; time_value < ((data_size - windows_size) - windows_size); time_value++)
        {
            for (i = 0; i < windows_size; i++)
            {
                Dif[time_value][i] = SD[i] - SD[i + time_value];
                SDDif_v = SDDif_v + Dif[time_value][i];
            }
            SDDif_N[time_value] = SDDif_v / windows_size;
            SDDif_v = 0;
            for (i = 0; i < windows_size; i++)
            {
                Temp_2[i] = Math.pow(Math.abs(Dif[time_value][i] - SDDif_N[time_value]), 2);
                SSD_v = SSD_v + Temp_2[i];
            }
            SSD[time_value] = Math.sqrt((SSD_v / (windows_size - 1)));
            SSD_v = 0;
        }

        double[] Norm = new double[(data_size - windows_size) - windows_size];
        double Norm_v = 0;
        Norm[0] = 1;
        for (i = 1; i < ((data_size - windows_size) - windows_size); i++)
        {
            for (j = 1; j <= i; j++)
            {
                Norm_v = Norm_v + SSD[j];
            }
            Norm[i] = SSD[i] / Math.abs(Norm_v / (i + 1));
            Norm_v = 0;
        }
        double[] Smooth = new double[(data_size - windows_size) - windows_size];
        double[] Diff1 = new double[(data_size - windows_size) - windows_size];
        double[] Sign = new double[(data_size - windows_size) - windows_size];
        double[] Diff2 = new double[(data_size - windows_size) - windows_size];
//        double[] Search = new double[data_size / sample_rate];
        double[] Search = new double[100];
//        double[] Diff3 = new double[data_size / sample_rate];
        double[] Diff3 = new double[100];
        double SUM = 0;
        Smooth[0] = Norm[0];
        Smooth[1] = (Norm[0] + Norm[1] + Norm[2]) / 3;
        for (i = 2; i < (((data_size - windows_size) - windows_size) - 2); i++)
        {
            Smooth[i] = (Norm[i - 2] + Norm[i - 1] + Norm[i] + Norm[i + 1] + Norm[i + 2]) / 5;
        }
        for (i = 0; i < (((data_size - windows_size) - windows_size) - 3); i++)
        {
            Diff1[i] = Smooth[i + 1] - Smooth[i];
        }
        for (i = 0; i < ((data_size - windows_size) - windows_size); i++)
        {
            if (Diff1[i] > 0)
                Sign[i] = 1;
            else if (Diff1[i] == 0)
                Sign[i] = 0;
            else
                Sign[i] = -1;
        }
        for (i = 0; i < (((data_size - windows_size) - windows_size) - 1); i++)
        {
            Diff2[i] = Sign[i + 1] - Sign[i];
        }
        j = 0;
        for (i = 0; i < ((data_size - windows_size) - windows_size); i++)
        {
            if (Diff2[i] == 2)
            {
                Search[j] = i;
                j++;
            }
        }
        int k = 0;
        for (i = 0; i < (j - 2); i++)
        {
            Diff3[i] = Search[i + 1] - Search[i];
//            SUM = SUM + Diff3[i];

            if (Diff3[i] > 60)
            {
                SUM = SUM + Diff3[i];
                k++;
            }
//            Log.e(TAG, "i:" + i + " j:" + j);
        }
        SUM = SUM / k;
//        SUM = SUM / (j - 2);
//        Log.e(TAG,"SUM:"+SUM+" k:"+k);
        return 1 / (SUM / sample_rate) * 60;
    }

}
