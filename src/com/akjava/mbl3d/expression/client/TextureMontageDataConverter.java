package com.akjava.mbl3d.expression.client;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class TextureMontageDataConverter extends Converter<String, List<TextureMontageData>>{

	@Override
	protected List<TextureMontageData> doForward(String lines) {
		List<String> csvs=CSVUtils.splitLinesWithGuava(lines);
		TextureMontageData data=null;
		
		List<TextureMontageData> datas=Lists.newArrayList();
		
		for(String csv:csvs){
			if(csv.startsWith("#")){
				data=new TextureMontageData();
				datas.add(data);
				String[] values=csv.substring(1).split(",");
				data.setKeyName(values[0]);
				
				if(values.length>1){
					if(!values[1].isEmpty()){
					data.setValue(values[1]);
					}
				}
				
				if(values.length>2){
					boolean enabled=ValuesUtils.toBoolean(values[2], false);
					data.setEnabled(enabled);
				}
				
				if(values.length>3){
					if(values[3].equals("color")){
						data.setType(TextureMontageData.TYPE_COLOR);
					}
				}
			}else{
				if(csv.isEmpty()){
					continue;
				}
				
				if(data==null){
					LogUtils.log("first line must header start with #:"+csv);
					continue;
				}
				
				data.getValues().add(csv);
				if(data.getValue()==null){//if null,first one is selected
					data.setValue(csv);
				}
			}
		}
		
		if(datas.isEmpty()){
			LogUtils.log("TextureMontageDataConverter:empty data");
		}
		
		return datas;
	}

	@Override
	protected String doBackward(List<TextureMontageData> datas) {
		List<String> lines=Lists.newArrayList();
		for(TextureMontageData data:datas){
			String header="#"+data.getKeyName()+","+data.getValue();
			if(data.isEnabled()){
				header+=","+"true";
			}else{
				if(data.getType()!=TextureMontageData.TYPE_LIST){
					header+=",";
				}
			}
			
			if(data.getType()!=TextureMontageData.TYPE_LIST){
				header+=",color";
			}
			lines.add(header);
			
			if(data.getValues()!=null){
			for(String v:data.getValues()){
				lines.add(v);
			}
			}
		}
		return Joiner.on("\r\n").join(lines);
	}

}
