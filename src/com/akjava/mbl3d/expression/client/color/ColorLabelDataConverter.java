package com.akjava.mbl3d.expression.client.color;

import java.util.ArrayList;
import java.util.List;

import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;

public  class ColorLabelDataConverter extends Converter<List<ColorLabelData>,String>{
		private String separator=",";//TODO support separator
		@Override
		protected List<ColorLabelData> doBackward(String text) {
			text=CSVUtils.toNLineSeparator(text);
			List<ColorLabelData> list=new ArrayList<ColorLabelData>();
			String[] lines=text.split("\n");
			for(String line:lines){
				String[] label_color=line.split(separator);
				if(label_color.length==0){
					continue;
				}
				String label=label_color[0];
				String color="";
				
				if(label_color.length>1){
					color=label_color[1];
				}
				list.add(new ColorLabelData(label, color));
			}
			return list;
		}

		@Override
		protected String doForward(List<ColorLabelData> datas) {
			
			//LogUtils.log("converter:"+Joiner.on("\n").join(datas));
			return Joiner.on("\n").join(datas);
		}
		
	}