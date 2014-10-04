package org.home.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringMongoConfig.class)
public class UserDAOTest {

	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String EMAIL = "email";

	@Autowired
	UserDAO userDAO;

	@Test
	public void testUserInsertionAndDeletion() {
		UserDTO expectedUser = new UserDTO(USERNAME, PASSWORD, EMAIL);
		userDAO.addUser(USERNAME, PASSWORD, EMAIL);
		UserDTO user = userDAO.findUserByName(USERNAME);
		
		assertThat(expectedUser.getUsername(), is(equalTo(user.getUsername())));
		assertThat(expectedUser.getEmail(), is(equalTo(user.getEmail())));
		
		userDAO.removeUserByName(USERNAME);
		user = userDAO.findUserByName(USERNAME);
		
		assertTrue(user == null);
	}
	
	@Test
	public void testDuplicateKeyException() {
		userDAO.removeUserByName(USERNAME);
		userDAO.addUser(USERNAME, PASSWORD, EMAIL);
		assertThat(userDAO.addUser(USERNAME, PASSWORD, EMAIL), is(false));
	}
	
	@Test
	public void testLoginValidation() {
		userDAO.addUser(USERNAME, PASSWORD, EMAIL);
		UserDTO user = userDAO.validateLogin(USERNAME, PASSWORD);
		
		assertFalse(user == null);
		assertThat(user.getUsername(), is(equalTo(USERNAME)));
		System.out.println(user);
	}

}
