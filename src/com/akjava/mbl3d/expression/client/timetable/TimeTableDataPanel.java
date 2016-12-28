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
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
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
			TextColumn<TimeTableData> nameColumn=new TextColumn<TimeTableData>() {
				@Override
				public String getValue(TimeTableData object) {
					return object.getLabel();
				}
			};
			table.addColumn(nameColumn);
			
			TextColumn<TimeTableData> typeColumn=new TextColumn<TimeTableData>() {
				@Override
				public String getValue(TimeTableData object) {
					//TODO convert time label
					return ""+object.getTime();
				}
			};
			table.addColumn(typeColumn);
		}
	};
	this.add(table);
	
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
			}else{
				addData(newData,true);
				editor.setValue(new TimeTableData());//for continue data
			//cellObjects.setSelected(newData, true);
			}
			
		}
	});
	addOrUpdateBt.setWidth("80px");
	buttons.add(addOrUpdateBt);
	
	Button copyBt=new Button("copy",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			TimeTableData selection=cellObjects.getSelection();
			if(selection==null){
				return;
			}
			TimeTableData newData=copy(selection);
			addData(newData,true);
			cellObjects.setSelected(newData, true);
			
		}
	});
	buttons.add(copyBt);
	

	
	Button removeBt=new Button("remove",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			
			removeData(cellObjects.getSelection());
			
		}
	});
	buttons.add(removeBt);
	
	Button removeAll=new Button("remove All",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			boolean confirm=Window.confirm("remove all?");
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
	protected String getDownloadFileName() {
		return baseFileName+".csv";
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
		
		if(updateStorages){
			storeData();
		}
	}
	
	public void storeData(){
		 //store data
		 String lines=toStoreText();
		 LogUtils.log(lines);
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
	if(selection==null){
		newBt.setEnabled(false);
		editor.setValue(new TimeTableData());
		addOrUpdateBt.setText("Add");
	}else{
		addOrUpdateBt.setText("Update");
		newBt.setEnabled(true);
		editor.setValue(selection);
	}
	}

	public void onDataRemoved(TimeTableData data){
		
	}
	public void onDataAdded(TimeTableData data){
		
	}
	public void onDataUpdated(TimeTableData data){
		//TODO link something
		cellObjects.getSimpleCellTable().getCellTable().redraw();
	}
	

	
	public class TimeTableDataEditor extends VerticalPanel implements Editor<TimeTableData>,ValueAwareEditor<TimeTableData>{
		private TimeTableData value;
		private TextBox labelEditor;
		private DoubleBox timeEditor;
		private IntegerBox referenceIdEditor;
		private CheckBox referenceEditor;

		public TimeTableData getValue() {
			return value;
		}
		
		public TimeTableDataEditor(){
			String labelWidth="180px";
			int fontSize=14;

						HorizontalPanel labelPanel=new HorizontalPanel();
						labelPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(labelPanel);
						Label labelLabel=new Label("Label");
						labelLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						labelLabel.setWidth(labelWidth);
						labelPanel.add(labelLabel);
						labelEditor=new TextBox();
			labelEditor.setWidth("100px");
						labelPanel.add(labelEditor);


						HorizontalPanel timePanel=new HorizontalPanel();
						timePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
						add(timePanel);
						Label timeLabel=new Label("Time");
						timeLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
						timeLabel.setWidth(labelWidth);
						timePanel.add(timeLabel);
						timeEditor=new DoubleBox();
			timeEditor.setWidth("100px");
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
