package Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import Data.Answer;
import Data.Comment;
import Data.Course;
import Data.DoneExam;
import Data.Exam;
import Data.MyFile;
import Data.Question;
import Data.QuestionInExam;
import Data.Report;
import Server.MyServerBoundary.Affiliation;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import java.io.*;

/**
 * arbitrator between clients & database
 * 
 * @author Ayala Cohen & Yarden Adika
 */
public class MyServer extends AbstractServer {
	/**
	 * mySQL connection
	 */
	JDBCSingleton jdbc;
	
	public static String examReportActual;

	Map<String, Reminder> timerThreads = new HashMap<>(); /* maps student id's to timer threads */
	Map<String, String> studentIDs = new HashMap<>(); /* maps student id's to exam execution code */
	Map<String, Integer> finishTimes = new HashMap<>(); /* maps student id's to their finish times */

	/**
	 * constructor, instantiates the database connection
	 * 
	 * @param port The server port to database
	 */
	public MyServer(int port) {
		super(port);
		jdbc = JDBCSingleton.getInstance();
	}

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		/* get update duration for exam from client */
		String newDuration, ExamID, str, tmp, res, username, password; /* new exam duration in minutes & exam ID */
		Exam ex;
		Question q;
		String op;
		boolean isFailExam = false;
		System.out.println(this.getClass().getName() + " -  message from client:  " + msg);

