package com.akjava.mbl3d.expression.client.datalist;

import java.util.Comparator;
import java.util.List;

import com.akjava.mbl3d.expression.client.Emotion;


public class Mbl3dDataComparator implements Comparator<Mbl3dData>{
	private  int order;
	private List<Emotion> emotions;
	
	public Mbl3dDataComparator(List<Emotion> emotions) {
		super();
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
			return o1.getName().compareTo(o2.getName());
		}
		if(order==ORDER_ZA){
			return o2.getName().compareTo(o1.getName());
		}
		if(order==ORDER_TYPE){
			return findEmotionAt(o1) - findEmotionAt(o2);
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
