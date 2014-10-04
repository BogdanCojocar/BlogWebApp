package org.home.data;

import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;

import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.Logger;

import org.home.utilities.DatabaseAccessLayerUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class UserDAO {

	private final MongoOperations operations;
	private Random random;

	private static final Logger LOG = Logger.getLogger(UserDAO.class.getName());

	@Autowired
	public UserDAO(MongoOperations operations) {
		Assert.notNull(operations);
		this.operations = operations;
		this.random = new SecureRandom();
		LOG.info("UserDAO created");
	}

	public boolean addUser(String username, String password, String email) {
		String passwordHash = DatabaseAccessLayerUtilities.makePasswordHash(
				password, Integer.toString(random.nextInt()));

		try {
			UserDTO user = new UserDTO(username, passwordHash, email);
			operations.save(user);
			LOG.info("New user added: " + user.toString());
			return true;
		} catch (DuplicateKeyException e) {
			LOG.info("exception: " + e);
			LOG.info("User already in db " + username);
			return false;
		}
	}

	public UserDTO findUserByName(String username) {
		Query query = query(where("username").is(username));
		return operations.findOne(query, UserDTO.class);
	}
	
	public void removeUserByName(String username) {
		Query query = query(where("username").is(username));
		operations.remove(query, UserDTO.class);
	}

	public UserDTO validateLogin(String username, String password) {
		UserDTO user = findUserByName(username);

		if (user == null) {
			LOG.info("User not in database");
			return null;
		}

		String hashedAndSalted = user.getPassword();
		String salt = hashedAndSalted.split(",")[1];

		if (!hashedAndSalted.equals(DatabaseAccessLayerUtilities
				.makePasswordHash(password, salt))) {
			LOG.info("Submitted password is not a match");
			return null;
		}

		return user;
	}
}
