package com.akjava.mbl3d.expression.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

public class EmotionPredicates {

	public static Predicate<String> filterEmotion(final EmotionsData emotionData,final String filter){
		checkNotNull(emotionData,"filterEmotion:need emotions");
		checkNotNull(filter,"filterEmotion:need filter");
		
		return new Predicate<String>() {
			@Override
			public boolean apply(@Nullable String input) {
				if(Character.isUpperCase(filter.charAt(0))){
					List<String> secondaries=emotionData.getSecondaryNameByPrimaryName(filter);
					return secondaries.contains(input);
				}else {
					return filter.equals(input);
				}
			}
		};
	}
}
