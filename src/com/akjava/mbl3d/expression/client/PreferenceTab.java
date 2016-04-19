package com.akjava.mbl3d.expression.client;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.datalist.SimpleTextDatasOwner;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.mbl3d.expression.client.datalist.DumpRestoreClearPanel;
import com.akjava.mbl3d.expression.client.player.PlayerPanel;
import com.akjava.mbl3d.expression.client.texture.CanvasTexturePainter;
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
	
	public void generateCanvasTexturePainterPanel(){
		canvasTexturePainterPanel.clear();
	
		for(int i=0;i<canvasTexturePainter.getTextures().size();i++){
			final int index=i;
			Texture texture=canvasTexturePainter.getTextures().get(i);
			String name=texture.getSourceFile();
			
			name=FileNames.getRemovedExtensionName(FileNames.asSlash().getFileName(name));
			CheckBox check=new CheckBox(name);
			check.setValue(canvasTexturePainter.getTextureLayers().isVisible(i));
			check.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					canvasTexturePainter.getTextureLayers().setVisible(index, event.getValue());
				}
			});
			canvasTexturePainterPanel.add(check);
		}
	}
}
