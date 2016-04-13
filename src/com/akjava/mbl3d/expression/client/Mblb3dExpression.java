package com.akjava.mbl3d.expression.client;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class Mblb3dExpression {


private Map<String,Double> map;
public Map<String, Double> getMap() {
	return map;
}

private String name;

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public Mblb3dExpression(){
	this("");
}
public Mblb3dExpression(String name){
	this.name=name;
	map=new LinkedHashMap<String, Double>();
}

public boolean containsKey(String key){
	return map.containsKey(key);
}

public double get(String key){
	return map.get(key);
}

public void set(String key,double value){
	map.put(key, value);
}

public Set<String> getKeys(){
	return map.keySet();
}

public String toString(){
	return name+":"+Joiner.on(",").withKeyValueSeparator("=").join(map);
}
	
public static Mblb3dExpression merge(Mblb3dExpression... expressions){
	Mblb3dExpression base=new Mblb3dExpression("");
	for(Mblb3dExpression expression:expressions){
		if(expression==null){
			continue;
		}
		//overwrite
		for(String key:expression.getKeys()){
			if(base.containsKey(key)){
				LogUtils.log("warning Mblb3dExpression merge.already exist key:"+key);
			}
			base.set(key,expression.get(key));
		}
	}
	return base;
}
public static ClosedResult findClosed(Mblb3dExpression source,List<Mblb3dExpression> expressions){
	double length=Double.MAX_VALUE;
	Mblb3dExpression closed=expressions.get(0);
	
	for(Mblb3dExpression expression:expressions){
		if(expression==null){
			continue;
		}
		double l=source.length(expression);
		if(l<length){
			length=l;
			closed=expression;
		}
	}
	
	return new ClosedResult(closed,length);
}
public static class ClosedResult{
	Mblb3dExpression expression;
	public ClosedResult(Mblb3dExpression closed, double length) {
		super();
		this.expression = closed;
		this.length = length;
	}
	
	public Mblb3dExpression getExpression() {
		return expression;
	}

	public void setExpression(Mblb3dExpression expression) {
		this.expression = expression;
	}

	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	double length;
}

public double length(Mblb3dExpression expression){
	double length=0;
	Set<String> allKeys=Sets.newHashSet();
	for(String key:getKeys()){
		allKeys.add(key);
	}
	for(String key:expression.getKeys()){
		allKeys.add(key);
	}
	for(String key:allKeys){
		double a=containsKey(key)?get(key):0;
		double b=expression.containsKey(key)?expression.get(key):0;
		length+=Math.abs(a-b);
	}
	return length;
}


}
