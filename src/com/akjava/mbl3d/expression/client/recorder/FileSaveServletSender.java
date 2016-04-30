package com.akjava.mbl3d.expression.client.recorder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

public class FileSaveServletSender {
	private String servletAddress;
	public FileSaveServletSender(String servletAddress) {
		super();
		this.servletAddress = servletAddress;
	}
	
	public  void callClearImages(final PostListener listener){
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, servletAddress);
		builder.setHeader("Content-type", "application/x-www-form-urlencoded");
		StringBuilder sb = new StringBuilder();
		sb.append("command").append("=").append("clear");

		try{
			Request response  =builder.sendRequest(sb.toString(), new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(listener!=null)
					listener.onReceived(response.getText());
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					if(listener!=null)
					listener.onError(exception.getMessage());
				}
			});
		}catch(Exception e){}
	}

	public  void post(final String fileName,String data,final PostListener listener){
		checkNotNull(listener,"FileSaveServletSender:need lister");
		if(fileName==null || fileName.isEmpty()){
			listener.onError("FileSaveServletSender:fileName is null or empty");
			return;
		}
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, servletAddress);
		builder.setHeader("Content-type", "application/x-www-form-urlencoded");
		StringBuilder sb = new StringBuilder();
		sb.append("name").append("=").append(URL.encodeQueryString(fileName));
		
		sb.append("&");
		
		sb.append("data").append("=").append(URL.encodeQueryString(data));
		
		
		try{
			Request response  =builder.sendRequest(sb.toString(), new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(listener!=null)
					listener.onReceived(response.getText());
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					if(listener!=null)
					listener.onError(exception.getMessage());
				}
			});
		}catch(Exception e){}
	}
	
	public static interface PostListener{
		public void onError(String message);
		public void onReceived(String response);
	}
}
