package com.akjava.mbl3d.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Mbl3dTools extends ThreeAppEntryPointWithControler {

	
	private HorizontalPanel downloadPanel;
	private SkinnedMesh mesh;
	
	
	
	private CombinedExpression combinedExpression;
	private IntegerBox indexBox;
	
	@Override
	public WebGLRendererParameter createRendererParameter() {
		return GWTParamUtils.WebGLRenderer().preserveDrawingBuffer(true);
	}

	@Override
	public void onInitializedThree() {
		//LogUtils.log("onInitializedThree");
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
					
					
					TabPanel tab=new TabPanel();
					controlerRootPanel.add(tab);
					
					
					basicPanel = new BasicExpressionPanel(mesh);
					tab.add(basicPanel,"Basic");
					
					
					
					
					
				
					/*
					Button shot=new Button("test",new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							doShot();
						}

						
					});
					hpanel.add(shot);
					
					downloadPanel = new HorizontalPanel();
					hpanel.add(downloadPanel);
					*/
					
					
					
					
					
					//LogUtils.log(debug);
					
					THREE.XHRLoader().load("models/mbl3d/expressions.txt",new XHRLoadHandler() {

						

						@Override
						public void onLoad(String text) {
							final List<Mblb3dExpression> expressionList = Lists.newArrayList();
							expressionList.add(null);//add empty
							
							List<String> names=CSVUtils.splitLinesWithGuava(text);
							
							AsyncMultiCaller<String> caller=new AsyncMultiCaller<String>(names) {
								@Override
								public void doFinally(boolean cancelled) {
									basicPanel.setExpressionList(expressionList);
									
									
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
				private void doShot() {
					String fileName=expressionsListBox.getValue()==null?"neutral":expressionsListBox.getValue().getName();
					fileName+=".png";
					String url=renderer.gwtPngDataUrl();
					Anchor a=HTML5Download.get().generateBase64DownloadLink(url, "image/png", fileName, "download", true);
					downloadPanel.add(a);
				}
				*/
				
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
				
				updateClosedLabel(expression);
				basicPanel.setMbl3dExpression(expression);
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
				
					updateClosedLabel(null);
					basicPanel.setMbl3dExpression(null);
				reset.setText("Back");
				prev.setEnabled(false);
				next.setEnabled(false);
				}else{
					int index=indexBox.getValue();
					Mblb3dExpression expression=combinedExpression.getAt(index);
					updateClosedLabel(expression);
					basicPanel.setMbl3dExpression(expression);
					
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
		
		panel.add(new Label("Tools"));
		
		final HorizontalPanel p=new HorizontalPanel();
		p.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		p.add(new Button("dump",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				List<String> lines=Lists.newArrayList();
				for(int i=1;i<=3671;i++){
					String line=null;
					String type=storageControler.getValue(STORAGE_KEY+i, null);
					String description=storageControler.getValue(STORAGE_DESCRIPTION_KEY+i, null);
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
		panel.add(p);
		FileUploadForm uploadDump=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				
				for(int i=1;i<=3671;i++){
					storageControler.removeValue(STORAGE_KEY+i);
					storageControler.removeValue(STORAGE_DESCRIPTION_KEY+i);
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
								storageControler.setValue(STORAGE_KEY+index, type);
							} catch (StorageException e) {
								LogUtils.log(e.getMessage());
							}
							}
						}
						if(csv.length>2){
							String description=csv[2];
							if(!description.isEmpty()){
							try {
								storageControler.setValue(STORAGE_DESCRIPTION_KEY+index, description);
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
		
		ClosedResult result=basicPanel.findClosed(expression);
		closedLabel.setText(result.getExpression().getName()+":"+result.getLength());
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
	private BasicExpressionPanel basicPanel;
	
}
