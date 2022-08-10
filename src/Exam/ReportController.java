package Exam;

import Client.ClientUI;
import Client.MyClient;
import Data.Report;

/**
 * genereates up to date report for exam
 * 
 * @author Ayala Cohen
 *
 */
public class ReportController {
	Report report;

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
		report.setAverage(Integer.parseInt(MyClient.reportInfo.get(0)));
		report.setMedian(Integer.parseInt(MyClient.reportInfo.get(1)));
		int dist[] = new int[10];
		
		dist[0] =  Integer.parseInt(MyClient.reportInfo.get(2));
		dist[1] = Integer.parseInt(MyClient.reportInfo.get(3));
		dist[2] = Integer.parseInt(MyClient.reportInfo.get(4));
		dist[3] = Integer.parseInt(MyClient.reportInfo.get(5));
		dist[4] = Integer.parseInt(MyClient.reportInfo.get(6));
		dist[5] = Integer.parseInt(MyClient.reportInfo.get(7));
		dist[6] = Integer.parseInt(MyClient.reportInfo.get(8));
		dist[7] = Integer.parseInt(MyClient.reportInfo.get(9));
		dist[8] = Integer.parseInt(MyClient.reportInfo.get(10));
		dist[9] = Integer.parseInt(MyClient.reportInfo.get(11));
		return report;
		
	}

}
