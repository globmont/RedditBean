package api;

import org.json.JSONObject;

public abstract class Thing {
	private JSONObject data = new JSONObject();
	private int type;
	public Thing(JSONObject data, int type) {
		this.data = data;
		this.type = type;
	}
	
	public String get(String key) {
		return	data.get(key).toString();
	}
	
	public boolean contains(String key) {
		return data.has(key);
	}
	
	public int getType() {
		return type;
	}
}
