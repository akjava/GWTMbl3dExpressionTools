package com.akjava.mbl3d.expression.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

//TODO convert widget
public class TextureMontageWidget extends VerticalPanel{

	//not fire yet
	private List<TextureMontageData> textureMontageDatas;
	private TextureMontage textureMontage;
	private ListBox typeBox;
	private VerticalPanel container;

	private void initWidget(VerticalPanel container){
		container.clear();
		for(final TextureMontageData data:textureMontageDatas){
			HorizontalPanel panel=new HorizontalPanel();
			container.add(panel);
			HorizontalPanel h1=new HorizontalPanel();
			h1.setWidth("100px");
			panel.add(h1);
			CheckBox check=new CheckBox(data.getKeyName());
			check.setWidth("100px");
			h1.add(check);
			
			IntegerBox opacityBox=new IntegerBox();
			opacityBox.setWidth("30px");
			opacityBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {

				@Override
				public void onValueChange(ValueChangeEvent<Integer> event) {
					LogUtils.log(data.getKeyName()+",opacity="+event.getValue());
					data.setOpacity(event.getValue());
				}
			});
			panel.add(opacityBox);
			opacityBox.setValue(data.getOpacity());
			
			if(data.getType()==TextureMontageData.TYPE_LIST){
				final ValueListBox<String> box=new ValueListBox<String>(new Renderer<String>() {
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
						
					}
				});
				box.setValue(data.getValue());
				box.setAcceptableValues(data.getValues());
				panel.add(box);
				box.addValueChangeHandler(new ValueChangeHandler<String>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						data.setValue(event.getValue());
					}
				});
				
				check.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						box.setEnabled(event.getValue());
						data.setEnabled(event.getValue());
					}
				});
				
			check.setValue(data.isEnabled(), true);
			}else if(data.getType()==TextureMontageData.TYPE_COLOR){
				//TODO support color;
			}
		}
	}
	public TextureMontageWidget(TextureMontage textureMontage) {
		super();
		this.textureMontage=textureMontage;
		this.textureMontageDatas = textureMontage.getTextureMontageDatas();
		
		container = new VerticalPanel();
		add(container);
		initWidget(container);
		
		HorizontalPanel savePanel=new HorizontalPanel();
		add(savePanel);
		final HorizontalPanel linkPanel=new HorizontalPanel();
		savePanel.setVerticalAlignment(ALIGN_MIDDLE);
		savePanel.setSpacing(2);
		savePanel.add(new Label("Export"));
		Button exportBt=new Button("Data",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				linkPanel.clear();
				String text=new TextureMontageDataConverter().reverse().convert(TextureMontageWidget.this.textureMontageDatas);
				Anchor alink=HTML5Download.get().generateTextDownloadLink(text, "montage.txt", "download",true);
				linkPanel.add(alink);
				
				//TODO store
			}
		});
		savePanel.add(exportBt);
		
		//TODO image
		Button textureBt=new Button("Texture",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				linkPanel.clear();
				String text=TextureMontageWidget.this.textureMontage.getCanvas().toDataUrl();
				Anchor alink=HTML5Download.get().generateBase64DownloadLink(text,"image/png", "montage_texture.png", "download",true);
				linkPanel.add(alink);
				
				//TODO store
			}
		});
		savePanel.add(textureBt);
		
		savePanel.add(linkPanel);
		
		HorizontalPanel loadPanel=new HorizontalPanel();
		typeBox = new ListBox();
		typeBox.addItem("Load");
		typeBox.addItem("Replace");
		typeBox.setSelectedIndex(0);
		loadPanel.add(typeBox);
		
		add(loadPanel);
		FileUploadForm upload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				loadData(text);
			}
		}, true);
		upload.setAccept(FileUploadForm.ACCEPT_TXT);
		loadPanel.add(upload);
		
		//presets
		final HorizontalPanel presetPanel=new HorizontalPanel();
		add(presetPanel);
		Label presetLabel=new Label("Preset:");
		presetPanel.add(presetLabel);
		
THREE.XHRLoader().load("montagepreset.txt"+GWTHTMLUtils.parameterTime(), new XHRLoadHandler() {
			

			@Override
			public void onLoad(String text) {
				List<String> lines=CSVUtils.splitLinesWithGuava(text,true);//empty no need
				
				ValueListBox<String> presetListBox=new ValueListBox<String>(new Renderer<String>() {

					@Override
					public String render(String object) {
						if(object!=null){
							String file=FileNames.getFileNameAsSlashFileSeparator(object);
							String name=FileNames.getRemovedExtensionName(file);
							
							return name;
						}
						return null;
					}

					@Override
					public void render(String object, Appendable appendable) throws IOException {
						// TODO Auto-generated method stub
						
					}
				});
				presetListBox.setValue(lines.get(0));
				presetListBox.setAcceptableValues(lines);
				presetPanel.add(presetListBox);
				
				presetListBox.addValueChangeHandler(new ValueChangeHandler<String>() {

					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						THREE.XHRLoader().load("models/mbl3d14/montage_presets/"+event.getValue()+GWTHTMLUtils.parameterTime(), new XHRLoadHandler() {
							@Override
							public void onLoad(String text) {
								loadData(text);
							}
						});
						
					}
				});
			}
});
		
		
	}
	public void loadData(String text){
		//TODO validate
		List<TextureMontageData> montageData=new TextureMontageDataConverter().convert(text);
		
		
		//replace
		if(typeBox.getSelectedIndex()==1){
		//copy?
		TextureMontageWidget.this.textureMontageDatas.clear();
		for(TextureMontageData data:montageData){
			TextureMontageWidget.this.textureMontageDatas.add(data);
		}
		}else{
			//Load
			for(TextureMontageData newdata:montageData){
				//find key
				boolean finded=false;
				for(TextureMontageData data:textureMontageDatas){
					if(data.getKeyName().equals(newdata.getKeyName())){
						data.setOpacity(newdata.getOpacity());
						data.setValue(newdata.getValue());
						finded=true;
						break;
					}
				}
				if(!finded){
					LogUtils.log("load-faild:key not found "+newdata.getKeyName());
				}
				
			}
		}
		
		//re-widget
		initWidget(container);
	}
	
}
