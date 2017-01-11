package com.akjava.mbl3d.expression.client;

import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;

public interface Mbl3dDataHolder {
	public Mbl3dData getDataById(int id,boolean enableBrows,boolean enableEyes,boolean enableMouth);
}
