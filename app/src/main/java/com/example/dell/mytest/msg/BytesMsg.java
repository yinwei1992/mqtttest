package com.example.dell.mytest.msg;


import com.example.dell.mytest.utils.StringUtils;

public class BytesMsg extends AbsMsg {

	public BytesMsg(byte[] data) {
		this.data = data;
	}

	@Override
	public String toString() {
		if (data != null)
			return StringUtils.bytes2Hex(data);
		else
			return getClass().getSimpleName();
	}
}
