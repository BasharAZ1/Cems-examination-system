package Client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import Data.Answer;
import Data.Comment;
import Data.Course;
import Data.DoneExam;
import Data.Exam;
import Data.MyFile;
import Data.Person;
import Data.Question;
import Data.QuestionInExam;
import Data.Student;
import Data.Subject;
import Login.MyClientBoundary;
import ocsf.client.*;

/**
 * client side of client-server architecture
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class MyClient extends AbstractClient {
	public static boolean awaitResponse = false, timeChanged = false;

	public static ArrayList<Exam> exams = new ArrayList<>();
	public static ArrayList<Student> students = new ArrayList<>();
	public static ArrayList<Person> persons = new ArrayList<>();
	public static ArrayList<Exam> exams1 = new ArrayList<>();
	public static ArrayList<String> resoansprin = new ArrayList<>();
	public static ArrayList<String> Students = new ArrayList<>();
	public static ArrayList<String> teacher = new ArrayList<>();
	public static ArrayList<String> Coursesname = new ArrayList<>();
	public static ArrayList<Question> Questions = new ArrayList<>();
	public static ArrayList<String> QuestionsForEdit = new ArrayList<>();
	public static ArrayList<QuestionInExam> QuestionsInex = new ArrayList<>();
	public static ArrayList<Course> courses = new ArrayList<>();
	public static ArrayList<Subject> subjects = new ArrayList<>();
	public static ArrayList<Subject> subjects1 = new ArrayList<>();
	public static ArrayList<String> reportInfo = new ArrayList<>();
	public static ArrayList<DoneExam> Grades = new ArrayList<>();
	public static ArrayList<DoneExam> DoneExams = new ArrayList<>();
	public static ArrayList<DoneExam> DoneExamsForReport = new ArrayList<>();
	public static ArrayList<QuestionInExam> QuestionforExam = new ArrayList<>();
	public static ArrayList<QuestionInExam> QuestionforCopy = new ArrayList<>();
	public static ArrayList<String> examsID = new ArrayList<>();
	public static ArrayList<Exam> examsForEdit = new ArrayList<>();
	public static ArrayList<String> examCourse = new ArrayList<>();
	public static ArrayList<String> examsSubject = new ArrayList<>();
	public static ArrayList<String> Static = new ArrayList<>();
	public static ArrayList<String> Idsforpersons = new ArrayList<>();
	public static ArrayList<String> Idsforpersons1 = new ArrayList<>();
	public static ArrayList<String> execCodesGradeApprove = new ArrayList<>();

	public static Exam exam2;
	public static Question question;

	public static int questionsize, timerChanged, timerStartExam;

	public static String role = "", approveGrade = "", durationChangePending = "", durationChangeSuccess = "",
			getFullOnlineExam = "", durationChangeFail = "", queDeleteUnsucc = "", noQue = "", alreadyLogged = "",
			invalidLogin = "", editExam = "", reportforexam = "", pendingGrade = "", StudentsForDoneExam = "",
			EditOfflineExam = "", getreport = "", isExamOff = "", OngoingStatus = "", offexamstatus = "1",
			excutioncodereq = "OK", stopExamination = "", TeacherName, Subject, Course, ExamID, suspicion,
			StudentComments, TeacherComments, FinalGrade, generatedCode, teacherFullName, principalFullName,
			studentFullName, Lockstatus, status, statusINOUT, StatusOF, canDeleteTable = "";

	public static boolean offlineExamCopy = false;
	
	ChatIF clientUI;
	
	/**
	 * start new client connection
	 * 
	 * @param host     server IP
	 * @param port     server's port number
	 * @param clientUI client chat interface
	 * @throws IOException
	 */
	public MyClient(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port);
		this.clientUI = clientUI;
		openConnection();
	}

	/**
	 * Handles a message sent from the server to this client.
	 *
	 * @param msg the message sent.
	 */
	@Override
	protected void handleMessageFromServer(Object msg) {
		System.out.println("message from server  :" + msg);
		if (msg instanceof MyFile) {
			offlineExamCopy = true;
			MyFile clientFile = (MyFile) msg;

			@SuppressWarnings("unused")
			int fileSize = clientFile.getSize();

			try {
				byte[] mybytearray = clientFile.getMybytearray();
				File newFile = new File("C:\\CEMS_client\\" + clientFile.getFileName());
				FileOutputStream fos = new FileOutputStream(newFile); /* Create file output stream */
				@SuppressWarnings("resource")
				BufferedOutputStream bos = new BufferedOutputStream(fos); /* Create BufferedFileOutputStream */
				bos.write(mybytearray, 0, clientFile.getSize());/* Write byte array to output stream */
			} catch (Exception e) {
				System.out.println("Error uploading File to Client");
				MyClientBoundary.staticAlertError("Error uploading File to Client");
				awaitResponse = false;
				return;
			}

			MyClientBoundary.staticAlertInfo("File successfully downloaded");
			awaitResponse = false;
			return;
		}
		String str;
		char op;
		str = (String) msg;

		if (!(msg instanceof String)) {
			System.out.println("Client : Invalid message from server !!");
			awaitResponse = false;
			return;
		}

		str = (String) msg;
		if (str.equals("logout"))
			statusINOUT = "logout";

		String[] temp = str.split("-");
		op = str.charAt(0);
		if (temp[0].equals("uniqueExecutionCode")) {
			if (temp[1].equals("OK"))
				excutioncodereq = "OK";
			else
				excutioncodereq = "taken";
		}

		editExam = "";
		reportforexam = "";
		pendingGrade = "";
		StudentsForDoneExam = "";
		getreport = "";
		durationChangeSuccess = "";
		status = "";
		StatusOF = "";
		subjects.clear();
		OngoingStatus = "";
		EditOfflineExam = "";

		String[] tempTimer = str.split("_");
		String strTimer = tempTimer[0];
		if (strTimer.equals("ExamTimer")) {
			strTimer = tempTimer[1];
			switch (strTimer) {
			case "NewDuration":
				System.out.println("new duration");
				timerChanged = Integer.parseInt(tempTimer[4]);
				System.out.println("new time = " + timerChanged);
				timeChanged = true;
				break;
			case "StartTimer":
				timerStartExam = Integer.parseInt(tempTimer[4]);
				System.out.println("timerStartExam = " + timerStartExam);
				break;
			case "StopTimer":
				System.out.println("stop timer");
				stopExamination = "lock";
				break;
			case "ExamLocked":
				System.out.println("stop timer exam locked");
				stopExamination = "lock";
				break;
			}
		}

		switch (op) {
		case 'Y': /* server says he updated the database successfully */
		{
			String str2 = temp[1];
			if (str2.equals("approveGrade success")) {
				approveGrade = "accept";
				MyClientBoundary.staticAlertInfo("Grade successfully approved.");
				break;
			} else if (str2.equals("duration change request noted")) {
				durationChangePending = "pending";
				MyClientBoundary.staticAlertInfo("Duration change request noted");
				break;
			} else if (str2.equals("duration changed successfuly")) {
				MyClientBoundary.staticAlertInfo("Duration changed successfuly");
				durationChangeSuccess = "accept";

				break;
			} else if (str2.equals("Exam updated successfully in DB")) {
				MyClientBoundary.staticAlertInfo("Exam updated successfully in DB");
				break;
			} else if (str2.equals("Question updated successfully in DB")) {
				MyClientBoundary.staticAlertInfo("Question updated successfully in DB");
				break;
			} else if (str2.equals("Edit question updated successfully in DB")) {
				MyClientBoundary.staticAlertInfo("Edit question updated successfully in DB");
				break;
			} else if (str2.equals("update successful")) {
				MyClientBoundary.staticAlertInfo("Update successful.");
				break;
			} else if (str2.equals("bank delete success")) {
				MyClientBoundary.staticAlertInfo("Bank delete success.");
				break;
			} else if (str2.equals("deleteQuestionsUnderQuestionBankAndCourse")) {
				MyClientBoundary.staticAlertInfo("Delete success.");
				break;
			} else if (str2.equals("question delete success")) {
				MyClientBoundary.staticAlertInfo("Question delete success.");
				break;
			} else if (str2.equals("changeGrade success")) {
				MyClientBoundary.staticAlertInfo("Change grade success.");
				break;
			} else if (str2.equals("submitExam success")) {
				MyClientBoundary.staticAlertInfo("Submit exam success.");
				break;
			} else if (str2.equals("approvalChangeDuration success")) {
				MyClientBoundary.staticAlertInfo("Approval of change duration success.");
				break;
			} else if (str2.equals("approvalChangeDuration success")) {
				MyClientBoundary.staticAlertInfo("file uploaded successfully to the database.");
				break;
			} else if (str2.equals("file uploaded successfully to the database.")) {
				MyClientBoundary.staticAlertInfo("file uploaded successfully to the database.");
				break;
			} else if (str2.equals("Valid execution code")) {
				StatusOF = "accept";
				break;
			}
		}

		case 'S': /* server says he replayed the database */
		{
			String str2 = temp[1];
			if (str2.equals("questions"))
				Questions = decodeMessageFromServerForQuestions(str);
			else if (str2.equals("getCoursesUnderSubject&Teacher"))
				courses = decodeMessageFromServerForCourses(str);
			else if (str2.equals("subjects")) {
				subjects1 = decodeMessageFromServerForSubject(str);
			} else if (str2.equals("generateUniqueExecutionCode")) {
				generatedCode = temp[2];
			} else if (str2.equals("examBank") || str2.equals("questionBank")) {
				subjects = decodeMessageFromServerForSubject(str);
			} else if (str2.equals("getAllExamsOfTeacher")) {
				exams1 = decodeMessageFromServerExamDur(str);
			} else if (str2.equals("question"))
				question = decodeMessageFromServerforEditQuestion(str);
			else if (str2.equals("getExam")) {
				decodeMessageFromServerforEditexam(str);
			} else if (str2.equals("getPendingGrades")) {
				Grades = decodeMessageFromServerforExamapprove(str);
			} else if (str2.equals("examReport")) {
				reportInfo = decodeMessageFromServerforExamReport(str);
			} else if (str2.equals("getStudentForDoneExam")) {
				Grades = decodeMessageFromServerforExamapprove(str);
			} else if (str2.equals("getFullOnlineExam")) {
				getFullOnlineExam = "accept";
				QuestionforExam = decodeMessageFromServerforExamaintion(str);
			} else if (str2.equals("getExamsUnderTeacherExamBankCourse")) {
				examsForEdit = decodeMessageFromServerForExamsEdit(str);
			} else if (str2.equals("GetDoneExamForStudent")) {
				DoneExams = decodeMessageFromServerForViewG(str);
			} else if (str2.equals("getChangeDuration")) {
				exams = decodeMessageFromServerForprincipal(str);
				durationChangeSuccess = "accept";
			} else if (str2.equals("GetAllStudents")) {
				Students = decodeMessageFromServerForprincipalCombo1(str);
			} else if (str2.equals("GetAllTeachers")) {
				teacher = decodeMessageFromServerForprincipalCombo2(str);
			} else if (str2.equals("GetAllCourses")) {
				Coursesname = decodeMessageFromServerForprincipalCombo(str);
			} else if (str2.equals("GetCoursesData")) {
				courses = decodeMessageFromServerForCourses1(str);
			} else if (str2.equals("GetStudentsData")) {
				students = decodeMessageFromServerForStudentsData(str);
			} else if (str2.equals("GetTeachersData")) {
				persons = decodeMessageFromServerForPersons(str);
			} else if (str2.equals("getCourseReport")) {
				Static = decodeMessageFromServerForReports2(str);
			} else if (str2.equals("getCopyOfExam")) {
				{
					offlineExamCopy = false;
					QuestionforCopy = decodeMessageFromServerForExamCopy(str);
				}
			} else if (str2.equals("getTeacherReportForPrincipal")) {
				Static = decodeMessageFromServerForReports2(str);
			} else if (str2.equals("getStudentReportForPrincipal")) {
				Static = decodeMessageFromServerForReports(str);
				DoneExamsForReport = decodeMessageFromServerForReports1(str);
			}
			break;
		}

		case 'X': /* Errors */
		{
			String str2 = temp[1];
			if (str2.equals("duration change failed ")) {
				durationChangeFail = "decline";
				MyClientBoundary.staticAlertError("Duration change failed.");
				break;
			} else if (str2.equals("getPendingGrade no pending grade")) {
				// request = "decline";
				MyClientBoundary.staticAlertError("No pending grade for approval!");
				break;
			} else if (str2.equals("question delete unsuccessful")) {
				queDeleteUnsucc = "decline";
				MyClientBoundary.staticAlertError("A question that belongs to a test can't be deleted.");
				break;
			} else if (str2.equals("getOfflineExam")) {
				offexamstatus = "0";
				str2 = temp[2];
				if (str2.equals("invalid execution code")) {
					MyClientBoundary.staticAlertError("Invalid execution code.");
					StatusOF = "invalid";
					break;
				}
				if (str2.equals("error getting exam")) {
					MyClientBoundary.staticAlertError("Error getting exam.");
					StatusOF = "decline"; /* don't start offline exam's timer */
				}
			} else if (str2.equals("Too many exams in exam bank.")) {
				MyClientBoundary.staticAlertError("Too many exams in exam bank.");
				break;
			} else if (str2.equals("Already did this exam")) {
				MyClientBoundary.staticAlertError("Already did this exam");
				getFullOnlineExam="Cant";
				break;
			} else if (str2.equals("Update in database unsuccessful.")) {
				MyClientBoundary.staticAlertError("Update in database unsuccessful.");
				break;
			} else if (str2.equals("Too many questions in exam bank.")) {
				MyClientBoundary.staticAlertError("Too many questions in exam bank.");
				break;
			} else if (str2.equals("no question found")) {
				MyClientBoundary.staticAlertError("No question found.");
				break;
			} else if (str2.equals("no questions under this bank and course duo.")) {
				noQue = "decline";
				MyClientBoundary.staticAlertError("No questions under this bank and course duo.");
				break;
			} else if (str2.equals("Edit question update in database unsuccessful.")) {
				MyClientBoundary.staticAlertError("Edit question update in database unsuccessful.");
				break;
			} else if (str2.equals("failed to fetch exam")) {
				MyClientBoundary.staticAlertError("Failed to fetch exam.");
				break;
			} else if (str2.equals("Offline Exam")) {
				EditOfflineExam = "Yes";
				MyClientBoundary.staticAlertError("Cant Edit Offline Exam!.");
				break;
			} else if (str2.equals("getFullOnlineExam") && op == 'X') {
				MyClientBoundary.staticAlertError("Failed to fetch exam.");
				break;
			} else if (str2.equals("update unsuccessful")) {
				MyClientBoundary.staticAlertError("Update unsuccessful.");
				break;
			} else if (str2.equals("no courses under this teacher")) {
				MyClientBoundary.staticAlertError("No courses under this teacher.");
				break;
			} else if (str2.equals("no subjects under this user")) {
				MyClientBoundary.staticAlertError("No subjects under this user.");
				break;
			} else if (str2.equals("no courses under this subject")) {
				MyClientBoundary.staticAlertError("No courses under this subject.");
				break;
			} else if (str2.equals("getAllCoursesUnderSubjectThatContainQuestions")) {
				MyClientBoundary.staticAlertError("No courses.");
				break;
			} else if (str2.equals("no exams under given teacher")) {
				MyClientBoundary.staticAlertError("No exams under given teacher.");
				break;
			} else if (str2.equals("no students under this exam")) {
				MyClientBoundary.staticAlertError("No students under this exam.");
				StudentsForDoneExam = "No";
				break;
			} else if (str2.equals("no exams under this teacher, subject and course")) {
				MyClientBoundary.staticAlertError("No exams under this teacher, subject and course.");
				status = "1";
				break;
			} else if (str2.equals("no examBank under this teacher")) {
				MyClientBoundary.staticAlertError("No exam bank under this teacher.");
				editExam = "No";
				break;
			} else if (str2.equals("Invalid execution code")) {
				StatusOF = "invalid";
				MyClientBoundary.staticAlertError("Invalid execution code.");
				break;
			} else if (str2.equals("cannot delete an ongoing exam from db")) {
				MyClientBoundary.staticAlertError("Cannot delete an ongoing exam from db.");
				OngoingStatus = "Yes";
				break;
			} else if (str2.equals("bank delete unsuccessful")) {
				MyClientBoundary.staticAlertError("Bank delete unsuccessful.");
				break;
			} else if (str2.equals("no questionBanks under this teacher")) {
				MyClientBoundary.staticAlertError("No question banks under this teacher.");
				break;
			} else if (str2.equals("deleteQuestionsUnderQuestionBankAndCourse")) {
				canDeleteTable = "no";
				//MyClientBoundary.staticAlertError("Delete failed.");
				break;
			} else if (str2.equals("failed to fetch report")) {
				MyClientBoundary.staticAlertError("Failed to fetch report.");
				reportforexam = "No";
				break;
			} else if ((str2.equals("getCourseReport")) && op == 'X') {
				MyClientBoundary.staticAlertError("Failed to fetch course report.");
				getreport = "No";
				break;
			} else if ((str2.equals("getTeacherReportForPrincipal")) && op == 'X') {
				MyClientBoundary.staticAlertError("Failed to fetch report.");
				getreport = "No";
				break;
			} else if ((str2.equals("getStudentReportForPrincipal")) && op == 'X') {
				MyClientBoundary.staticAlertError("Failed to fetch report.");
				getreport = "No";
				break;
			} else if (str2.equals("approveGrade fail")) {
				MyClientBoundary.staticAlertError("Approve grade failure.");
				break;
			} else if (str2.equals("changeGrade fail")) {
				MyClientBoundary.staticAlertError("Change grade failure.");
				break;
			} else if (str2.equals("failed to fetch done exam for student")) {
				MyClientBoundary.staticAlertError("Failed to fetch done exam for student.");
				break;
			} else if (str2.equals("submitExam fail")) {
				MyClientBoundary.staticAlertError("Exam submission failure.");
				break;
			} else if (str2.equals("unable to upload the file to the server.")) {
				MyClientBoundary.staticAlertError("unable to upload the file to the server.");
				break;
			} else if (str2.equals("failed to fetch change duration request for principal")) {
				MyClientBoundary.staticAlertError("Failed to fetch change duration request for principal.");
				break;
			} else if (str2.equals("approvalChangeDuration fail")) {
				MyClientBoundary.staticAlertError("Approval of change duration fail.");
				break;
			} else if (str2.equals("getPendingGrades no pending grades")) {
				Grades = null;
				MyClientBoundary.staticAlertError("No pending grades.");
				pendingGrade = "No";
				break;
			} else if (str2.equals("no students in db")) {
				MyClientBoundary.staticAlertError("No students in DB.");
				break;
			} else if (str2.equals("Offline Exam")) {
				isExamOff = "offline exam";
			} else if (str2.equals("no teachers in db")) {
				MyClientBoundary.staticAlertError("No teachers in DB.");
				break;
			} else if (str2.equals("no courses in db")) {
				MyClientBoundary.staticAlertError("No courses in DB.");
				break;
			} else if (str2.equals("no students info in db")) {
				MyClientBoundary.staticAlertError("No students info in DB.");
				break;
			} else if (str2.equals("no teachers info in db")) {
				MyClientBoundary.staticAlertError("No teachers info in DB.");
				break;
			} else if (str2.equals("no courses info in db")) {
				MyClientBoundary.staticAlertError("No courses info in DB.");
				break;
			} else if (str2.equals("User already logged in")) {
				alreadyLogged = "deny";
				role = "";
				break;
			} else if (str2.equals("Invalid username or password.")) {
				invalidLogin = "deny";
				role = "";
				break;
			} else if (str2.equals("Student submitted an empty exam")) {
				MyClientBoundary.staticAlertError("Student submitted an empty exam");
				break;
			}
		}

		// cases for the three possible user types logging into the server
		case 'P': {
			role = "Principal";
			invalidLogin = "";
			principalFullName = temp[1];
			alreadyLogged = "";
			break;
		}

		case 'T': {
			role = "Teacher";
			invalidLogin = "";
			teacherFullName = temp[1];
			alreadyLogged = "";
			break;
		}

		case 'W': {
			role = "Student";
			invalidLogin = "";
			studentFullName = temp[1];
			alreadyLogged = "";
			break;
		}

		}
		awaitResponse = false;
	}





	/**
	 * decodes message from server to array list of student details for boundary
	 * 
	 * @param message
	 * @return array list of student details
	 */
	private ArrayList<Student> decodeMessageFromServerForStudentsData(String message) {
		ArrayList<Student> studs = new ArrayList<>();
		String[] decodedMsg;
		decodedMsg = message.split("-");
		decodedMsg = decodedMsg[2].split("_");

		for (int j = 0; j < decodedMsg.length; j = j + 4) {
			Student student = new Student();
			student.setId(decodedMsg[j]);
			student.setName(decodedMsg[j + 1]);
			student.setSurname(decodedMsg[j + 2]);
			student.setEmail(decodedMsg[j + 3]);
			studs.add(student);
		}
		return studs;
	}

	/**
	 * decodes message from server to array list of questions in exam
	 * 
	 * @param str
	 * @return array list of questions in exam
	 */
	private ArrayList<QuestionInExam> decodeMessageFromServerForExamCopy(String str) {
		ArrayList<QuestionInExam> question = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(1);
		String[] tempSplit = tempF.split("-");
		str = tempSplit[2];
		decodedMsg = str.split("_");

		// getCopyOfExam_examID_teacherName
		// teacherLastname_subject_course_studentComment_teacherComment_questionText_ans1_ans2_ans3_ans4_chosenAns_correctAns_qScore_actualqScore
		ExamID = decodedMsg[0].replaceAll("1a6gf", "_");
		FinalGrade = decodedMsg[1].replaceAll("1a6gf", "_");
		TeacherName = decodedMsg[2].replaceAll("1a6gf", "_");
		Subject = decodedMsg[3].replaceAll("1a6gf", "_");
		Course = decodedMsg[4].replaceAll("1a6gf", "_");
		StudentComments = decodedMsg[5].replaceAll("1a6gf", "_");
		TeacherComments = decodedMsg[6].replaceAll("1a6gf", "_");
		for (int i = 7; i < decodedMsg.length; i += 9) {
			questionsize++;
			QuestionInExam temp = new QuestionInExam();
			temp.setQuestionText(decodedMsg[i].replaceAll("1a6gf", "_"));
			temp.setAnswer1(new Answer(decodedMsg[i + 1].replaceAll("1a6gf", "_")));
			temp.setAnswer2(new Answer(decodedMsg[i + 2].replaceAll("1a6gf", "_")));
			temp.setAnswer3(new Answer(decodedMsg[i + 3].replaceAll("1a6gf", "_")));
			temp.setAnswer4(new Answer(decodedMsg[i + 4].replaceAll("1a6gf", "_")));
			temp.setChosenAns(Integer.valueOf(decodedMsg[i + 5].replaceAll("1a6gf", "_")));
			temp.setCorrectAns(Integer.valueOf(decodedMsg[i + 6].replaceAll("1a6gf", "_")));
			temp.setScore(Integer.valueOf(decodedMsg[i + 7].replaceAll("1a6gf", "_")));
			temp.setActualScore(Integer.valueOf(decodedMsg[i + 8].replaceAll("1a6gf", "_")));
			question.add(temp);
		}
		return question;
	}

	/**
	 * decodes message from server to array list of string for principal
	 * 
	 * @param str
	 * @return array list of String
	 */
	private ArrayList<String> decodeMessageFromServerForprincipalCombo1(String str) {
		ArrayList<String> temp = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(2);

		String[] tempSplit = tempF.split("-");
		decodedMsg = tempSplit[1].split("_");

		for (int i = 0; i < decodedMsg.length; i++) {
			Idsforpersons.add(decodedMsg[i].replaceAll("1a6gf", "_"));
			temp.add(decodedMsg[i + 1].replaceAll("1a6gf", "_") + " " + decodedMsg[i + 2].replaceAll("1a6gf", "_"));
			i = i + 2;
		}
		return temp;
	}

	/**
	 * decodes message from server to array list of string for principal
	 * 
	 * @param str
	 * @return array list of String
	 */
	private ArrayList<String> decodeMessageFromServerForprincipalCombo2(String str) {
		ArrayList<String> temp = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(2);

		String[] tempSplit = tempF.split("-");
		decodedMsg = tempSplit[1].split("_");

		for (int i = 0; i < decodedMsg.length; i++) {
			Idsforpersons1.add(decodedMsg[i].replaceAll("1a6gf", "_"));
			temp.add(decodedMsg[i + 1].replaceAll("1a6gf", "_") + " " + decodedMsg[i + 2].replaceAll("1a6gf", "_"));
			i = i + 2;
		}
		return temp;
	}

	/**
	 * decodes message from server to array list of string for reports
	 * 
	 * @param str
	 * @return array list of string
	 */
	private ArrayList<String> decodeMessageFromServerForReports(String str) {
		ArrayList<String> reports = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(2);
		String[] tempSplit = tempF.split("-");
		tempF = str.substring(tempSplit[1].length() + 4);
		decodedMsg = tempF.split("_");
		for (int j = 0; j < decodedMsg.length; j++) {
			reports.add(decodedMsg[j].replaceAll("1a6gf", "_"));
		}
		return reports;
	}

	/**
	 * decodes message from server to array list of string for reports
	 * 
	 * @param str
	 * @return array list of string
	 */
	private ArrayList<String> decodeMessageFromServerForReports2(String str) {
		ArrayList<String> reports = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(2);
		String[] tempSplit = tempF.split("-");
		decodedMsg = tempSplit[1].split("_");

		for (int j = 0; j < decodedMsg.length; j++) {
			reports.add(decodedMsg[j].replaceAll("1a6gf", "_"));
		}
		return reports;
	}

	/**
	 * decodes message from server to array list of DoneExam for reports
	 * 
	 * @param str
	 * @return array list of DoneExam
	 */
	private ArrayList<DoneExam> decodeMessageFromServerForReports1(String str) {
		ArrayList<DoneExam> reportstable = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(2);
		String[] tempSplit = tempF.split("-");
		tempF = str.substring(tempSplit[1].length() + 4);
		decodedMsg = tempF.split("_");

		for (int i = 3; i < decodedMsg.length; i++) {
			DoneExam temp = new DoneExam();
			temp.setExamID(decodedMsg[i].replaceAll("1a6gf", "_"));
			temp.setGrade(Integer.parseInt(decodedMsg[i + 1].replaceAll("1a6gf", "_")));
			temp.setDuration(Integer.parseInt(decodedMsg[i + 2].replaceAll("1a6gf", "_")));
			temp.setActualDuration(Integer.parseInt(decodedMsg[i + 3].replaceAll("1a6gf", "_")));
			temp.setTimeOfsub(decodedMsg[i + 4].replaceAll("1a6gf", "_"));
			i = i + 4;
			reportstable.add(temp);
		}
		return reportstable;
	}

	/**
	 * decodes message from server to array list of person for users
	 * 
	 * @param str
	 * @return array list of Person
	 */
	private ArrayList<Person> decodeMessageFromServerForPersons(String str) {
		ArrayList<Person> persontable = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(2);
		String[] tempSplit = tempF.split("-");
		decodedMsg = tempSplit[1].split("_");
		for (int j = 0; j < decodedMsg.length; j++) {
			Person temp = new Person();
			temp.setId(decodedMsg[j].replaceAll("1a6gf", "_"));
			temp.setName(decodedMsg[j + 1].replaceAll("1a6gf", "_"));
			temp.setSurname(decodedMsg[j + 2].replaceAll("1a6gf", "_"));
			temp.setEmail(decodedMsg[j + 3].replaceAll("1a6gf", "_"));
			temp.setSubjects(decodedMsg[j + 4].replaceAll("1a6gf", "_"));
			persontable.add(temp);
			j = j + 4;
		}
		return persontable;
	}

	/**
	 * decodes message from server to array list of Course for courses
	 * 
	 * @param str
	 * @return array list of Course
	 */
	private ArrayList<Course> decodeMessageFromServerForCourses1(String str) {
		ArrayList<Course> CourseTable = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(2);
		String[] tempSplit = tempF.split("-");
		decodedMsg = tempSplit[1].split("_");
		for (int j = 0; j < decodedMsg.length; j++) {
			Course qs = new Course();
			qs.setSubjectName(decodedMsg[j].replaceAll("1a6gf", "_"));
			qs.setCourseName(decodedMsg[j + 1].replaceAll("1a6gf", "_"));
			qs.setCourseCode(decodedMsg[j + 2].replaceAll("1a6gf", "_"));
			j = j + 2;
			CourseTable.add(qs);
		}
		return CourseTable;
	}

	/**
	 * decodes message from server to array list of string for principal
	 * 
	 * @param str
	 * @return array list of String
	 */
	private ArrayList<String> decodeMessageFromServerForprincipalCombo(String str) {
		ArrayList<String> temp = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(2);
		String[] tempSplit = tempF.split("-");
		decodedMsg = tempSplit[1].split("_");
		for (int i = 0; i < decodedMsg.length; i++)
			temp.add(decodedMsg[i].replaceAll("1a6gf", "_"));
		return temp;
	}

	/**
	 * decodes message from server to array list of Exam for principal
	 * 
	 * @param str
	 * @return array list of Exam
	 */
	private ArrayList<Exam> decodeMessageFromServerForprincipal(String str) {
		ArrayList<Exam> exams = new ArrayList<>();
		ArrayList<String> resoans = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(2);
		String[] tempSplit = tempF.split("-");
		decodedMsg = tempSplit[1].split("_");
		for (int i = 0; i < decodedMsg.length; i++) {
			Exam temp = new Exam();
			temp.setExamID(decodedMsg[i].replaceAll("1a6gf", "_"));
			temp.setSubjectName(decodedMsg[i + 1].replaceAll("1a6gf", "_"));
			int duration = Integer.parseInt(decodedMsg[i + 2].replaceAll("1a6gf", "_"));
			temp.setDuration(duration);
			int duration1 = Integer.parseInt(decodedMsg[i + 3].replaceAll("1a6gf", "_"));
			temp.setNewDuration(duration1);
			exams.add(temp);
			resoans.add(decodedMsg[i + 4].replaceAll("1a6gf", "_"));
			i = i + 5;
		}
		resoansprin = resoans;
		return exams;
	}

	/**
	 * decodes message from server to array list of DoneExam for principal to view
	 * grades
	 * 
	 * @param str
	 * @return array list of DoneExam
	 */
	private ArrayList<DoneExam> decodeMessageFromServerForViewG(String str) {
		ArrayList<DoneExam> doneexam = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(2);
		String[] tempSplit = tempF.split("-");
		decodedMsg = tempSplit[1].split("_");
		for (int i = 0; i < decodedMsg.length; i = i + 4) {
			DoneExam temp = new DoneExam();
			temp.setExamID(decodedMsg[i]);
			temp.setCourseName(decodedMsg[i + 1]);
			temp.setSubjectName(decodedMsg[i + 2]);
			temp.setGrade(Integer.valueOf(decodedMsg[i + 3]));
			doneexam.add(temp);
		}
		return doneexam;
	}

	/**
	 * decodes message from server to array list of Exam for editing exams
	 * 
	 * @param str
	 * @return array list of Exam
	 */
	private ArrayList<Exam> decodeMessageFromServerForExamsEdit(String str) {
		ArrayList<Exam> resault = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(1);
		String[] tempSplit = tempF.split("-");
		str = tempSplit[2];
		decodedMsg = str.split("_");
		for (int i = 0; i < decodedMsg.length; i += 2) {
			Exam temp = new Exam();
			temp.setExamID(decodedMsg[i].replaceAll("1a6gf", "_"));
			temp.setExecutionCode(decodedMsg[i + 1].replaceAll("1a6gf", "_"));
			resault.add(temp);
		}
		return resault;
	}

	/**
	 * decodes message from server to array list of string for exams ID
	 * 
	 * @param str
	 * @return array list of String
	 */
	@SuppressWarnings("unused")
	private ArrayList<String> decodeMessageFromServerForExamsID(String str) {
		ArrayList<String> IDs = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(1);
		String[] tempSplit = tempF.split("-");
		str = tempSplit[2];
		decodedMsg = str.split("_");
		for (int i = 0; i < decodedMsg.length; i += 2) {
			String temp = "", temp2 = "";
			temp = decodedMsg[i].replaceAll("1a6gf", "_");
			temp2 = decodedMsg[i + 1].replaceAll("1a6gf", "_");
			IDs.add(temp);
			execCodesGradeApprove.add(temp2);
		}
		return IDs;
	}

	/**
	 * decodes message from server to array list of questions in exam
	 * 
	 * @param str
	 * @return array list of questions in exam
	 */
	private ArrayList<QuestionInExam> decodeMessageFromServerforExamaintion(String str) {
		ArrayList<QuestionInExam> question = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(1);
		String[] tempSplit = tempF.split("-");
		str = tempSplit[2];
		decodedMsg = str.split("_");
		ExamID = decodedMsg[0].replaceAll("1a6gf", "_");
		TeacherName = decodedMsg[1].replaceAll("1a6gf", "_");
		Subject = decodedMsg[2].replaceAll("1a6gf", "_");
		Course = decodedMsg[3].replaceAll("1a6gf", "_");
		StudentComments = decodedMsg[4].replaceAll("1a6gf", "_");
		for (int i = 5; i < decodedMsg.length; i += 7) {
			questionsize++;
			QuestionInExam temp = new QuestionInExam();
			temp.setQuestionID(decodedMsg[i].replaceAll("1a6gf", "_"));
			temp.setQuestionText(decodedMsg[i + 1].replaceAll("1a6gf", "_"));
			Answer an = new Answer(null, null);
			Answer an1 = new Answer(null, null);
			Answer an2 = new Answer(null, null);
			Answer an3 = new Answer(null, null);
			an.setAnswerText(decodedMsg[i + 2].replaceAll("1a6gf", "_"));
			temp.setAnswer1(an);
			an1.setAnswerText(decodedMsg[i + 3].replaceAll("1a6gf", "_"));
			temp.setAnswer2(an1);
			an2.setAnswerText(decodedMsg[i + 4].replaceAll("1a6gf", "_"));
			temp.setAnswer3(an2);
			an3.setAnswerText(decodedMsg[i + 5].replaceAll("1a6gf", "_"));
			temp.setAnswer4(an3);
			temp.setScore(Integer.valueOf(decodedMsg[i + 6].replaceAll("1a6gf", "_")));
			question.add(temp);
		}
		return question;
	}

	/**
	 * decodes message from server to array list of string for exams report
	 * 
	 * @param str
	 * @return array list of String
	 */
	private ArrayList<String> decodeMessageFromServerforExamReport(String str) {
		ArrayList<String> reports = new ArrayList<>();

		String[] decodedMsg;
		String tempF = str.substring(2);
		String[] tempSplit = tempF.split("-");
		decodedMsg = tempSplit[1].split("_");

		for (int j = 0; j < decodedMsg.length; j++)
			reports.add(decodedMsg[j].replaceAll("1a6gf", "_"));

		return reports;
	}

	@SuppressWarnings("unused")
	private String decodeMessageFromServerforGradesApproval(String str) {
		String temp = str.substring(1);
		String[] decodeSus;
		decodeSus = temp.split("_");
		return decodeSus[4];
	}

	/**
	 * creating ArrayList of exams that will transfer to the view DB screen
	 * 
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unused")
	private ArrayList<Exam> decodeMessageFromServer(String str) {
		ArrayList<Exam> examTable = new ArrayList<>();
		String[] decodedMsg;
		String temp = str.substring(1);
		int i = 0;

		decodedMsg = temp.split("-");

		for (int j = 0; j < decodedMsg.length / 5; j++) {
			Exam ex = new Exam();
			ex.setExamID(decodedMsg[i].replaceAll("1a6gf", "_"));
			ex.setSubject(new Subject(decodedMsg[i + 1].replaceAll("1a6gf", "_")));
			ex.setCourse(new Course(decodedMsg[i + 2].replaceAll("1a6gf", "_")));
			ex.setDuration(Integer.parseInt(decodedMsg[i + 3].replaceAll("1a6gf", "_")));
			ex.setScores(new ArrayList<String>(Arrays.asList(decodedMsg[i + 4].replaceAll("1a6gf", "_"))));
			i = i + 5;
			examTable.add(ex);
		}
		return examTable;
	}

	/**
	 * decodes message from server to array list of subject
	 * 
	 * @param str
	 * @return array list of subject
	 */
	private ArrayList<Subject> decodeMessageFromServerForSubject(String str) {
		ArrayList<Subject> subTable = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(2);
		String[] tempSplit = tempF.split("-");
		decodedMsg = tempSplit[1].split("_");

		for (int j = 0; j < decodedMsg.length; j++) {
			Subject subj = new Subject();
			subj.setSubjectName(decodedMsg[j].replaceAll("1a6gf", "_"));
			subTable.add(subj);
		}
		return subTable;
	}

	/**
	 * decodes message from server to array list of question
	 * 
	 * @param str
	 * @return array list of question
	 */
	private ArrayList<Question> decodeMessageFromServerForQuestions(String str) {
		ArrayList<Question> questionTable = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(1);
		String[] tempSplit = tempF.split("-");
		str = tempSplit[2];
		decodedMsg = str.split("_");

		int i = 0;

		for (int j = 0; j < decodedMsg.length / 7; j++) {
			Question qs = new Question();
			qs.setQuestionID(decodedMsg[i].replaceAll("1a6gf", "_"));
			qs.setQuestionText(decodedMsg[i + 1].replaceAll("1a6gf", "_"));
			qs.setAnswer1(new Answer(decodedMsg[i + 2].replaceAll("1a6gf", "_")));
			qs.setAnswer2(new Answer(decodedMsg[i + 3].replaceAll("1a6gf", "_")));
			qs.setAnswer3(new Answer(decodedMsg[i + 4].replaceAll("1a6gf", "_")));
			qs.setAnswer4(new Answer(decodedMsg[i + 5].replaceAll("1a6gf", "_")));
			qs.setCorrectAns(Integer.parseInt(decodedMsg[i + 6].replaceAll("1a6gf", "_")));

			i = i + 7;
			questionTable.add(qs);
		}
		return questionTable;
	}

	/**
	 * decodes message from server to array list of course
	 * 
	 * @param str
	 * @return array list of course
	 */
	private ArrayList<Course> decodeMessageFromServerForCourses(String str) {
		ArrayList<Course> CourseTable = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(2);
		String[] tempSplit = tempF.split("-");
		decodedMsg = tempSplit[1].split("_");
		for (int j = 0; j < decodedMsg.length; j++) {
			Course qs = new Course();
			qs.setCourseName(decodedMsg[j].replaceAll("1a6gf", "_"));
			CourseTable.add(qs);
		}
		return CourseTable;
	}

	/**
	 * decodes message from server to array list of exam for exams duration
	 * 
	 * @param str
	 * @return array list of exam
	 */
	private ArrayList<Exam> decodeMessageFromServerExamDur(String str) {
		ArrayList<Exam> ExamTable = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(1);
		String[] tempSplit = tempF.split("-");
		str = tempSplit[2];
		decodedMsg = str.split("_");
		int i = 0, dur, dur1;
		for (int j = 0; j < decodedMsg.length / 7; j++) {
			Exam ex1 = new Exam();
			ex1.setExamID(decodedMsg[i].replaceAll("1a6gf", "_"));
			ex1.setSubjectName(decodedMsg[i + 1].replaceAll("1a6gf", "_"));
			dur = Integer.parseInt(decodedMsg[i + 2].replaceAll("1a6gf", "_"));
			ex1.setDuration(dur);
			dur1 = Integer.parseInt(decodedMsg[i + 3].replaceAll("1a6gf", "_"));
			ex1.setNewDuration(dur1);
			ex1.setCourseName(decodedMsg[i + 4].replaceAll("1a6gf", "_"));
			ex1.setLockStatusString(decodedMsg[i + 5].replaceAll("1a6gf", "_"));
			ex1.setOngoingString(decodedMsg[i + 6].replaceAll("1a6gf", "_"));
			i = i + 7;
			ExamTable.add(ex1);
		}
		return ExamTable;
	}

	/**
	 * decodes message from server to question for editing question
	 * 
	 * @param str
	 * @return Question object
	 */
	private Question decodeMessageFromServerforEditQuestion(String str) {
		Question temp = new Question();
		String[] decodedMsg;
		String tempF = str.substring(1);
		String[] tempSplit = tempF.split("-");
		str = tempSplit[2];
		decodedMsg = str.split("_");

		temp.setQuestionID(decodedMsg[0].replaceAll("1a6gf", "_"));
		temp.setQuestionText(decodedMsg[1].replaceAll("1a6gf", "_"));

		Answer an = new Answer(null, null);
		Answer an1 = new Answer(null, null);
		Answer an2 = new Answer(null, null);
		Answer an3 = new Answer(null, null);

		an.setAnswerText(decodedMsg[2].replaceAll("1a6gf", "_"));
		temp.setAnswer1(an);
		an1.setAnswerText(decodedMsg[3].replaceAll("1a6gf", "_"));
		temp.setAnswer2(an1);
		an2.setAnswerText(decodedMsg[4].replaceAll("1a6gf", "_"));
		temp.setAnswer3(an2);
		an3.setAnswerText(decodedMsg[5].replaceAll("1a6gf", "_"));
		temp.setAnswer4(an3);

		temp.setCorrectAns(Integer.parseInt(decodedMsg[6].replaceAll("1a6gf", "_")));

		for (int i = 8; i < decodedMsg.length; i++)
			QuestionsForEdit.add((decodedMsg[i].replaceAll("1a6gf", "_")));

		return temp;
	}

	/**
	 * decodes message from server to array list of DoneExam for exam approval
	 * 
	 * @param str
	 * @return array list of DoneExam
	 */
	private ArrayList<DoneExam> decodeMessageFromServerforExamapprove(String str) {
		ArrayList<DoneExam> temp1 = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(1);
		String[] tempSplit = tempF.split("-");
		str = tempSplit[2];
		decodedMsg = str.split("_");
		for (int j = 0; j < decodedMsg.length; j++) {
			DoneExam temp = new DoneExam();
			temp.setStudentID(decodedMsg[j].replaceAll("1a6gf", "_"));
			temp.setGrade(Integer.valueOf(decodedMsg[j + 2].replaceAll("1a6gf", "_")));
			temp.setCopyStatus(decodedMsg[j + 4].replaceAll("1a6gf", "_"));
			temp1.add(temp);
			j = j + 4;
		}
		return temp1;
	}

	/**
	 * decodes message from server to array list of question in exam for editing
	 * exam
	 * 
	 * @param str
	 */
	private void decodeMessageFromServerforEditexam(String str) {
		Exam temp = new Exam();
		ArrayList<QuestionInExam> adder = new ArrayList<>();
		String[] decodedMsg;
		String tempF = str.substring(1);
		String[] tempSplit = tempF.split("-");
		str = tempSplit[2];
		decodedMsg = str.split("_");

		temp.setExamID(decodedMsg[0].replaceAll("1a6gf", "_"));
		temp.setSubjectName(decodedMsg[1].replaceAll("1a6gf", "_"));
		temp.setCourseName(decodedMsg[2].replaceAll("1a6gf", "_"));
		int sr = Integer.parseInt(decodedMsg[4].replaceAll("1a6gf", "_"));
		temp.setDuration(sr);
		temp.setExecutionCode(decodedMsg[3].replaceAll("1a6gf", "_"));
		Comment come1 = new Comment(Comment.Type.ForStudent, decodedMsg[5].replaceAll("1a6gf", "_"));
		Comment come2 = new Comment(Comment.Type.ForTeacher, decodedMsg[6].replaceAll("1a6gf", "_"));
		temp.setStudentComment(come1);
		temp.setTeacherComment(come2);
		for (int i = 7; i < decodedMsg.length; i++) {
			sr = Integer.parseInt(decodedMsg[i + 1].replaceAll("1a6gf", "_"));
			QuestionInExam Quesadd = new QuestionInExam("", decodedMsg[i].replaceAll("1a6gf", "_"),
					MyClientBoundary.usName, sr);
			adder.add(Quesadd);
			i = i + 1;
		}
		QuestionsInex = adder;
		exam2 = temp;
	}

	/**
	 * 
	 * Handles a message sent from client GUI
	 *
	 * @param message the message sent.
	 */
	public void handleMessageFromClientUI(Object message) {
		try {
			openConnection();// in order to send more than one message
			awaitResponse = true;
			sendToServer(message);
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			clientUI.display("Could not send message to server: Terminating client." + e);
			quit();
		}
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			System.out.println("sent to server quit");
			System.out.println("logout_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
			this.sendToServer("logout_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
			closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}
}