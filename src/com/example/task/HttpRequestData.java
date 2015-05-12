package com.example.task;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpRequestData {
	
	// 通过请求URL获取数据,返回-1的话代表连接失败
	public static String getJsonContent(String query_url){
		// 设置time
		HttpParams httpParams = new BasicHttpParams();
		// 连接超时
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
		// socket超时
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		
		HttpClient httpclient = null;
		HttpGet httpget = null;
		try{
			//httpclient对象
			httpclient = new DefaultHttpClient(httpParams);
			//httpget对象，构造
			httpget = new HttpGet(query_url);
			//请求httpclient获得httpresponse
			HttpResponse httpresponse = httpclient.execute(httpget);
			if(httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity httpEntity = httpresponse.getEntity();
				//获得返回的字符串
				String json_data = EntityUtils.toString(httpEntity, "utf-8");
				Log.v("HttpRequestData", "成功解析并返回");
				return json_data;
			}
			else{
				//中断连接
				httpget.abort();
			}
		}
		catch(Exception e){
			e.printStackTrace();	
		}	
		httpclient.getConnectionManager().shutdown();
		return "-1";	
	}
}
