package api;

import org.json.JSONObject;

public class Subreddit {
	private JSONObject data;
	
	public Subreddit(JSONObject data) {
		this.data = data;
	}

	public String get(String value) {
		return data.getString(value);
	}
}
