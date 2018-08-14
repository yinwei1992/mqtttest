package com.example.dell.mytest.protocol;
import com.example.dell.mytest.msg.IMsg;

import java.util.List;

/**
 * 协议解析接口
 *
 * @author sylar
 */
public interface IProtocol {

    byte[] encode(IMsg msg) throws Exception;

    List<IMsg> decode(byte[] data, Object... params) throws Exception;
}