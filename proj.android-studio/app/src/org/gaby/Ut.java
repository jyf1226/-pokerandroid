package org.gaby;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Ut {
    public static byte[] bmpToByteArray(final Bitmap bmp) {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 70, localByteArrayOutputStream);
        byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
        try {
            localByteArrayOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayOfByte;
    }

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");

    public static String getAvailableFile(String dir, String fix) {
        String pre = dir + "/" + sdf.format(new Date());
        String file = null;
        int i = 0;
        do {
            i++;
            file = pre + "-" + i + fix;
        } while (GFiles.existsFile(file));
        return file;
    }

    public static void savePNG(Bitmap bmp, Bitmap.CompressFormat format, String file) {
        File f = new File(file);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(format, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 根据单边缩放
    public static Bitmap compressBitmapByShort(Context context, Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap afterBitmap = null;

        // 不需要压缩
        if (newWidth <= 0 && newHeight <= 0) {
            afterBitmap = bitmap.copy(bitmap.getConfig(), false);
        } else {
            // 图片原有的宽度和高度
            float beforeWidth = bitmap.getWidth();
            float beforeHeight = bitmap.getHeight();

            // 计算宽高缩放率
            float scaleWidth = 1;
            float scaleHeight = 1;

            // 如果需要的宽度更小
            if (newWidth > 0 && newWidth < beforeWidth) {
                scaleWidth = ((float) newWidth) / beforeWidth;
            }

            // 如果需要的高度更小
            if (newHeight > 0 && newHeight < beforeHeight) {
                scaleHeight = ((float) newHeight) / beforeHeight;
            }

            // 如果两边都需要缩放
            if (newWidth > 0 && newHeight > 0) {
                // 取最小比例
                if (scaleWidth < scaleHeight) {
                    scaleHeight = scaleWidth;
                } else {
                    scaleWidth = scaleHeight;
                }
            }
            // 如果只需要限制宽度
            else if (newWidth > 0) {
                scaleHeight = scaleWidth;
            }
            // 如果只需要限制高度
            else if (newHeight > 0) {
                scaleWidth = scaleHeight;
            }
            // 矩阵对象
            Matrix matrix = new Matrix();
            // 缩放图片动作 缩放比例
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建一个新的Bitmap 从原始图像剪切图像
            afterBitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) beforeWidth, (int) beforeHeight, matrix, true);
            int w = afterBitmap.getWidth();
            int h = afterBitmap.getWidth();
            JLog.getInstance().log("压缩图片: (" + beforeWidth + "," + beforeHeight + ")-->(" + w + "," + h + ")");
        }
        return afterBitmap;
    }

    // 等比缩放至最大化填充
    public static Bitmap compressBitmap(Context context, Bitmap bitmap, int newWidth, int newHeight) {
        // 图片原有的宽度和高度
        float beforeWidth = bitmap.getWidth();
        float beforeHeight = bitmap.getHeight();

        // 计算宽高缩放率
        float scaleWidth = 0;
        float scaleHeight = 0;

        // 等比缩放最长边
        scaleWidth = ((float) newWidth) / beforeWidth;
        scaleHeight = ((float) newHeight) / beforeHeight;
        if (scaleWidth < scaleHeight) {
            scaleHeight = scaleWidth;
        } else {
            scaleWidth = scaleHeight;
        }

        // 矩阵对象
        Matrix matrix = new Matrix();
        // 缩放图片动作 缩放比例
        matrix.postScale(scaleWidth, scaleHeight);
        // 创建一个新的Bitmap 从原始图像剪切图像
        Bitmap afterBitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) beforeWidth, (int) beforeHeight, matrix, true);
        int w = afterBitmap.getWidth();
        int h = afterBitmap.getWidth();
        JLog.getInstance().log("压缩图片: (" + beforeWidth + "," + beforeHeight + ")-->(" + w + "," + h + ")");

        return afterBitmap;

    }
}
