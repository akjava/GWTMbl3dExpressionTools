package com.akjava.mbl3d.expression.client.datalist;

import java.util.Map;

import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import com.google.common.collect.Maps;

public  class Mbl3dDataConverter extends Converter<String,Mbl3dData>{
	public static final MapSplitter splitter=Splitter.on("\t").withKeyValueSeparator("\f");
	public static final MapJoiner joiner=Joiner.on("\t").withKeyValueSeparator("\f");
	@Override
	protected Mbl3dData doForward(String text) {
		Map<String,String> map=Maps.newHashMap(splitter.split(text));
		
		
		String name=map.remove("name");
		String type=map.remove("type");
		String description=map.remove("description");
		
		
		return new Mbl3dData(name,type, description, map);
	}

	@Override
	protected String doBackward(Mbl3dData data) {
		Map<String,String> copy=Maps.newHashMap(data.getValues());
		if(data.getType()!=null){
			copy.put("type", data.getType());
		}
		if(data.getDescription()!=null){
			copy.put("description", data.getDescription());
		}
		
		return joiner.join(copy);
	}	
}