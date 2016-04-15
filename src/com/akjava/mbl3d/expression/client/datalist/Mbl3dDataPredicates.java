package com.akjava.mbl3d.expression.client.datalist;

import com.google.common.base.Predicate;

public class Mbl3dDataPredicates {

	public static  EyesOnly getEyesOnly(){
		return  EyesOnly.INSTANCE;
	}
	public enum EyesOnly implements Predicate<String>{
		INSTANCE;

		@Override
		public boolean apply(String input) {
			return input.contains("eyes");
		}
}
	
	public static  MouthOnly getMouthOnly(){
		return  MouthOnly.INSTANCE;
	}
	public enum MouthOnly implements Predicate<String>{
		INSTANCE;

		@Override
		public boolean apply(String input) {
			return input.contains("mouth");
		}
}

}
