package org.gaby;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;

/**
 * * Created by Administrator on 2017/6/29 0029.
 */
public class JLog {

    private static JLog instance;

    private JLog() {
    }

    public static JLog getInstance() {
        if (instance == null) {
            instance = new JLog();
        }
        return instance;
    }

    public void log(String str) {
        Log.i("JAVA", str);
        synchronized (logs) {
            logs.add(str + "\n");
        }
    }

    private String pop() {
        String r = null;
        synchronized (logs) {
            if (logs.size() > 0) {
                r = logs.pop();
            }
        }
        return r;
    }

    public void stop() {
        thread = null;
    }

    public void start(final String path) {
        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {

                    try {
                        log("thread start with path = " + path);
                        File file = new File(path);
                        if (!file.exists()) {
                            file.mkdirs();
                            file.delete();
                            file.createNewFile();
                        }
                        FileOutputStream out = new FileOutputStream(file, true); //追加

                        while (thread != null) {
                            String r = pop();
                            if (r != null) {
                                out.write(r.getBytes("utf-8"));//注意需要转换对应的字符集
                                out.flush();
                            } else {
                                sleep(3);
                            }
                        }
                    } catch (Exception ex) {
                        Log.e("AAA", "thread write log error", ex);
                    }
                }
            };
            thread.start();
        }
    }


    private Thread thread = null;
    private final LinkedList<String> logs = new LinkedList<String>();
}
