package com.akjava.mbl3d.expression.client.timetable;

import com.akjava.gwt.lib.client.json.JSONObjectWrapper;
import com.google.common.base.Converter;
import com.google.gwt.json.client.JSONObject;

public class TimeTableDataConverter extends Converter<TimeTableData,JSONObject>{

	@Override
	protected JSONObject doForward(TimeTableData data) {
		JSONObject object=new JSONObject();
		JSONObjectWrapper wrapper=new JSONObjectWrapper(object);
		
		if(data.getLabel()!=null){
			wrapper.setString("label", data.getLabel());
		}
		
		wrapper.setDouble("time", data.getTime());
		wrapper.setDouble("waittime", data.getWaitTime());
		
		wrapper.setBoolean("enableBrows", data.isEnableBrows());
		wrapper.setBoolean("enableEyes", data.isEnableEyes());
		wrapper.setBoolean("enableMouth", data.isEnableMouth());
		
		wrapper.setDouble("ratio", data.getRatio());
		
		
		wrapper.setBoolean("reference", data.isReference());
		wrapper.setInt("referenceId",data.getReferenceId());
		
		
		//TODO set direct case
		
		return object;
	}

	@Override
	protected TimeTableData doBackward(JSONObject jsonValue) {
		TimeTableData data=new TimeTableData();
		JSONObjectWrapper wrapper=new JSONObjectWrapper(jsonValue);
		
		data.setLabel(wrapper.getString("label", data.getLabel()));
		
		data.setTime(wrapper.getDouble("time", data.getTime()));
		data.setWaitTime(wrapper.getDouble("waittime", data.getWaitTime()));
		
		data.setEnableBrows(wrapper.getBoolean("enableBrows", data.isEnableBrows()));
		data.setEnableEyes(wrapper.getBoolean("enableEyes", data.isEnableEyes()));
		data.setEnableMouth(wrapper.getBoolean("enableMouth", data.isEnableMouth()));
		
		data.setRatio(wrapper.getDouble("ratio", data.getRatio()));
		
		data.setReference(wrapper.getBoolean("reference", data.isReference()));
		data.setReferenceId(wrapper.getInt("referenceId",data.getReferenceId()));
		
		return data;
	}

}
