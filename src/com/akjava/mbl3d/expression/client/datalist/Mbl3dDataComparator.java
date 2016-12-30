package com.akjava.mbl3d.expression.client.datalist;

import java.util.Comparator;
import java.util.List;

import com.akjava.mbl3d.expression.client.Emotion;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;


public class Mbl3dDataComparator implements Comparator<Mbl3dData>{
	private  int order;
	private List<Emotion> emotions;
	
	public Mbl3dDataComparator(List<Emotion> emotions) {
		super();
		this.emotions=emotions;
	}
	public void setEmotions(List<Emotion> emotions) {
		this.emotions = emotions;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public static final int ORDER_ID=0;
	public static final int ORDER_ID_DESC=1;
	public static final int ORDER_AZ=2;
	public static final int ORDER_ZA=3;
	public static final int ORDER_TYPE=4;
	public static final int ORDER_BROW=5;
	public static final int ORDER_EYES=6;
	public static final int ORDER_MOUTH=7;
	@Override
	public int compare(Mbl3dData o1, Mbl3dData o2) {
		/*
		 * noisy
		if(emotions==null){
			LogUtils.log("Mbl3dDataComparator:need emotions");
		}
		*/
		
		if(order==ORDER_ID){
			return o1.getId()-o2.getId();
		}else if(order==ORDER_ID_DESC){
			return o2.getId()-o1.getId();
		}
		
		
		
		
		
		
		List<ComparaHelper<Mbl3dData>> comparables=Lists.newArrayList();
		
		
		
		ComparaHelper<Mbl3dData> nameGetter=new ComparaHelper<Mbl3dData>(){
			@Override
			public ComparisonChain compare(ComparisonChain chain, Mbl3dData o1, Mbl3dData o2) {
				return chain.compare(o1.getName()!=null?o1.getName():"", o2.getName()!=null?o2.getName():"");
			}
		};
		comparables.add(nameGetter);
		
		ComparaHelper<Mbl3dData> emotionGetter=new ComparaHelper<Mbl3dData>(){

			@Override
			public ComparisonChain compare(ComparisonChain chain, Mbl3dData data1, Mbl3dData data2) {
				return chain.compare(findEmotionAt(data1),findEmotionAt(data2));
			}
			
		};
		comparables.add(emotionGetter);
		
		ComparaHelper<Mbl3dData> eyesGetter=new ComparaHelper<Mbl3dData>(){
			@Override
			public ComparisonChain compare(ComparisonChain chain, Mbl3dData o1, Mbl3dData o2) {
				return chain.compare(Mbl3dDataUtils.getEyesKey(o1), Mbl3dDataUtils.getEyesKey(o2));
			}
		};
		comparables.add(eyesGetter);
		
		ComparaHelper<Mbl3dData> mouthGetter=new ComparaHelper<Mbl3dData>(){
			@Override
			public ComparisonChain compare(ComparisonChain chain, Mbl3dData o1, Mbl3dData o2) {
				return chain.compare(Mbl3dDataUtils.getMouthKey(o1), Mbl3dDataUtils.getMouthKey(o2));
			}
		};
		comparables.add(mouthGetter);
		
		ComparaHelper<Mbl3dData> browGetter=new ComparaHelper<Mbl3dData>(){
			@Override
			public ComparisonChain compare(ComparisonChain chain, Mbl3dData o1, Mbl3dData o2) {
				return chain.compare(Mbl3dDataUtils.getBrowKey(o1), Mbl3dDataUtils.getBrowKey(o2));
			}
		};
		comparables.add(browGetter);
		
		ComparaHelper<Mbl3dData> descriptionGetter=new ComparaHelper<Mbl3dData>(){
			@Override
			public ComparisonChain compare(ComparisonChain chain, Mbl3dData o1, Mbl3dData o2) {
				return chain.compare(o1.getDescription()!=null?o1.getDescription():"", o2.getDescription()!=null?o2.getDescription():"");
			}
		};
		comparables.add(descriptionGetter);
		
		ComparaHelper<Mbl3dData> idGetter=new ComparaHelper<Mbl3dData>(){
			@Override
			public ComparisonChain compare(ComparisonChain chain, Mbl3dData o1, Mbl3dData o2) {
				return chain.compare(o1.getId(), o2.getId());
			}
		};
		comparables.add(idGetter);
		
		
		//to support primary & secondary sort.
		
		if(order==ORDER_AZ){
			comparables.remove(nameGetter);
			comparables.add(0, nameGetter);
		}else if(order==ORDER_ZA){
			comparables.remove(nameGetter);
			ComparaHelper<Mbl3dData> nameGetterDesc=new ComparaHelper<Mbl3dData>(){
				@Override
				public ComparisonChain compare(ComparisonChain chain, Mbl3dData o1, Mbl3dData o2) {
					return chain.compare(o2.getName()!=null?o2.getName():"", o1.getName()!=null?o1.getName():"");
				}
			};
			comparables.add(0,nameGetterDesc);
			
		}else if(order==ORDER_TYPE){
			comparables.remove(emotionGetter);
			comparables.add(0, emotionGetter);
			
			
			//return findEmotionAt(o1) -  findEmotionAt(o2);
			/*
			 * some how not good at null handling(getName());
			 */
			
			/*
			return ComparisonChain.start().compare(findEmotionAt(o1), findEmotionAt(o2))
					.compare(o1.getName()!=null?o1.getName():"", o2.getName()!=null?o2.getName():"")
					.compare(Mbl3dDataUtils.getEyesKey(o1), Mbl3dDataUtils.getEyesKey(o2))
					.compare(Mbl3dDataUtils.getMouthKey(o1), Mbl3dDataUtils.getMouthKey(o2))
					.compare(o1.getDescription(),o2.getDescription())
					.compare(o1.getId(), o2.getId())
			.result();
			*/
			
		}else if(order==ORDER_BROW){
			//return findEmotionAt(o1) -  findEmotionAt(o2);
			comparables.remove(browGetter);
			comparables.add(0, browGetter);
		}else if(order==ORDER_EYES){
			//return findEmotionAt(o1) -  findEmotionAt(o2);
			comparables.remove(eyesGetter);
			comparables.add(0, eyesGetter);
		}else if(order==ORDER_MOUTH){
			//return findEmotionAt(o1) -  findEmotionAt(o2);
			comparables.remove(mouthGetter);
			comparables.add(0, mouthGetter);
		}
		
		ComparisonChain chain=ComparisonChain.start();
		for(int i=0;i<comparables.size();i++){
			chain=comparables.get(i).compare(chain,o1,o2);
		}
		
		return chain.result();
		
	}
	
	
	public interface ComparaHelper<T>{
		public ComparisonChain compare(ComparisonChain chain,T data1,T data2);
	}
	
	
	private int findEmotionAt(Mbl3dData data){
		for(int i=0;i<emotions.size();i++){
			Emotion emotion=emotions.get(i);
			if(emotion==null){
				continue;
			}
			if(emotion.getSecondary().equals(data.getType())){
				return i;
			}
		}
		return -1;
	}

}
