package api;

import org.json.JSONObject;

public class Subreddit extends Thing{
	JSONObject data;
	public Subreddit(JSONObject data) {
		super(data, 5);
		this.data = data;
	}

}
