package com.akjava.mbl3d.expression.client.datalist;

import com.akjava.gwt.lib.client.datalist.SimpleTextData;
import com.google.common.base.Converter;
import com.google.common.base.Strings;

public  class Mbl3dDataSimpleTextConverter extends Converter<SimpleTextData,Mbl3dData>{
		Mbl3dDataConverter converter=new Mbl3dDataConverter();
		@Override
		protected Mbl3dData doForward(SimpleTextData data) {
			Mbl3dData mData=converter.convert(data.getData());
			mData.setId(data.getId());
			mData.setName(Strings.isNullOrEmpty(data.getName())?null:data.getName());
			mData.setCdate(data.getCdate());
			return mData;
		}

		@Override
		protected SimpleTextData doBackward(Mbl3dData data) {
			SimpleTextData sData=new SimpleTextData("",converter.reverse().convert(data));
			sData.setId(data.getId());
			sData.setName(data.getName()==null?"":data.getName());
			sData.setCdate(data.getCdate());
			return sData;
		}
		
	}