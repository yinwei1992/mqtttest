package com.example.dell.mytest.polling;

import com.example.dell.mytest.protocol.MqttProtocol;
import com.example.dell.mytest.service.MqttServiceAPI;

/**
 * Created by Dell on 2018/7/24.
 */

public class AbsOnPolling{


    ImOnPoll imOnPoll;
    MqttServiceAPI mqttServiceAPI;
    MqttProtocol mqttProtocol;

    public  AbsOnPolling(ImOnPoll imOnPoll, MqttServiceAPI mqttServiceAPI, MqttProtocol mqttProtocol){
        this.imOnPoll = imOnPoll;
        this.mqttProtocol = mqttProtocol;
        this.mqttServiceAPI = mqttServiceAPI;
    }

    public void onPolling(){
        imOnPoll.init(mqttServiceAPI,mqttProtocol);
        imOnPoll.onPolling();
    }


}
