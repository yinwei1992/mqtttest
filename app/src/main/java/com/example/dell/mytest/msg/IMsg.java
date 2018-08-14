package com.example.dell.mytest.msg;



/**
 * IO通讯消息接口
 *
 * @author sylar
 */
public interface IMsg extends IKey<Integer>, ITag {

    byte[] getBytes();
}