package com.akjava.mbl3d.expression.client.datalist;

import java.io.IOException;
import java.util.List;

import com.akjava.mbl3d.expression.client.datalist.Mbl3dDataComparatorValueBox.Mbl3dDataComparatorValue;
import com.google.common.collect.Lists;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueListBox;

public class Mbl3dDataComparatorValueBox extends ValueListBox<Mbl3dDataComparatorValue>{


	public Mbl3dDataComparatorValueBox() {
		super(
				new Renderer<Mbl3dDataComparatorValue>(){

					@Override
					public String render(Mbl3dDataComparatorValue object) {
						return object.getLabel();
					}

					@Override
					public void render(Mbl3dDataComparatorValue object, Appendable appendable) throws IOException {
						// TODO Auto-generated method stub
						
					}
			
		});
		List<Mbl3dDataComparatorValue> values=Lists.newArrayList(
				new Mbl3dDataComparatorValue(0,"ID"),
				new Mbl3dDataComparatorValue(1,"ID Desc"),
				new Mbl3dDataComparatorValue(2,"Name"),
				new Mbl3dDataComparatorValue(3,"Name Desc"),
				new Mbl3dDataComparatorValue(4,"Type"),
				new Mbl3dDataComparatorValue(5,"Brow"),
				new Mbl3dDataComparatorValue(6,"Eyes"),
				new Mbl3dDataComparatorValue(7,"Mouth")
				);
		//TODO load value from last
		setValue(values.get(1));//this type is popular
		setAcceptableValues(values);
	}

	public static class Mbl3dDataComparatorValue{
		private int mode;
		public Mbl3dDataComparatorValue(int mode, String label) {
			super();
			this.mode = mode;
			this.label = label;
		}
		public int getMode() {
			return mode;
		}
		public void setMode(int mode) {
			this.mode = mode;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		private String label;
	}
}
