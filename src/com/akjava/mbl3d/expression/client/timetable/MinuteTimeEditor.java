package com.akjava.mbl3d.expression.client.timetable;

import com.akjava.lib.common.utils.TimeUtils.TimeValue;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MinuteTimeEditor extends Composite implements Editor<Double>,ValueAwareEditor<Double>  , HasValueChangeHandlers<Double>, HasValue<Double>{
private double value;
private PlusMinusIntegerBox minuteBox;
private PlusMinusIntegerBox secondBox;
private PlusMinusIntegerBox millisecondBox;


	public MinuteTimeEditor() {
	super();
	VerticalPanel root=new VerticalPanel();
	
	HorizontalPanel timePanel=new HorizontalPanel();
	timePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	root.add(timePanel);
	

	

	
	minuteBox = new PlusMinusIntegerBox(0,59);
	minuteBox.getValueBox().setWidth("20px");
	timePanel.add(minuteBox);
	
	timePanel.add(new Label(":"));
	
	
	
	secondBox = new PlusMinusIntegerBox(0,59);
	secondBox.getValueBox().setWidth("20px");
	timePanel.add(secondBox);
	
	timePanel.add(new Label("."));
	
	millisecondBox = new PlusMinusIntegerBox(0,999,50);
	millisecondBox.getValueBox().setWidth("30px");
	timePanel.add(millisecondBox);
	
	initWidget(root);
	}
	
	public void setEnabled(boolean enabled){
		minuteBox.setEnabled(enabled);
		secondBox.setEnabled(enabled);
		millisecondBox.setEnabled(enabled);
	}

	@Override
	public void setDelegate(EditorDelegate<Double> delegate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Double getValue() {
		flush();
		return value;
	}

	@Override
	public void setValue(Double value, boolean fireEvents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Double> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
		long t=minuteBox.getValue()*60*1000;
		t+=secondBox.getValue()*1000;
		t+=millisecondBox.getValue();
		value=t;
	}

	@Override
	public void onPropertyChange(String... paths) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(Double value) {
		this.value=value;
		double t=value;
		TimeValue time=new TimeValue((long)t);
		minuteBox.setValue(time.getMinute());
		secondBox.setValue(time.getSecond());
		millisecondBox.setValue(time.getMillisecond());
	}

}
