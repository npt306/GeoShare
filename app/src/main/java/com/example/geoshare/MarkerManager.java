package com.example.geoshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import androidx.annotation.DrawableRes;

public class MarkerManager {
    MainActivity callerContext;
    public MarkerManager(Context callerContext){
        this.callerContext = (MainActivity) callerContext;
    }

    // crop middle circle from a bitmap
    private Bitmap cropCircleFromBitmap(Bitmap bitmap){

        final int r = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(outputBitmap);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return outputBitmap;
    }


    // support converting image to bitmap, which will be used as marker icon
    private Bitmap getBitmapFromImage(@DrawableRes int resId){

        BitmapDrawable bitmapdraw=(BitmapDrawable)callerContext.getResources().getDrawable(resId);
        Bitmap bitmap = bitmapdraw.getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, false);

        return cropCircleFromBitmap(bitmap);
    }
}