		if (msg instanceof MyFile) {
			MyFile clientFile = (MyFile) msg;
			int fileSize = clientFile.getSize();
			if (!clientFile.isPrefixValid()) {
				try {
					client.sendToClient("X-invalid file type");
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (clientFile.getFileSource().equals(MyFile.FileSource.StudentSubmitOfflineExam))
					isFailExam = true;
				else
					return;
			}
			System.out.println("Message received: " + ((MyFile) msg).getFileName() + " from " + client);
			System.out.println("length " + fileSize);
			try {
				FileOutputStream fos = null;
				BufferedOutputStream bos = null;
				if (!isFailExam) {
					byte[] mybytearray = clientFile.getMybytearray();
					File newFile = new File("C:\\CEMS_server\\" + clientFile.getFileName());
					fos = new FileOutputStream(newFile); /* Create file output stream */
					bos = new BufferedOutputStream(fos); /* Create BufferedFileOutputStream */
					bos.write(mybytearray, 0, clientFile.getSize()); /* Write byte array to output stream */
				}
				if (clientFile.getFileSource() == MyFile.FileSource.TeacherAddOfflineExam) /* file is a new exam */
				{
					if (clientFile.getExecutionCode().length() != 4) {
						client.sendToClient("X-Execution code too short");
						return;
					}
					Exam e = prepareFileForOfflineExam(clientFile);
					if (jdbc.updateExamFileInDB(e) == false)
						client.sendToClient("X-Update file in database unsuccessful.");
					else
						client.sendToClient("Y-file uploaded successfully to the database.");
				} else if (clientFile
						.getFileSource() == MyFile.FileSource.StudentSubmitOfflineExam) /*
																						 * file is a submitted exam by
																						 * student
																						 */
				{
					if (clientFile.getFileName() == null)
						client.sendToClient("X-invalid faild name");

					if (jdbc.SubmitAlready(jdbc.getExamByExecutionCode(clientFile.getExecutionCode()),
							jdbc.getUserID(clientFile.getUsername(), clientFile.getPassword()))) {
						client.sendToClient("X-Already submitted this exam!");
						return;
					}
					if (jdbc.isLocked(jdbc.getExamByExecutionCode(clientFile.getExecutionCode())))
						client.sendToClient("X-submit offline exam-Exam time is over cannot submit exam");

					int actualDur = timerThreads.get(jdbc.getUserID(clientFile.getUsername(), clientFile.getPassword()))
							.stopTimer();
					finishTimes.remove(jdbc.getUserID(clientFile.getUsername(), clientFile.getPassword()));
					DoneExam e = prepareFileForStudentSubmitOfflineExam(clientFile);
					e.setActualDuration(actualDur);
					if (jdbc.updateStudentOfflineExamInDB(e) == false)
						client.sendToClient("X-Update file in database unsuccessful.");
					else
						client.sendToClient("Y-file uploaded successfully to the database.");
				}
				if (!isFailExam) {
					bos.flush();
					fos.flush();
				}
				isFailExam = false;
			} catch (Exception e) {
				System.out.println("Error uploading File to Server");
				try {
					client.sendToClient("X-unable to upload the file to the server.");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			return;
		}

		if (!(msg instanceof String)) {
			System.out.println("Server : Invalid message from client !!");
			return;
		}
		str = (String) msg;

		String decoded[] = str.split("_");
		op = decoded[0];
		try {
			switch (op) {
			case "uniqueExecutionCode": /*
										 * check if execution code is not used already //
										 * uniqueExecutionCode_executionCode
										 */
				if (jdbc.getExamByExecutionCode(decoded[1]) == null)
					client.sendToClient("uniqueExecutionCode-OK"); /* no exam with this execution code */
				else
					client.sendToClient("uniqueExecutionCode-taken"); /* there's an exam with this execution code */
				break;
			case "generateUniqueExecutionCode": /*
												 * generates a unique alphanumeric code of 4 digits //
												 * generateUniqueExecutionCode
												 */
				String code = jdbc.generateUniqueExecutionCode();
				if (code == null)
					client.sendToClient("X-Error creating code");
				else
					client.sendToClient("S-generateUniqueExecutionCode-" + code);
				break;
			case "addExam": /* user wants to add a new exam */
				ex = prepareExam(decoded); /*
											 * addExam_type_username_password_bankName_courseName_duration_execCode_StudentCom_TeacherCom_QIDs_Scores
											 */
				if (ex == null) {
					client.sendToClient("X-Too many exams in exam bank.");
					break;
				}
				if (jdbc.updateExamInDB(ex) == false) {
					client.sendToClient("X-Update in database unsuccessful.");
					break;
				}
				client.sendToClient("Y-Exam updated successfully in DB"); /* update successful */
				break;
			case "addQuestion": /* user wants to add a new question */// addQuestion_questionBankName_username_password_QuestionText_answer1_answer2_answer3_answer4_rightAnswerNumber_courseNames
				q = prepareQuestion(decoded); //
				System.out.println("prepared");
				if (q == null) {
					client.sendToClient("X-Too many questions in exam bank."); /*
																				 * server tells client there are too
																				 * many exams in bank & course duo
																				 */
					break;
				}
				if (jdbc.updateQuestionInDB(q) == false) {
					client.sendToClient("X-Update in database unsuccessful.");
					break;
				}
				client.sendToClient("Y-Question updated successfully in DB"); /* update successful */
				break;
			case "getQuestion": /* get full question from database // getQuestion_qId */
				String question = this.jdbc.getQuestion(decoded[1]);
				if (question != null)
					client.sendToClient("S-question-" + question);
				else
					client.sendToClient("X-no question found");
				break;
			case "getQuestions": /* get all questions under given bank ID and course ID */
				// getQuestions_bankId_courseID
				String questions = this.jdbc.getQuestionsUnderBankAndCourseToString(decoded[1], decoded[2]);
				if (questions == null) {
					client.sendToClient("X-no questions under this bank and course duo.");
					break;
				} else
					client.sendToClient("S-questions-" + questions);
				break;
			case "editQuestion": /*
									 * editQuestion_bankdName_QuestionID_NewQuestionText_NewAns1_NewAns2_NewAns3_NewAns4_NewRightAns_NewCourseNames
									 */
				q = prepareEditQuestion(decoded); //
				if (q == null) {
					client.sendToClient("X-Update in database unsuccessful.");
					break;
				}
				if (jdbc.editQuestionInDB(q) == false) {
					client.sendToClient("X-Edit question update in database unsuccessful.");
					break;
				}
				client.sendToClient("Y-Edit question updated successfully in DB"); /* update successful */
				break;
			case "getExam": /* fetch exam from database // getExam_examID */
				int type = jdbc.getExamType(decoded[1]); // check to see if exam is online or offline
				if (type == 0) {
					client.sendToClient("X-Offline Exam");
					break;
				}
				String exam = jdbc.getExam(decoded[1]);
				if (exam != null)
					client.sendToClient("S-getExam-" + exam);
				else
					client.sendToClient("X-failed to fetch exam");
				break;
			case "canDoExam": /*
								 * a check for offline exam - is execution code valid,is the exam unlocked,
								 * student already did the exam // canDoExam_executionCode_username_password
								 */
				if (jdbc.SubmitAlready(jdbc.getExamByExecutionCode(decoded[1]), jdbc.getUserID(decoded[2], decoded[3]))
						|| !jdbc.validExecutionCode(decoded[1])
						|| jdbc.isLocked(jdbc.getExamByExecutionCode(decoded[1]))) {
					client.sendToClient("X-Invalid execution code");
					break;
				} else {
					client.sendToClient("Y-Valid execution code");
					break;
				}
			case "getFullOnlineExam": /* fetch full exam for student // getFullOnlineExam_executionCode_studentID */
				if (decoded.length < 3) {
					client.sendToClient("X-getFullOnlineExam-bad execution code");
					break;
				}
				if (jdbc.getExamType(jdbc.getExamByExecutionCode(decoded[1])) == 0) {
					client.sendToClient("X-getFullOnlineExam-bad execution code");
					break;
				}
				if (jdbc.SubmitAlready(jdbc.getExamByExecutionCode(decoded[1]), decoded[2])) {
					client.sendToClient("X-Already did this exam");
					break;
				}
				String fullExam = jdbc.getFullOnlineExam(decoded[1], decoded[2]);
				if (fullExam.equals("fail") || fullExam.equals("exam locked") || fullExam.equals("bad execution code")
						|| fullExam.equals("wrong id")) {
					client.sendToClient("X-getFullOnlineExam-" + fullExam);
				} else {
					client.sendToClient("S-getFullOnlineExam-" + fullExam);
					jdbc.setExamOngoing(jdbc.getExamByExecutionCode(decoded[1]), 1); /* set exam ongoing */
					jdbc.updateExamLog(jdbc.getExamByExecutionCode(decoded[1])); /* add exam to log */
					// start timer
					startTimerForExam(decoded[1], decoded[2], client); // execution code, student id
				}
				break;
			case "getOfflineExam": /*
									 * fetch offline exam for student
									 * //getOfflineExam_executionCode_username_password
									 */
				if (!jdbc.validExecutionCode(decoded[1])) {
					client.sendToClient("X-getOfflineExam-invalid execution code");
					break;
				}
				if (getOfflineExam(decoded[1], jdbc.getUserID(decoded[2], decoded[3]),
						client)) /* send offline exam to student */
				{
					if (!timerThreads.containsKey(jdbc.getUserID(decoded[2], decoded[3]))) {
						jdbc.setExamOngoing(jdbc.getExamByExecutionCode(decoded[1]), 1); /* set exam ongoing */
						jdbc.updateExamLog(jdbc.getExamByExecutionCode(decoded[1])); /* add exam to log */
						// start timer
						startTimerForExam(decoded[1], jdbc.getUserID(decoded[2], decoded[3]), client);
					}
				} else {
					client.sendToClient("X-getOfflineExam-error getting exam");
				}
				break;
			case "getCopyOfExam": /* get copy of checked exam */
				// getCopyOfExam_examID_studentID or getCopyOfExam_examID_username_password
				String studentid;
				boolean forTeacher = false;
				if (decoded.length == 4) {
					studentid = jdbc.getUserID(decoded[2], decoded[3]);
					forTeacher = false; // 4Student
				} else {
					studentid = decoded[2]; // 4Teacher
					forTeacher = true;
				}
				Object copy = jdbc.getCopyOfExam(decoded[1], studentid, forTeacher);
				if (copy == null) {
					client.sendToClient("X-Student submitted an empty exam");
					break;
				}
				if (copy instanceof String)
					client.sendToClient("S-getCopyOfExam-" + copy);
				if (copy instanceof MyFile) {
					if (((MyFile) copy).getFileName().equals("null")) {
						client.sendToClient("X-Student submitted an empty exam");
						break;
					}
					File newFile = new File("C:\\CEMS_server\\" + ((MyFile) copy).getFileName());
					byte[] mybytearray = new byte[(int) newFile.length()];
					FileInputStream fis = new FileInputStream(newFile);
					BufferedInputStream bis = new BufferedInputStream(fis);

					((MyFile) copy).initArray(mybytearray.length);
					((MyFile) copy).setSize(mybytearray.length);

					bis.read(((MyFile) copy).getMybytearray(), 0, mybytearray.length);

					client.sendToClient(copy);
				}
				break;
			case "editExam": /* edit an exam in DB */
				// editExam_oldExamID_type_bankName_courseName_duration_execCode_StudentCom_TeacherCom_QIDs_Scores
				ex = prepareEditExam(decoded);
				if (ex != null) {
					if (jdbc.editExamInDB(ex)) {
						client.sendToClient("Y-update successful");
						break;
					}
				}
				client.sendToClient("X-update unsuccessful");
				break;
			case "getCourses": /* get all courses under this username and password *//// getCourses_username_password
				String courses = this.jdbc.getTeacherCourses(decoded[1], decoded[2]);
				if (courses == null) {
					client.sendToClient("X-no courses under this teacher");
					break;
				} else
					client.sendToClient("S-courses-" + courses);
				break;
			case "getSubjects": /* get all subjects under this username and password */
				// getSubjects_username_password
				String subjects = this.jdbc.getAllTeacherSubjects(decoded[1], decoded[2]);
				if (subjects == null) {
					client.sendToClient("X-no subjects under this user");
					break;
				} else
					client.sendToClient("S-subjects-" + subjects);
				break;
			case "getCoursesUnderSubject&Teacher": /* get all teacher courses under given exambank */
				// getCoursesUnderSubject&Teacher_examBankName
				String coursesUnderSubject = this.jdbc.getTeacherCoursesUnderExamBank(decoded[1]);
				if (coursesUnderSubject == null) {
					client.sendToClient("X-no courses under this subject"); // shouldn't happen
					break;
				} else
					client.sendToClient("S-getCoursesUnderSubject&Teacher-" + coursesUnderSubject);
				break;
			case "getAllCoursesUnderSubjectThatContainQuestions": /*
																	 * get all courses under given exam bank that have
																	 * at least 1 question associated with them //
																	 * getAllCoursesUnderSubjectThatContainQuestions_examBankName
																	 */
				String coursesUnderSubjectwQ = this.jdbc.getAllCoursesUnderSubjectThatContainQuestions(decoded[1]);
				if (coursesUnderSubjectwQ != null)
					client.sendToClient("S-getAllCoursesUnderSubjectThatContainQuestions" + coursesUnderSubjectwQ);
				else
					client.sendToClient("X-getAllCoursesUnderSubjectThatContainQuestions-no courses");
				break;
			case "getAllExamsOfTeacher": /* returns all exams made by given teacher */
				// getAllExamsOfTeacher_username_password
				String allExams = this.jdbc.getTeacherExams(this.jdbc.getUserID(decoded[1], decoded[2]));
				if (allExams == null) {
					client.sendToClient("X-no exams under given teacher");
					break;
				} else
					client.sendToClient("S-getAllExamsOfTeacher-" + allExams);
				break;
			case "getStudentForDoneExam": /* returns all students who took particular exam */
				// getStudentForDoneExam_examId
				String students = this.jdbc.getStudentForDoneExam(decoded[1]);
				if (students == null) {
					client.sendToClient("X-no students under this exam");
					break;
				} else
					client.sendToClient("S-getStudentForDoneExam-" + students);
				break;
			case "getExamsUnderTeacherExamBankCourse": /* returns all exams related to teacher, subject and course */
				// getExamsUnderTeacherExamBankCourse_BankName_CourseName_username_password
				String examsId = this.jdbc.getExamsUnderTeacherExamBankCourse(decoded[1], decoded[2], decoded[3],
						decoded[4]);
				if (examsId == null) {
					client.sendToClient("X-no exams under this teacher, subject and course");
					break;
				} else
					client.sendToClient("S-getExamsUnderTeacherExamBankCourse-" + examsId);
				break;
			case "examBank": /* get all exam banks under this username and password */// examBank_username_password
				String examBank = this.jdbc.getExisitingExamBanksUnderGivenTeacher(decoded[1], decoded[2]);
				if (examBank == null) {
					client.sendToClient("X-no examBank under this teacher");
					break;
				} else
					client.sendToClient("S-examBank-" + examBank);

				break;
			case "deleteExamBank": /* clear exam bank from all exams under it // deleteExamBank_courseName */
				if (jdbc.isThereExamOngoingInBank(jdbc.getSubjectCodeOfCourse(
						decoded[1])) != 0) /* check if there is an ongoing exam inside this bank */
				{
					client.sendToClient("X-cannot delete an ongoing exam from db");
					break;
				}
				if (jdbc.deleteExamBank(jdbc.getSubjectCodeOfCourse(decoded[1]), jdbc.getCourseID(decoded[1])))
					client.sendToClient("Y-bank delete success");
				else
					client.sendToClient("X-bank delete unsuccessful");
				break;
			case "deleteExam": /* delete exam from db */
				// deleteExam_ExamID
				if (jdbc.isExamOngoing(decoded[1]) != 0) /* check if the exam is currently ongoing */
				{
					client.sendToClient("X-cannot delete an ongoing exam from db");
					break;
				}
				if (jdbc.getExamType(decoded[1]) == 0) // if exam if offline
				{
					String f = jdbc.getExamFileName(decoded[1]);
					File obj = new File("C:\\CEMS_server\\" + f);
					if (!obj.delete()) {
						client.sendToClient("X-delete file unsuccessful");
						break;
					}
				}
				if (jdbc.deleteExam(decoded[1]))
					client.sendToClient("Y-exam delete success");
				else
					client.sendToClient("X-exam delete unsuccessful");
				break;
			case "questionBank": /* get all question banks under given username and password */
				// quesitonBank_username_password
				String questionBank = this.jdbc.getExisitingQuestionBanksUnderGivenTeacher(decoded[1], decoded[2]);
				if (questionBank == null) {
					client.sendToClient("X-no questionBanks under this teacher");
					break;
				} else
					client.sendToClient("S-questionBank-" + questionBank);
				break;
			case "deleteQuestionBank": /* delete all questions under question bank */
				if (jdbc.deleteQuestionBank(jdbc.getBankID(decoded[1]))) // deleteQuestionBank_qbankName
					client.sendToClient("Y-bank delete success");
				else
					client.sendToClient("X-bank delete unsuccessful"); // error - delete exam bank before question bank!
				break;
			case "deleteQuestionsUnderQuestionBankAndCourse": /*
																 * delete questions from bank under given course //
																 * deleteQuestionsUnderQuestionBankAndCourse_bankName_courseName
																 */
				String bID = jdbc.getBankID(decoded[1]);
				String cID = jdbc.getCourseID(decoded[2]);
				if (bID == null || cID == null) {
					client.sendToClient("X-deleteQuestionsUnderQuestionBankAndCourse-invalid bank or course");
					break;
				}
				if (jdbc.deleteQuestionsUnderQuestionBankAndCourse(bID, cID)) {
					client.sendToClient("Y-deleteQuestionsUnderQuestionBankAndCourse-success");
				} else {
					client.sendToClient("X-deleteQuestionsUnderQuestionBankAndCourse-fail");
				}
				break;
			case "deleteQuestion": /* delete specific question from db */
				// deleteQuestion_QuestionID
				if (jdbc.deleteQuestion(decoded[1]))
					client.sendToClient("Y-question delete success");
				else
					client.sendToClient("X-question delete unsuccessful"); // error - delete exam bank before question
																			// bank!
				break;
			case "changeExamDuration": /* change duration of exam // changeExamDuration_examID_newDur_Rationale */
				if (jdbc.isExamOngoing(decoded[1]) == 1) /* if exam is ongoing */
				{
					// enter request
					if (jdbc.enterChangeDurRequest(decoded[1], decoded[2], decoded[3])) {
						client.sendToClient("Y-duration change request noted");
					} else
						client.sendToClient("X-duration change failed");
					break;
				} else if (jdbc.isExamOngoing(decoded[1]) != 1
						&& jdbc.updateExamDuration(decoded[1], decoded[2])) /* exam is not ongoing */
					client.sendToClient("Y-duration changed successfuly");
				else
					client.sendToClient("X-duration change failed");
				break;

			case "lockExam": // lockexam_examID_command(0-unlock, 1-lock)
				boolean success = this.jdbc.lockOrUnlockExam(decoded[1], Integer.parseInt(decoded[2]));
				if (success) {
					client.sendToClient("Y-update sucessful");

					if (jdbc.isExamOngoing(decoded[1]) == 1) { // if exam is ongoing, lock exam to all students
						String executionCode = jdbc.getExectionCodeByExamID(decoded[1]);

						// fetch all students doing this exam
						ArrayList<String> studIds = new ArrayList<>();
						for (Map.Entry<String, String> entry : studentIDs
								.entrySet()) { /* studentIDs: key=student id, val=execution code */
							if (entry.getValue().equals(executionCode)) {
								studIds.add(entry.getKey());
							}
						}

						// stop all timers
						for (int i = 0; i < studIds.size(); i++) {
							timerThreads.get(studIds.get(i))
									.lockExam(); /* timerThreads: key=student id, val=timer thread */
						}
					}
				} else
					client.sendToClient("X-update unsuccessful");
				break;
			case "getExamReport": /* get exam report // getExamReport_examID */
				examReportActual = createExamReport(decoded);
				if (examReportActual != null)
					client.sendToClient("S-examReport-" + examReportActual);
				else
					client.sendToClient("X-failed to fetch report");
				break;
			case "getCourseReport": /* get course report // getCourseReport_courseName */
				String courseReport = jdbc.getCourseReport(jdbc.getCourseID(decoded[1]));
				if (courseReport != null)
					client.sendToClient("S-getCourseReport-" + courseReport);
				else
					client.sendToClient("X-getCourseReport-fail");
				break;
			case "getTeacherReportForPrincipal": /* get full teacher report // getTeacherReportForPrincipal_teacherID */
				String teacherReport = jdbc.getTeacherReportForPrincipal(decoded[1]);
				if (teacherReport != null)
					client.sendToClient("S-getTeacherReportForPrincipal-" + teacherReport);
				else
					client.sendToClient("X-getTeacherReportForPrincipal-fail");
				break;
			case "getStudentReportForPrincipal": /* get full student report // getStudentReportForPrincipal_studentID */
				String studentReport = jdbc.getStudentReportForPrincipal(decoded[1]);
				if (studentReport != null)
					client.sendToClient("S-getStudentReportForPrincipal-" + studentReport);
				else
					client.sendToClient("X-getStudentReportForPrincipal-fail");
				break;
			case "approveGrade": /*
									 * teacher approves student grade that was generated by the system //
									 * approveGrade_examID_studentID_optionalTeacherComment
									 */
				if (jdbc.approveStudentGrade(decoded[1], decoded[2], decoded[3]))
					client.sendToClient("Y-approveGrade success");
				else
					client.sendToClient("X-approveGrade fail");
				break;
			case "changeGrade": /*
								 * teacher changes grade that was given by the system //
								 * changeGrade_username_pass_examId_studentId_newGrade_reason_comment
								 */
				if (decoded.length < 7) {
					client.sendToClient("X-Teacher must give a reason for changing the grade");
					break;
				}
				String comment = null;
				if (decoded.length == 8)
					comment = decoded[7];
				if (jdbc.changeStudentGrade(jdbc.getUserID(decoded[1], decoded[2]), decoded[3], decoded[4],
						Integer.parseInt(decoded[5]), decoded[6], comment))
					client.sendToClient("Y-changeGrade success");
				else
					client.sendToClient("X-changeGrade fail");
				break;
			case "GetDoneExamForStudent": /* student get all his checked exam */
				// GetDoneExamForStudent_username_password
				String DoneExam = jdbc.GetDoneExamForStudent(jdbc.getUserID(decoded[1], decoded[2]));
				if (DoneExam != null)
					client.sendToClient("S-GetDoneExamForStudent-" + DoneExam);
				else
					client.sendToClient("X-failed to fetch done exam for student");
				break;
			case "submitExam": /* student submit online exam */
				// submitExam_examID_username_password_qID1-ans1_qID2-ans2..
				// int successful=1;
				if (jdbc.isLocked(decoded[1])
						&& !jdbc.SubmitAlready(decoded[1], jdbc.getUserID(decoded[2], decoded[3]))) // check if it's ok
																									// to submit exam in
																									// database
				{
					client.sendToClient("X-submitExam-Exam is locked, cannot submit exam error");
					// DoneExam f = prepareFailExam(decoded[1], jdbc.getUserID(decoded[2],
					// decoded[3]));
					// successful=0;
					// finishTimes.remove(jdbc.getUserID(decoded[2], decoded[3])); // remove student
					// from this list
					// jdbc.updateFinishedFailExamInDB(f);
					// break;
				}
				if (jdbc.SubmitAlready(decoded[1], jdbc.getUserID(decoded[2], decoded[3]))) {
					client.sendToClient("X-already submitted exam!");
					break;
				}
				String answers = "";
				if (decoded.length > 5) {
					for (int i = 4; i < decoded.length; i++) {
						answers += decoded[i] + "_"; // answers = 01002-3_01003-4_..
					}
				} else
					answers = decoded[4];
				DoneExam e = prepareOnlineDoneExam(decoded[1], jdbc.getUserID(decoded[2], decoded[3]), answers);
				if (jdbc.isExamOngoing(decoded[1]) == 1) // student submitted exam before endtime
					e.setFinishedSuccessful(1);
				else
					e.setFinishedSuccessful(0);

				int actualDur = stopTimerForStudent(decoded[1], jdbc.getUserID(decoded[2], decoded[3]));
				e.setActualDuration(actualDur);
				finishTimes.remove(jdbc.getUserID(decoded[2], decoded[3])); // remove student from this list
				int count = 0;

				/* see if everyone that started the exam finished it */
				for (Map.Entry<String, String> entry : studentIDs
						.entrySet()) { /* studentIDs: key=student id, val=execution code */
					if (entry.getValue().equals(jdbc.getExectionCodeByExamID(decoded[1]))) {
						count++;
					}
				}
				if (count == 0) /* everyone that started the exam finished it - lock the exam */
					jdbc.lockOrUnlockExam(decoded[1], 1);
				boolean isOK = handleSubmitExamRequest(e);
				if (isOK)
					client.sendToClient("Y-submitExam success");
				else
					client.sendToClient("X-submitExam fail");
				break;
			case "getChangeDuration": /* get all change duration requests for principal */
				// getChangeDuration
				String ChangeDuration = jdbc.getChangeDuration();
				if (ChangeDuration != null)
					client.sendToClient("S-getChangeDuration-" + ChangeDuration);
				else
					client.sendToClient("X-failed to fetch change duration request for principal");
				break;
			case "approvalChangeDuration": /* confirm/deny change duration request for principal */
				// approvalChangeDuration_examID_status - status = (1 - confirm, 2 - deny)
				if (jdbc.approvalChangeDuration(decoded[1], decoded[2])) {
					client.sendToClient("Y-approvalChangeDuration success");
					if ((Integer.parseInt(decoded[2]) == 1)
							&& (jdbc.isExamOngoing(decoded[1]) == 1)) /* change duration approved & exam is ongoing */
					{
						int newDur = jdbc.getExamDuration(jdbc.getExectionCodeByExamID(decoded[1]));
						// fetch all students doing this exam
						ArrayList<String> IDs = new ArrayList<>();
						for (Map.Entry<String, String> entry : studentIDs
								.entrySet()) { /* studentIDs: key=student id, val=execution code */
							if (entry.getValue().equals(jdbc.getExectionCodeByExamID(decoded[1]))) {
								IDs.add(entry.getKey());
							}
						}

						// notify timer threads of new duration
						for (int i = 0; i < IDs.size(); i++) {
							timerThreads.get(IDs.get(i))
									.newDuration(newDur); /* timerThreads: key=student id, val=timer thread */
						}
					}
				} else
					client.sendToClient("X-approvalChangeDuration fail");
				break;
			case "getPendingGrades": /*
										 * get all pending grades of given exam //
										 * getPendingGrades_username_password_examID
										 */
				String pendingGrades = jdbc.getTeachersPendingGrades(jdbc.getUserID(decoded[1], decoded[2]),
						decoded[3]);
				if (pendingGrades == null) // no pending grades
				{
					client.sendToClient("X-getPendingGrades no pending grades");
				} else
					client.sendToClient("S-getPendingGrades-" + pendingGrades);
				break;
			case "GetAllStudents": /* get all students for principal */
				// GetAllStudents
				String studentsName = this.jdbc.GetAllPersonByAffiliation("Student");
				if (studentsName == null) {
					client.sendToClient("X-no students in db");
					break;
				} else
					client.sendToClient("S-GetAllStudents-" + studentsName);
				break;
			case "GetAllTeachers": /* get all teachers for principal */
				// GetAllTeachers
				String teachersName = this.jdbc.GetAllPersonByAffiliation("Teacher");
				if (teachersName == null) {
					client.sendToClient("X-no teachers in db");
					break;
				} else
					client.sendToClient("S-GetAllTeachers-" + teachersName);
				break;
			case "GetAllCourses": /* get all courses for principal */
				// GetAllCourses
				String coursesName = this.jdbc.GetAllCourses();
				if (coursesName == null) {
					client.sendToClient("X-no courses in db");
					break;
				} else
					client.sendToClient("S-GetAllCourses-" + coursesName);
				break;
			case "GetStudentsData": /* get all students info from db for principal */
				// GetStudentsData
				String Students = this.jdbc.GetPersonDataByAffiliation("Student");
				if (Students == null) {
					client.sendToClient("X-no students info in db");
					break;
				} else
					client.sendToClient("S-GetStudentsData-" + Students);
				break;
			case "GetTeachersData": /* get all teachers info from db for principal */
				// GetTeachersData
				String Teachers = this.jdbc.GetPersonDataByAffiliation("Teacher");
				if (Teachers == null) {
					client.sendToClient("X-no teachers info in db");
					break;
				} else
					client.sendToClient("S-GetTeachersData-" + Teachers);
				break;
			case "GetCoursesData": /* get all teachers info from db for principal */
				// GetCoursesData
				String Courses = this.jdbc.GetCoursesData();
				if (Courses == null) {
					client.sendToClient("X-no courses info in db");
					break;
				} else
					client.sendToClient("S-GetCoursesData-" + Courses);
				break;
			case "logout": /* client says he is disconnected // disconnected_username */
				if (decoded.length == 3) {
					updateClientDetails(client.getInetAddress().getLocalHost(), client.getInetAddress().getHostName(),
							decoded[1], "Disconnected");

					username = decoded[1]; // logout_username_password
					password = decoded[2];
					if (!this.jdbc.setUserStatus(username, password, 0)) /* update user status to offline in DB */
					{
						client.sendToClient("X-update unsuccessful");
						break;
					}
				}
				client.sendToClient("logout");
				break;
			case "login": /* client wants to log in, server tells client what type he is */
				String messageToClient = handleLoginRequest(decoded);
				client.sendToClient(messageToClient);
				if (messageToClient.charAt(0) != 'X')
					updateClientDetails(client.getInetAddress().getLocalHost(), client.getInetAddress().getHostName(),
							decoded[1], "Connected");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String createExamReport(String[] decoded) {
		String examReport = jdbc.getExamReport(decoded[1]);
		return examReport;
	}

	/**
	 * takes a done exam and submits it in database
	 * 
	 * @param e done online exam
	 * @return indication of submit in database succeeded
	 */
	public boolean handleSubmitExamRequest(DoneExam e) { // REFACTOR

		Report report = createDoneExamReport(e); /* get up to date report for this exam */
		
		return updateDoneExamInDB(e, report);
	}

	public boolean updateDoneExamInDB(DoneExam e, Report report) { // REFACTOR
		return jdbc.updateFinishedExamInDB(e, report);
	}

	/**
	 * create a report of this exam: distribution, average, median
	 * 
	 * @param e done exam
	 * @return full up to date report for this exam
	 */
	public Report createDoneExamReport(DoneExam e) { // REFACTOR
		if (e == null)
			return null;
		Report report = new Report();

		int average = calculateExamAverage(getGrades(e.getExamID()));
		int median = calculateExamMedian(getGrades(e.getExamID()));
		report.setAverage(average);
		report.setMedian(median);

		int[] distribution = new int[10]; /* distribution for all the various deciles */
		distribution = getExamDistribution(e.getExamID()); /* get most current distribution of this exam */

		if (e.getSysGrade() <= 9)
			distribution[0]++;
		else if (10 <= e.getSysGrade() && e.getSysGrade() <= 19)
			distribution[1]++;
		else if (20 <= e.getSysGrade() && e.getSysGrade() <= 29)
			distribution[2]++;
		else if (30 <= e.getSysGrade() && e.getSysGrade() <= 39)
			distribution[3]++;
		else if (40 <= e.getSysGrade() && e.getSysGrade() <= 49)
			distribution[4]++;
		else if (50 <= e.getSysGrade() && e.getSysGrade() <= 59)
			distribution[5]++;
		else if (60 <= e.getSysGrade() && e.getSysGrade() <= 69)
			distribution[6]++;
		else if (70 <= e.getSysGrade() && e.getSysGrade() <= 79)
			distribution[7]++;
		else if (80 <= e.getSysGrade() && e.getSysGrade() <= 89)
			distribution[8]++;
		else if (90 <= e.getSysGrade() && e.getSysGrade() <= 100)
			distribution[9]++;

		report.setDistribution(distribution);

		return report;

	}

	/**
	 * get exam's distribution from database
	 * 
	 * @param examID
	 * @return exam's full distribution for all the various deciles
	 */
	public int[] getExamDistribution(String examID) { // REFACTOR
		int[] distribution = new int[10];

		for (int i = 0; i < 10; i++) {
			distribution[i] = getNumStudentWhoGotGradeInGivenDecile(i,
					examID); /* return number of students who got grade that is in range of decile i */
		}

		return distribution;
	}

	/**
	 * return number of students who got grade that is in range of decile i
	 * 
	 * @param i
	 * @param examID
	 * @return return number of students who got grade that is in range of decile i
	 */
	public int getNumStudentWhoGotGradeInGivenDecile(int i, String examID) {
		return jdbc.getNumStudentWhoGotGradeInGivenDecile(i, examID);
	}

	/**
	 * get exam's median grade
	 * 
	 * @param examID
	 * @return exam's median grade
	 */
	public int calculateExamMedian(ArrayList<Integer> grades) { // REFACTOR
		int median;
		if (grades.size() == 0)
			return 0;
		if (grades.size() == 1)
			return grades.get(0);
		if (grades.size() % 2 == 0)
			median = (grades.get(grades.size() / 2 - 1) + grades.get(grades.size() / 2)) / 2;
		else
			median = grades.get(((grades.size() + 1) / 2) - 1);
		return median;
	}

	/**
	 * return array list of grades of given exam from database
	 * 
	 * @param examID
	 * @return all approved grades of given exam
	 */
	public ArrayList<Integer> getGrades(String examID) { // REFACTOR
		return jdbc.getGrades(examID);
	}

	/**
	 * get exam's average grade
	 * 
	 * @param grades
	 * @return exam's average grade
	 */
	public int calculateExamAverage(ArrayList<Integer> grades) { // REFACTOR
		int sum = 0;
		for (Integer grade : grades)
			sum += grade;

		return sum / grades.size();
	}

	/**
	 * according to the client's message, builds the response message from the
	 * server to the client
	 * 
	 * @param decoded message from client
	 * @return the message to send client
	 */
	public String handleLoginRequest(String[] decoded) { // REFACTOR
		if (!LoginValidMessage(decoded))
			return "X-Invalid username or password.";
		String username = decoded[1];
		String password = decoded[2];

		if (getUserStatusInDB(username, password) == 1)
			return "X-User already logged in";

		String res = getUserTypeFromDB(username, password);

		if (res == null)
			return "X-Invalid username or password.";

		if (!setUserStatusInDB(username, password, 1)) /* update user status to online in DB */
			return "X-update unsuccessful";

		String fullName = getUserFullNameInDB(username, password);

		if (res.equals("Teacher"))
			return "T-" + fullName;
		else if (res.equals("Student"))
			return "W-" + fullName;
		else if (res.equals("Principal")) /* principal */
			return "P-" + fullName;

		return "X-Invalid username or password.";
	}

	/**
	 * gets user's full name from database
	 * 
	 * @param username
	 * @param password
	 * @return user's full name
	 */
	public String getUserFullNameInDB(String username, String password) { // REFACTOR
		return jdbc.getUserFullName(username, password);
	}

	/**
	 * sets user's status in database
	 * 
	 * @param username
	 * @param password
	 * @param i        set status to i (0-offline, 1-online)
	 * @return if setting status in DB succeeded or not
	 */
	public boolean setUserStatusInDB(String username, String password, int i) { // REFACTOR
		return jdbc.setUserStatus(username, password, 1);
	}

	/**
	 * get user's type using the database
	 * 
	 * @param username
	 * @param password
	 * @return user's type (Teacher, Student, Principal)
	 */
	public String getUserTypeFromDB(String username, String password) { // REFACTOR
		return jdbc.getUserType(username, password);
	}

	/**
	 * get user's status in system using the database (1-online, 0-offline)
	 * 
	 * @param username
	 * @param password
	 * @return user's status (1-online, 0-offline)
	 */
	public int getUserStatusInDB(String username, String password) { // REFACTOR
		return this.jdbc.getUserStatus(username, password);
	}

	/**
	 * checks if login message is valid
	 * 
	 * @param decoded message from client
	 * @return true if message is valid
	 */
	public boolean LoginValidMessage(String[] decoded) { // REFACTOR
		if (decoded.length != 3)
			return false;
		return true;
	}

	/**
	 * prepare a done exam that will go into the database with grade 0
	 * 
	 * @param examID
	 * @param studentID
	 * @return done exam with grade 0
	 */
	private DoneExam prepareFailExam(String examID, String studentID) {
		DoneExam f = new DoneExam();

		f.setExamID(examID);
		f.setStudentID(studentID);
		f.setCourseID(examID.substring(2, 4));
		f.setBankID(examID.substring(0, 2));
		f.setTime(java.time.LocalDateTime.now());
		f.setDate(java.time.LocalDate.now());
		f.setDuration(jdbc.getExamDuration(jdbc.getExectionCodeByExamID(examID)));
		f.setActualDuration(f.getDuration());
		f.setSysGrade(0);
		f.setFinishedSuccessful(0);

		return f;
	}

	/**
	 * stops timer for this student if student submits exam
	 * 
	 * @param examID
	 * @param studentID
	 * @return the time it took the student to finish the exam
	 */
	private int stopTimerForStudent(String examID, String studentID) {
		int dur;
		if (jdbc.isLocked(examID))
			dur = jdbc.getExamDuration(jdbc.getExectionCodeByExamID(examID));
		else
			dur = timerThreads.get(studentID).stopTimer(); /* stop timer */

		finishTimes.remove(studentID);

		return dur; /* the time it took the student to finish the exam */
	}

	/**
	 * converts client's message to a done exam
	 * 
	 * @param examID
	 * @param studentID
	 * @param answers
	 * @return done exam
	 */
	private DoneExam prepareOnlineDoneExam(String examID, String studentID, String answers) {
		DoneExam e = new DoneExam();
		String[] EQans = answers.split("_"); // 01001-1
		String dbAns = "";
		ArrayList<QuestionInExam> examQuestions = new ArrayList<>();

		e.setExamID(examID);
		e.setStudentID(studentID);
		e.setCourseID(examID.substring(2, 4));
		e.setBankID(examID.substring(0, 2));
		e.setTime(java.time.LocalDateTime.now());
		e.setDate(java.time.LocalDate.now());
		e.setDuration(jdbc.getExamDuration(jdbc.getExectionCodeByExamID(examID)));

		for (int i = 0; i < EQans.length; i++) { // prepares exam answers in format: 1,2,3,..
			String[] temp = EQans[i].split("-"); // temp[0]=01000 temp[1]=2

			QuestionInExam question = new QuestionInExam();
			question.setQuestionID(temp[0]);
			question.setChosenAns(Integer.parseInt(temp[1]));
			examQuestions.add(question);

			dbAns += temp[1];
			if (i < EQans.length - 1)
				dbAns += ",";
		}
		e.setQuestions(examQuestions);
		e.setDBanswer(dbAns);
		e.setSysGrade(jdbc.generateSystemGrade(e)); /* generate grade */

		return e;
	}

	/**
	 * converts client file to submitted offline exam
	 * 
	 * @param clientFile
	 * @return offline exam for submission
	 */
	private DoneExam prepareFileForStudentSubmitOfflineExam(MyFile clientFile) {
		DoneExam e = new DoneExam();
		e.setExamType(0); /* exam type is offline (0-offline, 1-online) */
		e.setBankID(jdbc.getBankID(clientFile.getSubjectName()));
		e.setCourseID(jdbc.getCourseID(clientFile.getCourseName()));
		e.setStudentID(jdbc.getUserID(clientFile.getUsername(), clientFile.getPassword()));
		e.setExamID(jdbc.getExamByExecutionCode(clientFile.getExecutionCode()));
		e.setExecutionCode(clientFile.getExecutionCode());
		e.setFileName(clientFile.getFileName());
		e.setStudentDuration(clientFile.getDuration());
		e.setTime(java.time.LocalDateTime.now()); // (I think it needs to be start time & date and not end time and
													// date.)
		e.setDate(java.time.LocalDate.now());
		if (jdbc.isLocked(e.getExamID()) || e.getFileName() == null)
			e.setFinishedSuccessful(0);
		else
			e.setFinishedSuccessful(1);
		return e;
	}

	/**
	 * sends the offline exam to the student
	 * 
	 * @param executionCode
	 * @param studentID
	 * @param client
	 * @return if file was sent ok - true, false otherwise
	 */
	private boolean getOfflineExam(String executionCode, String studentID, ConnectionToClient client) {
		// getOfflineExam_executionCode_studentID
		String LocalfilePath = jdbc.getExamFileName(jdbc.getExamByExecutionCode(executionCode));
		MyFile exam = new MyFile(LocalfilePath);

		try {

			File newFile = new File("C:\\CEMS_server\\" + LocalfilePath);
			byte[] mybytearray = new byte[(int) newFile.length()];
			FileInputStream fis = new FileInputStream(newFile);
			BufferedInputStream bis = new BufferedInputStream(fis);

			exam.initArray(mybytearray.length);
			exam.setSize(mybytearray.length);
			exam.setDuration(jdbc.getExamDuration(executionCode));
			exam.setCourseName(jdbc.getCourseName(jdbc.getExamByExecutionCode(executionCode).substring(2, 4)));
			exam.setSubjectName(jdbc.getBankName(jdbc.getExamByExecutionCode(executionCode).substring(0, 2)));

			bis.read(exam.getMybytearray(), 0, mybytearray.length);

			client.sendToClient(exam);
		} catch (Exception e) {
			System.out.println("Error send (Files)msg) to Client");
			return false;
		}
		return true;
	}

	/**
	 * starts exam timer for student
	 * 
	 * @param executionCode exam execution code
	 * @param studentID     student ID number
	 * @param client
	 */
	private void startTimerForExam(String executionCode, String studentID, ConnectionToClient client) // execution code,
																										// student id
	{
		int duration = jdbc.getExamDuration(executionCode); /* get initial exam duration in minutes */
		jdbc.setExamOngoing(jdbc.getExamByExecutionCode(executionCode), 1);
		Reminder timer = new Reminder(duration, executionCode, studentID, client); /* start timer */
		timerThreads.put(studentID, timer); /* add timer thread to map (key=student id, val=timer thread) */
		studentIDs.put(studentID, executionCode); /* map student id to exam execution code */
		try {
			client.sendToClient("ExamTimer_StartTimer_" + executionCode + "_" + studentID + "_" + duration);
			System.out.println("ExamTimer_StartTimer_" + executionCode + "_" + studentID + "_" + duration);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * receives decoded message from client & decodes the message into an exam to
	 * insert into the DB
	 * 
	 * @param decoded client's message chopped to array
	 * @return exam object that represents the new exam details
	 */
	private Exam prepareEditExam(String[] decoded) { // editExam_oldExamID_type_bankName_courseName_duration_execCode_StudentCom_TeacherCom_QIDs_Scores
//		1 old exam id
//		2 type
//		3 bankname
//		4 course name
//		5 duration
//		6 execution code
//		7 comments 4 students
//		8 comments 4 teacher
//		9 question ids
//		10 question scores
//		addExam_type_username_password_bankName_courseName_duration_execCode_StudentCom_TeacherCom_QIDs_Scores	
		String[] splitQID = decoded[9].split(" ");
		String[] splitScores = decoded[10].split(" ");
		ArrayList<String> questionID = new ArrayList<>();
		ArrayList<String> scores = new ArrayList<>();
		String temp;
		String newExamID = "";
		for (int i = 0; i < splitQID.length; i++) {
			questionID.add(splitQID[i]);
			scores.add(splitScores[i]);
		}
		Exam ex = new Exam();
		ex.setExamType(Integer.parseInt(decoded[2])); /* exam type (1-online/0-offline) */
		ex.setSubjectName(decoded[3]);
		ex.setBankID(jdbc.getBankID(decoded[3]));
		ex.setCourseName(decoded[4]);
		ex.setCourseID(jdbc.getCourseID(decoded[4]));
		ex.setDuration(Integer.parseInt(decoded[5]));
		ex.setExecutionCode(decoded[6]);
		temp = decoded[7].replace("1a6gf", "_");
		ex.setStudentComment(new Comment(Comment.Type.ForStudent, temp));
		temp = decoded[8].replaceAll("1a6gf", "_");
		ex.setTeacherComment(new Comment(Comment.Type.ForTeacher, temp));
		ex.setQuestionsID(questionID);
		ex.setScores(scores);
		ex.setExamID(decoded[1]);
		if (!ex.getBankID().equals(ex.getExamID().substring(0, 2))
				|| !ex.getCourseID().equals(ex.getExamID().substring(2, 4))) {
			newExamID = generateExamID(ex.getBankID(), ex.getCourseID());
			if (newExamID == null) /* too many exams under bankId and course - notify user of the error */
				return null;
		} else
			newExamID = ex.getExamID();

		ex.setNewExamID(newExamID);
		return ex;
	}

	/**
	 * prepare exam details that will go into the database according to the file
	 * that the client sent to the server
	 * 
	 * @param clientFile
	 * @return offline exam - file details
	 */
	private Exam prepareFileForOfflineExam(MyFile clientFile) {
		Exam ex = new Exam();
		String authorId = this.jdbc.getUserID(clientFile.getUsername(), clientFile.getPassword());
		String bankId = this.jdbc.getBankID(clientFile.getSubjectName());
		String courseId = this.jdbc.getCourseID(clientFile.getCourseName());
		/*
		 * File
		 */
//		1 - type
//		2 - username
//		3 - password
//		4 - bankName = subject
//		5 - courseName
//		6 - duration
//		7 - execCode
//		8 - fileName
		ex.setExamType(0); /* 1-online, 0-offline */
		ex.setAuthorID(authorId); /* teacher ID */
		ex.setBankID(bankId); // e.g. 02 (math)
		ex.setCourseID(courseId); // e.g. 03 (algebra)
		ex.setDuration(clientFile.getDuration());
		ex.setExecutionCode(clientFile.getExecutionCode());
		ex.setFileName(clientFile.getFileName());

		String examId = generateExamID(ex.getBankID(), ex.getCourseID());
		if (examId == null) /* too many exams under bankId and course - notify user of the error */
			return null;
		ex.setExamID(examId);

		return ex;
	}

	/**
	 * updates users details in boundary
	 * 
	 * @param host
	 * @param ip
	 * @param status
	 * @param username
	 */
	private void updateUserInfo(String host, String ip, String status, String username) {
		ServerUI.sb.updateUserInfo(host, ip, status, username);
	}

	/**
	 * prepares a question object to insert into the database
	 * 
	 * @param decoded question details in the form of a string
	 * @return question object with given details
	 */
	private Question prepareQuestion(String[] decoded) {
		// addQuestion_questionBankName_username_password_QuestionText_answer1_answer2_answer3_answer4_rightAnswerNumber_courseNames
		ArrayList<Course> courseIds = new ArrayList<>();
		Question q = new Question();
		String str = decoded[10];
		String courseNames[] = str.split("-");
		String temp;

		for (int i = 0; i < courseNames.length; i++) {
			Course c = new Course(courseNames[i], this.jdbc.getCourseID(courseNames[i]));
			courseIds.add(c);
		}
		q.setCourseIDs(courseIds);
		q.setqBankName(decoded[1]); /* question bank's name */
		q.setqBankID(this.jdbc.getBankID(decoded[1])); /* question bank ID */
		q.setAuthorID(this.jdbc.getUserID(decoded[2], decoded[3])); /* teacher ID */
		temp = decoded[4].replaceAll("1a6gf", "_");
		q.setQuestionText(temp);
		temp = decoded[5].replaceAll("1a6gf", "_");
		Answer answer1 = new Answer(temp);
		temp = decoded[6].replaceAll("1a6gf", "_");
		Answer answer2 = new Answer(temp);
		temp = decoded[7].replaceAll("1a6gf", "_");
		Answer answer3 = new Answer(temp);
		temp = decoded[8].replaceAll("1a6gf", "_");
		Answer answer4 = new Answer(temp);
		q.setAnswer1(answer1);
		q.setAnswer2(answer2);
		q.setAnswer3(answer3);
		q.setAnswer4(answer4);
		q.setCorrectAns(Integer.parseInt(decoded[9]));
		q.setQuestionID(generateQuestionID(q.getqBankID()));
		q.setqNum(q.getQuestionID().substring(2, 5));
		return q;
	}

	/**
	 * prepares a question object to edit into the database
	 * 
	 * @param decoded question details in the form of a string
	 * @return question object with given details
	 */
	private Question prepareEditQuestion(String[] decoded) {
		// editQuestion_bankdName_QuestionID_NewQuestionText_NewAns1_NewAns2_NewAns3_NewAns4_NewRightAns_NewCourseNames
//		1 bank name
//		2 qID
//		3 txt
//		4 ans1
//		5 ans2
//		6 ans3
//		7 ans4
//		8 rightAns
//		9 cNames

		ArrayList<Course> courseIds = new ArrayList<>();
		Question q = new Question();
		String newQuestionID = "";
		String str = decoded[9];
		String courseNames[] = str.split("-");
		String temp;
		for (int i = 0; i < courseNames.length; i++) {
			Course c = new Course(courseNames[i], this.jdbc.getCourseID(courseNames[i]));
			courseIds.add(c);
		}
		q.setqBankName(decoded[1]); /* bank name */
		q.setQuestionID(decoded[2]); /* question ID */
		q.setqBankID(jdbc.getBankID(q.getqBankName())); /* question bank id */
		if (!q.getqBankID().equals(q.getQuestionID().substring(0, 2))) { /* if user changed question's bank */
			newQuestionID = this.generateQuestionID(q.getqBankID());
			if (newQuestionID == null) /* too many questions under bankId */
				return null;
		} else
			newQuestionID = q.getQuestionID();
		q.setQuestionID(newQuestionID);
		q.setCourseIDs(courseIds);
		temp = decoded[3].replaceAll("1a6gf", "_");
		q.setQuestionText(temp);
		temp = decoded[4].replaceAll("1a6gf", "_");
		Answer answer1 = new Answer(temp);
		temp = decoded[5].replaceAll("1a6gf", "_");
		Answer answer2 = new Answer(temp);
		temp = decoded[6].replaceAll("1a6gf", "_");
		Answer answer3 = new Answer(temp);
		temp = decoded[7].replaceAll("1a6gf", "_");
		Answer answer4 = new Answer(temp);
		q.setAnswer1(answer1);
		q.setAnswer2(answer2);
		q.setAnswer3(answer3);
		q.setAnswer4(answer4);
		q.setCorrectAns(Integer.parseInt(decoded[8]));
		q.setqNum(q.getQuestionID().substring(2, 5)); /* question number in bank */
		return q;
	}

	/**
	 * generates a question ID using the database
	 * 
	 * @param bank question bank ID that the question belongs to
	 * @return question's ID
	 */
	private String generateQuestionID(String bank) {
		String examNum = jdbc.getQuestionNumber(bank);
		if (examNum == null) /* bank is full */
			return null;
		return bank + examNum;
	}

	/**
	 * prepares an exam object to insert into the database
	 * 
	 * @param decoded exam details in the form of a string
	 * @return exam object with given details
	 */
	private Exam prepareExam(String[] decoded) {
		Exam ex = new Exam();
		ArrayList<String> questionsID = new ArrayList<>();
		ArrayList<String> scores = new ArrayList<>();
		String authorId = this.jdbc.getUserID(decoded[2], decoded[3]);
		String bankId = this.jdbc.getBankID(decoded[4]);
		String courseId = this.jdbc.getCourseID(decoded[5]);
		String[] splitQID = decoded[10].split(" ");
		String[] splitScores = decoded[11].split(" ");
		/*
		 * addExam_type_username_password_bankName_courseName_duration_execCode_StudentCom_TeacherCom_QIDs_Scores
		 *///
//		1 - type
//		2 - username
//		3 - password
//		4 - bankName
//		5 - courseName
//		6 - duration
//		7 - execCode
//		8 - studentComment
//		9 - Teacher Comment
//		10 - QIDs
//		11 - scores
//		12 - fileName
		ex.setExamType(Integer.parseInt(decoded[1])); /* 1-online, 0-offline */
		ex.setAuthorID(authorId); /* teacher ID */
		ex.setBankID(bankId); // e.g. 02 (math)
		ex.setCourseID(courseId); // e.g. 03 (algebra)
		ex.setDuration(Integer.parseInt(decoded[6]));
		ex.setExecutionCode(decoded[7]);
		String temp = decoded[8].replaceAll("1a6gf", "_");
		ex.setStudentComment(new Comment(Comment.Type.ForStudent, temp));
		temp = decoded[9].replaceAll("1a6gf", "_");
		ex.setTeacherComment(new Comment(Comment.Type.ForTeacher, temp));

		for (int i = 0; i < splitQID.length; i++) {
			questionsID.add(splitQID[i]);
			scores.add(splitScores[i]);
		}

		ex.setQuestionsID(questionsID);
		ex.setScores(scores);

		String examId = generateExamID(ex.getBankID(), ex.getCourseID());
		if (examId == null) /* too many exams under bankId and course - notify user of the error */
			return null;
		ex.setExamID(examId);
		return ex;
	}

//	
//	 * generates exam ID using the database
//	 * 
//	 * @param bankID bank ID that the exam belongs to
//	 * @param course course that the exam belongs to
//	 * @return exam's ID
//	 */
	private String generateExamID(String bankID, String courseID) {
		String examID = bankID + courseID; // 0203
		String examNum = jdbc.getExamNumber(bankID, courseID);
		if (examNum == null) /* too many exams under bankID & course */
			return null;
		return examID + examNum; // 020301
	}

	/**
	 * Hook method called each time a new client connection is accepted. The default
	 * implementation does nothing.
	 * 
	 * @param client the connection connected to the client.
	 */
	protected void clientConnected(ConnectionToClient client) {
		/* output client details */
		try {
			updateClientDetails(client.getInetAddress().getLocalHost(), client.getInetAddress().getHostName(), "",
					"Connected");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * send Client details to show in serverBoundary UI
	 * 
	 * @param ip
	 * @param hostName
	 * @param username
	 * @param status
	 */
	private void updateClientDetails(InetAddress ip, String hostName, String username, String status) {
		MyServerBoundary.Affiliation aff = null;
		if (!username.equals("")) {
			String res = jdbc.getUserTypeByUsername(username);
			if (res == null)
				return;
			switch (res) {
			case "Teacher":
				aff = Affiliation.Teacher;
				break;
			case "Student":
				aff = Affiliation.Student;
				break;
			case "Principal":
				aff = Affiliation.Principal;
				break;
			}
		}
		ServerUI.sb.updateServerBoundary(hostName, ip, username, status, aff);
	}

	/**
	 * 
	 * @param localHost
	 * @param hostName
	 * @param username
	 */
//	private void updateUserDetails(InetAddress localHost, String hostName, String username, String status) {
//		ServerUI.sb.updateServerBoundaryUsers(localHost, hostName, username, status);
//		
//	}
	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
		this.jdbc.resetUsersStatus();
		System.out.println("Server listening for connections on port " + getPort());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	/**
	 * exam timer class
	 * 
	 * @author Ayala Cohen
	 *
	 */
	private class Reminder {
		Timer timer;
		int endTimeMin, startTimeMin = 0;
		String timerName = "";
		String executionCode;
		String studentID;
		private ConnectionToClient client;
		int duration;

		public void lockExam() {
			jdbc.lockOrUnlockExam(jdbc.getExamByExecutionCode(executionCode), 1); /* times up - lock exam ! */
			jdbc.setExamOngoing(jdbc.getExamByExecutionCode(executionCode), 0); /* set exam not ongoing */
			try {
				client.sendToClient("ExamTimer_ExamLocked_" + studentID + "_" + executionCode);
				System.out.println("send to client : " + "ExamTimer_ExamLocked_" + studentID + "_" + executionCode);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int finish = (int) (System.nanoTime() / (Math.pow(10, 9) * 60)); /* global finish time (in minutes) */
			finishTimes.put(studentID, finish);
			studentIDs.remove(studentID);
			timerThreads.remove(studentID);
			timer.cancel();
		}

		public Reminder(int duration, String executionCode, String studentID, ConnectionToClient client) {
			timer = new Timer();
			this.executionCode = executionCode;
			this.duration = duration;
			this.studentID = studentID;
			this.client = client;
			this.startTimeMin = (int) (System.nanoTime() / (Math.pow(10, 9) * 60));
			jdbc.setExamOngoing(jdbc.getExamByExecutionCode(executionCode), 1);
			timer.schedule(new RemindTask(), duration * 60000); /* duration minutes */
		}

		public int stopTimer() { // if student submits exam before finish time
			System.out.println("timer stopped" + timerName);
			// kill everyone!!

			try {
				client.sendToClient("ExamTimer_Stop_" + studentID + "_" + executionCode);
				System.out.println("ExamTimer_Stop_" + studentID + "_" + executionCode);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int finish = (int) (System.nanoTime() / (Math.pow(10, 9) * 60)); /* global finish time (in minutes) */
			finishTimes.put(studentID, finish);
			studentIDs.remove(studentID);
			timerThreads.remove(studentID);
			timer.cancel();
			if (timerThreads.size() == 0)
				jdbc.setExamOngoing(jdbc.getExamByExecutionCode(executionCode), 0);
			if (finish - startTimeMin >= jdbc.getExamDuration(executionCode)) {
				jdbc.lockOrUnlockExam(jdbc.getExamByExecutionCode(executionCode), 1); /* times up - lock exam ! */
			}
			return finish - startTimeMin; // return the duration of the exam in minutes
		}

		class RemindTask extends TimerTask {

			@Override
			public void run() {
				System.out.println("Time's up!" + timerName);
				lockExam();
			}
		}

		/* set new timer duration */
		public void newDuration(int newDur) {
			if (newDur == duration)
				return;
			long endTime = System.nanoTime();
			int endTimeMin = (int) (System.nanoTime() / (Math.pow(10, 9) * 60));
			int leftTime = newDur - (endTimeMin - startTimeMin);
			if (leftTime > 0) { // newDur > oldDur
				timer.cancel();
				timer = new Timer();
				timer.schedule(new RemindTask(), leftTime * 60000);
				try {
					System.out.println("send to client " + client.getName() + " : ExamTimer_NewDuration_" + studentID
							+ "_" + executionCode + "_" + leftTime);
					client.sendToClient("ExamTimer_NewDuration_" + studentID + "_" + executionCode + "_" + leftTime);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else // newDur < oldDur
				lockExam();
		}
	}
}