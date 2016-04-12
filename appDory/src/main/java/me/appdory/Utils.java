package me.appdory;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

import java.io.InputStream;
import java.util.Scanner;

public class Utils {

    public static String inputStreamToString(InputStream inputStream) {
        Scanner s = new Scanner(inputStream);
        s.useDelimiter("\\A");
        String string = s.hasNext() ? s.next() : "";
        s.close();
        return string;
    }

    static Bitmap getCircular(Bitmap bitmap) {
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);

        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);

        return circleBitmap;
    }
}
