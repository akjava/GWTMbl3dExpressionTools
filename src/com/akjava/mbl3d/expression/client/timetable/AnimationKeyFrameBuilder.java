package com.akjava.mbl3d.expression.client.timetable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.akjava.lib.common.utils.ValuesUtils;
import com.akjava.mbl3d.expression.client.Mbl3dDataHolder;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

//TODO many test
public class AnimationKeyFrameBuilder {
	private Mbl3dDataHolder mbl3dDataHolder;
	public AnimationKeyFrameBuilder(Mbl3dDataHolder mbl3dDataHolder) {
		super();
		this.mbl3dDataHolder = mbl3dDataHolder;
	}
	private double totalTime;//most length time;
	public double getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}
	
	private Set<String> keys=Sets.newHashSet();
	
	//can set from morphtargets
	public  void setKeys(Iterable<TimeTableDataBlock> blocks){
		keys.clear();
		for(TimeTableDataBlock block:blocks){
			for(TimeTableData data:block.getTimeTableDatas()){
				Mbl3dData mbl3d=mbl3dDataHolder.getDataById(data.getReferenceId(),data.isEnableBrows(),data.isEnableEyes(),data.isEnableMouth());
				if(mbl3d!=null){
					for(String key:mbl3d.getValues().keySet()){
						keys.add(key);
					}
				}
			}
		}
	}
	public  AnimationKeyGroup createGroup(TimeTableDataBlock block){
		List<Mbl3dAnimationKeyFrame> frames=Lists.newArrayList();
		
		//Mbl3dData mbl3dData=id==-1?new Mbl3dData():Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel().getDataById(id);
		
		//resolve loop time
		int loopTime=block.isLoop()?block.getLoopTime():1;
		
		
		double remainTime=0;
		
		if(loopTime==0){
			double time=totalTime-block.getStartAt()-block.getBeforeMargin();//ignore after-margin
			double lastTime=block.getLastTime();
			
			//System.out.println("time="+time+",ltime="+lastTime+",l+interval="+(lastTime+block.getLoopInterval()));
			
			if(time>lastTime){
				time-=lastTime;
				int tmp=(int)(time/(lastTime+block.getLoopInterval()));
				loopTime=1+tmp;
				remainTime=time%(lastTime+block.getLoopInterval());
			}else{
				remainTime=time;
			}
			
			
			//handling remainTime hard than expected just one more, it so far
			if(remainTime!=0){
				loopTime++;
			}
		}
		
		
		Mbl3dData clerData=createClearData(block.getTimeTableDatas());
		
		//solving loop action
		double timeAt=block.getStartAt();
		
		if(block.getBeforeMargin()!=0){
			double t=0;
			for(String key:keys){
				frames.add(new Mbl3dAnimationKeyFrame(key, t, 0));
			}
		}
		
		timeAt+=block.getBeforeMargin();
		for(int i=0;i<loopTime;i++){
		for(TimeTableData timeTableData:block.getTimeTableDatas()){
			double time=timeAt+timeTableData.getTime();
			Mbl3dData data=mbl3dDataHolder.getDataById(timeTableData.getReferenceId(),timeTableData.isEnableBrows(),timeTableData.isEnableEyes(),timeTableData.isEnableMouth());
			if(data==null){
				//LogUtils.log("somehow invalid data:"+timeTableData.getReferenceId());
				data=clerData;//-1 means empty
			}
			List<String> remains=Lists.newArrayList(keys);
			for(String key:data.getValues().keySet()){
				double value=ValuesUtils.toDouble(data.getValues().get(key), 0);
				Mbl3dAnimationKeyFrame frame=new Mbl3dAnimationKeyFrame(key, time, value);
				frames.add(frame);
				remains.remove(key);
			}
			
			//for reset
			for(String key:remains){
				frames.add(new Mbl3dAnimationKeyFrame(key, time, 0));
			}
		}
		timeAt+=block.getLastTime();
		if(i!=loopTime-1){
			timeAt+=+block.getLoopInterval();
			}
		}
		
		//after margin action
		if(block.getAfterMargin()!=0){
			timeAt+=block.getAfterMargin();
			for(String key:keys){
				frames.add(new Mbl3dAnimationKeyFrame(key, timeAt, 0));
			}
		}
		
	//modify remainTime
	/*	if(remainTime>0){
			if(remainTime<=block.getLoopInterval()){
				//handle interval
				double ratio=remainTime/block.getLoopInterval();
				
			}else{
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
			
			
		}*/
		
		AnimationKeyGroup group=new AnimationKeyGroup(frames);
		return group;
	}
	
	/**
	 * used for endless loop
	 * @param blocks
	 */
	public void setTotalTimeByBlocks(Iterable<TimeTableDataBlock> blocks){
		double max=0;
		for(TimeTableDataBlock block:blocks){
			double time=block.calcurateEndTime();
			if(time>max){
				max=time;
			}
		}
		setTotalTime(max);
	}
	public  AnimationKeyGroup createMergedGroup(Iterable<TimeTableDataBlock> blocks){
		setKeys(blocks);
		
		
		List<AnimationKeyGroup> groups=Lists.newArrayList();
		for(TimeTableDataBlock block:blocks){
			AnimationKeyGroup group=createGroup(block);
			//System.out.println("[group[\n"+group+"\n");
			//System.out.println("group:start="+group.getStartTime()+",end="+group.getEndTime());
			groups.add(group);
		}
		
		for(int i=groups.size()-1;i>0;i--){
			AnimationKeyGroup last=groups.get(i);
			for(int j=i-1;j>=0;j--){
			AnimationKeyGroup prev=groups.get(j);
			prev.cut(last);
			}
		}
		
		while(groups.size()>1){
			AnimationKeyGroup first=groups.get(0);
			AnimationKeyGroup next=groups.get(1);
			
			//pre.cut(last);
			first.merge(next);
			groups.remove(next);
		}
		return groups.get(0);
	}
	
	private Mbl3dData createClearData( List<TimeTableData> datas){
		Map<String,String> values=Maps.newHashMap();
		for(TimeTableData tdata:datas){
			Mbl3dData data=mbl3dDataHolder.getDataById(tdata.getReferenceId(),tdata.isEnableBrows(),tdata.isEnableEyes(),tdata.isEnableMouth());
			if(data!=null){
				for(String key:data.getValues().keySet()){
					values.put(key, "0");
				}
			}
		}
		Mbl3dData data=new Mbl3dData();
		data.setValues(values);
		return data;
	}
}
