package com.example.dell.mytest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.dell.mytest.msg.BytesMsg;
import com.example.dell.mytest.msg.IMsg;
import com.example.dell.mytest.utils.LogUtils;
import com.google.common.collect.Lists;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Dell on 2018/7/23.
 */

public class MqttService extends Service {
    private ServiceBinder serviceBinder = new ServiceBinder();

    private static MqttClient client = null;
    private MqttConnectOptions options;
    private Handler handler;
    private ScheduledExecutorService scheduler = null;
    private MqttServiceListener mMqttServiceListener = null;
    private Boolean recvTag = false;
    private Boolean connectTag = false;

    private String[] topicList = null;

    private ArrayList<String> topicLists = null;

    @Override
    public IBinder onBind(Intent intent) {

        String host = intent.getStringExtra("host");
        String port = intent.getStringExtra("port");
        String userName = intent.getStringExtra("userName");
        String passWord = intent.getStringExtra("passWord");
        String clientID = intent.getStringExtra("clientID");
        ArrayList<String> topicLists = intent.getStringArrayListExtra("topic");
        boolean isencrypt = intent.getBooleanExtra("isencrypt", false);

        startMqttService(host, port, userName, passWord, clientID, topicLists, isencrypt);

        return serviceBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendMsgToClient(MQTTErrCode._STOP_CODE, MQTTErrCode._STOP_MSG);
        stopMqttService();
    }

