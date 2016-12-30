package com.akjava.mbl3d.expression.client.datalist;

import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.datalist.SimpleTextData;
import com.akjava.gwt.lib.client.datalist.SimpleTextDatasOwner;
import com.akjava.gwt.lib.client.datalist.SimpleTextDataUtils;
import com.akjava.gwt.lib.client.datalist.SimpleTextDatasCsvConverter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DumpRestoreClearPanel extends VerticalPanel{
	private String dumpFileName="dump.csv";
	
	public String getDumpFileName() {
		return dumpFileName;
	}

	public void setDumpFileName(String dumpFileName) {
		this.dumpFileName = dumpFileName;
	}

	private SimpleTextDatasOwner owner;
	private HorizontalPanel downloadPanel;
	public DumpRestoreClearPanel(final SimpleTextDatasOwner owner){
		this.owner=owner;
		HorizontalPanel dumpPanel=new HorizontalPanel();
		this.add(dumpPanel);
		downloadPanel = new HorizontalPanel();
		downloadPanel.setSpacing(4);
		
		Button exportBt=new Button("Dump csv all",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeDump();
			}
		});
		exportBt.setWidth("120px");
		dumpPanel.add(exportBt);
		dumpPanel.add(downloadPanel);
		
		HorizontalPanel h2=new HorizontalPanel();
		h2.setVerticalAlignment(ALIGN_MIDDLE);
		h2.add(new Label("Restore"));
		add(h2);
		FileUploadForm uploadForm = FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				
				boolean confirm=Window.confirm("Restore datas:clear current datas and replace csv datas.Are  you sure?");
				if(!confirm){
					return;
				}
				
				SimpleTextDataUtils.execRestore(owner.getStorageDataList(),value);
				owner.initializeListData();
			}
		}, true);
		uploadForm.setAccept(FileUploadForm.ACCEPT_CSV);
		
		
		h2.add(uploadForm);
		
		Button clearBt=new Button("Clear all datas",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(owner.getStorageDataList().getDataList().isEmpty()){
					return;
				}
				boolean confirm=Window.confirm("Warning!!Clear All Data.Are you sure?\nThis action can't cancel or undo.\nAuto dump would execute.if you need download and restore it.");
				if(!confirm){
					return;
				}
				
				executeDump();//backup first
				
				SimpleTextDataUtils.execClear(owner.getStorageDataList());
				owner.initializeListData();
				
			}
		});
		
		add(clearBt);
		clearBt.setWidth("120px");
	}
	
	private void executeDump(){
		List<SimpleTextData> datas=owner.getStorageDataList().getDataList();
		if(datas.isEmpty()){
			Window.alert("empty datas.quit");
			return;
		}
		String text=new SimpleTextDatasCsvConverter().convert(datas);
		Anchor anchor=HTML5Download.get().generateTextDownloadLink(text, dumpFileName, "Push to download",true);
		downloadPanel.clear();
		downloadPanel.add(anchor);
	}
}
