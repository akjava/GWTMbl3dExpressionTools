package com.akjava.mbl3d.expression.client.timetable;

import java.util.List;

import com.akjava.gwt.lib.client.json.JSONObjectWrapper;
import com.google.common.base.Converter;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;

public class TimeTableDataBlockConverter extends Converter<TimeTableDataBlock,JSONObject>{

	@Override
	protected JSONObject doForward(TimeTableDataBlock data) {
		JSONObject object=new JSONObject();
		JSONObjectWrapper wrapper=new JSONObjectWrapper(object);
		
		if(data.getName()!=null){
			wrapper.setString("name", data.getName());
		}
		
		wrapper.setDouble("startAt", data.getStartAt());
		wrapper.setDouble("beforeMargin", data.getBeforeMargin());
		wrapper.setDouble("afterMargin", data.getAfterMargin());
		
		wrapper.setBoolean("loop", data.isLoop());
		wrapper.setInt("loopTime",data.getLoopTime());
		wrapper.setDouble("loopInterval", data.getLoopInterval());
		
		if(data.getTimeTableDatas()!=null){
			Iterable<JSONObject> objects=new TimeTableDataConverter().convertAll(data.getTimeTableDatas());
			JsArray<JavaScriptObject> values=JsArray.createArray().cast();
			for(JSONObject obj:objects){
				values.push(obj.getJavaScriptObject());
			}
			wrapper.setArray("timeTableDatas", values);
		}
		
		return object;
	}

	@Override
	protected TimeTableDataBlock doBackward(JSONObject jsonValue) {
		TimeTableDataBlock data=new TimeTableDataBlock();
		JSONObjectWrapper wrapper=new JSONObjectWrapper(jsonValue);
		
		data.setName(wrapper.getString("name", data.getName()));
	
	
	data.setStartAt(wrapper.getDouble("startAt", data.getStartAt()));
	data.setBeforeMargin(wrapper.getDouble("beforeMargin", data.getBeforeMargin()));
	data.setAfterMargin(wrapper.getDouble("afterMargin", data.getAfterMargin()));
	
	data.setLoop(wrapper.getBoolean("loop", data.isLoop()));
	data.setLoopTime(wrapper.getInt("loopTime",data.getLoopTime()));
	data.setLoopInterval(wrapper.getDouble("loopInterval", data.getLoopInterval()));
	
	JsArray<JavaScriptObject> array=wrapper.getArray("timeTableDatas");
	if(array!=null){
		List<JSONObject> objects=Lists.newArrayList();
		for(int i=0;i<array.length();i++){
			objects.add(new JSONObject(array.get(i)));
		}
		data.setTimeTableDatas(Lists.newArrayList(new TimeTableDataConverter().reverse().convertAll(objects)));
	}
	
		return data;
	}

}
