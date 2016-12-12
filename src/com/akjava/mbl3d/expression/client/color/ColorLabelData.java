package com.akjava.mbl3d.expression.client.color;
public  class ColorLabelData{
	public String toString(){
		return (label!=null?label:"")+","+ (color!=null?color:"");
	}
	public ColorLabelData(){}
	private String label;
	public ColorLabelData(String label, String color) {
		super();
		this.label = label;
		this.color = color;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	private String color;
}