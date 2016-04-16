package com.akjava.mbl3d.expression.client;

import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.jetty.util.StringUtil;

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
import com.akjava.lib.common.utils.StringUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.akjava.mbl3d.expression.client.Mbl3dExpression.ClosedResult;
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
import com.google.gwt.user.client.ui.ListBox;
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
	private EmotionsData emotionsData;
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
				
				//update selectio here
				updateSelectionBox(indexBox.getValue());
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
				
				emotionsData = new EmotionsData(emotions);
				for(String name:emotionsData.getPrimaryNames()){
					typeFilterBox.addItem(name);
				}
				for(String name:emotionsData.getSecondaryNames()){
					typeFilterBox.addItem(name);
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
				selectedExpression=combinedExpression.getAt(value);
				
				updateClosedLabel(selectedExpression);
				updateBasicPanelExpression(selectedExpression);
				
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
						if(validateFilterEmotion(emotion)){
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
						if(validateFilterEmotion(emotion)){
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
					copyToListBt.setEnabled(true);
					int index=indexBox.getValue();
					//need initial
					selectedExpression=combinedExpression.getAt(index);
					updateClosedLabel(selectedExpression);
					updateBasicPanelExpression(selectedExpression);
					
					reset.setText("Neutral");
					prev.setEnabled(true);
					next.setEnabled(true);
				}
			}
		});
		h.add(reset);
		
		
		
		
		HorizontalPanel filterPanel=new HorizontalPanel();
		this.add(filterPanel);
	
		hasValueCheck = new CheckBox("has emotion only");
		filterPanel.add(hasValueCheck);
		hasValueCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				typeFilterBox.setEnabled(event.getValue());
			}
		});
		
		typeFilterBox = new ListBox();
		typeFilterBox.setEnabled(false);
		typeFilterBox.addItem("");
		typeFilterBox.setSelectedIndex(0);
		filterPanel.add(typeFilterBox);
		
		
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
		//updateSelectionBox(index);
		
		//descriptionBox.setValue(storageControler.getValue(StorageKeys.STORAGE_DESCRIPTION_KEY+index,""));
		
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
		
		final HorizontalPanel copyPanel=new HorizontalPanel();
		this.add(copyPanel);
		
		copyToListBt = new Button("Copy to DataList",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				doCopyToDataList();
				
			}
		});
		copyPanel.add(copyToListBt);
		copyToListBt.setEnabled(false);
		
		//TODO move to preference
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
		
		Button test=new Button("doCopyToHasEmotionDataListAll",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doCopyToHasEmotionDataListAll();
			}
		});
		add(test);
	}
	
	public boolean hasEmotion(int index){
		String emotion=getEmotion(index);
		return emotion!=null;
	}
	public String getEmotion(int index){
		return storageControler.getValue(StorageKeys.STORAGE_KEY+index, null);
	}
	public String getDescription(int index){
		return storageControler.getValue(StorageKeys.STORAGE_DESCRIPTION_KEY+index, null);
	}
	
	protected void doCopyToDataList() {
		if(selectedExpression==null){
			LogUtils.log("doCopyToDataList:need selection");
			return;
		}
		
		String description=descriptionBox.getText();
		if(description.isEmpty()){
			description=null;
		}
		//i believe static access smart 
		Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel().add(selectedExpression, null, selectedEmotion, description);
		Mbl3dExpressionEntryPoint.INSTANCE.setSelectedTab(2);
	}
	
	protected void doCopyToHasEmotionDataListAll() {
		int size=combinedExpression.getIndexSize();

		for(int i=0;i<size;i++){
			if(hasEmotion(i)){
				
				Mbl3dExpression expression=combinedExpression.getAt(i);
				String emotion=getEmotion(i);
				String description=getDescription(i);
				
				Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel().add(expression, null, emotion, description);
				
			}
		}
		Mbl3dExpressionEntryPoint.INSTANCE.setSelectedTab(2);
		
		String description=descriptionBox.getText();
		if(description.isEmpty()){
			description=null;
		}
		//i believe static access smart 
		Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel().add(selectedExpression, null, selectedEmotion, description);
		Mbl3dExpressionEntryPoint.INSTANCE.setSelectedTab(2);
	}

	private boolean validateFilterEmotion(String emotion){
		String typeFilter=typeFilterBox.getValue(typeFilterBox.getSelectedIndex());
		if(typeFilter.isEmpty()){
			return emotion!=null;
		}
		if(Character.isUpperCase(typeFilter.charAt(0))){
			List<String> secondaries=emotionsData.getSecondaryNameByPrimaryName(typeFilter);
			return secondaries.contains(emotion);
		}else {
			return typeFilter.equals(emotion);
		}
	}
	
	protected void updateBasicPanelExpression(Mbl3dExpression expression) {
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
	
	private Mbl3dExpression selectedExpression;
	private String selectedEmotion;
	protected void updateSelectionBox(int value) {
		selectedEmotion=storageControler.getValue(StorageKeys.STORAGE_KEY+value, null);
		String label="Type:";
		if(selectedEmotion!=null){
			Emotion em=Emotion.findEmotionBySecondaryName(selectedEmotion,emotions);
			label+=em.toString();
		}
		
		String description=storageControler.getValue(StorageKeys.STORAGE_DESCRIPTION_KEY+value, "");
		descriptionBox.setValue(description);
		
		selectionLabel.setText(label);
	}

	private void updateClosedLabel(@Nullable Mbl3dExpression expression){
		if(expression==null){
			closedLabel.setText("");
			return;
		}
		
		ClosedResult result=basicPanel.findClosed(expression);
		closedLabel.setText(result.getExpression().getName()+":"+result.getLength());
	}
	Label closedLabel;


	private ListBox typeFilterBox;


	private Button copyToListBt;
	
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
