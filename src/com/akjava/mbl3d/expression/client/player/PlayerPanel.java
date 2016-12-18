package com.akjava.mbl3d.expression.client.player;

import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.mbl3d.expression.client.Mbl3dExpression;
import com.akjava.mbl3d.expression.client.Mbl3dExpressionEntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PlayerPanel extends VerticalPanel{

	public PlayerPanel(){

		HorizontalPanel buttons=new HorizontalPanel();
		this.add(buttons);
		Button play=new Button("Play",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doPlay();
			}
		});
		buttons.add(play);
		
		Button stop=new Button("Stop",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Mbl3dExpressionEntryPoint.INSTANCE.stopAnimation();
			}
		});
		buttons.add(stop);
		
		LabeledInputRangeWidget2 duration=new LabeledInputRangeWidget2("duraction", 0.1, 4, 0.1);
		duration.getLabel().setWidth("60px");
		duration.getRange().setWidth("80px");
		buttons.add(duration);
		duration.setValue(1);
		duration.addtRangeListener(new ValueChangeHandler<Number>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				Mbl3dExpressionEntryPoint.INSTANCE.setDuration(event.getValue().doubleValue());
			}
		});
		HorizontalPanel startPanel=new HorizontalPanel();
		Label fromLabel=new Label("From:");
		this.add(startPanel);
		startPanel.add(fromLabel);
		final Label selectionLabel=new Label();
		selectionLabel.setWidth("100%");
		startPanel.add(selectionLabel);
		
		Button setBt=new Button("set",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				fromExpression=currentRangesToMbl3dExpression();
				//how to get name?,
				String label=Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().generateFileName();
				selectionLabel.setText(label);
			}
		});
		startPanel.add(setBt);
		Button clearBt=new Button("clear",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				fromExpression=null;
				selectionLabel.setText("");
			}
		});
		startPanel.add(clearBt);
	}
	private Mbl3dExpression fromExpression;
	private Mbl3dExpression currentRangesToMbl3dExpression(){
		return Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().currentRangesToMbl3dExpression(true);
	}

	protected void doPlay() {
		
		Mbl3dExpression toExpression=currentRangesToMbl3dExpression();
		Mbl3dExpressionEntryPoint.INSTANCE.playAnimation(fromExpression,toExpression,true,true,true);
		
		
	}

}
