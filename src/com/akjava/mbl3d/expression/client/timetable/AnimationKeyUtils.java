package com.akjava.mbl3d.expression.client.timetable;

import java.util.List;

import com.akjava.gwt.three.client.java.file.MorphTargetKeyFrame;
import com.akjava.gwt.three.client.java.file.MorphtargetsModifier;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.tracks.NumberKeyframeTrack;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;

public class AnimationKeyUtils {
private AnimationKeyUtils(){}

/*
 * must be same key
 */
public static NumberKeyframeTrack toTrack(String keyName,int index,List<Mbl3dAnimationKeyFrame> frames,MorphtargetsModifier modifier){
	String trackName=".morphTargetInfluences["+index+"]";
	
	
	JsArrayNumber times=JavaScriptObject.createArray().cast();
	JsArrayNumber values=JavaScriptObject.createArray().cast();
	for(Mbl3dAnimationKeyFrame frame:frames){
		times.push(frame.getTime()/1000);//millisecond to second
		values.push(toModifyValue(keyName,frame.getValue(),modifier));
	}
	
	NumberKeyframeTrack track=THREE.NumberKeyframeTrack(trackName, times, values);
	return track;
}



//TODO BasicExpressionPanel to better location
private static double toModifyValue(String key,double value,MorphtargetsModifier modifier) {
	return modifier.getModifiedValue(key, value);
}



}
