package com.JLin.serialportdemo.services;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.JLin.serialportdemo.MessageEvent;

import org.greenrobot.eventbus.EventBus;


/**
 * @author JLin
 * @date 2019/7/12
 * @describe 串口服务
 */
public class SerialPortService extends BaseSerialPortService {
    StringBuffer stringBuffer = new StringBuffer();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDataReceived(byte[] buffer, int size) {
        if (size > 0) {
            stringBuffer.append(new String(buffer, 0, size));
            String data = bytesToHexString(buffer).trim().substring(0, size * 2);
            System.out.println("WeighService：" + "onDataReceived" + "++++ " + data);
            if (size == 8) {
                data = data.substring(4, 10);
            }
            if (size == 9) {
                data = data.substring(6, 12);
            }
            int x = Integer.parseInt(data, 16);
            EventBus.getDefault().post(new MessageEvent("COM_WEIGHT", x + ""));
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
