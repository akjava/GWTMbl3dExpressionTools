package com.akjava.mbl3d.expression.client.datalist;

import java.util.Comparator;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.mbl3d.expression.client.Emotion;
import com.google.common.collect.ComparisonChain;


public class Mbl3dDataComparator implements Comparator<Mbl3dData>{
	private  int order;
	private List<Emotion> emotions;
	
	public Mbl3dDataComparator() {
		super();
	}
	public void setEmotions(List<Emotion> emotions) {
		this.emotions = emotions;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public static final int ORDER_ID=0;
	public static final int ORDER_ID_DESC=1;
	public static final int ORDER_AZ=2;
	public static final int ORDER_ZA=3;
	public static final int ORDER_TYPE=4;
	@Override
	public int compare(Mbl3dData o1, Mbl3dData o2) {
		
		
		
		if(order==ORDER_ID_DESC){
			return o2.getId()-o1.getId();
		}
		if(order==ORDER_AZ){
			return ComparisonChain.start().compare(o1.getName(),o2.getName()).result();
			/*
			String name1=o1.getName()!=null?o1.getName():"";
			String name2=o2.getName()!=null?o2.getName():"";
			return name1.compareTo(name2);
			*/
		}
		if(order==ORDER_ZA){
			String name1=o1.getName()!=null?o1.getName():"";
			String name2=o2.getName()!=null?o2.getName():"";

			return name2.compareTo(name1);
		}
		if(order==ORDER_TYPE){
			//return findEmotionAt(o1) -  findEmotionAt(o2);
			/*
			 * some how not good at null handling(getName());
			 */
			
			return ComparisonChain.start().compare(findEmotionAt(o1), findEmotionAt(o2))
					.compare(o1.getName()!=null?o1.getName():"", o2.getName()!=null?o2.getName():"")
					.compare(Mbl3dDataUtils.getEyesKey(o1), Mbl3dDataUtils.getEyesKey(o2))
					.compare(Mbl3dDataUtils.getMouthKey(o1), Mbl3dDataUtils.getMouthKey(o2))
					.compare(o1.getDescription(),o2.getDescription())
					.compare(o1.getId(), o2.getId())
			.result();
			
			
		}		
		//ORDER_ID
		return o1.getId()-o2.getId();
	}
	
	private int findEmotionAt(Mbl3dData data){
		for(int i=0;i<emotions.size();i++){
			Emotion emotion=emotions.get(i);
			if(emotion==null){
				continue;
			}
			if(emotion.getSecondary().equals(data.getType())){
				return i;
			}
		}
		return -1;
	}

}
