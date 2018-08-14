package com.example.dell.mytest.polling;

import com.example.dell.mytest.protocol.MqttProtocol;
import com.example.dell.mytest.service.MqttServiceAPI;

/**
 * Created by Dell on 2018/7/24.
 */

public abstract class AbsPolling implements ImOnPoll{
    public MqttProtocol mqttProtocol;
    public MqttServiceAPI mqttServiceAPI;

    public AbsPolling(){

    }

    @Override
    public void init(MqttServiceAPI mqttServiceAPI, MqttProtocol mqttProtocol) {
        this.mqttServiceAPI = mqttServiceAPI;
        this.mqttProtocol = mqttProtocol;
    }


}
