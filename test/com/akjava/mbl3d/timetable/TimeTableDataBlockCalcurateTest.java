package com.akjava.mbl3d.timetable;

import java.util.List;

import junit.framework.TestCase;

import com.akjava.mbl3d.expression.client.timetable.TimeTableData;
import com.akjava.mbl3d.expression.client.timetable.TimeTableDataBlock;
import com.google.common.collect.Lists;

public class TimeTableDataBlockCalcurateTest extends TestCase{

	public void test1(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		
		double correct=1000;
		assertEquals( correct, block.calcurateEndTime());
	}
	
	public void testMargin1(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		
	
		block.setBeforeMargin(1000);
		
		double correct=2000;
		assertEquals( correct, block.calcurateEndTime());
	}
	public void testMargin2(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		
		block.setAfterMargin(1000);
		
		double correct=2000;
		assertEquals( correct, block.calcurateEndTime());
	}
	public void testMargin3(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		
		block.setAfterMargin(1000);
		block.setBeforeMargin(1000);
		
		double correct=3000;
		assertEquals( correct, block.calcurateEndTime());
	}
	
	public void testLoop1(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		
		block.setLoop(true);
		
		double correct=0;
		assertEquals( correct, block.calcurateEndTime());
	}
	public void testLoop2(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		
		block.setLoop(true);
		block.setLoopTime(1);
		
		double correct=1000;
		assertEquals( correct, block.calcurateEndTime());
	}
	public void testLoop3(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		
		block.setLoop(true);
		block.setLoopTime(1);
		block.setLoopInterval(500);
		
		double correct=1000;
		assertEquals( correct, block.calcurateEndTime());
	}
	public void testLoop4(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		
		block.setLoop(true);
		block.setLoopTime(2);
		
		double correct=2000;
		assertEquals( correct, block.calcurateEndTime());
	}
	public void testLoop5(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		
		block.setLoop(true);
		block.setLoopTime(2);
		block.setLoopInterval(500);
		
		double correct=2500;
		assertEquals( correct, block.calcurateEndTime());
	}
	public void testLoop6(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		
		block.setLoop(true);
		block.setLoopTime(3);
		block.setLoopInterval(500);
		block.setAfterMargin(1000);
		block.setBeforeMargin(1000);
		
		double correct=6000;
		assertEquals( correct, block.calcurateEndTime());
	}
	public void testLoop7(){
		TimeTableData data1=new TimeTableData();
		data1.setTime(0);
		TimeTableData data2=new TimeTableData();
		data2.setTime(1000);
		List<TimeTableData> datas=Lists.newArrayList(data1,data2);
		TimeTableDataBlock block=new TimeTableDataBlock(datas);
		
		block.setStartAt(2000);
		block.setLoop(true);
		block.setLoopTime(3);
		block.setLoopInterval(500);
		block.setAfterMargin(1000);
		block.setBeforeMargin(1000);
		
		double correct=8000;
		assertEquals( correct, block.calcurateEndTime());
	}
}
