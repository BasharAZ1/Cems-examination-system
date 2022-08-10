package Server;

import static org.junit.Assert.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.*;

public class MyServerLoginTest {
	@Spy
	MyServer myServer; // our class under test

	@Rule
	public MockitoRule mockitorule = MockitoJUnit.rule(); // Establish an external resource like a socket or a database
															// connection before a test method is invoked.

	@BeforeEach
	public void setUp() throws Exception { // initialize test
		myServer = spy(new MyServer(5555)); // create instance of our class under test

	}

	/**
	 * Test MyServer method LoginValidMessage with valid login message from client.
	 * 
	 * Expected result: method should return true.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoginValidMessageTrue() throws Exception {
		String messageFromClient[] = { "login", "moshe", "1234" };

		assertTrue(myServer.LoginValidMessage(messageFromClient));
	}

	/**
	 * Test MyServer method LoginValidMessage with invalid login message from
	 * client.
	 * 
	 * Expected result: method should return false.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoginValidMessageFalse() throws Exception {
		String messageFromClient[] = { "login", "moshe" }; // invalid message from client

		assertFalse(myServer.LoginValidMessage(messageFromClient));
	}

	/**
	 * Test MyServer method handleLoginRequest with user that is already logged into
	 * the system
	 * 
	 * Expected result: method should return "X-User already logged in".
	 * 
	 * @throws Exception
	 */
	@Test
	public void testhandleLoginRequestAlreadyLoggedIn() throws Exception {
		String messageFromClient[] = { "login", "moshe", "1234" }; // a user that is already logged in
		String expected = "X-User already logged in";
		doReturn(1).when(myServer).getUserStatusInDB("moshe", "1234");

		assertEquals(expected, myServer.handleLoginRequest(messageFromClient));
	}

	/**
	 * Test MyServer method handleLoginRequest with invalid user
	 * 
	 * Expected result: method should return "X-Invalid username or password.".
	 * 
	 * @throws Exception
	 */
	@Test
	public void testhandleLoginRequestInvalidUser() throws Exception {
		String messageFromClient[] = { "login", "john", "1234" }; // a user that is already logged in
		String expected = "X-Invalid username or password.";
		doReturn(null).when(myServer).getUserTypeFromDB("john", "1234");

		assertEquals(expected, myServer.handleLoginRequest(messageFromClient));
	}

	/**
	 * Test MyServer method handleLoginRequest with a valid login request from user
	 * 
	 * Expected result: method should return "W-Moshe Levi".
	 * 
	 * @throws Exception
	 */
	@Test
	public void testhandleLoginRequestValidUser() throws Exception {
		String messageFromClient[] = { "login", "moshe", "1234" }; // a user that is already logged in
		String expected = "W-Moshe Levi";
		doReturn("Student").when(myServer).getUserTypeFromDB("moshe", "1234");
		doReturn("Moshe Levi").when(myServer).getUserFullNameInDB("moshe", "1234");
		doReturn(true).when(myServer).setUserStatusInDB("moshe", "1234", 1);
		assertEquals(expected, myServer.handleLoginRequest(messageFromClient));
		verify(myServer).setUserStatusInDB("moshe", "1234", 1); // check to see if user status was changed
	}

}
