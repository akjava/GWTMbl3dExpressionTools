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
			values.put("eyes", "0");
			values.put("mouth", "0");
			values.put("eyebrow", "0");
			
			break;
		case 1:
			values.put("eyes", "1");
			break;
		case 2:
			values.put("eyes", "1");
			values.put("mouth", "1");
			break;
		case 3:
			values.put("eyes", "1");
			values.put("mouth", "1");
			values.put("eyebrow", "1");
			break;
		case 4:
			values.put("eyes", "0");
			break;
		case 5:
			values.put("mouth", "0");
			break;
		case 6:
			values.put("mouth", "1");
			break;
		case 7:
			values.put("eyes", "0.4");
			break;
		case 8:
			values.put("eyes", "0.6");
			break;
		case 9:
			values.put("eyes", "0");
			values.put("eyes2", "0");
			break;
		case 10:
			values.put("eyes", "1");
			values.put("eyes2", "1");
			break;
		}
		return data;
	}

}
