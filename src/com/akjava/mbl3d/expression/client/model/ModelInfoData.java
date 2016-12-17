package com.akjava.mbl3d.expression.client.model;

public class ModelInfoData {
private String path;
public ModelInfoData(String path, double eyeModier) {
	super();
	this.path = path;
	this.eyeModifier = eyeModier;
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
private double eyeModifier;

public String getPath() {
	return path;
}
public void setPath(String path) {
	this.path = path;
}
public double getEyeModifier() {
	return eyeModifier;
}
public void setEyeModifier(double eyeModier) {
	this.eyeModifier = eyeModier;
}
}
