package com.akjava.mbl3d.expression.client;

import java.util.List;

import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.datalist.SimpleTextDatasOwner;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.ColorUtils;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.mbl3d.expression.client.color.ColorLabelData;
import com.akjava.mbl3d.expression.client.color.ColorLabelDataConverter;
import com.akjava.mbl3d.expression.client.color.ColorLabelListBox;
import com.akjava.mbl3d.expression.client.datalist.DumpRestoreClearPanel;
import com.akjava.mbl3d.expression.client.player.PlayerPanel;
import com.akjava.mbl3d.expression.client.texture.CanvasTexturePainter;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PreferenceTab extends VerticalPanel{
	
	private Mbl3dExpressionEntryPoint mbl3dExpressionEntryPoint;
	
	public PreferenceTab(Mbl3dExpressionEntryPoint mbl3dExpressionEntryPoint,final SimpleTextDatasOwner owner){
		this.mbl3dExpressionEntryPoint=mbl3dExpressionEntryPoint;
		this.add(new Label("DataList"));
		DumpRestoreClearPanel panel=new DumpRestoreClearPanel(owner);
		panel.setDumpFileName("expressions_list_dump.csv");
		
		this.add(panel);
		
		this.add(new Label("Player"));
		this.add(new PlayerPanel());
		
		//this.add(new Label("CanvasTexturePainter"));
		canvasTexturePainterPanel = new VerticalPanel();//canvasTexturePainterPanel is almost deprecated
		//this.add(canvasTexturePainterPanel);
		
		String labelWidth="60px";
		add(new Label("Background color"));
		
		//colors
				HorizontalPanel colorControlPanel=new HorizontalPanel();
				colorControlPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
				add(colorControlPanel);
				Label colorLabel=new Label("Color:");
				colorLabel.setWidth(labelWidth);
				colorControlPanel.add(colorLabel);
				final ColorBox hairColorBox=new ColorBox();
				colorControlPanel.add(hairColorBox);
				hairColorBox.addValueChangeHandler(new ValueChangeHandler<String>() {

					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						LogUtils.log("color-changed:"+event.getValue());
						int hex=ColorUtils.toColor(event.getValue());
						PreferenceTab.this.mbl3dExpressionEntryPoint.getRenderer().setClearColor(hex);
						
					}
				});
				
				hairColorListBox = new ColorLabelListBox();
				colorControlPanel.add(hairColorListBox);
				hairColorListBox.addValueChangeHandler(new ValueChangeHandler<ColorLabelData>() {
					@Override
					public void onValueChange(ValueChangeEvent<ColorLabelData> event) {
						hairColorBox.setValue(event.getValue().getColor(), true);
					}
				});
				
				loadHairColorList("bgcolors.txt");
		
	}
	
	protected void loadHairColorList(String hairListPath){
		THREE.XHRLoader().load(hairListPath+GWTHTMLUtils.parameterTime(), new XHRLoadHandler() {
			

			@Override
			public void onLoad(String text) {
				colorLabelDatas=new ColorLabelDataConverter().reverse().convert(text);
				hairColorListBox.setValue(colorLabelDatas.get(0),true);
				hairColorListBox.setAcceptableValues(colorLabelDatas);
				
				//not update,because hard to sync loading hair-model
			}
		});
	}
		
	private List<ColorLabelData> colorLabelDatas;
	private ColorLabelListBox hairColorListBox;
	

	private CanvasTexturePainter canvasTexturePainter;
	private VerticalPanel canvasTexturePainterPanel;

	/**
	 * @deprecated
	 * @param canvasTexturePainter
	 */
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

	/**
	 * fire if checkbox touched,call onAnimationBooleanUpdated
	 */
	protected void updateAnimationBoolean() {
		Mbl3dExpressionEntryPoint.INSTANCE.getAnimationBoolean().clear();
		for(CheckBox box:boxes){
			Mbl3dExpressionEntryPoint.INSTANCE.getAnimationBoolean().add(box.getValue());
		}
		Mbl3dExpressionEntryPoint.INSTANCE.onAnimationBooleanUpdated();
	}
}
