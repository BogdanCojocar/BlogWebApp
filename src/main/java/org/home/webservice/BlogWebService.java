package org.home.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.home.data.BlogPostDAO;
import org.home.data.BlogPostDTO;
import org.home.data.SessionDAO;
import org.home.data.UserDAO;
import org.home.data.UserDTO;
import org.home.utilities.WebServiceUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class BlogWebService {

	private static final int POSTS_NUMBER_LIMIT = 10;
	private static final String TEST_PATH = "/test";
	private static final String SIGNUP_PATH = "/signup";
	private static final String WELCOME_PATH = "/welcome";
	private static final String HOME_PAGE_PATH = "/";
	private static final String INTERNAL_ERROR_PATH = "/internal_error";
	private static final String LOGIN_PATH = "/login";
	private static final String POST_NOT_FOUND_PATH = "/post_not_found";
	private static final String LOGOUT_PATH = "/logout";
	private static final String POST_PATH = "/post/{permalink}";
	private static final String NEWPOST_PATH = "/newpost";
	private static final String NEWCOMMENT_PATH = "/newcomment";
	private static final String TAG_PATH = "/tag/{tag}";

	@Autowired
	private BlogWebServiceFrontend blogFrontend;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private SessionDAO sessionDAO;

	@Autowired
	private BlogPostDAO blogPostDAO;

	private final static Logger LOG = Logger.getLogger(BlogWebService.class
			.getName());

	@RequestMapping(value = HOME_PAGE_PATH, method = RequestMethod.GET)
	public String blogHomePage(HttpServletRequest request) {
		Cookie cookie = WebServiceUtilities.getSessionCookie(request);
		String username = sessionDAO.findUserNameBySessionId(cookie.getValue());

		List<BlogPostDTO> posts = blogPostDAO
				.findPostByDateDescending(POSTS_NUMBER_LIMIT);
		Map<String, Object> templateData = new HashMap<String, Object>();
		templateData.put("myposts", posts);
		if (!username.isEmpty()) {
			templateData.put("username", username);
		}
		return blogFrontend.addTemplate(BlogWebServiceFrontend.BLOG_TEMPLATE,
				templateData);
	}

	@RequestMapping(value = WELCOME_PATH, method = RequestMethod.GET)
	public String welcome(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Cookie cookie = WebServiceUtilities.getSessionCookie(request);
		String username = sessionDAO.findUserNameBySessionId(cookie.getValue());

		if (username.isEmpty()) {
			LOG.info("User not found, redirecting to the signup page");
			response.sendRedirect(SIGNUP_PATH);
			return "";
		} else {
			Map<String, Object> templateData = new HashMap<String, Object>();
			templateData.put("username", username);
			return blogFrontend.addTemplate(
					BlogWebServiceFrontend.WELCOME_TEMPLATE, templateData);
		}
	}

	@RequestMapping(value = LOGIN_PATH, method = RequestMethod.GET)
	public String signup(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> templateData = new HashMap<String, Object>();
		templateData.put("username", "");
		templateData.put("password", "");
		templateData.put("login_error", "");
		return blogFrontend.addTemplate(BlogWebServiceFrontend.LOGIN_TEMPLATE,
				templateData);
	}

	@RequestMapping(value = LOGIN_PATH, method = RequestMethod.POST)
	public String signupUser(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String pageData = "";
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		LOG.info("User " + username + " signed in with password " + password);

		UserDTO user = userDAO.validateLogin(username, password);

		if (user != null) {
			String sessionId = sessionDAO.startSession(username);

			if (sessionId.isEmpty()) {
				LOG.info("Problem while starting the session");
				response.sendRedirect(INTERNAL_ERROR_PATH);
			} else {
				// set the cookie for the user's browser
				response.addCookie(new Cookie("session", sessionId));
				response.sendRedirect(WELCOME_PATH);
			}
		} else {
			Map<String, Object> templateData = new HashMap<String, Object>();
			templateData.put("username", username);
			templateData.put("password", "");
			templateData.put("login_error", "Invalid login");
			pageData = blogFrontend.addTemplate(
					BlogWebServiceFrontend.LOGIN_TEMPLATE, templateData);
		}
		return pageData;
	}

	@RequestMapping(value = SIGNUP_PATH, method = RequestMethod.GET)
	public String login() {
		Map<String, Object> templateData = new HashMap<String, Object>();
		templateData.put("username", "");
		templateData.put("password", "");
		templateData.put("email", "");
		templateData.put("password_error", "");
		templateData.put("username_error", "");
		templateData.put("email_error", "");
		templateData.put("verify_error", "");
		return blogFrontend.addTemplate(BlogWebServiceFrontend.SIGNUP_TEMPLATE,
				templateData);
	}

	@RequestMapping(value = SIGNUP_PATH, method = RequestMethod.POST)
	public String loginUser(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		String verify = request.getParameter("verify");

		HashMap<String, Object> templateData = new HashMap<String, Object>();
		templateData.put("username", username);
		templateData.put("email", email);

		if (WebServiceUtilities.validateSignup(username, password, verify,
				email, templateData)) {
			LOG.info("Signup: Creating user with: " + username + " " + password);
			if (!userDAO.addUser(username, password, email)) {
				templateData.put("username_error",
						"Username already in use, Please choose another.");
				return blogFrontend.addTemplate(
						BlogWebServiceFrontend.SIGNUP_TEMPLATE, templateData);
			} else {
				String sessionID = sessionDAO.startSession(username);
				LOG.info("Session ID is " + sessionID);
				response.addCookie(new Cookie("session", sessionID));
				response.sendRedirect(WELCOME_PATH);
				return "";
			}
		} else {
			LOG.info("Invalid data for new user.");
			return blogFrontend.addTemplate(
					BlogWebServiceFrontend.SIGNUP_TEMPLATE, templateData);
		}
	}

	@RequestMapping(value = POST_PATH, method = RequestMethod.GET)
	public String showPost(@PathVariable String permalink,
			HttpServletResponse response) throws IOException {
		BlogPostDTO post = blogPostDAO.findPostByPermalink(permalink);
		if (post == null) {
			response.sendRedirect(POST_NOT_FOUND_PATH);
			return "";
		} else {
			// empty comment to hold new comment in form at bottom of the blog
			// entry
			Map<String, Object> emptyCommentData = new HashMap<String, Object>();
			emptyCommentData.put("name", "");
			emptyCommentData.put("email", "");
			emptyCommentData.put("body", "");

			Map<String, Object> postTemplateData = new HashMap<String, Object>();
			postTemplateData.put("post", post);
			postTemplateData.put("comment", emptyCommentData);
			return blogFrontend.addTemplate(
					BlogWebServiceFrontend.ENTRY_TEMPLATE, postTemplateData);
		}
	}

	@RequestMapping(value = NEWPOST_PATH, method = RequestMethod.GET)
	public String newPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Cookie cookie = WebServiceUtilities.getSessionCookie(request);
		String sessionId = cookie.getValue();
		String username = sessionDAO.findUserNameBySessionId(sessionId);

		if (username.isEmpty()) {
			LOG.info("User is not logged in.");
			response.sendRedirect(LOGIN_PATH);
			return "";
		} else {
			Map<String, Object> templateData = new HashMap<String, Object>();
			templateData.put("username", username);
			return blogFrontend.addTemplate(
					BlogWebServiceFrontend.NEWPOST_TEMPLATE, templateData);
		}
	}

	@RequestMapping(value = NEWCOMMENT_PATH, method = RequestMethod.POST)
	public String submitNewComment(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String name = request.getParameter("commentName");
		String email = request.getParameter("commentEmail");
		String body = request.getParameter("commentBody");
		String permalink = request.getParameter("permalink");

		BlogPostDTO post = blogPostDAO.findPostByPermalink(permalink);
		if (post == null) {
			response.sendRedirect(POST_NOT_FOUND_PATH);
			return "";
		} else if (name.equals("") || body.equals("")) {
			Map<String, Object> commentData = new HashMap<String, Object>();
			Map<String, Object> postData = new HashMap<String, Object>();

			commentData.put("name", name);
			commentData.put("email", email);
			commentData.put("body", body);
			postData.put("comment", commentData);
			postData.put("post", post);
			postData.put("errors",
					"Post must contain your name and an actual comment");

			return blogFrontend.addTemplate(
					BlogWebServiceFrontend.ENTRY_TEMPLATE, postData);
		} else {
			blogPostDAO.addPostComment(name, body, email, permalink);
			response.sendRedirect("/post/" + permalink);
			return "";
		}
	}

	@RequestMapping(value = TAG_PATH, method = RequestMethod.GET)
	public String showPostForTag(@PathVariable String tag,
			HttpServletRequest request, HttpServletResponse response) {
		System.out.println("here");
		Cookie cookie = WebServiceUtilities.getSessionCookie(request);
		String sessionId = cookie.getValue();
		String username = sessionDAO.findUserNameBySessionId(sessionId);

		Map<String, Object> templateData = new HashMap<String, Object>();
		List<BlogPostDTO> posts = blogPostDAO.findPostByTagWithDateDescending(
				tag, POSTS_NUMBER_LIMIT);

		templateData.put("myposts", posts);
		if (!username.isEmpty()) {
			templateData.put("username", username);
		}
		return blogFrontend.addTemplate(BlogWebServiceFrontend.BLOG_TEMPLATE,
				templateData);
	}

	@RequestMapping(value = NEWPOST_PATH, method = RequestMethod.POST)
	public String submitNewPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String title = request.getParameter("subject");
		String post = request.getParameter("body");
		String tags = request.getParameter("tags");

		Cookie cookie = WebServiceUtilities.getSessionCookie(request);
		String sessionId = cookie.getValue();
		String username = sessionDAO.findUserNameBySessionId(sessionId);

		if (username.isEmpty()) {
			LOG.info("User is not logged in.");
			response.sendRedirect(LOGIN_PATH);
			return "";
		} else if (title.equals("") || post.equals("")) {
			HashMap<String, Object> templateData = new HashMap<String, Object>();
			templateData.put("errors",
					"post must contain a title and blog entry.");
			templateData.put("subject", title);
			templateData.put("username", username);
			templateData.put("tags", tags);
			templateData.put("body", post);
			return blogFrontend.addTemplate(
					BlogWebServiceFrontend.NEWPOST_TEMPLATE, templateData);
		} else {
			ArrayList<String> tagsArray = WebServiceUtilities.extractTags(tags);
			post = post.replaceAll("\\r?\\n", "<p>");
			String permalink = blogPostDAO.addPost(title, post, tagsArray,
					username);
			response.sendRedirect("/post/" + permalink);
			return "";
		}

	}

	@RequestMapping(value = LOGOUT_PATH, method = RequestMethod.GET)
	public void logoutUser(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Cookie cookie = WebServiceUtilities.getSessionCookie(request);
		String sessionId = cookie.getValue();

		if (sessionId != null) {
			sessionDAO.endSession(sessionId);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
		response.sendRedirect(LOGIN_PATH);
	}

	@RequestMapping(value = INTERNAL_ERROR_PATH, method = RequestMethod.GET)
	public String internalError() {
		Map<String, Object> templateData = new HashMap<String, Object>();
		templateData.put("error", "System has encountered an error");
		return blogFrontend.addTemplate(BlogWebServiceFrontend.ERROR_TEMPLATE,
				templateData);
	}

	@RequestMapping(value = POST_NOT_FOUND_PATH, method = RequestMethod.GET)
	public String postNotFound() {
		return blogFrontend.addTemplate(
				BlogWebServiceFrontend.POST_NOT_FOUND_TEMPLATE,
				new HashMap<String, Object>());
	}

	@RequestMapping(TEST_PATH)
	public String testFreemarker() {
		return blogFrontend.testTemplate();
	}

}