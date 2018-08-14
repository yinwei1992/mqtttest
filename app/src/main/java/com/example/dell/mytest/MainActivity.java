package com.example.dell.mytest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dell.mytest.msg.BytesMsg;
import com.example.dell.mytest.msg.Msg;


import com.example.dell.mytest.polling.AbsOnPolling;
import com.example.dell.mytest.polling.AbsPolling;
import com.example.dell.mytest.polling.FanOnPolling;
import com.example.dell.mytest.polling.OvenOnPolling;
import com.example.dell.mytest.polling.SteamOnPolling;
import com.example.dell.mytest.protocol.MqttProtocol;
import com.example.dell.mytest.protocol.MsgKeys;
import com.example.dell.mytest.protocol.MsgParams;
import com.example.dell.mytest.protocol.TerminalType;
import com.example.dell.mytest.service.MqttServiceAPI;
import com.example.dell.mytest.service.MqttServiceListener;
import com.example.dell.mytest.utils.LogUtils;
import com.example.dell.mytest.utils.TaskService;
import com.example.dell.mytest.utils.TimeUtils;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    String host = "你的服务器";
    String port = "端口号";
    String userName = "用户名";
    String passWord = "密码";
    String clientId = "设备id";
    ArrayList<String> topic = new ArrayList<>();
    boolean isencrypt = false;

    Button btn3;
    Button btn1;
    MqttProtocol mqttProtocol = new MqttProtocol();
    protected short terminalType = TerminalType.getType();
    protected ScheduledFuture<?> future;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    File file=new File(Environment.getExternalStorageDirectory(),"ww.txt");
    StringBuffer sb = new StringBuffer();
    Date d = new Date();
    private MqttServiceAPI mqttServiceAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn3 = (Button) findViewById(R.id.btn_push);
        btn1 = (Button) findViewById(R.id.btn1);
        verifyStoragePermissions(MainActivity.this);
        topic.add("订阅的主题");
        mqttServiceAPI = new MqttServiceAPI();
        mqttServiceAPI.startMqttService(MainActivity.this, host, port,
                userName, passWord, clientId, topic, isencrypt,
                new MqttServiceListener() {
                    @Override
                    public void onMqttReceiver(int code, String messages) {
                        String time = TimeUtils.getNowTime(d);
                        writeText(time+":"+messages+"\n");
                       if (messages!=null){
                           Toast.makeText(MainActivity.this,"轮询正常"+messages,Toast.LENGTH_SHORT).show();
                       }
                    }
                });

        OvenOnPolling ovenOnPolling = new OvenOnPolling();
        SteamOnPolling steamOnPolling = new SteamOnPolling();
        FanOnPolling fanOnPolling = new FanOnPolling();
        final AbsOnPolling absOnPolling = new AbsOnPolling(ovenOnPolling,mqttServiceAPI,mqttProtocol);
        final AbsOnPolling absOnPolling1 = new AbsOnPolling(steamOnPolling,mqttServiceAPI,mqttProtocol);
        final AbsOnPolling absOnPolling2 = new AbsOnPolling(fanOnPolling,mqttServiceAPI,mqttProtocol);
        future = TaskService.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
               absOnPolling.onPolling();
                absOnPolling2.onPolling();
            }
        },1000 * 2,1000 * 10,TimeUnit.MILLISECONDS);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    byte[] data = null;
                    Msg reqMsg = Msg.newRequestMsg(MsgKeys.setOvenStatusControl_Req, "RR026C71bWajnvu00");
                    reqMsg.putOpt(MsgParams.TerminalType, terminalType);   // 控制端类型区分
                    reqMsg.putOpt(MsgParams.UserId, "4166351656");
                    reqMsg.putOpt(MsgParams.OvenStatus, (short)2);
                    data = mqttProtocol.encode(reqMsg);
                    String topic = reqMsg.getTag();

                    LogUtils.i("20180724","newtopic::"+topic);
                    mqttServiceAPI.publishCommand(topic,data,0,false);
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }






    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }





    private void writeText(String content) {

        if (content == null || content.equals("")) {
            return;
        }

        String filePath = "/storage/emulated/0/kkk/";
        File dirFile = new File(filePath);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        String fileName =  "gggg.txt";
        File file = new File(filePath + fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file,true);
            fos.write(content.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    @Override
    protected void onDestroy() {
        mqttServiceAPI.stopMqttService(MainActivity.this);
        super.onDestroy();
    }

      /* btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    byte[] data = null;
                    Msg reqMsg = Msg.newRequestMsg(MsgKeys.setOvenStatusControl_Req, "RR026C71bWajnvu00");
                    reqMsg.putOpt(MsgParams.TerminalType, terminalType);   // 控制端类型区分
                    reqMsg.putOpt(MsgParams.UserId, "4166351656");
                    reqMsg.putOpt(MsgParams.OvenStatus, (short)2);
                    data = mqttProtocol.encode(reqMsg);
                    String topic = reqMsg.getTag();

                    LogUtils.i("20180724","newtopic::"+topic);
                    mqttServiceAPI.publishCommand(topic,data,0,false);
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });*/
}
