package com.akjava.mbl3d.expression.client;

import java.util.List;

import com.akjava.gwt.lib.client.datalist.SimpleTextDatasOwner;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.mbl3d.expression.client.datalist.DumpRestoreClearPanel;
import com.akjava.mbl3d.expression.client.player.PlayerPanel;
import com.akjava.mbl3d.expression.client.texture.CanvasTexturePainter;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PreferenceTab extends VerticalPanel{
	

	public PreferenceTab(final SimpleTextDatasOwner owner){
		
		this.add(new Label("DataList"));
		DumpRestoreClearPanel panel=new DumpRestoreClearPanel(owner);
		panel.setDumpFileName("expressions_list_dump.csv");
		
		this.add(panel);
		
		this.add(new Label("Player"));
		this.add(new PlayerPanel());
		
		this.add(new Label("CanvasTexturePainter"));
		canvasTexturePainterPanel = new VerticalPanel();
		this.add(canvasTexturePainterPanel);
	}
	
	private CanvasTexturePainter canvasTexturePainter;
	private VerticalPanel canvasTexturePainterPanel;

	public void setCanvasTexturePainter(CanvasTexturePainter canvasTexturePainter) {
		this.canvasTexturePainter = canvasTexturePainter;
		generateCanvasTexturePainterPanel();
	}
	
	private List<CheckBox> boxes=Lists.newArrayList();
	public void generateCanvasTexturePainterPanel(){
		canvasTexturePainterPanel.clear();
	
		for(int i=0;i<canvasTexturePainter.getTextures().size();i++){
			
			Texture texture=canvasTexturePainter.getTextures().get(i);
			String name=texture.getSourceFile();
			
			name=FileNames.getRemovedExtensionName(FileNames.asSlash().getFileName(name));
			CheckBox check=new CheckBox(name);
			//check.setValue(canvasTexturePainter.getTextureLayers().isVisible(i));
			check.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					//
					updateAnimationBoolean();
				}
			});
			canvasTexturePainterPanel.add(check);
			boxes.add(check);
		}
		
		
		/*//for test
		boxes.get(0).setValue(true);
		canvasTexturePainter.getTextureLayers().setVisible(0, true);
		*/
		
		//already too much repainted;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				updateAnimationBoolean();
			}
		});
		
		
		
	}

	protected void updateAnimationBoolean() {
		Mbl3dExpressionEntryPoint.INSTANCE.getAnimationBoolean().clear();
		for(CheckBox box:boxes){
			Mbl3dExpressionEntryPoint.INSTANCE.getAnimationBoolean().add(box.getValue());
		}
		Mbl3dExpressionEntryPoint.INSTANCE.onAnimationBooleanUpdated();
	}
}
