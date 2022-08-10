package Exam;

import java.util.ArrayList;

import Client.ClientUI;
import Client.MyClient;
import Data.Report;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

/**
 * generates up to date report for exam
 * 
 * @author Ayala Cohen
 *
 */
public class TeacherReportController {
	TeacherReport2Boundary tr2;
	Report report;

	/**
	 * get report from DB in the form of an ArrayList of strings
	 * 
	 * @return report in the form of an ArrayList
	 */
	public ArrayList<String> getReportInfoForExamDB() {
		return MyClient.reportInfo;
	}
	
	/**
	 * sends via client a request for an exam report
	 * 
	 * @param examid
	 * @return true if report exists, false if report doesn't exist
	 */
	public static boolean getExamReportFromServer(String examid) {
		ClientUI.chat.accept("getExamReport_" + examid);
		if (MyClient.reportforexam.equals("No"))
			return false;
		return true;
	}

	/**
	 * prepare report out of the information from DB
	 * 
	 * @return Report object
	 */
	public Report prepareReport() {
		report = new Report();
		ArrayList<String> tmp = getReportInfoForExamDB();
		report.setAverage(Integer.parseInt(tmp.get(0)));
		report.setMedian(Integer.parseInt(tmp.get(1)));
		int dist[] = new int[10];
		
		dist[0] =  Integer.parseInt(tmp.get(2));
		dist[1] = Integer.parseInt(tmp.get(3));
		dist[2] = Integer.parseInt(tmp.get(4));
		dist[3] = Integer.parseInt(tmp.get(5));
		dist[4] = Integer.parseInt(tmp.get(6));
		dist[5] = Integer.parseInt(tmp.get(7));
		dist[6] = Integer.parseInt(tmp.get(8));
		dist[7] = Integer.parseInt(tmp.get(9));
		dist[8] = Integer.parseInt(tmp.get(10));
		dist[9] = Integer.parseInt(tmp.get(11));
		report.setDistribution(dist);
		return report;
		
	}
}