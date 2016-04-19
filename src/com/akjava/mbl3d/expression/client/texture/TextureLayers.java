package com.akjava.mbl3d.expression.client.texture;

import com.google.gwt.core.client.JavaScriptObject;

public class TextureLayers extends JavaScriptObject{
protected TextureLayers(){}

public static final native TextureLayers create()/*-{
return {visibles:[],alphas:[]};
}-*/;

public final native void add(boolean visible,double value)/*-{
this.visibles.push(visible);
this.alphas.push(value);
}-*/;

public final native int size()/*-{
	return this.visibles.length;
}-*/;

public final native boolean isVisible(int index)/*-{
return this.visibles[index];
}-*/;

public final native double getAlpha(int index)/*-{
return this.alphas[index];
}-*/;

public final  native void setVisible(int index,boolean  value)/*-{
this.visibles[index]=value;
}-*/;
public final  native void setAlphas(int index,double  value)/*-{
this.alphas[index]=value;
}-*/;

}
