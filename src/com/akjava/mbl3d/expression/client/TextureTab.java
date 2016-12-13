package com.akjava.mbl3d.expression.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TextureTab extends VerticalPanel{
	private Mbl3dExpressionEntryPoint mbl3dExpressionEntryPoint;


	private VerticalPanel TextureMontagePanel;
	
	public VerticalPanel getTextureMontagePanel() {
		return TextureMontagePanel;
	}
	
	public TextureTab(Mbl3dExpressionEntryPoint mbl3dExpressionEntryPoint) {
		super();
		this.mbl3dExpressionEntryPoint = mbl3dExpressionEntryPoint;
	
		this.add(new Label("TextureMontage"));
		TextureMontagePanel = new VerticalPanel();
		this.add(TextureMontagePanel);
	}
	
}
