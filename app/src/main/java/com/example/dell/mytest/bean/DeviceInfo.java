package com.example.dell.mytest.bean;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Dell on 2018/7/23.
 */

public class DeviceInfo extends SubDeviceInfo{
    public long ownerId;

    public long groupId;

    public String mac;

    public DeviceInfo(){

    }
    public DeviceInfo(String guid, String bid, long ownerId) {
        super(guid, bid);
        this.ownerId = ownerId;
    }

    public List<SubDeviceInfo> subDevices = Lists.newArrayList();
}
