package com.akjava.mbl3d.expression.client.timetable;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.json.JSONFormatConverter;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.ExtentedSafeHtmlCell;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.lib.client.widget.cell.SimpleContextMenu;
import com.akjava.gwt.lib.client.widget.cell.StyledTextColumn;
import com.akjava.gwt.three.client.gwt.JSParameter;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.lib.common.utils.TimeUtils.TimeValue;
import com.akjava.mbl3d.expression.client.Mbl3dExpression;
import com.akjava.mbl3d.expression.client.Mbl3dExpressionEntryPoint;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataFunctions;
import com.google.common.base.Ascii;
import com.google.common.base.Joiner;
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

public class TimeTableDataPanel extends VerticalPanel{
	 
	private TimeTableDataEditor editor;
	private EasyCellTableObjects<TimeTableData> cellObjects;
	private TimeTableDataBlockPanel timeTableDataBlockPanel;
	public TimeTableDataPanel(TimeTableDataBlockPanel timeTableDataBlockPanel) {
		this.timeTableDataBlockPanel=timeTableDataBlockPanel;
		editor = new TimeTableDataEditor();    
				
		editor.setValue(new TimeTableData());
		this.add(editor);


	
	//create easy cell tables
	SimpleCellTable<TimeTableData> table=new SimpleCellTable<TimeTableData>(6) {
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
						String label="";
						
						int id=object.getReferenceId();
						if(!object.isReference()||id==-1){
							return new StyleAndLabel("","");
						}else{
						
						Mbl3dData data=Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel().getDataById(id,object.isEnableBrows(),object.isEnableEyes(),object.isEnableMouth(),object.getRatio());
						if(data==null){
							label= "#"+id+" NOT FOUND";
						}else{
							List<String> labels=Lists.newArrayList();
							labels.add(object.getLabel()!=null?object.getLabel():"");
							labels.add(data.getName());
							labels.add(data.getDescription());
							labels.add(data.getType());
							labels.add("#"+id);
							
							label= Ascii.truncate(Joiner.on(" ").skipNulls().join(labels), 13, "..");
						}
						
						return new StyleAndLabel("",label);
					}
						
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
			
			
			 StyledTextColumn<TimeTableData> startColumn=new StyledTextColumn<TimeTableData>(extentedCell){
					@Override
					public com.akjava.gwt.lib.client.widget.cell.StyledTextColumn.StyleAndLabel getStyleAndLabel(TimeTableData object) {
						String style="";
						if(!isLargerTime(object)){
							style="red";
							
						}
						TimeValue timeValue=new TimeValue((long)object.getTime());
						
						return new StyleAndLabel(style,timeValue.toMinuteString());
					}
					 
				 };
				 table.addColumn(startColumn,"Start"); 
				 
				 
				 StyledTextColumn<TimeTableData> endColumn=new StyledTextColumn<TimeTableData>(extentedCell){
						@Override
						public com.akjava.gwt.lib.client.widget.cell.StyledTextColumn.StyleAndLabel getStyleAndLabel(TimeTableData object) {
							String style="";
						
							TimeValue timeValue=new TimeValue((long)(object.getTime()+object.getWaitTime()));
							
							return new StyleAndLabel(style,timeValue.toMinuteString());
						}
						 
					 };
					 table.addColumn(endColumn,"End"); 
			
			
			
			
			
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
	 
	 
	 /*
	  * some how directly set table,not good at VerticalAlignment
	  */
	 VerticalPanel v=new VerticalPanel();
	 v.setHeight("180px");
	 v.setVerticalAlignment(ALIGN_TOP);
	 this.add(v);
	 
	 v.add(table);
	 
	 
	 this.add(uploadPanel);
	 this.add(downloadPanels);
	

	
	

	
	/*HorizontalPanel playerPanel=new HorizontalPanel();
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
	playerPanel.add(stop);*/
	
}
	protected void doPlay() {
		//old direct making way
		/*List<Double> times=Lists.newArrayList();
		List<Mbl3dExpression> expressions=Lists.newArrayList();
		
		Mbl3dExpressionFunctionWithModifier mbl3dExpressionFunctionWithEyeModifier=new Mbl3dExpressionFunctionWithModifier(Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().getMorphtargetsModifier());
		
		for(TimeTableData data:cellObjects.getDatas()){
			if(data.isReference()){
				int id=data.getReferenceId();
				
				double time=data.getTime();
				
				if(times.size()>0 && times.get(times.size()-1)>=time){
					LogUtils.log("skipped time, smaller than last time");
					continue;
				}
				
				
				// * -1 is used as clear
				 
				Mbl3dData mbl3dData=id==-1?new Mbl3dData():Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel().getDataById(id,data.isEnableBrows(),data.isEnableEyes(),data.isEnableMouth());
				Mbl3dExpression expression=mbl3dExpressionFunctionWithEyeModifier.apply(mbl3dData);
				
				expressions.add(expression);
				times.add(time/1000);//clip animation is second base,
			}else{
				//TODO
			}
		}
		
		AnimationClip clip=Mbl3dExpressionEntryPoint.INSTANCE.converToAnimationClip("test", times, expressions);*/
		
		AnimationKeyFrameBuilder builder=new AnimationKeyFrameBuilder(Mbl3dExpressionEntryPoint.INSTANCE.getDataListPanel());
		TimeTableDataBlock block=new TimeTableDataBlock(cellObjects.getDatas());
		builder.setKeys(Lists.newArrayList(block));
		AnimationKeyGroup group=builder.createGroup(block);
		
		JSParameter param=Mbl3dExpressionEntryPoint.INSTANCE.getMesh().getMorphTargetDictionary().cast();
		AnimationClip clip=group.converToAnimationClip("test",Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().getMorphtargetsModifier(),param);
	
		
		Mbl3dExpressionEntryPoint.INSTANCE.playAnimation(clip);
		
		
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

	public void setValue(List<TimeTableData> datas){
		cellObjects.unselect();//proble happen if selected and datas small than current.
		
		if(datas==null){
		cellObjects.setDatas(new ArrayList<TimeTableData>());
		cellObjects.update();
		editor.setValue(new TimeTableData());//for clear
		editor.setValue(null);//for disable
		}else{
		cellObjects.setDatas(datas);
		cellObjects.update();
		editor.setValue(new TimeTableData());//if already null never fire
		}
	}

	public void addData(TimeTableData data,boolean updateStorages) {
		cellObjects.addItem(data);
		onDataAdded(data);//link something
		
		if(updateStorages){//on initialize no need re-store
			storeData();
		}
	}
	
	public void storeData(){
		timeTableDataBlockPanel.storeData();
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
		timeTableDataBlockPanel.onDataUpdated(null);
	}
	public void onDataAdded(TimeTableData data){
		
		timeTableDataBlockPanel.onDataUpdated(null);
	}
	public void onDataUpdated(TimeTableData data){
		
		//TODO link something
		cellObjects.getSimpleCellTable().getCellTable().redraw();
		timeTableDataBlockPanel.onDataUpdated(null);
	}
	

	private boolean isLargerTime(TimeTableData data){
		int index=cellObjects.getDatas().indexOf(data);
		if(index==-1){
			return false;
		}
		double max=-1;
		for(int i=0;i<index;i++){
			//to avoid overwrap endtime is minus 1 millisecond
			double endTime=cellObjects.getDatas().get(i).calcurateEndTime();
			if(cellObjects.getDatas().get(i).getWaitTime()!=0){
				endTime--;
			}
			if(max<endTime){
				max=endTime;
			}
		}
		return data.getTime()>max;
	}
	
	public class TimeTableDataEditor extends VerticalPanel implements Editor<TimeTableData>,ValueAwareEditor<TimeTableData>{
		private TimeTableData value;
		private TextBox labelEditor;
		private MinuteTimeEditor timeEditor;
		private MinuteTimeEditor waittimeEditor;
		private IntegerBox referenceIdEditor;
		private LabeledInputRangeWidget2 ratioEditor;
		public IntegerBox getReferenceIdEditor() {
			return referenceIdEditor;
		}

		private CheckBox referenceEditor;
		
		private CheckBox enableEyesEditor;
		private CheckBox enableBrowsEditor;
		private CheckBox enableMouthEditor;

		
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
						
						HorizontalPanel waittimePanel=new HorizontalPanel();
						waittimePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(waittimePanel);
						Label waittimeLabel=new Label("WaitTime");
						waittimeLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						waittimeLabel.setWidth(labelWidth);
						waittimePanel.add(waittimeLabel);
						waittimeEditor=new MinuteTimeEditor();
			//timeEditor.setWidth("100px");
						waittimePanel.add(waittimeEditor);


						HorizontalPanel referenceIdPanel=new HorizontalPanel();
						referenceIdPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(referenceIdPanel);
						Label referenceIdLabel=new Label("ReferenceId");
						referenceIdLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						referenceIdLabel.setWidth(labelWidth);
						referenceIdPanel.add(referenceIdLabel);
						referenceIdEditor=new IntegerBox();
						referenceIdEditor.setWidth("50px");
						referenceIdPanel.add(referenceIdEditor);


						
						referenceEditor=new CheckBox("Reference");
						
						referenceIdPanel.add(referenceEditor);
						
						HorizontalPanel ratioPanel=new HorizontalPanel();
						ratioPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(ratioPanel);
						
						ratioEditor=new LabeledInputRangeWidget2("Ratio", 0.01, 1.0, 0.01);
						ratioEditor.getLabel().setWidth(labelWidth);
						ratioEditor.getRange().setWidth("170px");
						
						ratioPanel.add(ratioEditor);


						HorizontalPanel enablePanel=new HorizontalPanel();
						enablePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(enablePanel);
						
						enableBrowsEditor=new CheckBox("enable Brow");
						enablePanel.add(enableBrowsEditor);
						enableEyesEditor=new CheckBox("enable Eyes");
						enablePanel.add(enableEyesEditor);
						enableMouthEditor=new CheckBox("enable Mouth");
						enablePanel.add(enableMouthEditor);
		}


@Override
			public void setDelegate(EditorDelegate<TimeTableData> delegate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				
				
				value.setLabel(labelEditor.getValue());
				value.setTime(timeEditor.getValue());
				value.setWaitTime(waittimeEditor.getValue());
				value.setReferenceId(referenceIdEditor.getValue());
				value.setReference(referenceEditor.getValue());

				value.setEnableEyes(enableEyesEditor.getValue());
				value.setEnableBrows(enableBrowsEditor.getValue());
				value.setEnableMouth(enableMouthEditor.getValue());

				value.setRatio(ratioEditor.getValue());
				
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
					waittimeEditor.setEnabled(false);
					referenceIdEditor.setEnabled(false);
					referenceEditor.setEnabled(false);
					
					enableEyesEditor.setEnabled(false);
					enableBrowsEditor.setEnabled(false);
					enableMouthEditor.setEnabled(false);

					ratioEditor.setEnabled(false);
					return;
				}else{
					//set enable
					labelEditor.setEnabled(true);
					timeEditor.setEnabled(true);
					waittimeEditor.setEnabled(true);
					referenceIdEditor.setEnabled(true);
					referenceEditor.setEnabled(true);
					
					enableEyesEditor.setEnabled(true);
					enableBrowsEditor.setEnabled(true);
					enableMouthEditor.setEnabled(true);

					ratioEditor.setEnabled(true);
				}
				
				labelEditor.setValue(value.getLabel());
				timeEditor.setValue(value.getTime());
				waittimeEditor.setValue(value.getWaitTime());
				referenceIdEditor.setValue(value.getReferenceId());
				referenceEditor.setValue(value.isReference());

				
				enableEyesEditor.setValue(value.isEnableEyes());
				enableBrowsEditor.setValue(value.isEnableBrows());
				enableMouthEditor.setValue(value.isEnableMouth());
				ratioEditor.setValue(value.getRatio());

			}
	}

	public void setReferenceId(int id) {
		if(editor.getValue()==null){
			return;
		}
		//force update
		editor.getValue().setReferenceId(id);
		editor.getReferenceIdEditor().setValue(id);
		
		
	}
}
