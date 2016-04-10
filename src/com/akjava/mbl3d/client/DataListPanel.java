package com.akjava.mbl3d.client;

import java.io.IOException;
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
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DataListPanel extends VerticalPanel{
	public static DateTimeFormat dateFormat=DateTimeFormat.getFormat("yy/MM/dd HH:mm");
	
	private StorageDataList dataList;
	private EasyCellTableObjects<Mbl3dData> dataObjects;
	private Mbl3dExpressionSetter mbl3dExpressionSetter;
	private Mbl3dDataEditor editor;
	public DataListPanel(StorageControler storageControler,final Mbl3dExpressionSetter mbl3dExpressionSetter){
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
				
				TextColumn<Mbl3dData> descriptionColumn=new TextColumn<Mbl3dData>() {
					@Override
					public String getValue(Mbl3dData object) {
						if(object.getDescription()!=null){
							return object.getDescription();
						}
						
						
						
						return "";
					}
				};
				table.addColumn(descriptionColumn);
				
				TextColumn<Mbl3dData> dateColumn=new TextColumn<Mbl3dData>() {
					@Override
					public String getValue(Mbl3dData object) {
						return dateFormat.format(new Date(object.getCdate()));
					}
				};
				table.addColumn(dateColumn);
			}
		};
		
		dataObjects = new EasyCellTableObjects<DataListPanel.Mbl3dData>(table) {
			@Override
			public void onSelect(Mbl3dData selection) {
				
				driver.edit(selection);
				
				mbl3dExpressionSetter.setMbl3dExpression(convertToExpression(selection));
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
	}
	
	private void doUpdate() {
		Mbl3dData data=driver.flush();
		if(data!=null){
			String text=new Mbl3dDataConverter().reverse().convert(data);
			dataList.updateData(data.getId(), "", text);
			dataObjects.update();
		}
	}
	
	public static class Mbl3dDataSimpleTextConverter extends Converter<SimpleTextData,Mbl3dData>{
		Mbl3dDataConverter converter=new Mbl3dDataConverter();
		@Override
		protected Mbl3dData doForward(SimpleTextData data) {
			Mbl3dData mData=converter.convert(data.getData());
			mData.setId(data.getId());
			mData.setCdate(data.getCdate());
			return mData;
		}

		@Override
		protected SimpleTextData doBackward(Mbl3dData data) {
			SimpleTextData sData=new SimpleTextData("",converter.reverse().convert(data));
			sData.setId(data.getId());
			sData.setCdate(data.getCdate());
			return sData;
		}
		
	}
	//I'm not sure split 2 classes?
	public static class Mbl3dDataConverter extends Converter<String,Mbl3dData>{
		public static final MapSplitter splitter=Splitter.on("\t").withKeyValueSeparator("\f");
		public static final MapJoiner joiner=Joiner.on("\t").withKeyValueSeparator("\f");
		@Override
		protected Mbl3dData doForward(String text) {
			Map<String,String> map=Maps.newHashMap(splitter.split(text));
			
			String type=map.remove("type");
			String description=map.remove("description");
			
			
			return new Mbl3dData(type, description, map);
		}

		@Override
		protected String doBackward(Mbl3dData data) {
			Map<String,String> copy=Maps.newHashMap(data.getValues());
			if(data.getType()!=null){
				copy.put("type", data.getType());
			}
			if(data.getDescription()!=null){
				copy.put("description", data.getDescription());
			}
			
			return joiner.join(copy);
		}	
	}
	
	
	private Map<String,String> mblb3dExpressionToMap(Mblb3dExpression expression){
		Map<String,String> values=Maps.newHashMap();
		for(String key:expression.getKeys()){
			values.put(key, String.valueOf(expression.get(key)));
		}
		return values;
	}
	
	public void add(Mblb3dExpression expression,String type,String description){
		Map<String,String> values=mblb3dExpressionToMap(expression);
		Mbl3dData data=new Mbl3dData(type,description,values);
		data.setCdate(System.currentTimeMillis());
		String text=new Mbl3dDataConverter().reverse().convert(data);
		int id=dataList.addData("", text);
		data.setId(id);
		
		dataObjects.addItem(data);
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
			expression.set(key, ValuesUtils.toDouble(data.getValues().get(key), 0));
		}
		}
		return expression;
	}
	
	
	public static class Mbl3dData{
		private long cdate;
		public long getCdate() {
			return cdate;
		}
		public void setCdate(long cdate) {
			this.cdate = cdate;
		}
		private int id;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		private String type;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Map<String, String> getValues() {
			return values;
		}
		public void setValues(Map<String, String> values) {
			this.values = values;
		}
		public Mbl3dData(String type, String description, Map<String, String> values) {
			super();
			this.type = type;
			this.description = description;
			this.values = values;
		}
		private String description;
		private Map<String,String> values;
		
		public String toString(){
			return type+","+description;
		}
	}
	
	 interface Driver extends SimpleBeanEditorDriver< Mbl3dData,  Mbl3dDataEditor> {}
	 Driver driver = GWT.create(Driver.class);

	public void onModuleLoad() {
		


	
	
	
	
}

	
	public class Mbl3dDataEditor extends VerticalPanel implements Editor<Mbl3dData>,ValueAwareEditor<Mbl3dData>{
		/*
		 * watch out start with null
		 */
		private List<Emotion> emotions;
		private ValueListBox<Emotion> emotionsBox;
		private TextBox descriptionBox;
		private Mbl3dData value;
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
				
				value.setType(type);
				value.setDescription(description);
				
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
					
					emotionsBox.setValue(null);
					descriptionBox.setValue("");
					return;
				}else{
					emotionsBox.setEnabled(true);
					descriptionBox.setEnabled(true);
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
				
			}
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
