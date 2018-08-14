package com.example.dell.mytest.polling;

import com.example.dell.mytest.protocol.MqttProtocol;
import com.example.dell.mytest.service.MqttServiceAPI;

/**
 * Created by Dell on 2018/7/24.
 */

public interface ImOnPoll {

    void init(MqttServiceAPI mqttServiceAPI,MqttProtocol mqttProtocol);

    void onPolling();

}
