package com.JLin.serialportdemo.services;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.JLin.serialportdemo.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
            if (bytesToHexString(buffer).contains("5a5c")) {
                String data = bytesToHexString(buffer).trim();
                System.out.println("SerialPortService onDataReceived ====:" + data);
                String regex = "f.*f5a5c.*a5";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(data);
                if (matcher.find()) {
                    data = matcher.group();
                    //原本此处发送广播

                    while (data.startsWith("ff")) {
                        data = data.substring(2);
                    }
                    EventBus.getDefault().post(new MessageEvent("COM_RESPONSE", data));
                }
                stringBuffer.setLength(0);
            }
            if (stringBuffer.length() > 45) {
                stringBuffer.setLength(0);
            }
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
