package com.example.dell.mytest.bean;

/**
 * Created by Dell on 2018/7/23.
 */

public class SubDeviceInfo {

    public String guid;

    public String name;

    public String bid;

    public int ver;

    public int mcuType;
    public boolean isConnected;

    public SubDeviceInfo(){

    }

    //周定钧
    public SubDeviceInfo(String guid, String bid) {
        this.guid = guid;
        this.bid = bid;
    }

    public String dc;


    public String dp;


    public String dt;


    public String displayType;
}
