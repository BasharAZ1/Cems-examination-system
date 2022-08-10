package Server;

import static org.junit.Assert.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import Data.DoneExam;
import Data.Report;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MyServerTest {
	@Spy
	MyServer myServer; // our class under test
	DoneExam exam = new DoneExam();

	@Rule
	public MockitoRule mockitorule = MockitoJUnit.rule(); // Establish an external resource like a socket or a database
															// connection before a test method is invoked.

	@BeforeEach
	public void setUp() throws Exception { // initialize test
		myServer = spy(new MyServer(5555)); // create instance of our class under test

		// prepare an exam to submit
		exam.setExamID("010100");
		exam.setActualDuration(10);
		exam.setDuration(90);
		exam.setStudentID("111");
		exam.setFinishedSuccessful(1);
		exam.setTime(LocalDateTime.now());
		exam.setSysGrade(80);
		exam.setDBanswer("2,1,1");

	}

	/**
	 * Test createDoneExamReport method of SUT class for first exam submit with
	 * grade 80
	 * 
	 * Expected result: decile 80-90 is incremented by 1.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateDoneExamReportFirstToSubmitExam() throws Exception {
		Report expected = new Report();
		int[] expectedDist = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 };
		int[] zeros = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		expected.setAverage(0);
		expected.setMedian(0);
		expected.setDistribution(expectedDist);
		doReturn(zeros).when(myServer).getExamDistribution("010100");
		doReturn(0).when(myServer).calculateExamAverage(any());
		doReturn(0).when(myServer).calculateExamMedian(any());
		doReturn(true).when(myServer).updateDoneExamInDB(anyObject(), anyObject());
		Report actual = myServer.createDoneExamReport(exam);

		assertTrue(expected.equals(actual));
	}

	/**
	 * Test createDoneExamReport method of SUT class with invalid exam
	 * 
	 * Expected result: method should return null.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateDoneExamReportInvalidExam() throws Exception {

		Report actual = myServer.createDoneExamReport(null);

		assertEquals(null, actual);
	}

	/**
	 * Test createDoneExamReport method of SUT class with more than 1 exam in DB
	 * that was submitted
	 * 
	 * Expected result: decile 80-90 is incremented by 1.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateDoneExamReportMoreThan1Exam() throws Exception {
		Report expected = new Report();
		int[] expectedDist = { 0, 0, 0, 0, 0, 0, 0, 0, 2, 0 };
		int[] lastDist = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 };
		expected.setAverage(0);
		expected.setMedian(0);
		expected.setDistribution(expectedDist);
		doReturn(lastDist).when(myServer).getExamDistribution("010100");
		doReturn(0).when(myServer).calculateExamAverage(any());
		doReturn(0).when(myServer).calculateExamMedian(any());
		Report actual = myServer.createDoneExamReport(exam);

		assertTrue(expected.equals(actual));
	}

	/**
	 * Test SUT's method getExamDistribution.
	 * 
	 * Expected result: the same distribution that is in database
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetExamDistribution() throws Exception {
		int expected[] = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 };

		doReturn(0).when(myServer).getNumStudentWhoGotGradeInGivenDecile(0, "010100");
		doReturn(0).when(myServer).getNumStudentWhoGotGradeInGivenDecile(1, "010100");
		doReturn(0).when(myServer).getNumStudentWhoGotGradeInGivenDecile(2, "010100");
		doReturn(0).when(myServer).getNumStudentWhoGotGradeInGivenDecile(3, "010100");
		doReturn(0).when(myServer).getNumStudentWhoGotGradeInGivenDecile(4, "010100");
		doReturn(0).when(myServer).getNumStudentWhoGotGradeInGivenDecile(5, "010100");
		doReturn(0).when(myServer).getNumStudentWhoGotGradeInGivenDecile(6, "010100");
		doReturn(0).when(myServer).getNumStudentWhoGotGradeInGivenDecile(7, "010100");
		doReturn(1).when(myServer).getNumStudentWhoGotGradeInGivenDecile(8, "010100");
		doReturn(0).when(myServer).getNumStudentWhoGotGradeInGivenDecile(9, "010100");

		int actual[] = myServer.getExamDistribution("010100");
		Assert.assertArrayEquals(expected, actual);
	}

	/**
	 * Test SUT's method calculateExamMedian with grades 40, 50, 20, 70.
	 * 
	 * Expected result: 45.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalcualteExamMedian() throws Exception {
		ArrayList<Integer> grades = new ArrayList<Integer>();
		grades.add(20);
		grades.add(40);
		grades.add(50);
		grades.add(70);
		int expected = 45;
		int actual = myServer.calculateExamMedian(grades);
		
		assertEquals(expected, actual);
	}

	/**
	 * Test SUT's method calculateExamAverage with grades 40, 60, 90, 70.
	 * 
	 * Expected result: 65.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateExamAverage() throws Exception {
		ArrayList<Integer> grades = new ArrayList<Integer>();
		grades.add(40);
		grades.add(60);
		grades.add(90);
		grades.add(70);
		
		int expected = 65;
		int actual = myServer.calculateExamAverage(grades);
		
		assertEquals(expected, actual);
	}

}
