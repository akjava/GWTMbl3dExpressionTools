package com.akjava.mbl3d.expression.client.timetable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/*
 * the data after solving loops
 */
public class AnimationKeyGroup {
private List<AnimationKeyFrame> frames;

public AnimationKeyGroup(List<AnimationKeyFrame> frames) {
	super();
	this.frames = frames;
}

public List<AnimationKeyFrame> getFrames() {
	return frames;
}

//start
public double getStartTime(){
	return frames.get(0).getTime();
}

//end
public double getEndTime(){
	return frames.get(frames.size()-1).getTime();
}

public boolean haveEyeBrow(){
	for(AnimationKeyFrame frame:frames){
		if(frame.getType()==AnimationKeyFrame.TYPE_EYEBROW){
			return true;
		}
	}
	return false;
}
public boolean haveEye(){
	for(AnimationKeyFrame frame:frames){
		if(frame.getType()==AnimationKeyFrame.TYPE_EYE){
			return true;
		}
	}
	return false;
}
public boolean haveMouth(){
	for(AnimationKeyFrame frame:frames){
		if(frame.getType()==AnimationKeyFrame.TYPE_MOUTH){
			return true;
		}
	}
	return false;
}

public List<String> getKeys(){
	List<String> keys=Lists.newArrayList();
	for(AnimationKeyFrame frame:frames){
		if(!keys.contains(frame.getKeyName())){
			keys.add(frame.getKeyName());
		}
	}
	return keys;
}
//cutting from after datas
public void cut(AnimationKeyGroup group){
	double start=group.getStartTime();
	double end=group.getEndTime();
	
	List<AnimationKeyFrame> newFrames=Lists.newArrayList();
	if(start==end){
		//TODO
		throw new RuntimeException("cut:same time,not support yet");
	}else{
		
		for(AnimationKeyFrame frame:frames){
			double time=frame.getTime();
			if(time>start&&time<end){
				//possible ignore
				if(frame.getType()==AnimationKeyFrame.TYPE_EYEBROW){
					if(!group.haveEyeBrow()){
						newFrames.add(frame);
					}
				}else if(frame.getType()==AnimationKeyFrame.TYPE_EYE){
					if(!group.haveEye()){
						newFrames.add(frame);
					}
				}else if(frame.getType()==AnimationKeyFrame.TYPE_MOUTH){
					if(!group.haveMouth()){
						newFrames.add(frame);
					}
				}
				
			}else{
				newFrames.add(frame);
			}
		}
		
		//insert reset key at start and end
		for(String key:getKeys()){
			AnimationKeyFrame startFrame=new AnimationKeyFrame(key, start, 0);
			newFrames.add(startFrame);
			
			AnimationKeyFrame endFrame=new AnimationKeyFrame(key, end, 0);
			newFrames.add(endFrame);
		}
		
		frames=newFrames;//replace
	}
}
private String toNameAndTime(AnimationKeyFrame frame){
	return frame.getKeyName()+":"+frame.getTime();
}
public void merge(AnimationKeyGroup group){
	Map<String,AnimationKeyFrame> newFrameMap=Maps.newLinkedHashMap();
	for(AnimationKeyFrame frame:frames){
		newFrameMap.put(toNameAndTime(frame), frame);
	}
	//remove same timing data
	for(AnimationKeyFrame frame:group.getFrames()){
		//remove same time
		String key=toNameAndTime(frame);
		newFrameMap.remove(key);
	}
	
	List<AnimationKeyFrame> newFrame=Lists.newArrayList(newFrameMap.values());
	for(AnimationKeyFrame frame:group.getFrames()){
		newFrame.add(frame);
	}
	
	frames=newFrame;
	
	//TODO sort
	Collections.sort(frames, new AnimationKeyFrameComparator());
}


public String toString(){
	return Joiner.on("\r\n").join(frames);
}


/*
 * 
 * eyeFilterValue is eyemodifier
 */
public AnimationClip converToAnimationClip(String name,double eyeFilterValue){
	//sort
	Collections.sort(frames, new AnimationKeyFrameComparator());
	//split by type
	
	//check not same value exist.
	//make track from key ,convert time,modify value
	return null;
}

}
