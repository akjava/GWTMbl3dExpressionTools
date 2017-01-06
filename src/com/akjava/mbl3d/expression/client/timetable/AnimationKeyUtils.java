package com.akjava.mbl3d.expression.client.timetable;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.tracks.NumberKeyframeTrack;
import com.akjava.mbl3d.expression.client.BasicExpressionPanel;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;

public class AnimationKeyUtils {
private AnimationKeyUtils(){}

/*
 * must be same key
 */
public static NumberKeyframeTrack toTrack(String keyName,int index,List<AnimationKeyFrame> frames,double modifyValue){
	String trackName=".morphTargetInfluences["+index+"]";
	
	
	JsArrayNumber times=JavaScriptObject.createArray().cast();
	JsArrayNumber values=JavaScriptObject.createArray().cast();
	for(AnimationKeyFrame frame:frames){
		times.push(frame.getTime()/1000);//millisecond to second
		values.push(toModifyValue(keyName,frame.getValue(),modifyValue));
	}
	
	NumberKeyframeTrack track=THREE.NumberKeyframeTrack(trackName, times, values);
	return track;
}

//TODO BasicExpressionPanel to better location
private static double toModifyValue(String key,double value,double modifyValue) {
	if(BasicExpressionPanel.isNeedEyeModifier(key)){
		return value*modifyValue;
	}else{
		return value;
	}
}



}
