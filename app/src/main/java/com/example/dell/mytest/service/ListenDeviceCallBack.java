package com.example.dell.mytest.service;

/**
 * Created by Dell on 2018/7/23.
 */

public abstract class ListenDeviceCallBack {
    public void onSuccess(int code, String message){}
    public void onFailure(int code, String message){}
    public void onDeviceStatusReceived(int code, String messages){}
}
