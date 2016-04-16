package com.akjava.mbl3d.expression.client.datalist;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;

public class Mbl3dDataUtils {
	public static String getEyesKey(@Nullable Mbl3dData data){
		if(data==null){
			return "";
		}
		String key=Joiner.on("-").join(
				FluentIterable.from(data.getValues().keySet()).filter(Mbl3dDataPredicates.passEyesOnly()).transform(Mbl3dDataFunctions.getShortenName())
				);
		return key;
	}
	public static String getMouthKey(@Nullable Mbl3dData data){
		if(data==null){
			return "";
		}
		String key=Joiner.on("-").join(
				FluentIterable.from(data.getValues().keySet()).filter(Mbl3dDataPredicates.passMouthOnly()).transform(Mbl3dDataFunctions.getShortenName())
				);
		return key;
	}
}
