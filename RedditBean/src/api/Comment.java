package api;

import java.util.ArrayList;

import org.json.JSONObject;

public class Comment implements Thing {
	private JSONObject data;
	private boolean isTopLevel;
	private ArrayList<Comment> children = new ArrayList<Comment>();
	private int numDirectChildren = 0;
	
	public Comment(JSONObject data, boolean isTopLevel) {
		this.data = data;
		this.isTopLevel = isTopLevel;
	}
	
	public void addChild(Comment comment) {
		children.add(comment);
		numDirectChildren++;
	}
	
	public void removeChild(Comment comment) {
		children.remove(comment);
		numDirectChildren--;
	}
	
	public ArrayList<Comment> getChildren() {
		return children;
	}
	
	public boolean isTopLevel() {
		return isTopLevel;
	}
	
	public int getNumDirectChildren() {
		return numDirectChildren;
	}
	
	public int getNumAbsoluteChildren() {
		int total = numDirectChildren;
		for(Comment c : children) {
			total += c.getNumAbsoluteChildren();
		}
		
		return total;
	}
		
	public String get(String value) {
		return data.getString(value);
	}
}
