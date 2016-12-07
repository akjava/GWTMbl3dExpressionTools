package com.akjava.mbl3d.expression.client;

import java.util.List;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.MultiImageElementLoader;
import com.akjava.gwt.lib.client.MultiImageElementLoader.MultiImageElementListener;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.textures.CanvasTexture;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;

public class TextureMontage {
	private String baseDirectory;
	private Material material;
	public TextureMontage(String baseDirectory, Canvas canvas,Material material) {
		super();
		if(!baseDirectory.endsWith("/")){
			baseDirectory+="/";
		}
		this.baseDirectory = baseDirectory;
		this.canvas = canvas;
		canvasTexture=THREE.CanvasTexture(this.canvas.getCanvasElement());
		this.material=material;
	}

	private Canvas canvas;
	private CanvasTexture canvasTexture;
	
	public Canvas getCanvas() {
		return canvas;
	}

	public CanvasTexture getCanvasTexture() {
		return canvasTexture;
	}

	private List<TextureMontageData> textureMontageDatas;
	
	public void setTextureMontageDatas(List<TextureMontageData> textureMontageDatas) {
		this.textureMontageDatas = textureMontageDatas;
	}

	private String lastKey="";
	
	private boolean updating;
	public void update(){
		if(updating){
			return;
		}
		
		final String key=generateKeys();
		//LogUtils.log(key);
		if(key.equals(lastKey)){
			return;
		}
		
		//LogUtils.log("last key changed");
		
		updating=true;
		
		CanvasUtils.clear(canvas);
		final int w=canvas.getCoordinateSpaceWidth();
		final int h=canvas.getCoordinateSpaceHeight();
		List<String> paths=Lists.newArrayList();
		for(TextureMontageData data:textureMontageDatas){
			if(!data.isEnabled()){
				continue;
			}
			
			if(data.getType()==TextureMontageData.TYPE_LIST){
				paths.add(baseDirectory+data.getValue());
			}else if(data.getType()==TextureMontageData.TYPE_COLOR){
				//TODO,should use toDataUrl?
				LogUtils.log("TODO:support type color");
			}else{
				//never
			}
		}
		
		//dynamic load
		
		new MultiImageElementLoader().loadImages(paths, new MultiImageElementListener() {
			
			@Override
			public void onLoad(List<String> successPaths, List<ImageElement> imageElements) {
				for(ImageElement image:imageElements){
					CanvasUtils.drawImage(canvas, image, 0, 0,w,h);
					
				}
			
				lastKey=key;
				updating=false;
				canvasTexture.setNeedsUpdate(true);
				LogUtils.log("TextureMontage:texture-updated lastKey="+lastKey);
				material.setNeedsUpdate(true);
			}
			
			@Override
			public void onError(List<String> errorPaths) {
				for(String path:errorPaths){
					LogUtils.log("TextureMontage load-faild:"+path);
				}
			}
		});
		
	}
	
	//check is uniq?
	public String generateKeys(){
		List<String> keys=Lists.newArrayList();
		for(TextureMontageData data:textureMontageDatas){
			keys.add(data.generateKey());
		}
		return Joiner.on(",").join(keys);
	}
	
	
	
	
}
