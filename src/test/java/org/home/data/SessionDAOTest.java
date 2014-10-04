package org.home.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringMongoConfig.class)
public class SessionDAOTest {

	private static final String USERNAME = "username";

	@Autowired
	private SessionDAO sessionDAO;

	@Test
	public void testSessionDAOLifeCycle() {
		String sessionId = sessionDAO.startSession(USERNAME);
		SessionDTO expectedSessionDTO = new SessionDTO(sessionId, USERNAME);
		SessionDTO sessionDTO = sessionDAO.findSessionById(sessionId);

		assertThat(expectedSessionDTO, is(equalTo(sessionDTO)));

		String user = sessionDAO.findUserNameBySessionId(sessionId);
		assertThat(USERNAME, is(equalTo(user)));

		sessionDAO.endSession(sessionId);
		user = sessionDAO.findUserNameBySessionId(sessionId);
		assertThat(user, is(equalTo("")));
	}
}
