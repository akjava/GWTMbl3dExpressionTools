package com.akjava.mbl3d.expression.client.datalist;

import com.akjava.mbl3d.expression.client.EmotionPredicates;
import com.akjava.mbl3d.expression.client.EmotionsData;
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
	
	public static Predicate<Mbl3dData> emotionTypePredicate(final String type,final EmotionsData data){
		return new Predicate<Mbl3dData>() {
			Predicate<String> typePredicate=EmotionPredicates.filterEmotion(data, type);
			@Override
			public boolean apply(Mbl3dData input) {
				return typePredicate.apply(input.getType());
			}
		};
	}
	
}
