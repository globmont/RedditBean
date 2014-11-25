package api;

import org.json.JSONObject;

public class Submission implements Thing {
	private JSONObject data;
	
	public Submission(JSONObject data) {
		this.data = data;
	}
		
	public String get(String value) {
		return data.getString(value);
	}
}
