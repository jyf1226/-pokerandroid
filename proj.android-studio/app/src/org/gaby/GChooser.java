package org.gaby;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.lang.ref.WeakReference;

public abstract class GChooser {

    private int width;
    private int height;
    private int chooseRequestCode = 0;
    private int cropRequestCode = 0;
    private String fileFrom;
    private String fileTo;
    private WeakReference<Activity> activity;

    public GChooser(Activity activity, int chooseRequestCode, int cropRequestCode) {
        this.activity = new WeakReference<Activity>(activity);
        this.chooseRequestCode = chooseRequestCode;
        this.cropRequestCode = cropRequestCode;
    }

    public void start(String fileFrom, String fileTo, int width, int height) {
        this.fileFrom = fileFrom;
        this.fileTo = fileTo;
        this.width = width;
        this.height = height;

        // 源
        Uri source = Uri.parse("file://" + fileFrom);

        // 目标
        Uri destination = Uri.parse("file://" + fileTo);
        //        String dir = JsBridge.getWorkingDir() + "/shot";
        //        GFiles.newFolder(dir);
        //        String file = Ut.getAvailableFile(dir, ".jpg");
        //        Uri destination = Uri.fromFile(new File(file));

        Activity act = activity.get();
        if (act != null) {
            Crop.of(source, destination).asSquare().start(act, cropRequestCode);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == this.cropRequestCode) {
            chose(fileTo, width, height);
        }
    }

    public abstract void chose(String file, int width, int height);
}
