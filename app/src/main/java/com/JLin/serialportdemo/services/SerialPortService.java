package com.JLin.serialportdemo.services;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.JLin.serialportdemo.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author JLin
 * @date 2019/7/12
 * @describe 串口服务
 */
public class SerialPortService extends BaseSerialPortService {
    private static final String TAG = "SerialPortService";
    private StringBuffer stringBuffer = new StringBuffer();
    private ScheduledExecutorService service;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDataReceived(byte[] buffer, int size) {
        if (service != null && !service.isShutdown()) {
            service.shutdownNow();
            service = null;
        }
        if (size > 0) {
            stringBuffer.append(bytesToHexString(buffer).substring(0, size * 2));
            Log.e(TAG, "onDataReceived: " + stringBuffer.toString());

            service = new ScheduledThreadPoolExecutor(1);
            service.schedule(() -> {
                String data = stringBuffer.toString();
//                if (data.contains("5a5c")) {
//                    Log.d(TAG, "onDataReceived: " + data);
//                    String regex = "f.*f5a5c.*a5";
//                    Pattern pattern = Pattern.compile(regex);
//                    Matcher matcher = pattern.matcher(data);
//                    if (matcher.find()) {
//                        data = matcher.group();
//                        //原本此处发送广播
//
//                        while (data.startsWith("ff")) {
//                            data = data.substring(2);
//                        }
                EventBus.getDefault().post(new MessageEvent("COM_RESPONSE", data));
//                    }
                stringBuffer.setLength(0);
//                } else {
//                    stringBuffer.setLength(0);
//                }
            }, 200, TimeUnit.MILLISECONDS);
        }
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
