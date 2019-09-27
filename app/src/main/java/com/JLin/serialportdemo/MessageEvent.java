package com.JLin.serialportdemo;

/**
 * Created by ChenPin on 2019/3/2/0002.
 * Phone: 13216117237
 * Function:
 */
public class MessageEvent {
    private String event;
    private String msg;

    public MessageEvent(String event, String msg) {
        this.event = event;
        this.msg = msg;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