    /**
     * start mqtt service
     *
     * @param host      host
     * @param port      port
     * @param userName  userName
     * @param password  password
     * @param clientID  clientID
     * @param topic     topic
     * @param isencrypt is need secrety
     */
    public void startMqttService(String host, String port, String userName, String password, String clientID, final ArrayList<String> topic, boolean isencrypt) {

        //mybe it without port
        String URI = "";
        if (ComHelper.checkPara(port))
            URI = host + ":" + port;
        else
            URI = host;

        if (isencrypt) {
            mqttInit("ssl://" + URI, userName, password, clientID, true);
        } else {
            mqttInit("tcp://" + URI, userName, password, clientID, false);
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {// message arrived
                    Log.d("20180723", (String) msg.obj);
                    sendMsgToClient(MQTTErrCode._PAYLOAD_CODE, (String) msg.obj);
                } else if (msg.what == 2) {// connection
                    connectTag = true;
                    if (ComHelper.checkListPara(topic)){
                        reSubscribe(topic);
                    }

                    Log.d("20180723", "Connected");
                    sendMsgToClient(MQTTErrCode._CON_CODE, MQTTErrCode._CON_MSG);
                } else if (msg.what == 3) {// connect exception
                    Log.d("20180723", "Connect Exception");
                    sendMsgToClient(MQTTErrCode._EXCEPTION_CODE, MQTTErrCode._EXCEP_MSG);
                }
            }
        };
        startReconnect();
    }

    /**
     * subscibe again
     *
     * @param topic topic
     */
    private void reSubscribe(ArrayList<String> topic) {
        if (topic != null) {
            String[] topicFilters = new String[topic.size()];
            topic.toArray(topicFilters);

            int[] qos = new int[topic.size()];
            Arrays.fill(qos, 0);
            try {
                client.subscribe(topicFilters,qos);
            } catch (Exception e) {
                LogUtils.i("20180724","error::"+e.getMessage());
                e.printStackTrace();
            }

        }
        sendMsgToClient(MQTTErrCode._RESUB_CODE, MQTTErrCode._RESUB_MSG);
    }

    /**
     * init mqtt
     *
     * @param host      host
     * @param userName  userName
     * @param password  password
     * @param clientID  clientID
     * @param isencrypt is need
     */

    private void mqttInit(String host, String userName, String password, String clientID, boolean isencrypt) {
        try {
            /*
             * host client id must different MemoryPersistence is default
			 */
            client = new MqttClient(host, clientID, new MemoryPersistence());
            // MQTT setting
            options = new MqttConnectOptions();
            /*
			 * if clean session false, the service will remember the id of
			 * client true, it will create a new id when it connected
			 */
            options.setCleanSession(true);
			/*
			 * user name
			 */
            options.setUserName(userName);
			/*
			 * password
			 */
            options.setPassword(password.toCharArray());


			/*
			 * overtime 10ms
			 */
            options.setConnectionTimeout(10);
			/*
			 * HeartBeat service will send heart beat once 1.5*20 but it doesn't
			 * reconnect
			 */
             options.setKeepAliveInterval(30);

            if (isencrypt) {
                //创建SSL连接
                try {
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                    tmf.init((KeyStore) null);
                    TrustManager[] trustManagers = tmf.getTrustManagers();

//					创建返利授权证书
                    SSLContext sslc = SSLContext.getInstance("SSLv3");

                    sslc.init(null, trustManagers, null);

//					配置mqtt连接
                    options.setSocketFactory(sslc.getSocketFactory());
                } catch (NoSuchAlgorithmException e) {
                    LogUtils.i("20180723", "e:" + e.getMessage());
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    LogUtils.i("20180723", "e:" + e.getMessage());
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    LogUtils.i("20180723", "e:" + e.getMessage());
                    e.printStackTrace();
                }
            }

            // call back
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    LogUtils.i("20180725","cause::"+cause.getMessage()+" rrr:"+cause.getCause()+" code:");
                    connectTag = false;
                    // Log.d(LOG_TAG, "Lost");
                    sendMsgToClient(MQTTErrCode._LOST_CODE, MQTTErrCode._LOST_MSG);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                    sendMsgToClient(MQTTErrCode._PUB_CODE, MQTTErrCode._PUB_MSG);
                }

                @Override
                public void messageArrived(String topicName, MqttMessage payload)
                        throws Exception {
                    BytesMsg bm = new BytesMsg(payload.getPayload());
                    Log.i("20180723", "topicName::" + topicName + "pay::" + bm.toString());
                    if (recvTag) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "{\"topic\":\"" + topicName + "\",\"payload\":" + payload.toString().getBytes("utf-8") + "}";
                        handler.sendMessage(msg);
                    }
                }
            });
        } catch (MqttException e) {
            LogUtils.i("20180723","e:"+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * subscribe topic
     *
     * @param topic topic
     * @param qos   012
     */
    public void subscribeService(String topic, int qos) {
        if (topic.equals("")) {
            sendMsgToClient(MQTTErrCode._TOPIC_CODE, MQTTErrCode._TOPIC_MSG);
        } else if (!connectTag) {
            sendMsgToClient(MQTTErrCode._DISCON_CODE, MQTTErrCode._DISCON_MSG);
        } else {
            try {
                client.subscribe(topic, qos);

                int ssize = topicList.length;
                String[] tmp = new String[ssize + 1];
                int i = 0;
                for (; i < ssize; i++) {
                    tmp[i] = topicList[i];
                }
                tmp[i] = topic;

                topicList = tmp;
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendMsgToClient(MQTTErrCode._SUB_CODE, MQTTErrCode._SUB_MSG);
        }
    }

    /**
     * cancel subscribe topic
     *
     * @param topic topic
     */
    public void unSubscribeService(String topic) {
        if (topic.equals("")) {
            sendMsgToClient(MQTTErrCode._TOPIC_CODE, MQTTErrCode._TOPIC_MSG);
        } else if (!connectTag) {
            sendMsgToClient(MQTTErrCode._DISCON_CODE, MQTTErrCode._DISCON_MSG);
        } else {
            try {
                client.unsubscribe(topic);

                int ssize = topicList.length;
                int i = 0, j = 0;
                for (; i < ssize; i++) {
                    if (topic != topicList[i]) {
                        j++;
                    }
                }
                String[] tmp = new String[j];
                i = 0;
                j = 0;
                for (; i < ssize; i++) {
                    if (topic != topicList[i]) {
                        tmp[j++] = topicList[i];
                    }
                }
                topicList = tmp;

            } catch (Exception e) {
                e.printStackTrace();
            }
            sendMsgToClient(MQTTErrCode._UNSUB_CODE, MQTTErrCode._UNSUB_MSG);
        }
    }

    /**
     * if the connect is lost, it will reconnect
     */
    private void startReconnect() {

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (!client.isConnected()) {
                    connect();
                }
            }
        }, 1* 1000, 5 * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * connect the mqtt service
     */
    private void connect() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    client.connect(options);
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
//                    connectTag = true;
                } catch (Exception e) {
                    Log.i("20180723", "e:" + e.getMessage());
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    /**
     * start receive messages
     *
     * @param mqttServiceListener call back
     */
    public void initMessageSend(MqttServiceListener mqttServiceListener) {
        mMqttServiceListener = mqttServiceListener;
        recvTag = true;
    }

    public void recvMsgService() {
        recvTag = true;
    }

    /**
     * stop receive messages
     */
    public void stopRecvMsgService() {
        recvTag = false;
    }

    /**
     * publish command to service
     *
     * @param topic    topic
     * @param qos      qos
     * @param retained retained
     */
    public void publishService(String topic, byte[] msg, int qos,
                               boolean retained) {
        try {
            client.publish(topic, msg, qos, retained);
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * stop mqtt service
     */
    public void stopMqttService() {
        try {
            if (null != scheduler) {
                scheduler.shutdown();
            }
            if (null != client && connectTag) {
                client.disconnect();
            }
            connectTag = false;
            recvTag = false;
        } catch (MqttException e) {
            LogUtils.i("20180725","e:"+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param code    code
     * @param message message
     */
    private void sendMsgToClient(int code, String message) {
        if (null != mMqttServiceListener) {
            mMqttServiceListener.onMqttReceiver(code, message);
        }
    }

	/*
	 * ---------------------------------------end mqtt
	 */

    /*
     * ---------------------------------------begin binder
     */
    public class ServiceBinder extends android.os.Binder {
        public MqttService getService() {
            return MqttService.this;
        }

        /**
         * bind listener then we can send message to api
         *
         * @param mqttServiceListener callback
         */
        public void bindListenerAPI(MqttServiceListener mqttServiceListener) {
            initMessageSend(mqttServiceListener);
        }

        /**
         * stop sending message to api
         */
        public void stopRecvMsgAPI() {
            stopRecvMsgService();
        }

        /**
         * publish from api
         *
         * @param topic    topic
         * @param qos      012
         * @param retained 0
         */
        public void publishAPI(String topic, byte[] msg, int qos,
                               boolean retained) {
            publishService(topic, msg, qos, retained);
        }

        /**
         * add subscribe from api
         *
         * @param topic topic
         * @param qos   012
         */
        public void addSubscribeAPI(String topic, int qos) {
            subscribeService(topic, qos);
        }

        /**
         * remove one topic of topics by api
         *
         * @param topic topic
         */
        public void unSubscribeAPI(String topic) {
            unSubscribeService(topic);
        }

        /***
         * want receive message again
         */
        public void recvMsgAPI() {
            recvMsgService();
        }
    }
	/*
	 * --------------------------------------end binder
	 */

}
