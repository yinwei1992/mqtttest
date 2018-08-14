package com.example.dell.mytest.msg;


import com.example.dell.mytest.bean.DeviceGuid;
import com.example.dell.mytest.protocol.Topic;
import com.example.dell.mytest.utils.LogUtils;

public class Msg extends AbsMsg implements IMqttMsg {

    protected DeviceGuid source, target;
    protected boolean incoming;
    //当前消息是否为烟机发出
    protected boolean isFan = false;

    public boolean getIsFan() {
        return isFan;
    }

    public void setIsFan(boolean isFan) {
        this.isFan = isFan;
    }


    private Msg(int msgKey) {
        this.msgKey = msgKey;
    }

    @Override
    public String getTopic() {
        return getTag();
    }

    /**
     * 获取消息的方向
     *
     * @return true-收到的消息 false-发出去的消息
     */
    public boolean isIncoming() {
        return incoming;
    }

    public DeviceGuid getSource() {
        return source;
    }

    public DeviceGuid getTarget() {
        return target;
    }

    public DeviceGuid getDeviceGuid() {
        return incoming ? source : target;
    }

    /**
     * 构造一个待发送的消息
     */
    static public Msg newRequestMsg(int msgKey, String targetGuid) {
        Msg msg = new Msg(msgKey);
        LogUtils.i("20170607","msgkey::"+msgKey);
        msg.incoming = false;
        msg.source = getAppGuid();
        LogUtils.i("20170607","targetGuid"+targetGuid);
        msg.target = DeviceGuid.newGuid(targetGuid);
        msg.setTag(Topic.newUnicastTopic(targetGuid).toString());
        return msg;
    }

    /**
     * 构造一个接收的消息
     */
    static public Msg newIncomingMsg(int msgKey, String sourceGuid) {
        Msg msg = new Msg(msgKey);
        msg.incoming = true;
        msg.source = DeviceGuid.newGuid(sourceGuid);
        msg.target = getAppGuid();
        LogUtils.i("20170531","sourceGuid：："+sourceGuid);
        msg.setTag(Topic.newUnicastTopic(sourceGuid).toString());
        return msg;
    }

    static DeviceGuid getAppGuid() {
        return DeviceGuid.newGuid("RKDRDA7h7U6d8X9oW");
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
