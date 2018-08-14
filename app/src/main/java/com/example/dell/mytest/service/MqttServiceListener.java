package com.example.dell.mytest.service;

/**
 * Created by Dell on 2018/7/23.
 */

public interface MqttServiceListener {
    public void onMqttReceiver(int code, String messages);
}
