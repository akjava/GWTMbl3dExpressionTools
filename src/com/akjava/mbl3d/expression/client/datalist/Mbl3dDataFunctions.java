package com.akjava.mbl3d.expression.client.datalist;

import com.akjava.lib.common.utils.ValuesUtils;
import com.akjava.mbl3d.expression.client.BasicExpressionPanel;
import com.akjava.mbl3d.expression.client.Mbl3dExpression;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;

public class Mbl3dDataFunctions {

	
	public static  ShortenName getShortenName(){
		return  ShortenName.INSTANCE;
	}
	public enum ShortenName implements Function<String,String>{
		INSTANCE;
		@Override
		public String apply(String input) {
			if(input.startsWith("Expressions_")){
				input=input.substring("Expressions_".length());
			}
			
			String number=CharMatcher.DIGIT.retainFrom(input);
			
			if(input.endsWith("min")){
				return number+"_min";
			}else if(input.endsWith("max")){
				return number+"_max";
			}
			
			return input;
		}
	}
	
	public static  Mbl3dExpressionFunction getMbl3dExpressionFunction(){
		return  Mbl3dExpressionFunction.INSTANCE;
	}
	public enum Mbl3dExpressionFunction implements Function<Mbl3dData,Mbl3dExpression>{
		INSTANCE;
		@Override
		public Mbl3dExpression apply(Mbl3dData input) {
			Mbl3dExpression expression=new Mbl3dExpression(input.getName());
			for(String name:input.getValues().keySet()){
				if(name==null || name.isEmpty()){
					continue;
				}
				String value=input.getValues().get(name);
				if(value==null || value.isEmpty()){
					continue;
				}
				double v=ValuesUtils.toDouble(value, 0);
				if(v==0){
					continue;
				}
				expression.set(name, v);
			}
		
			return expression;
		}
	}
	
	
	public static class Mbl3dExpressionFunctionWithEyeModifier implements Function<Mbl3dData,Mbl3dExpression>{
		private double eyeModifier;
		public Mbl3dExpressionFunctionWithEyeModifier(double eyeModifier) {
			super();
			this.eyeModifier = eyeModifier;
		}
		@Override
		public Mbl3dExpression apply(Mbl3dData input) {
			Mbl3dExpression expression=new Mbl3dExpression(input.getName());
			for(String name:input.getValues().keySet()){
				if(name==null || name.isEmpty()){
					continue;
				}
				String value=input.getValues().get(name);
				if(value==null || value.isEmpty()){
					continue;
				}
				double v=ValuesUtils.toDouble(value, 0);
				if(v==0){
					continue;
				}
				
				//modifier
				if(BasicExpressionPanel.isNeedEyeModifier(name)){
					v=eyeModifier*v;
				}
				
				expression.set(name, v);
			}
		
			return expression;
		}
	}
	
}
