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
import com.akjava.mbl3d.expression.client.Mbl3dExpression;
import com.akjava.mbl3d.expression.client.Mbl3dExpressionEntryPoint;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataFunctions;
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
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
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
	SimpleCellTable<TimeTableDataBlock> table=new SimpleCellTable<TimeTableDataBlock>() {
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
							
							return new StyleAndLabel(style,String.valueOf(object.getStartAt()));
						}
						 
					 };
			table.addColumn(startColumn);
			
			 StyledTextColumn<TimeTableDataBlock> endColumn=new StyledTextColumn<TimeTableDataBlock>(extentedCell){
					@Override
					public com.akjava.gwt.lib.client.widget.cell.StyledTextColumn.StyleAndLabel getStyleAndLabel(TimeTableDataBlock object) {
						String style="";
						
						String text="";
						double value=object.calcurateEndTime();
						if(value!=0){
							text=String.valueOf(value);
						}
						
						return new StyleAndLabel(style,text);
					}
					 
				 };
				 

					table.addColumn(endColumn);
			
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
			if(uploadModeBox.getSelectedIndex()==0){//replace
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
			Mbl3dExpressionEntryPoint.INSTANCE.stopAnimation();
			timeTableDataPanel.doPlay();
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
	
}
	protected void doPlay() {
		
		AnimationKeyFrameBuilder builder=new AnimationKeyFrameBuilder(Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel());
		
		
		List<TimeTableDataBlock> blocks=cellObjects.getDatas();
		
		builder.setTotalTimeByBlocks(blocks);
		
		AnimationKeyGroup group=builder.createMergedGroup(blocks);
		
		//LogUtils.log(group);
		
		JSParameter param=Mbl3dExpressionEntryPoint.INSTANCE.getMesh().getMorphTargetDictionary().cast();
		AnimationClip clip=group.converToAnimationClip("test",Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().getMorphtargetsModifier(),param);
	
		Mbl3dExpressionEntryPoint.INSTANCE.playAnimation(clip);
		
		
		
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
	
	private String baseFileName="TimeTableDataBlock";
	private Button newBt;
	private Button addOrUpdateBt;
	private Button removeBt;
	private Button copyBt;
	private TimeTableDataPanel timeTableDataPanel;
	protected String getDownloadFileName() {
		return baseFileName+".json";
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
	
		private DoubleBox startAtEditor;
		private DoubleBox beforeMarginEditor;
		private DoubleBox afterMarginEditor;
		private CheckBox loopEditor;
		private IntegerBox loopTimeEditor;
		private DoubleBox loopIntervalEditor;


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
									startAtEditor=new DoubleBox();
						startAtEditor.setWidth("100px");
									startAtPanel.add(startAtEditor);


									HorizontalPanel beforeMarginPanel=new HorizontalPanel();
									beforeMarginPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
									add(beforeMarginPanel);
									Label beforeMarginLabel=new Label("BeforeMargin");
									beforeMarginLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
									beforeMarginLabel.setWidth(labelWidth);
									beforeMarginPanel.add(beforeMarginLabel);
									beforeMarginEditor=new DoubleBox();
						beforeMarginEditor.setWidth("100px");
									beforeMarginPanel.add(beforeMarginEditor);


									HorizontalPanel afterMarginPanel=new HorizontalPanel();
									afterMarginPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
									add(afterMarginPanel);
									Label afterMarginLabel=new Label("AfterMargin");
									afterMarginLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
									afterMarginLabel.setWidth(labelWidth);
									afterMarginPanel.add(afterMarginLabel);
									afterMarginEditor=new DoubleBox();
						afterMarginEditor.setWidth("100px");
									afterMarginPanel.add(afterMarginEditor);


									HorizontalPanel loopPanel=new HorizontalPanel();
									loopPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
									add(loopPanel);
									Label loopLabel=new Label("Loop");
									loopLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
									loopLabel.setWidth(labelWidth);
									loopPanel.add(loopLabel);
									loopEditor=new CheckBox();
						loopEditor.setWidth("100px");
									loopPanel.add(loopEditor);


									HorizontalPanel loopTimePanel=new HorizontalPanel();
									loopTimePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
									add(loopTimePanel);
									Label loopTimeLabel=new Label("LoopTime");
									loopTimeLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
									loopTimeLabel.setWidth(labelWidth);
									loopTimePanel.add(loopTimeLabel);
									loopTimeEditor=new IntegerBox();
						loopTimeEditor.setWidth("100px");
									loopTimePanel.add(loopTimeEditor);


									HorizontalPanel loopIntervalPanel=new HorizontalPanel();
									loopIntervalPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
									add(loopIntervalPanel);
									Label loopIntervalLabel=new Label("LoopInterval");
									loopIntervalLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
									loopIntervalLabel.setWidth(labelWidth);
									loopIntervalPanel.add(loopIntervalLabel);
									loopIntervalEditor=new DoubleBox();
						loopIntervalEditor.setWidth("100px");
									loopIntervalPanel.add(loopIntervalEditor);




						
						
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


				}
				
				nameEditor.setValue(value.getName());
				startAtEditor.setValue(value.getStartAt());
				beforeMarginEditor.setValue(value.getBeforeMargin());
				afterMarginEditor.setValue(value.getAfterMargin());
				loopEditor.setValue(value.isLoop());
				loopTimeEditor.setValue(value.getLoopTime());
				loopIntervalEditor.setValue(value.getLoopInterval());

			}
	}
}
