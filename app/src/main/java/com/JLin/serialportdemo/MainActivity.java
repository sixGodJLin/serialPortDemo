package com.JLin.serialportdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.JLin.serialportdemo.services.SerialPortService;

import java.io.IOException;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * @author JLin
 * @date 2019/6/25
 * @describe 窜口通信 demo
 */
public class MainActivity extends AppCompatActivity {

    private OutputStream mOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openCOM();
        initCOMConfig();

        TextView textView = findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send("FF FF FF FF 5A 5C 0C 03 00 06 00 A5");
            }
        });
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
    }


    /**
     * 发送串口指令
     *
     * @param s 串口指令数据
     */
    private void send(String s) {
        System.out.println("MainActivity 窜口发送的数据 ====:" + s);
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
}
