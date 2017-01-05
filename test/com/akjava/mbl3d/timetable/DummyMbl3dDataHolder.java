package com.akjava.mbl3d.timetable;

import java.util.Map;

import com.akjava.mbl3d.expression.client.Mbl3dDataHolder;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.google.common.collect.Maps;

public class DummyMbl3dDataHolder implements Mbl3dDataHolder{

	@Override
	public Mbl3dData getDataById(int id) {
		Mbl3dData data=new Mbl3dData();
		Map<String, String> values=Maps.newHashMap();
		data.setValues(values);
		switch(id){
		case 0:
			values.put("eye", "0");
			values.put("mouth", "0");
			values.put("eyebrow", "0");
			
			break;
		case 1:
			values.put("eye", "1");
			break;
		case 2:
			values.put("eye", "1");
			values.put("mouth", "1");
			break;
		case 3:
			values.put("eye", "1");
			values.put("mouth", "1");
			values.put("eyebrow", "1");
			break;
		}
		return data;
	}

}
