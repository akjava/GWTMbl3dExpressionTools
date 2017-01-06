package com.akjava.mbl3d.expression.client.timetable;

import java.util.List;
import java.util.Map;

import com.akjava.lib.common.utils.ValuesUtils;
import com.akjava.mbl3d.expression.client.Mbl3dDataHolder;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
	public  AnimationKeyGroup createGroup(TimeTableDataBlock block){
		List<AnimationKeyFrame> frames=Lists.newArrayList();
		
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
		timeAt+=block.getBeforeMargin();
		for(int i=0;i<loopTime;i++){
		for(TimeTableData timeTableData:block.getTimeTableDatas()){
			double time=timeAt+timeTableData.getTime();
			Mbl3dData data=mbl3dDataHolder.getDataById(timeTableData.getReferenceId());
			if(data==null){
				//LogUtils.log("somehow invalid data:"+timeTableData.getReferenceId());
				data=clerData;//-1 means empty
			}
			for(String key:data.getValues().keySet()){
				double value=ValuesUtils.toDouble(data.getValues().get(key), 0);
				AnimationKeyFrame frame=new AnimationKeyFrame(key, time, value);
				frames.add(frame);
			}
		}
		timeAt+=block.getLastTime();
		if(i!=loopTime-1){
			timeAt+=+block.getLoopInterval();
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
	private Mbl3dData createClearData( List<TimeTableData> datas){
		Map<String,String> values=Maps.newHashMap();
		for(TimeTableData tdata:datas){
			Mbl3dData data=mbl3dDataHolder.getDataById(tdata.getReferenceId());
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
