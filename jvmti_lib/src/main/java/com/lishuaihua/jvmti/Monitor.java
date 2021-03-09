package com.lishuaihua.jvmti;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Debug;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Monitor {

    private static final String LIB_NAME = "monitor_agent";

    public static void init(Application application) {
        // 最低支持 Android 8.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        String agentPath = createAgentLib(application);
        //加载指定位置的so
        System.load(agentPath);

        //加载jvmti agent
        attachAgent(agentPath, application.getClassLoader());

        //创建日志存放目录：/sdcard/Android/data/packagename/files/monitor
        File file = application.getExternalFilesDir("");
        File root = new File(file, "Monitor");
        root.mkdirs();
        native_init(root.getAbsolutePath());
    }


    private static void attachAgent(String agentPath, ClassLoader classLoader) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Debug.attachJvmtiAgent(agentPath, null, classLoader);
            } else {
                Class vmDebugClazz = Class.forName("dalvik.system.VMDebug");
                Method attachAgentMethod = vmDebugClazz.getMethod("attachAgent", String.class);
                attachAgentMethod.setAccessible(true);
                attachAgentMethod.invoke(null, agentPath);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String createAgentLib(Context context) {
        try {
            //1、获得so的地址
            ClassLoader classLoader = context.getClassLoader();
            Method findLibrary = ClassLoader.class.getDeclaredMethod("findLibrary", String.class);
            String jvmtiAgentLibPath = (String) findLibrary.invoke(classLoader, LIB_NAME);

            //2、创建目录：/data/data/packagename/files/monitor
            File filesDir = context.getFilesDir();
            File jvmtiLibDir = new File(filesDir, "monitor");
            if (!jvmtiLibDir.exists()) {
                jvmtiLibDir.mkdirs();
            }
            //3、将so拷贝到上面的目录中
            File agentLibSo = new File(jvmtiLibDir, "agent.so");
            if (agentLibSo.exists()) {
                agentLibSo.delete();
            }
            Files.copy(Paths.get(new File(jvmtiAgentLibPath).getAbsolutePath()),
                    Paths.get((agentLibSo).getAbsolutePath()));
            return agentLibSo.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void release() {
        native_release();
    }

    private native static void native_init(String path);

    private native static void native_release();
}
