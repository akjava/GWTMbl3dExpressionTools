package com.akjava.mbl3d.expression.client.datalist;

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
	
}
