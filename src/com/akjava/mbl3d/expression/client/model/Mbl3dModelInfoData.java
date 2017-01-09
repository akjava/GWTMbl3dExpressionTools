package com.akjava.mbl3d.expression.client.model;

import java.util.List;

import com.akjava.gwt.three.client.java.file.MorphtargetsModifier;
import com.akjava.mbl3d.expression.client.Mbl3dMorphtargetsModifier;
import com.google.common.collect.Lists;

/*
 * simple mbl3dmodel
 */
public class Mbl3dModelInfoData {

	
public static final List<String> needModiferKeys=Lists.newArrayList("eyes01_min","eyes01_max","eyes03_min","eyes03_max");
	
	
private String path;
public Mbl3dModelInfoData(String path, double eyeModierValue) {
	super();
	this.path = path;
	

	eyeModifier=new Mbl3dMorphtargetsModifier();
	
	for(String key:needModiferKeys){
		eyeModifier.set("Expressions_"+key, eyeModierValue);
	}
	
}

/*
 * on morph target animation,
 * for Expression eyes01-min/max and eyes03-min/max
 * 1.0 value is for largest eye,if eys is small ,eye-motion would be broken.
 * To avoid these, Define modifier value load ,
 * 
 * Warning
 * 
 * only this app modifiering when set morph target influence value,do support modifier eye-closing by your app.
 */
private MorphtargetsModifier eyeModifier;

public String getPath() {
	return path;
}
public void setPath(String path) {
	this.path = path;
}
public MorphtargetsModifier getEyeModifier() {
	return eyeModifier;
}
public void setEyeModifier(MorphtargetsModifier eyeModier) {
	this.eyeModifier = eyeModier;
}
}
