package com.example.dell.mytest.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.example.dell.mytest.msg.IMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 2018/7/23.
 */

public class MqttServiceAPI implements ServiceConnection {
    private Intent serviceIntent;
    // private Context mContext;
    private MqttService.ServiceBinder binder;
    private MqttServiceListener msl;

    private boolean mqtttag = false;
    private boolean msgtag = false;

    private Handler tcHandler;

    /**
     * init context
     *
     * @param context context
     */
    // public MiCOMQTT(Context context) {
    // ctx = context;
    // }

    /**
     * start mqtt service
     * @param context context
     * @param host host
     * @param port port
     * @param userName userName
     * @param passWord passWord
     * @param clientID clientID
     * @param topic  topic
     * @param isencrypt is need secrety
     * @param mqttServiceListener mqttlistener
     */
    public void startMqttService(Context context, String host, String port,
                                 String userName, String passWord, String clientID, ArrayList<String> topic,
                                 boolean isencrypt,
                                 MqttServiceListener mqttServiceListener) {
        msl = mqttServiceListener;
        if (ComHelper.checkPara(host, userName, clientID)) {
            if (!mqtttag) {
                serviceIntent = new Intent(context, MqttService.class);
                serviceIntent.putExtra("host", host);
                serviceIntent.putExtra("port", port);
                serviceIntent.putExtra("userName", userName);
                serviceIntent.putExtra("passWord", passWord);
                serviceIntent.putExtra("clientID", clientID);
                serviceIntent.putStringArrayListExtra("topic",topic);
                serviceIntent.putExtra("isencrypt", isencrypt);
                context.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
                mqtttag = true;
            }
        } else {
            msl.onMqttReceiver(MQTTErrCode.EMPTY_CODE, MQTTErrCode.EMPTY);
        }

        tcHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // super.handleMessage(msg);
                msl.onMqttReceiver(msg.what, (String) msg.obj);
            }
        };
    }

    /**
     * stop mqtt service
     * @param context context
     */
    public void stopMqttService(Context context) {
        if (mqtttag) {
            context.unbindService(this);
            mqtttag = false;
            msgtag = false;
        }
    }

    /**
     * send command to device
     *
     * @param topic topics
     * @param qos 012
     * @param retained 0
     */
    public void publishCommand(String topic, byte[] msg, int qos,
                               boolean retained) {
        if (mqtttag) {
            binder.publishAPI(topic, msg, qos, retained);
        }
    }

    public void stopRecvMessage() {
        if (msgtag && mqtttag) {
            binder.stopRecvMsgAPI();
            msgtag = false;
        }
    }

    public void recvMessage() {
        if (!msgtag && mqtttag) {
            binder.recvMsgAPI();
            msgtag = true;
        }
    }

    public void subscribe(String topic, int qos) {
        if (mqtttag) {
            binder.addSubscribeAPI(topic, qos);
        }
    }

    public void unsubscribe(String topic) {
        if (mqtttag) {
            binder.unSubscribeAPI(topic);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (MqttService.ServiceBinder) service;
        if (!msgtag) {
            binder.bindListenerAPI(new MqttServiceListener() {

                @Override
                public void onMqttReceiver(int code, String messages) {
                    // msl.onMqttReceiver(msgType, messages);
                    Message msg = new Message();
                    msg.what = code;
                    msg.obj = messages;
                    tcHandler.sendMessage(msg);
                }
            });
            msgtag = true;
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
    }
}
