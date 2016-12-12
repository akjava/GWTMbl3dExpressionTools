package com.akjava.mbl3d.expression.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.ColorUtils;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.mbl3d.expression.client.color.ColorLabelData;
import com.akjava.mbl3d.expression.client.color.ColorLabelDataConverter;
import com.akjava.mbl3d.expression.client.color.ColorLabelListBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HairControlPanel extends VerticalPanel{
private Mbl3dExpressionEntryPoint mbl3dExpressionEntryPoint;
	private ValueListBox<String> hairListBox;

	public HairControlPanel(Mbl3dExpressionEntryPoint mbl3dExpressionEntryPoint){
		this.mbl3dExpressionEntryPoint=mbl3dExpressionEntryPoint;
		add(new Label("Hair Control"));
		
		
		hairListBox = new ValueListBox<String>(new Renderer<String>() {

			@Override
			public String render(String object) {
				if(object!=null){
					String file=FileNames.getFileNameAsSlashFileSeparator(object);
					String name=FileNames.getRemovedExtensionName(file);
					
					//get last directory name
					
					//TODO dir check;
					return name;
				}
				return null;
			}

			@Override
			public void render(String object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		HorizontalPanel namePanel=new HorizontalPanel();
		namePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		String labelWidth="60px";
		
		Label nameLabel=new Label("Name:");
		nameLabel.setWidth(labelWidth);
		namePanel.add(nameLabel);
		namePanel.add(hairListBox);
		add(namePanel);
		
		
		Button next=new Button("Next",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String value=hairListBox.getValue();
				if(value==null){
					return;
				}
				
				int index=hairListData.indexOf(value);
				if(index==hairListData.size()-1){
					index=0;
				}else{
					index++;
				}
				String newValue=hairListData.get(index);
				hairListBox.setValue(newValue,true);
			}
		});
		namePanel.add(next);
		
		
		String hairList="hairs.txt";
		hairBase="models/mbl3d14/hairs/";
		
		hairListBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				loadHair();
			}
		});
		
		loadHairList(hairList);
		
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
				HairControlPanel.this.mbl3dExpressionEntryPoint.getHairMaterial().getColor().setHex(hex);
				
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
		
		loadHairColorList("haircolors.txt");
		
	}
	String hairBase;
	
	private List<String> hairListData;
	
	protected void loadHairList(String hairListPath){
		THREE.XHRLoader().load(hairListPath+GWTHTMLUtils.parameterTime(), new XHRLoadHandler() {
			

			@Override
			public void onLoad(String text) {
				hairListData = CSVUtils.splitLinesWithGuava(text);
				hairListBox.setValue(hairListData.get(0));
				hairListBox.setAcceptableValues(hairListData);
				
				loadHair();
			}
		});
	}
	
	private List<ColorLabelData> colorLabelDatas;
	private ColorLabelListBox hairColorListBox;
	protected void loadHairColorList(String hairListPath){
		THREE.XHRLoader().load(hairListPath+GWTHTMLUtils.parameterTime(), new XHRLoadHandler() {
			

			@Override
			public void onLoad(String text) {
				colorLabelDatas=new ColorLabelDataConverter().reverse().convert(text);
				hairColorListBox.setAcceptableValues(colorLabelDatas);
				//not update,because hard to sync loading hair-model
			}
		});
	}


	protected void loadHair() {
		String url=hairBase+hairListBox.getValue();
		mbl3dExpressionEntryPoint.loadHair(url);
	}
}
