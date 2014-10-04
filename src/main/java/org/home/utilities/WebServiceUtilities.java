package org.home.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class WebServiceUtilities {

	private final static String USER_RE = "^[a-zA-Z0-9_-]{3,20}$";
	private final static String PASS_RE = "^.{3,20}$";
	private final static String EMAIL_RE = "^[\\S]+@[\\S]+\\.[\\S]+$";

	public static Cookie getSessionCookie(HttpServletRequest request) {
		if (request.getCookies() == null) {
			return null;
		}
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals("session")) {
				return cookie;
			}
		}
		return null;
	}

	public static boolean validateSignup(String username, String password,
			String verify, String email, HashMap<String, Object> errors) {

		errors.put("username_error", "");
		errors.put("password_error", "");
		errors.put("verify_error", "");
		errors.put("email_error", "");

		if (!username.matches(USER_RE)) {
			errors.put("username_error",
					"invalid username. try just letters and numbers");
			return false;
		}

		if (!password.matches(PASS_RE)) {
			errors.put("password_error", "invalid password.");
			return false;
		}

		if (!password.equals(verify)) {
			errors.put("verify_error", "password must match");
			return false;
		}

		if (!email.equals("")) {
			if (!email.matches(EMAIL_RE)) {
				errors.put("email_error", "Invalid Email Address");
				return false;
			}
		}

		return true;
	}

	public static ArrayList<String> extractTags(String tags) {

		tags = tags.replaceAll("\\s", "");
		String tagArray[] = tags.split(",");

		ArrayList<String> cleaned = new ArrayList<String>();
		for (String tag : tagArray) {
			if (!tag.equals("") && !cleaned.contains(tag)) {
				cleaned.add(tag);
			}
		}
		return cleaned;
	}
}
