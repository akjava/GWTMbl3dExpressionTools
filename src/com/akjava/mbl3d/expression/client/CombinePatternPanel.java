package com.akjava.mbl3d.expression.client;

import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.widget.EnterKeySupportTextBox;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.akjava.mbl3d.expression.client.Mblb3dExpression.ClosedResult;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CombinePatternPanel extends VerticalPanel{
	StorageControler storageControler;
	

	List<Emotion> emotions;
	private Label selectionLabel;
	
	private CombinedExpression combinedExpression;
	private IntegerBox indexBox;
	private EnterKeySupportTextBox descriptionBox;
	
	private BasicExpressionPanel basicPanel;
	

	private CheckBox hasValueCheck;
	
	public CombinePatternPanel(final StorageControler storageControler,final BasicExpressionPanel basicPanel){
		this.storageControler=storageControler;
		this.basicPanel=basicPanel;
		final VerticalPanel emotionButtons=new VerticalPanel();
		
		//null button
		Button bt=new Button("None",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				selectEmotion(null);
			}
		});
		emotionButtons.add(bt);
		bt.setWidth("200px");
		
		THREE.XHRLoader().load("models/mbl3d/emotions.csv", new XHRLoadHandler() {
			
			@Override
			public void onLoad(String text) {
				emotions=new EmotionCsvConverter().convert(text);
				
				//debug
				
				for(final Emotion e:emotions){
					//LogUtils.log(e.toString());
					Button bt=new Button(e.getPrimary()+" - "+e.getSecondary(),new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							selectEmotion(e);
						}
					});
					emotionButtons.add(bt);
					bt.setWidth("200px");
				}
				
				
			}
		});
		
		
		combinedExpression = new CombinedExpression();
		
		
		//indexBox
		
		HorizontalPanel h=new HorizontalPanel();
		h.setWidth("300px");
		indexBox = new IntegerBox();
		
				
		indexBox.setValue(storageControler.getValue(StorageKeys.STORAGE_INDEX_KEY,0));
		indexBox.setWidth("40px");
		h.add(indexBox);
		
		indexBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				int value=indexBox.getValue();
				
				try {
					storageControler.setValue(StorageKeys.STORAGE_INDEX_KEY, value);
				} catch (StorageException e) {
					LogUtils.log(e.getMessage());
				}
				
				updateSelectionBox(value);
				
				//setIndex here
				Mblb3dExpression expression=combinedExpression.getAt(value);
				
				updateClosedLabel(expression);
				updateBasicPanelExpression(expression);
				
			}
		});
		this.add(h);
		
		final Button prev=new Button("Prev",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int index=indexBox.getValue();
				if(hasValueCheck.getValue()){
					int start=index;
					index--;
					while(index!=start){
						if(index<0){
							index=combinedExpression.getIndexSize()-1;
						}
						//check
						String emotion=storageControler.getValue(StorageKeys.STORAGE_KEY+index, null);
						if(emotion!=null){
							break;
						}
						index--;
					}
					setIndex(index);
				}else{
					index--;
					if(index<0){
						index=combinedExpression.getIndexSize()-1;
					}
					setIndex(index);
				}
				
			}
		});
		h.add(prev);
		
		final Button next=new Button("Next",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int index=indexBox.getValue();
				if(hasValueCheck.getValue()){
					int start=index;
					index++;
					while(index!=start){
						if(index>=combinedExpression.getIndexSize()){
							index=0;
						}
						//check
						String emotion=storageControler.getValue(StorageKeys.STORAGE_KEY+index, null);
						if(emotion!=null){
							break;
						}
						index++;
					}
					setIndex(index);
				}else{
				index++;
				if(index>=combinedExpression.getIndexSize()){
					index=0;
				}
				setIndex(index);
				}
			}
		});
		h.add(next);
		
		final Button reset=new Button("Back");
		
		prev.setEnabled(false);
		next.setEnabled(false);
		
		reset.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(reset.getText().equals("Neutral")){
				
					updateClosedLabel(null);
					updateBasicPanelExpression(null);
				reset.setText("Back");
				prev.setEnabled(false);
				next.setEnabled(false);
				}else{
					int index=indexBox.getValue();
					Mblb3dExpression expression=combinedExpression.getAt(index);
					updateClosedLabel(expression);
					updateBasicPanelExpression(expression);
					
					reset.setText("Neutral");
					prev.setEnabled(true);
					next.setEnabled(true);
				}
			}
		});
		h.add(reset);
		
		hasValueCheck = new CheckBox("has value only");
		h.add(hasValueCheck);
		
		
		HorizontalPanel closedPanel=new HorizontalPanel();
		this.add(closedPanel);
		
		
		
		closedLabel=new Label();
		
		CheckBox showCheck=new CheckBox();
		showCheck.setValue(true);
		showCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				closedLabel.setVisible(event.getValue());
			}
			
		});
		closedPanel.add(showCheck);
		
		closedPanel.add(closedLabel);
		
		HorizontalPanel selectionPanel=new HorizontalPanel();
		selectionLabel=new Label("Type:");
		selectionLabel.setWidth("200px");
		selectionPanel.add(selectionLabel);
		
		
		descriptionBox = new EnterKeySupportTextBox() {
			@Override
			public void onEnterKeyDown() {
				submitBox();
			}
		};
		
		
		
		descriptionBox.setWidth("200px");
		
		descriptionBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				LogUtils.log(event.getValue());
			}
		});
		
		int index=indexBox.getValue();
		descriptionBox.setValue(storageControler.getValue(StorageKeys.STORAGE_DESCRIPTION_KEY+index,""));
		//check stored or not
		descriptionBox.addKeyUpHandler(new KeyUpHandler() {
		    @Override
		    public void onKeyUp(KeyUpEvent event) {
		     if(event.getNativeKeyCode() != KeyCodes.KEY_ENTER) {
		               updateDescriptionBoxColor();
		           }
		    }
		});
		this.add(selectionPanel);
		this.add(descriptionBox);
		
		this.add(emotionButtons);
		
		this.add(new Label("Tools"));
		
		final HorizontalPanel p=new HorizontalPanel();
		p.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		p.add(new Button("dump",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				List<String> lines=Lists.newArrayList();
				for(int i=1;i<=3671;i++){
					String line=null;
					String type=storageControler.getValue(StorageKeys.STORAGE_KEY+i, null);
					String description=storageControler.getValue(StorageKeys.STORAGE_DESCRIPTION_KEY+i, null);
					if(type!=null || description!=null){
						if(type==null){
							type="";
						}
						line=i+"\t"+type;
						if(description!=null){
							line+="\t"+description;
						}
					}
					if(line!=null){
						lines.add(line);
					}
				}
				String text=Joiner.on("\r\n").join(lines);
				Anchor a=HTML5Download.get().generateTextDownloadLink(text, "dump.txt", "dump",true);
				p.add(a);
			}
		}));
		this.add(p);
		FileUploadForm uploadDump=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				
				for(int i=1;i<=3671;i++){
					storageControler.removeValue(StorageKeys.STORAGE_KEY+i);
					storageControler.removeValue(StorageKeys.STORAGE_DESCRIPTION_KEY+i);
				}
				List<String[]> csvs=CSVUtils.csvTextToArrayList(text, '\t');
				for(String[] csv:csvs){
					String index=csv[0];
					if(index.isEmpty()){
						continue;
					}
					int id=ValuesUtils.toInt(index, -1);
					if(id!=-1){
						if(csv.length>1){
							String type=csv[1];
							if(!type.isEmpty()){
							try {
								storageControler.setValue(StorageKeys.STORAGE_KEY+index, type);
							} catch (StorageException e) {
								LogUtils.log(e.getMessage());
							}
							}
						}
						if(csv.length>2){
							String description=csv[2];
							if(!description.isEmpty()){
							try {
								storageControler.setValue(StorageKeys.STORAGE_DESCRIPTION_KEY+index, description);
							} catch (StorageException e) {
								LogUtils.log(e.getMessage());
							}
							}
						}
					}
				}
			}
		}, true, "UTF-8");
		p.add(uploadDump);
		uploadDump.setAccept(FileUploadForm.ACCEPT_TXT);
	}
	
	protected void updateBasicPanelExpression(Mblb3dExpression expression) {
		basicPanel.setMbl3dExpression(expression);
		basicPanel.setOverwriteEnable(false);//changed
	}

	public void selectEmotion(Emotion emotion){
		String key=StorageKeys.STORAGE_KEY+indexBox.getValue();
		
		if(emotion!=null){
		String value=emotion.getSecondary();
		try {
			storageControler.setValue(key, value);
			submitBox();
		} catch (StorageException e) {
			LogUtils.log(e.getMessage());
		}}else{
			storageControler.removeValue(key);	
		}
		
		updateSelectionBox(indexBox.getValue());
	}
	
	protected void updateSelectionBox(int value) {
		String emotion=storageControler.getValue(StorageKeys.STORAGE_KEY+value, null);
		String label="Type:";
		if(emotion!=null){
			Emotion em=Emotion.findEmotionBySecondaryName(emotion,emotions);
			label+=em.toString();
		}
		
		String description=storageControler.getValue(StorageKeys.STORAGE_DESCRIPTION_KEY+value, "");
		descriptionBox.setValue(description);
		
		selectionLabel.setText(label);
	}

	private void updateClosedLabel(@Nullable Mblb3dExpression expression){
		if(expression==null){
			closedLabel.setText("");
			return;
		}
		
		ClosedResult result=basicPanel.findClosed(expression);
		closedLabel.setText(result.getExpression().getName()+":"+result.getLength());
	}
	Label closedLabel;
	
	private void setIndex(int index){
		indexBox.setValue(index,true);
		updateDescriptionBoxColor();
	}
	
	protected void updateDescriptionBoxColor() {
		int index=indexBox.getValue();
		String description=descriptionBox.getValue();
		String value=storageControler.getValue(StorageKeys.STORAGE_DESCRIPTION_KEY+index,"");
		if(description.equals(value)){
			GWTHTMLUtils.setBackgroundColor(descriptionBox, "#ffffff");
		}else{
			GWTHTMLUtils.setBackgroundColor(descriptionBox, "#ffcccc");
		}
	}
	

	protected void submitBox() {
		int index=indexBox.getValue();
		String description=descriptionBox.getValue();
		if(description.isEmpty()){
			storageControler.removeValue(StorageKeys.STORAGE_DESCRIPTION_KEY+index);
		}else{
			try {
				storageControler.setValue(StorageKeys.STORAGE_DESCRIPTION_KEY+index, descriptionBox.getValue());
			} catch (StorageException e) {
				LogUtils.log(e.getMessage());
			}
		}
		
		updateDescriptionBoxColor();
	}
	

}
