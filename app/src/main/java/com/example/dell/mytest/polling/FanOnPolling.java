package com.example.dell.mytest.polling;

import com.example.dell.mytest.msg.Msg;
import com.example.dell.mytest.protocol.MsgKeys;

/**
 * Created by Dell on 2018/7/24.
 */

public class FanOnPolling extends AbsPolling implements ImOnPoll {

    @Override
    public void onPolling() {
        try {
            byte[] data = null;
            Msg reqMsg = Msg.newRequestMsg(MsgKeys.GetFanStatus_Req, "R8229bae45a0a6b00");
            data = mqttProtocol.encode(reqMsg);
            String topic = reqMsg.getTag();
            mqttServiceAPI.publishCommand(topic,data,0,false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
