package api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Comment implements Thing {
	private JSONObject data;
	private boolean isTopLevel;
	private ArrayList<Comment> children = new ArrayList<Comment>();
	private int numDirectChildren = 0;
	private boolean childrenHaveBeenGenerated = false;
	
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
		//System.out.println(data.toString());
		if(data.get("replies") instanceof JSONObject) {
		JSONObject childrenObj = data.getJSONObject("replies");
			if(!childrenHaveBeenGenerated) {
				if(childrenObj != null) {
					JSONArray childrenArr = childrenObj.getJSONObject("data").getJSONArray("children");
					for(int i = 0; i < childrenArr.length(); i++) {
						children.add(new Comment(childrenArr.getJSONObject(i).getJSONObject("data"), false));
					}
					childrenHaveBeenGenerated = true;
					return children;
				}
			}
		}
			
		return new ArrayList<Comment>();
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
	
	public static void printComment(Comment comment, String prefix) {
		System.out.println(prefix + comment.get("body"));
		for(Comment child : comment.getChildren()) {
			printComment(child, prefix + "\t");
		}
	}
}
