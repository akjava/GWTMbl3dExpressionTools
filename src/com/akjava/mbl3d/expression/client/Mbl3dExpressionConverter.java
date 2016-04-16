package com.akjava.mbl3d.expression.client;

import java.util.Set;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.JSParameter;
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
public class Mbl3dExpressionConverter extends Converter<String, Mbl3dExpression>{

	@Override
	protected Mbl3dExpression doForward(String text) {
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
		
		Mbl3dExpression data=new Mbl3dExpression(null);
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
	protected String doBackward(Mbl3dExpression data) {
		JSONObject root=new JSONObject();
		
		JSParameter parameter=JSParameter.createParameter();
		for(String key:data.getKeys()){
			double value=data.get(key);
			if(value==0){
				continue;
			}
			double convertedValue=0;
			if(key.endsWith("_min")){
				convertedValue=0.5-value/2;
			}else if(key.endsWith("_max")){
				convertedValue=0.5+value/2;
			}else{
				LogUtils.log("invalid key skipped:"+key);
				continue;
			}
			String convertedKey=key.substring(0,key.length()-4);
			if(parameter.exists(convertedKey)){
				LogUtils.log("invalidly conflict key.overwrited:"+key);
			}
			parameter.set(convertedKey, convertedValue);
		}
		
		JSONObject structuralValue=new JSONObject(parameter);
		root.put("structural", structuralValue);
		
		return root.toString();
	}

}
