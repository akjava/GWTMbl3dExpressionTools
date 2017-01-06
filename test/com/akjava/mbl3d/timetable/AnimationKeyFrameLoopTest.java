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

public class AnimationKeyFrameLoopTest extends TestCase {

	
	public void execute(TimeTableDataBlock block,String fileName,double totalTime){
		AnimationKeyFrameBuilder builder=new AnimationKeyFrameBuilder(new DummyMbl3dDataHolder());
		builder.setTotalTime(totalTime);
		AnimationKeyGroup group=builder.createGroup(block);
		String correctText;
		try {
			
			correctText = Resources.toString(Resources.getResource("com/akjava/mbl3d/timetable/resources/"+fileName), Charsets.UTF_8);
			assertEquals(correctText, group.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * no loop
	 */
	public void test_loop1(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		data1.setReferenceId(1);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		data2.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(false);
		
		
		execute(block,"loop1.txt",0);
	}
	/**
	 * one loop
	 */
	public void test_loop1b(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		data1.setReferenceId(1);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		data2.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(1);
		
		execute(block,"loop1b.txt",0);
	}
	/**
	 *  2 times loop ,value is same
	 */
	public void test_loop2(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		data1.setReferenceId(1);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		data2.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(2);
		block.setLoopInterval(1000);
		
		
		execute(block,"loop2.txt",0);
	}
	
	/**
	 * unlimited time base ,exactlly time
	 */
	public void test_loop3(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		data1.setReferenceId(1);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		data2.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(0);
		block.setLoopInterval(1000);
		
		
		execute(block,"loop3.txt",3000);
	}
	
	/*
	 * unlimited time base loop,but need todo
	 */
	public void test_loop4(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		data1.setReferenceId(1);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		data2.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(0);
		block.setLoopInterval(1000);
		
		//simplly just one more loop
		execute(block,"loop4todo.txt",4000);
	}
	/**
	 * 2 times loop
	 */
	public void test_loop5(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		data1.setReferenceId(4);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		data2.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(2);
		block.setLoopInterval(1000);
		
		//simplly just one more loop
		execute(block,"loop5.txt",0);
	}
	/*
	 * 2 times loop but value inverse
	 */
	public void test_loop6(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		data1.setReferenceId(1);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		data2.setReferenceId(4);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(2);
		block.setLoopInterval(1000);
		
		//simplly just one more loop
		execute(block,"loop6.txt",0);
	}
	
	/*
	 * 3 times loop
	 */
	public void test_loop7(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		data1.setReferenceId(4);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		data2.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setLoop(true);
		block.setLoopTime(3);
		block.setLoopInterval(1000);
		
		execute(block,"loop7.txt",0);
	}
	
	/*
	 * 3 times loop with startAt & beforeMargin
	 */
	public void test_loop8(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		data1.setReferenceId(4);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		data2.setReferenceId(1);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		block.setStartAt(9000);
		block.setBeforeMargin(1000);
		block.setLoop(true);
		block.setLoopTime(3);
		block.setLoopInterval(1000);
		
		execute(block,"loop8.txt",0);
	}
}
