package com.akjava.mbl3d.expression.client;


/*
 * have fix key
 */
public class Mbl3dMorphtargetsModifier extends MorphtargetsModifier{
	public Double get(String key){
		return get(fixKey(key));
	}
	
	private String fixKey(String key){
		if(!key.startsWith("Expressions_")){
			key="Expressions_"+key;
		}
		return key;
	}
	
	public void remove(String key){
		remove(fixKey(key));
	}

	public void set(String key,double value){
		
		set(fixKey(key), value);
	}
	
}
