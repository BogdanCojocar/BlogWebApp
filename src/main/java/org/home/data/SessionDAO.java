package org.home.data;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.security.SecureRandom;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import sun.misc.BASE64Encoder;

@Repository
public class SessionDAO {

	private MongoOperations operations;

	private static final Logger LOG = Logger.getLogger(SessionDAO.class
			.getName());

	@Autowired
	public SessionDAO(MongoOperations operations) {
		Assert.notNull(operations);
		LOG.info("SessionDAO created");
		this.operations = operations;
	}

	public SessionDTO findSessionById(String sessionID) {
		Query query = query(where("sessionID").is(sessionID));
		return operations.findOne(query, SessionDTO.class);
	}

	public String findUserNameBySessionId(String sessionId) {
		SessionDTO session = findSessionById(sessionId);

		String username = "";
		if (session != null) {
			username = session.getUsername();
		}
		return username;
	}

	public String startSession(String username) {
		SecureRandom generator = new SecureRandom();
		byte randomBytes[] = new byte[32];
		generator.nextBytes(randomBytes);

		BASE64Encoder encoder = new BASE64Encoder();
		String sessionID = encoder.encode(randomBytes);
		SessionDTO session = new SessionDTO(sessionID, username);

		try {
			operations.save(session);
			return sessionID;
		} catch (DuplicateKeyException e) {
			LOG.info("exception: " + e);
			LOG.info("Session already in db " + session);
			return "";
		}
	}

	// ends the session by deleting it from the sessions table
	public void endSession(String sessionID) {
		Query query = query(where("sessionID").is(sessionID));
		operations.remove(query, SessionDTO.class);
	}
}
