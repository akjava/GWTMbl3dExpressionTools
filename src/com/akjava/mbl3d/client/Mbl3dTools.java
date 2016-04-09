package com.akjava.mbl3d.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.experimental.AsyncMultiCaller;
import com.akjava.gwt.lib.client.widget.EnterKeySupportTextBox;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.JSParameter;
import com.akjava.gwt.three.client.gwt.core.BoundingBox;
import com.akjava.gwt.three.client.gwt.loader.JSONLoaderObject;
import com.akjava.gwt.three.client.gwt.renderers.WebGLRendererParameter;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.lights.AmbientLight;
import com.akjava.gwt.three.client.js.lights.DirectionalLight;
import com.akjava.gwt.three.client.js.loaders.JSONLoader;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.materials.MeshBasicMaterial;
import com.akjava.gwt.three.client.js.materials.MultiMaterial;
import com.akjava.gwt.three.client.js.math.THREEMath;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.lib.common.utils.ValuesUtils;
import com.akjava.mbl3d.client.Mblb3dExpression.ClosedResult;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Mbl3dTools extends SimpleThreeAppEntryPoint {

	private ValueListBox<Mblb3dExpression> expressionsListBox;
	private HorizontalPanel downloadPanel;
	private SkinnedMesh mesh;
	
	private Map<String,LabeledInputRangeWidget2> ranges;
	private List<Mblb3dExpression> expressionList;
	
	private VerticalPanel gui;
	private CombinedExpression combinedExpression;
	private IntegerBox indexBox;
	
	@Override
	public WebGLRendererParameter createRendererParameter() {
		return GWTParamUtils.WebGLRenderer().preserveDrawingBuffer(true);
	}

	@Override
	public void onInitializedThree() {
		renderer.setClearColor(0xffffff);//default is black?
		
		/*
		rendererContainer.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				onDocumentMouseMove(event);
			}
		});
		*/
		//light
		AmbientLight ambient = THREE.AmbientLight( 0xeeeeee );//var ambient = new THREE.AmbientLight( 0xffffff );
		scene.add( ambient );

		DirectionalLight directionalLight = THREE.DirectionalLight( 0x333333 );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		directionalLight.getPosition().set( -1, 1, 1 ).normalize();//directionalLight.position.set( -1, 1, 1 ).normalize();
		scene.add( directionalLight );
		
		//String url= "models/mbl3d/morph.json";//var url= "morph.json";
				String url= "models/mbl3d/morph.json";//var url= "morph.json";
				THREE.XHRLoader().load(url,new XHRLoadHandler() {
					

					@Override
					public void onLoad(String text) {
						/*
						 * fix morphtargets vertices
						 * wrong [[x,y,z]] [x,y,z] & x-up
						 */
					
						final JSONValue json=JSONParser.parseStrict(text);
						JSONObject jsonObj=json.isObject();
						JSONValue jsonMorph=jsonObj.get("morphTargets");
						JSONArray morphArray=jsonMorph.isArray();
						for(int i=0;i<morphArray.size();i++){
							JSONValue jsonMorphTarget=morphArray.get(i);
							JSONObject morphTarget=jsonMorphTarget.isObject();
							
							JSONValue jsonVertices=morphTarget.get("vertices");
							JSONArray vertices=jsonVertices.isArray();
							
							JsArrayNumber fixed=convertMorphVertices(vertices);
							
							JSONArray fixedArray=new JSONArray(fixed);
							
							morphTarget.put("vertices", fixedArray);
						}
						
						JSONLoader jsonLoader=THREE.JSONLoader();
						JSONLoaderObject loadedObject=jsonLoader.parse(jsonObj.getJavaScriptObject());
						
						Geometry geometry=loadedObject.getGeometry();
						
						geometry.computeBoundingBox();
						BoundingBox bb = geometry.getBoundingBox();
						double x=-20, y=-1270,z= -300,s= 800;

						//double x=-0, y=-0,z= -0,s= 1;
						
						JsArray<Material> materials=loadedObject.getMaterials();
						for(int i=0;i<materials.length();i++){
							MeshBasicMaterial m=materials.get(i).cast();//need cast GWT problem
							m.setMorphTargets(true);
						}
						
						MultiMaterial mat=THREE.MultiMaterial(materials );//var mat=THREE.MultiMaterial( materials);//MultiMaterial mat=THREE.MultiMaterial( materials);//var mat=new THREE.MultiMaterial( materials);


						mesh = THREE.SkinnedMesh( geometry, mat );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = new THREE.SkinnedMesh( geometry, mat );
						mesh.setName("model");//mesh.setName("model");//mesh.setName("model");//mesh.name = "model";
						mesh.getPosition().set( x, y - bb.getMin().getY() * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.position.set( x, y - bb.min.y * s, z );
						mesh.getScale().set( s, s, s );//mesh.getScale().set( s, s, s );//mesh.getScale().set( s, s, s );//mesh.scale.set( s, s, s );
						scene.add( mesh );
						
						//mesh.setVisible(false);
						
						//temp test
						
						final double fx=x;
						final double fy=y - bb.getMin().getY() * s;
						final double fz=z;
						final double fs=s;
						
						//TODO init ui;
						initUi();
						
						
						jsonLoader.load("models/mbl3d/onlyhair.json", new JSONLoadHandler() {
							
							/*
							 * take care of materials.
							 * must export with face-colors.
							 * 
							 */
							@Override
							public void loaded(Geometry geometry, JsArray<Material> materials) {
								geometry.computeBoundingBox();
								BoundingBox bb = geometry.getBoundingBox();
								//LogUtils.log(bb);
								
								Mesh hair = THREE.Mesh( geometry, THREE.MeshLambertMaterial(GWTParamUtils.MeshLambertMaterial().color(0x553817)) );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = new THREE.SkinnedMesh( geometry, mat );
								
								//Mesh hair = THREE.Mesh( geometry, THREE.MultiMaterial(materials) );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = new THREE.SkinnedMesh( geometry, mat );
								//mesh.setName("model");//mesh.setName("model");//mesh.name = "model";
								hair.getPosition().set( fx, fy,fz );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.position.set( x, y - bb.min.y * s, z );
								hair.getScale().set( fs, fs, fs );//mesh.getScale().set( s, s, s );//mesh.getScale().set( s, s, s );//mesh.scale.set( s, s, s );
								scene.add( hair );
								
							}
						});
						
						
						
					}
		
	});
	}
				
				private void initUi() {
					gui=createRightTopPopup();
					
					TabPanel tab=new TabPanel();
					gui.add(tab);
					
					
					VerticalPanel basic=new VerticalPanel();
					tab.add(basic,"Basic");
					
					
					
					ranges=Maps.newHashMap();
					Label expression=new Label("Expression");
					
					basic.add(expression);
					
					expressionsListBox = new ValueListBox<Mblb3dExpression>(new Renderer<Mblb3dExpression>() {
						@Override
						public String render(Mblb3dExpression object) {
							if(object!=null){
								return object.getName();
							}
							return "";
						}

						@Override
						public void render(Mblb3dExpression object, Appendable appendable) throws IOException {
							// TODO Auto-generated method stub
							
						}
					});
					expressionsListBox.addValueChangeHandler(new ValueChangeHandler<Mblb3dExpression>() {
						@Override
						public void onValueChange(ValueChangeEvent<Mblb3dExpression> event) {
							
							setMbl3dExpression(event.getValue());
							
						}
					});
					
					HorizontalPanel hpanel=new HorizontalPanel();
					hpanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
					basic.add(hpanel);
					
					hpanel.add(expressionsListBox);
					
					Button prev=new Button("Prev",new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							Mblb3dExpression expression= expressionsListBox.getValue();
							int index=expressionList.indexOf(expression);
							index--;
							if(index<0){
								index=expressionList.size()-1;
							}
							expressionsListBox.setValue(expressionList.get(index),true);
						}
					});
					hpanel.add(prev);
					
					Button next=new Button("Next",new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							Mblb3dExpression expression= expressionsListBox.getValue();
							int index=expressionList.indexOf(expression);
							index++;
							if(index>=expressionList.size()){
								index=0;
							}
							expressionsListBox.setValue(expressionList.get(index),true);
						}
					});
					hpanel.add(next);
					
					Button shot=new Button("test",new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							doShot();
						}

						
					});
					hpanel.add(shot);
					
					downloadPanel = new HorizontalPanel();
					hpanel.add(downloadPanel);
					
					
					
					
					
					Label morph=new Label("Morph");
					basic.add(morph);
					
					JSParameter param=mesh.getMorphTargetDictionary().cast();
					
					String debug="";//for get key all
					
					JsArrayString keys=param.getKeys();
					for(int i=0;i<keys.length();i++){
						String key=keys.get(i);
						final int index=param.getInt(key);
						String originKey=key;
						key=key.substring("Expressions_".length());
						HorizontalPanel inputPanel=new HorizontalPanel();
						final ToggleButton toggle=new ToggleButton(key, new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								//toggleClicked((ToggleButton)event.getSource());
							}

							
						});
						inputPanel.add(toggle);
						
						LabeledInputRangeWidget2 inputRange=new LabeledInputRangeWidget2(key, 0, 1, 0.01);
						inputRange.getLabel().setVisible(false);
						inputPanel.add(inputRange);
						
						ranges.put(originKey, inputRange);
						inputRange.getTextBox().setHeight("12px");
						
						
						debug+=key+"\n";
						
						
						inputRange.setValue(0);
						basic.add(inputPanel);
						inputRange.addtRangeListener(new ValueChangeHandler<Number>() {
							@Override
							public void onValueChange(ValueChangeEvent<Number> event) {
								mesh.getMorphTargetInfluences().set(index, event.getValue().doubleValue());
							}
						});
					}
					//LogUtils.log(debug);
					
					THREE.XHRLoader().load("models/mbl3d/expressions.txt",new XHRLoadHandler() {

						

						@Override
						public void onLoad(String text) {
							expressionList = Lists.newArrayList();
							expressionList.add(null);//empty
							
							List<String> names=CSVUtils.splitLinesWithGuava(text);
							
							AsyncMultiCaller<String> caller=new AsyncMultiCaller<String>(names) {
								@Override
								public void doFinally(boolean cancelled) {
									expressionsListBox.setValue(expressionList.get(0));
									expressionsListBox.setAcceptableValues(expressionList);
									
									//create graph
									//pringDebug(expressionList);
								}

								@Override
								public void execAsync(final String fileName) {
									THREE.XHRLoader().load("models/mbl3d/expressions/"+fileName,new XHRLoadHandler() {

										@Override
										public void onLoad(String text) {
											Mblb3dExpression expression=new Mbl3dExpressionConverter().convert(text);
											expression.setName(FileNames.getRemovedExtensionName(fileName));
											expressionList.add(expression);
											done(fileName, true);
										}
										
									});
									
								}
							};
							caller.startCall(1);
							
						}});
					
					tab.add(createPatternTab(),"Pattern");
					
					tab.selectTab(1);//for debug;
					
					
				}
				
				
				/*
				 * 320px
				 */
				protected VerticalPanel createRightTopPopup(){
					popup=new PopupPanel();	
					
					
					VerticalPanel root=new VerticalPanel();
					popup.add(root);
					
					final VerticalPanel controler=new VerticalPanel();
					controler.setWidth("320px");//some widget broke,like checkbox without parent size
					controler.setSpacing(2);
					
					root.add(controler);
					
					final Button bt=new Button("Close Controls");
					bt.setWidth("320px");
					bt.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							controler.setVisible(!controler.isVisible());
							if(controler.isVisible()){
								bt.setText("Close Controls");
							}else{
								bt.setText("Open Controls");
							}
							updateGUI();
						}
					});
					
					root.add(bt);
					root.setSpacing(2);
					
					//popup.show();
					//moveToAroundRightTop(popup);
					
					//TODO keep
					HandlerRegistration resizeHandler = Window.addResizeHandler(new ResizeHandler() {
						@Override
						public void onResize(ResizeEvent event) {
							updateGUI();
						}
					});
					
					
					popup.show();
					moveToAroundRightTop(popup);
					
					return controler;
				}
				
				protected void updateGUI(){
					if(popup==null){
						return;
					}
					popup.show();//for initial,show first before move
					moveToAroundRightTop(popup);
					
				}
				protected PopupPanel popup;
				
				private void moveToAroundRightTop(PopupPanel dialog){
					int clientWidth=Window.getClientWidth();
					int scrollTopPos=Window.getScrollTop();
					int dw=dialog.getOffsetWidth();
					
					
					
					//LogUtils.log(clientWidth+","+scrollTopPos+","+dw);
					
					
					dialog.setPopupPosition(clientWidth-dw, scrollTopPos+0);
					
				}
	
				private void doShot() {
					String fileName=expressionsListBox.getValue()==null?"neutral":expressionsListBox.getValue().getName();
					fileName+=".png";
					String url=renderer.gwtPngDataUrl();
					Anchor a=HTML5Download.get().generateBase64DownloadLink(url, "image/png", fileName, "download", true);
					downloadPanel.add(a);
				}
				
					//fix r74 blender exporter blend keys
					public JsArrayNumber convertMorphVertices(JSONArray morphTargetsVertices){
						JsArrayNumber arrays=JavaScriptObject.createArray().cast();
						Vector3 axis = THREE.Vector3( 1, 0, 0 );
						double angle = THREEMath.degToRad( -90 );
						
						for(int i=0;i<morphTargetsVertices.size();i++){
							JSONValue avalue=morphTargetsVertices.get(i);
							JSONArray verticesArray=avalue.isArray();
							JsArrayNumber verticesNumber=verticesArray.getJavaScriptObject().cast();
							
							Vector3 vec=THREE.Vector3().fromArray(verticesNumber);
							vec.applyAxisAngle( axis, angle );
							
							arrays.push(vec.getX());
							arrays.push(vec.getY());
							arrays.push(vec.getZ());
						}
						return arrays;
					}
	@Override
	public void onBeforeStartApp() {
		
	}

	@Override
	public void onAfterStartApp() {
		
	}

	@Override
	public PerspectiveCamera createCamera() {
		PerspectiveCamera camera = THREE.PerspectiveCamera(45, getWindowInnerWidth()/getWindowInnerHeight(), 1, 4000);
		camera.getPosition().set(0, 0, 25);
		return camera;
	}

	private static class ExpressionUsed{
		private String name;
		private String leftBrow;
		private String rightBrow;
		private String brow;
		private String mouth;
		private String eye;
		
		
		public static boolean isLeftBrow(String name){
			return name.indexOf("brow")!=-1 && name.indexOf("L")!=-1;
		}
		public static boolean isRightBrow(String name){
			return name.indexOf("brow")!=-1 && name.indexOf("R")!=-1;
		}
		public static boolean isBothBrow(String name){
			return name.indexOf("brow")!=-1 && name.indexOf("L")==-1 && name.indexOf("R")==-1;
		}
		
		public static boolean isEye(String name){
			return name.indexOf("eye")!=-1;
		}
		
		public static boolean isMouth(String name){
			return name.indexOf("mouth")!=-1;
		}
		
		public void setData(Mblb3dExpression data){
			
			this.name=data.getName();
			//LogUtils.log(name);
			
			for(String key:data.getKeys()){
				if(isLeftBrow(key)){
					if(leftBrow==null){
						leftBrow=fixName(key);
					}else{
						leftBrow=leftBrow+","+fixName(key);
					}
				}else if(isRightBrow(key)){
					if(rightBrow==null){
						rightBrow=fixName(key);
					}else{
						rightBrow=rightBrow+","+fixName(key);
					}
				}else if(isBothBrow(key)){
					if(brow==null){
						brow=fixName(key);
					}else{
						brow=brow+","+fixName(key);
					}
				}else if(isEye(key)){
					if(eye==null){
						eye=fixName(key);
					}else{
						eye=eye+","+fixName(key);
					}
				}else if(isMouth(key)){
					if(mouth==null){
						mouth=fixName(key);
					}else{
						mouth=mouth+","+fixName(key);
					}
				}
			}
	
		}
		
		private String fixName(String key){
			
			if(key.startsWith("Expressions_")){
				key=key.substring("Expressions_".length());
			}
			
			String number=CharMatcher.DIGIT.retainFrom(key);
			
			if(key.endsWith("min")){
				return number+"_min";
			}else if(key.endsWith("max")){
				return number+"_max";
			}
			
			
			return key;
		}
		
		public String toString(){
			List<String> lines=Lists.newArrayList();
			
			lines.add(name);
			lines.add(leftBrow==null?"":leftBrow);
			lines.add(rightBrow==null?"":rightBrow);
			lines.add(brow==null?"":brow);
			lines.add(eye==null?"":eye);
			lines.add(mouth==null?"":mouth);
			
			return Joiner.on("\t").join(lines);
		}
	}
	
	public void selectEmotion(Emotion emotion){
		String key=STORAGE_KEY+indexBox.getValue();
		
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
	List<Emotion> emotions;
	private Label selectionLabel;
	
public Panel createPatternTab(){
		
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
		VerticalPanel panel=new VerticalPanel();
		
		combinedExpression = new CombinedExpression();
		
		
		//indexBox
		
		HorizontalPanel h=new HorizontalPanel();
		h.setWidth("300px");
		indexBox = new IntegerBox();
		
				
		indexBox.setValue(storageControler.getValue(STORAGE_INDEX_KEY,0));
		indexBox.setWidth("40px");
		h.add(indexBox);
		
		indexBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				int value=indexBox.getValue();
				
				try {
					storageControler.setValue(STORAGE_INDEX_KEY, value);
				} catch (StorageException e) {
					LogUtils.log(e.getMessage());
				}
				
				updateSelectionBox(value);
				
				//setIndex here
				Mblb3dExpression expression=combinedExpression.getAt(value);
				
				setMbl3dExpression(expression);
			}
		});
		panel.add(h);
		
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
						String emotion=storageControler.getValue(STORAGE_KEY+index, null);
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
						String emotion=storageControler.getValue(STORAGE_KEY+index, null);
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
		
		final Button reset=new Button("Neutral");
		reset.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(reset.getText().equals("Neutral")){
				
					setMbl3dExpression(null);
					
				reset.setText("Back");
				prev.setEnabled(false);
				next.setEnabled(false);
				}else{
					int index=indexBox.getValue();
					Mblb3dExpression expression=combinedExpression.getAt(index);
					setMbl3dExpression(expression);//setIndex not call fire event
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
		panel.add(closedPanel);
		
		
		
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
		descriptionBox.setValue(storageControler.getValue(STORAGE_DESCRIPTION_KEY+index,""));
		//check stored or not
		descriptionBox.addKeyUpHandler(new KeyUpHandler() {
		    @Override
		    public void onKeyUp(KeyUpEvent event) {
		     if(event.getNativeKeyCode() != KeyCodes.KEY_ENTER) {
		               updateDescriptionBoxColor();
		           }
		    }
		});
		panel.add(selectionPanel);
		panel.add(descriptionBox);
		
		panel.add(emotionButtons);
		
		
		return panel;
	}
	
	protected void updateDescriptionBoxColor() {
		int index=indexBox.getValue();
		String description=descriptionBox.getValue();
		String value=storageControler.getValue(STORAGE_DESCRIPTION_KEY+index,"");
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
			storageControler.removeValue(STORAGE_DESCRIPTION_KEY+index);
		}else{
			try {
				storageControler.setValue(STORAGE_DESCRIPTION_KEY+index, descriptionBox.getValue());
			} catch (StorageException e) {
				LogUtils.log(e.getMessage());
			}
		}
		
		updateDescriptionBoxColor();
	}

	public static final Mblb3dExpression emptyExpression=new Mblb3dExpression("");

	private void setIndex(int index){
		indexBox.setValue(index,true);
		updateDescriptionBoxColor();
	}
	
	StorageControler storageControler=new StorageControler();
	String STORAGE_KEY="em";//shorten
	String STORAGE_DESCRIPTION_KEY="de";//shorten
	
	final String STORAGE_INDEX_KEY="emindex";//shorten
	protected void updateSelectionBox(int value) {
		String emotion=storageControler.getValue(STORAGE_KEY+value, null);
		String label="Type:";
		if(emotion!=null){
			Emotion em=Emotion.findEmotionBySecondaryName(emotion,emotions);
			label+=em.toString();
		}
		
		String description=storageControler.getValue(STORAGE_DESCRIPTION_KEY+value, "");
		descriptionBox.setValue(description);
		
		selectionLabel.setText(label);
	}

	Label closedLabel;
	

	private void updateClosedLabel(@Nullable Mblb3dExpression expression){
		if(expression==null){
			closedLabel.setText("");
			return;
		}
		
		ClosedResult result=Mblb3dExpression.findClosed(expression, expressionList);
		closedLabel.setText(result.getExpression().getName()+":"+result.getLength());
	}
	
	protected void setMbl3dExpression(@Nullable Mblb3dExpression expression) {
		updateClosedLabel(expression);
		//TODO not set direct via label
		for(String key:ranges.keySet()){
			LabeledInputRangeWidget2 widget=ranges.get(key);
			widget.setValue(0,true);
		}
			
		//clear 0 first
		/*
		for(int i=0;i<mesh.getMorphTargetInfluences().length();i++){
			mesh.getMorphTargetInfluences().set(i,0);
		}
		*/
		
		if(expression==null){
			//no value reset
			return;
		}
		
		//set new values
		for(String key:expression.getKeys()){
			
			LabeledInputRangeWidget2 widget=ranges.get(key);
			double value=expression.get(key);
			widget.setValue(value,true);
			
			/*
			int index=mesh.getMorphTargetIndexByName(key);
			double value=expression.get(key);
			mesh.getMorphTargetInfluences().set(index, value);
			*/
		}
	}
	
	public static  class CombinedExpression{
		private List<Mblb3dExpression> brows;
		private List<Mblb3dExpression> eyes;
		private List<Mblb3dExpression> mouths;
		private List<String> indexs=Lists.newArrayList();
		
		public int getIndexSize(){
			return indexs.size();
		}
		
		public CombinedExpression(){
			
			initBrows();
			initEyes();
			initMouths();
			//TODO eye,mouth
			
			//test index
			for(int k=0;k<mouths.size();k++){
			for(int j=0;j<eyes.size();j++){
			for(int i=0;i<brows.size();i++){
				indexs.add(String.valueOf(i)+"-"+String.valueOf(j)+"-"+String.valueOf(k));
				}
			}
			}
			
		}
		
		private void initEyes() {
			String type=null;
			//singles
			eyes=Lists.newArrayList();
			eyes.add(new Mblb3dExpression("eye"));//empty
			
			type="eyes";
			
			for(int i=0;i<6;i++){
				String direction="max";
				for(int j=0;j<2;j++){
					if(i==5 && j==1){
						continue;// 6 has no min
					}
					
					if(j==1){
						direction="min";
					}
					Mblb3dExpression expression=new Mblb3dExpression("eyes");
					String name=Strings.padStart(String.valueOf(i+1), 2, '0');
					String key=header+type+name+"_"+direction;
					expression.set(key, 1);
					
					eyes.add(expression);
				}
				
			}
		}
		
		private void initMouths() {
			String type=null;
			//singles
			mouths=Lists.newArrayList();
			mouths.add(new Mblb3dExpression("mouth"));//empty
			
			type="mouth";
			
			for(int i=0;i<11;i++){
				if(i==2 ){
					continue; //skip3
				}
				
				String direction="max";
				for(int j=0;j<2;j++){
					
					if(i==4 && j==0){
						continue;	//skip 5max
					}
					
					if(i==9 && j==1){
						continue;	//skip 10 min
					}
					
					if(i==10 && j==1){
						continue;	//skip 11 min
					}
					
					
					if(j==1){
						direction="min";
					}
					Mblb3dExpression expression=new Mblb3dExpression("mouth");
					String name=Strings.padStart(String.valueOf(i+1), 2, '0');
					String key=header+type+name+"_"+direction;
					expression.set(key, 1);
					
					mouths.add(expression);
				}
				
			}
		}

		public Mblb3dExpression getAt(int index){
			String key=indexs.get(index);
			return parseIndex(key);
		}
		
		public Mblb3dExpression parseIndex(String key){
			//LogUtils.log(key);
			List<String> splittedKey=Lists.newArrayList(Splitter.on('-').split(key));
			
			Mblb3dExpression brow=brows.get(ValuesUtils.toInt(splittedKey.get(0), 0));
			
			Mblb3dExpression eye=null;
			if(splittedKey.size()>1){
				eye=eyes.get(ValuesUtils.toInt(splittedKey.get(1), 0));
				
			}
			
			Mblb3dExpression mouth=null;
			if(splittedKey.size()>2){
				mouth=mouths.get(ValuesUtils.toInt(splittedKey.get(2), 0));
			}
			
			return Mblb3dExpression.merge(brow,eye,mouth);
		}
		

		String header="Expressions_";
		
		public void initBrows() {

			
			String type=null;
			//singles
			brows=Lists.newArrayList();
			brows.add(new Mblb3dExpression("brow"));//empty
			
			type="brow";
			List<String> browNames=Lists.newArrayList();
			for(int i=0;i<2;i++){
				String direction="max";
				for(int j=0;j<2;j++){
					if(j==1){
						direction="min";
					}
					Mblb3dExpression expression=new Mblb3dExpression("brow");
					String name=Strings.padStart(String.valueOf(i+1), 2, '0')+"L";
					String key=header+type+name+"_"+direction;
					expression.set(key, 1);
					browNames.add(key);
					brows.add(expression);
				}
				
			}
			
			//same
			for(int i=0;i<2;i++){
				String direction="max";
				for(int j=0;j<2;j++){
					if(j==1){
						direction="min";
					}
					Mblb3dExpression expression=new Mblb3dExpression("brow");
					String name=Strings.padStart(String.valueOf(i+1), 2, '0')+"L";
					String key=header+type+name+"_"+direction;
					expression.set(key, 1);
					expression.set(key.replace("L", "R"), 1);
					brows.add(expression);
				}
				
			}
			
			//combine
			String first=browNames.get(0);
			for(int i=1;i<4;i++){
				String second=browNames.get(i).replace("L", "R");
				brows.add(makeMblb3dExpression("brow", first,second));
			}
			first=browNames.get(1);
			for(int i=2;i<4;i++){
				String second=browNames.get(i).replace("L", "R");
				brows.add(makeMblb3dExpression("brow", first,second));
			}
			
			first=browNames.get(2);
			for(int i=3;i<4;i++){
				String second=browNames.get(i).replace("L", "R");
				brows.add(makeMblb3dExpression("brow", first,second));
			}
			
			//both
			String direction="max";
			for(int j=0;j<2;j++){
				if(j==1){
					direction="min";
				}
				Mblb3dExpression expression=new Mblb3dExpression("brow");
				String name="03";
				String key=header+type+name+"_"+direction;
				expression.set(key, 1);
				
				brows.add(expression);
			}
		}
		
		public Mblb3dExpression makeMblb3dExpression(String name,String... keys){
			Mblb3dExpression expression=new Mblb3dExpression(name);
			for(String key:keys){
				expression.set(key, 1);
			}
			return expression;
		}
	}
	
	private CheckBox hasValueCheck;
	private EnterKeySupportTextBox descriptionBox;
	
}
