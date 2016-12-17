package com.akjava.mbl3d.expression.client.model;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.collect.Lists;

public class ModelInfoDataConverter extends Converter<String,List<ModelInfoData>> {

	@Override
	protected List<ModelInfoData> doForward(String text) {
		List<ModelInfoData> infoData=Lists.newArrayList();
		List<String> lines=CSVUtils.splitLinesWithGuava(text, true);
		for(String line:lines){
			String[] value=line.split(",");
			String path=value[0];
			if(path.isEmpty()){
				LogUtils.log("some how contain empty path skipped:"+line);
				continue;
			}
			double modifer=1;
			if(value.length>1){
				modifer=ValuesUtils.toDouble(value[1], 1);
			}
			infoData.add(new ModelInfoData(path, modifer));
		}
		return infoData;
	}

	@Override
	protected String doBackward(List<ModelInfoData> datas) {
		throw new RuntimeException("ModelInfoDataConverter:not support yet");
		
	}

}
