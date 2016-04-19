package com.akjava.mbl3d.expression.client;

import java.util.List;

import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.experimental.AsyncMultiCaller;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.JSParameter;
import com.akjava.gwt.three.client.gwt.core.BoundingBox;
import com.akjava.gwt.three.client.gwt.loader.JSONLoaderObject;
import com.akjava.gwt.three.client.gwt.renderers.WebGLRendererParameter;
import com.akjava.gwt.three.client.java.utils.MultiTextureLoader;
import com.akjava.gwt.three.client.java.utils.MultiTextureLoader.MultiTextureLoaderListener;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.animation.AnimationMixer;
import com.akjava.gwt.three.client.js.animation.tracks.NumberKeyframeTrack;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.core.Clock;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.lights.AmbientLight;
import com.akjava.gwt.three.client.js.lights.DirectionalLight;
import com.akjava.gwt.three.client.js.loaders.JSONLoader;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.materials.MeshBasicMaterial;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.materials.MultiMaterial;
import com.akjava.gwt.three.client.js.math.THREEMath;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataPredicates;
import com.akjava.mbl3d.expression.client.texture.CanvasTexturePainter;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
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
	@Override
	public WebGLRendererParameter createRendererParameter() {
		return GWTParamUtils.WebGLRenderer().preserveDrawingBuffer(true);
	}
	
	public String toImageDataUrl(){
		return renderer.gwtPngDataUrl();
	}
	

	@Override
	public void onInitializedThree() {
		INSTANCE=this;
		//LogUtils.log("onInitializedThree");
		renderer.setClearColor(0);//default is black?
		
		 windowHalfX= (int)(SCREEN_WIDTH/2);
		 windowHalfY= (int)(SCREEN_HEIGHT/2);
		 
		
		rendererContainer.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				onDocumentMouseMove(event);
			}
		});
		rendererContainer.addMouseWheelHandler(new MouseWheelHandler() {
			
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				camera.getPosition().gwtIncrementZ(event.getDeltaY()*3);//for chrome only
			}
		});
		
		//light
		
		AmbientLight ambient = THREE.AmbientLight( 0xaaaaaa );//var ambient = new THREE.AmbientLight( 0xffffff );
		scene.add( ambient );

		DirectionalLight directionalLight = THREE.DirectionalLight( 0x666666 );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		directionalLight.getPosition().set( -1, 1, 1 ).normalize();//directionalLight.position.set( -1, 1, 1 ).normalize();
		scene.add( directionalLight );
		
		//String url= "models/mbl3d/morph.json";//var url= "morph.json";
				String url= "models/mbl3d/white5.json#"+System.currentTimeMillis();//var url= "morph.json";
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
						//double x=-20, y=-1270,z= -300,s= 800;

						double x=-0, y=-460,z= -100,s= 290;
						
						List<String> urls=Lists.newArrayList("models/mbl3d/eyeonly2.png",
															 "models/mbl3d/test.png"
								);
						
						
						final MeshPhongMaterial material=THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial()
								.morphTargets(true)
								);
						
						
						
						new MultiTextureLoader().load(urls, new MultiTextureLoaderListener() {
							@Override
							public void onLoad(List<Texture> textures) {
								CanvasTexturePainter painter=new CanvasTexturePainter(textures,null);
								material.setMap(painter.getCanvasTexture());
								//painter.getTextureLayers().setVisible(0, false);
								painter.getTextureLayers().setVisible(1, true);
								painter.update();
								material.setNeedsUpdate(true);
							}
							
							@Override
							public void onError(List<String> messages) {
								for(String message:messages){
									LogUtils.log(message);
								}
							}
						});
						
						
						
						
						
						/*
						Material material2=THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial().map(
								THREE.TextureLoader().load("models/mbl3d/test.png"))
								.morphTargets(true)
								.transparent(true)
								
								);
						*/
						
						
						
						JsArray<Material> materials=loadedObject.getMaterials();
						for(int i=0;i<materials.length();i++){
							MeshBasicMaterial m=materials.get(i).cast();//need cast GWT problem
							m.setMorphTargets(true);
							
						}
						JsArray<Material> filterd=JavaScriptUtils.createJSArray();
						//filterd.push(material);
						
						for(int i=0;i<materials.length();i++){
							if(materials.get(i).getName().equals("White")){//eye & tooth
								//LogUtils.log(i+" white");
								//materials.set(i, material);
								filterd.push(material);
								continue;
							}
							
							//body
							if(materials.get(i).getName().equals("Pink02")){
								//LogUtils.log(i+" pink02");
								//this make problem ,i know when transparent?
								//materials.get(i).setVisible(false);//allow modify
								//materials.set(i, material);
								filterd.push(material);
								//filterd.push(THREE.MeshBasicMaterial());
								continue;
							}
							filterd.push(materials.get(i));
						}
						
						//var mat=THREE.MultiMaterial( materials);//MultiMaterial mat=THREE.MultiMaterial( materials);//var mat=new THREE.MultiMaterial( materials);
						
					
						//MultiMaterial mat=THREE.MultiMaterial(materials );
						
						MultiMaterial mat=THREE.MultiMaterial(filterd);
						LogUtils.log(mat);
						
						//materials.push(material);
						//material=mat.cast();
						
						
						mesh = THREE.SkinnedMesh( geometry, mat );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = new THREE.SkinnedMesh( geometry, mat );
						mesh.setName("model");//mesh.setName("model");//mesh.setName("model");//mesh.name = "model";
						mesh.getPosition().set( x, y - bb.getMin().getY() * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.position.set( x, y - bb.min.y * s, z );
						mesh.getScale().set( s, s, s );//mesh.getScale().set( s, s, s );//mesh.getScale().set( s, s, s );//mesh.scale.set( s, s, s );
						scene.add( mesh );
						
						
						mixer = THREE.AnimationMixer(mesh);
						
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
	
				private Panel createBasicPanel(){
					basicPanel = new BasicExpressionPanel(mesh,this);
					
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
	
				private void initUi() {
					clock=THREE.Clock();
					
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
					
					tab.add(createPatternTab(),"Pattern");
					
					
					
					
					
					tab.add(cratePreferenceTab(),"Misc");
					
					tab.selectTab(1);
					
					
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
					PreferenceTab preferenceTab=new PreferenceTab(dataListPanel);
					//if need set
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
		PerspectiveCamera camera = THREE.PerspectiveCamera(45, getWindowInnerWidth()/getWindowInnerHeight(), 1, 4000);
		camera.getPosition().set(0, 0, 25);
		return camera;
	}
	@Override
	public void onWindowResize() {
		super.onWindowResize();
		 windowHalfX= (int)(SCREEN_WIDTH/2);
		 windowHalfY= (int)(SCREEN_HEIGHT/2);
	}
	
	@Override
	public void animate(double timestamp) {
		//camera.getPosition().gwtIncrementX(( - mouseX - camera.getPosition().getX()) * .001);//camera.position.y += ( - mouseY - camera.position.y ) * .01;
		camera.lookAt(scene.getPosition());//look at 0
	
		if(mixer!=null){
			mixer.update(clock.getDelta());
		}
		
		renderer.render(scene, camera);
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


	

	public void playAnimation(Mbl3dExpression expression,boolean filterBrow,boolean filterEyes,boolean filterMouth) {
		
		stopAnimation();
		
		if(mesh==null){
			//not ready
			return;
		}
		
		JSParameter param=mesh.getMorphTargetDictionary().cast();
		
		double duration=1;
		
		JsArray<NumberKeyframeTrack> tracks=JavaScriptObject.createArray().cast();
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
			
			double wait=0.3;
			
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
		}

		
		AnimationClip clip=THREE.AnimationClip("test", -1, tracks);
		Mbl3dExpressionEntryPoint.INSTANCE.getMixer().uncacheClip(clip);//same name cache that.
		Mbl3dExpressionEntryPoint.INSTANCE.getMixer().clipAction(clip).play();
	}
	
	public void stopAnimation() {
		getMixer().stopAllAction();	
	}
	
	
	

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
	
}
