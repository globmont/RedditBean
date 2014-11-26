package api;

import org.json.JSONObject;

public class Submission extends Thing {
	JSONObject data;
	public Submission(JSONObject data) {
		super(data, 3);
		this.data = data;
	}
}
