package com.akjava.mbl3d.expression.client.timetable;

import java.util.ArrayList;
import java.util.List;

public class TimeTableDataBlock {
private String name;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
private double beforeMargin;
public double getBeforeMargin() {
	return beforeMargin;
}
public void setBeforeMargin(double beforeMargin) {
	this.beforeMargin = beforeMargin;
}
public double getAfterMargin() {
	return afterMargin;
}
public void setAfterMargin(double afterMargin) {
	this.afterMargin = afterMargin;
}
public double getLoopInterval() {
	return loopInterval;
}
public void setLoopInterval(double loopInterval) {
	this.loopInterval = loopInterval;
}
private double afterMargin;

private boolean loop;
/*
 * very important ,if use loop ,must greater than 0
 */
private double loopInterval=1;

public boolean isLoop() {
	return loop;
}
public void setLoop(boolean loop) {
	this.loop = loop;
}
public int getLoopTime() {
	return loopTime;
}
public void setLoopTime(int loopTime) {
	this.loopTime = loopTime;
}
public List<TimeTableData> getTimeTableDatas() {
	return timeTableDatas;
}
public void setTimeTableDatas(List<TimeTableData> timeTableDatas) {
	this.timeTableDatas = timeTableDatas;
}
private int playTime;//for stretch TODO

private double startAt;
public double getStartAt() {
	return startAt;
}
public void setStartAt(double startAt) {
	this.startAt = startAt;
}
/**
 * 0 means unlimited
 */
private int loopTime;
private List<TimeTableData> timeTableDatas;

public TimeTableDataBlock(){
	this(new ArrayList<TimeTableData>());
}
public TimeTableDataBlock(List<TimeTableData> timeTableDatas) {
	super();
	this.timeTableDatas = timeTableDatas;
}
public double getLastTime(){
	if(timeTableDatas.isEmpty()){
		return 0;
	}
	return timeTableDatas.get(timeTableDatas.size()-1).getTime();
}
/**
 * technically this is end time
 * if need length,minus start-time
 * @return
 */
public double calcurateEndTime(){
	double lastTime=getLastTime();
	
	double totalTime=startAt;
	int repeatTime=loop?loopTime:1;
	
	for(int i=0;i<repeatTime;i++){
		totalTime+=i==0?lastTime:lastTime+loopInterval;
	}
	
	totalTime+=beforeMargin+afterMargin;
	
	return totalTime;
}
}
