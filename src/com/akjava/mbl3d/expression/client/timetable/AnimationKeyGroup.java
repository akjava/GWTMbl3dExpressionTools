package com.akjava.mbl3d.expression.client.timetable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.JSParameter;
import com.akjava.gwt.three.client.java.file.MorphTargetKeyFrame;
import com.akjava.gwt.three.client.java.file.MorphtargetsModifier;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.animation.KeyframeTrack;
import com.akjava.gwt.three.client.js.animation.tracks.NumberKeyframeTrack;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/*
 * the data after solving loops
 */
public class AnimationKeyGroup {
private List<Mbl3dAnimationKeyFrame> frames;
private boolean noClear;
public boolean isNoClear() {
	return noClear;
}

public void setNoClear(boolean noClear) {
	this.noClear = noClear;
}

public AnimationKeyGroup(List<Mbl3dAnimationKeyFrame> frames) {
	super();
	this.frames = frames;
}

public List<Mbl3dAnimationKeyFrame> getFrames() {
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
	for(Mbl3dAnimationKeyFrame frame:frames){
		if(frame.getType()==Mbl3dAnimationKeyFrame.TYPE_EYEBROW){
			return true;
		}
	}
	return false;
}
public boolean haveEye(){
	for(Mbl3dAnimationKeyFrame frame:frames){
		if(frame.getType()==Mbl3dAnimationKeyFrame.TYPE_EYE){
			return true;
		}
	}
	return false;
}
public boolean haveMouth(){
	for(Mbl3dAnimationKeyFrame frame:frames){
		if(frame.getType()==Mbl3dAnimationKeyFrame.TYPE_MOUTH){
			return true;
		}
	}
	return false;
}

public List<String> getKeys(){
	List<String> keys=Lists.newArrayList();
	for(Mbl3dAnimationKeyFrame frame:frames){
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
	
	
	List<Mbl3dAnimationKeyFrame> newFrames=Lists.newArrayList();
	if(start==end){
		//TODO
		throw new RuntimeException("cut:same time,not support yet.start="+start+",end="+end);
	}else{
		//no clear only remove same key in time
		if(group.isNoClear()){
			List<String> keys=group.getKeys();
			
			for(Mbl3dAnimationKeyFrame frame:frames){
				double time=frame.getTime();
				if(time>start&&time<end){
					//skip
					if(!keys.contains(frame.getKeyName())){
						//LogUtils.log("not contain:"+frame.getKeyName());
						newFrames.add(frame);
					}else{
						//LogUtils.log("key removed:"+frame.getKeyName());
					}
				}else{
					//LogUtils.log("out of time:"+frame.getKeyName());
					newFrames.add(frame);
				}
			}
			
			frames=newFrames;//replace
			Collections.sort(frames, new AnimationKeyFrameComparator());
			return;
		}
		
		
		
		
		
		for(Mbl3dAnimationKeyFrame frame:frames){
			double time=frame.getTime();
			if(time>start&&time<end){
				boolean inserted=false;
				//possible ignore
				if(frame.getType()==Mbl3dAnimationKeyFrame.TYPE_EYEBROW){
					if(!group.haveEyeBrow()){
						newFrames.add(frame);
						inserted=true;
					}
				}else if(frame.getType()==Mbl3dAnimationKeyFrame.TYPE_EYE){
					if(!group.haveEye()){
						newFrames.add(frame);
						inserted=true;
					}
				}else if(frame.getType()==Mbl3dAnimationKeyFrame.TYPE_MOUTH){
					if(!group.haveMouth()){
						newFrames.add(frame);
						inserted=true;
					}
				}
				
				if(!inserted){
					//debug
					//System.out.println("start="+start+",end="+end+",time="+time);
				}
				
				
			}else{
				newFrames.add(frame);
			}
		}
		
		//insert reset key at start and end
		for(Mbl3dAnimationKeyFrame frame:frames){
			double time=frame.getTime();
			if(time>start&&time<end){
			boolean needReset=false;
			if(frame.getType()==Mbl3dAnimationKeyFrame.TYPE_EYEBROW){
				if(group.haveEyeBrow()){
					needReset=true;
				}
			}else if(frame.getType()==Mbl3dAnimationKeyFrame.TYPE_EYE){
				if(group.haveEye()){
					needReset=true;
				}
			}else if(frame.getType()==Mbl3dAnimationKeyFrame.TYPE_MOUTH){
				if(group.haveMouth()){
					needReset=true;
				}
			}
			
			if(frame.getKeyName().equals("eyes2")){
				//System.out.println(frame.getType()+",have-eye="+group.haveEye()+",needreset="+needReset);
			}
			
			if(needReset){
				String key=frame.getKeyName();
				Mbl3dAnimationKeyFrame startFrame=new Mbl3dAnimationKeyFrame(key, start, 0);
				newFrames.add(startFrame);
				
				Mbl3dAnimationKeyFrame endFrame=new Mbl3dAnimationKeyFrame(key, end, 0);
				newFrames.add(endFrame);
			}
			}	
		}
	
		
		frames=newFrames;//replace
		Collections.sort(frames, new AnimationKeyFrameComparator());
	}
}
private String toNameAndTime(Mbl3dAnimationKeyFrame frame){
	return frame.getKeyName()+":"+frame.getTime();
}
/**
 * should cut before merge
 * @param group
 */
public void merge(AnimationKeyGroup group){
	Map<String,Mbl3dAnimationKeyFrame> newFrameMap=Maps.newLinkedHashMap();
	for(Mbl3dAnimationKeyFrame frame:frames){
		newFrameMap.put(toNameAndTime(frame), frame);
	}
	//remove same timing data
	for(Mbl3dAnimationKeyFrame frame:group.getFrames()){
		//remove same time
		String key=toNameAndTime(frame);
		newFrameMap.remove(key);
	}
	
	List<Mbl3dAnimationKeyFrame> newFrame=Lists.newArrayList(newFrameMap.values());
	for(Mbl3dAnimationKeyFrame frame:group.getFrames()){
		newFrame.add(frame);
	}
	
	frames=newFrame;
	
	
	Collections.sort(frames, new AnimationKeyFrameComparator());
}


public String toString(){
	return Joiner.on("\r\n").join(frames);
}


/**
 * 
 * @param animationClipName
 * @param modifier,modify value,usually mbl3dmodel's eyes need modify
 * @param morphTargetDictionary  find morphtarget index #Mesh.getMorphTargetDictionary()
 * @return
 */
public AnimationClip converToAnimationClip(String animationClipName,MorphtargetsModifier modifier,JSParameter morphTargetDictionary){
	
	Map<String,List<Mbl3dAnimationKeyFrame>> frameListMap=toListMap();
	
	JsArray<KeyframeTrack> tracks=JavaScriptObject.createArray().cast();
	for(String key:frameListMap.keySet()){
		List<Mbl3dAnimationKeyFrame> list=frameListMap.get(key);
		if(!morphTargetDictionary.exists(key)){
			LogUtils.log("converToAnimationClip:call not exist key="+key+".skipped creating track");
			continue;
		}
		int index=morphTargetDictionary.getInt(key);//possible null?
		NumberKeyframeTrack track=AnimationKeyUtils.toTrack(key,index,list,modifier);
		tracks.push(track);
	}
	
	AnimationClip clip=THREE.AnimationClip(animationClipName, -1, tracks);
	return clip;
}

private Map<String,List<Mbl3dAnimationKeyFrame>> toListMap(){
	//sort
		Collections.sort(frames, new AnimationKeyFrameComparator());
		//split by type
		Map<String,List<Mbl3dAnimationKeyFrame>> frameListMap=Maps.newLinkedHashMap();
		
		for(Mbl3dAnimationKeyFrame frame:frames){
			String key=frame.getKeyName();
			List<Mbl3dAnimationKeyFrame> list=frameListMap.get(key);
			if(list==null){
				list=Lists.newArrayList();
				frameListMap.put(key, list);
			}
			list.add(frame);
		}
		return frameListMap;
}
/**
 * see  #MorphTargetKeyFrameConverter,#JSONMorphTargetsFileConverter
 * 
 * @return
 */
public List<List<MorphTargetKeyFrame>> converToMorphTargetKeyFrame(){
	
	Map<String,List<Mbl3dAnimationKeyFrame>> frameListMap=toListMap();

	List<List<MorphTargetKeyFrame>> values=Lists.newArrayList();
	for(String key:frameListMap.keySet()){
		List<Mbl3dAnimationKeyFrame> list=frameListMap.get(key);
		List<MorphTargetKeyFrame> newList=Lists.newArrayList();
		for(MorphTargetKeyFrame frame:list){
			newList.add(frame);
		}
		values.add(newList);
	}
	
	return values;
}

}
