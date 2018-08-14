package com.example.dell.mytest.service;

/**
 * Created by Dell on 2018/7/23.
 */

public class MQTTErrCode {
    public static int SUCCESS_CODE = 0;
    public static int EMPTY_CODE = 4201;
    public static int CONTEXT_CODE = 4202;
    public static int BUSY_CODE = 4203;
    public static int CLOSED_CODE = 4204;
    public static int QOS_CODE = 4205;
    public static int EXCEPTION_CODE = 4206;

    public static String SUCCESS = toJsonM("success");
    public static String EMPTY = toJsonM("invalid param");
    public static String CONTEXT = toJsonM("invalid context");
    public static String BUSY = toJsonM("mqtt busy");
    public static String CLOSED = toJsonM("mqtt closed");
    public static String QOSERR = toJsonM("qos must within(0,1,2)");

    public static int _PAYLOAD_CODE = 4200;
    public static int _CON_CODE = 4210;
    public static int _TOPIC_CODE = 4211;
    public static int _STOP_CODE = 4212;
    public static int _SUB_CODE = 4213;
    public static int _RESUB_CODE = 4214;
    public static int _UNSUB_CODE = 4215;
    public static int _EXCEPTION_CODE = 4216;
    public static int _LOST_CODE = 4217;
    public static int _DISCON_CODE = 4218;
    public static int _PUB_CODE = 4219;

    public static String _CON_MSG = toJsonS("connected");
    public static String _TOPIC_MSG = toJsonS("topic missing");
    public static String _STOP_MSG = toJsonS("stopped");
    public static String _SUB_MSG = toJsonS("subscribe success");
    public static String _RESUB_MSG = toJsonS("re-subscribe success");
    public static String _UNSUB_MSG = toJsonS("unsubscribe success");
    public static String _EXCEP_MSG = toJsonS("connect exception");
    public static String _LOST_MSG = toJsonS("lost");
    public static String _PUB_MSG = toJsonS("publish success");
    public static String _DISCON_MSG = toJsonS("disconnect");

    private static String toJsonM(String message){
        return "{\"message\":\""+ message +"\"}";
    }
    private static String toJsonS(String message){
        return "{\"status\":\""+ message +"\"}";
    }
}
