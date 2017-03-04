package tw.org.cic.mic;

import android.os.Environment;

import java.io.File;

/**
 * Created by willywen on 2015/4/10.
 */

public class audiofilefunc {
    private final static String AUDIO_RAW_FILENAME = "RawAudio.raw";
    private final static String AUDIO_WAV_FILENAME = "FinalAudio.wav";
    public final static String AUDIO_AMR_FILENAME = "FinalAudio.amr";

    /**
     * 判斷是否有外部存儲設備sdcard
     * @return true | false
     */
    public static boolean isSdcardExit(){
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 獲取麥克風輸入的原始音頻流文件路徑
     * @return
     */
    public static String getRawFilePath(){
        String mAudioRawPath = "";
        if(isSdcardExit()){
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mAudioRawPath = fileBasePath+"/"+AUDIO_RAW_FILENAME;
        }

        return mAudioRawPath;
    }

    /**
     * 獲取編碼後的WAV格式音頻文件路徑
     * @return
     */
    public static String getWavFilePath(){
        String mAudioWavPath = "";
        if(isSdcardExit()){
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mAudioWavPath = fileBasePath+"/"+AUDIO_WAV_FILENAME;
        }
        return mAudioWavPath;
    }

    /**
     * 獲取編碼後的AMR格式音頻文件路徑
     * @return
     */
    public static String getAMRFilePath(){
        String mAudioAMRPath = "";
        if(isSdcardExit()){
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mAudioAMRPath = fileBasePath+"/"+AUDIO_AMR_FILENAME;
        }
        return mAudioAMRPath;
    }

    /**
     * 獲取文件大小
     * @param path,文件的絕對路徑
     * @return
     */
    public static long getFileSize(String path){
        File mFile = new File(path);
        if(!mFile.exists())
            return -1;
        return mFile.length();
    }

}
