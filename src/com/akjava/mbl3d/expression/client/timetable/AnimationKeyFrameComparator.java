package com.akjava.mbl3d.expression.client.timetable;

import java.util.Comparator;
import java.util.List;

import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataComparator.ComparaHelper;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

public class AnimationKeyFrameComparator implements Comparator<AnimationKeyFrame>{

	private List<ComparaHelper<AnimationKeyFrame>> comparables;
	public AnimationKeyFrameComparator(){
		comparables = Lists.newArrayList();
		
		ComparaHelper<AnimationKeyFrame> nameGetter=new ComparaHelper<AnimationKeyFrame>(){
			@Override
			public ComparisonChain compare(ComparisonChain chain, AnimationKeyFrame o1, AnimationKeyFrame o2) {
				return chain.compare(o1.getKeyName()!=null?o1.getKeyName():"", o2.getKeyName()!=null?o2.getKeyName():"");
			}
		};
		comparables.add(nameGetter);
		
		ComparaHelper<AnimationKeyFrame> timeGetter=new ComparaHelper<AnimationKeyFrame>(){
			@Override
			public ComparisonChain compare(ComparisonChain chain, AnimationKeyFrame o1, AnimationKeyFrame o2) {
				return chain.compare(o1.getTime(), o2.getTime());
			}
		};
		comparables.add(timeGetter);
	}
	@Override
	public int compare(AnimationKeyFrame o1, AnimationKeyFrame o2) {
		ComparisonChain chain=ComparisonChain.start();
		for(int i=0;i<comparables.size();i++){
			chain=comparables.get(i).compare(chain,o1,o2);
		}
		
		return chain.result();
	}

}
