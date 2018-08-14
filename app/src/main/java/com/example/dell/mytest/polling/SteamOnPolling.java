package com.example.dell.mytest.polling;

import com.example.dell.mytest.msg.Msg;
import com.example.dell.mytest.protocol.MsgKeys;
import com.example.dell.mytest.service.MqttServiceAPI;

/**
 * Created by Dell on 2018/7/24.
 */

public class SteamOnPolling extends AbsPolling implements ImOnPoll{

    @Override
    public void onPolling() {
        /*try {
            byte[] data = null;
            Msg reqMsg = Msg.newRequestMsg(MsgKeys.GetSteamOvenStatus_Req, "RR026C71bWajnvu00");
            data = mqttProtocol.encode(reqMsg);
            String topic = reqMsg.getTag();
            mqttServiceAPI.publishCommand(topic,data,0,false);
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }
}
