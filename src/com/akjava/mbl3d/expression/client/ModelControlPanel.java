package com.akjava.mbl3d.expression.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.mbl3d.expression.client.model.ModelInfoData;
import com.akjava.mbl3d.expression.client.model.ModelInfoDataConverter;
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

public class ModelControlPanel extends VerticalPanel{
	private Mbl3dExpressionEntryPoint mbl3dExpressionEntryPoint;
	private ValueListBox<ModelInfoData> modelInfoDataBox;
	

	public static String toModelName(String url){
		String file=FileNames.getFileNameAsSlashFileSeparator(url);//TODO add fileNames?
		String name=FileNames.getRemovedExtensionName(file);
		return name;
	}
	public ModelControlPanel(Mbl3dExpressionEntryPoint mbl3dExpressionEntryPoint){
		this.mbl3dExpressionEntryPoint=mbl3dExpressionEntryPoint;
		add(new Label("Model Control"));
		
		
		modelInfoDataBox = new ValueListBox<ModelInfoData>(new Renderer<ModelInfoData>() {

			@Override
			public String render(ModelInfoData object) {
				if(object!=null){
					return toModelName(object.getPath());
				}
				return null;
			}

			@Override
			public void render(ModelInfoData object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		HorizontalPanel namePanel=new HorizontalPanel();
		namePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		String labelWidth="60px";
		
		Label nameLabel=new Label("Name:");
		nameLabel.setWidth(labelWidth);
		namePanel.add(nameLabel);
		namePanel.add(modelInfoDataBox);
		add(namePanel);
		
		
		Button next=new Button("Next",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String value=modelInfoDataBox.getValue().getPath();
				if(value==null){
					return;
				}
				
				int index=hairListData.indexOf(value);
				if(index==hairListData.size()-1){
					index=0;
				}else{
					index++;
				}
				ModelInfoData newValue=hairListData.get(index);
				modelInfoDataBox.setValue(newValue,true);
			}
		});
		namePanel.add(next);
		
		
		String hairList="models.txt";
		String hairBase="";
		
		modelInfoDataBox.addValueChangeHandler(new ValueChangeHandler<ModelInfoData>() {
			@Override
			public void onValueChange(ValueChangeEvent<ModelInfoData> event) {
				ModelControlPanel.this.mbl3dExpressionEntryPoint.loadModel(event.getValue().getPath());
				ModelControlPanel.this.mbl3dExpressionEntryPoint.setEyeModifier(event.getValue().getEyeModifier());
				//
			}
		});
		
		loadHairList(hairList);
	}
	
	private List<ModelInfoData> hairListData;
	
	protected void loadHairList(String hairListPath){
		THREE.XHRLoader().load(hairListPath+GWTHTMLUtils.parameterTime(), new XHRLoadHandler() {
			@Override
			public void onLoad(String text) {
				hairListData = new ModelInfoDataConverter().convert(text);
				modelInfoDataBox.setValue(hairListData.get(0),true);
				modelInfoDataBox.setAcceptableValues(hairListData);
				
			}
		});
	}
}
