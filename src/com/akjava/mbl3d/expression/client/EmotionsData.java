package com.akjava.mbl3d.expression.client;

import java.util.List;
import java.util.Set;

import com.akjava.mbl3d.expression.client.datalist.Mbl3dData;
import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataPredicates;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EmotionsData {
private List<String> primaryNames;
public List<String> getPrimaryNames() {
	return primaryNames;
}

public List<String> getSecondaryNames() {
	return secondaryNames;
}

private List<String> secondaryNames;
private List<Emotion> emotions;
	public EmotionsData(List<Emotion> emotions){
		this.emotions=ImmutableList.copyOf(emotions);
		
		Set<String> tmpPrimary=Sets.newHashSet();
		Set<String> tmpSecondary=Sets.newHashSet();
		
		for(Emotion emotion:emotions){
			if(emotion==null){
				continue;
			}
			tmpPrimary.add(emotion.getPrimary());
			tmpSecondary.add(emotion.getSecondary());
		}
		primaryNames=ImmutableList.copyOf(tmpPrimary);
		secondaryNames=ImmutableList.copyOf(tmpSecondary);
	}
	
	public List<Emotion> getEmotions() {
		return emotions;
	}

	public  boolean containsInPrimary(String name){
		return primaryNames.contains(name);
	}
	
	public  boolean containsInSecondary(String name){
		return secondaryNames.contains(name);
	}
	
	//should i optional?
	public String getPrimaryNameBySecondaryName(String secondary){
		for(Emotion emotion:emotions){
			if(emotion.getSecondary().equals(secondary)){
				return emotion.getPrimary();
			}
		}
		return null;
	}
	
	public List<String> getSecondaryNameByPrimaryName(String primary){
		List<String> tmp=Lists.newArrayList();
		for(Emotion emotion:emotions){
			if(emotion.getPrimary().equals(primary)){
				tmp.add(emotion.getSecondary());
			}
		}
		return ImmutableList.copyOf(tmp);
	}
	
	public  Predicate<Mbl3dData> makeNamePredicate(final String name){
		return Mbl3dDataPredicates.emotionTypePredicate(name, this);
	}
}
