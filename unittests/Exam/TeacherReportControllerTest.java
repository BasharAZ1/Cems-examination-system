package Exam;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import Data.Report;

public class TeacherReportControllerTest {

	@Spy
	TeacherReportController trc; // our class under test

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule(); // Establish an external resource like a socket or a database
															// connection before a test method is invoked.

	@BeforeEach
	public void setUp() throws Exception { // initialize test
		trc = spy(TeacherReportController.class); // create instance of our class under test
	}

	/**
	 * Test prepareReport method of SUT class with given input from server report
	 * which consists out of median = 37, average = 37 and distribution
	 * [0,1,0,0,0,0,1,0,0,0]
	 * 
	 * Expected result: full Report object with median = 37, average = 37 and
	 * distribution [0,1,0,0,0,0,1,0,0,0] .
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPrepareReport() throws Exception {
		ArrayList<String> tmp = new ArrayList<>();

		tmp.add("37");
		tmp.add("37");
		tmp.add("0");
		tmp.add("1");
		tmp.add("0");
		tmp.add("0");
		tmp.add("0");
		tmp.add("0");
		tmp.add("1");
		tmp.add("0");
		tmp.add("0");
		tmp.add("0");

		doReturn(tmp).when(trc).getReportInfoForExamDB(); // Call method to get report for exam from DB
		Report expectedRep = new Report();
		expectedRep.setAverage(37);
		expectedRep.setMedian(37);
		int[] dist = { 0, 1, 0, 0, 0, 0, 1, 0, 0, 0 };
		expectedRep.setDistribution(dist);

		Report actual = trc.prepareReport(); // Call method under test
		assertTrue(expectedRep.equals(actual));
	}

}