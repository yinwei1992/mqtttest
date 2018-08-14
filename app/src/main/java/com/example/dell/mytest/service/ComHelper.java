package com.example.dell.mytest.service;

import java.util.ArrayList;

/**
 * Created by Dell on 2018/7/23.
 */

public class ComHelper {
    /**
     * Check argument, whether it is null or blank
     *
     * @param param any parament
     * @return is null or nil
     */
    public static boolean checkPara(String... param) {
        if (null == param || param.equals("")) {
            return false;
        } else if (param.length > 0) {
            for (String str : param) {
                if (null == str || str.equals("")) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean checkListPara(ArrayList<String> param) {
        if (null == param) {
            return false;
        } else if (param.size() > 0) {
            for (String str : param) {
                if (null == str || str.equals("")) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public static void successCBCtrlDev(int code, String message, ListenDeviceCallBack ctrldevcb){
        if (null == ctrldevcb)
            return;
        ctrldevcb.onSuccess(code, message);
    }
    public static void failureCBCtrlDev(int code, String message, ListenDeviceCallBack ctrldevcb) {
        if (null == ctrldevcb)
            return;
        ctrldevcb.onFailure(code, message);
    }
    public static void onDevStatusReceived(int code, String messages, ListenDeviceCallBack ctrldevcb){
        if (null == ctrldevcb)
            return;
        ctrldevcb.onDeviceStatusReceived(code, messages);
    }
}
