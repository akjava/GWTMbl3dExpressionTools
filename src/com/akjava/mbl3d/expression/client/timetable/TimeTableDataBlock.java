package com.akjava.mbl3d.expression.client.timetable;

import java.util.List;

public class TimeTableDataBlock {
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
private double loopInterval;

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

public TimeTableDataBlock(List<TimeTableData> timeTableDatas) {
	super();
	this.timeTableDatas = timeTableDatas;
}
public double getLastTime(){
	return timeTableDatas.get(timeTableDatas.size()-1).getTime();
}
public double calcurateTotalTime(){
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
