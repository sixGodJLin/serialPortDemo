package com.JLin.serialportdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.JLin.serialportdemo.services.SerialPortService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android_serialport_api.SerialPort;

/**
 * @author JLin
 * @date 2019/6/25
 * @describe 窜口通信 demo
 */
public class MainActivity extends AppCompatActivity {

    private OutputStream mOutputStream;

    /**
     * 当前指令传输FLAG
     */
    private int statusFlag = 0;

    private LinkedList<Integer> oneWeightList;
    private int oneSum = 0;
    private int oneAvg;
    private int oneCount = 0;

    /**
     * 检查当前称数据的线程
     */
    private ScheduledExecutorService checkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        checkService = new ScheduledThreadPoolExecutor(1);

        oneWeightList = new LinkedList<>();

        openCOM();
        initCOMConfig();
    }

    /**
     * 开启串口服务
     */
    private void openCOM() {
        Intent intent = new Intent(this, SerialPortService.class);
        startService(intent);
    }

    /**
     * 串口配置
     */
    private void initCOMConfig() {
        App mApplication = (App) getApplication();
        try {
            SerialPort mSerialPort = mApplication.getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getOneWeight();
    }

    private void getOneWeight() {
        statusFlag = 11;
        send("01 03 40 03 00 02 21 cb");
    }

    private void getThreeWeight() {
        statusFlag = 31;
        send("03 03 40 03 00 02 20 29");

        if (checkService == null) {
            checkService = new ScheduledThreadPoolExecutor(1);
        }
        checkService.schedule(() -> {
            getOneWeight();
            System.out.println("MainActivity：" + "getThreeWeight" + "==== 3号称超时");
        }, 300, TimeUnit.MILLISECONDS);
    }

    /**
     * 发送串口指令
     *
     * @param s 串口指令数据
     */
    private void send(String s) {
        System.out.println("MainActivity 窜口发送的数据 ++++:" + s);
        byte[] mRestart;
        try {
            mRestart = hexCommandToByte(s.getBytes());
            mOutputStream.write(mRestart);
            mOutputStream.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] hexCommandToByte(byte[] data) {
        if (data == null) {
            return null;
        }
        int nLength = data.length;

        String strTemString = new String(data, 0, nLength);
        String[] strings = strTemString.split(" ");
        nLength = strings.length;
        data = new byte[nLength];
        for (int i = 0; i < nLength; i++) {
            if (strings[i].length() != 2) {
                data[i] = 0;
                continue;
            }
            try {
                data[i] = (byte) Integer.parseInt(strings[i], 16);
            } catch (Exception e) {
                data[i] = 0;
            }
        }

        return data;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final MessageEvent event) {
        switch (event.getEvent()) {
            case "COM_WEIGHT":
                int data = Integer.parseInt(event.getMsg());
                if (statusFlag == 11) {
                    System.out.println("1号桶获取重量: ++++++++++++++++++++++" + data);
                    if (checkService != null && !checkService.isShutdown()) {
                        checkService.shutdownNow();
                        checkService = null;
                    }

                    if (data < 999999) {
                        oneWeightList.addLast(data);
                    }

                    if (oneWeightList.size() >= 7) {
                        for (int i = 0; i < 7; i++) {
                            oneSum += oneWeightList.pollFirst();
                        }
                        oneAvg = oneSum / 7;
                        oneSum = 0;
                    }

                    if (data - oneAvg < 15 && data - oneAvg > -15) {
                        System.out.println("MainActivity：" + "11111111111111" + "---- ");
                        oneCount++;
                    } else {
                        System.out.println("MainActivity：" + "22222222222222" + "---- ");
                        oneCount = 0;
                    }

                    if (oneCount == 10) {
                        System.out.println("MainActivity：" + "oneWeight" + "==== " + data);
                        oneCount = 0;
                    }

                    send("01 03 40 03 00 02 21 cb");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
