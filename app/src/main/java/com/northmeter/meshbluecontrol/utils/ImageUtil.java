package com.northmeter.meshbluecontrol.utils;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Base64;


import com.northmeter.meshbluecontrol.base.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lht on 2016/5/10.
 */
public class ImageUtil {
    private static final String TAG = ImageUtil.class.getSimpleName();
    public static String mCurrentPhotoPath;
    private static boolean compress;

    public static String createImageName(String customerId) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        mCurrentPhotoPath = customerId + "W" + timeStamp + ".jpg";
        return mCurrentPhotoPath;
    }

    public static Bitmap getSmallBitmap(String path, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }


    /**
     * 计算图片的缩放率
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;//必须是2的次方
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;

    }

    /**
     * 图像上画日期
     *
     * @param bitmap
     * @return
     */
    public static Bitmap setMbitmap(Bitmap bitmap) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint p1 = new Paint();
        p1.setAntiAlias(true);
        p1.setColor(Color.GREEN);
        p1.setTextSize(40);
        p1.setStrokeWidth(5);
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        int height = bitmap.getHeight();
        canvas.drawText(timeStamp, 5, (int) (height * 0.1), p1);
        return mutableBitmap;
    }


    public static Bitmap zoomImage(Bitmap bgimage, int newHeight) {
        // 获取这个图片的宽和高
        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) newHeight) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height,
                matrix, true);
        return bitmap;
    }

    /**Base64加密图片*/
    public static String picStrToBase64(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] byteArray = bos.toByteArray();
        byte[] encode = Base64.encode(byteArray,Base64.DEFAULT);
        String encodeString = new String(encode);
        return encodeString;
    }

    public static String picPathToStr(String imagPath) {
        byte[] byteArray = picPathToByte(imagPath);
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static byte[] picPathToByte(String imagPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagPath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix mtx = new Matrix();
        mtx.setRotate(degree);
        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
        return result;
    }


    public static Bitmap setMbitmap(Bitmap bitmap, String pos) {
        Paint p = new Paint();
        p.setAntiAlias(true); //去锯齿
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1);
        //set ARGB_4444,cause many android client cannot set this bitmap to imageview
        //change to ARGB_8888
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        p.setColor(Color.RED);
        String recIn[] = pos.split(",");
        if (recIn.length < 8)
            return null;
        //x1
        float x1 = Float.parseFloat(recIn[0]);
        //y1
        float y1 = Float.parseFloat(recIn[1]);
        //x2
        float x2 = Float.parseFloat(recIn[2]);
        float y2 = Float.parseFloat(recIn[3]);
        float x3 = Float.parseFloat(recIn[4]);
        float y3 = Float.parseFloat(recIn[5]);
        float x4 = Float.parseFloat(recIn[6]);
        float y4 = Float.parseFloat(recIn[7]);

        float[] pts = {
                x1, y1, x2, y2,
                x2, y2, x3, y3,
                x3, y3, x4, y4,
                x4, y4, x1, y1};
        canvas.drawLines(pts, p);

       /* Paint p1 = new Paint();
        p1.setColor(Color.YELLOW);
        p1.setTextSize(30);
        canvas.drawText("传输时间:" + transTime + "ms", 0, 100, p1);
        canvas.drawText("计算时间:" + processTime + "ms", 0, 150, p1);*/
        return mutableBitmap;
    }

    /**
     *  字节数组，转换为bitmap(oom)
     * @param imgByte
     * @return
     */
    public static Bitmap byteToBitmap(byte[] imgByte) {
        InputStream input = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        input = new ByteArrayInputStream(imgByte);
        SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(
                input, null, options));
        bitmap = (Bitmap) softRef.get();
        if (imgByte != null) {
            imgByte = null;
        }

        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * 保存 图片
     *
     * @param bmp
     * @param dir
     * @param fileName
     * @return
     */
    public static boolean saveImage(Bitmap bmp, String dir, String fileName) {
        File file = new File(dir.trim());
        if (!file.exists()) {
            file.mkdirs();
        }
        File endFile = new File(dir, fileName);
        if (!endFile.exists()) {
            try {
                endFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(endFile);
            compress = bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
            MyApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(endFile)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (compress) {
                return true;
            }
        }
        return false;
    }

}
