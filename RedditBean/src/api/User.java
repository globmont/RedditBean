package api;

import org.json.JSONObject;

public class User extends Thing{
	JSONObject data;
	public User(JSONObject data) {
		super(data, 2);
		this.data = data;
	}
}
