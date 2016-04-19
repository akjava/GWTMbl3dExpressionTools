package com.akjava.mbl3d.expression.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageDataList;
import com.akjava.gwt.lib.client.datalist.SimpleTextData;
import com.akjava.gwt.lib.client.datalist.SimpleTextDatasOwner;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.lib.common.utils.ValuesUtils;
import com.akjava.mbl3d.expression.client.datalist.CellTableResources;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataComparator;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataComparatorValueBox;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataComparatorValueBox.Mbl3dDataComparatorValue;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataEditor;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataSimpleTextConverter;
import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DataListPanel extends VerticalPanel implements SimpleTextDatasOwner{
	public static DateTimeFormat dateFormat=DateTimeFormat.getFormat("yy/MM/dd HH:mm");
	
	private StorageDataList dataList;
	private EasyCellTableObjects<Mbl3dData> dataObjects;
	private BasicExpressionPanel mbl3dExpressionSetter;
	private Mbl3dDataEditor editor;

	protected boolean playAnimationOnSelect;

	protected boolean filterBrow=true;
	protected boolean filterEyes=true;
	protected boolean filterMouth=true;
	
	private Mbl3dExpression currentSelectedExpression;
	public DataListPanel(StorageControler storageControler,final BasicExpressionPanel mbl3dExpressionSetter){
		dataList = new StorageDataList(storageControler,StorageKeys.DATA_LIST_KEY);
		this.mbl3dExpressionSetter=mbl3dExpressionSetter;
		
		
		final VerticalPanel editorPanel=new VerticalPanel();
		THREE.XHRLoader().load("models/mbl3d/emotions.csv", new XHRLoadHandler() {
			

			@Override
			public void onLoad(String text) {
				List<Emotion> emotions=new EmotionCsvConverter().convert(text);
				
				editor = new Mbl3dDataEditor(emotions);    
				driver.initialize(editor);
				
				
				driver.edit(null);
				editorPanel.add(editor);
				
			    	Button updateBt=new Button("Update",new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						doUpdate();
					}

					
				});
			    	updateBt.setWidth("100px");
			    	
			    	HorizontalPanel bts=new HorizontalPanel();
			    	editorPanel.add(bts);
			    	bts.add(updateBt);
			    	
			    	Button removeBt=new Button("Remove",new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							boolean result=Window.confirm("delete selection?");
							if(!result){
								return;
							}
							Mbl3dData data=driver.flush();
							if(data!=null){
								dataList.clearData(data.getId());
								dataObjects.removeItem(data);
								updateListData();
							}
						}
					});
			    	bts.add(removeBt);
			    	
			    	comparator.setEmotions(emotions);
			    	
			    	updateListData();
				
			}
		});

		
	    
	    add(editorPanel);
	    
	    CellTableResources.INSTANCE.cellTableStyle().ensureInjected();
		//read
		SimpleCellTable<Mbl3dData> table=new SimpleCellTable<Mbl3dData>(20,CellTableResources.INSTANCE) {
			@Override
			public void addColumns(CellTable<Mbl3dData> table) {
				
				
				
				TextColumn<Mbl3dData> typeColumn=new TextColumn<Mbl3dData>() {
					@Override
					public String getValue(Mbl3dData object) {
						if(object.getType()!=null){
							return object.getType();
						}
						return "";
					}
				};
				table.addColumn(typeColumn);
				
				TextColumn<Mbl3dData> nameColumn=new TextColumn<Mbl3dData>() {
					@Override
					public String getValue(Mbl3dData object) {
						if(object.getName()!=null){
							return object.getName();
						}
						
						
						
						return "";
					}
				};
				table.addColumn(nameColumn);
				
				
				TextColumn<Mbl3dData> descriptionColumn=new TextColumn<Mbl3dData>() {
					@Override
					public String getValue(Mbl3dData object) {
						if(object.getDescription()!=null){
							return object.getDescription();
						}
						
						if(object.getName()==null && object.getType()==null){
							return dateFormat.format(new Date(object.getCdate()));
						}
						
						return "";
					}
				};
				table.addColumn(descriptionColumn);
				table.setColumnWidth(2, "200px");
				
				
				/*
				TextColumn<Mbl3dData> dateColumn=new TextColumn<Mbl3dData>() {
					@Override
					public String getValue(Mbl3dData object) {
						return dateFormat.format(new Date(object.getCdate()));
					}
				};
				table.addColumn(dateColumn);
				*/
			}
		};
		//help navigato
		table.getControlPanel().add(new Button("Next",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Mbl3dData selection=dataObjects.getSelection();
				if(selection==null){
					Optional<Mbl3dData> first=dataObjects.getFirst();
					for(Mbl3dData data:first.asSet()){
						dataObjects.setSelected(data, true);
					}
				}
				
				Optional<Mbl3dData> next=dataObjects.getNext(selection);
				for(Mbl3dData data:next.asSet()){
					dataObjects.setSelected(data, true);
				}
				updateListData();//for update selection;
			}
		}));
		
		dataObjects = new EasyCellTableObjects<Mbl3dData>(table) {
			

			@Override
			public void onSelect(Mbl3dData selection) {
				
				driver.edit(selection);
				
				currentSelectedExpression = convertToExpression(selection);
				
				mbl3dExpressionSetter.setOverwriteEnable(true);
				
				
				updateRangeAndAnimation();
				
			}
		};
		
		
		comparator=new Mbl3dDataComparator();
		comparator.setOrder(Mbl3dDataComparator.ORDER_ID_DESC);
		//load
		initializeListData();
		
		add(table);
		
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		add(panel);
		panel.add(new Label("Order:"));
		
	
		
		Mbl3dDataComparatorValueBox sortBox = new Mbl3dDataComparatorValueBox();
		panel.add(sortBox);
		sortBox.addValueChangeHandler(new ValueChangeHandler<Mbl3dDataComparatorValueBox.Mbl3dDataComparatorValue>() {
			@Override
			public void onValueChange(ValueChangeEvent<Mbl3dDataComparatorValue> event) {
				if(comparator==null){
					return;
				}
				comparator.setOrder(event.getValue().getMode());
				updateListData();
			}
		});
		
		CheckBox autoMovePageCheck=new CheckBox("autoMovePage");
		autoMovePageCheck.setValue(true);
		
		autoMovePageCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				autoMovePage=event.getValue();
			}
		});
		panel.add(autoMovePageCheck);
		
		
		HorizontalPanel animationPanel=new HorizontalPanel();
		animationPanel.setVerticalAlignment(ALIGN_MIDDLE);
		add(animationPanel);
		
		this.add(new Label("Animation:"));
		
		final CheckBox filterBrowCheck=new  CheckBox("brow");
		filterBrowCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				filterBrow=event.getValue();
				updateRangeAndAnimation();
			}
			
		});
		filterBrowCheck.setValue(true);
		
		final CheckBox filterEyesCheck=new  CheckBox("eyes");
		filterEyesCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				filterEyes=event.getValue();
				updateRangeAndAnimation();
			}
			
		});
		filterEyesCheck.setValue(true);
		
		final CheckBox filterMouthCheck=new  CheckBox("mouth");
		filterMouthCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				filterMouth=event.getValue();
				updateRangeAndAnimation();
			}
			
		});
		filterMouthCheck.setValue(true);
		filterBrowCheck.setEnabled(false);
		filterEyesCheck.setEnabled(false);
		filterMouthCheck.setEnabled(false);
		
		CheckBox playAnimationCheck=new  CheckBox("play animtion");
		playAnimationCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				playAnimationOnSelect=event.getValue();
				if(event.getValue()){
					filterBrowCheck.setEnabled(true);
					filterEyesCheck.setEnabled(true);
					filterMouthCheck.setEnabled(true);
					
					//set value instead call updateRangeAndAnimation();
					filterBrowCheck.setValue(true);
					filterEyesCheck.setValue(true);
					filterMouthCheck.setValue(true);
					filterMouth=true;
					filterEyes=true;
					filterBrow=true;
				}else{
					filterBrowCheck.setEnabled(false);
					filterEyesCheck.setEnabled(false);
					filterMouthCheck.setEnabled(false);
				}
				updateRangeAndAnimation();
			}
		});
		animationPanel.add(playAnimationCheck);
		animationPanel.add(filterBrowCheck);
		animationPanel.add(filterEyesCheck);
		animationPanel.add(filterMouthCheck);
		
		
		HorizontalPanel toolsPanel=new HorizontalPanel();
		toolsPanel.setVerticalAlignment(ALIGN_MIDDLE);
		add(toolsPanel);
		
		toolsPanel.add(new Label("Tools:"));
		
		final HorizontalPanel dlPanel=new HorizontalPanel();
		dlPanel.setSpacing(4);
		
		Button imageBt=new Button("Image",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//still confusing how to handle non indexed data.
				String fileBaseName="unknown";
				Mbl3dData selection=dataObjects.getSelection();
				if(selection==null){
					Window.alert("export only selection");
					return;
				}
				
				if(selection!=null && selection.getName()!=null){
					fileBaseName=selection.getName();
				}
				
				String url=Mbl3dExpressionEntryPoint.INSTANCE.toImageDataUrl();
				Anchor a=HTML5Download.get().generateBase64DownloadLink(url, "image/png", fileBaseName+".png", "Download", true);
				dlPanel.clear();
				dlPanel.add(a);
			}
		});
		toolsPanel.add(imageBt);
		Button jsonBt=new Button("JSON",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String fileBaseName="unknown";
				Mbl3dData selection=dataObjects.getSelection();
				if(selection==null){
					Window.alert("export only selection");
					return;
				}
				
				if(selection!=null && selection.getName()!=null){
					fileBaseName=selection.getName();
				}
				
				//new Mbl
				Mbl3dExpression expression=convertToExpression(selection);
				String json=new Mbl3dExpressionConverter().reverse().convert(expression);
				
				
				
				Anchor a=HTML5Download.get().generateTextDownloadLink(json, fileBaseName+".json", "Download", true);
				dlPanel.clear();
				dlPanel.add(a);
			}
		});
		toolsPanel.add(jsonBt);
		toolsPanel.add(dlPanel);
		
	}
	protected void updateRangeAndAnimation() {
		Mbl3dExpressionEntryPoint.INSTANCE.stopAnimation();
		
		mbl3dExpressionSetter.setMbl3dExpression(currentSelectedExpression);
		
		if(currentSelectedExpression==null){
			return;
		}
		
		if(playAnimationOnSelect){
			Mbl3dExpressionEntryPoint.INSTANCE.playAnimation(currentSelectedExpression,filterBrow,filterEyes,filterMouth);
		}
	}
	private Mbl3dDataComparator comparator;
	
	public void initializeListData(){
		List<Mbl3dData> datas=Lists.newArrayList(
				new Mbl3dDataSimpleTextConverter().convertAll(dataList.getDataList())
				);
		
		
		dataObjects.setDatas(datas);
		updateListData();
	}
	
	private boolean autoMovePage=true;
	protected void updateListData() {
		int currentPage=dataObjects.getSimpleCellTable().getPager().getPage();
		
		Collections.sort(dataObjects.getDatas(), comparator);
		dataObjects.update();
		
		//test
		Stopwatch watch=Stopwatch.createStarted();
		for(Mbl3dData data:dataObjects.getDatas()){
			//String key=getEyesKey(data);
			//LogUtils.log(key);
		}
		//LogUtils.log(watch.elapsed(TimeUnit.MILLISECONDS));
		
		
		if(autoMovePage){
		//fix page selection
		if(dataObjects.isSelected()){
			int index=dataObjects.getSelectedIndex().get();
			int pageSize=dataObjects.getSimpleCellTable().getPager().getPageSize();
			
			//LogUtils.log(index+","+pageSize);
			
			if(index>=pageSize){
				int page=index/pageSize;
				dataObjects.getSimpleCellTable().getPager().setPage(page);
				//LogUtils.log(page);
			}
		}
		}else{
			//possible bug if deleted go first page
			int needSize=currentPage*dataObjects.getSimpleCellTable().getPager().getPageSize();
			if(dataObjects.getDatas().size()>=needSize){
				dataObjects.getSimpleCellTable().getPager().setPage(currentPage);
			}
		}
	}

	private void doUpdate() {
		Mbl3dData data=driver.flush();
		if(data!=null){
			//write storage
			SimpleTextData textData=new Mbl3dDataSimpleTextConverter().reverse().convert(data);
			dataList.updateData(textData);
			
			updateListData();
		}
	}
	
	
	//I'm not sure split 2 classes?

	
	
	private Map<String,String> mblb3dExpressionToMap(Mbl3dExpression expression){
		Map<String,String> values=Maps.newHashMap();
		for(String key:expression.getKeys()){
			values.put(key, String.valueOf(expression.get(key)));
		}
		return values;
	}
	
	public void add(Mbl3dExpression expression,String name,String type,String description){
		
		Map<String,String> values=mblb3dExpressionToMap(expression);
		Mbl3dData data=new Mbl3dData(name,type,description,values);
		data.setCdate(System.currentTimeMillis());
	
		SimpleTextData textData=new Mbl3dDataSimpleTextConverter().reverse().convert(data);
		int id=dataList.addData(textData);
		
		data.setId(id);
		
		dataObjects.addItem(data);
		dataObjects.setSelected(data, true);
	
		updateListData();//sort
	}
	
	public Mbl3dExpression convertToExpression(@Nullable Mbl3dData data){
		Mbl3dExpression expression=new Mbl3dExpression("");
		if(data!=null){
		for(String key:data.getValues().keySet()){
			if(key.equals("type")){
				continue;
			}
			if(key.equals("description")){
				continue;
			}
			if(key.equals("name")){
				continue;
			}
			expression.set(key, ValuesUtils.toDouble(data.getValues().get(key), 0));
		}
		}
		return expression;
	}
	
	
	
	
	
	 interface Driver extends SimpleBeanEditorDriver< Mbl3dData,  Mbl3dDataEditor> {}
	 Driver driver = GWT.create(Driver.class);

	




	public void overwrite(Mbl3dExpression expression) {
		if(editor.getValue()==null){
			Window.alert("no data selected.click store button to add");
			return;
		}
		editor.getValue().setValues(mblb3dExpressionToMap(expression));
		//editor.getValue().
		doUpdate();
	}
	@Override
	public StorageDataList getStorageDataList() {
		return dataList;
	}
}
