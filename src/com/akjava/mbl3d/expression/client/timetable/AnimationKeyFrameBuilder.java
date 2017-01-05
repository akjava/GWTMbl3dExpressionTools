package com.akjava.mbl3d.expression.client.timetable;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.akjava.mbl3d.expression.client.Mbl3dDataHolder;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.google.common.collect.Lists;

//TODO many test
public class AnimationKeyFrameBuilder {
	private Mbl3dDataHolder mbl3dDataHolder;
	public AnimationKeyFrameBuilder(Mbl3dDataHolder mbl3dDataHolder) {
		super();
		this.mbl3dDataHolder = mbl3dDataHolder;
	}
	private double totalTime;//most length time;
	public  AnimationKeyGroup createGroup(TimeTableDataBlock block){
		List<AnimationKeyFrame> frames=Lists.newArrayList();
		
		//Mbl3dData mbl3dData=id==-1?new Mbl3dData():Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel().getDataById(id);
		
		//resolve loop time
		int loopTime=block.isLoop()?block.getLoopTime():1;
		
		
		double remainTime=0;
		
		if(loopTime==0){
			double time=totalTime-block.getStartAt()-block.getBeforeMargin();//ignore after-margin
			double lastTime=block.getLastTime();
			
			if(time>lastTime){
				time-=lastTime;
				int tmp=(int)(time/(lastTime+block.getLoopInterval()));
				loopTime=1+tmp;
				remainTime=time%(lastTime+block.getLoopInterval());
			}else{
				remainTime=time;
			}
		}
		
		//solving loop action
		double timeAt=block.getStartAt();
		timeAt+=block.getBeforeMargin();
		for(int i=0;i<loopTime;i++){
		for(TimeTableData timeTableData:block.getTimeTableDatas()){
			double time=timeAt+timeTableData.getTime();
			Mbl3dData data=mbl3dDataHolder.getDataById(timeTableData.getReferenceId());
			if(data==null){
				LogUtils.log("somehow invalid data:"+timeTableData.getReferenceId());
				continue;
			}
			for(String key:data.getValues().keySet()){
				double value=ValuesUtils.toDouble(data.getValues().get(key), 0);
				AnimationKeyFrame frame=new AnimationKeyFrame(key, time, value);
				frames.add(frame);
			}
		}
		timeAt+=block.getLastTime()+block.getLoopInterval();
		}
		
		//modify remainTime
		if(remainTime>0){
			double lastTime=block.getLastTime();
			double ratio=remainTime/lastTime;
			for(TimeTableData timeTableData:block.getTimeTableDatas()){
				double time=timeAt+timeTableData.getTime();
				Mbl3dData data=mbl3dDataHolder.getDataById(timeTableData.getReferenceId());
				if(data==null){
					LogUtils.log("somehow invalid data:"+timeTableData.getReferenceId());
					continue;
				}
				for(String key:data.getValues().keySet()){
					double value=ValuesUtils.toDouble(data.getValues().get(key), 0);
					AnimationKeyFrame frame=new AnimationKeyFrame(key, time, value*ratio);
					frames.add(frame);
				}
			}
		}
		
		AnimationKeyGroup group=new AnimationKeyGroup(frames);
		return group;
	}
}
