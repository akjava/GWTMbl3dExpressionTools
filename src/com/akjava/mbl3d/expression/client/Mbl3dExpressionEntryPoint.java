package com.akjava.mbl3d.expression.client;

import java.util.List;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.experimental.AsyncMultiCaller;
import com.akjava.gwt.three.client.examples.ColladaLoader.ColladaLoadHandler;
import com.akjava.gwt.three.client.examples.js.THREEExp;
import com.akjava.gwt.three.client.examples.js.controls.OrbitControls;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.JSParameter;
import com.akjava.gwt.three.client.gwt.core.BoundingBox;
import com.akjava.gwt.three.client.gwt.renderers.WebGLRendererParameter;
import com.akjava.gwt.three.client.java.ui.experiments.ThreeAppEntryPointWithControler;
import com.akjava.gwt.three.client.java.utils.Mbl3dLoader;
import com.akjava.gwt.three.client.java.utils.MultiTextureLoader;
import com.akjava.gwt.three.client.java.utils.MultiTextureLoader.MultiTextureLoaderListener;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.animation.AnimationMixer;
import com.akjava.gwt.three.client.js.animation.AnimationMixerAction;
import com.akjava.gwt.three.client.js.animation.KeyframeTrack;
import com.akjava.gwt.three.client.js.animation.tracks.BooleanKeyframeTrack;
import com.akjava.gwt.three.client.js.animation.tracks.NumberKeyframeTrack;
import com.akjava.gwt.three.client.js.animation.tracks.StringKeyframeTrack;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.core.Clock;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.lights.AmbientLight;
import com.akjava.gwt.three.client.js.lights.DirectionalLight;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.materials.MultiMaterial;
import com.akjava.gwt.three.client.js.math.THREEMath;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataPredicates;
import com.akjava.mbl3d.expression.client.recorder.RecorderPanel;
import com.akjava.mbl3d.expression.client.texture.CanvasTexturePainter;
import com.akjava.mbl3d.expression.client.texture.TextureSwitcher;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayBoolean;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Mbl3dExpressionEntryPoint extends ThreeAppEntryPointWithControler implements Mbl3dExpressionReceiver {

	
	private SkinnedMesh mesh;
	public SkinnedMesh getMesh() {
		return mesh;
	}


	private int windowHalfX;
	private int windowHalfY;
	
	
	public static Mbl3dExpressionEntryPoint INSTANCE;
	
	private AnimationMixer mixer;
	
	private CanvasTexturePainter canvasTexturePainter;
	
	//private TextureSwitcher textureSwitcher; //i'm not sure,what is this?maybe for graduate texture change
	
	@Override
	public WebGLRendererParameter createRendererParameter() {
		return GWTParamUtils.WebGLRenderer().preserveDrawingBuffer(true).antialias(true);
	}
	
	public String toImageDataUrl(){
		return renderer.gwtPngDataUrl();
	}
	/**
	 * @deprecated
	 */
	private MeshPhongMaterial material;

	
	private OrbitControls controls;
	private int cameraY=1550;
	private int cameraZ=500;
	@Override
	public void onInitializedThree() {
		clock=THREE.Clock();
		INSTANCE=this;
		
		//LogUtils.log("onInitializedThree");
		renderer.setClearColor(0x333333);//default is black?
		
		 windowHalfX= (int)(SCREEN_WIDTH/2);
		 windowHalfY= (int)(SCREEN_HEIGHT/2);
		 
		 
		 
		 //renderer.setGammaOutput(true);//for blender color,however maybe make color problem  with gimp-created texture
		
		rendererContainer.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				onDocumentMouseMove(event);
			}
		});
		rendererContainer.addMouseWheelHandler(new MouseWheelHandler() {
			
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				event.preventDefault();
				camera.getPosition().gwtIncrementZ(event.getDeltaY()*3);//for chrome only
			}
		});
		
		//test(1.0);
		//light
		
		//0xaaaaaa
		AmbientLight ambient = THREE.AmbientLight( 0xaaaaaa );//var ambient = new THREE.AmbientLight( 0xffffff );
		//AmbientLight ambient = THREE.AmbientLight( 0xc38038);
		scene.add( ambient );

		
		
		//DirectionalLight directionalLight = THREE.DirectionalLight( 0xffffff );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		
		DirectionalLight directionalLight = THREE.DirectionalLight( 0x666666 );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		directionalLight.getPosition().set( -0.5, 1, -1 ).normalize();//directionalLight.position.set( -1, 1, 1 ).normalize();
		scene.add( directionalLight );
		
		final String modelUrl=GWTHTMLUtils.parameterFile("model");
		final String textureUrl=GWTHTMLUtils.parameterFile("texture");
		LogUtils.log("model & texture:"+modelUrl+","+textureUrl);
		
		//String url= "models/mbl3d/morph.json";//var url= "morph.json";
				//String url= "models/mbl3d/tmp5.json";
				//String url= "models/mbl3d/model8o.json";
				//"models/mbl3d/simpleface.png"
		
		
		
		
		
		material2 = THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial()
				.morphTargets(true)
				.transparent(true)
				.alphaTest(0.5)
				.opacity(1)
				.side(THREE.DoubleSide)//tongue need this
				.shininess(15)
				//.specular(0xffffff).shininess(200) //need map?
				//.specular(0x111111).shininess(0)
				);
		
		
		LogUtils.log("camera-y:"+cameraY);
		controls = THREEExp.OrbitControls(camera,rendererContainer.getElement() );
		controls.setTarget(THREE.Vector3( 0, cameraY, 0 ));
		controls.getMouseButtons().set("ORBIT", THREE.MOUSE.MIDDLE);
		controls.getMouseButtons().set("ZOOM", 3);//3 is not exist,for ignore left button
		controls.update();
		
		
		//TODO init ui;
		initUi();//need model
		loadTextureMontage(material2);//need tab
		
				
	}
	
	private double characterScale=1000;
	private double hairScale=characterScale;
	private double characterX,characterY,characterZ;
	private Mesh hairMesh;
	private MeshPhongMaterial hairMaterial;
	public Mesh getHairMesh(){
		return hairMesh;
	}
	public MeshPhongMaterial getHairMaterial(){
	if(hairMaterial==null){
		hairMaterial=THREE.MeshPhongMaterial(GWTParamUtils.
				MeshPhongMaterial().color(0x553817).side(THREE.DoubleSide).specular(0xffffff).shininess(15));
	}
	return hairMaterial;	
	}
	public void loadHair(String hairPath){
		//Mbl3dLoader support both ver3 & ver4 format
		new Mbl3dLoader().load(hairPath, new JSONLoadHandler() {	//hair2
			
			

			/*
			 * take care of materials.
			 * must export with face-colors.
			 * 
			 */
			@Override
			public void loaded(Geometry geometry, JsArray<Material> materials) {
				if(hairMesh!=null){
					scene.remove(hairMesh);
				}
				//LogUtils.log(bb);
				
				hairMesh = THREE.Mesh( geometry, getHairMaterial()
						//.map(THREE.TextureLoader().load("models/mbl3d/hair1.png"))
						 );
				
				//Mesh hair = THREE.Mesh( geometry, THREE.MultiMaterial(materials) );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = new THREE.SkinnedMesh( geometry, mat );
				//mesh.setName("model");//mesh.setName("model");//mesh.name = "model";
				hairMesh.getPosition().set( characterX, characterY,characterZ );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.position.set( x, y - bb.min.y * s, z );
				hairMesh.getScale().setScalar(hairScale);//mesh.getScale().set( s, s, s );//mesh.getScale().set( s, s, s );//mesh.scale.set( s, s, s );
				scene.add( hairMesh );	
			}
		});
	}
	
	
	private TextureMontage textureMontage;
	protected void loadTextureMontage(final MeshPhongMaterial material){
		THREE.XHRLoader().load("models/mbl3d14/montage_presets/montage_default.txt"+GWTHTMLUtils.parameterTime(), new XHRLoadHandler() {
			

			@Override
			public void onLoad(String text) {
				List<TextureMontageData> datas= new TextureMontageDataConverter().convert(text);
				
				Canvas canvas=CanvasUtils.createCanvas(2048, 2048);
				textureMontage = new TextureMontage("models/mbl3d14/", canvas,material);
				
				textureMontage.setTextureMontageDatas(datas);
				
				
				material.setMap(textureMontage.getCanvasTexture());
				textureMontage.update();//need material?
				
				TextureMontageWidget widget=new TextureMontageWidget(textureMontage);
				textureTab.getTextureMontagePanel().add(widget);
				
				
				
				//create widget TODO
			}
		});
	}
	
	/**
	 * @using texture switcher purpose is making animated changing texture
	 * @param material
	 */
				/*protected void loadTextures(final MeshPhongMaterial material) {
					THREE.XHRLoader().load("textures.txt", new XHRLoadHandler() {
						@Override
						public void onLoad(String text) {
							List<String> urls=Lists.newArrayList(CSVUtils.splitLines(text));
							new MultiTextureLoader().load(urls, new MultiTextureLoaderListener() {
								

								@Override
								public void onLoad(List<Texture> textures) {
									textureSwitcher=TextureSwitcher.create();
									textureSwitcher.setMaterial(material);
									
									canvasTexturePainter = new CanvasTexturePainter(textures,null);
									canvasTexturePainter.getTextureLayers().setVisible(0, true);
									canvasTexturePainter.getTextureLayers().setVisible(1, true);
									canvasTexturePainter.update();
									
									
									String dataUrl=canvasTexturePainter.getCanvas().toDataUrl();
									Texture defaultTexture=THREE.TextureLoader().load(dataUrl);
									textureSwitcher.put("default", defaultTexture);
									textureSwitcher.setSeletion("default");
									
									textureSwitcher.setSeletion("canvas");
									
									//painter.getTextureLayers().setVisible(0, false);
									//canvasTexturePainter.getTextureLayers().setVisible(1, true);
									//canvasTexturePainter.update();
									
									
									
									preferenceTab.setCanvasTexturePainter(canvasTexturePainter);
								}
								
								@Override
								public void onError(List<String> messages) {
									for(String message:messages){
										LogUtils.log(message);
									}
								}
							});
							
							
							
						}
					});
				}*/

				private Panel createBasicPanel(){
					basicPanel = new BasicExpressionPanel(this);
					
					THREE.XHRLoader().load("models/mbl3d/expressions.txt",new XHRLoadHandler() {

						

						@Override
						public void onLoad(String text) {
							final List<Mbl3dExpression> expressionList = Lists.newArrayList();
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
											Mbl3dExpression expression=new Mbl3dExpressionConverter().convert(text);
											expression.setName(FileNames.getRemovedExtensionName(fileName));
											expressionList.add(expression);
											done(fileName, true);
										}
										
									});
									
								}
							};
							caller.startCall(1);
							
						}});
					return basicPanel;
				}
	
				//must call after model loaded
				private void initUi() {
					//can replace?
					
					tab = new TabPanel();
					controlerRootPanel.add(tab);
					
					
					tab.add(createBasicPanel(),"Basic");
					
					
					
					
					
				
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
					
					tab.add(createDataListTab(),"DataList");
					
					//disable right now
					//tab.add(createPatternTab(),"Pattern");
					
					
					
					
					
					tab.add(cratePreferenceTab(),"Misc");
					
					textureTab=new TextureTab(this);
					tab.add(textureTab,"Texture");
					
					tab.selectTab(3);
					
					
					THREE.XHRLoader().load("models/mbl3d/emotions.csv", new XHRLoadHandler() {
						@Override
						public void onLoad(String text) {
							List<Emotion> emotions=new EmotionCsvConverter().convert(text);

							EmotionsData emotionData=new EmotionsData(emotions);
							dataListPanel.setEmotions(emotions);
							combinePatternPanel.setEmotion(emotions);
							
							recorderPanel.setEmotionData(emotionData);
						}
					});
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
				
					private Widget cratePreferenceTab() {
					preferenceTab = new PreferenceTab(this,dataListPanel);
					//if need set
					
					/*
					 * maybe for making animation,right now stop using this.
					recorderPanel = new RecorderPanel();
					preferenceTab.add(recorderPanel);
					*/
					
					
					//
					HairControlPanel hairControlPanel=new HairControlPanel(this);
					preferenceTab.add(hairControlPanel);
					
					ModelControlPanel modelControlPanel=new ModelControlPanel(this);
					preferenceTab.add(modelControlPanel);
					
					return preferenceTab;
				}

					private Panel createDataListTab() {
					dataListPanel = new DataListPanel(storageControler,basicPanel);
					
					
					
					return dataListPanel;
				}

					//fix r74 blender exporter blend keys
					//TODO make method
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
					
					
					
					
	private String modelName;
	public void setEyeModifier(double value){
		basicPanel.getEyeModifier().setValue(value);//expression must be reset
	}
	public void loadModel(final String modelUrl){
		if(mesh!=null){
			scene.remove( mesh );
		}
		Mbl3dLoader loader=new Mbl3dLoader();
		loader.forceApplyAxisAngle(GWTHTMLUtils.parameterBoolean("forceFixMorphtargets",false));
		loader.load(modelUrl,new JSONLoadHandler() {
			
			private MultiMaterial multiMaterials;
			
			@Override
			public void loaded(Geometry geometry,JsArray<Material> materials) {
				modelName=ModelControlPanel.toModelName(modelUrl);
				//Geometry geometry=loadedObject.getGeometry();
				
				geometry.computeBoundingBox();
				BoundingBox bb = geometry.getBoundingBox();
				//double x=-20, y=-1270,z= -300,s= 800;

				double x=-0, y=bb.getMin().getY()*characterScale,z=0;
				
				
				
				
				/*
				List<String> urls=Lists.newArrayList("models/mbl3d/body.png", //"models/mbl3d/body.png",
						"models/mbl3d/green_eye.png",
						"models/mbl3d/eye_large.png",
						"models/mbl3d/eye_small.png",
													 "models/mbl3d/test.png",
													 "models/mbl3d/redface1.png",
													 "models/mbl3d/redface2.png",
													 "models/mbl3d/redface3.png",
													 "models/mbl3d/blueface1.png",
													 
													 "models/mbl3d/uv.png"
						);
				*/
				
				
				material = THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial()
						.morphTargets(true)
						.transparent(true)
						.specular(0x555555).shininess(5)
						.opacity(1)
						);
				
				material.setVisible(false);
				
				//loadTextures(material);
				
				
		
				
				
				
				
				
				
				
				
				
				if(materials!=null){
				for(int i=0;i<materials.length();i++){
					MeshPhongMaterial m=materials.get(i).cast();//need cast GWT problem
					m.setMorphTargets(true);
					
					//update material
					if(m.getName().equals("White")){
						m.setColor(THREE.Color(0xf8f8f8));
						m.setSpecular(THREE.Color(0xffffff));//less shine
						m.setShininess(100);
					}else if(m.getName().equals("Blue")){//edge of mouth
						m.setColor(THREE.Color(0x007ebb));
						m.setSpecular(THREE.Color(0xffffff));
						m.setShininess(100);
					}else if(m.getName().equals("Pink01")){//mouth and inside
						m.setColor(THREE.Color(0xffa3ac));
						m.setSpecular(THREE.Color(0x888888));
						m.setShininess(50);
					}else if(m.getName().equals("Pink02") || m.getName().equals("Lip")){//face & lip
						m.setColor(THREE.Color(0xFFE4C6));
						m.setSpecular(THREE.Color(0x111111));
						m.setShininess(5);
					}else if(m.getName().equals("gum")){//edge of mouth
						m.setColor(THREE.Color(0x7c4f53));
						m.setSpecular(THREE.Color(0x111111));
						m.setShininess(5);
					}else{//
						m.setSpecular(THREE.Color(0x111111));//less shine
						m.setShininess(5);
					}	
				}
				}
				
				boolean hasHead=false;
				JsArray<Material> filterd=JavaScriptUtils.createJSArray();
				//filterd.push(material);
				
				if(materials!=null){
				for(int i=0;i<materials.length();i++){
					
					//if exists
					if(materials.get(i).getName().equals("Eyes") || materials.get(i).getName().equals("Pink02")){//eye & tooth
						//LogUtils.log(i+" white");
						//materials.set(i, material);
						
						
						//filterd.push(materials.get(i));
						//filterd.push(eyeMaterial);
						continue;
					}
					
					//if exists
					//body
					if(materials.get(i).getName().equals("head")){
						hasHead=true;
						//LogUtils.log("head:"+i+" pink02");
						//this make problem ,i know when transparent?
						//materials.get(i).setVisible(false);//allow modify
						//materials.set(i, material);
						filterd.push(material);
						//filterd.push(THREE.MeshBasicMaterial());
						
						//filterd.push(materials.get(i));
						continue;
					}
					filterd.push(materials.get(i));
				}
				}
				if(!hasHead){
					LogUtils.log("this model not contain head-material,not work canvas-painter");
				}
				//var mat=THREE.MultiMaterial( materials);//MultiMaterial mat=THREE.MultiMaterial( materials);//var mat=new THREE.MultiMaterial( materials);
				
			
				//MultiMaterial mat=THREE.MultiMaterial(materials );
				
				MultiMaterial mat=THREE.MultiMaterial(filterd);
				if(filterd.length()==0){
					mesh = THREE.SkinnedMesh( geometry, material2 );//using texture switch
				}else{
					LogUtils.log("use json-models multi-material");
					//multi material:not so good,because of speed
					mesh = THREE.SkinnedMesh( geometry, mat );
				}
				
				
				//materials.push(material);
				//material=mat.cast();
				
				//subdivision(1,geometry);//not support morph-target
				
				
				mesh.setName("model");//mesh.setName("model");//mesh.setName("model");//mesh.name = "model";
				mesh.getPosition().set( x, y, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.position.set( x, y - bb.min.y * s, z );
				mesh.getScale().set( characterScale, characterScale, characterScale );//mesh.getScale().set( s, s, s );//mesh.getScale().set( s, s, s );//mesh.scale.set( s, s, s );
				mesh.updateMatrixWorld();
				scene.add( mesh );
				
				
				mixer = THREE.AnimationMixer(mesh);
				
				//mesh.setVisible(false);
				
				//temp test
				
				characterX=x;
				characterY=y;
				characterZ=z;
				
				//update hair position if exist
				
				basicPanel.setMesh(mesh);
				
			}

});
	}
	
	
	public String getModelName() {
		return modelName;
	}

	public final native void subdivision(int division,Geometry geometry)/*-{
	var modifier = new $wnd.THREE.SubdivisionModifier( division );
    modifier.modify( geometry );
	}-*/;
	
	@Override
	public void onBeforeStartApp() {
		
	}

	@Override
	public void onAfterStartApp() {
		
	}
	
	
	Clock clock;
	public void setSelectedTab(int index){
		tab.selectTab(index);
	}
	
	int mouseX;
	int mouseY;
	protected void onDocumentMouseMove(MouseMoveEvent event) {
		mouseX = ( event.getClientX() - windowHalfX );
		mouseY = ( event.getClientY() - windowHalfY )*2;
	}

	@Override
	public PerspectiveCamera createCamera() {
		PerspectiveCamera camera = THREE.PerspectiveCamera(45, getWindowInnerWidth()/getWindowInnerHeight(), 10, 20000);
		camera.getPosition().set(0, cameraY, cameraZ);
		return camera;
	}
	@Override
	public void onWindowResize() {
		super.onWindowResize();
		 windowHalfX= (int)(SCREEN_WIDTH/2);
		 windowHalfY= (int)(SCREEN_HEIGHT/2);
	}
	

	
	double frameTime=1.0/30;

	@Override
	public void animate(double timestamp) {
		
		//camera.getPosition().gwtIncrementX(( - mouseX - camera.getPosition().getX()) * .001);//camera.position.y += ( - mouseY - camera.position.y ) * .01;
		//camera.lookAt(scene.getPosition());//look at 0
	
		if(recorderPanel!=null && recorderPanel.isRecording()){
			if(recorderPanel.isWriting()){
				return;
			}
			
			mixer.update(frameTime);	
		//	textureSwitcher.update();
			
			renderer.render(scene, camera);
			
			recorderPanel.record(toImageDataUrl());
			
		}else{
			
			
			if(mixer!=null){
				
				mixer.update(clock.getDelta());
				
			}	
			
			/*if(textureSwitcher!=null){
				textureSwitcher.update();
			}*/
			
			renderer.render(scene, camera);
		}
		
		if(textureMontage!=null){
			textureMontage.update();
		}
	}

	
	public AnimationMixer getMixer() {
		return mixer;
	}

	private Panel createPatternTab(){
		combinePatternPanel = new CombinePatternPanel(storageControler, basicPanel);
		
		
		return combinePatternPanel;
	}
	


	public static final Mbl3dExpression emptyExpression=new Mbl3dExpression("");


	
	StorageControler storageControler=new StorageControler();


	
	/**
	 * verty important tips,NEVER duplicate time.
	 * 
	 * if duplicate time that would removed in optimize(),and this usually break other timings.
	 * 
	 * 
	 * 
	 * 
	 * @param name
	 * @param expressions
	 * @param filterBrow
	 * @param filterEyes
	 * @param filterMouth
	 * @return
	 */
	public AnimationClip converToAnimationClip(String name,List<Mbl3dExpression> expressions,boolean filterBrow,boolean filterEyes,boolean filterMouth){
		JSParameter param=mesh.getMorphTargetDictionary().cast();
		
		JsArray<KeyframeTrack> tracks=JavaScriptObject.createArray().cast();
		
		
		
		
		for(int i=0;i<param.getKeys().length();i++){
			String key=param.getKeys().get(i);
			
			JsArrayNumber times=JavaScriptObject.createArray().cast();
			JsArrayNumber values=JavaScriptObject.createArray().cast();
			
			double time=0;
			for(int j=0;j<expressions.size();j++){
				Mbl3dExpression expression=expressions.get(j);
			
			
			
			boolean needSetValue=false;
			
			if(expression.containsKey(key)){
				boolean passed=false;
				if(!passed && filterBrow && Mbl3dDataPredicates.passBrowOnly().apply(key)){
					passed=true;
				}
				if(!passed && filterEyes && Mbl3dDataPredicates.passEyesOnly().apply(key)){
					passed=true;
				}
				if(!passed && filterMouth && Mbl3dDataPredicates.passMouthOnly().apply(key)){
					passed=true;
				}
				if(passed){
					needSetValue=true;
				}
			}
			//add time
			if(j==0){
				times.push(time);
			}
			
			times.push(time+duration);
			times.push(time+duration*2);
			
			time=time+duration*2;
			
			//add value
			if(j==0){
				values.push(0);
			}
			
			double value=0;
			if(needSetValue){
				value=expression.get(key);
			}
			values.push(value);
			values.push(0);
			
			
			
			}
		
			String trackName=".morphTargetInfluences["+i+"]";
			NumberKeyframeTrack track=THREE.NumberKeyframeTrack(trackName, times, values);
			tracks.push(track);
		}
		
		
		
		//Mbl3dExpressionEntryPoint.INSTANCE.
		//morph animation
		AnimationClip clip=THREE.AnimationClip(name, -1, tracks);
		return clip;
	}

	/*
	 * convert Mbl3dExpression to AnimationClip
	 */
	public AnimationClip converToAnimationClip(String name,Mbl3dExpression expression,boolean filterBrow,boolean filterEyes,boolean filterMouth){
		JSParameter param=mesh.getMorphTargetDictionary().cast();
		
		
		
		JsArray<KeyframeTrack> tracks=JavaScriptObject.createArray().cast();
		for(String key:expression.getKeys()){
			boolean passed=false;
			if(!passed && filterBrow && Mbl3dDataPredicates.passBrowOnly().apply(key)){
				passed=true;
			}
			if(!passed && filterEyes && Mbl3dDataPredicates.passEyesOnly().apply(key)){
				passed=true;
			}
			if(!passed && filterMouth && Mbl3dDataPredicates.passMouthOnly().apply(key)){
				passed=true;
			}
			//no need check
			if(!passed){
				continue;
			}
			
			int index=param.getInt(key);
			
			String trackName=".morphTargetInfluences["+index+"]";
			double value=expression.get(key);
			
			
			
			JsArrayNumber times=JavaScriptObject.createArray().cast();
			times.push(0);
			times.push(duration);
			times.push(duration*2);
			/*
			times.push(duration+wait);//wait pause
			times.push(duration*2+wait);
			*/
			
			JsArrayNumber values=JavaScriptObject.createArray().cast();
			values.push(0);
			values.push(value);
			//values.push(value);
			values.push(0);
			
			NumberKeyframeTrack track=THREE.NumberKeyframeTrack(trackName, times, values);
			tracks.push(track);
			
			
			//test animation
			
			
			
		}

		
		//Mbl3dExpressionEntryPoint.INSTANCE.
		//morph animation
		AnimationClip clip=THREE.AnimationClip(name, -1, tracks);
		return clip;
	}

	public String createAnimationJson(){
		Mbl3dExpression expression=Mbl3dExpressionEntryPoint.INSTANCE.getBasicPanel().currentRangesToMbl3dExpression(false);
		AnimationClip clip=converToAnimationClip("test", expression, true, true, true);
		
		
		LogUtils.log(AnimationClip.toJSON(clip));
		
		JSONObject object=new JSONObject(AnimationClip.toJSON(clip));
		
		return object.toString();
	}
	
	public void playAnimation(Mbl3dExpression expression,boolean filterBrow,boolean filterEyes,boolean filterMouth) {
		//test(1);
		stopAnimation();
		
		if(mesh==null){
			//not ready
			return;
		}
		
		AnimationClip clip=converToAnimationClip("test", expression, filterBrow, filterEyes, filterMouth);
		
		getMixer().uncacheClip(clip);//same name cache that.
		getMixer().clipAction(clip).play();
		
		
		//texture animation
		
		
		//insertSwitchTextureAnimations(duration);
		//insertVisibleTextureAnimations(duration);
		//onAnimationBooleanUpdated();
		
		
		//
		insertMaterialAlphaAnimations(material,duration);
		//LogUtils.log("4");
		
	}
	
	/**
	 * modified when checkbox clicked
	 */
	private List<Boolean> animationBoolean=Lists.newArrayList();
	
	public List<Boolean> getAnimationBoolean() {
		return animationBoolean;
	}
	
	/**
	 * @deprecated
	 */
	public void onAnimationBooleanUpdated(){
		for(int i=0;i<animationBoolean.size();i++){
			canvasTexturePainter.getTextureLayers().setAlpha(i, 100);
			canvasTexturePainter.getTextureLayers().setVisible(i, animationBoolean.get(i));
		}
		canvasTexturePainter.update();
		material.setMap(canvasTexturePainter.getCanvasTexture());
		
		//debug canvas
		//String url=canvasTexturePainter.getCanvas().toDataUrl();
		//Window.open(url, "_debug", null);//
		
		if(material!=null){//?why check canvasTexturePainter
			material.setNeedsUpdate(true);
			material.setVisible(true);
		}
	}
	
	public boolean isInitialTexture(int index){
		return index==0 || index==1;
	}
	
	public void insertMaterialAlphaAnimations() {
		insertMaterialAlphaAnimations(material,duration);
	}
	
private void insertMaterialAlphaAnimations(Material material,double duration) {
		
		
		JsArray<KeyframeTrack> tracks2=JavaScriptObject.createArray().cast();
		
		String trackName=".opacity";
		JsArrayNumber times=JavaScriptObject.createArray().cast();
		times.push(0);
		times.push(duration);
		times.push(duration*2);
		
		
		JsArrayNumber values=JavaScriptObject.createArray().cast();
		
		values.push(0);
		
		values.push(1);
		
		values.push(0);
		
		//have multiple?
		/*
		values.push(false);
		values.push(true);
		values.push(false);
		*/
		
		NumberKeyframeTrack track=THREE.NumberKeyframeTrack(trackName, times, values);
	
		tracks2.push(track);
		
		AnimationClip clip2=THREE.AnimationClip("test2", -1, tracks2);
		
		getMixer().uncacheClip(clip2);//same name cache that.
		
		AnimationMixerAction action=getMixer().clipAction(clip2,material);
		
		action.play();
	}

	private void insertAlphaTextureAnimations(double duration) {
		
		//init initial datas
		for(int i=0;i<canvasTexturePainter.getTextureLayers().size();i++){
			canvasTexturePainter.getTextureLayers().setVisible(i, true);
			if(isInitialTexture(i)){
				//canvasTexturePainter.getTextureLayers().setAlpha(i, 1);
				canvasTexturePainter.getTextureLayers().setAlpha(i, 0);//only use effect now
			}else{
				canvasTexturePainter.getTextureLayers().setAlpha(i, 0);
			}
		}
		
		JsArray<KeyframeTrack> tracks2=JavaScriptObject.createArray().cast();
		
		String trackName=".alphas";
		JsArrayNumber times=JavaScriptObject.createArray().cast();
		times.push(0);
		times.push(duration);
		times.push(duration*2);
		
		
		JsArrayNumber values=JavaScriptObject.createArray().cast();
		
		for(int i=0;i<canvasTexturePainter.getTextureLayers().size();i++){
			if(i==0 || i==1){
				//values.push(1);
				values.push(0);
			}else{
				values.push(0);
			}
		}
		
		//changed value from ui
		for(boolean value:animationBoolean){
			if(value){
				values.push(1);
			}else{
				values.push(0);
			}
			
		}
		
		
		for(int i=0;i<canvasTexturePainter.getTextureLayers().size();i++){
			if(i==0 || i==1){
				values.push(0);
				//values.push(1);
			}else{
				values.push(0);
			}
		}
		
		//have multiple?
		/*
		values.push(false);
		values.push(true);
		values.push(false);
		*/
		
		NumberKeyframeTrack track=THREE.NumberKeyframeTrack(trackName, times, values);
	
		tracks2.push(track);
		
		AnimationClip clip2=THREE.AnimationClip("test2", -1, tracks2);
		
		getMixer().uncacheClip(clip2);//same name cache that.
		
		AnimationMixerAction action=getMixer().clipAction(clip2,canvasTexturePainter.getTextureLayers());
		
		action.play();
	}

/* trying graduate color
 * private void insertSwitchTextureAnimations(double duration) {	
	for(int i=0;i<canvasTexturePainter.getTextureLayers().size();i++){
		canvasTexturePainter.getTextureLayers().setAlpha(i, 1);
	}
	
	for(int i=0;i<animationBoolean.size();i++){
		canvasTexturePainter.getTextureLayers().setVisible(i, animationBoolean.get(i));
	}
	
		canvasTexturePainter.update();//redraw here when need;
		
		textureSwitcher.put("canvas", canvasTexturePainter.getCanvasTexture());
		
		
		JsArray<KeyframeTrack> tracks2=JavaScriptObject.createArray().cast();
		
		String trackName="."+TextureSwitcher.SELECTION_KEY;
		
		//create time keys
		JsArrayNumber times=JavaScriptObject.createArray().cast();
		times.push(0);
		times.push(duration/2);
		times.push(duration+duration/2);
		times.push(duration*2);
		
		
		JsArrayString values=JavaScriptObject.createArray().cast();
		
		values.push("default");
		values.push("canvas");
		values.push("canvas");
		values.push("default");
		
		
		
		StringKeyframeTrack track=THREE.StringKeyframeTrack(trackName, times, values);
	
		tracks2.push(track);
		
		AnimationClip clip2=THREE.AnimationClip("test2", -1, tracks2);
		
		getMixer().uncacheClip(clip2);//always cacheed
		
		AnimationMixerAction action=getMixer().clipAction(clip2,textureSwitcher);
		
		action.play();
	}*/
	
	private void insertVisibleTextureAnimations(double duration) {
		
		for(int i=0;i<canvasTexturePainter.getTextureLayers().size();i++){
			canvasTexturePainter.getTextureLayers().setAlpha(i, 1);
			if(isInitialTexture(i)){
				canvasTexturePainter.getTextureLayers().setVisible(i, true);
			}else{
				canvasTexturePainter.getTextureLayers().setVisible(i, false);
			}
		}
		
		JsArray<KeyframeTrack> tracks2=JavaScriptObject.createArray().cast();
		
		String trackName=".visibles";
		JsArrayNumber times=JavaScriptObject.createArray().cast();
		
		times.push(0);
		times.push(duration/2);
		times.push(duration+duration/2);
		times.push(duration*2);
		
		
		JsArrayBoolean values=JavaScriptObject.createArray().cast();
		
		for(int i=0;i<canvasTexturePainter.getTextureLayers().size();i++){
			if(isInitialTexture(i)){
				values.push(true);
			}else{
				values.push(false);
			}
		}
		
		//twice
		for(boolean value:animationBoolean){
			values.push(value);
		}
		
		for(boolean value:animationBoolean){
			values.push(value);
		}
		
		
		for(int i=0;i<canvasTexturePainter.getTextureLayers().size();i++){
			if(isInitialTexture(i)){
				values.push(true);
			}else{
				values.push(false);
			}
		}
		
		//have multiple?
		/*
		values.push(false);
		values.push(true);
		values.push(false);
		*/
		
		BooleanKeyframeTrack track=THREE.BooleanKeyframeTrack(trackName, times, values);
	
		tracks2.push(track);
		
		AnimationClip clip2=THREE.AnimationClip("test2", -1, tracks2);
		
		getMixer().uncacheClip(clip2);//same name cache that.
		
		AnimationMixerAction action=getMixer().clipAction(clip2,canvasTexturePainter.getTextureLayers());
		
		action.play();
	}

	public void stopAnimation() {
		getMixer().stopAllAction();	
	}
	
	/*
	 	
	 */
	
	public static final native AnimationClip test(double duration)/*-{
	
	//console.log($wnd.THREE.IntepolateDiscrete);
	var times = [ 0, duration / 2, duration ], values = [ true, false, true ];

	var trackName = '.visible';
	
	

	var track = $wnd.THREE.BooleanKeyframeTrack( trackName, times, values );

	return new $wnd.THREE.AnimationClip( null, duration, [ track ] );
	}-*/;

	private BasicExpressionPanel basicPanel;
	private DataListPanel dataListPanel;
	public BasicExpressionPanel getBasicPanel() {
		return basicPanel;
	}

	public DataListPanel getDataListPanel() {
		return dataListPanel;
	}

	private TabPanel tab;
	private CombinePatternPanel combinePatternPanel;
	private PreferenceTab preferenceTab;
	private TextureTab textureTab;
	private double duration=1;
	private RecorderPanel recorderPanel;
	private MeshPhongMaterial material2;


	public List<Mbl3dData> getMbl3dDatas(){
		return dataListPanel.getDatas();
	}

	public void logMesh(){
		LogUtils.log(mesh);
	}


	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public CombinePatternPanel getCombinePatternPanel() {
		return combinePatternPanel;
	}

	@Override
	public void receive(Mbl3dExpression expression,boolean overwrite) {
		if(overwrite){
		dataListPanel.overwrite(expression);
		}else{
		dataListPanel.add(expression,null, null, null);
		
		}
		tab.selectTab(1);//data list
	}

	public void fixRenderSize() {
		boolean needH=false;
		boolean needW=false;
		if(SCREEN_WIDTH%2!=0){
			needW=true;
		}
		if(SCREEN_HEIGHT%2!=0){
			needH=true;
		}
		if(!needH && !needW){
			return;
		}
		double w=needW?SCREEN_WIDTH-1:SCREEN_WIDTH;
		double h=needH?SCREEN_HEIGHT-1:SCREEN_HEIGHT;
	
		renderer.setSize(w, h);
	}

	public void setHairScale(int scale) {
		hairScale=scale;//mesh is update from caller
	}
	
}
