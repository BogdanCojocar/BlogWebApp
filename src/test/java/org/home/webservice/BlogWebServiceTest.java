package org.home.webservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = { BlogWebApp.class })
@IntegrationTest
public class BlogWebServiceTest {
	
	private final static String LOGIN_URL = "http://localhost:8080/login";

	@Test
	public void testLogin() {
		RestTemplate blogWebService = new RestTemplate();
		System.out.println(blogWebService.getForEntity(LOGIN_URL, String.class));
		
	}
}
