package application;

import databasePart1.MessagesDAO;
import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessagesTesting {
	private MessagesDAO dao;
	
	@Before
	public void connectDb() throws SQLException {
		dao = new MessagesDAO();
	}
	
	@After
	public void closeDb() {
		if (dao != null) {
			dao.closeConnection();
		}
	}
	
	@Test
	public void testCreateMessage() throws SQLException {
		Message test = new Message("Create Message Test.", "This is our test message.", "sender", "receiver");
		
		int testId = dao.createMessage(test);
		Message checkAgainst = dao.getMessageById(testId);
		
		assertEquals("Create Message Test.", checkAgainst.getTitle());
		assertEquals("This is our test message.", checkAgainst.getContent());
		assertEquals("sender", checkAgainst.getAuthorUserName());
		assertEquals("receiver", checkAgainst.getReceiverUserName());
	}
	
	@Test
	public void testUserMessage() throws SQLException {
		Message test1 = new Message("Test Users.", "Test message 1 to user.", "sender", "test");
		Message test2 = new Message("Test Users.", "Test message 2 to user.", "sender", "test");
		
		dao.createMessage(test1);
		dao.createMessage(test2);
		Messages userTest = dao.getUserMessages("test");
		
		assertEquals(2, userTest.size());
	}
	
	@Test
	public void testReply() throws SQLException {
		Message send = new Message("Test Reply.", "Test message to user.", "sender1", "receiver1");
		Message reply = new Message("Test Reply.", "Test message to quick reply back.", "receiver1", "sender1");
		
		dao.createMessage(send);
		int replyMessage = dao.createMessage(reply);
		Message checkReply = dao.getMessageById(replyMessage);
		
		assertEquals("sender1", checkReply.getReceiverUserName());
	}
	
	@Test
	public void testInvalidId() throws SQLException {
		Message testInvalid = dao.getMessageById(82);
		
		assertNull("Invalid id should return nothing.", testInvalid);
	}
	
	@Test
	public void testNoMessages() throws SQLException {
		Messages zeroMessageUser = dao.getUserMessages("zeroUser");
		
		assertEquals(0, zeroMessageUser.size());
	}
}
