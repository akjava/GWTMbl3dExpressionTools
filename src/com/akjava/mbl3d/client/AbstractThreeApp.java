package com.akjava.mbl3d.client;

import com.akjava.gwt.lib.client.LogUtils;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;

/**
 * @deprecated duplicate equirectangular project
 * don't modify here
 * @author aki
 *
 */
public abstract class AbstractThreeApp implements AnimationCallback{
	private AnimationHandle handler;
	protected Panel parent;
	
	private boolean debugAnimateOneTime;
	
	
	
	public boolean isDebugAnimateOneTime() {
		return debugAnimateOneTime;
	}

	public void setDebugAnimateOneTimeOnly(boolean debugAnimateOneTime) {
		this.debugAnimateOneTime = debugAnimateOneTime;
	}

	public Panel getParent() {
		return parent;
	}

	public void start(Panel parent) {
		this.parent=parent;
		init();
		execute(System.currentTimeMillis());
	}

	public void stop() {
		if(handler!=null){
			handler.cancel();
		}
		
		if(parent!=null){
			parent.clear();
		}
		

		
		if(resizeHandler!=null){
			resizeHandler.removeHandler();
		}
	}

	@Override
	public void execute(double timestamp) {
		if(!debugAnimateOneTime){//for debug,if error happen on animate
		handler=AnimationScheduler.get().requestAnimationFrame(this);
		}else{
			LogUtils.log("debugAnimateOneTime:true only render called one time for debug");
		}
		animate(timestamp);
	}
	public abstract void animate(double timestamp);
	public abstract void init();
	public abstract void onWindowResize();
	
	protected HandlerRegistration resizeHandler;
	//alternative to dat.GUI
	
	/*
	 * 
	 * it's better to keep Verticalpanel ,it's hard to use Layout*Panel
	 * possible problem,if container size changed after showed,usually problem
	 */
	protected void addResizeHandler(){
	
		//popup.show();
		//moveToAroundRightTop(popup);
		
		
		resizeHandler = Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				onWindowResize();
			}
		});
		
		
	}
	

	
	/**
	 * 
	 * @return double.this value used for calculate ratio,if return Int it's make problem without cast.
	 * THREE.PerspectiveCamera( 30, SCREEN_WIDTH / SCREEN_HEIGHT, 1, 10000 );
	 */
	public double getWindowInnerWidth(){
		return getParent().getOffsetWidth();
	}
	
	public double getWindowInnerHeight(){
		return getParent().getOffsetHeight();
	}
	
	//for attach event,must be focus panel
	protected FocusPanel createContainerPanel(){
		FocusPanel panel=new FocusPanel();
		getParent().add(panel);
		return panel;
	}
	
	


	protected HTML createAbsoluteHTML(String html, int x, int y) {
		HTML htmlWidget=new HTML(html);
		htmlWidget.getElement().getStyle().setPosition(Position.ABSOLUTE);
		htmlWidget.getElement().getStyle().setLeft(x, Unit.PX);
		htmlWidget.getElement().getStyle().setTop(y, Unit.PX);
		return htmlWidget;
	}
}
