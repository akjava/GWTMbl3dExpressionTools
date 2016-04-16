package com.akjava.mbl3d.expression.client.player;

import com.akjava.gwt.three.client.gwt.JSParameter;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.animation.tracks.NumberKeyframeTrack;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.mbl3d.expression.client.Mbl3dExpressionEntryPoint;
import com.akjava.mbl3d.expression.client.Mbl3dExpression;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
	}

	protected void doPlay() {
		
		Mbl3dExpression expression=Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().currentRangesToMbl3dExpression();
		Mbl3dExpressionEntryPoint.INSTANCE.playAnimation(expression,true,true,true);
		
		
	}

}
