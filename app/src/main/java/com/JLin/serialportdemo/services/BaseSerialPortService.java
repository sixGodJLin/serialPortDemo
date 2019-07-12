package com.JLin.serialportdemo.services;

import android.app.Service;
import android.widget.Toast;

import com.JLin.serialportdemo.App;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * @author JLin
 * @date 2019/7/12
 * @describe 串口service
 */
public abstract class BaseSerialPortService extends Service {
    protected App mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;


    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = (App) getApplication();

        try {
            mSerialPort = mApplication.getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            //create a receiving thread
            ReadThread mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            showMessage("没有对串口的读写权限");
        } catch (IOException e) {
            showMessage("串口打开失败");
        } catch (InvalidParameterException e) {
            showMessage("请首先配置一下串口");
        }

    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mApplication.closeSerialPort();
        mSerialPort = null;
    }

    /**
     * 数据接收
     *
     * @param buffer buffer
     * @param size   size
     */
    protected abstract void onDataReceived(final byte[] buffer, final int size);

    /**
     * 消息显示
     *
     * @param msg msg
     */
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
