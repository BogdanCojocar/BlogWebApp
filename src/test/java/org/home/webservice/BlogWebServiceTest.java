package org.home.webservice;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = { BlogWebApp.class })
@IntegrationTest
public class BlogWebServiceTest {

	private final static String LOGIN_URL = "http://localhost:8080/login";
	private final static String SIGNUP_URL = "http://localhost:8080/signup";
	private final static String ERROR_URL = "http://localhost:8080/internal_error";
	private final static String USERNAME = "username";
	private final static String PASSWORD = "password";

	@Test
	public void testLogin() {
		RestTemplate blogWebService = new RestTemplate();
		MultiValueMap<String, String> loginParameters = new LinkedMultiValueMap<String, String>();
		loginParameters.add(USERNAME, USERNAME);
		loginParameters.add(PASSWORD, PASSWORD);
	    
		ResponseEntity<String> response = blogWebService.postForEntity(LOGIN_URL,
				loginParameters, String.class);
		
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
	}
	
	@Test
	public void testLoginGet() {
		RestTemplate blogWebService = new RestTemplate();
		ResponseEntity<String> response = blogWebService.getForEntity(LOGIN_URL, String.class);
		
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
	}
	
	@Test
	public void testSignupGet() {
		RestTemplate blogWebService = new RestTemplate();
		ResponseEntity<String> response = blogWebService.getForEntity(SIGNUP_URL, String.class);
		
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
	}
	
	@Test
	public void testInternalError() {
		RestTemplate blogWebService = new RestTemplate();
		ResponseEntity<String> response = blogWebService.getForEntity(ERROR_URL, String.class);
		
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
	}
}
