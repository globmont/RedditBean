package helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

public class HTTPHelper {
	final String HOST = "www.reddit.com";
	final String SCHEME = "http";
	HttpClient client = HttpClientBuilder.create().build();
	HttpPost postRequest = new HttpPost();
	HttpGet getRequest = new HttpGet();
	URIBuilder builder = new URIBuilder();
	HashMap<String, String> defaultHeaders = new HashMap<String, String>();
	public HTTPHelper() {
		builder.setScheme(SCHEME);
		builder.setHost(HOST);
		defaultHeaders.put("User-Agent", Meta.getUserAgent());
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
		
		if(headers != null) {
			for(String key : headers.keySet()) {
				getRequest.addHeader(key, headers.get(key));
			}
		} else {
			headers = new HashMap<String, String>();
		}
		
		headers.putAll(defaultHeaders);
		
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
			HttpResponse response = client.execute(getRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while((line = reader.readLine()) != null) {
				result.append(line);
			}
			resultText = result.toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(resultText);
		if(resultText.charAt(0) == '[') {
			resultText = "{ \"array\":" + resultText + "}";
		}
		JSONObject resultJson = new JSONObject(resultText);
		
		
		return resultJson;
		
	}
}