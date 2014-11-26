package api.driver;

import helpers.HTTPHelper;
import helpers.Meta;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import api.Comment;
import api.Submission;
import api.Subreddit;
import api.Thing;
import api.User;
import api.captcha.Captcha;
import exceptions.ForbiddenException;
import exceptions.IncorrectPasswordException;
import exceptions.NotFoundException;
import exceptions.RateLimitException;
import exceptions.UsernameTakenException;

public class Driver {
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
	
	public static User getUser(String username) throws NotFoundException, ForbiddenException {
		JSONObject json = http.makeGetRequest("/user/" + username + "/about.json", null, null);
		if(json.has("error") && json.getInt("error") == 404) {
			throw new NotFoundException("404 url not found: http://www.reddit.com/user/" + username + "/about.json");
		}
		
		if(json.has("error") && json.getInt("error") == 403) {
			throw new ForbiddenException("403 forbidden: http://www.reddit.com/user/" + username + "/about.json");
		}
		return new User(json.getJSONObject("data"));
	}
	
	public static User getAuthenticatedUser(String username) throws NotFoundException {
		JSONObject json = http.makeGetRequest("/api/me.json", null, null);
		if(json.has("error") && json.getInt("error") == 404) {
			throw new NotFoundException("404 url not found: http://www.reddit.com/user/" + username + "/about.json");
		}
		return new User(json.getJSONObject("data"));
	}
	
	public static Thing[] getUserContentWhere(User user, String where, String show, String sort, String t, String after, String before, int count, int limit) throws NotFoundException, ForbiddenException {
		limit = (limit > 100) ? 100 : limit;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("show", show);
		params.put("sort", sort);
		params.put("t", t);
		if(after != null) {
			params.put("after", after);
		}
		if(before != null) {
			params.put("before", before);
		}
		params.put("username", user.get("name"));
		params.put("count", "" + count);
		params.put("limit", "" + limit);
		JSONObject json = http.makeGetRequest("/user/" + user.get("name") + "/" + where + ".json", params, null);
		if(json.has("error") && json.getInt("error") == 404) {
			throw new NotFoundException("404 url not found: http://www.reddit.com/user/" + user.get("name") + "/" + where + ".json");
		}
		
		if(json.has("error") && json.getInt("error") == 403) {
			throw new ForbiddenException("403 forbidden: http://www.reddit.com/user/" + user.get("name") + "/" + where + ".json");
		}
		json = json.getJSONObject("data");
		Meta.setModhash(json.getString("modhash"));
		JSONArray thingArr = json.getJSONArray("children");
		Thing[] things = new Thing[thingArr.length()];
		for(int i = 0; i < thingArr.length(); i++) {
			JSONObject thing = thingArr.getJSONObject(i);
			JSONObject thingData = thing.getJSONObject("data");
			if(thing.getString("kind").equals("t3")) {
				//submission
				Submission s = new Submission(thingData);
				things[i] = s;
			} else if(thing.getString("kind").equals("t1")) {
				//comment
				Comment c = new Comment(thingData, thingData.getString("parent_id").equals(thingData.getString("link_id")));
				things[i] = c;
				
			}
		}
		
		return things;
		
	}
	
	public static void login(String username, String password, boolean remember) throws IncorrectPasswordException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user", username);
		params.put("passwd", password);
		params.put("rem", "" + remember);
		params.put("api_type", "json");
		
		JSONObject json = http.makePostRequest("/api/login", params, null).getJSONObject("json");
		if(json.getJSONArray("errors").length() == 0) {
			json = json.getJSONObject("data");
			Meta.setModhash(json.getString("modhash"));
		} else {
			JSONArray errors = json.getJSONArray("errors");
			for(int i = 0; i < errors.length(); i++) {
				String error = errors.getJSONArray(i).getString(0);
				if(error.equals("WRONG_PASSWORD")) {
					throw new IncorrectPasswordException(errors.getJSONArray(i).getString(1));
				}
			}
		}		
		
	}
	
	public static Captcha getCaptcha() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("api_type", "json");
		String iden = http.makePostRequest("/api/new_captcha", params, null).getJSONObject("json").getJSONObject("data").getString("iden");
		BufferedImage image = http.getImage("/captcha/" + iden + ".png", null, null);
		Captcha captcha = new Captcha(iden, image);
		return captcha;
	}
	
	public static void registerAccount(String captchaResponse, String email, String iden, String password, boolean remember, String username) throws RateLimitException, UsernameTakenException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("captcha", captchaResponse);
		if(email != null) {
			params.put("email", email);
		}
		params.put("iden", iden);
		params.put("passwd", password);
		params.put("passwd2", password);
		params.put("rem", "" + remember);
		params.put("user", username);
		params.put("rem", "" + remember);
		params.put("api_type", "json");
		HTTPHelper.resetClient();
		JSONObject json = http.makePostRequest("/api/register", params, null).getJSONObject("json");
		if(json.getJSONArray("errors").length() == 0) {
			json = json.getJSONObject("data");
			Meta.setModhash(json.getString("modhash"));
		} else {
			JSONArray errors = json.getJSONArray("errors");
			for(int i = 0; i < errors.length(); i++) {
				String error = errors.getJSONArray(i).getString(0);
				if(error.equals("USERNAME_TAKEN")) {
					throw new UsernameTakenException(errors.getJSONArray(i).getString(1));
				}
				
				if(error.equals("RATELIMIT")) {
					throw new RateLimitException(errors.getJSONArray(i).getString(1));
				}
			}
		}		
	}
	
	public static void deleteAccount(String deleteMessage, String username, String password) {
		
	}
}
