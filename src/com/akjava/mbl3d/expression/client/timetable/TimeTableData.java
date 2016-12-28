package com.akjava.mbl3d.expression.client.timetable;

public class TimeTableData {
private String label;
public String getLabel() {
	return label;
}
public void setLabel(String label) {
	this.label = label;
}
private boolean reference;
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

/*
 * millisecond
 */
public double getTime() {
	return time;
}
public void setTime(double time) {
	this.time = time;
}
}
