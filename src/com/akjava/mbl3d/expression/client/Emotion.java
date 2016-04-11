package com.akjava.mbl3d.expression.client;

import java.util.List;

public class Emotion {
private String primary;
public Emotion(String primary, String secondary) {
	super();
	this.primary = primary;
	this.secondary = secondary;
}
public String getPrimary() {
	return primary;
}
public void setPrimary(String primary) {
	this.primary = primary;
}
public String getSecondary() {
	return secondary;
}
public void setSecondary(String secondary) {
	this.secondary = secondary;
}
private String secondary;

public String toString(){
	return primary+","+secondary;
}

public static Emotion findEmotionBySecondaryName(String name,List<Emotion> list){
	for(Emotion e:list){
		if(e.getSecondary().equals(name)){
			return e;
		}
	}
	return null;
}
}
