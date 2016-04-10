package com.akjava.mbl3d.client;

import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;


/**
 * @deprecated
 * maybe not used
 * @author aki
 *
 */
public  class ExpressionUsed{
	private String name;
	private String leftBrow;
	private String rightBrow;
	private String brow;
	private String mouth;
	private String eye;
	
	
	public static boolean isLeftBrow(String name){
		return name.indexOf("brow")!=-1 && name.indexOf("L")!=-1;
	}
	public static boolean isRightBrow(String name){
		return name.indexOf("brow")!=-1 && name.indexOf("R")!=-1;
	}
	public static boolean isBothBrow(String name){
		return name.indexOf("brow")!=-1 && name.indexOf("L")==-1 && name.indexOf("R")==-1;
	}
	
	public static boolean isEye(String name){
		return name.indexOf("eye")!=-1;
	}
	
	public static boolean isMouth(String name){
		return name.indexOf("mouth")!=-1;
	}
	
	public void setData(Mblb3dExpression data){
		
		this.name=data.getName();
		//LogUtils.log(name);
		
		for(String key:data.getKeys()){
			if(isLeftBrow(key)){
				if(leftBrow==null){
					leftBrow=fixName(key);
				}else{
					leftBrow=leftBrow+","+fixName(key);
				}
			}else if(isRightBrow(key)){
				if(rightBrow==null){
					rightBrow=fixName(key);
				}else{
					rightBrow=rightBrow+","+fixName(key);
				}
			}else if(isBothBrow(key)){
				if(brow==null){
					brow=fixName(key);
				}else{
					brow=brow+","+fixName(key);
				}
			}else if(isEye(key)){
				if(eye==null){
					eye=fixName(key);
				}else{
					eye=eye+","+fixName(key);
				}
			}else if(isMouth(key)){
				if(mouth==null){
					mouth=fixName(key);
				}else{
					mouth=mouth+","+fixName(key);
				}
			}
		}

	}
	
	private String fixName(String key){
		
		if(key.startsWith("Expressions_")){
			key=key.substring("Expressions_".length());
		}
		
		String number=CharMatcher.DIGIT.retainFrom(key);
		
		if(key.endsWith("min")){
			return number+"_min";
		}else if(key.endsWith("max")){
			return number+"_max";
		}
		
		
		return key;
	}
	
	public String toString(){
		List<String> lines=Lists.newArrayList();
		
		lines.add(name);
		lines.add(leftBrow==null?"":leftBrow);
		lines.add(rightBrow==null?"":rightBrow);
		lines.add(brow==null?"":brow);
		lines.add(eye==null?"":eye);
		lines.add(mouth==null?"":mouth);
		
		return Joiner.on("\t").join(lines);
	}
}