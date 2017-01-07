package com.akjava.mbl3d.expression.client.timetable;

import java.util.Comparator;
import java.util.List;

import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataComparator.ComparaHelper;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

public class AnimationKeyFrameComparator implements Comparator<Mbl3dAnimationKeyFrame>{

	private List<ComparaHelper<Mbl3dAnimationKeyFrame>> comparables;
	public AnimationKeyFrameComparator(){
		comparables = Lists.newArrayList();
		
		ComparaHelper<Mbl3dAnimationKeyFrame> nameGetter=new ComparaHelper<Mbl3dAnimationKeyFrame>(){
			@Override
			public ComparisonChain compare(ComparisonChain chain, Mbl3dAnimationKeyFrame o1, Mbl3dAnimationKeyFrame o2) {
				return chain.compare(o1.getKeyName()!=null?o1.getKeyName():"", o2.getKeyName()!=null?o2.getKeyName():"");
			}
		};
		comparables.add(nameGetter);
		
		ComparaHelper<Mbl3dAnimationKeyFrame> timeGetter=new ComparaHelper<Mbl3dAnimationKeyFrame>(){
			@Override
			public ComparisonChain compare(ComparisonChain chain, Mbl3dAnimationKeyFrame o1, Mbl3dAnimationKeyFrame o2) {
				return chain.compare(o1.getTime(), o2.getTime());
			}
		};
		comparables.add(timeGetter);
	}
	@Override
	public int compare(Mbl3dAnimationKeyFrame o1, Mbl3dAnimationKeyFrame o2) {
		ComparisonChain chain=ComparisonChain.start();
		for(int i=0;i<comparables.size();i++){
			chain=comparables.get(i).compare(chain,o1,o2);
		}
		
		return chain.result();
	}

}
