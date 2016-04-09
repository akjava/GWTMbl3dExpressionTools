package com.akjava.mbl3d.client;

import java.util.Set;

import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.base.Converter;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * 1.1 based
 * @author aki
 *
 */
public class Mbl3dExpressionConverter extends Converter<String, Mblb3dExpression>{

	@Override
	protected Mblb3dExpression doForward(String text) {
		JSONValue value=JSONParser.parseStrict(text);
		JSONObject object=value.isObject();
		if(object==null){
			LogUtils.log("Mblb3dExpression parse faild.not object:"+text);
			return null;
		}
		JSONValue structuralValue=object.get("structural");
		JSONObject structuralObject=structuralValue.isObject();
		if(structuralObject==null){
			LogUtils.log("Mblb3dExpression parse faild.structural not found:"+text);
			return null;
		}
		Set<String> keys=structuralObject.keySet();
		
		Mblb3dExpression data=new Mblb3dExpression(null);
		for(String key:keys){
			JSONValue keyValue=structuralObject.get(key);
			JSONNumber keyNumber=keyValue.isNumber();
			if(keyNumber==null){
				LogUtils.log("Mblb3dExpression parse faild.not number:"+key+","+keyValue);
			}else{
				String convertedName;
				double number=keyNumber.doubleValue();
				double convertedNumber;
				
				if(number<0.5){
					convertedName=key+"_min";
					convertedNumber=1.0-number*2;
				}else{
					convertedName=key+"_max";
					convertedNumber=(number-0.5)*2;
				}
				
				data.set(convertedName, convertedNumber);
			}
		}
		return data;
	}

	@Override
	protected String doBackward(Mblb3dExpression b) {
		// TODO Auto-generated method stub
		return null;
	}

}
