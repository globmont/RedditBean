package api.listings;

import helpers.HTTPHelper;
import helpers.Meta;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import api.Comment;
import api.Submission;
import api.Subreddit;

public class Listing {
	private static HTTPHelper http = new HTTPHelper();
	
	public static Comment[] getCommentsFromPost(Submission post, String sort, int context, Comment comment, int depth, int limit) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("article", post.get("id"));
		params.put("context", "" + context);
		params.put("sort", sort);
		params.put("depth", "" + depth);
		params.put("limit", "" + limit);
		if(comment != null) {
			params.put("comment", comment.get("id"));
		}
		
		JSONObject json = http.makeGetRequest("/r/" + post.get("subreddit") + "/comments/" + post.get("id") + ".json", params, null).getJSONArray("array").getJSONObject(1);
		int numTopLevel = 0;
		Meta.setModhash(json.getJSONObject("data").getString("modhash"));
		JSONArray comments = (JSONArray)((JSONObject)json.get("data")).get("children");
		HashMap<String, Comment> commentList = new HashMap<String, Comment>();
		for(int i = 0; i < comments.length(); i++) {
			JSONObject data = comments.getJSONObject(i).getJSONObject("data");
			boolean isTopLevel = data.getString("link_id").equals(data.getString("parent_id"));
			if(isTopLevel) {
				numTopLevel++;
			}
			commentList.put(data.getString("name"), new Comment(data, isTopLevel));
		}
		
		for(int i = 0; i < comments.length(); i++) {
			JSONObject data = comments.getJSONObject(i).getJSONObject("data");
			if(commentList.get(data.getString("parent_id")) != null) {
				commentList.get(data.getString("parent_id")).addChild(commentList.get(data.getString("name")));
				
			}
		}
		
		Comment[] commentArray = new Comment[numTopLevel];
		int index = 0;
		
		for(String key : commentList.keySet()) {
			if(commentList.get(key).isTopLevel()) {
				commentArray[index++] = commentList.get(key);
			}
		}
		
		return commentArray;
	}
	
	public static Subreddit getSubreddit(String subredditName) {
		JSONObject json = http.makeGetRequest("/r/" + subredditName + "/about.json", null, null).getJSONObject("data");
		return new Subreddit(json);
	}
	
	public static Submission[] getPostsFromSubreddit(Subreddit subreddit, String sort, String t, String after, String before, int limit, String show, int count) {
		HashMap<String, String> params = new HashMap<String, String>();
		if(after != null) {
			params.put("after", after);
		}
		
		if(before != null) {
			params.put("before", before);
		}
		
		params.put("limit", "" + limit);
		params.put("show", show);
		params.put("count", "" + count);
		params.put("t", t);
		
		JSONObject json = http.makeGetRequest("/r/" + subreddit.get("display_name") + "/" + sort + ".json", params, null);
		//System.out.println("ME: " + json.toString());
		Meta.setModhash(json.getJSONObject("data").getString("modhash"));
		JSONArray submissions = json.getJSONObject("data").getJSONArray("children");
		Submission[] subArr = new Submission[submissions.length()];
		for(int i = 0; i < submissions.length(); i++) {
			JSONObject submission = submissions.getJSONObject(i).getJSONObject("data");
			subArr[i] = new Submission(submission);
		}
		
		return subArr;
	}
	
	public static Submission[] getPostsByFullNames(String[] names) {
		String s = "";
		for(int i = 0; i < names.length; i++) {
			s += names[i];
			if(i != names.length - 1) {
				s += ",";
			}
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("names", s);
		JSONObject json = http.makeGetRequest("/by_id/" + s + "/.json", params, null);
		Meta.setModhash(json.getJSONObject("data").getString("modhash"));
		JSONArray submissions = json.getJSONObject("data").getJSONArray("children");
		Submission[] submissionArr = new Submission[submissions.length()];
		for(int i = 0; i < submissionArr.length; i++) {
			submissionArr[i] = new Submission(submissions.getJSONObject(i).getJSONObject("data"));
		}
		
		return submissionArr;
	}
}
