package com.akjava.mbl3d.expression.client.timetable;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;

public class PlusMinusIntegerBox extends Composite implements Editor<Integer>,ValueAwareEditor<Integer>  , HasValueChangeHandlers<Integer>, HasValue<Integer>{
	private int min;
	private Button minusButton;
	private Button plusButton;
	public PlusMinusIntegerBox(int min, int max){
		this(min,max,1);
	}
	public PlusMinusIntegerBox(int min, int max, int increment) {
		super();
		this.min = min;
		this.max = max;
		this.increment = increment;
		
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		initWidget(panel);
		
		valueBox = new IntegerBox();
		
		minusButton = new Button("-",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int m=valueBox.getValue()-PlusMinusIntegerBox.this.increment;
				if(m<PlusMinusIntegerBox.this.min){
					m=PlusMinusIntegerBox.this.max;
				}
				valueBox.setValue(m);
			}
		});
		panel.add(minusButton);
		
		plusButton = new Button("+",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int m=valueBox.getValue()+PlusMinusIntegerBox.this.increment;
				if(m>PlusMinusIntegerBox.this.max){
					m=PlusMinusIntegerBox.this.min;
				}
				valueBox.setValue(m);
			}
		});
		panel.add(plusButton);
		
		panel.add(valueBox);
	}

	private int max;
	private int increment;
	private int value;
	private IntegerBox valueBox;
	public IntegerBox getValueBox() {
		return valueBox;
	}
	@Override
	public void setDelegate(EditorDelegate<Integer> delegate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getValue() {
		flush();
		return value;
	}

	@Override
	public void setValue(Integer value, boolean fireEvents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
		this.value=valueBox.getValue();
	}

	@Override
	public void onPropertyChange(String... paths) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(Integer value) {
		this.value=value;
		valueBox.setValue(value);
	}
	
	public void setEnabled(boolean enabled){
		valueBox.setEnabled(enabled);
		plusButton.setEnabled(enabled);
		minusButton.setEnabled(enabled);
	}

}
