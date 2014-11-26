package helpers;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class HTTPHelper {
	private static long lastExecuted = System.currentTimeMillis();
	final String HOST = "www.reddit.com";
	final String SCHEME = "http";
	private static HttpClient client = HttpClientBuilder.create().build();
	private static HttpPost postRequest = new HttpPost();
	private static HttpGet getRequest = new HttpGet();
	private URIBuilder builder = new URIBuilder();
	private HashMap<String, String> defaultHeaders = new HashMap<String, String>();
	public HTTPHelper() {
		builder.setScheme(SCHEME);
		builder.setHost(HOST);
		defaultHeaders.put("User-Agent", Meta.getUserAgent());
	}
	
	public static HttpClient getHttpClient() {
		return client;
	}
	
	public static void resetClient() {
		client = HttpClientBuilder.create().build();
		postRequest = new HttpPost();
		getRequest = new HttpGet();
	}
	
	public JSONObject makeGetRequest(String path, HashMap<String, String> parameters, HashMap<String, String> headers) {
		builder.clearParameters();
		getRequest.reset();
		
		builder.setPath(path);
		if(parameters != null) {
			for(String key : parameters.keySet()) {
				builder.setParameter(key, parameters.get(key));
			}
		}
		if(headers == null) {
			headers = new HashMap<String, String>();
		}
		
		headers.putAll(defaultHeaders);

		for(String key : headers.keySet()) {
			getRequest.addHeader(key, headers.get(key));
		}
				
		URI uri;
		try {
			uri = builder.build();
			getRequest.setURI(uri);
			System.out.println(uri.toString());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		String resultText = "";
		
		try {
			long diff = System.currentTimeMillis() - lastExecuted;
			if(diff < 2000) {
				try {
				    Thread.sleep(2000 - diff);                 //1000 milliseconds is one second.
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
			HttpResponse response = client.execute(getRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while((line = reader.readLine()) != null) {
				result.append(line);
			}
			resultText = result.toString();
			reader.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(resultText);
		if(resultText != null && resultText.length() > 0 && resultText.charAt(0) == '[') {
			resultText = "{ \"array\":" + resultText + "}";
		}
		JSONObject resultJson = new JSONObject(resultText);
		
		
		return resultJson;
		
	}
	
	public BufferedImage getImage(String path, HashMap<String, String> parameters, HashMap<String, String> headers) {
		builder.clearParameters();
		getRequest.reset();
		
		builder.setPath(path);
		if(parameters != null) {
			for(String key : parameters.keySet()) {
				builder.setParameter(key, parameters.get(key));
			}
		}
		if(headers == null) {
			headers = new HashMap<String, String>();
		}
		
		headers.putAll(defaultHeaders);

		for(String key : headers.keySet()) {
			getRequest.addHeader(key, headers.get(key));
		}
				
		URI uri;
		URL url = null;
		try {
			uri = builder.build();
			System.out.println(uri.toString());
			url = uri.toURL();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String resultText = "";
		
		BufferedImage image = null;
		try {
			long diff = System.currentTimeMillis() - lastExecuted;
			if(diff < 2000) {
				try {
				    Thread.sleep(2000 - diff);                 //1000 milliseconds is one second.
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", Meta.getUserAgent());
			image = ImageIO.read(connection.getInputStream());
			connection.disconnect();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return image;
	}
	
	public JSONObject makePostRequest(String path, HashMap<String, String> parameters, HashMap<String, String> headers) {
		builder.clearParameters();
		postRequest.reset();
		
		builder.setPath(path);
		
		if(parameters != null) {
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			for(String key : parameters.keySet()) {
				urlParameters.add(new BasicNameValuePair(key, parameters.get(key)));
			}
			
			try {
				postRequest.setEntity(new UrlEncodedFormEntity(urlParameters));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		
		
		if(headers == null) {
			headers = new HashMap<String, String>();
		}
		
		headers.putAll(defaultHeaders);

		for(String key : headers.keySet()) {
			postRequest.addHeader(key, headers.get(key));
		}

		URI uri;
		try {
			uri = builder.build();
			postRequest.setURI(uri);
			System.out.println(uri.toString());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		String resultText = "";
		
		try {
			long diff = System.currentTimeMillis() - lastExecuted;
			if(diff < 2000) {
				try {
				    Thread.sleep(2000 - diff);                 //1000 milliseconds is one second.
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
			HttpResponse response = client.execute(postRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while((line = reader.readLine()) != null) {
				result.append(line);
			}
			resultText = result.toString();
			reader.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(resultText);
		if(resultText != null && resultText.length() > 0 && resultText.charAt(0) == '[') {
			resultText = "{ \"array\":" + resultText + "}";
		}
		JSONObject resultJson = new JSONObject(resultText);
		
		
		return resultJson;
		
	}
}
