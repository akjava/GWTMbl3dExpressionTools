package com.akjava.mbl3d.expression.client.texture;

import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * trying have layer
 * @author aki
 *
 */
public class TextureSwitcher extends JavaScriptObject{
protected TextureSwitcher(){}
public static final String SELECTION_KEY="selection";

public final  native String getTarget()/*-{
return this.target;
}-*/;

public final  native void setTarget(String  param)/*-{
this.target=param;
}-*/;
public final  native Material getMaterial()/*-{
return this.material;
}-*/;

public final  native void setMaterial(Material  param)/*-{
this.material=param;
}-*/;
public final  native String getLastSelection()/*-{
return this.lastSelection;
}-*/;

public final  native void setLastSelection(String  param)/*-{
this.lastSelection=param;
}-*/;

public static final native TextureSwitcher create()/*-{
return {selection:[""],target:"map",lastSelection:"",textures:{}};
}-*/;

public final native void put(String key,Texture texture)/*-{
this.textures[key]=texture;
}-*/;
public final native Texture getTexture(String key)/*-{
return this.textures[key];
}-*/;

public final void update(){
	String selection=getSeletion();
	if(!getLastSelection().equals(selection)){
		//Texture texture=getTexture(selection);
		updateTexture(getMaterial(),getTarget(),getTexture(selection));
		//LogUtils.log("TextureSwitcher:updated:"+selection);
		//LogUtils.log(getTexture(selection));
	}
	setLastSelection(selection);
}

public final native void updateTexture(Material material,String target,Texture texture)/*-{
material[target]=texture;
material.needsUpdate=true;
}-*/;

//for avoid Uncaught TypeError: Cannot assign to read only property '0' of string 'default'

public final native String getSeletion()/*-{
return this.selection[0];
}-*/;

public final native void setSeletion(String selection)/*-{
this.selection[0]=selection;
}-*/;

}
