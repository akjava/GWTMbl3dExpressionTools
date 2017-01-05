package com.akjava.mbl3d.expression.client.timetable;

import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataPredicates;


public class AnimationKeyFrame {

	public static final int TYPE_EYEBROW=0;
	public static final int TYPE_EYE=1;
	public static final int TYPE_MOUTH=2;
	public static final int TYPE_OTHER=3;

	private int type;//for easy filtering
	
	private String keyName;

public AnimationKeyFrame(String keyName, double time, double value) {
		super();
		this.keyName = keyName;
		this.time = time;
		this.value = value;
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
/**
 * millisecond base.
 */

public String toString(){
	return keyName+":"+time+":"+value;
}

private double time;
public int getType() {
	return type;
}
public void setType(int type) {
	this.type = type;
}
public String getKeyName() {
	return keyName;
}
public void setKeyName(String keyName) {
	this.keyName = keyName;
}
public double getTime() {
	return time;
}
public void setTime(double time) {
	this.time = time;
}
public double getValue() {
	return value;
}
public void setValue(double value) {
	this.value = value;
}
private double value;
}
