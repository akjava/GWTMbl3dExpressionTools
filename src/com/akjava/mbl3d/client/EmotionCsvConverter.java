package com.akjava.mbl3d.client;

import java.util.List;

import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Converter;
import com.google.common.collect.Lists;

public class EmotionCsvConverter extends Converter<String, List<Emotion>>{

	@Override
	protected List<Emotion> doForward(String text) {
		List<Emotion> emotions=Lists.newArrayList();
		List<String[]> csvs=CSVUtils.csvTextToArrayList(text, '\t');
		String primary=null;
		for(String[] csv:csvs){
			if(csv.length==0){
				continue;
			}
			String p=csv[0];
			if(p.isEmpty()){
				p=primary;
			}
			primary=p;
			String second=null;
			if(csv.length>1){
				second=csv[1];
			}
			if(p!=null && second!=null){
				emotions.add(new Emotion(p, second));
			}
		}
		
		return emotions;
	}

	@Override
	protected String doBackward(List<Emotion> b) {
		// TODO Auto-generated method stub
		return null;
	}

}
