package Login;

import static org.junit.Assert.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import static org.mockito.Mockito.*;
/**
 * Test class for Login functionallity in Client Boundary class via property
 * injection
 * 
 * @author Ayala Cohen
 *
 */
public class MyClientBoundaryTest {

	@Spy
	MyClientBoundary myClientBoundary; // our class under test

	@Rule
	public MockitoRule mockitorule = MockitoJUnit.rule(); // Establish an external resource like a socket or a database
															// connection before a test method is invoked.

	@BeforeEach
	public void setUp() throws Exception { // initialize test
		myClientBoundary = spy(new MyClientBoundary()); // create instance of our class under test

	}

	/**
	 * Test tryToLogin method with invalid username and password.
	 * 
	 * Expected result: method should show alert window with "Invalid username or
	 * password." message and return null.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTryToLogInInvalidLogin() throws Exception {
		String expected = null;
		
		
		doReturn("invalidLogin").when(myClientBoundary).getUserTypeFromDB("sason", "333");
		doNothing().when(myClientBoundary).alertError("Invalid username or password.");

		String actual = myClientBoundary.tryToLogIn("sason", "333");
		verify(myClientBoundary).alertError("Invalid username or password.");
		
		assertEquals(expected, actual);
	}

	/**
	 * Test tryToLogin method valid username and password but user is already logged
	 * in.
	 * 
	 * Expected result: method should show alert window with "User already logged
	 * in." message and return null.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTryToLogInAlreadyLoggedIn() throws Exception {
		String expected = null;
		doReturn("alreadyLogged").when(myClientBoundary).getUserTypeFromDB("moshe", "1234");
		doNothing().when(myClientBoundary).alertError("User already logged in.");
		String actual = myClientBoundary.tryToLogIn("moshe", "1234");
		verify(myClientBoundary).alertError("User already logged in.");

		assertEquals(expected, actual);
	}
	
	
	/**
	 * Test tryToLogin method valid username and password with user that is a teacher.
	 * 
	 * Expected result: method should return user type "Teacher".
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTryToLogInValidUser() throws Exception {
		String expected = "Teacher";

		doReturn("Teacher").when(myClientBoundary).getUserTypeFromDB("yuvalc", "4321");
		String actual = myClientBoundary.tryToLogIn("yuvalc", "4321");

		assertEquals(expected, actual);
	}


}
