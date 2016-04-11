package com.akjava.mbl3d.expression.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageDataList;
import com.akjava.gwt.lib.client.datalist.SimpleTextData;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.lib.common.utils.ValuesUtils;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataComparator;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataComparatorValueBox;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataComparatorValueBox.Mbl3dDataComparatorValue;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataEditor;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataSimpleTextConverter;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DataListPanel extends VerticalPanel{
	public static DateTimeFormat dateFormat=DateTimeFormat.getFormat("yy/MM/dd HH:mm");
	
	private StorageDataList dataList;
	private EasyCellTableObjects<Mbl3dData> dataObjects;
	private BasicExpressionPanel mbl3dExpressionSetter;
	private Mbl3dDataEditor editor;
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
							}
						}
					});
			    	bts.add(removeBt);
			    	
			    	comparator=new Mbl3dDataComparator(emotions);
			    	updateListData();
				
			}
		});

		
	    
	    add(editorPanel);
	    
		
		//read
		SimpleCellTable<Mbl3dData> table=new SimpleCellTable<Mbl3dData>(20) {
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
		
		dataObjects = new EasyCellTableObjects<Mbl3dData>(table) {
			@Override
			public void onSelect(Mbl3dData selection) {
				
				driver.edit(selection);
				
				mbl3dExpressionSetter.setMbl3dExpression(convertToExpression(selection));
				mbl3dExpressionSetter.setOverwriteEnable(true);
			}
		};
		
		
		
		//load
		List<Mbl3dData> datas=Lists.newArrayList(
				new Mbl3dDataSimpleTextConverter().convertAll(dataList.getDataList())
				);
		
		//datas.add(new Mbl3dData("hello", "world", null));
		//datas.add(new Mbl3dData("hello2", "world", null));
		
		dataObjects.setDatas(datas);
		dataObjects.update();
		
		add(table);
		
		HorizontalPanel panel=new HorizontalPanel();
		add(panel);
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
	}
	private Mbl3dDataComparator comparator;
	
	protected void updateListData() {
		Collections.sort(dataObjects.getDatas(), comparator);
		dataObjects.update();
	}

	private void doUpdate() {
		Mbl3dData data=driver.flush();
		if(data!=null){
			SimpleTextData textData=new Mbl3dDataSimpleTextConverter().reverse().convert(data);
			
			dataList.updateData(textData);
			updateListData();
		}
	}
	
	
	//I'm not sure split 2 classes?

	
	
	private Map<String,String> mblb3dExpressionToMap(Mblb3dExpression expression){
		Map<String,String> values=Maps.newHashMap();
		for(String key:expression.getKeys()){
			values.put(key, String.valueOf(expression.get(key)));
		}
		return values;
	}
	
	public void add(Mblb3dExpression expression,String name,String type,String description){
		Map<String,String> values=mblb3dExpressionToMap(expression);
		Mbl3dData data=new Mbl3dData(name,type,description,values);
		data.setCdate(System.currentTimeMillis());
		
		SimpleTextData textData=new Mbl3dDataSimpleTextConverter().reverse().convert(data);
		int id=dataList.addData(textData);
		
		data.setId(id);
		
		dataObjects.addItem(data);
		dataObjects.setSelected(data, true);
	}
	
	public Mblb3dExpression convertToExpression(@Nullable Mbl3dData data){
		Mblb3dExpression expression=new Mblb3dExpression("");
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

	

	public void onModuleLoad() {
		


	
	
	
	
}

	


	public void overwrite(Mblb3dExpression expression) {
		if(editor.getValue()==null){
			Window.alert("no data selected.click store button to add");
			return;
		}
		editor.getValue().setValues(mblb3dExpressionToMap(expression));
		//editor.getValue().
		doUpdate();
	}
}
