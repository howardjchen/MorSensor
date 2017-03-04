package tw.org.cic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import tw.org.cic.morsensor_example_3.IRIViewActivity_2_1;

/**
 * Created by wllai on 2016/3/9.
 */
public class ThermalView extends View {
    private static final String TAG = "ThermalView";
    public ThermalView(Context context) {
        super(context);
    }

    static final int pixel = 10;
    static int left = 0, top = 0, right = pixel, bottom = pixel;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);//設置填滿
        left = 0;
        top = 0;
        right = pixel;
        bottom = pixel;

        for (int i = 0; i < 4800; i++) { //4800 = 80column * 60row
            Log.i("onDraw", "" + IRIViewActivity_2_1.pixels[i]);
            if (IRIViewActivity_2_1.pixels[i] <= 0) {
                p.setColor(Color.BLACK);
            } else if (IRIViewActivity_2_1.pixels[i] <= 20 / 2) {
                p.setColor(Hue(0));
            } else if (IRIViewActivity_2_1.pixels[i] <= 40 / 2) {
                p.setColor(Hue(1));
            } else if (IRIViewActivity_2_1.pixels[i] <= 60 / 2) {
                p.setColor(Hue(2));
            } else if (IRIViewActivity_2_1.pixels[i] <= 80 / 2) {
                p.setColor(Hue(3));
            } else if (IRIViewActivity_2_1.pixels[i] <= 100 / 2) {
                p.setColor(Hue(4));
            } else if (IRIViewActivity_2_1.pixels[i] <= 120 / 2) {
                p.setColor(Hue(5));
            } else if (IRIViewActivity_2_1.pixels[i] <= 140 / 2) {
                p.setColor(Hue(6));
            } else if (IRIViewActivity_2_1.pixels[i] <= 160 / 2) {
                p.setColor(Hue(7));
            } else if (IRIViewActivity_2_1.pixels[i] <= 180 / 2) {
                p.setColor(Hue(8));
            } else if (IRIViewActivity_2_1.pixels[i] <= 200 / 2) {
                p.setColor(Hue(9));
            } else if (IRIViewActivity_2_1.pixels[i] <= 220 / 2) {
                p.setColor(Hue(10));
            } else if (IRIViewActivity_2_1.pixels[i] <= 240 / 2) {
                p.setColor(Hue(11));
            } else if (IRIViewActivity_2_1.pixels[i] <= 260 / 2) {
                p.setColor(Hue(12));
            } else if (IRIViewActivity_2_1.pixels[i] <= 280 / 2) {
                p.setColor(Hue(13));
            } else if (IRIViewActivity_2_1.pixels[i] <= 300 / 2) {
                p.setColor(Hue(14));
            } else if (IRIViewActivity_2_1.pixels[i] <= 320 / 2) {
                p.setColor(Hue(15));
            } else if (IRIViewActivity_2_1.pixels[i] <= 340 / 2) {
                p.setColor(Hue(16));
            } else if (IRIViewActivity_2_1.pixels[i] <= 360 / 2) {
                p.setColor(Hue(17));
            } else if (IRIViewActivity_2_1.pixels[i] <= 380 / 2) {
                p.setColor(Hue(18));
            } else if (IRIViewActivity_2_1.pixels[i] <= 400 / 2) {
                p.setColor(Hue(19));
            } else if (IRIViewActivity_2_1.pixels[i] <= 420 / 2) {
                p.setColor(Hue(20));
            } else if (IRIViewActivity_2_1.pixels[i] <= 440 / 2) {
                p.setColor(Hue(21));
            } else if (IRIViewActivity_2_1.pixels[i] <= 460 / 2) {
                p.setColor(Hue(22));
            } else if (IRIViewActivity_2_1.pixels[i] <= 480 / 2) {
                p.setColor(Hue(23));
            } else if (IRIViewActivity_2_1.pixels[i] <= 500 / 2) {
                p.setColor(Hue(24));
            } else if (IRIViewActivity_2_1.pixels[i] <= 520 / 2) {
                p.setColor(Hue(25));
            } else if (IRIViewActivity_2_1.pixels[i] <= 540 / 2) {
                p.setColor(Hue(26));
            } else if (IRIViewActivity_2_1.pixels[i] > 540 / 2) {
                p.setColor(Hue(27));
            } else {
                p.setColor(Color.WHITE);
            }

            if (i != 0 && i % 80 == 0) { //80*4dp = 1Row (1pixel = 4dp)
                left = 0;
                top += pixel;
                right = pixel;
                bottom += pixel;
            }
            canvas.drawRect(left, top, right, bottom, p);// 正方形

            left += pixel;
            right += pixel;
        }

        /** 0x000-0x1FF
         ___80*4_=_320__
         |　　　　　　　　 |
         |　　　　　　　　 |
         |　　　　　　　　 |60*4=240
         |　　　　　　　　 |
         |　　　　　　　　 |
         |_______________|

         */
    }

    int color;
    float mhue = 0f;
    static float[] hsv = new float[]{0f, 1f, 1f}; //Hue[0...360] Saturation[0...1] Value[0...1]
    private int Hue(int progress) { //色相  Red-0 Green-120 Blue-240
        if(240-progress*12 < 0) //Blue to Red (Hue Circle)
            mhue = 360+(240-progress*12);
        else
            mhue = 240-progress*12;

        hsv[0]=mhue;
        color = Color.HSVToColor(hsv);
//        Log.d(TAG, "Color:"+color+" mhue:"+mhue);
        return color;
    }
}