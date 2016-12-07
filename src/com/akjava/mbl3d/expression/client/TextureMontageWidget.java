package com.akjava.mbl3d.expression.client;

import java.io.IOException;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

//TODO convert widget
public class TextureMontageWidget extends VerticalPanel{

	//not fire yet
	private List<TextureMontageData> textureMontageDatas;

	public TextureMontageWidget(List<TextureMontageData> textureMontageDatas) {
		super();
		this.textureMontageDatas = textureMontageDatas;
		
		for(final TextureMontageData data:textureMontageDatas){
			HorizontalPanel panel=new HorizontalPanel();
			add(panel);
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
							return object;
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
	
}
