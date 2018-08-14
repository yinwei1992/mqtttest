package com.example.dell.mytest.protocol;



import com.example.dell.mytest.msg.Msg;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RokiMsgMarshaller implements IAppMsgMarshaller {

    static public final int BufferSize = AbsPlatProtocol.BufferSize;
    static public final ByteOrder BYTE_ORDER = AbsPlatProtocol.BYTE_ORDER;

    @Override
    public byte[] marshal(Msg msg) throws Exception {

        int key = msg.getID();
        ByteBuffer buf = ByteBuffer.allocate(BufferSize).order(BYTE_ORDER);


            OvenNewMsgMar.marshaller(key, msg, buf);

        byte[] data = new byte[buf.position()];
        System.arraycopy(buf.array(), 0, data, 0, data.length);
        buf.clear();
        return data;
    }

    @Override
    public void unmarshal(Msg msg, byte[] payload) throws Exception {
        int key = msg.getID();
        OvenNewMsgMar.unmarshaller(key, msg, payload);
    }

}
