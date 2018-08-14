package com.example.dell.mytest.protocol;

import android.content.Context;

abstract public class AbsProtocol implements IProtocol {

	protected Context cx;
	protected Object[] params;


	public void init(Context cx, Object... params) {
		this.cx = cx;
		this.params = params;
	}

}
