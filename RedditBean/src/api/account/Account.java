package api.account;

import helpers.HTTPHelper;
import helpers.Meta;
import api.User;
import api.driver.Driver;
import exceptions.ForbiddenException;
import exceptions.IncorrectPasswordException;
import exceptions.NotFoundException;
import exceptions.RateLimitException;
import exceptions.UsernameTakenException;

public class Account {
	private User user;
	private String username;
	private boolean hasUpdatedInfo = false;
	private boolean isLoggedIn = false;
	public Account(String username) throws NotFoundException, ForbiddenException {
		this.username = username;
		updateInfo();
	}
	
	public User updateInfo() throws NotFoundException, ForbiddenException {
		user = null;
		
		if(!isLoggedIn) {
			user = Driver.getUser(username);
		} else {
			user = Driver.getAuthenticatedUser(username);
		}
		
		if(user.contains("modhash")) {
			Meta.setModhash(user.get("modhash"));
		}
		
		return user;
		
	}
	
	public User login(String password, boolean remember) throws IncorrectPasswordException, NotFoundException, ForbiddenException {
		isLoggedIn = true;
		hasUpdatedInfo = false;
		Driver.login(username, password, remember);
		user = updateInfo();
		return user;
	}
	
	public User logout() throws NotFoundException, ForbiddenException {
		isLoggedIn = false;
		hasUpdatedInfo = false;
		HTTPHelper.resetClient();
		user = updateInfo();
		return user;
	}
	
	public User getUser() {
		return user;
	}
	
	public void delete() {
		
	}
	
	private void setLoggedIn(boolean b) {
		isLoggedIn = b;
	}
	
	//make sure that password is correct and that username is unused
	public static Account create(String captchaResponse, String email, String iden, String password, boolean remember, String username) throws RateLimitException, UsernameTakenException, ForbiddenException{
		Driver.registerAccount(captchaResponse, email, iden, password, remember, username);
		Account account = null;
		try {
			account = new Account(username);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		account.setLoggedIn(true);
		return account;
		
	}
	
}
