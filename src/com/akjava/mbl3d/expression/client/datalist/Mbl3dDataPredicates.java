package com.akjava.mbl3d.expression.client.datalist;

import com.google.common.base.Predicate;

public class Mbl3dDataPredicates {

	public static  EyesOnly passEyesOnly(){
		return  EyesOnly.INSTANCE;
	}
	public enum EyesOnly implements Predicate<String>{
		INSTANCE;

		@Override
		public boolean apply(String input) {
			return input.contains("eyes");
		}
}
	
	public static  MouthOnly passMouthOnly(){
		return  MouthOnly.INSTANCE;
	}
	public enum MouthOnly implements Predicate<String>{
		INSTANCE;

		@Override
		public boolean apply(String input) {
			return input.contains("mouth");
		}
	}
	public static  BrowOnly passBrowOnly(){
		return  BrowOnly.INSTANCE;
	}
	public enum BrowOnly implements Predicate<String>{
		INSTANCE;

		@Override
		public boolean apply(String input) {
			return input.contains("brow");
		}
	}

}
