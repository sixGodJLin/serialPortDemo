package com.JLin.serialportdemo;

import android.app.Application;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * @author JLin
 * @date 2019/6/25
 * @describe application
 */
public class App extends Application {
    private SerialPort serialPort;

    /**
     * 串口
     */
    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (serialPort == null) {
            /* Read serial port parameters */
            String path = "/dev/ttyUSB11";
            int baudRate = 9600;
            /* Open the serial port */
            serialPort = new SerialPort(new File(path), baudRate, 0);
        }
        return serialPort;
    }

    public void closeSerialPort() {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
    }
}
