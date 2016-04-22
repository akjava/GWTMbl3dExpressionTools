package com.akjava.mbl3d.expression.client.texture;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.textures.CanvasTexture;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.Maps;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;

/**
 * not bad but slow.
 * @author aki
 *
 */
public class CanvasTexturePainter {
	private TextureLayers textureLayers;//for animation-mixer control
	private Canvas canvas;
	public Canvas getCanvas() {
		return canvas;
	}

	private String lastKey="";
	private CanvasTexture canvasTexture;
	private MapJoiner joiner=Joiner.on("-").withKeyValueSeparator("=");
	private Map<Integer,String> map=Maps.newHashMap();
	private List<Texture> textures;
	
	public CanvasTexture getCanvasTexture() {
		return canvasTexture;
	}

	private int w,h;
	
	//textures must be loaded
	public CanvasTexturePainter(List<Texture> textures,@Nullable Canvas canvas){
		this.textures=textures;
		checkNotNull(textures,"CanvasTexturePainter:not allow null");
		checkArgument(textures.size()>0,"CanvasTexturePainter:at least need 1 texture");
		
		textureLayers=TextureLayers.create();
		
		
		//get size from first one.
		if(canvas==null){
		w=textures.get(0).getImage().getWidth();
		h=textures.get(0).getImage().getHeight();
		if(w==0 || h==0){
			LogUtils.log("CanvasTexturePainter:invalid size:"+w+"x"+h+".possible not loaded");
		}
			this.canvas=CanvasUtils.createCanvas(w,h);
		}else{
			//not test yet
			this.canvas=canvas;
			w=canvas.getCoordinateSpaceWidth();
			h=canvas.getCoordinateSpaceHeight();
		}
		canvasTexture=THREE.CanvasTexture(this.canvas.getCanvasElement());
		for(int i=0;i<textures.size();i++){
			if(i==0){
				textureLayers.add(true, 1);
			}else{
				textureLayers.add(false, 1);
			}
		}
		
		update();
	}
	
	public List<Texture> getTextures() {
		return textures;
	}

	public TextureLayers getTextureLayers() {
		return textureLayers;
	}

	public void update(){
		String key=generateKey();//is this slow?
		if(!lastKey.equals(key)){
			lastKey=key;
			CanvasUtils.clear(canvas);
			for(int i=0;i<textureLayers.size();i++){
				if(textureLayers.isVisible(i)){
				canvas.getContext2d().setGlobalAlpha(textureLayers.getAlpha(i));
				ImageElement image=textures.get(i).getImage();
				if(image==null){
					LogUtils.log("CanvasTexturePainter:image is null:"+i);
				}
				canvas.getContext2d().drawImage(image, 0, 0,w,h);//resize
				}
			}
			canvasTexture.setNeedsUpdate(true);
		}
		//get layers & make keys.if changed repaint & needUpdate
	}
	public String generateKey(){
		if(textureLayers==null){
			return "";
		}
		for(int i=0;i<textureLayers.size();i++){
			map.put(i, String.valueOf(textureLayers.isVisible(i))+String.valueOf(textureLayers.getAlpha(i)));
		}
		return joiner.join(map);
	}
	
	
}
