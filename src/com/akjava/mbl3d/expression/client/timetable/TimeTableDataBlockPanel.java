package com.akjava.mbl3d.expression.client.timetable;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.json.JSONFormatConverter;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.ExtentedSafeHtmlCell;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.lib.client.widget.cell.SimpleContextMenu;
import com.akjava.gwt.lib.client.widget.cell.StyledTextColumn;
import com.akjava.gwt.three.client.gwt.JSParameter;
import com.akjava.gwt.three.client.java.file.JSONMorphTargetsFile;
import com.akjava.gwt.three.client.java.file.JSONMorphTargetsFileConverter;
import com.akjava.gwt.three.client.java.file.MorphTargetKeyFrame;
import com.akjava.gwt.three.client.java.file.MorphTargetKeyFrameConverter;
import com.akjava.gwt.three.client.java.file.MorphtargetsModifier;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.lib.common.utils.TimeUtils.TimeValue;
import com.akjava.mbl3d.expression.client.Mbl3dExpression;
import com.akjava.mbl3d.expression.client.Mbl3dExpressionEntryPoint;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataFunctions;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TimeTableDataBlockPanel extends VerticalPanel{
	 
	private TimeTableDataBlockEditor editor;
	private EasyCellTableObjects<TimeTableDataBlock> cellObjects;

	private String storageKey;
	private StorageControler storageControler;
	public TimeTableDataBlockPanel(String storageKey,StorageControler storageControler) {
		this.storageKey=storageKey;
		this.storageControler=storageControler;
		
		
		
		add(new Label("TimeTable"));
		
		timeTableDataPanel = new TimeTableDataPanel(this);
		
		add(timeTableDataPanel);
		
		add(new Label("Blocks"));
		
		
		editor = new TimeTableDataBlockEditor();    
				
		editor.setValue(new TimeTableDataBlock());
		this.add(editor);


	
	//create easy cell tables
	SimpleCellTable<TimeTableDataBlock> table=new SimpleCellTable<TimeTableDataBlock>(6) {
		@Override
		public void addColumns(CellTable<TimeTableDataBlock> table) {
			
			ExtentedSafeHtmlCell extentedCell=new ExtentedSafeHtmlCell(){
				@Override
				public void onDoubleClick(int clientX, int clientY) {
					//
				}};
			
				
			
			
			final SimpleContextMenu simpleMenu=new SimpleContextMenu();
			
			simpleMenu.addCommand("MoveTop", new Command(){
				@Override
				public void execute() {
					cellObjects.topItem(cellObjects.getSelection());
					storeData();
					simpleMenu.hide();
				}

				

				});
			
			simpleMenu.addCommand("MoveUp", new Command(){
				@Override
				public void execute() {
					cellObjects.upItem(cellObjects.getSelection());
					storeData();
					simpleMenu.hide();
				}

				

				});
			simpleMenu.addCommand("MoveDown", new Command(){
				@Override
				public void execute() {
					cellObjects.downItem(cellObjects.getSelection());
					storeData();
					simpleMenu.hide();
				}

				});
			
			simpleMenu.addCommand("MoveBottom", new Command(){
				@Override
				public void execute() {
					cellObjects.bottomItem(cellObjects.getSelection());
					storeData();
					simpleMenu.hide();
				}

				

				});
			simpleMenu.addSeparator();
			
			extentedCell.setCellContextMenu(simpleMenu);
			
			
			 StyledTextColumn<TimeTableDataBlock> nameColumn=new StyledTextColumn<TimeTableDataBlock>(extentedCell){
					@Override
					public com.akjava.gwt.lib.client.widget.cell.StyledTextColumn.StyleAndLabel getStyleAndLabel(TimeTableDataBlock object) {
						String style="";
						
						return new StyleAndLabel(style,object.getName());
					}
					 
				 };
				 
				 table.addColumn(nameColumn);
				 
		
				 StyledTextColumn<TimeTableDataBlock> startColumn=new StyledTextColumn<TimeTableDataBlock>(extentedCell){
						@Override
						public com.akjava.gwt.lib.client.widget.cell.StyledTextColumn.StyleAndLabel getStyleAndLabel(TimeTableDataBlock object) {
							String style="";
							
							TimeValue timeValue=new TimeValue((long)object.getStartAt());
							
							return new StyleAndLabel(style,timeValue.toMinuteString());
							
						}
						 
					 };
			table.addColumn(startColumn,"start");
			
			 StyledTextColumn<TimeTableDataBlock> endColumn=new StyledTextColumn<TimeTableDataBlock>(extentedCell){
					@Override
					public com.akjava.gwt.lib.client.widget.cell.StyledTextColumn.StyleAndLabel getStyleAndLabel(TimeTableDataBlock object) {
						String style="";
						
						double value=object.calcurateEndTime();
						if(value==0){
							return new StyleAndLabel(style,"");
						}
						
						if(object.isLoop() && object.getLastTime()==0){
							return new StyleAndLabel(style,"");
						}
						
						TimeValue timeValue=new TimeValue((long)value);
						
						return new StyleAndLabel(style,timeValue.toMinuteString());
					}
					 
				 };
				 

					table.addColumn(endColumn,"end");
			
			table.setColumnWidth(nameColumn, "120px");
		}
	};
	
	
	cellObjects = new EasyCellTableObjects<TimeTableDataBlock>(table){
		@Override
		public void onSelect(TimeTableDataBlock selection) {
			editor.setValue(selection);
			onDataSelected(selection);
		}};
	
	
	//controler
	HorizontalPanel buttons=new HorizontalPanel();
	this.add(buttons);
	newBt = new Button("New",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			editor.setValue(new TimeTableDataBlock());
			cellObjects.unselect();
		}
	});
	buttons.add(newBt);
	newBt.setEnabled(false);
	
	addOrUpdateBt = new Button("Add",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			TimeTableDataBlock newData=makeData();
			if(cellObjects.isSelected()){
				cellObjects.update();
				cellObjects.unselect();
				storeData();
			}else{
				addData(newData,true);
				editor.setValue(new TimeTableDataBlock());//for continue data
			//cellObjects.setSelected(newData, true);
			}
			
		}
	});
	addOrUpdateBt.setWidth("80px");
	buttons.add(addOrUpdateBt);
	
	copyBt = new Button("clone",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			TimeTableDataBlock selection=cellObjects.getSelection();
			if(selection==null){
				return;
			}
			TimeTableDataBlock newData=copy(selection);
			addData(newData,true);
			
			cellObjects.setSelected(newData, true);//no selection version
			
		}
	});
	buttons.add(copyBt);
	copyBt.setEnabled(false);

	
	removeBt = new Button("remove",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			
			removeData(cellObjects.getSelection());
			
		}
	});
	buttons.add(removeBt);
	removeBt.setEnabled(false);
	
	Button removeAll=new Button("remove All",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			boolean confirm=Window.confirm("TimeTableData:remove all datas?you should export first.");
			if(!confirm){
				return;
			}
			clearAllData();
		}
	});
	buttons.add(removeAll);
	
	
	
	//download replace import widget
	HorizontalPanel uploadPanel=new HorizontalPanel();
	 uploadPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	 final ListBox uploadModeBox=new ListBox();
	 uploadModeBox.addItem("Replace");
	 uploadModeBox.addItem("Import");
	 uploadPanel.add(uploadModeBox);
	 uploadModeBox.setSelectedIndex(0);
	 
	 FileUploadForm upload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
		
		@Override
		public void uploaded(File file, String text) {
			uploadText(text,uploadModeBox.getSelectedIndex()==0);
			
		}
	}, true, "UTF-8");
	 upload.setAccept(".json");
	 uploadPanel.add(upload);
	 
	 //downloads
	 HorizontalPanel downloadPanels=new HorizontalPanel();
	 downloadPanels.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	 final HorizontalPanel download=new HorizontalPanel();
	 
	final  ListBox downloadTypeBox=new ListBox();
	downloadPanels.add(downloadTypeBox);
	 downloadTypeBox.addItem("Normal");
	 downloadTypeBox.addItem("As Universal");
	 downloadTypeBox.addItem("As Threejs-Clip");
	 downloadTypeBox.setSelectedIndex(0);
	 
	 
	 Button downloadBt=new Button("download",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			download.clear();
			String text=toStoreText();
			String fileName=getDownloadFileName(cellObjects.getDatas());
			int selection=downloadTypeBox.getSelectedIndex();
			
			if(selection==1){
				List<TimeTableDataBlock> blocks=cellObjects.getDatas();
				//TODO convert universal foramt
				AnimationKeyFrameBuilder builder=new AnimationKeyFrameBuilder(Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel());
				
				builder.setTotalTimeByBlocks(blocks);
				AnimationKeyGroup group=builder.createMergedGroup(blocks);
				List<List<MorphTargetKeyFrame>> list=group.converToMorphTargetKeyFrame();
				LogUtils.log("list:"+list.size());
				
				Iterable<JSONMorphTargetsFile> files=new MorphTargetKeyFrameConverter().convertAll(list);
				List<JSONMorphTargetsFile> filesList=Lists.newArrayList(files);
				LogUtils.log("files:"+filesList.size());
				
				
				
				Iterable<JSONObject> objects=new JSONMorphTargetsFileConverter().convertAll(files);
				LogUtils.log("objects");
				
				JSONArray jsonArray=JSONFormatConverter.createJSONArray(objects);
				LogUtils.log("array");
				
				text=new JSONFormatConverter("Mbl3dExpression", "jsonmorphtargetsfile").reverse().convert(jsonArray);
				fileName="jsonmorphtargetsfile.json";
				
			}else if(selection==2){
				AnimationClip clip=generateAnimationClip(cellObjects.getDatas());
				JSONObject object=new JSONObject(AnimationClip.toJSON(clip));
				
				text= object.toString();
				fileName="threejs-clip.json";
			}
			
			Anchor a=HTML5Download.get().generateTextDownloadLink(text, fileName, "click to download",true);
			download.add(a);
		}
	});
	 downloadPanels.add(downloadBt);
	 downloadPanels.add(download);
	 
	 
	 VerticalPanel v=new VerticalPanel();
	 v.setHeight("200px");
	 v.setVerticalAlignment(ALIGN_TOP);
	 this.add(v);
	 
	 v.add(table);
	 
	 
	 
	 
	 this.add(uploadPanel);
	 this.add(downloadPanels);
	
	//initial load from storage
	String jsonText=storageControler.getValue(storageKey, null);
	if(jsonText!=null){
		Iterable<TimeTableDataBlock> datas=jsonTextToTimeTableDatas(jsonText);
		if(datas!=null){
		for(TimeTableDataBlock data:datas){
			addData(data,false);
		}
		}else{
			LogUtils.log("stored data is invalid");
		}
	}
	
	

	
	HorizontalPanel playerPanel=new HorizontalPanel();
	this.add(playerPanel);
	Button play=new Button("Play All",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Mbl3dExpressionEntryPoint.INSTANCE.stopAnimation();
			doPlay();
		}
	});
	playerPanel.add(play);
	
	Button playSelection=new Button("Play Selection",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(cellObjects.getSelection()==null){
				return;
			}
			
			List<TimeTableDataBlock> blocks=Lists.newArrayList(cellObjects.getSelection());
			Mbl3dExpressionEntryPoint.INSTANCE.playAnimation(generateAnimationClip(blocks));
			
			
			//Mbl3dExpressionEntryPoint.INSTANCE.stopAnimation();
			//timeTableDataPanel.doPlay();
		}
	});
	playerPanel.add(playSelection);
	
	Button stop=new Button("Stop",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Mbl3dExpressionEntryPoint.INSTANCE.stopAnimation();
		}
	});
	playerPanel.add(stop);
	
	
	HorizontalPanel uploadPanel2=new HorizontalPanel();
	uploadPanel2.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	
	final Button play2=new Button("Play >>",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Mbl3dExpressionEntryPoint.INSTANCE.playAnimation(uploadClip);
		}
	});
	uploadPanel2.add(play2);
	play2.setEnabled(false);
	
	 FileUploadForm upload2=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
		
		@Override
		public void uploaded(File file, String text) {
			String type=JSONFormatConverter.parseDataType(text);
			AnimationClip clip=null;
			if(type!=null){
				if(type.equals("timetableblock")){
					Iterable<TimeTableDataBlock> newDatas=jsonTextToTimeTableDatas(text);
					
					if(newDatas==null){
						Window.alert("invalid file format.see log");
						return;
					}
					clip=generateAnimationClip(Lists.newArrayList(newDatas));
					
				}else if(type.equals("jsonmorphtargetsfile")){
					JSONValue value=new JSONFormatConverter("Mbl3dExpression", "jsonmorphtargetsfile").convert(text);
					
					Iterable<JSONMorphTargetsFile> files=new JSONMorphTargetsFileConverter().reverse().convertAll(JSONFormatConverter.convertToJSONObject(value));
					Iterable<List<MorphTargetKeyFrame>> framesList=new MorphTargetKeyFrameConverter().reverse().convertAll(files);
					

					JSParameter morphTargetDictionary=Mbl3dExpressionEntryPoint.INSTANCE.getMesh().getMorphTargetDictionary().cast();
					MorphtargetsModifier modifier=Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().getMorphtargetsModifier();
					
					clip=MorphTargetKeyFrameConverter.converToAnimationClip(framesList, "test", modifier, morphTargetDictionary);
					
				}//TODO support universal
				
			}else{
				JSONValue value=JSONParser.parseStrict(text);
				clip=AnimationClip.parse(value.isObject().getJavaScriptObject());
				
			}
			
			if(clip!=null){
			Mbl3dExpressionEntryPoint.INSTANCE.playAnimation(clip);
			uploadClip=clip;
			play2.setEnabled(true);
			}else{
				LogUtils.log("file-uploaded.but parsing faild\n"+text);
			}
			
		}
	}, false, "UTF-8");//can't reselect same file
	 upload2.setAccept(".json");
	 uploadPanel2.add(upload2);
	 this.add(uploadPanel2);
	
	 createPresetPanel();
}
	protected void uploadText(String text,boolean replace) {
		if(replace){//replace
			clearAllData();
		}
		
		Iterable<TimeTableDataBlock> newDatas=jsonTextToTimeTableDatas(text);
		
		if(newDatas==null){
			Window.alert("invalid file format.see log");
			return;
		}
		
		 for(TimeTableDataBlock newData:newDatas){
			 addData(newData,false);
			 cellObjects.setSelected(newData, true);//maybe last selected
		 }
		 storeData();
		 Mbl3dExpressionEntryPoint.INSTANCE.stopAnimation();
	}
	private void createPresetPanel() {
		HorizontalPanel panel=new HorizontalPanel();
		add(panel);
		
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		panel.add(new Label("Preset:"));
		final  CheckBox confirmCheckBox=new CheckBox("Confirm");
		confirmCheckBox.setValue(true);
		panel.add(confirmCheckBox);
		final ValueListBox<String> fileListBox=new ValueListBox<String>(new Renderer<String>() {
			@Override
			public String render(String object) {
				if(object.isEmpty()){
					return "";
				}
				String removeExtension=FileNames.getRemovedExtensionName(object);
				String nameOnly=FileNames.getFileNameAsSlashFileSeparator(removeExtension);
				
				String prefix="TimeTableDataBlock-";
				if(nameOnly.startsWith(prefix)){
					nameOnly=nameOnly.substring(prefix.length());
				}
				return nameOnly;
			}

			@Override
			public void render(String object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		panel.add(fileListBox);
		THREE.XHRLoader().load("examples/index.txt", new XHRLoadHandler() {
			@Override
			public void onLoad(String text) {
				List<String> keys=CSVUtils.splitLinesWithGuava(text);
				List<String> arrayList=Lists.newArrayList(keys);
				arrayList.add(0, "");
				fileListBox.setValue("");
				fileListBox.setAcceptableValues(arrayList);
			}
		});
		fileListBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String value=event.getValue();
				if(value.isEmpty()){
					return;
				}
				
				THREE.XHRLoader().load("examples/"+value, new XHRLoadHandler() {
					@Override
					public void onLoad(String text) {
						if(confirmCheckBox.getValue()){
							boolean conf=Window.confirm("load preset data.replace current data.do you finished to save?");
							if(!conf){
								return;
							}
						}
						uploadText(text, true);
					}
				});
			}
		});
	}
	private AnimationClip uploadClip;
	protected void doPlay() {
		List<TimeTableDataBlock> blocks=cellObjects.getDatas();
		Mbl3dExpressionEntryPoint.INSTANCE.playAnimation(generateAnimationClip(blocks));
		
		
		
	}
	
	private AnimationClip generateAnimationClip(List<TimeTableDataBlock> blocks){
AnimationKeyFrameBuilder builder=new AnimationKeyFrameBuilder(Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel());
		
		
		
		
		builder.setTotalTimeByBlocks(blocks);
		
		AnimationKeyGroup group=builder.createMergedGroup(blocks);
		
		LogUtils.log("generateAnimationClip");
		LogUtils.log(group);
		
		JSParameter param=Mbl3dExpressionEntryPoint.INSTANCE.getMesh().getMorphTargetDictionary().cast();
		AnimationClip clip=group.converToAnimationClip("test",Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().getMorphtargetsModifier(),param);
	
		return clip;
	}
	
	//TODO merge above
	public Mbl3dExpression timeTableDataToMbl3dExpression(TimeTableData data){
		if(data.isReference()){
			int id=data.getReferenceId();
			Mbl3dData mbl3dData=id==-1?new Mbl3dData():Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel().getDataById(id,data.isEnableBrows(),data.isEnableEyes(),data.isEnableMouth(),data.getRatio());
			if(mbl3dData==null){
				return null;
			}
			return Mbl3dDataFunctions.getMbl3dExpressionFunction().apply(mbl3dData);
		}else{
			//TODO
			return null;
		}
	}
	
	
	
	private Iterable<TimeTableDataBlock> jsonTextToTimeTableDatas(String text){
		
		JSONFormatConverter converter=new JSONFormatConverter("TimeTableBlock","timetableblock");
		//todo check validate
		JSONValue jsonValue=null;
		
		
		// * converter make null error 
		 
		try{
			jsonValue=converter.convert(text);
		}catch (Exception e) {
			LogUtils.log(e.getMessage());
			return null;
		}
		
		List<JSONObject> datas=converter.toJsonObjectList(jsonValue);
		
		 Iterable<TimeTableDataBlock> newDatas=new TimeTableDataBlockConverter().reverse().convertAll(datas);
		 return newDatas;
	}
	
	public void setReferenceId(int id){
		timeTableDataPanel.setReferenceId(id);
	}
	
	private String baseFileName="TimeTableDataBlock";
	private Button newBt;
	private Button addOrUpdateBt;
	private Button removeBt;
	private Button copyBt;
	private TimeTableDataPanel timeTableDataPanel;
	protected String getDownloadFileName(List<TimeTableDataBlock> blocks) {
		List<String> names=Lists.newArrayList();
		if(blocks!=null){
			for(TimeTableDataBlock block:blocks){
				if(!Strings.isNullOrEmpty(block.getName())){
				names.add(block.getName());
				}
			}
		}
		return baseFileName+"-"+Joiner.on("-").join(names)+".json";
	}

	public TimeTableDataBlock copy(TimeTableDataBlock data){
		TimeTableDataBlockConverter converter=new TimeTableDataBlockConverter();
		return converter.reverse().convert(converter.convert(data));
		
	}
	
	protected TimeTableDataBlock makeData() {
		editor.flush();
		
		return editor.getValue();
	}


	public void addData(TimeTableDataBlock data,boolean updateStorages) {
		cellObjects.addItem(data);
		onDataAdded(data);//link something
		
		if(updateStorages){//on initialize no need re-store
			storeData();
		}
	}
	
	public void storeData(){
		 //store data
		 String lines=toStoreText();
		
		 try {
			storageControler.setValue(storageKey, lines);
		} catch (StorageException e) {
			//possible quote error
			Window.alert(e.getMessage());
		}
	}
	
	protected void clearAllData() {
		 for(TimeTableDataBlock data:ImmutableList.copyOf(cellObjects.getDatas())){
				removeData(data);
			}
	}
	
	 public void removeData(TimeTableDataBlock data){
		 if(data==null){
			 return;
		 }
		 cellObjects.removeItem(data);
		 onDataRemoved(data);
	 }
	
	public String toStoreText(){
		JSONFormatConverter converter=new JSONFormatConverter("TimeTableDataBlockPanel", "timetableblock");
		JSONValue value=converter.fromJsonObjectList(new TimeTableDataBlockConverter().convertAll(cellObjects.getDatas()));
		return converter.reverse().convert(value);
		//return Joiner.on("\r\n").join();
	 }

	public void onDataSelected(@Nullable TimeTableDataBlock selection) {
	//enabling buttons
	if(selection==null){
		newBt.setEnabled(false);
		copyBt.setEnabled(false);
		removeBt.setEnabled(false);
		editor.setValue(new TimeTableDataBlock());
		addOrUpdateBt.setText("Add");
	}else{
		addOrUpdateBt.setText("Update");
		newBt.setEnabled(true);
		copyBt.setEnabled(true);
		removeBt.setEnabled(true);
		editor.setValue(selection);
	}
	
	//update range
	if(selection!=null){
		timeTableDataPanel.setValue(selection.getTimeTableDatas());
	}else{
		timeTableDataPanel.setValue(null);
		}
	}

	public void onDataRemoved(TimeTableDataBlock data){
		storeData();
	}
	public void onDataAdded(TimeTableDataBlock data){
		
	}
	/**
	 * calling when timetabledata update
	 * @param data
	 */
	public void onDataUpdated(@Nullable TimeTableDataBlock data){
		//TODO link something
		cellObjects.getSimpleCellTable().getCellTable().redraw();
		storeData();
	}
	

	public class TimeTableDataBlockEditor extends VerticalPanel implements Editor<TimeTableDataBlock>,ValueAwareEditor<TimeTableDataBlock>{
		private TimeTableDataBlock value;
		private TextBox nameEditor;
	
		private MinuteTimeEditor startAtEditor;
		private MinuteTimeEditor beforeMarginEditor;
		private MinuteTimeEditor afterMarginEditor;
		private CheckBox loopEditor;
		private IntegerBox loopTimeEditor;
		private DoubleBox loopIntervalEditor;


		private CheckBox noClearEditor;

		public TimeTableDataBlock getValue() {
			return value;
		}
		
		public TimeTableDataBlockEditor(){
			String labelWidth="100px";
			int fontSize=14;

						HorizontalPanel labelPanel=new HorizontalPanel();
						labelPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(labelPanel);
						Label labelLabel=new Label("Name");
						labelLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						labelLabel.setWidth(labelWidth);
						labelPanel.add(labelLabel);
						nameEditor=new TextBox();
						nameEditor.setWidth("200px");
						labelPanel.add(nameEditor);


									HorizontalPanel startAtPanel=new HorizontalPanel();
									startAtPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
									add(startAtPanel);
									Label startAtLabel=new Label("StartAt");
									startAtLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
									startAtLabel.setWidth(labelWidth);
									startAtPanel.add(startAtLabel);
									startAtEditor=new MinuteTimeEditor();
						startAtEditor.setWidth("100px");
									startAtPanel.add(startAtEditor);


									HorizontalPanel beforeMarginPanel=new HorizontalPanel();
									beforeMarginPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
									add(beforeMarginPanel);
									Label beforeMarginLabel=new Label("BeforeMargin");
									beforeMarginLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
									beforeMarginLabel.setWidth(labelWidth);
									beforeMarginPanel.add(beforeMarginLabel);
									beforeMarginEditor=new MinuteTimeEditor();
						beforeMarginEditor.setWidth("100px");
									beforeMarginPanel.add(beforeMarginEditor);


									HorizontalPanel afterMarginPanel=new HorizontalPanel();
									afterMarginPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
									add(afterMarginPanel);
									Label afterMarginLabel=new Label("AfterMargin");
									afterMarginLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
									afterMarginLabel.setWidth(labelWidth);
									afterMarginPanel.add(afterMarginLabel);
									afterMarginEditor=new MinuteTimeEditor();
						afterMarginEditor.setWidth("100px");
									afterMarginPanel.add(afterMarginEditor);


									HorizontalPanel loopPanel=new HorizontalPanel();
									loopPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
									add(loopPanel);
									Label loopLabel=new Label("Loop");
									loopLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
									//loopLabel.setWidth(labelWidth);
									loopPanel.add(loopLabel);
									loopEditor=new CheckBox();
									loopEditor.setWidth("20px");
									loopPanel.add(loopEditor);


								/*	HorizontalPanel loopTimePanel=new HorizontalPanel();
									loopTimePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
									add(loopTimePanel);*/
									Label loopTimeLabel=new Label("LoopTime");
									loopTimeLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
									//loopTimeLabel.setWidth(labelWidth);
									loopPanel.add(loopTimeLabel);
									loopTimeEditor=new IntegerBox();
									loopTimeEditor.setWidth("50px");
									loopPanel.add(loopTimeEditor);


									/*HorizontalPanel loopIntervalPanel=new HorizontalPanel();
									loopIntervalPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
									add(loopIntervalPanel);*/
									Label loopIntervalLabel=new Label("LoopInterval");
									loopIntervalLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
									//loopIntervalLabel.setWidth(labelWidth);
									loopPanel.add(loopIntervalLabel);
									loopIntervalEditor=new DoubleBox();
									loopIntervalEditor.setWidth("50px");
									loopPanel.add(loopIntervalEditor);


									HorizontalPanel noClearPanel=new HorizontalPanel();
									noClearPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
									add(noClearPanel);
									Label noClearLabel=new Label("No Clear");
									noClearLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
									noClearLabel.setWidth(labelWidth);
									noClearPanel.add(noClearLabel);
									
									noClearEditor=new CheckBox();
									noClearPanel.add(noClearEditor);
						
						
		}


@Override
			public void setDelegate(EditorDelegate<TimeTableDataBlock> delegate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				
				
				value.setName(nameEditor.getValue());
				value.setStartAt(startAtEditor.getValue());
				value.setBeforeMargin(beforeMarginEditor.getValue());
				value.setAfterMargin(afterMarginEditor.getValue());
				value.setLoop(loopEditor.getValue());
				value.setLoopTime(loopTimeEditor.getValue());
				value.setLoopInterval(loopIntervalEditor.getValue());

				value.setNoClear(noClearEditor.getValue());
				
				onDataUpdated(value);
			}

			@Override
			public void onPropertyChange(String... paths) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValue(@Nullable TimeTableDataBlock value) {
				this.value=value;
				if(value==null){
					//set disable
					nameEditor.setEnabled(false);
					startAtEditor.setEnabled(false);
					beforeMarginEditor.setEnabled(false);
					afterMarginEditor.setEnabled(false);
					loopEditor.setEnabled(false);
					loopTimeEditor.setEnabled(false);
					loopIntervalEditor.setEnabled(false);
					noClearEditor.setEnabled(false);
					return;
				}else{
					//set enable
					nameEditor.setEnabled(true);
					startAtEditor.setEnabled(true);
					beforeMarginEditor.setEnabled(true);
					afterMarginEditor.setEnabled(true);
					loopEditor.setEnabled(true);
					loopTimeEditor.setEnabled(true);
					loopIntervalEditor.setEnabled(true);
					noClearEditor.setEnabled(true);

				}
				
				nameEditor.setValue(value.getName());
				startAtEditor.setValue(value.getStartAt());
				beforeMarginEditor.setValue(value.getBeforeMargin());
				afterMarginEditor.setValue(value.getAfterMargin());
				loopEditor.setValue(value.isLoop());
				loopTimeEditor.setValue(value.getLoopTime());
				loopIntervalEditor.setValue(value.getLoopInterval());

				noClearEditor.setValue(value.isNoClear());
			}
	}
}
