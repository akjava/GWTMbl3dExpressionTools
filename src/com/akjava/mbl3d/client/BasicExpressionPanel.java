package com.akjava.mbl3d.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.akjava.gwt.three.client.gwt.JSParameter;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.mbl3d.client.Mblb3dExpression.ClosedResult;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BasicExpressionPanel extends VerticalPanel{

	private Map<String,LabeledInputRangeWidget2> ranges;
	private List<Mblb3dExpression> expressionList;
	private ValueListBox<Mblb3dExpression> expressionsListBox;
	
	public BasicExpressionPanel(final Mesh morphMesh){
		ranges=Maps.newHashMap();
		Label expression=new Label("Expression");
		
		this.add(expression);
		
		expressionsListBox = new ValueListBox<Mblb3dExpression>(new Renderer<Mblb3dExpression>() {
			@Override
			public String render(Mblb3dExpression object) {
				if(object!=null){
					return object.getName();
				}
				return "";
			}

			@Override
			public void render(Mblb3dExpression object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		expressionsListBox.addValueChangeHandler(new ValueChangeHandler<Mblb3dExpression>() {
			@Override
			public void onValueChange(ValueChangeEvent<Mblb3dExpression> event) {
				
				setMbl3dExpression(event.getValue());
				
			}
		});
		
		//first column
		HorizontalPanel hpanel=new HorizontalPanel();
		hpanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		this.add(hpanel);
		
		hpanel.add(expressionsListBox);
		
		Button prev=new Button("Prev",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Mblb3dExpression expression= expressionsListBox.getValue();
				int index=expressionList.indexOf(expression);
				index--;
				if(index<0){
					index=expressionList.size()-1;
				}
				expressionsListBox.setValue(expressionList.get(index),true);
			}
		});
		hpanel.add(prev);
		
		Button next=new Button("Next",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Mblb3dExpression expression= expressionsListBox.getValue();
				int index=expressionList.indexOf(expression);
				index++;
				if(index>=expressionList.size()){
					index=0;
				}
				expressionsListBox.setValue(expressionList.get(index),true);
			}
		});
		hpanel.add(next);
		
		//TODO make export panel
		
		Label morph=new Label("Morph");
		this.add(morph);
		
		
		
		String debug="";//for get key all
		JSParameter param=morphMesh.getMorphTargetDictionary().cast();
		
		JsArrayString keys=param.getKeys();
		for(int i=0;i<keys.length();i++){
			String key=keys.get(i);
			final int index=param.getInt(key);
			String originKey=key;
			key=key.substring("Expressions_".length());
			HorizontalPanel inputPanel=new HorizontalPanel();
			final ToggleButton toggle=new ToggleButton(key, new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					//toggleClicked((ToggleButton)event.getSource());
				}

				
			});
			inputPanel.add(toggle);
			
			LabeledInputRangeWidget2 inputRange=new LabeledInputRangeWidget2(key, 0, 1, 0.01);
			inputRange.getLabel().setVisible(false);
			inputPanel.add(inputRange);
			
			ranges.put(originKey, inputRange);
			inputRange.getTextBox().setHeight("12px");
			
			
			debug+=key+"\n";
			
			
			inputRange.setValue(0);
			this.add(inputPanel);
			inputRange.addtRangeListener(new ValueChangeHandler<Number>() {
				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					morphMesh.getMorphTargetInfluences().set(index, event.getValue().doubleValue());
				}
			});
		}
	}
	
	protected void setMbl3dExpression(@Nullable Mblb3dExpression expression) {
		//updateClosedLabel(expression);
		
		//TODO not set direct via label
		for(String key:ranges.keySet()){
			LabeledInputRangeWidget2 widget=ranges.get(key);
			widget.setValue(0,true);
		}
			
		//clear 0 first
		/*
		for(int i=0;i<mesh.getMorphTargetInfluences().length();i++){
			mesh.getMorphTargetInfluences().set(i,0);
		}
		*/
		
		if(expression==null){
			//no value reset
			return;
		}
		
		//set new values
		for(String key:expression.getKeys()){
			
			LabeledInputRangeWidget2 widget=ranges.get(key);
			double value=expression.get(key);
			widget.setValue(value,true);
			
			/*
			int index=mesh.getMorphTargetIndexByName(key);
			double value=expression.get(key);
			mesh.getMorphTargetInfluences().set(index, value);
			*/
		}
	}

	public void setExpressionList(List<Mblb3dExpression> expressionList) {
		this.expressionList=expressionList;
		expressionsListBox.setValue(expressionList.get(0));
		expressionsListBox.setAcceptableValues(expressionList);
	}
	public ClosedResult findClosed(Mblb3dExpression expression){
		return Mblb3dExpression.findClosed(expression, expressionList);
	}
	
}
