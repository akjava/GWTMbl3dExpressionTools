package com.akjava.mbl3d.expression.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
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
	private ValueListBox<String> hairListBox;
	

	public static String toModelName(String url){
		String file=FileNames.getFileNameAsSlashFileSeparator(url);//TODO add fileNames?
		String name=FileNames.getRemovedExtensionName(file);
		return name;
	}
	public ModelControlPanel(Mbl3dExpressionEntryPoint mbl3dExpressionEntryPoint){
		this.mbl3dExpressionEntryPoint=mbl3dExpressionEntryPoint;
		add(new Label("Model Control"));
		
		
		hairListBox = new ValueListBox<String>(new Renderer<String>() {

			@Override
			public String render(String object) {
				if(object!=null){
					return toModelName(object);
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
		
		
		String hairList="models.txt";
		String hairBase="";
		
		hairListBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				ModelControlPanel.this.mbl3dExpressionEntryPoint.loadModel(event.getValue());
			}
		});
		
		loadHairList(hairList);
	}
	
	private List<String> hairListData;
	
	protected void loadHairList(String hairListPath){
		THREE.XHRLoader().load(hairListPath+GWTHTMLUtils.parameterTime(), new XHRLoadHandler() {
			@Override
			public void onLoad(String text) {
				hairListData = CSVUtils.splitLinesWithGuava(text);
				hairListBox.setValue(hairListData.get(0),true);
				hairListBox.setAcceptableValues(hairListData);
				
			}
		});
	}
}
