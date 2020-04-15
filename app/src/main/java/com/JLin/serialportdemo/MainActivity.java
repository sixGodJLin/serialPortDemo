package com.JLin.serialportdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private TextView tvSend;
    private OutputStream mOutputStream;

    /**
     * 检查当前称数据的线程
     */
    private ScheduledExecutorService checkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        tvSend = findViewById(R.id.tv_send);

        checkService = new ScheduledThreadPoolExecutor(1);
        openCOM();
        initCOMConfig();

        tvSend.setOnClickListener(v -> getOneWeight());
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
        send("01 03 00 01 00 02 95 cb");

        if (checkService == null) {
            checkService = new ScheduledThreadPoolExecutor(1);
        }
        checkService.schedule(() -> {
//            System.out.println("MainActivity：" + "getOneWeight" + "==== 1号称超时");
            getTwoWeight();
        }, 1000, TimeUnit.MILLISECONDS);
    }

    private void getTwoWeight() {
        send("02 03 00 01 00 02 95 f8");

        if (checkService == null) {
            checkService = new ScheduledThreadPoolExecutor(1);
        }
        checkService.schedule(() -> {
//            System.out.println("MainActivity：" + "getTwoWeight" + "==== 2号称超时");
            getThreeWeight();
        }, 1000, TimeUnit.MILLISECONDS);
    }

    private void getThreeWeight() {
        send("03 03 00 01 00 02 94 29");

        if (checkService == null) {
            checkService = new ScheduledThreadPoolExecutor(1);
        }
        checkService.schedule(() -> {
//            System.out.println("MainActivity：" + "getThreeWeight" + "==== 3号称超时");
            getFourWeight();
        }, 1000, TimeUnit.MILLISECONDS);
    }

    private void getFourWeight() {
        send("04 03 00 01 00 02 95 9e");

        if (checkService == null) {
            checkService = new ScheduledThreadPoolExecutor(1);
        }
        checkService.schedule(() -> {
//            System.out.println("MainActivity：" + "getFourWeight" + "==== 4号称超时");
            getFiveWeight();
        }, 1000, TimeUnit.MILLISECONDS);
    }

    private void getFiveWeight() {
        send("05 03 00 01 00 02 94 4f");

        if (checkService == null) {
            checkService = new ScheduledThreadPoolExecutor(1);
        }
        checkService.schedule(() -> {
//            System.out.println("MainActivity：" + "getFourWeight" + "==== 5号称超时");
            getOneWeight();
        }, 1000, TimeUnit.MILLISECONDS);
    }

//    private void getSixWeight() {
//        send("FF FF FF FF 5A 5C 0C 06 00 06 00 A5");
//
//        if (checkService == null) {
//            checkService = new ScheduledThreadPoolExecutor(1);
//        }
//        checkService.schedule(() -> {
//            System.out.println("MainActivity：" + "getFourWeight" + "==== 6号称超时");
//            getSevenWeight();
//        }, 1000, TimeUnit.MILLISECONDS);
//    }
//
//    private void getSevenWeight() {
//        send("FF FF FF FF 5A 5C 0C 07 00 06 00 A5");
//
//        if (checkService == null) {
//            checkService = new ScheduledThreadPoolExecutor(1);
//        }
//        checkService.schedule(() -> {
//            System.out.println("MainActivity：" + "getFourWeight" + "==== 7号称超时");
//            getEightWeight();
//        }, 1000, TimeUnit.MILLISECONDS);
//    }
//
//    private void getEightWeight() {
//        send("FF FF FF FF 5A 5C 0C 08 00 06 00 A5");
//
//        if (checkService == null) {
//            checkService = new ScheduledThreadPoolExecutor(1);
//        }
//        checkService.schedule(() -> {
//            System.out.println("MainActivity：" + "getFourWeight" + "==== 8号称超时");
//            getOneWeight();
//        }, 1000, TimeUnit.MILLISECONDS);
//    }


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
            mOutputStream.flush();
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
            case "COM_RESPONSE":
                /*查询设备状态*/
                String data = event.getMsg();

//                if (checkService != null && !checkService.isShutdown()) {
//                    checkService.shutdownNow();
//                    checkService = null;
//                }
//                try {
//                    Thread.sleep(10);
//                    switch (data.substring(0, 2)) {
//                        case "01":
//                            getTwoWeight();
//                            break;
//                        case "02":
//                            getThreeWeight();
//                            break;
//                        case "03":
//                            getFourWeight();
//                            break;
//                        case "04":
//                            getFiveWeight();
//                            break;
//                        case "05":
//                            getOneWeight();
//                            break;
//                        case "06":
//                            getSevenWeight();
//                            break;
//                        case "07":
//                            getEightWeight();
//                            break;
//                        case "08":
//                            getOneWeight();
//                            break;
                        default:
                            break;
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

//                break;
//            default:
//                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
