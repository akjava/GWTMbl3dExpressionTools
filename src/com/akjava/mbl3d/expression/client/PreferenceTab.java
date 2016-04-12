package com.akjava.mbl3d.expression.client;

import com.akjava.gwt.lib.client.datalist.SimpleTextDatasOwner;
import com.akjava.mbl3d.expression.client.datalist.DumpRestoreClearPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PreferenceTab extends VerticalPanel{
	

	public PreferenceTab(final SimpleTextDatasOwner owner){
		
		this.add(new Label("DataList"));
		DumpRestoreClearPanel panel=new DumpRestoreClearPanel(owner);
		panel.setDumpFileName("expressions_list_dump.csv");
		
		this.add(panel);
	}
}
