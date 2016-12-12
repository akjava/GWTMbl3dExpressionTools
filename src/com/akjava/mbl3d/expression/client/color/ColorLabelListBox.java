package com.akjava.mbl3d.expression.client.color;

import java.io.IOException;

import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueListBox;

public class ColorLabelListBox extends ValueListBox<ColorLabelData>{

	public ColorLabelListBox() {
		super(new Renderer<ColorLabelData>(){

			@Override
			public String render(ColorLabelData object) {
				if(object==null){
					return "";
				}
				return object.getLabel();
			}

			@Override
			public void render(ColorLabelData object, Appendable appendable) throws IOException {
				
			}
			
		});
	}

}
