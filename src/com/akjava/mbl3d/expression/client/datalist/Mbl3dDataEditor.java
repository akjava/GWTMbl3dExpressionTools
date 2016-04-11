package com.akjava.mbl3d.expression.client.datalist;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.akjava.mbl3d.expression.client.DataListPanel;
import com.akjava.mbl3d.expression.client.Emotion;
import com.google.common.collect.Lists;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public  class Mbl3dDataEditor extends VerticalPanel implements Editor<Mbl3dData>,ValueAwareEditor<Mbl3dData>{
	/*
	 * watch out start with null
	 */
	private List<Emotion> emotions;
	private ValueListBox<Emotion> emotionsBox;
	private TextBox descriptionBox;
	private Mbl3dData value;
	private TextBox nameBox;
	private Label dateLabel;
	public Mbl3dData getValue() {
		return value;
	}
	public Mbl3dDataEditor(List<Emotion> emotions){
		this.emotions=Lists.newArrayList(emotions);
		this.emotions.add(0, null);
		emotionsBox = new ValueListBox<Emotion>(new Renderer<Emotion>() {

			@Override
			public String render(Emotion object) {
				if(object!=null){
					return object.getPrimary()+" - "+object.getSecondary();
				}
				return "";
			}

			@Override
			public void render(Emotion object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		emotionsBox.setAcceptableValues(emotions);
		
		dateLabel = new Label();
		add(dateLabel);
		
		HorizontalPanel h0=new HorizontalPanel();
		this.add(h0);
		Label name=new Label("name");
		name.setWidth("100px");
		h0.add(name);
		nameBox = new TextBox();
		h0.add(nameBox);
				
				
		HorizontalPanel h1=new HorizontalPanel();
		this.add(h1);
		Label type=new Label("type");
		type.setWidth("100px");
		h1.add(type);
		h1.add(emotionsBox);
		
		//description text-area
		HorizontalPanel h2=new HorizontalPanel();
		this.add(h2);
		Label description=new Label("description");
		description.setWidth("100px");
		h2.add(description);
		
		descriptionBox = new TextBox();
		h2.add(descriptionBox);
		
		//TODO link to basic panel
	}
	@Override
		public void setDelegate(EditorDelegate<Mbl3dData> delegate) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void flush() {
			if(value==null){
				return;
			}
			Emotion emotion=emotionsBox.getValue();
			String type=emotion==null?null:emotion.getSecondary();
			
			String description=descriptionBox.getText();
			if(description.isEmpty()){
				description=null;
			}
			
			String name=nameBox.getText();
			if(name.isEmpty()){
				name=null;
			}
			
			
			value.setType(type);
			value.setDescription(description);
			value.setName(name);
			
		}

		@Override
		public void onPropertyChange(String... paths) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setValue(Mbl3dData value) {
			this.value=value;
			if(value==null){
				emotionsBox.setEnabled(false);
				descriptionBox.setEnabled(false);
				nameBox.setEnabled(false);
				
				emotionsBox.setValue(null);
				descriptionBox.setValue("");
				nameBox.setValue("");
				
				dateLabel.setText("#");
				return;
			}else{
				emotionsBox.setEnabled(true);
				descriptionBox.setEnabled(true);
				nameBox.setEnabled(true);
			}
			
			emotionsBox.setValue(null);
			for(Emotion emotion:emotions){
				if(emotion==null){
					continue;
				}
				if(emotion.getSecondary().equals(value.getType())){
					emotionsBox.setValue(emotion);
				}
			}
			String description=value.getDescription()!=null?value.getDescription():"";
			descriptionBox.setValue(description);
			
			String name=value.getName()!=null?value.getName():"";
			nameBox.setValue(name);
			
			String id="#"+value.getId()+" ";
			dateLabel.setText(id+DataListPanel.dateFormat.format(new Date(value.getCdate())));
		}
}