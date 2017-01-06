package com.akjava.mbl3d.timetable;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import com.akjava.mbl3d.expression.client.timetable.AnimationKeyFrameBuilder;
import com.akjava.mbl3d.expression.client.timetable.AnimationKeyGroup;
import com.akjava.mbl3d.expression.client.timetable.TimeTableData;
import com.akjava.mbl3d.expression.client.timetable.TimeTableDataBlock;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

public class AnimationFrameMergeTest extends TestCase{

	public void execute(List<TimeTableDataBlock> blocks,String fileName,double totalTime){
		AnimationKeyFrameBuilder builder=new AnimationKeyFrameBuilder(new DummyMbl3dDataHolder());
		builder.setTotalTime(totalTime);
		
		List<AnimationKeyGroup> groups=Lists.newArrayList();
		for(TimeTableDataBlock block:blocks){
			AnimationKeyGroup group=builder.createGroup(block);
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
		
		
		
		String correctText;
		try {
			correctText = Resources.toString(Resources.getResource("com/akjava/mbl3d/timetable/resources/"+fileName), Charsets.UTF_8);
			assertEquals(correctText, groups.get(0).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void test_simple0(){
		//block1
		TimeTableData block1a=new TimeTableData();
		block1a.setTime(0);
		block1a.setReferenceId(4);
		TimeTableData block1b=new TimeTableData();
		block1b.setTime(1000);
		block1b.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(block1a,block1b);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(false);
		
		
		List<TimeTableDataBlock> blocks=Lists.newArrayList(block);
		execute(blocks,"simple0.txt",0);
	}
	
	/*
	 * no conflict
	 */
	public void test_simple1(){
		//block1
		TimeTableData block1a=new TimeTableData();
		block1a.setTime(0);
		block1a.setReferenceId(4);
		TimeTableData block1b=new TimeTableData();
		block1b.setTime(1000);
		block1b.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(block1a,block1b);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(false);
		
		TimeTableData block2a=new TimeTableData();
		block2a.setTime(0);
		block2a.setReferenceId(5);
		TimeTableData block2b=new TimeTableData();
		block2b.setTime(1000);
		block2b.setReferenceId(6);
		List<TimeTableData> datas2=Lists.newArrayList(block2a,block2b);
		TimeTableDataBlock block2=new TimeTableDataBlock(datas2);
		block2.setLoop(false);
		
		List<TimeTableDataBlock> blocks=Lists.newArrayList(block,block2);
		execute(blocks,"simple1.txt",0);
	}
	/*
	 * overwrite
	 */
	public void test_simple2(){
		//block1
		TimeTableData block1a=new TimeTableData();
		block1a.setTime(0);
		block1a.setReferenceId(4);
		TimeTableData block1b=new TimeTableData();
		block1b.setTime(1000);
		block1b.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(block1a,block1b);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(false);
		
		TimeTableData block2a=new TimeTableData();
		block2a.setTime(0);
		block2a.setReferenceId(4);
		TimeTableData block2b=new TimeTableData();
		block2b.setTime(1000);
		block2b.setReferenceId(1);
		List<TimeTableData> datas2=Lists.newArrayList(block2a,block2b);
		TimeTableDataBlock block2=new TimeTableDataBlock(datas2);
		block2.setLoop(false);
		
		List<TimeTableDataBlock> blocks=Lists.newArrayList(block,block2);
		execute(blocks,"simple2.txt",0);
	}
	
	/*
	 * overwrite
	 */
	public void test_simple3(){
		//block1
		TimeTableData block1a=new TimeTableData();
		block1a.setTime(0);
		block1a.setReferenceId(4);
		TimeTableData block1b=new TimeTableData();
		block1b.setTime(1000);
		block1b.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(block1a,block1b);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(false);
		
		TimeTableData block2a=new TimeTableData();
		block2a.setTime(0);
		block2a.setReferenceId(4);
		TimeTableData block2b=new TimeTableData();
		block2b.setTime(2000);
		block2b.setReferenceId(1);
		List<TimeTableData> datas2=Lists.newArrayList(block2a,block2b);
		TimeTableDataBlock block2=new TimeTableDataBlock(datas2);
		block2.setLoop(false);
		
		List<TimeTableDataBlock> blocks=Lists.newArrayList(block,block2);
		execute(blocks,"simple3.txt",0);
	}
	public void test_simple4(){
		//block1
		TimeTableData block1a=new TimeTableData();
		block1a.setTime(0);
		block1a.setReferenceId(4);
		TimeTableData block1b=new TimeTableData();
		block1b.setTime(2000);
		block1b.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(block1a,block1b);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(false);
		
		TimeTableData block2a=new TimeTableData();
		block2a.setTime(0);
		block2a.setReferenceId(4);
		TimeTableData block2b=new TimeTableData();
		block2b.setTime(1000);
		block2b.setReferenceId(1);
		List<TimeTableData> datas2=Lists.newArrayList(block2a,block2b);
		TimeTableDataBlock block2=new TimeTableDataBlock(datas2);
		block2.setLoop(false);
		
		List<TimeTableDataBlock> blocks=Lists.newArrayList(block,block2);
		execute(blocks,"simple4.txt",0);
	}
	
	/*
	 * insert
	 */
	public void test_insert1(){
		//block1
		TimeTableData block1a=new TimeTableData();
		block1a.setTime(0);
		block1a.setReferenceId(4);
		TimeTableData block1b=new TimeTableData();
		block1b.setTime(1000);
		block1b.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(block1a,block1b);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(false);
		//block2
		TimeTableData block2a=new TimeTableData();
		block2a.setTime(250);
		block2a.setReferenceId(7);
		TimeTableData block2b=new TimeTableData();
		block2b.setTime(500);
		block2b.setReferenceId(8);
		List<TimeTableData> datas2=Lists.newArrayList(block2a,block2b);
		TimeTableDataBlock block2=new TimeTableDataBlock(datas2);
		block2.setLoop(false);
		
		List<TimeTableDataBlock> blocks=Lists.newArrayList(block,block2);
		execute(blocks,"insert1.txt",0);
	}
	
	/*
	 * insert with loop
	 */
	public void test_insert2(){
		//block1
		TimeTableData block1a=new TimeTableData();
		block1a.setTime(0);
		block1a.setReferenceId(4);
		TimeTableData block1b=new TimeTableData();
		block1b.setTime(1000);
		block1b.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(block1a,block1b);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(2);
		block.setLoopInterval(1000);
		//block2
		TimeTableData block2a=new TimeTableData();
		block2a.setTime(2500);
		block2a.setReferenceId(7);
		TimeTableData block2b=new TimeTableData();
		block2b.setTime(2750);
		block2b.setReferenceId(8);
		List<TimeTableData> datas2=Lists.newArrayList(block2a,block2b);
		TimeTableDataBlock block2=new TimeTableDataBlock(datas2);
		block2.setLoop(false);
		
		List<TimeTableDataBlock> blocks=Lists.newArrayList(block,block2);
		execute(blocks,"insert2.txt",0);
	}
	
	/*
	 * insert with loop expect remove
	 */
	public void test_insert3(){
		//block1
		TimeTableData block1a=new TimeTableData();
		block1a.setTime(0);
		block1a.setReferenceId(4);
		TimeTableData block1b=new TimeTableData();
		block1b.setTime(1000);
		block1b.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(block1a,block1b);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(2);
		block.setLoopInterval(1000);
		//block2
		TimeTableData block2a=new TimeTableData();
		block2a.setTime(1500);
		block2a.setReferenceId(7);
		TimeTableData block2b=new TimeTableData();
		block2b.setTime(2750);
		block2b.setReferenceId(8);
		List<TimeTableData> datas2=Lists.newArrayList(block2a,block2b);
		TimeTableDataBlock block2=new TimeTableDataBlock(datas2);
		block2.setLoop(false);
		
		List<TimeTableDataBlock> blocks=Lists.newArrayList(block,block2);
		execute(blocks,"insert3.txt",0);
	}
	/*
	 * insert with loop expect remove,with other
	 */
	public void test_insert4(){
		//block1
		TimeTableData block1a=new TimeTableData();
		block1a.setTime(0);
		block1a.setReferenceId(0);
		TimeTableData block1b=new TimeTableData();
		block1b.setTime(1000);
		block1b.setReferenceId(3);
		List<TimeTableData> datas=Lists.newArrayList(block1a,block1b);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(2);
		block.setLoopInterval(1000);
		//block2
		TimeTableData block2a=new TimeTableData();
		block2a.setTime(1500);
		block2a.setReferenceId(7);
		TimeTableData block2b=new TimeTableData();
		block2b.setTime(2750);
		block2b.setReferenceId(8);
		List<TimeTableData> datas2=Lists.newArrayList(block2a,block2b);
		TimeTableDataBlock block2=new TimeTableDataBlock(datas2);
		block2.setLoop(false);
		
		List<TimeTableDataBlock> blocks=Lists.newArrayList(block,block2);
		execute(blocks,"insert4.txt",0);
	}
	/*
	 * insert with loop expect remove,with other2
	 */
	public void test_insert5(){
		//block1
		TimeTableData block1a=new TimeTableData();
		block1a.setTime(0);
		block1a.setReferenceId(9);
		TimeTableData block1b=new TimeTableData();
		block1b.setTime(1000);
		block1b.setReferenceId(10);
		List<TimeTableData> datas=Lists.newArrayList(block1a,block1b);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(2);
		block.setLoopInterval(1000);
		//block2
		TimeTableData block2a=new TimeTableData();
		block2a.setTime(1500);
		block2a.setReferenceId(7);
		TimeTableData block2b=new TimeTableData();
		block2b.setTime(2750);
		block2b.setReferenceId(8);
		List<TimeTableData> datas2=Lists.newArrayList(block2a,block2b);
		TimeTableDataBlock block2=new TimeTableDataBlock(datas2);
		block2.setLoop(false);
		
		List<TimeTableDataBlock> blocks=Lists.newArrayList(block,block2);
		execute(blocks,"insert5.txt",0);
	}
	
	
	/*
	 * insert with loop multiple
	 */
	public void test_insert6(){
		//block1
		TimeTableData block1a=new TimeTableData();
		block1a.setTime(0);
		block1a.setReferenceId(4);
		TimeTableData block1b=new TimeTableData();
		block1b.setTime(1000);
		block1b.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(block1a,block1b);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(3);
		block.setLoopInterval(1000);
		
		//block2
		TimeTableData block2a=new TimeTableData();
		block2a.setTime(1500);
		block2a.setReferenceId(7);
		TimeTableData block2b=new TimeTableData();
		block2b.setTime(2750);
		block2b.setReferenceId(8);
		List<TimeTableData> datas2=Lists.newArrayList(block2a,block2b);
		TimeTableDataBlock block2=new TimeTableDataBlock(datas2);
		block2.setLoop(false);
		
		//block3
				TimeTableData block3a=new TimeTableData();
				block3a.setTime(3500);
				block3a.setReferenceId(7);
				TimeTableData block3b=new TimeTableData();
				block3b.setTime(4750);
				block3b.setReferenceId(8);
				List<TimeTableData> datas3=Lists.newArrayList(block3a,block3b);
				TimeTableDataBlock block3=new TimeTableDataBlock(datas3);
				block3.setLoop(false);
				
		List<TimeTableDataBlock> blocks=Lists.newArrayList(block,block2,block3);
		execute(blocks,"insert6.txt",0);
	}
	
	/*
	 * insert with loop multiple
	 */
	public void test_insert7(){
		//block1
		TimeTableData block1a=new TimeTableData();
		block1a.setTime(0);
		block1a.setReferenceId(4);
		TimeTableData block1b=new TimeTableData();
		block1b.setTime(1000);
		block1b.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(block1a,block1b);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(3);
		block.setLoopInterval(1000);
		
		//block2
		TimeTableData block2a=new TimeTableData();
		block2a.setTime(1500);
		block2a.setReferenceId(7);
		TimeTableData block2b=new TimeTableData();
		block2b.setTime(2750);
		block2b.setReferenceId(8);
		List<TimeTableData> datas2=Lists.newArrayList(block2a,block2b);
		TimeTableDataBlock block2=new TimeTableDataBlock(datas2);
		block2.setLoop(false);
		
		//block3
				TimeTableData block3a=new TimeTableData();
				block3a.setTime(2500);
				block3a.setReferenceId(7);
				TimeTableData block3b=new TimeTableData();
				block3b.setTime(4750);
				block3b.setReferenceId(8);
				List<TimeTableData> datas3=Lists.newArrayList(block3a,block3b);
				TimeTableDataBlock block3=new TimeTableDataBlock(datas3);
				block3.setLoop(false);
				
		List<TimeTableDataBlock> blocks=Lists.newArrayList(block,block2,block3);
		execute(blocks,"insert7.txt",0);
	}
}
