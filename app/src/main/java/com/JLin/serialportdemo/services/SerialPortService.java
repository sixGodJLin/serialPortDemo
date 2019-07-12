package com.JLin.serialportdemo.services;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * @author JLin
 * @date 2019/7/12
 * @describe 串口服务
 */
public class SerialPortService extends BaseSerialPortService {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDataReceived(byte[] buffer, int size) {
        if (size > 0) {
            System.out.println("收到数据 ====:" + bytesToHexString(buffer).trim());
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
