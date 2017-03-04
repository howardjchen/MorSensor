package tw.org.cic.dataManage;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

public class ControlSDCard {
    private static final String TAG = "ControlSDCard";

    String tempmDeviceData="";
    static int counter = 0;

    public static String SDCardStorageDirectory(){
        String RealElement = "null";
        try{
            //取得真正SD卡儲存路徑
            File mountFile = new File("/proc/mounts");
            if(mountFile.exists()){
                Scanner scanner = new Scanner(mountFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("/dev/block/vold/")) {
                        String[] lineElements = line.split(" ");
                        String element = lineElements[1];
                        String[] element2 = element.split("/");
                        RealElement = "/storage/" + element2[element2.length-1];
                        Log.e(TAG, "element:" + element2[element2.length - 1] + "  ");
                    }
                }
            }
            //取得內部儲存空間路徑
            if(RealElement.equals("null")){ RealElement = Environment.getExternalStorageDirectory() + ""; }
            return RealElement;
        }catch (Exception e){
            Log.e(TAG,"SDCardStorageDirectory:" + e);
            //取得內部儲存空間路徑
            RealElement = Environment.getExternalStorageDirectory() + "";
            return RealElement;
        }
    }



    public static void WriteSDCard(String tempmDeviceData){
        try
        {
            //檢查有沒有SD卡裝置
            if(Environment.getExternalStorageState().equals( Environment.MEDIA_REMOVED)){
//                Toast.makeText(PlotterViewActivity.mPlotterViewActivity, "沒有SD卡!!!", Toast.LENGTH_SHORT).show();
                Log.e(TAG,"SDCard Status: No SD Card.");
                return ;
            } else {
                //建立文件檔儲存路徑
                File mFile = new File( SDCardStorageDirectory()+"/Android/data/tw.org.cic.morsensor/files" );

                //若沒有檔案儲存路徑時則建立此檔案路徑
                if(!mFile.exists()){
                    mFile.mkdirs();
                }

                //將RawData儲存寫入至SD卡文件裡
                FileWriter mFileWriter = new FileWriter( SDCardStorageDirectory()+"/Android/data/tw.org.cic.morsensor/files/RawData.txt" ,true);
//                mFileWriter.write("\n----------------**** 500 ****----------------\n\n" + tempmDeviceData);
                mFileWriter.write(tempmDeviceData);
                mFileWriter.close();
//                Toast.makeText(PlotterViewActivity.mPlotterViewActivity, "已儲存文字", Toast.LENGTH_SHORT).show();
                Log.e(TAG,"SDCard Status: The data saved to SD card.");
            }
        }
        catch (Exception e){
            Log.e(TAG,"WriteSDCardError:" + e);
        }
    }

    public static String ReadSDCard(){
        try
        {
            //讀取文件檔路徑
            FileReader mFileReader = new FileReader( SDCardStorageDirectory()+"/Android/data/tw.org.cic.axis/9axis.txt" );

            BufferedReader mBufferedReader = new BufferedReader(mFileReader);
            String mReadText = "";
            String mTextLine = mBufferedReader.readLine();

            //一行一行取出文字字串裝入String裡，直到沒有下一行文字停止跳出
            while (mTextLine!=null)
            {
                mReadText += mTextLine+"\n";
                mTextLine = mBufferedReader.readLine();
//                readCharacter(hexToBytes(mBufferedReader.readLine()));
                return mBufferedReader.readLine();
            }
//            Toast.makeText(PlotterViewActivity.mPlotterViewActivity, "已讀取文字", Toast.LENGTH_SHORT).show();
            return "";
        }
        catch(Exception e){
            Log.e(TAG,"ReadSDCardError:" + e);
            return "";
        }
    }

}
