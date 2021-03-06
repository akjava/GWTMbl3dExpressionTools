package com.akjava.mbl3d.expression.client.timetable;

public class TimeTableData {
private String label;
public String getLabel() {
	return label;
}
public void setLabel(String label) {
	this.label = label;
}
private boolean reference=true;//default is true
public boolean isReference() {
	return reference;
}
public void setReference(boolean reference) {
	this.reference = reference;
}
private int referenceId=-1;
public int getReferenceId() {
	return referenceId;
}
public void setReferenceId(int referenceId) {
	this.referenceId = referenceId;
}
private double time;
//TODO have direct value

private boolean enableMouth=true;
public boolean isEnableMouth() {
	return enableMouth;
}
public void setEnableMouth(boolean enableMouth) {
	this.enableMouth = enableMouth;
}
public boolean isEnableEyes() {
	return enableEyes;
}
public void setEnableEyes(boolean enableEyes) {
	this.enableEyes = enableEyes;
}
public boolean isEnableBrows() {
	return enableBrows;
}
public void setEnableBrows(boolean enableBrows) {
	this.enableBrows = enableBrows;
}
private boolean enableEyes=true;
private boolean enableBrows=true;

private double ratio=1;
public double getRatio() {
	return ratio;
}
public void setRatio(double ratio) {
	this.ratio = ratio;
}
/*
 * millisecond
 */
public double getTime() {
	return time;
}
public void setTime(double time) {
	this.time = time;
}

private double waitTime;
public double getWaitTime() {
	return waitTime;
}
public void setWaitTime(double waitTime) {
	this.waitTime = waitTime;
}
public double calcurateEndTime(){
	return time+waitTime;
}
}
