package com.example.task;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.example.zhihupocket.MainActivity;

public class HttpRequestData {
	
	// 通过请求URL获取数据,返回-1的话代表连接失败
	public static String getJsonContent(){
		HttpClient httpclient = null;
		try{
			//httpclient对象
			httpclient = new DefaultHttpClient();
			//httpget对象，构造
			HttpGet httpget = new HttpGet(MainActivity.ZHIHU_API);
			//请求httpclient获得httpresponse
			HttpResponse httpresponse = httpclient.execute(httpget);
			if(httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				
				HttpEntity httpEntity = httpresponse.getEntity();
				//获得返回的字符串
				String json_data = EntityUtils.toString(httpEntity, "utf-8");
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
