package com.akjava.mbl3d.expression.client;

import java.util.List;

import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public   class CombinedExpression{
		private List<Mbl3dExpression> brows;
		private List<Mbl3dExpression> eyes;
		private List<Mbl3dExpression> mouths;
		private List<String> indexs=Lists.newArrayList();
		
		public int getIndexSize(){
			return indexs.size();
		}
		
		public CombinedExpression(){
			
			initBrows();
			initEyes();
			initMouths();
			//TODO eye,mouth
			
			//test index
			for(int k=0;k<mouths.size();k++){
			for(int j=0;j<eyes.size();j++){
			for(int i=0;i<brows.size();i++){
				indexs.add(String.valueOf(i)+"-"+String.valueOf(j)+"-"+String.valueOf(k));
				}
			}
			}
			
		}
		
		private void initEyes() {
			String type=null;
			//singles
			eyes=Lists.newArrayList();
			eyes.add(new Mbl3dExpression("eye"));//empty
			
			type="eyes";
			
			for(int i=0;i<6;i++){
				String direction="max";
				for(int j=0;j<2;j++){
					if(i==5 && j==1){
						continue;// 6 has no min
					}
					
					if(j==1){
						direction="min";
					}
					Mbl3dExpression expression=new Mbl3dExpression("eyes");
					String name=Strings.padStart(String.valueOf(i+1), 2, '0');
					String key=header+type+name+"_"+direction;
					expression.set(key, 1);
					
					eyes.add(expression);
				}
				
			}
		}
		
		private void initMouths() {
			String type=null;
			//singles
			mouths=Lists.newArrayList();
			mouths.add(new Mbl3dExpression("mouth"));//empty
			
			type="mouth";
			
			for(int i=0;i<11;i++){
				if(i==2 ){
					continue; //skip3
				}
				
				String direction="max";
				for(int j=0;j<2;j++){
					
					if(i==4 && j==0){
						continue;	//skip 5max
					}
					
					if(i==9 && j==1){
						continue;	//skip 10 min
					}
					
					if(i==10 && j==1){
						continue;	//skip 11 min
					}
					
					
					if(j==1){
						direction="min";
					}
					Mbl3dExpression expression=new Mbl3dExpression("mouth");
					String name=Strings.padStart(String.valueOf(i+1), 2, '0');
					String key=header+type+name+"_"+direction;
					expression.set(key, 1);
					
					mouths.add(expression);
				}
				
			}
		}

		public Mbl3dExpression getAt(int index){
			String key=indexs.get(index);
			return parseIndex(key);
		}
		
		public Mbl3dExpression parseIndex(String key){
			//LogUtils.log(key);
			List<String> splittedKey=Lists.newArrayList(Splitter.on('-').split(key));
			
			Mbl3dExpression brow=brows.get(ValuesUtils.toInt(splittedKey.get(0), 0));
			
			Mbl3dExpression eye=null;
			if(splittedKey.size()>1){
				eye=eyes.get(ValuesUtils.toInt(splittedKey.get(1), 0));
				
			}
			
			Mbl3dExpression mouth=null;
			if(splittedKey.size()>2){
				mouth=mouths.get(ValuesUtils.toInt(splittedKey.get(2), 0));
			}
			
			return Mbl3dExpression.merge(brow,eye,mouth);
		}
		

		String header="Expressions_";
		
		public void initBrows() {

			
			String type=null;
			//singles
			brows=Lists.newArrayList();
			brows.add(new Mbl3dExpression("brow"));//empty
			
			type="brow";
			List<String> browNames=Lists.newArrayList();
			for(int i=0;i<2;i++){
				String direction="max";
				for(int j=0;j<2;j++){
					if(j==1){
						direction="min";
					}
					Mbl3dExpression expression=new Mbl3dExpression("brow");
					String name=Strings.padStart(String.valueOf(i+1), 2, '0')+"L";
					String key=header+type+name+"_"+direction;
					expression.set(key, 1);
					browNames.add(key);
					brows.add(expression);
				}
				
			}
			
			//same
			for(int i=0;i<2;i++){
				String direction="max";
				for(int j=0;j<2;j++){
					if(j==1){
						direction="min";
					}
					Mbl3dExpression expression=new Mbl3dExpression("brow");
					String name=Strings.padStart(String.valueOf(i+1), 2, '0')+"L";
					String key=header+type+name+"_"+direction;
					expression.set(key, 1);
					expression.set(key.replace("L", "R"), 1);
					brows.add(expression);
				}
				
			}
			
			//combine
			String first=browNames.get(0);
			for(int i=1;i<4;i++){
				String second=browNames.get(i).replace("L", "R");
				brows.add(makeMblb3dExpression("brow", first,second));
			}
			first=browNames.get(1);
			for(int i=2;i<4;i++){
				String second=browNames.get(i).replace("L", "R");
				brows.add(makeMblb3dExpression("brow", first,second));
			}
			
			first=browNames.get(2);
			for(int i=3;i<4;i++){
				String second=browNames.get(i).replace("L", "R");
				brows.add(makeMblb3dExpression("brow", first,second));
			}
			
			//both
			String direction="max";
			for(int j=0;j<2;j++){
				if(j==1){
					direction="min";
				}
				Mbl3dExpression expression=new Mbl3dExpression("brow");
				String name="03";
				String key=header+type+name+"_"+direction;
				expression.set(key, 1);
				
				brows.add(expression);
			}
		}
		
		public Mbl3dExpression makeMblb3dExpression(String name,String... keys){
			Mbl3dExpression expression=new Mbl3dExpression(name);
			for(String key:keys){
				expression.set(key, 1);
			}
			return expression;
		}
	}