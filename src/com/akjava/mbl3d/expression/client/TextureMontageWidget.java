package com.akjava.mbl3d.expression.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

//TODO convert widget
public class TextureMontageWidget extends VerticalPanel{

	//not fire yet
	private List<TextureMontageData> textureMontageDatas;
	private TextureMontage textureMontage;

	private void initWidget(VerticalPanel container){
		container.clear();
		for(final TextureMontageData data:textureMontageDatas){
			HorizontalPanel panel=new HorizontalPanel();
			container.add(panel);
			HorizontalPanel h1=new HorizontalPanel();
			h1.setWidth("80px");
			panel.add(h1);
			CheckBox check=new CheckBox(data.getKeyName());
			check.setWidth("80px");
			h1.add(check);
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
		
		final VerticalPanel container=new VerticalPanel();
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
				String text=TextureMontageWidget.this.textureMontage.getCanvas().toDataUrl();
				Anchor alink=HTML5Download.get().generateBase64DownloadLink(text,"image/png", "montage_texture.png", "download",true);
				linkPanel.add(alink);
				
				//TODO store
			}
		});
		savePanel.add(textureBt);
		
		savePanel.add(linkPanel);
		
		HorizontalPanel loadPanel=new HorizontalPanel();
		loadPanel.add(new Label("Load"));
		add(loadPanel);
		FileUploadForm upload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				//TODO validate
				List<TextureMontageData> montageData=new TextureMontageDataConverter().convert(text);
				
				//copy?
				TextureMontageWidget.this.textureMontageDatas.clear();
				for(TextureMontageData data:montageData){
					TextureMontageWidget.this.textureMontageDatas.add(data);
				}
				
				//re-widget
				initWidget(container);
			}
		}, true);
		upload.setAccept(FileUploadForm.ACCEPT_TXT);
		loadPanel.add(upload);
	}
	
}