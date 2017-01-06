package com.akjava.mbl3d.expression.client.timetable;

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
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.lib.common.utils.TimeUtils.TimeValue;
import com.akjava.mbl3d.expression.client.Mbl3dExpression;
import com.akjava.mbl3d.expression.client.Mbl3dExpressionEntryPoint;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataFunctions;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataFunctions.Mbl3dExpressionFunctionWithEyeModifier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TimeTableDataPanel extends VerticalPanel{
	 
	private TimeTableDataEditor editor;
	private EasyCellTableObjects<TimeTableData> cellObjects;

	private String storageKey;
	private StorageControler storageControler;
	public TimeTableDataPanel(String storageKey,StorageControler storageControler) {
		this.storageKey=storageKey;
		this.storageControler=storageControler;
		editor = new TimeTableDataEditor();    
				
		editor.setValue(new TimeTableData());
		this.add(editor);


	
	//create easy cell tables
	SimpleCellTable<TimeTableData> table=new SimpleCellTable<TimeTableData>() {
		@Override
		public void addColumns(CellTable<TimeTableData> table) {
			
			ExtentedSafeHtmlCell extentedCell=new ExtentedSafeHtmlCell(){
				@Override
				public void onDoubleClick(int clientX, int clientY) {
					//
				}};
			
				
				 StyledTextColumn<TimeTableData> labelColumn=new StyledTextColumn<TimeTableData>(extentedCell){
					@Override
					public com.akjava.gwt.lib.client.widget.cell.StyledTextColumn.StyleAndLabel getStyleAndLabel(TimeTableData object) {
						// TODO Auto-generated method stub
						return new StyleAndLabel("",object.getLabel());
					}
					 
				 };
			
			
			table.addColumn(labelColumn);
			table.setColumnWidth(labelColumn, "120px");
			
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
			
			
			 StyledTextColumn<TimeTableData> timeColumn=new StyledTextColumn<TimeTableData>(extentedCell){
					@Override
					public com.akjava.gwt.lib.client.widget.cell.StyledTextColumn.StyleAndLabel getStyleAndLabel(TimeTableData object) {
						String style="";
						if(!isLargerTime(object)){
							style="red";
							LogUtils.log("style-red");
						}
						TimeValue timeValue=new TimeValue((long)object.getTime());
						
						return new StyleAndLabel(style,timeValue.toMinuteString());
					}
					 
				 };
				 
			
			
			TextColumn<TimeTableData> typeColumn=new TextColumn<TimeTableData>() {
				@Override
				public String getValue(TimeTableData object) {
					//TODO convert time label
					TimeValue timeValue=new TimeValue((long)object.getTime());
					return ""+timeValue.toMinuteString();
				}
			};
			table.addColumn(timeColumn);
			
			TextColumn<TimeTableData> referenceColumn=new TextColumn<TimeTableData>() {
				@Override
				public String getValue(TimeTableData object) {
					int id=object.getReferenceId();
					if(!object.isReference()||id==-1){
						return "";
					}
					
					Mbl3dData data=Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel().getDataById(id);
					if(data==null){
						return "#"+id+" NOT FOUND";
					}else{
						return data.getName();
					}
				}
			};
			table.addColumn(referenceColumn);
			table.setColumnWidth(referenceColumn, "120px");
		}
	};
	
	
	cellObjects = new EasyCellTableObjects<TimeTableData>(table){
		@Override
		public void onSelect(TimeTableData selection) {
			editor.setValue(selection);
			onDataSelected(selection);
		}};
	
	
	//controler
	HorizontalPanel buttons=new HorizontalPanel();
	this.add(buttons);
	newBt = new Button("New",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			editor.setValue(new TimeTableData());
			cellObjects.unselect();
		}
	});
	buttons.add(newBt);
	newBt.setEnabled(false);
	
	addOrUpdateBt = new Button("Add",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			TimeTableData newData=makeData();
			if(cellObjects.isSelected()){
				cellObjects.update();
				cellObjects.unselect();
				storeData();
			}else{
				addData(newData,true);
				editor.setValue(new TimeTableData());//for continue data
			//cellObjects.setSelected(newData, true);
			}
			
		}
	});
	addOrUpdateBt.setWidth("80px");
	buttons.add(addOrUpdateBt);
	
	copyBt = new Button("clone",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			TimeTableData selection=cellObjects.getSelection();
			if(selection==null){
				return;
			}
			TimeTableData newData=copy(selection);
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
			if(uploadModeBox.getSelectedIndex()==0){//replace
				clearAllData();
			}
			
			Iterable<TimeTableData> newDatas=jsonTextToTimeTableDatas(text);
			
			if(newDatas==null){
				Window.alert("invalid file format.see log");
				return;
			}
			
			 for(TimeTableData newData:newDatas){
				 addData(newData,false);
				 cellObjects.setSelected(newData, true);//maybe last selected
			 }
			 storeData();
			
		}
	}, true, "UTF-8");
	 upload.setAccept(".json");
	 uploadPanel.add(upload);
	 
	 //downloads
	 HorizontalPanel downloadPanels=new HorizontalPanel();
	 downloadPanels.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	 final HorizontalPanel download=new HorizontalPanel();
	 
	 Button downloadBt=new Button("download",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			download.clear();
			String text=toStoreText();
			Anchor a=HTML5Download.get().generateTextDownloadLink(text, getDownloadFileName(), "click to download",true);
			download.add(a);
		}
	});
	 downloadPanels.add(downloadBt);
	 downloadPanels.add(download);
	 
	 
	 
	 this.add(table);
	 
	 
	 this.add(uploadPanel);
	 this.add(downloadPanels);
	
	//initial load from storage
	String jsonText=storageControler.getValue(storageKey, null);
	if(jsonText!=null){
		Iterable<TimeTableData> datas=jsonTextToTimeTableDatas(jsonText);
		if(datas!=null){
		for(TimeTableData data:datas){
			addData(data,false);
		}
		}else{
			LogUtils.log("stored data is invalid");
		}
	}
	
	

	
	HorizontalPanel playerPanel=new HorizontalPanel();
	this.add(playerPanel);
	Button play=new Button("Play",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			doPlay();
		}
	});
	playerPanel.add(play);
	
	Button stop=new Button("Stop",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Mbl3dExpressionEntryPoint.INSTANCE.stopAnimation();
		}
	});
	playerPanel.add(stop);
	
}
	protected void doPlay() {
		/*List<Double> times=Lists.newArrayList();
		List<Mbl3dExpression> expressions=Lists.newArrayList();
		
		Mbl3dExpressionFunctionWithEyeModifier mbl3dExpressionFunctionWithEyeModifier=new Mbl3dExpressionFunctionWithEyeModifier(Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().getEyeModifierValue());
		
		for(TimeTableData data:cellObjects.getDatas()){
			if(data.isReference()){
				int id=data.getReferenceId();
				
				double time=data.getTime();
				
				if(times.size()>0 && times.get(times.size()-1)>=time){
					LogUtils.log("skipped time, smaller than last time");
					continue;
				}
				
				
				 * -1 is used as clear
				 
				Mbl3dData mbl3dData=id==-1?new Mbl3dData():Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel().getDataById(id);
				Mbl3dExpression expression=mbl3dExpressionFunctionWithEyeModifier.apply(mbl3dData);
				
				expressions.add(expression);
				times.add(time/1000);//clip animation is second base,
			}else{
				//TODO
			}
		}
		
		
	
		AnimationClip clip=Mbl3dExpressionEntryPoint.INSTANCE.converToAnimationClip("test", times, expressions);
		Mbl3dExpressionEntryPoint.INSTANCE.playAnimation(clip);*/
		
		final TimeTableDataBlock others=new TimeTableDataBlock(cellObjects.getDatas());
		others.setStartAt(2000);
		
THREE.XHRLoader().load("animations/eyeblink.json", new XHRLoadHandler() {
			
			@Override
			public void onLoad(String text) {
				List<TimeTableData> newDatas=Lists.newArrayList(jsonTextToTimeTableDatas(text));
				TimeTableDataBlock eyeblink=new TimeTableDataBlock(newDatas);
				eyeblink.setLoop(true);
				eyeblink.setLoopTime(5);
				eyeblink.setLoopInterval(1000);
				AnimationKeyFrameBuilder builder=new AnimationKeyFrameBuilder(Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel());
				
				
				List<TimeTableDataBlock> blocks=Lists.newArrayList(eyeblink,others);
				
				
				AnimationKeyGroup group=builder.createMergedGroup(blocks);
				
				JSParameter param=Mbl3dExpressionEntryPoint.INSTANCE.getMesh().getMorphTargetDictionary().cast();
				AnimationClip clip=group.converToAnimationClip("test",Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().getEyeModifierValue(),param);
			
				Mbl3dExpressionEntryPoint.INSTANCE.playAnimation(clip);
			}
		});
	}
	
	//TODO merge above
	public Mbl3dExpression timeTableDataToMbl3dExpression(TimeTableData data){
		if(data.isReference()){
			int id=data.getReferenceId();
			Mbl3dData mbl3dData=id==-1?new Mbl3dData():Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel().getDataById(id);
			if(mbl3dData==null){
				return null;
			}
			return Mbl3dDataFunctions.getMbl3dExpressionFunction().apply(mbl3dData);
		}else{
			//TODO
			return null;
		}
	}
	
	
	
	private Iterable<TimeTableData> jsonTextToTimeTableDatas(String text){
		
		JSONFormatConverter converter=new JSONFormatConverter("TimeTable","timetable");
		//todo check validate
		JSONValue jsonValue=null;
		
		/**
		 * converter make null error 
		 */
		try{
			jsonValue=converter.convert(text);
		}catch (Exception e) {
			LogUtils.log(e.getMessage());
			return null;
		}
		
		List<JSONObject> datas=converter.toJsonObjectList(jsonValue);
		
		 Iterable<TimeTableData> newDatas=new TimeTableDataConverter().reverse().convertAll(datas);
		 return newDatas;
	}
	
	private String baseFileName="TimeTableData";
	private Button newBt;
	private Button addOrUpdateBt;
	private Button removeBt;
	private Button copyBt;
	protected String getDownloadFileName() {
		return baseFileName+".json";
	}

	public TimeTableData copy(TimeTableData data){
		TimeTableDataConverter converter=new TimeTableDataConverter();
		return converter.reverse().convert(converter.convert(data));
	}
	
	protected TimeTableData makeData() {
		editor.flush();
		
		return editor.getValue();
	}


	public void addData(TimeTableData data,boolean updateStorages) {
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
		 for(TimeTableData data:ImmutableList.copyOf(cellObjects.getDatas())){
				removeData(data);
			}
	}
	
	 public void removeData(TimeTableData data){
		 if(data==null){
			 return;
		 }
		 cellObjects.removeItem(data);
		 onDataRemoved(data);
	 }
	
	public String toStoreText(){
		JSONFormatConverter converter=new JSONFormatConverter("TimeTableDataPanel", "timetable");
		JSONValue value=converter.fromJsonObjectList(new TimeTableDataConverter().convertAll(cellObjects.getDatas()));
		return converter.reverse().convert(value);
		//return Joiner.on("\r\n").join();
	 }

	public void onDataSelected(@Nullable TimeTableData selection) {
	//enabling buttons
	if(selection==null){
		newBt.setEnabled(false);
		copyBt.setEnabled(false);
		removeBt.setEnabled(false);
		editor.setValue(new TimeTableData());
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
		Mbl3dExpression expression=timeTableDataToMbl3dExpression(selection);
		if(expression!=null){
			Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().setMbl3dExpression(expression);
		}else{
			LogUtils.log("somehow can't converted.skipped selecting range");
		}
	}else{
		Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().setMbl3dExpression(null);//meaning empty
		}
	}

	public void onDataRemoved(TimeTableData data){
		storeData();
	}
	public void onDataAdded(TimeTableData data){
		
	}
	public void onDataUpdated(TimeTableData data){
		//TODO link something
		cellObjects.getSimpleCellTable().getCellTable().redraw();
	}
	

	private boolean isLargerTime(TimeTableData data){
		int index=cellObjects.getDatas().indexOf(data);
		if(index==-1){
			return false;
		}
		double max=-1;
		for(int i=0;i<index;i++){
			if(max<cellObjects.getDatas().get(i).getTime()){
				max=cellObjects.getDatas().get(i).getTime();
			}
		}
		return data.getTime()>max;
	}
	
	public class TimeTableDataEditor extends VerticalPanel implements Editor<TimeTableData>,ValueAwareEditor<TimeTableData>{
		private TimeTableData value;
		private TextBox labelEditor;
		private MinuteTimeEditor timeEditor;
		private IntegerBox referenceIdEditor;
		private CheckBox referenceEditor;

		public TimeTableData getValue() {
			return value;
		}
		
		public TimeTableDataEditor(){
			String labelWidth="100px";
			int fontSize=14;

						HorizontalPanel labelPanel=new HorizontalPanel();
						labelPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(labelPanel);
						Label labelLabel=new Label("Label");
						labelLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						labelLabel.setWidth(labelWidth);
						labelPanel.add(labelLabel);
						labelEditor=new TextBox();
			labelEditor.setWidth("200px");
						labelPanel.add(labelEditor);


						HorizontalPanel timePanel=new HorizontalPanel();
						timePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(timePanel);
						Label timeLabel=new Label("Time");
						timeLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						timeLabel.setWidth(labelWidth);
						timePanel.add(timeLabel);
						timeEditor=new MinuteTimeEditor();
			//timeEditor.setWidth("100px");
						timePanel.add(timeEditor);


						HorizontalPanel referenceIdPanel=new HorizontalPanel();
						referenceIdPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(referenceIdPanel);
						Label referenceIdLabel=new Label("ReferenceId");
						referenceIdLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						referenceIdLabel.setWidth(labelWidth);
						referenceIdPanel.add(referenceIdLabel);
						referenceIdEditor=new IntegerBox();
			referenceIdEditor.setWidth("100px");
						referenceIdPanel.add(referenceIdEditor);


						HorizontalPanel referencePanel=new HorizontalPanel();
						referencePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(referencePanel);
						Label referenceLabel=new Label("Reference");
						referenceLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						referenceLabel.setWidth(labelWidth);
						referencePanel.add(referenceLabel);
						referenceEditor=new CheckBox();
			referenceEditor.setWidth("100px");
						referencePanel.add(referenceEditor);


						
						
		}


@Override
			public void setDelegate(EditorDelegate<TimeTableData> delegate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				
				
				value.setLabel(labelEditor.getValue());
				value.setTime(timeEditor.getValue());
				value.setReferenceId(referenceIdEditor.getValue());
				value.setReference(referenceEditor.getValue());

				onDataUpdated(value);
			}

			@Override
			public void onPropertyChange(String... paths) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValue(@Nullable TimeTableData value) {
				this.value=value;
				if(value==null){
					//set disable
					labelEditor.setEnabled(false);
					timeEditor.setEnabled(false);
					referenceIdEditor.setEnabled(false);
					referenceEditor.setEnabled(false);
					return;
				}else{
					//set enable
					labelEditor.setEnabled(true);
					timeEditor.setEnabled(true);
					referenceIdEditor.setEnabled(true);
					referenceEditor.setEnabled(true);

				}
				
				labelEditor.setValue(value.getLabel());
				timeEditor.setValue(value.getTime());
				referenceIdEditor.setValue(value.getReferenceId());
				referenceEditor.setValue(value.isReference());

			}
	}
}
