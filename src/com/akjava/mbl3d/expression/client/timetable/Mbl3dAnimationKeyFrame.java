package com.akjava.mbl3d.expression.client.timetable;

import com.akjava.gwt.three.client.java.ui.experiments.MorphTargetKeyFrame;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataPredicates;


public class Mbl3dAnimationKeyFrame  extends MorphTargetKeyFrame{

	public static final int TYPE_EYEBROW=0;
	public static final int TYPE_EYE=1;
	public static final int TYPE_MOUTH=2;
	public static final int TYPE_OTHER=3;

	private int type;//for easy filtering
	
public Mbl3dAnimationKeyFrame(String keyName, double time, double value) {
		super(keyName,time,value);
		
		//TODO set type by keyName
		if(Mbl3dDataPredicates.passBrowOnly().apply(keyName)){
			type=TYPE_EYEBROW;
		}else if(Mbl3dDataPredicates.passEyesOnly().apply(keyName)){
			type=TYPE_EYE;
		}else if(Mbl3dDataPredicates.passMouthOnly().apply(keyName)){
			type=TYPE_MOUTH;
		}else{
			type=TYPE_OTHER;
		}
	}

public int getType() {
	return type;
}
public void setType(int type) {
	this.type = type;
}

}
