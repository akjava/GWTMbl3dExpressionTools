package com.akjava.mbl3d.expression.client;

import java.util.List;

import com.google.common.collect.Lists;

public class TextureMontageData {
private int opacity=100;	
public int getOpacity() {
	return opacity;
}

public void setOpacity(int opacity) {
	this.opacity = opacity;
}

public static final int TYPE_LIST=0;
public static final int TYPE_COLOR=1;
private String keyName;
public String getKeyName() {
	return keyName;
}

public void setKeyName(String keyName) {
	this.keyName = keyName;
}

public List<String> getValues() {
	return values;
}

public void setValues(List<String> values) {
	this.values = values;
}

public String getValue() {
	return value;
}

public void setValue(String value) {
	this.value = value;
}

public int getType() {
	return type;
}

public void setType(int type) {
	this.type = type;
}

private List<String> values=Lists.newArrayList();
private String value;
private boolean enabled;
public boolean isEnabled() {
	return enabled;
}

public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}

private int type;

public String generateKey(){
	if(!enabled){
		return "";
	}
	return keyName+":"+value+":"+opacity;
}
}
