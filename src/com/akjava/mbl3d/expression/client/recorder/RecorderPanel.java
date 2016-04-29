package com.akjava.mbl3d.expression.client.recorder;

import java.util.Collections;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.mbl3d.expression.client.EmotionsData;
import com.akjava.mbl3d.expression.client.Mbl3dExpression;
import com.akjava.mbl3d.expression.client.Mbl3dExpressionEntryPoint;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataComparator;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataFunctions;
import com.akjava.mbl3d.expression.client.recorder.FileSaveServletSender.PostListener;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RecorderPanel extends VerticalPanel{

	private ExecuteButton recordBt;
private FileSaveServletSender sender=new FileSaveServletSender("/write");
private ListBox addExpressionBox;
	public RecorderPanel(){
		this.add(new Label("Recorder"));
		
		HorizontalPanel buttons=new HorizontalPanel();
		this.add(buttons);
		recordBt = new ExecuteButton("Record",false) {
			@Override
			public void executeOnClick() {
				
				startRecord();
			}
		};
		
		buttons.add(recordBt);
		
		Button cancelBt=new Button("Cancel",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cancelRecord();
			}
		});
		buttons.add(cancelBt);
		
		Button testBt=new Button("test",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				Mbl3dExpressionEntryPoint.INSTANCE.fixRenderSize();
			}
		});
		buttons.add(testBt);
		
		HorizontalPanel h2=new HorizontalPanel();
		h2.add(new Label("type"));
		this.add(h2);
		typeFilterBox = new ListBox();
		typeFilterBox.addItem("");
		typeFilterBox.setSelectedIndex(0);
		h2.add(typeFilterBox);
		
		HorizontalPanel h3=new HorizontalPanel();
		h3.add(new Label("Add expression"));
		this.add(h3);
		
		addExpressionBox = new ListBox();
		addExpressionBox.addItem("");
		addExpressionBox.setSelectedIndex(0);
		h3.add(addExpressionBox);
		
		addExpressionBox.addItem("eyes04_min");
		addExpressionBox.addItem("eyes04_max");
		addExpressionBox.addItem("eyes07_min");
		addExpressionBox.addItem("eyes07_max");
		addExpressionBox.addItem("eyes08_min");
		addExpressionBox.addItem("eyes08_max");
	}
	
	public String getTypeName(){
		return typeFilterBox.getItemText(typeFilterBox.getSelectedIndex());
	}
	public String getAdditionalExpression(){
		return addExpressionBox.getItemText(addExpressionBox.getSelectedIndex());
	}

	protected void cancelRecord() {
		Mbl3dExpressionEntryPoint.INSTANCE.stopAnimation();
		recording=false;
		recordBt.setEnabled(true);
		writing=false;
	}

	protected void startRecord() {
		Mbl3dExpressionEntryPoint.INSTANCE.fixRenderSize();
		//stop first
		Mbl3dExpressionEntryPoint.INSTANCE.stopAnimation();
		//calcurate frame index
		//create animation & start
		
		LogUtils.log(getTypeName());
		
		String type=getTypeName();
		
		List<Mbl3dExpression> expressions=null;
		if(type.isEmpty()){
			Mbl3dExpression expression=Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().currentRangesToMbl3dExpression();
			expressions=Lists.newArrayList(expression);
		}else{
			
			List<Mbl3dData> list=Lists.newArrayList(Mbl3dExpressionEntryPoint.INSTANCE.getMbl3dDatas());
			
			Mbl3dDataComparator comparator=new Mbl3dDataComparator(emotionsData.getEmotions());
			comparator.setOrder(Mbl3dDataComparator.ORDER_TYPE);
			Collections.sort(list, comparator);
			
			
			
			
			
			//watch out expressions is immutable,so if you will modify convert to arraylist
			expressions=Lists.newArrayList(FluentIterable.from(list)
			.filter(emotionsData.makeNamePredicate(type))
			.transform(Mbl3dDataFunctions.getMbl3dExpressionFunction()));
			
			if(!getAdditionalExpression().isEmpty()){
				for(Mbl3dExpression expression:expressions){
					expression.set("Expressions_"+getAdditionalExpression(), 1);
				}
			}
			
			
			//insert first and last simple-eye-blink animation
			expressions.add(0,new Mbl3dExpression().set("Expressions_eyes01_min", 1));//eye blink.empty is boring
			expressions.add(new Mbl3dExpression().set("Expressions_eyes01_min", 1));//eye blink
			
			LogUtils.log(expressions.size());
		}
		
		
		
		currentFrame=1;
		
		//temporaly just animate selection 
		
		//filter type by datas with sort
		
		
		
		AnimationClip clip=Mbl3dExpressionEntryPoint.INSTANCE.converToAnimationClip("test", expressions, true, true, true);
		LogUtils.log(clip);
		
		Mbl3dExpressionEntryPoint.INSTANCE.getMixer().uncacheClip(clip);//same name cache that.
		Mbl3dExpressionEntryPoint.INSTANCE.getMixer().clipAction(clip).play();
		
		Mbl3dExpressionEntryPoint.INSTANCE.insertMaterialAlphaAnimations();
		
		maxFrame=(int)(clip.getDuration()*1000/(1000.0/30))+1;
		
		LogUtils.log("max-frame:"+maxFrame);
		
		recording=true;
	}
	
	boolean recording;
	boolean writing;
	public boolean isWriting() {
		return writing;
	}

	public void setWriting(boolean writing) {
		this.writing = writing;
	}

	public boolean isRecording() {
		return recording;
	}

	private int currentFrame;
	private int maxFrame;
	
	public void setRecording(boolean recording) {
		this.recording = recording;
	}
	
	boolean debug=false;
	
	public void record(String dataUrl){
		writing=true;
		//capture it.send request.
		String fileName=Strings.padStart(String.valueOf(currentFrame), 5, '0')+".png";
		
		if(!debug){
		sender.post(fileName, dataUrl, new PostListener() {
			
			@Override
			public void onReceived(String response) {
				//LogUtils.log(response); //return just size
				writing=false;
			}
			
			@Override
			public void onError(String message) {
				LogUtils.log(message);
				writing=false;
			}
		});
		}else{
			writing=false;
		}
		
		currentFrame++;
		if(currentFrame==maxFrame){
			cancelRecord();
		}
	}
	private ListBox typeFilterBox;
	private EmotionsData emotionsData;
	public void setEmotionData(EmotionsData emotionData) {
		this.emotionsData=emotionData;
		
		
		for(String name:emotionsData.getPrimaryNames()){
			typeFilterBox.addItem(name);
		}
		for(String name:emotionsData.getSecondaryNames()){
			typeFilterBox.addItem(name);
		}
		
	}
}
