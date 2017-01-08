package com.akjava.mbl3d.expression.client;


/*
 * all keys start with 'Expressions_'
 */
public class Mbl3dMorphtargetsModifier extends MorphtargetsModifier{
	public Double get(String key){
		return super.get(fixKey(key));
	}
	
	private String fixKey(String key){
		if(!key.startsWith("Expressions_")){
			key="Expressions_"+key;
		}
		return key;
	}
	
	public void remove(String key){
		super.remove(fixKey(key));
	}

	public void set(String key,double value){
		
		super.set(fixKey(key), value);
	}
	
}
