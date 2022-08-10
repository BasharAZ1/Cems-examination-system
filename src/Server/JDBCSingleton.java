package Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import Data.Answer;
import Data.Comment;
import Data.Course;
import Data.DoneExam;
import Data.Exam;
import Data.MyFile;
import Data.MyFile.FileSource;
import Data.Question;
import Data.QuestionInExam;
import Data.Report;
import Data.Teacher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

/**
 * @author Ayala Cohen & Yarden Adika
 * 
 *         holds connection to the mySQL database singleton - there is only 1
 *         instance of the JDBC connector
 */
public class JDBCSingleton {
	int temp;
	/**
	 * handle to this class
	 */
	private static JDBCSingleton jdbc = null;
	/**
	 * handle to connection
	 */
	private Connection con;

	/**
	 * constructor, called only once by getInstance() method
	 */
	private JDBCSingleton() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
		}

		try {
			this.con = DriverManager.getConnection("jdbc:mysql://localhost/cems?serverTimezone=IST", "root",
					"Aa123456");
			System.out.println(con);
			System.out.println("SQL connection succeed");
		} catch (SQLException ex) {/* handle any errors */
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	/**
	 * 
	 * @return instance of the database connector
	 */
	public static JDBCSingleton getInstance() {
		if (jdbc == null)
			jdbc = new JDBCSingleton();
		return jdbc;
	}

	/**
	 * send to database the exam id and new duration to update
	 * 
	 * @param id     - Exam ID that will have its duration updated
	 * @param newdur New duration for given exam ID
	 * @return true if successfuly updated,false when the update didn't complate
	 */
	public boolean updateQuery(String id, String newdur) { // PROTOTYPE
		int duration = Integer.parseInt(newdur);
		Statement stmt;

		try {
			stmt = con.createStatement();
			temp = stmt.executeUpdate("update test set Duration='" + duration + "' where ExamID='" + id + "';");
			stmt.close();
		} catch (SQLException e) {
			// e.printStackTrace();
			return false; /* update database unsuccessful */
		}
		if (temp == 0)
			return false;
		return true;
	}

	/**
	 * get all the data form the table in our mySql
	 * 
	 * @return resultset from database (includes Id subject course duration score)
	 */
	public String selectQueryToString() { // PROTOTYPE
		Statement stmt;
		ResultSet rs;
		String id, sub, cour, scores, result = "";

		int dur;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from test");
			while (rs.next()) {
				// get values from database
				id = rs.getString(1);
				sub = rs.getString(2);
				cour = rs.getString(3);
				dur = rs.getInt(4);
				scores = rs.getString(5);
				result += id + "-" + sub + "-" + cour + "-" + dur + "-" + scores + "-";
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * returns type of user (student/principal/teacher) returns null if user-name
	 * and password combination does not exist in database
	 * 
	 * @param username
	 * @param password
	 * @return type of user
	 */
	public String getUserType(String username, String password) {
		String type = null, sql = "select person.Affiliation from person where person.Username='" + username
				+ "' and person.Password='" + password + "'";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			type = rs.getString(1);
			stmt.close();
			rs.close();

		} catch (SQLException e) {
			return null; /* username & password combination does not exist in DB */
		}
		if (type.equals(""))
			return null;
		return type;
	}

	/**
	 * adds new exam to exams table in database
	 * 
	 * @param ex new exam to add to database
	 * @return indication if update succeeded
	 */
	public boolean updateExamInDB(Exam ex) {
		String[] examIDarray;
		boolean bankExists = false;
		String sql = "insert into exam (ExamID, Duration, Comments4Teacher, Comments4Students, "
				+ "TeacherID, ExamType, ExecutionCode, ExamQuestionIDs, ExamQuestionScores, isOngoing) " + "values ('"
				+ ex.getExamID() + "', " + ex.getDuration() + ", '" + ex.getTeacherComment().getFreeText() + "', '"
				+ ex.getStudentComment().getFreeText() + "', '" + ex.getAuthorID() + "', " + ex.getExamType() + ", '"
				+ ex.getExecutionCode() + "', '" + ex.getQuestionIDsToString() + "', '" + ex.getScoresToString()
				+ "', 0);";
		if (!runUpdateQuery(sql))
			return false;
		sql = "select person.ExamBanksID from person where person.ID='" + ex.getAuthorID() + "'";
		String res = jdbc.getStringQuery(sql);
		if (res != null) // see if exam bank already exists for this user
		{
			if (res.length() > 2) // res = 01,02
			{
				examIDarray = res.split(",");
				for (int i = 0; i < examIDarray.length; i++) {
					if (examIDarray[i].equals(ex.getBankID()))
						bankExists = true;
				}
			} else if (res.equals(ex.getBankID()))
				bankExists = true;
		}
		if (res == null) // teacher's first exam bank
			return jdbc.runUpdateQuery("update person set person.ExamBanksID='" + ex.getBankID() + "' where person.ID='"
					+ ex.getAuthorID() + "'");
		if (!bankExists)
			return jdbc.runUpdateQuery("update person set person.ExamBanksID='" + res + "," + ex.getBankID()
					+ "' where person.ID='" + ex.getAuthorID() + "'");
		return true;
	}

	/**
	 * returns the index of the exam under given course & bank
	 * 
	 * @param bankID   exam bank ID number
	 * @param courseID course ID number
	 * @return exam number
	 */
	public String getExamNumber(String bankID, String courseID) {
		String sql = "select count(ExamID) from exam where exam.ExamID like '" + bankID + courseID + "%'";
		String sum, result;
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql); /* query should return only one row */
			rs.next();
			sum = rs.getString(1); /* sum = number of exams under same given bankId and course (0203..) */
			stmt.close();
			rs.close();

		} catch (SQLException e) {
			return null;
		}
		if (sum.length() > 2) /* sum must be 2 digits long or less */
			return null; /* too many exams under given bankID and courseID */

		if (sum.length() == 1) /* if index is less than 10, add leading zero (e.g. 09) */
			result = "0" + sum;
		else
			result = sum;
		if (result.equals(""))
			return null;
		return result; /* exam number */
	}

	/**
	 * returns the index of the question under given bank ID
	 * 
	 * @param bank question bank ID number
	 * @return index of question
	 */
	public String getQuestionNumber(String bank) {
		String sql = "select count(QuestionID) from question where question.QuestionID like '" + bank + "%'";
		String sum;
		String result;
		int temp;
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql); /* query should return 1 result */
			rs.next();
			sum = rs.getString(1); /* sum = number of questions under same given bankId */
			stmt.close();
			rs.close();

		} catch (SQLException e) {
			return null;
		}

		if (sum.length() > 3)
			return null; /* too many questions under bankId */

		temp = Integer.parseInt(sum);

		if (temp < 100 && temp > 9) /* if index is 2 digits, add leading zero (e.g. 099) */
			result = "0" + sum;
		else if (temp < 10) /* if index is 1 digit long, add 2 leading zeros (e.g. 009) */
			result = "00" + sum;
		else /* if index is 3 digits long (e.g. 999) */
			result = sum;
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * get all courses that belong to an existing exam bank of given teacher
	 * 
	 * @param username
	 * @param password
	 * @return course names associated to teacher
	 */
	public String getTeacherCourses(String username, String password) {
		Statement stmt;
		ResultSet rs;
		String CourseName, result = "";

		/* teacher's exam bank IDs */
		String msg = "select person.ExamBanksID from person where person.Username='" + username
				+ "' and person.Password='" + password + "'";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(msg);

			rs.next();
			// get values from database
			CourseName = rs.getString(1);

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		String decoded[] = CourseName.split(",");

		String sql = "select course.CName from course, subject where course.CourseCode=subject.id and (subject.id=";
		for (int i = 0; i < decoded.length; i++) {
			sql += "'" + decoded[i] + "' ";
			if (i < decoded.length - 1)
				sql += "or subject.id=";
		}
		sql += ")";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				// get values from database
				CourseName = rs.getString(1);
				result += CourseName + "_";
			}

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * returns all courses related to subject (teacher's exam bank)
	 * 
	 * @param bankname teacher's exam bank
	 * @return all courses under teacher's exam bank
	 */
	public String getTeacherCoursesUnderExamBank(String bankname) {
		String bankId = getBankID(bankname);
		if (bankId == null)
			return null;
		String sql = "select course.CName from course where course.SubjectCode='" + bankId + "'";
		String CourseName, result = "";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				// get values from database
				CourseName = rs.getString(1);
				result += CourseName + "_";
			}

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * returns all the existing exam banks names under given teacher (user-name +
	 * password)
	 * 
	 * @param username
	 * @param password
	 * @return all exam bank names of given teacher
	 */
	public String getExisitingExamBanksUnderGivenTeacher(String username, String password) {
		Statement stmt;
		ResultSet rs;
		String examBankIds, result = "", examBankName;
		String bankIDarray[];
		String sql = "select person.ExamBanksID from person " + "where person.Username='" + username
				+ "' and person.Password='" + password + "'";

		examBankIds = jdbc.getStringQuery(sql);

		if (examBankIds == null)
			return null;
		if (examBankIds.length() > 2) { // exam bank ids = 01,02,..
			bankIDarray = examBankIds.split(",");
			sql = "select subject.name from subject where subject.id=";
			for (int i = 0; i < bankIDarray.length; i++) {
				sql += "'" + bankIDarray[i] + "' ";
				if (i < bankIDarray.length - 1)
					sql += "or subject.id=";
			}
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(sql);

				while (rs.next()) {
					// get values from database
					examBankName = rs.getString(1);
					result += examBankName + "_";
				}

				stmt.close();
				rs.close();
			} catch (SQLException e) {
				return null;
			}
			if (result.equals("") || result == null)
				return null;
		} else // exam bank id=01
			return jdbc.getBankName(examBankIds);
		return result;
	}

	/**
	 * adds new question to question table in database
	 * 
	 * @param q new question to add to database
	 * @return indication if update succeeded
	 */
	public boolean updateQuestionInDB(Question q) {
		String courseIds = "";
		String[] questionIDs;
		boolean bankExists = false;
		String banksforDB = "";
		for (int i = 0; i < q.getCourseIDs().size(); i++) {
			courseIds += q.getCourseIDs().get(i).getCourseCode();
			if (i < q.getCourseIDs().size() - 1)
				courseIds += ",";
		}
		String sql = "insert into question (AuthorID, QuestionID, Question, Answer1, Answer2, Answer3, Answer4, RightAnswer, BankID, QNum, CourseIDs) values ('"
				+ q.getAuthorID() + "', '" + q.getQuestionID() + "', '" + q.getQuestionText() + "', '"
				+ q.getAnswer1().getAnswerText() + "', '" + q.getAnswer2().getAnswerText() + "', '"
				+ q.getAnswer3().getAnswerText() + "', '" + q.getAnswer4().getAnswerText() + "', " + q.getCorrectAns()
				+ ", '" + q.getqBankID() + "', '" + q.getqNum() + "', '" + courseIds + "');";
		if (!runUpdateQuery(sql))
			return false;
		sql = "select person.QuestionBanksID from person where person.ID='" + q.getAuthorID() + "'";
		String res = jdbc.getStringQuery(sql);
		if (res != null) { // see if bank exists for this user
			if (res.length() > 2) // res=01,02
			{
				questionIDs = res.split(",");
				for (int i = 0; i < questionIDs.length; i++) {
					if (questionIDs[i].equals(q.getqBankID()))
						bankExists = true;
				}
			} else if (res.equals(q.getqBankID())) // only 1 bank & it's the same bank
				bankExists = true;
		}
		if (res == null) // teacher's first question bank
			return jdbc.runUpdateQuery("update person set person.QuestionBanksID='" + q.getqBankID()
					+ "' where person.ID='" + q.getAuthorID() + "'");
		if (!bankExists)
			return jdbc.runUpdateQuery("update person set person.QuestionBanksID='" + res + "," + q.getqBankID()
					+ "' where person.ID='" + q.getAuthorID() + "'");
		return true;
	}

	/**
	 * @param bank   question bank (=exam bank)
	 * @param course questions under course id
	 * @return all questions under bankID and course duo
	 */
	public String getQuestionsUnderBankAndCourseToString(String bank, String course) {
		String bankId = getBankID(bank);
		String courseId = getCourseID(course);
		String sql = "select question.QuestionID, question.Question, question.Answer1,"
				+ " question.Answer2, question.Answer3, question.Answer4," + " question.RightAnswer, question.CourseIDs"
				+ " from question where question.BankID='" + bankId + "'";
		String courseIds[], temp;
		int addToRes = 0;
		Statement stmt;
		ResultSet rs;

		String qID, qText, ans1, ans2, ans3, ans4, result = "";
		int rightAns;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				addToRes = 0;
				// get values from database
				qID = rs.getString(1);
				qText = rs.getString(2);
				qText = qText.replaceAll("_", "1a6gf");
				ans1 = rs.getString(3);
				ans1 = ans1.replaceAll("_", "1a6gf");
				ans2 = rs.getString(4);
				ans2 = ans2.replaceAll("_", "1a6gf");
				ans3 = rs.getString(5);
				ans3 = ans3.replaceAll("_", "1a6gf");
				ans4 = rs.getString(6);
				ans4 = ans4.replaceAll("_", "1a6gf");
				rightAns = rs.getInt(7);
				temp = rs.getString(8); /* temp = question's courses */
				if (temp != null) {
					if (temp.length() > 2) {
						courseIds = temp.split(","); /* courseIds[i]=02 */
						for (int i = 0; i < courseIds.length; i++) {
							if (courseIds[i].equals(courseId))
								addToRes = 1;
						}
					}
					if (temp.length() == 2) {
						if (temp.equals(courseId))
							addToRes = 1;
					}
					if (addToRes == 1)
						result += qID + "_" + qText + "_" + ans1 + " _" + ans2 + " _" + ans3 + " _" + ans4 + " _"
								+ rightAns + "_";
				}
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null; /* select from DB unsuccessful */
		}

		if (result == "")
			return null;

		return result;
	}

	/**
	 * returns course ID of given course name
	 * 
	 * @param course course name
	 * @return ID of course
	 */
	public String getCourseID(String course) {
		String sql = "select course.CourseCode from course where course.CName ='" + course + "'";

		String result = "";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			result = rs.getString(1);
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * returns all question banks under given teacher username and password
	 * 
	 * @param username
	 * @param password
	 * @return all question banks under given teacher
	 */
	public String getExisitingQuestionBanksUnderGivenTeacher(String username, String password) {
		String teacherId = getUserID(username, password);
		Statement stmt;
		ResultSet rs;
		String QuestionBankName, result = "";

		String sql = "select person.QuestionBanksID from person where person.ID='" + teacherId + "'";

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			rs.next();
			QuestionBankName = rs.getString(1);

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (QuestionBankName == null)
			return null;

		String decoded[];
		if (QuestionBankName.length() > 2) { // more than 1 bank
			decoded = QuestionBankName.split(",");

			sql = "select subject.name from subject where subject.id=";
			for (int i = 0; i < decoded.length; i++) {
				sql += "'" + decoded[i] + "' ";
				if (i < decoded.length - 1)
					sql += "or subject.id=";
			}
		} else
			sql = "select subject.name from subject where subject.id='" + QuestionBankName + "'"; // only 1 bank
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				// get values from database
				QuestionBankName = rs.getString(1);
				result += QuestionBankName + "_";
			}

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * returns bank ID given the bank's name
	 * 
	 * @param bankName bank name
	 * @return bank ID
	 */
	public String getBankID(String bankName) {
		String result = "", sql = "select subject.id from subject where subject.name='" + bankName + "'";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			result = rs.getString(1);
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * returns user's ID of given user-name and password
	 * 
	 * @param username
	 * @param password
	 * @return user's ID
	 */
	public String getUserID(String username, String password) {
		String sql = "select person.ID from person where person.Username='" + username + "' and person.Password='"
				+ password + "'";
		return jdbc.getStringQuery(sql);
	}

	/**
	 * returns user's full name of given user-name and password
	 * 
	 * @param username
	 * @param password
	 * @return user's full name
	 */
	public String getUserFullName(String username, String password) {
		String sql = "select concat(person.Name, ' ', person.Surname) from person where person.Username = '" + username
				+ "'" + " and person.Password = '" + password + "'";
		return jdbc.getStringQuery(sql);
	}

	/**
	 * returns all exams made by given teacher
	 * 
	 * @param authorID teacher ID
	 * @return all exams made by this teacher
	 */
	public String getTeacherExams(String authorID) {
		String sql = "select exam.ExamID, exam.Duration, exam.LockStatus, exam.isOngoing, exam.newDur, exam.newDurOK from exam where exam.TeacherID='"
				+ authorID + "'";

		int lockStatus, ongoing, newdur, newdurok;
		String result = "";
		Statement stmt;
		ResultSet rs;
		ArrayList<Exam> exams = new ArrayList<>();

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				// get values from database
				Exam ex = new Exam();
				ex.setExamID(rs.getString(1));
				ex.setDuration(rs.getInt(2));
				lockStatus = rs.getInt(3);
				ongoing = rs.getInt(4);
				newdur = rs.getInt(5);
				newdurok = rs.getInt(6);
				if (newdurok == 0)
					newdur = ex.getDuration();
				if (ongoing == 0)
					ex.setOngoingString("NO");
				else
					ex.setOngoingString("YES");
				if (lockStatus == 0)
					ex.setLockStatusString("Unlocked");
				else
					ex.setLockStatusString("Locked");
				ex.setSubjectName(this.getBankName(ex.getExamID().substring(0, 2)));
				ex.setCourseName(this.getCourseName(ex.getExamID().substring(2, 4)));

				result += ex.getExamID() + "_" + ex.getSubjectName() + "_" + ex.getDuration() + "_" + newdur + "_"
						+ ex.getCourseName() + "_" + ex.getLockStatusString() + "_" + ex.getOngoingString() + "_";
			}

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * returns subject name (bank name) given bank's ID
	 * 
	 * @param bankID bank's ID number
	 * @return bank's name
	 */
	public String getBankName(String bankID) {
		String sql = "select subject.name from subject where subject.id='" + bankID + "'";
		String result = "";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			result = rs.getString(1);
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * sets user's status in database: 1-online, 0-offline
	 * 
	 * @param username
	 * @param password
	 * @param status   sets user's status in database
	 * @return true on successful update, false if unsuccessful
	 */
	public boolean setUserStatus(String username, String password, int status) {
		String sql = "update person set person.Status=" + status + " where person.Username='" + username
				+ "' and person.Password='" + password + "'";
		Statement stmt;

		return runUpdateQuery(sql);
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @return user status in the cems system (0-offline, 1-online)
	 */
	public int getUserStatus(String username, String password) {
		String sql = "select person.Status from person where person.Username='" + username + "' and person.Password='"
				+ password + "'";
		int result;
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			result = rs.getInt(1);
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return -1;
		}
		return result;
	}

	/**
	 * insert offline exam in database
	 * 
	 * @param ex exam
	 */
	public boolean updateExamFileInDB(Exam ex) {
		String[] examIDarray;
		boolean bankExists = false;
		String sql = "insert into exam (ExamID, Duration, "
				+ "TeacherID, ExamType, ExecutionCode, FileName, isOngoing, LockStatus) " + "values ('" + ex.getExamID()
				+ "', " + ex.getDuration() + ", '" + ex.getAuthorID() + "', " + ex.getExamType() + ", '"
				+ ex.getExecutionCode() + "', '" + ex.getFileName() + "', 0, 0);";
		if (!runUpdateQuery(sql))
			return false;

		sql = "select person.ExamBanksID from person where person.ID='" + ex.getAuthorID() + "'";
		String res = jdbc.getStringQuery(sql);
		if (res != null) // see if exam bank already exists for this user
		{
			if (res.length() > 2) // res = 01,02
			{
				examIDarray = res.split(",");
				for (int i = 0; i < examIDarray.length; i++) {
					if (examIDarray[i].equals(ex.getBankID()))
						bankExists = true;
				}
			} else if (res.equals(ex.getBankID()))
				bankExists = true;
		}
		if (res == null) // teacher's first exam bank
		{
			return jdbc.runUpdateQuery("update person set person.ExamBanksID='" + ex.getBankID() + "' where person.ID='"
					+ ex.getAuthorID() + "'");
		}
		if (!bankExists) {
			return jdbc.runUpdateQuery("update person set person.ExamBanksID='" + res + "," + ex.getBankID()
					+ "' where person.ID='" + ex.getAuthorID() + "'");
		}
		return true;

	}

	/**
	 * reset user status in server table
	 */
	public void resetUsersStatus() {
		String sql = "update person set person.Status=0 where person.ID is not null";
		Statement stmt;

		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); /* update database unsuccessful */
		}
		/* update in database successful */
	}

	/**
	 * locks or unlocks exam
	 * 
	 * @param exID
	 * @param lockStatus
	 * @return indication if the update succeeded or not
	 */
	public boolean lockOrUnlockExam(String exID, int lockStatus) {
		String sql = "update exam set exam.LockStatus=" + lockStatus + ", exam.newDurOK=0 where exam.ExamID='" + exID
				+ "'";
		return runUpdateQuery(sql);
	}

	/**
	 * get all information on individuals that have given affiliation
	 * 
	 * @return all person details that have given affiliation (ID, firstname,
	 *         lastname, email, relevant subjects)
	 */
	public String GetPersonDataByAffiliation(String affiliation) {
		String sql = "select person.ID, person.Name, person.Surname, person.Email, person.RelevantSubjects"
				+ " from person where person.Affiliation = '" + affiliation + "'";
		String Id, FirstName, LastName, Email, Subjects, result = "";
		String[] subArray = null;
		boolean moreThan1 = false;
		Statement stmt;
		ResultSet rs;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				Id = rs.getString(1); /* person id */
				FirstName = rs.getString(2); /* person first name */
				LastName = rs.getString(3); /* person last name */
				Email = rs.getString(4); /* person email */
				result += Id + "_" + FirstName + "_" + LastName + "_" + Email + "_";
				if (affiliation.equals("Teacher")) {
					Subjects = rs.getString(5); /* person relevant subjects codes (01,02,...) */
					if (Subjects != null) {
						if (Subjects.length() > 2) {
							subArray = Subjects.split(",");
							moreThan1 = true;
						}

						if (moreThan1) {
							for (int i = 0; i < subArray.length; i++) {
								result += jdbc.getBankName(subArray[i]);
								if (i < subArray.length - 1)
									result += ",";
							}
						} else
							result += jdbc.getBankName(Subjects);
					}
					result += "_";

				}
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * get all courses info for principal (view data base)
	 * 
	 * @return courses id, name, subject
	 */
	public String GetCoursesData() {
		String sql = "select course.SubjectCode, course.CName, course.CourseCode from course";

		String SubjectName, Name, CourseCode, result = "";
		Statement stmt;
		ResultSet rs;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				SubjectName = jdbc.getBankName(rs.getString(1)); /* subject name */
				Name = rs.getString(2); /* course name */
				CourseCode = rs.getString(3); /* course code */
				result += SubjectName + "_" + Name + "_" + CourseCode + "_";
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * get full question given question ID (question text, answers, right answer)
	 * 
	 * @param qId question's ID number
	 * @return all question parameters (test, answer2, answer2, answer3, answer4,
	 *         Right Answer, subject id, course ids)
	 */
	public String getQuestion(String qId) { /* getQuestion_qId */
		String sql = "select question.QuestionID, question.Question, question.Answer1, question.Answer2, question.Answer3, question.Answer4, question.RightAnswer, question.BankID, question.CourseIDs from question where question.QuestionID='"
				+ qId + "'";
		Question q = new Question();
		String result = "";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			String quesID = rs.getString(1);
			String qText = rs.getString(2);
			qText = qText.replaceAll("_", "1a6gf");
			String ans1 = rs.getString(3);
			ans1 = ans1.replaceAll("_", "1a6gf");
			String ans2 = rs.getString(4);
			ans2 = ans2.replaceAll("_", "1a6gf");
			String ans3 = rs.getString(5);
			ans3 = ans3.replaceAll("_", "1a6gf");
			String ans4 = rs.getString(6);
			ans4 = ans4.replaceAll("_", "1a6gf");
			String rightAns = rs.getString(7);
			String bID = rs.getString(8); /* bank id 01 */
			String cIDs = rs.getString(9); /* course ids 02,03 */
			result = quesID + "_" + qText + "_" + ans1 + "_" + ans2 + "_" + ans3 + "_" + ans4 + "_" + rightAns + "_";
			String bankName = jdbc.getBankName(bID);
			result += bankName;
			String courses[];
			if (cIDs != null) {
				courses = cIDs.split(",");
				for (int i = 0; i < courses.length; i++)
					result += "_" + jdbc.getCourseName(courses[i]);
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (result.equals(""))
			return null;
		return result;

	}

	/**
	 * get Course Name by it's code
	 * 
	 * @param cCode course code
	 * @return course name
	 */
	public String getCourseName(String cCode) {
		String sql = "select course.CName from course where course.CourseCode='" + cCode + "'";
		String result = "";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			result = rs.getString(1);
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * edit an existing question in the data base
	 * 
	 * @param q question
	 * @return indication if the update succeeded or not
	 */
	public boolean editQuestionInDB(Question q) {
		String courseIds = "";

		for (int i = 0; i < q.getCourseIDs().size(); i++) {
			courseIds += q.getCourseIDs().get(i).getCourseCode();
			if (i < q.getCourseIDs().size() - 1)
				courseIds += ",";
		}

		String sql = "update question set question.Question = '" + q.getQuestionText() + "', " + "question.Answer1 = '"
				+ q.getAnswer1().getAnswerText() + "', " + "question.Answer2 = '" + q.getAnswer2().getAnswerText()
				+ "', " + "question.Answer3 = '" + q.getAnswer3().getAnswerText() + "', " + "question.Answer4 = '"
				+ q.getAnswer4().getAnswerText() + "', " + "question.RightAnswer = '" + q.getCorrectAns() + "', "
				+ "question.QNum = '" + q.getqNum() + "', " + "question.CourseIDs = '" + courseIds + "' "
				+ "where question.QuestionID ='" + q.getQuestionID() + "'";

		return runUpdateQuery(sql);
	}

	/**
	 * gets exam details to update in database
	 * 
	 * @param ex exam to update in database
	 * @return true if update succeeded, false if not
	 */
	public boolean editExamInDB(Exam ex) {
		String sql = "update exam set exam.ExamID='" + ex.getNewExamID() + "', exam.Duration='" + ex.getDuration()
				+ "', exam.Comments4Teacher='" + ex.getTeacherComment().getFreeText() + "', exam.Comments4Students='"
				+ ex.getStudentComment().getFreeText() + "', exam.ExecutionCode='" + ex.getExecutionCode()
				+ "', exam.ExamQuestionIDs='" + ex.getQuestionIDsToString() + "', exam.ExamQuestionScores='"
				+ ex.getScoresToString() + "' where exam.ExamID='" + ex.getExamID() + "'";
		Statement stmt;

		return runUpdateQuery(sql);
	}

	/**
	 * search for all exams under bankID + courseID & delete them from the database
	 * 
	 * @param exBankID exam bank ID number
	 * @param courseID course ID number
	 * @return indication if the deletion from database succeeded
	 */
	public boolean deleteExamBank(String exBankID, String courseID) { // bankID = 01, examID = 010201
		ArrayList<Teacher> teachers = new ArrayList<>();
		Statement stmt;
		ResultSet rs;
		String personID, personExamBanks;

		String sql = "delete from exam where exam.ExamID like '" + exBankID + courseID
				+ "%' and exam.ExamType=1"; /* delete exams from exam table */
		if (!runUpdateQuery(sql))
			return false;

		sql = "select count(exam.ExamID) from exam where exam.ExamID like '" + exBankID + "%'";
		int res = jdbc.getIntQuery(sql);
		if (res == 0) /*
						 * if there are no more exams under this bank, we need to delete the exam bank
						 */
		{
			/* get everyone with this bank */
			sql = "select person.ID, person.ExamBanksID from person where person.ExamBanksID like '%" + exBankID + "%'";
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					personID = rs.getString(1);
					personExamBanks = rs.getString(2);
					Teacher t = new Teacher();
					t.setId(personID);
					ArrayList<String> examBanksArrayList = new ArrayList<>();
					String[] ExamBankArray;
					String DBexambanks = "";
					if (personExamBanks.length() > 2) {
						ExamBankArray = personExamBanks.split(",");
						for (int i = 0; i < ExamBankArray.length; i++) {
							if (!ExamBankArray[i].equals(exBankID)) {
								examBanksArrayList.add(ExamBankArray[i]);
								DBexambanks += ExamBankArray[i];
								if (i < ExamBankArray.length - 1 && !ExamBankArray[i + 1].equals(exBankID))
									DBexambanks += ",";
							}
						}
						t.setDBexamBanks(DBexambanks);
						t.setExamBanks(examBanksArrayList);
					} else {
						/* teacher has only 1 bank (the one we need to delete) */
						t.setExamBanks(null);
						t.setDBexamBanks("");
					}
					teachers.add(t);
				}
				stmt.close();
				rs.close();

			} catch (SQLException e) {
				return false;
			}
			for (int i = 0; i < teachers.size(); i++) { /* update person table */
				if (!teachers.get(i).getDBexamBanks().equals(""))
					sql = "update person set person.ExamBanksID='" + teachers.get(i).getDBexamBanks()
							+ "' where person.ID='" + teachers.get(i).getId() + "'";
				else
					sql = "update person set person.ExamBanksID=NULL where person.ID='" + teachers.get(i).getId() + "'";
				if (!jdbc.runUpdateQuery(sql)) /* delete exam bank from person table */
					return false;
			}
		}
		return true;
	}

	/**
	 * search for a specific exam & delete it from the database
	 * 
	 * @param ExamID exam ID number
	 * @return indication if the deletion succeeded
	 */
	public boolean deleteExam(String ExamID) {
		ArrayList<Teacher> teachers = new ArrayList<>();
		Statement stmt;
		ResultSet rs;
		String personID, personExamBanks;

		String sql = "select count(exam.ExamID) from exam where exam.ExamID like '" + ExamID.substring(0, 2) + "%'";
		int res = jdbc.getIntQuery(sql);
		if (res == 1) /*
						 * last exam in exam bank - we need to delete the exam bank for this user (bank
						 * cannot be empty)
						 */
		{
			/* get everyone with this bank */
			sql = "select person.ID, person.ExamBanksID from person where person.ExamBanksID like '%"
					+ ExamID.substring(0, 2) + "%'";
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					personID = rs.getString(1);
					personExamBanks = rs.getString(2);
					Teacher t = new Teacher();
					t.setId(personID);
					ArrayList<String> examBanksArrayList = new ArrayList<>();
					String[] ExamBankArray;
					String DBexambanks = "";
					if (personExamBanks.length() > 2) {
						ExamBankArray = personExamBanks.split(",");
						for (int i = 0; i < ExamBankArray.length; i++) {
							if (!ExamBankArray[i].equals(ExamID.substring(0, 2))) {
								examBanksArrayList.add(ExamBankArray[i]);
								DBexambanks += ExamBankArray[i];
								if (i < ExamBankArray.length - 1)
									DBexambanks += ",";
							}
						}
						t.setDBexamBanks(DBexambanks);
						t.setExamBanks(examBanksArrayList);
					} else {
						/* teacher has only 1 bank (the one we need to delete) */
						t.setExamBanks(null);
						t.setDBexamBanks("");
					}
					teachers.add(t);
				}
				stmt.close();
				rs.close();

			} catch (SQLException e) {
				return false;
			}
			for (int i = 0; i < teachers.size(); i++) { /* update person table */
				if (!teachers.get(i).getDBexamBanks().equals(""))
					sql = "update person set person.ExamBanksID='" + teachers.get(i).getDBexamBanks()
							+ "' where person.ID='" + teachers.get(i).getId() + "'";
				else
					sql = "update person set person.ExamBanksID=NULL where person.ID='" + teachers.get(i).getId() + "'";
				if (!jdbc.runUpdateQuery(sql)) /* delete exam bank from person table */
					return false;
			}
		}
		sql = "delete from exam where exam.ExamID = '" + ExamID + "'"; /* delete exam from exam table */

		return runUpdateQuery(sql);
	}

	/**
	 * deletes all questions under bank ID from the database (only if they don't
	 * belong to any exam)
	 * 
	 * @param qbankID question bank ID
	 * @return true if deletion succeeded, false if deletion failed
	 */
	public boolean deleteQuestionBank(String qbankID) {
		String sql;
		int res;
		Statement stmt;
		ResultSet rs;

		/* check if questions belong to exams - if yes, do not delete */
		sql = "select count(ExamID) from exam where exam.ExamID like '" + qbankID + "%'";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			res = rs.getInt(1);
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return false;
		}
		if (res > 0)
			return false; /* make user delete exam bank before deleting question bank */

		/*
		 * if we got here, there are no exams with these questions - we can delete them
		 * from the database.
		 */
		sql = "delete from question where question.BankID='" + qbankID + "'"; /* delete questions from question table */
		return runUpdateQuery(sql);
	}

	/**
	 * delete all questions from question bank that belong to given course
	 * 
	 * @param qbankID  question bank ID number
	 * @param courseID course ID number
	 * @return indication if delete from database succeeded or not
	 */
	public boolean deleteQuestionsUnderQuestionBankAndCourse(String qbankID, String courseID) {
		String sql;
		int res;
		Statement stmt;
		ResultSet rs;
		String personID, personQBanks;
		ArrayList<Teacher> teachers = new ArrayList<>();

		/* check if questions belong to any exams - if yes, do not delete */
		sql = "select count(ExamID) from exam where exam.ExamID like '" + qbankID + "%'";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			res = rs.getInt(1);
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return false;
		}
		if (res > 0)
			return false; /*
							 * make user delete all exams under course and exam bank before deleting all
							 * questions under course and question bank
							 */

		/*
		 * if we got here, there are no exams with these questions - we can delete them
		 * from the database.
		 */
		sql = "delete from question where question.BankID='" + qbankID + "' and question.CourseIDs like '%" + courseID
				+ "%'";
		if (!runUpdateQuery(sql))
			return false;

		/**
		 * check to see if we need to delete the entire question bank
		 */
		sql = "select count(question.QuestionID) from question where question.QuestionID like '" + qbankID + "%'";
		res = jdbc.getIntQuery(sql);
		if (res == 0) /* we need to delete the banks */
		{/**
			 * delete question bank from all users
			 */
			sql = "select person.ID, person.QuestionBanksID from person where person.QuestionBanksID like '%" + qbankID
					+ "%'";
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					personID = rs.getString(1);
					personQBanks = rs.getString(2);
					Teacher t = new Teacher();
					t.setId(personID);
					ArrayList<String> qBanksArrayList = new ArrayList<>();
					String[] QBanks;
					String dbqBanks = "";
					if (personQBanks.length() > 2) {
						QBanks = personQBanks.split(",");
						for (int i = 0; i < QBanks.length; i++) {
							if (!QBanks[i].equals(qbankID)) {
								qBanksArrayList.add(QBanks[i]);
								dbqBanks += QBanks[i];
								if (i < QBanks.length - 1)
									dbqBanks += ",";
							}
						}
						t.setDBqbanks(dbqBanks);
						t.setQuestionBanks(qBanksArrayList);
					} else {
						/* teacher has only 1 bank (the one we need to delete) */
						t.setQuestionBanks(null);
						t.setDBqbanks("");
					}
					teachers.add(t);
				}
				stmt.close();
				rs.close();

			} catch (SQLException e) {
				return false;
			}
			for (int i = 0; i < teachers.size(); i++) { /* update person table */
				if (!teachers.get(i).getDBqbanks().equals(""))
					sql = "update person set person.QuestionBanksID='" + teachers.get(i).getDBqbanks()
							+ "' where person.ID='" + teachers.get(i).getId() + "'";
				else
					sql = "update person set person.QuestionBanksID=NULL where person.ID='" + teachers.get(i).getId()
							+ "'";

				jdbc.runUpdateQuery(sql);
			}
		}
		return true;
	}

	/**
	 * run update query
	 * 
	 * @param sql query
	 * @return indication if update in database succeeded
	 */
	private boolean runUpdateQuery(String sql) {
		Statement stmt;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			return false; /* update database unsuccessful */
		}

		/* update in database successful */
		return true;
	}

	/**
	 * deletes specific question from the database (only if it's don't belong to any
	 * exam)
	 * 
	 * @param QuestionID question ID number
	 * @return true if deletion succeeded, false if deletion failed
	 */
	public boolean deleteQuestion(String QuestionID) {
		String sql;
		int res;
		String personID, personQBanks;
		ArrayList<Teacher> teachers = new ArrayList<>();

		Statement stmt;
		ResultSet rs;

		/* check if the question belong to exams - if yes, do not delete */
		sql = "select count(*) from exam where exam.ExamQuestionIDs like '%" + QuestionID + "%'";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			res = rs.getInt(1);
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return false;
		}
		if (res > 0)
			return false; /* make user delete exam before deleting question */

		/*
		 * question does not belong to any exam. check if this is the last question in
		 * the question bank
		 */
		sql = "select count(question.BankID) from question where question.BankID='" + QuestionID.substring(0, 2) + "'";
		res = jdbc.getIntQuery(sql);
		if (res == 1) /*
						 * last question in bank - we need to delete the question bank (bank cannot be
						 * empty)
						 */
		{
			/* delete this question bank */
			sql = "select person.ID, person.QuestionBanksID from person where person.QuestionBanksID like '%"
					+ QuestionID.substring(0, 2) + "%'";
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					personID = rs.getString(1);
					personQBanks = rs.getString(2);
					Teacher t = new Teacher();
					t.setId(personID);
					ArrayList<String> qBanksArrayList = new ArrayList<>();
					String[] QBanks;
					String dbqBanks = "";
					if (personQBanks.length() > 2) {
						QBanks = personQBanks.split(",");
						for (int i = 0; i < QBanks.length; i++) {
							if (!QBanks[i].equals(QuestionID.substring(0, 2))) {
								qBanksArrayList.add(QBanks[i]);
								dbqBanks += QBanks[i];
								if (i < QBanks.length - 1)
									dbqBanks += ",";
							}
						}
						t.setDBqbanks(dbqBanks);
						t.setQuestionBanks(qBanksArrayList);
					} else {
						/* teacher has only 1 bank (the one we need to delete) */
						t.setQuestionBanks(null);
						t.setDBqbanks("");
					}
					teachers.add(t);
				}
				stmt.close();
				rs.close();

			} catch (SQLException e) {
				return false;
			}
			for (int i = 0; i < teachers.size(); i++) { /* update person table */
				if (!teachers.get(i).getDBqbanks().equals(""))
					sql = "update person set person.QuestionBanksID='" + teachers.get(i).getDBqbanks()
							+ "' where person.ID='" + teachers.get(i).getId() + "'";
				else
					sql = "update person set person.QuestionBanksID=NULL where person.ID='" + teachers.get(i).getId()
							+ "'";

				jdbc.runUpdateQuery(sql);
			}
		}
		/*
		 * if we got here, there are no exams with that question - we can delete it from
		 * the database.
		 */
		sql = "delete from question where question.QuestionID='" + QuestionID
				+ "'"; /* delete the question from question table */
		return runUpdateQuery(sql);
	}

	/**
	 * lets us know if there is an ongoing exam inside this bank
	 * 
	 * @param bankId bank ID number
	 * @return 1 if there is an ongoing exam, 0 if not
	 */
	public int isThereExamOngoingInBank(String bankId) {
		String sql = "select exam.isOngoing from exam where exam.ExamID like '" + bankId + "%'";
		int result;
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result = rs.getInt(1);
				if (result == 1)
					return 1;
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return -1;
		}
		return 0;
	}

	/**
	 * lets us know if the exam is ongoing or not
	 * 
	 * @param examID exam ID number
	 * @return 1 if exam is ongoing, 0 if not
	 */
	public int isExamOngoing(String examID) {
		String sql = "select exam.isOngoing from exam where exam.ExamID='" + examID + "'";
		int result;
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			result = rs.getInt(1);
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return -1;
		}
		return result;
	}

	/**
	 * update exam duration
	 * 
	 * @param examID exam ID number
	 * @param newDur new duration in minutes
	 * @return indication if the update succeeded or not
	 */
	public boolean updateExamDuration(String examID, String newDur) {
		String sql = "update exam set exam.Duration=" + newDur + " where exam.ExamID='" + examID + "'";
		return runUpdateQuery(sql);
	}

	/**
	 * enters in database the temporary exam duration change request for an ongoing
	 * exam
	 * 
	 * @param examID    the exam to change its duration (only for current ongoing
	 *                  exam)
	 * @param newDur    the new duration
	 * @param rationale teacher's rationale as to why the duration change is made
	 * @return indication if the update succeeded or not
	 */
	public boolean enterChangeDurRequest(String examID, String newDur, String rationale) {
		String sql = "update exam set exam.newDur=" + newDur + ", exam.durChangeRationale='" + rationale
				+ "', exam.newDurOK=0 where exam.ExamID='" + examID + "'";
		return runUpdateQuery(sql);
	}

	/**
	 * returns report of exam
	 * 
	 * @param examID exam ID number
	 * @return exam histogram, average, median
	 */
	public String getExamReport(String examID) {
		String report = "";
		String sql = "select count(exam_log.ExamID) from exam_log where exam_log.ExamID='" + examID + "'";
		int res = jdbc.getIntQuery(sql);
		if (res == 0)
			return null;
		int avg, median;
		String sql1 = "select count(done_exam.StudentID) from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.Grade between 0 and 9";
		String sql2 = "select count(done_exam.StudentID) from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.Grade between 10 and 19";
		String sql3 = "select count(done_exam.StudentID) from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.Grade between 20 and 29";
		String sql4 = "select count(done_exam.StudentID) from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.Grade between 30 and 39";
		String sql5 = "select count(done_exam.StudentID) from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.Grade between 40 and 49";
		String sql6 = "select count(done_exam.StudentID) from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.Grade between 50 and 59";
		String sql7 = "select count(done_exam.StudentID) from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.Grade between 60 and 69";
		String sql8 = "select count(done_exam.StudentID) from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.Grade between 70 and 79";
		String sql9 = "select count(done_exam.StudentID) from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.Grade between 80 and 89";
		String sql10 = "select count(done_exam.StudentID) from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.Grade between 90 and 100";
		int gradeGroup1, gradeGroup2, gradeGroup3, gradeGroup4, gradeGroup5, gradeGroup6, gradeGroup7, gradeGroup8,
				gradeGroup9, gradeGroup10;
		gradeGroup1 = getIntQuery(sql1);
		if (gradeGroup1 == -1)
			return null;
		gradeGroup2 = getIntQuery(sql2);
		if (gradeGroup2 == -1)
			return null;
		gradeGroup3 = getIntQuery(sql3);
		if (gradeGroup3 == -1)
			return null;
		gradeGroup4 = getIntQuery(sql4);
		if (gradeGroup4 == -1)
			return null;
		gradeGroup5 = getIntQuery(sql5);
		if (gradeGroup5 == -1)
			return null;
		gradeGroup6 = getIntQuery(sql6);
		if (gradeGroup6 == -1)
			return null;
		gradeGroup7 = getIntQuery(sql7);
		if (gradeGroup7 == -1)
			return null;
		gradeGroup8 = getIntQuery(sql8);
		if (gradeGroup8 == -1)
			return null;
		gradeGroup9 = getIntQuery(sql9);
		if (gradeGroup9 == -1)
			return null;
		gradeGroup10 = getIntQuery(sql10);
		if (gradeGroup9 == -1)
			return null;

		avg = calculateAverage(examID);
		if (avg == -1)
			return null;

		median = calculateMedian(examID);
		if (median == -1)
			return null;

		int numStarted = jdbc
				.getIntQuery("select exam_log.numStarted from exam_log where exam_log.ExamID='" + examID + "'");
		int numFinishSuccessful = jdbc.getIntQuery(
				"select exam_log.numFinishSuccessful from exam_log where exam_log.ExamID='" + examID + "'");
		int numFinishUnsuccessful = jdbc.getIntQuery(
				"select exam_log.numFinishUnsuccessful from exam_log where exam_log.ExamID='" + examID + "'");
		report = avg + "_" + median + "_" + gradeGroup1 + "_" + gradeGroup2 + "_" + gradeGroup3 + "_" + gradeGroup4
				+ "_" + gradeGroup5 + "_" + gradeGroup6 + "_" + gradeGroup7 + "_" + gradeGroup8 + "_" + gradeGroup9
				+ "_" + gradeGroup10 + "_" + numStarted + "_" + numFinishSuccessful + "_" + numFinishUnsuccessful;

		return report;
	}

	/**
	 * get exam average grade
	 * 
	 * @param examID exam ID number
	 * @return average
	 */
	public int calculateAverage(String examID) {
		String sql = "select avg(done_exam.Grade) from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.GradeStatus=1";
		return getIntQuery(sql);
	}

	/**
	 * return all grades in sorted array that were approved under given exam
	 * 
	 * @param examID
	 * @return all grades of exam sorted
	 */
	public ArrayList<Integer> getGrades(String examID) { // REFACTOR
		ArrayList<Integer> grades = new ArrayList<>();
		String sql = "select done_exam.Grade from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.GradeStatus=1 order by done_exam.Grade";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next())
				grades.add(rs.getInt(1));
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		return grades;
	}

	/**
	 * get median of given exam
	 * 
	 * @param examID exam ID number
	 * @return median
	 */
	public int calculateMedian(String examID) {
		int median;
		String sql = "select done_exam.Grade from done_exam where done_exam.ExamID='" + examID
				+ "' and done_exam.GradeStatus=1 order by done_exam.Grade";
		ArrayList<Integer> grades = new ArrayList<>();
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next())
				grades.add(rs.getInt(1));
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return 0;
		}
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
	 * get median of given course
	 * 
	 * @param courseID course ID number
	 * @return course median
	 */
	private int calculateCourseMedian(String courseID) {
		int median;
		String sql = "select done_exam.Grade from done_exam where substr(done_exam.ExamID, 3, 2)='" + courseID
				+ "'  and done_exam.GradeStatus=1 order by done_exam.Grade";
		ArrayList<Integer> grades = new ArrayList<>();
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next())
				grades.add(rs.getInt(1));
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return -1;
		}
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
	 * execute query that returns an integer result
	 * 
	 * @param sql query
	 * @return integer result
	 */
	private int getIntQuery(String sql) {
		int result;
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			result = rs.getInt(1);
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return -1;
		}
		return result;
	}

	/**
	 * fetch all exam details from database
	 * 
	 * @param examID exam ID number
	 * @return all exam details
	 */
	public String getExam(String examID) {
		String sql = "select exam.Duration, exam.Comments4Teacher, exam.Comments4Students, exam.ExecutionCode, exam.ExamQuestionIDs, exam.ExamQuestionScores from exam where exam.ExamID='"
				+ examID + "'";
		String result = "";
		Exam ex = new Exam();
		ex.setBankID(examID.substring(0, 2));
		ex.setSubjectName(this.getBankName(ex.getBankID()));
		ex.setCourseID(examID.substring(2, 4));
		ex.setCourseName(this.getCourseName(ex.getCourseID()));
		String[] qArray = null, qScores = null;
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			ex.setDuration(rs.getInt(1));
			String temp = rs.getString(2);
			temp = temp.replaceAll("_", "1a6gf");
			ex.setTeacherComment(new Comment(Comment.Type.ForTeacher, temp));
			temp = rs.getString(3);
			temp = temp.replaceAll("_", "1a6gf");
			ex.setStudentComment(new Comment(Comment.Type.ForStudent, temp));
			ex.setExecutionCode(rs.getString(4));
			String qids = rs.getString(5);
			String qscores = rs.getString(6);
			if (qids.length() > 2) {
				qArray = qids.split(",");
				qScores = qscores.split(",");
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		result = examID + "_" + ex.getSubjectName() + "_" + ex.getCourseName() + "_" + ex.getExecutionCode() + "_"
				+ ex.getDuration() + "_" + ex.getTeacherComment().getFreeText() + "_"
				+ ex.getStudentComment().getFreeText();
		for (int i = 0; i < qArray.length; i++) {
			result += "_" + qArray[i] + "_" + qScores[i];
		}
		return result;
	}

	/**
	 * returns full online exam if the execution code is correct & if the exam is
	 * not locked
	 * 
	 * @param executionCode exam's execution codes
	 * @param studentID     student's ID number
	 * @return full online exam
	 */
	public String getFullOnlineExam(String executionCode, String studentID) { // getFullOnlineExam_executionCode_studentID
		/* check if execution code is good */
		String sql = "select exam.ExamID, exam.LockStatus, concat(Name, ' ', Surname), exam.Comments4Students from exam, person"
				+ " where exam.TeacherID = person.ID" + " and exam.ExecutionCode='" + executionCode + "'";
		Statement stmt;
		ResultSet rs;
		String examID, teacherName, result = "";
		String comment4student;
		int lockstatus;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			examID = rs.getString(1); // get values from database
			lockstatus = rs.getInt(2);
			teacherName = rs.getString(3);
			comment4student = rs.getString(4);
			comment4student = comment4student.replaceAll("_", "1a6gf");
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return "fail";
		}

		if (lockstatus == 1) /* if exam is locked - student cannot do the exam */
			return "exam locked";

		if (examID == null) /* wrong execution code */
			return "bad execution code";

		/*
		 * if we got here everything is ok, we can get the exam's full questions &
		 * possible answers
		 */
		result += examID + "_" + teacherName + "_" + this.getBankName(examID.substring(0, 2)) + "_"
				+ this.getCourseName(examID.substring(2, 4)) + "_" + comment4student;

		String decoded[];
		String fullExam = getExam(examID);
		decoded = fullExam.split("_");
		String[] qIDs = null, qScores = null;
		qIDs = new String[60];
		qScores = new String[60];

		/* decode exam's questions ids and scores */
		for (int i = 7, j = 0; i < decoded.length; i++, j++) {
			qIDs[j] = decoded[i];
			i++;
			qScores[j] = decoded[i];
		}

		/* get full questions */
		ArrayList<Question> examQuestions = new ArrayList<>();
		examQuestions = getQuestionsForExam(qIDs);

		for (int i = 0; i < examQuestions.size(); i++) {
			String text = examQuestions.get(i).getQuestionText();
			text = text.replaceAll("_", "1a6gf");
			String ans1 = examQuestions.get(i).getAnswer1().getAnswerText();
			ans1 = ans1.replaceAll("_", "1a6gf");
			String ans2 = examQuestions.get(i).getAnswer2().getAnswerText();
			ans2 = ans2.replaceAll("_", "1a6gf");
			String ans3 = examQuestions.get(i).getAnswer3().getAnswerText();
			ans3 = ans3.replaceAll("_", "1a6gf");
			String ans4 = examQuestions.get(i).getAnswer4().getAnswerText();
			ans4 = ans4.replaceAll("_", "1a6gf");

			result += "_" + examQuestions.get(i).getQuestionID() + "_" + text + "_" + ans1 + "_" + ans2 + "_" + ans3
					+ "_" + ans4 + "_" + qScores[i];
		}

		return result;
	}

	/**
	 * get full questions text & possible answers
	 * 
	 * @param qIDs array of question ID's
	 * @return full questions with given question ids
	 */
	private ArrayList<Question> getQuestionsForExam(String[] qIDs) {
		ArrayList<Question> questions = new ArrayList<>();
		Statement stmt;
		ResultSet rs;
		String sql = "select question.QuestionID, question.Question, question.Answer1, question.Answer2, question.Answer3, question.Answer4 from question where question.QuestionID in (";

		for (int i = 0; i < qIDs.length; i++) {
			sql += "'" + qIDs[i] + "'";
			if (i < qIDs.length - 1)
				sql += ", ";
		}
		sql += ")";

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Question q = new Question();
				q.setQuestionID(rs.getString(1));
				String temp = rs.getString(2);
				temp = temp.replaceAll("_", "1a6gf");
				q.setQuestionText(temp);
				temp = rs.getString(3);
				temp = temp.replaceAll("_", "1a6gf");
				q.setAnswer1(new Answer(temp));
				temp = rs.getString(4);
				temp = temp.replaceAll("_", "1a6gf");
				q.setAnswer2(new Answer(temp));
				temp = rs.getString(5);
				temp = temp.replaceAll("_", "1a6gf");
				q.setAnswer3(new Answer(temp));
				temp = rs.getString(6);
				temp = temp.replaceAll("_", "1a6gf");
				q.setAnswer4(new Answer(temp));
				questions.add(q);
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		return questions;
	}

	/**
	 * run query that returns a single string result
	 * 
	 * @param sql query
	 * @return string result
	 */
	private String getStringQuery(String sql) {
		Statement stmt;
		ResultSet rs;
		String result = "";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			result = rs.getString(1); // get values from database
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (result == null)
			return null;
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * get all subjects that belong to given teacher
	 * 
	 * @param username teacher's username
	 * @param password teacher's password
	 * @return subjects names associated to teacher
	 */
	public String getAllTeacherSubjects(String username, String password) {
		Statement stmt;
		ResultSet rs;
		String SubjectName, result = "";

		/* teacher's subjects IDs */
		String msg = "select person.RelevantSubjects from person where person.Username='" + username
				+ "' and person.Password='" + password + "'";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(msg);

			rs.next();
			// get values from database
			SubjectName = rs.getString(1);

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (SubjectName.equals(""))
			return null;
		String decoded[] = SubjectName.split(",");

		String sql = "select subject.name from subject where (subject.id=";
		for (int i = 0; i < decoded.length; i++) {
			sql += "'" + decoded[i] + "' ";
			if (i < decoded.length - 1)
				sql += "or subject.id=";
		}
		sql += ")";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				// get values from database
				SubjectName = rs.getString(1);
				result += SubjectName + "_";
			}

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * get all courses under given subject that have at least one question
	 * associated with them
	 * 
	 * @param examBankName exam bank name
	 * @return all courses under given subject (exam bank) that have at least 1
	 *         question associated with them
	 */
	public String getAllCoursesUnderSubjectThatContainQuestions(String examBankName) {
//		getAllCoursesUnderSubjectThatContainQuestions_examBankName
		String bankId = getBankID(examBankName); // Math ==> 01
		ArrayList<String> courses = new ArrayList<>();
		if (bankId == null)
			return null; // no bank ID with given name
		String sql = "select question.CourseIDs from question where question.BankID ='" + bankId + "'";
		String questionCourseIds[], result = "";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				// get values from database
				questionCourseIds = rs.getString(1).split(",");
				for (int i = 0; i < questionCourseIds.length; i++)
					if (!courses.contains(questionCourseIds[i]))
						courses.add(questionCourseIds[i]);

			}
			for (int i = 0; i < courses.size(); i++) {
				result += this.getCourseName(courses.get(i));
				if (i < courses.size() - 1)
					result += "_";
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * approve student grade that was given automatically by the system
	 * 
	 * @param exID           exam ID number
	 * @param sID            student ID number
	 * @param teacherComment optional teacher comment
	 * @return indication if update of grade in database succeeded (yes-true,
	 *         no-false)
	 */
	public boolean approveStudentGrade(String exID, String sID, String teacherComment) {
		String sql = "update done_exam set done_exam.GradeStatus=1, done_exam.TeacherComments='" + teacherComment
				+ "', done_exam.Grade=done_exam.SysGrade where done_exam.ExamID='" + exID
				+ "' and done_exam.StudentID='" + sID + "'";
		boolean ok = runUpdateQuery(sql);
		if (!ok)
			return false;
		int average = jdbc.calculateAverage(exID);
		int median = jdbc.calculateMedian(exID);

		sql = "update exam_log set exam_log.average=" + average + ", exam_log.median=" + median
				+ " where exam_log.ExamID='" + exID + "'";

		return runUpdateQuery(sql); /* update exam's distribution in database */
	}

	/**
	 * get all students pending grades that this teacher needs to approve under exam
	 * ID
	 * 
	 * @param teacherId teacher's ID number
	 * @param examID    exam ID
	 * @return all students with pending grades under exam ID of this teacher
	 */
	public String getTeachersPendingGrades(String teacherId, String examID) {
//		getPendingGrades_teacherId
		String sql = "select d.StudentID, d.ExamID, d.SysGrade, d.Grade, d.copycatSuspecious from cems.done_exam d where d.GradeStatus=0 and d.ExamID='"
				+ examID + "' and d.ExamID in (select exam.ExamID from exam where exam.TeacherID='" + teacherId + "')";
		String id, exId, result = "";
		int sysGrade, grade, suspecious;
		Statement stmt;
		ResultSet rs;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				id = rs.getString(1); /* student id */
				exId = rs.getString(2); /* exam id */
				sysGrade = rs.getInt(3); /* grade generated by the system */
				grade = rs.getInt(4); /* final grade */
				suspecious = rs.getInt(5); /* is student suspected of cheating */
				result += id + "_" + exId + "_" + sysGrade + "_" + grade + "_";
				if (suspecious == 1)
					result += "YES_";
				else
					result += "NO_";
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * get all the student who took particular exam
	 * 
	 * @param exID exam ID number
	 * @return string of student id and grades
	 */
	public String getStudentForDoneExam(String exID) {
		String sql = "SELECT person.ID, done_exam.SysGrade, done_exam.copycatSuspecious FROM done_exam, person where done_exam.StudentID = person.ID"
				+ " and done_exam.ExamID = '" + exID + "'";

		String Student, result = "", susString;
		int grade, suspecious;
		Statement stmt;
		ResultSet rs;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				Student = rs.getString(1); /* student id */
				grade = rs.getInt(2); /* student grade */
				suspecious = rs.getInt(3);
				if (suspecious == 1)
					susString = "YES";
				else
					susString = "NO";
				result += Student + "_" + grade + "_" + susString + "_";
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * get all exams under given teacher's exam bank and course
	 * 
	 * @param BankName   bank name (subject), course name, teacher
	 * @param CourseName course name
	 * @param username   teacher's username
	 * @param password   teacher's password
	 * @return all exams under teacher's exam bank and course
	 */
	public String getExamsUnderTeacherExamBankCourse(String BankName, String CourseName, String username,
			String password) {
		String bankId = getBankID(BankName);
		String courseId = getCourseID(CourseName);
		String teacherId = getUserID(username, password);

		if (bankId == null)
			return null;

		String sql = "select exam.ExamID, exam.ExecutionCode from exam where exam.TeacherID = '" + teacherId + "'"
				+ " and substr(exam.ExamID, 1, 2) = '" + bankId + "'" + " and substr(exam.ExamID, 3, 2) = '" + courseId
				+ "'";

		String examId, code, result = "";
		Statement stmt;
		ResultSet rs;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				// get values from database
				examId = rs.getString(1);
				code = rs.getString(2);
				result += examId + "_" + code + "_";
			}

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * updates student grade in database
	 * 
	 * @param teacherID teacher ID number
	 * @param examId    exam ID number
	 * @param studentId student ID number
	 * @param newGrade  new grade
	 * @param reason    reason for changing the grade
	 * @param comment   optional teacher comment
	 * @return indication if the update was successful or not
	 */
	public boolean changeStudentGrade(String teacherID, String examId, String studentId, int newGrade, String reason,
			String comment) {
		boolean ok;
		int oldGrade = jdbc.getIntQuery("select done_exam.SysGrade from done_exam where done_exam.ExamID='" + examId
				+ "' and done_exam.StudentID='" + studentId + "'");
		String sql = "update done_exam set done_exam.Grade='" + newGrade + "', done_exam.GradeChangeRationale='"
				+ reason + "', done_exam.SysGrade=done_exam.Grade, done_exam.GradeStatus=1 where done_exam.ExamID='"
				+ examId + "' and done_exam.StudentID='" + studentId + "'";
		ok = runUpdateQuery(sql); /* update student's grade in database */
		if (!ok)
			return false;
		if (comment != null) {
			ok = runUpdateQuery("update done_exam set done_exam.TeacherComments='" + comment
					+ "' where done_exam.ExamID='" + examId + "' and done_exam.studentId='" + studentId + "'");
			if (!ok)
				return false;
		}
		/* update exam log */
		if (newGrade <= 9)
			sql = "update exam_log set exam_log.0_9=exam_log.0_9+1 where exam_log.ExamID='" + examId + "'";
		else if (10 <= newGrade && newGrade <= 19)
			sql = "update exam_log set exam_log.10_19=exam_log.10_19+1 where exam_log.ExamID='" + examId + "'";
		else if (20 <= newGrade && newGrade <= 29)
			sql = "update exam_log set exam_log.20_29=exam_log.20_29+1 where exam_log.ExamID='" + examId + "'";
		else if (30 <= newGrade && newGrade <= 39)
			sql = "update exam_log set exam_log.30_39=exam_log.30_39+1 where exam_log.ExamID='" + examId + "'";
		else if (40 <= newGrade && newGrade <= 49)
			sql = "update exam_log set exam_log.40_49=exam_log.40_49+1 where exam_log.ExamID='" + examId + "'";
		else if (50 <= newGrade && newGrade <= 59)
			sql = "update exam_log set exam_log.50_59=exam_log.50_59+1 where exam_log.ExamID='" + examId + "'";
		else if (60 <= newGrade && newGrade <= 69)
			sql = "update exam_log set exam_log.60_69=exam_log.60_69+1 where exam_log.ExamID='" + examId + "'";
		else if (70 <= newGrade && newGrade <= 79)
			sql = "update exam_log set exam_log.70_79=exam_log.70_79+1 where exam_log.ExamID='" + examId + "'";
		else if (80 <= newGrade && newGrade <= 89)
			sql = "update exam_log set exam_log.80_89=exam_log.80_89+1 where exam_log.ExamID='" + examId + "'";
		else if (90 <= newGrade && newGrade <= 100)
			sql = "update exam_log set exam_log.90_100=exam_log.90_100+1 where exam_log.ExamID='" + examId + "'";

		ok = jdbc.runUpdateQuery(sql);
		if (!ok)
			return false;

		/* update exam log */
		if (oldGrade <= 9)
			sql = "update exam_log set exam_log.0_9=exam_log.0_9-1 where exam_log.ExamID='" + examId + "'";
		else if (10 <= oldGrade && oldGrade <= 19)
			sql = "update exam_log set exam_log.10_19=exam_log.10_19-1 where exam_log.ExamID='" + examId + "'";
		else if (20 <= oldGrade && oldGrade <= 29)
			sql = "update exam_log set exam_log.20_29=exam_log.20_29-1 where exam_log.ExamID='" + examId + "'";
		else if (30 <= oldGrade && oldGrade <= 39)
			sql = "update exam_log set exam_log.30_39=exam_log.30_39-1 where exam_log.ExamID='" + examId + "'";
		else if (40 <= oldGrade && oldGrade <= 49)
			sql = "update exam_log set exam_log.40_49=exam_log.40_49-1 where exam_log.ExamID='" + examId + "'";
		else if (50 <= oldGrade && oldGrade <= 59)
			sql = "update exam_log set exam_log.50_59=exam_log.50_59-1 where exam_log.ExamID='" + examId + "'";
		else if (60 <= oldGrade && oldGrade <= 69)
			sql = "update exam_log set exam_log.60_69=exam_log.60_69-1 where exam_log.ExamID='" + examId + "'";
		else if (70 <= oldGrade && oldGrade <= 79)
			sql = "update exam_log set exam_log.70_79=exam_log.70_79-1 where exam_log.ExamID='" + examId + "'";
		else if (80 <= oldGrade && oldGrade <= 89)
			sql = "update exam_log set exam_log.80_89=exam_log.80_89-1 where exam_log.ExamID='" + examId + "'";
		else if (90 <= oldGrade && oldGrade <= 100)
			sql = "update exam_log set exam_log.90_100=exam_log.90_100-1 where exam_log.ExamID='" + examId + "'";

		ok = jdbc.runUpdateQuery(sql);
		if (!ok)
			return false;
		int average = jdbc.calculateAverage(examId);
		int median = jdbc.calculateMedian(examId);

		sql = "update exam_log set exam_log.average=" + average + ", exam_log.median=" + median
				+ " where exam_log.ExamID='" + examId + "'";
		return jdbc.runUpdateQuery(sql);
	}

	/**
	 * get exam's duration with given execution code
	 * 
	 * @param executionCode exam's execution code
	 * @return exam's duration in minutes
	 */
	public int getExamDuration(String executionCode) {
		String sql = "select if(exam.newDurOK='1', exam.newDur, exam.Duration) Duration from exam where exam.ExecutionCode='"
				+ executionCode + "'";
		return jdbc.getIntQuery(sql);
	}

	/**
	 * get exam id by execution code
	 * 
	 * @param executionCode
	 * @return exam id
	 */
	public String getExamByExecutionCode(String executionCode) {
		String sql = "select exam.ExamID from exam where exam.ExecutionCode='" + executionCode + "'";

		return jdbc.getStringQuery(sql);
	}

	/**
	 * gets offline exam id and returns its filename
	 * 
	 * @param examID exam's ID number
	 * @return filename of offline exam
	 */
	public String getExamFileName(String examID) {
		String sql = "select exam.FileName from exam where exam.ExamID='" + examID + "'";
		return jdbc.getStringQuery(sql);
	}

	/**
	 * checks if id is valid or not
	 * 
	 * @param id user ID number
	 * @return is id valid
	 */
	public boolean validID(String id) {
		String sql = "select person.ID from person where person.ID='" + id + "'";
		String res = jdbc.getStringQuery(sql);
		if (res == null)
			return false;
		return true;
	}

	/**
	 * checks if execution code is valid or not
	 * 
	 * @param executionCode exam execution code
	 * @return is execution code valid
	 */
	public boolean validExecutionCode(String executionCode) {
		String sql = "select exam.ExecutionCode from exam where exam.ExecutionCode='" + executionCode + "'";
		String res = jdbc.getStringQuery(sql);
		if (res == null)
			return false;
		return true;
	}

	/**
	 * sets exam to be ongoing (if ongoing=1) or not ongoing (=0)
	 * 
	 * @param examID  exam ID number
	 * @param ongoing (1-ongoing, 0-not ongoing)
	 */
	public void setExamOngoing(String examID, int ongoing) {
		String sql = "update exam set exam.isOngoing=" + ongoing + " where exam.ExamID='" + examID + "'";
		jdbc.runUpdateQuery(sql);
	}

	/**
	 * get student id and return all the exam he finished and get grade from a
	 * teacher
	 * 
	 * @param studentId student ID number
	 * @return all the exam for student
	 */
	public String GetDoneExamForStudent(String studentId) {
		String sql = "SELECT done_exam.ExamID, substr(done_exam.ExamID, 1, 2),"
				+ " substr(done_exam.ExamID, 3, 2), done_exam.Grade" + " FROM person, done_exam"
				+ " where person.ID = done_exam.StudentID" + " and done_exam.GradeStatus = 1" + " and person.ID = '"
				+ studentId + "'";

		String ExamID, SubjectName, CouesName, result = "";
		int Grade;
		Statement stmt;
		ResultSet rs;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				ExamID = rs.getString(1); /* exam id */
				SubjectName = jdbc.getBankName(rs.getString(2)); /* subject name */
				CouesName = jdbc.getCourseName(rs.getString(3)); /* course name */
				Grade = rs.getInt(4); /* grade */
				result += ExamID + "_" + SubjectName + "_" + CouesName + "_" + Grade + "_";
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * updates done exam in database
	 * 
	 * @param e      a done exam
	 * @param report
	 * @return indication if update in database succeeded or not
	 */
	public boolean updateFinishedExamInDB(DoneExam e, Report report) {
		boolean ok;
		String sql = "insert into done_exam (ExamID, StudentID, Date, OriginalDuration, ActualDuration, isFinishSuccessful, SysGrade, GradeStatus, EQanswers)"
				+ " values ('" + e.getExamID() + "', '" + e.getStudentID() + "', '" + e.getFormatDateTime() + "', "
				+ e.getDuration() + ", " + e.getActualDuration() + ", " + e.getFinishedSuccessful() + ", "
				+ e.getSysGrade() + ", 0, '" + e.getDBanswer() + "')";
		ok = runUpdateQuery(sql); /* update done_exam table in DB */
		if (!ok)
			return false;

		/* updates for statistic data */
		sql = "update exam_log set exam_log.median=" + report.getMedian() + ", exam_log.average=" + report.getAverage()
				+ ", exam_log.numFinishSuccessful="
				+ jdbc.getStringQuery(
						"select count(isFinishSuccessful) from done_exam where done_exam.ExamID='"
								+ e.getExamID() + "' and done_exam.isFinishSuccessful=1")
				+ ", exam_log.numFinishUnsuccessful="
				+ jdbc.getStringQuery("select count(isFinishSuccessful) from done_exam where done_exam.ExamID='"
						+ e.getExamID() + "' and done_exam.isFinishSuccessful!=1")
				+ " where exam_log.ExamID='" + e.getExamID() + "'";
		ok = runUpdateQuery(sql); /*
									 * update exam's median, average, numFinishedSuccessful, numFinishedUnsuccessful
									 * in database
									 */
		if (!ok)
			return false;

		int dist[] = report.getDistribution(); // REFACTOR
		for (int i = 0; i < 10; i++) {
			if (i == 0)
				jdbc.runUpdateQuery("update exam_log set exam_log.0_9=" + dist[i] + " where exam_log.ExamID='"
						+ e.getExamID() + "'");
			else if (i == 1)
				jdbc.runUpdateQuery("update exam_log set exam_log.10_19=" + dist[i] + " where exam_log.ExamID='"
						+ e.getExamID() + "'");
			else if (i == 2)
				jdbc.runUpdateQuery("update exam_log set exam_log.20_29=" + dist[i] + " where exam_log.ExamID='"
						+ e.getExamID() + "'");
			else if (i == 3)
				jdbc.runUpdateQuery("update exam_log set exam_log.30_39=" + dist[i] + " where exam_log.ExamID='"
						+ e.getExamID() + "'");
			else if (i == 4)
				jdbc.runUpdateQuery("update exam_log set exam_log.40_49=" + dist[i] + " where exam_log.ExamID='"
						+ e.getExamID() + "'");
			else if (i == 5)
				jdbc.runUpdateQuery("update exam_log set exam_log.50_59=" + dist[i] + " where exam_log.ExamID='"
						+ e.getExamID() + "'");
			else if (i == 6)
				jdbc.runUpdateQuery("update exam_log set exam_log.60_69=" + dist[i] + " where exam_log.ExamID='"
						+ e.getExamID() + "'");
			else if (i == 7)
				jdbc.runUpdateQuery("update exam_log set exam_log.70_79=" + dist[i] + " where exam_log.ExamID='"
						+ e.getExamID() + "'");
			else if (i == 8)
				jdbc.runUpdateQuery("update exam_log set exam_log.80_89=" + dist[i] + " where exam_log.ExamID='"
						+ e.getExamID() + "'");
			else if (i == 9)
				jdbc.runUpdateQuery("update exam_log set exam_log.90_100=" + dist[i] + " where exam_log.ExamID='"
						+ e.getExamID() + "'");
		}
//		if (e.getSysGrade() <= 9)
//			sql = "update exam_log set exam_log.0_9=exam_log.0_9+1 where exam_log.ExamID='" + e.getExamID() + "'";
//		else if (10 <= e.getSysGrade() && e.getSysGrade() <= 19)
//			sql = "update exam_log set exam_log.10_19=exam_log.10_19+1 where exam_log.ExamID='" + e.getExamID() + "'";
//		else if (20 <= e.getSysGrade() && e.getSysGrade() <= 29)
//			sql = "update exam_log set exam_log.20_29=exam_log.20_29+1 where exam_log.ExamID='" + e.getExamID() + "'";
//		else if (30 <= e.getSysGrade() && e.getSysGrade() <= 39)
//			sql = "update exam_log set exam_log.30_39=exam_log.30_39+1 where exam_log.ExamID='" + e.getExamID() + "'";
//		else if (40 <= e.getSysGrade() && e.getSysGrade() <= 49)
//			sql = "update exam_log set exam_log.40_49=exam_log.40_49+1 where exam_log.ExamID='" + e.getExamID() + "'";
//		else if (50 <= e.getSysGrade() && e.getSysGrade() <= 59)
//			sql = "update exam_log set exam_log.50_59=exam_log.50_59+1 where exam_log.ExamID='" + e.getExamID() + "'";
//		else if (60 <= e.getSysGrade() && e.getSysGrade() <= 69)
//			sql = "update exam_log set exam_log.60_69=exam_log.60_69+1 where exam_log.ExamID='" + e.getExamID() + "'";
//		else if (70 <= e.getSysGrade() && e.getSysGrade() <= 79)
//			sql = "update exam_log set exam_log.70_79=exam_log.70_79+1 where exam_log.ExamID='" + e.getExamID() + "'";
//		else if (80 <= e.getSysGrade() && e.getSysGrade() <= 89)
//			sql = "update exam_log set exam_log.80_89=exam_log.80_89+1 where exam_log.ExamID='" + e.getExamID() + "'";
//		else if (90 <= e.getSysGrade() && e.getSysGrade() <= 100)
//			sql = "update exam_log set exam_log.90_100=exam_log.90_100+1 where exam_log.ExamID='" + e.getExamID() + "'";
//
//		ok = runUpdateQuery(sql); /* update exam's distribution in database */
//		if (!ok)
//			return false;

		findCopyCats(e.getExamID());

		return true;

	}

	/**
	 * find students that are suspected for copying answers in given exam & mark
	 * them as copy-cats in database
	 * 
	 * @param examID exam ID number
	 */
	private void findCopyCats(String examID) {
		String sql = "select distinct cems.a.StudentID from cems.done_exam a, cems.done_exam b "
				+ "where b.EQanswers=a.EQanswers " + "and a.ExamID='" + examID + "' and b.ExamID='" + examID + "' "
				+ "and (a.StudentID != b.StudentID) and a.SysGrade!=100 and b.SysGrade!=100";

		Statement stmt;
		ResultSet rs;
		String studentID;
		ArrayList<String> copycats = new ArrayList<>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql); /* get copy-cats */

			while (rs.next()) {
				studentID = rs.getString(1);
				copycats.add(studentID);
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < copycats.size(); i++) { /* update copy-cats in database */
			sql = "update done_exam set done_exam.copycatSuspecious=1 where done_exam.StudentID='" + copycats.get(i)
					+ "' and done_exam.ExamID='" + examID + "'";
			jdbc.runUpdateQuery(sql);
		}
	}

	/**
	 * get all change duration request for principal
	 * 
	 * @return string of student id and grades
	 */
	public String getChangeDuration() {
		String sql = "select exam.ExamID, substr(exam.ExamID, 1, 2), exam.Duration, exam.newDur, exam.durChangeRationale"
				+ " from exam where exam.newDurOK = 0 and exam.isOngoing=1";

		String ExamID, SubjectName, ChangeReason, result = "";
		int OldDuration, NewDuration;
		Statement stmt;
		ResultSet rs;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				ExamID = rs.getString(1); /* exam id */
				SubjectName = jdbc.getBankName(rs.getString(2)); /* subject name */
				OldDuration = rs.getInt(3); /* old duration */
				NewDuration = rs.getInt(4); /* new duration */
				ChangeReason = rs.getString(5); /* change reason */
				result += ExamID + "_" + SubjectName + "_" + OldDuration + "_" + NewDuration + "_" + ChangeReason + "_";
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * returns exam's execution code given its exam ID
	 * 
	 * @param examID exam ID number
	 * @return exam's execution code
	 */
	public String getExectionCodeByExamID(String examID) {
		String sql = "select exam.ExecutionCode from exam where exam.ExamID='" + examID + "'";
		return jdbc.getStringQuery(sql);
	}

	/**
	 * confirm/deny change duration request for principal
	 * 
	 * @param examID exam ID number
	 * @param status 1 - confirm, 2 - deny
	 * @return indication if update in database succeeded
	 */
	public boolean approvalChangeDuration(String examID, String status) {
		String sql = "update exam set exam.newDurOK = '" + status + "'" + " where exam.ExamID = '" + examID + "'";
		return runUpdateQuery(sql);
	}

	/**
	 * get all individuals that have given affiliation
	 * 
	 * @param affiliation get details of given affiliation - Teacher, Student or
	 *                    Principal
	 * @return person details that have given affiliation (ID, firstname, lastname)
	 */
	public String GetAllPersonByAffiliation(String affiliation) {
		String sql = "select person.ID, person.Name, person.Surname from person" + " where person.Affiliation = '"
				+ affiliation + "'";

		String Id, FirstName, LastName, result = "";
		Statement stmt;
		ResultSet rs;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				Id = rs.getString(1);
				LastName = rs.getString(2);
				FirstName = rs.getString(3);
				result += Id + "_" + LastName + "_" + FirstName + "_";
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * get all courses for principal
	 * 
	 * @return courses name
	 */
	public String GetAllCourses() {
		String sql = "select course.CName from course";

		String CourseName, result = "";
		Statement stmt;
		ResultSet rs;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				CourseName = rs.getString(1); /* course name */
				result += CourseName + "_";
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return null;
		}

		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * update offline done exam details in database
	 * 
	 * @param e a done offline exam
	 * @return indication if update in database succeeded or not
	 */
	public boolean updateStudentOfflineExamInDB(DoneExam e) {
		boolean ok = true;
		String sql = "insert into done_exam (ExamID, StudentID, Date, OriginalDuration, ActualDuration, isFinishSuccessful, FileName, GradeStatus)"
				+ " values ('" + e.getExamID() + "', '" + e.getStudentID() + "', '" + e.getFormatDateTime() + "', "
				+ jdbc.getExamDuration(e.getExecutionCode()) + ", " + e.getActualDuration() + ", "
				+ e.getFinishedSuccessful() + ", '" + e.getFileName() + "', 0)";
		ok = jdbc.runUpdateQuery(sql);
		if (!ok)
			return false;
		/* updates for statistic data */
		sql = "update exam_log set exam_log.median=" + jdbc.calculateMedian(e.getExamID()) + ", exam_log.average="
				+ jdbc.calculateAverage(e.getExamID()) + ", exam_log.numFinishSuccessful="
				+ jdbc.getStringQuery(
						"select count(isFinishSuccessful) from done_exam where done_exam.ExamID='"
								+ e.getExamID() + "' and done_exam.isFinishSuccessful=1")
				+ ", exam_log.numFinishUnsuccessful="
				+ jdbc.getStringQuery("select count(isFinishSuccessful) from done_exam where done_exam.ExamID='"
						+ e.getExamID() + "' and done_exam.isFinishSuccessful!=1")
				+ " where exam_log.ExamID='" + e.getExamID() + "'";
		return runUpdateQuery(sql); /*
									 * update exam's median, average, numFinishedSuccessful, numFinishedUnsuccessful
									 * in database
									 */

	}

	/**
	 * generates grade for online exam
	 * 
	 * @param e a done exam
	 * @return exam's score
	 */
	public int generateSystemGrade(DoneExam e) {
		ArrayList<QuestionInExam> examQuestions = new ArrayList<>();
		for (int i = 0; i < e.getQuestions().size(); i++)
			examQuestions.add(e.getQuestions().get(i));

		int score = 0;
		boolean moreThan1 = false;
		String[] questionIDs = null, questionScores = null;
		String sql = "select exam.ExamQuestionIDs, exam.ExamQuestionScores from exam where exam.ExamID='"
				+ e.getExamID() + "'";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			String qIDs = rs.getString(1); /* exam question IDs */
			String qScores = rs.getString(2); /* exam question scores */

			if (qIDs.length() > 1) /* if exam has more than 1 question */
			{
				questionIDs = qIDs.split(",");
				questionScores = qScores.split(",");
				moreThan1 = true;
			}
			stmt.close();
			rs.close();
		} catch (SQLException e1) {
			return 0;
		}
		for (int i = 0; i < examQuestions.size(); i++) {
			sql = "select question.RightAnswer from question where question.QuestionID='"
					+ examQuestions.get(i).getQuestionID() + "'";
			int rightAnswer = jdbc.getIntQuery(sql);

			if (examQuestions.get(i).getChosenAns() == rightAnswer) { /* if answer is correct */
				if (moreThan1) { /* if there is more than 1 question in exam */
					for (int j = 0; j < questionScores.length; j++) // find question's score
						if (questionIDs[j].equals(examQuestions.get(i).getQuestionID()))
							score += Integer.parseInt(questionScores[j]);
				} else /* if there is only 1 question in exam */
					score = 100;
			}
		}

		return score; /* return exam grade */
	}

	/**
	 * update exam in exam log table when a student starts this exam
	 * 
	 * @param examID exam ID number
	 */
	public void updateExamLog(String examID) {
		String sql = "select count(ExamID) from exam_log where exam_log.ExamID='" + examID + "'";
		int count = jdbc.getIntQuery(sql);
		if (count == 0) /* if exam is not in exam log => add it to table & initialize it */
			sql = "insert into exam_log(ExamID, numStarted, numFinishSuccessful, numFinishUnsuccessful, median, average, 0_9, 10_19, 20_29, 30_39, 40_49, 50_59, 60_69, 70_79, 80_89, 90_100)"
					+ " values ('" + examID + "', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) ";
		else /* exam is in log already, increment numStarted by 1 */
			sql = "update exam_log set exam_log.numStarted = exam_log.numStarted+1 where exam_log.ExamID='" + examID
					+ "'";

		jdbc.runUpdateQuery(sql);

	}

	/**
	 * return full course report: course average, course median, & for each exam in
	 * course: exam number, exam average, exam median
	 * 
	 * @param courseID course ID number
	 * @return full course report
	 */
	public String getCourseReport(String courseID) {
		String sql = "select AVG(exam_log.average) from exam_log where substr(exam_log.ExamID, 3, 2)='" + courseID
				+ "'";
		int courseAverage = jdbc.getIntQuery(sql); /* course average */
		int median = jdbc.calculateCourseMedian(courseID); /* course median */
		String result = jdbc.getCourseName(courseID) + "_" + courseAverage + "_" + median;

		sql = "select exam_log.ExamID, exam_log.median, exam_log.average from exam_log where substr(exam_log.ExamID, 3, 2)='"
				+ courseID + "'";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String examID = rs.getString(1);
				int examMedian = rs.getInt(2);
				int examAverage = rs.getInt(3);
				result += "_" + examID + "_" + examMedian + "_" + examAverage;
			}
			stmt.close();
			rs.close();
		} catch (SQLException e1) {
			return null;
		}
		return result;
	}

	/**
	 * return full teacher report for principal: distribution between all exams of
	 * teacher, average grade & median for teacher's exams
	 * 
	 * @param teacherID teacher ID number
	 * @return full teacher report for principal
	 */
	public String getTeacherReportForPrincipal(String teacherID) {
		String sql = "select distinct l.ExamID, l.median, l.average from exam e, exam_log l, done_exam d where e.TeacherID='"
				+ teacherID + "' and l.ExamID=e.ExamID and l.ExamID=d.ExamID and d.GradeStatus=1";

		String result = teacherID;
		int teacherExamsAvg, teacherExamsMedian;
		teacherExamsAvg = jdbc.calculateTeacherExamsAverage(teacherID);
		teacherExamsMedian = jdbc.calculateTeacherExamsMedian(teacherID);
		result += "_" + teacherExamsAvg + "_" + teacherExamsMedian;
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String examID = rs.getString(1);
				int examMedian = rs.getInt(2);
				int examAverage = rs.getInt(3);
				result += "_" + examID + "_" + examMedian + "_" + examAverage;
			}
			stmt.close();
			rs.close();
		} catch (SQLException e1) {
			return null;
		}
		return result;
	}

	/**
	 * calculates the median grade of all exams of teacher
	 * 
	 * @param teacherID teacher ID number
	 * @return median grade of all exams of teacher
	 */
	private int calculateTeacherExamsMedian(String teacherID) {
		int median;
		String sql = "select d.Grade from done_exam d where d.GradeStatus=1 and d.ExamID in (select exam.ExamID from exam where exam.TeacherID='"
				+ teacherID + "') order by d.Grade";
		ArrayList<Integer> grades = new ArrayList<>();
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next())
				grades.add(rs.getInt(1));
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			return 0;
		}
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
	 * calculates the average grade of all exams of teacher
	 * 
	 * @param teacherID teacher ID number
	 * @return average grade of all exams of teacher
	 */
	private int calculateTeacherExamsAverage(String teacherID) {
		String sql = "select AVG(d.Grade) from done_exam d, exam where d.GradeStatus=1 and d.ExamID in (select exam.ExamID from exam where exam.TeacherID='"
				+ teacherID + "')";

		return jdbc.getIntQuery(sql);
	}

	/**
	 * return full student report for principal: distribution between all student's
	 * exams, his average and his median
	 * 
	 * @param studentID student ID number
	 * @return full student report for principal
	 */
	public String getStudentReportForPrincipal(String studentID) { // getStudentReportForPrincipal_studentID
		String sql = "select done_exam.Grade, done_exam.ExamID, done_exam.OriginalDuration, done_exam.ActualDuration, done_exam.Date from done_exam where done_exam.StudentID='"
				+ studentID + "' and done_exam.GradeStatus=1";
		ArrayList<Integer> grades = new ArrayList<>();
		int median, average, sum = 0;
		String result1 = "", result2 = "";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				int grade = rs.getInt(1);
				sum += grade;
				grades.add(grade);
				String examID = rs.getString(2);
				int origDur = rs.getInt(3);
				int actualDur = rs.getInt(4);
				String date = rs.getString(5);
				result2 += examID + "_" + grade + "_" + origDur + "_" + actualDur + "_" + date + "_";
			}
			stmt.close();
			rs.close();
		} catch (SQLException e1) {
			return null;
		}
		if (sum == 0 || grades.size() == 0)
			return null; /* student has no exams */
		Collections.sort(grades);
		if (grades.size() % 2 == 0)
			median = (grades.get(grades.size() / 2 - 1) + grades.get(grades.size() / 2)) / 2;
		else
			median = grades.get(((grades.size() + 1) / 2) - 1);

		average = sum / grades.size();

		result1 = studentID + "_" + average + "_" + median + "_" + result2;

		return result1; // studentID_average_median_examID_grade
	}

	/**
	 * returns copy of checked exam with grade and teacher comments
	 * 
	 * @param examID     exam ID number
	 * @param studentID  student ID number
	 * @param forTeacher true-copy for teacher, false-copy for student
	 * @return copy of checked exam (returns a MyFile if the exam was offline, a
	 *         string with all the exam information if the exam was online)
	 */
	public Object getCopyOfExam(String examID, String studentID, boolean forTeacher) {
		String sql = "select exam.ExamType from exam where exam.ExamID='" + examID + "'";
		int res = jdbc.getIntQuery(sql); /* res=1 if exam is online, res=0 if exam is offline */
		String teacherfullname = jdbc
				.getStringQuery("select concat(Name, ' ', Surname) from person, exam where exam.ExamID='" + examID
						+ "' and person.ID=exam.TeacherID");
		String courseName = this.getCourseName(examID.substring(2, 4));
		String subjectName = this.getBankName(examID.substring(0, 2));
		String studentComment = jdbc
				.getStringQuery("select exam.Comments4Students from exam where exam.ExamID='" + examID + "'");

		if (res == 1)
			studentComment = studentComment.replaceAll("_", "1a6gf");

		if (studentComment == null || studentComment.equals(""))
			studentComment = " ";
		int grade;
		if (forTeacher)
			grade = jdbc.getIntQuery("select done_exam.SysGrade from done_exam where done_exam.StudentID='" + studentID
					+ "' and done_exam.ExamID='" + examID + "'");
		else
			grade = jdbc.getIntQuery("select done_exam.Grade from done_exam where done_exam.StudentID='" + studentID
					+ "' and done_exam.ExamID='" + examID + "'");

		if (res == 1) { /* online exam */
			String questionIDs = jdbc
					.getStringQuery("select exam.ExamQuestionIDs from exam where exam.ExamID='" + examID + "'"); // 0100,0101,0102
			String questionScores = jdbc
					.getStringQuery("select exam.ExamQuestionScores from exam where exam.ExamID='" + examID + "'"); // 10,40,50
			String chosenAns = jdbc.getStringQuery("select done_exam.EQanswers from done_exam where done_exam.ExamID='"
					+ examID + "' and done_exam.StudentID='" + studentID + "'");

			/* get full exam questions, their scores & answers */
			ArrayList<QuestionInExam> questions = new ArrayList<>();
			String[] questionIDArr, scoresArr, chosenAnsArr;
			if (questionIDs.length() > 2) {
				questionIDArr = questionIDs.split(",");
				scoresArr = questionScores.split(",");
				chosenAnsArr = chosenAns.split(",");
				for (int i = 0; i < questionIDArr.length; i++) {
					QuestionInExam q = new QuestionInExam();
					q.setQuestionID(questionIDArr[i]);
					q.setScore(Integer.parseInt(scoresArr[i]));
					String temp = jdbc
							.getStringQuery("select question.Question from question where question.QuestionID='"
									+ q.getQuestionID() + "'");

					temp = temp.replaceAll("_", "1a6gf");

					q.setQuestionText(temp);
					q.setChosenAns(Integer.parseInt(chosenAnsArr[i]));
					q.setCorrectAns(Integer.parseInt(
							jdbc.getStringQuery("select question.RightAnswer from question where question.QuestionID='"
									+ q.getQuestionID() + "'")));
					if (q.getCorrectAns() == q.getChosenAns())
						q.setActualScore(q.getScore());
					else
						q.setActualScore(0);
					questions.add(q);
				}

				ArrayList<Question> examQuestions = new ArrayList<>();
				examQuestions = getQuestionsForExam(questionIDArr);

//				examID_teacherfullname_subject_course_studentComment_questionText_Ans1_Ans2_Ans3_Ans4_chosenAns_correctAns_qScore_actualqScore
				/* prepare result */
				String comment4Teacher = " ";
				if (forTeacher) {
					comment4Teacher = jdbc.getStringQuery(
							"select exam.Comments4Teacher from exam where exam.ExamID='" + examID + "'");
					comment4Teacher = comment4Teacher.replaceAll("_", "1a6gf");

					if (comment4Teacher == null || comment4Teacher.equals(""))
						comment4Teacher = " ";
				}
				if (!forTeacher) {
					studentComment = jdbc.getStringQuery(
							"select done_exam.TeacherComments from done_exam where done_exam.StudentID='" + studentID
									+ "' and done_exam.ExamID='" + examID + "'");
					if (studentComment == null)
						studentComment = " ";
				}
				String result = examID + "_" + grade + "_" + teacherfullname + "_" + subjectName + "_" + courseName
						+ "_" + studentComment + "_" + comment4Teacher;

				for (int i = 0; i < questions.size(); i++) {
					String questionText = questions.get(i).getQuestionText().replaceAll("_", "1a6gf");
					String answer1 = examQuestions.get(i).getAnswer1().getAnswerText().replaceAll("_", "1a6gf");
					String answer2 = examQuestions.get(i).getAnswer2().getAnswerText().replaceAll("_", "1a6gf");
					String answer3 = examQuestions.get(i).getAnswer3().getAnswerText().replaceAll("_", "1a6gf");
					String answer4 = examQuestions.get(i).getAnswer4().getAnswerText().replaceAll("_", "1a6gf");

					result += "_" + questionText + "_" + answer1 + "_" + answer2 + "_" + answer3 + "_" + answer4 + "_"
							+ questions.get(i).getChosenAns() + "_" + questions.get(i).getCorrectAns() + "_"
							+ questions.get(i).getScore() + "_" + questions.get(i).getActualScore();
				}
				return result;
			}
		}

		else /* offline exam */
		{
			String filename = jdbc.getStringQuery("select done_exam.FileName from done_exam where done_exam.ExamID='"
					+ examID + "' and done_exam.StudentID='" + studentID + "'");
			MyFile exam = new MyFile(filename);
			exam.setCourseName(courseName);
			exam.setSubjectName(subjectName);
			exam.setFileSource(FileSource.CopyForStudent);
			exam.setGrade(grade);

			return exam;
		}

		return null;
	}

	/**
	 * returns course's subject code
	 * 
	 * @param courseName course name
	 * @return course's subject code
	 */
	public String getSubjectCodeOfCourse(String courseName) {
		return jdbc.getStringQuery("select course.SubjectCode from course where course.CName='" + courseName + "'");
	}

	/**
	 * returns indication if the exam is locked or not
	 * 
	 * @param examID exam ID number
	 * @return true if exam is locked, false if not
	 */
	public boolean isLocked(String examID) {
		int res = jdbc.getIntQuery("select exam.LockStatus from exam where exam.ExamID='" + examID + "'");
		if (res == 1)
			return true;
		return false;
	}

	/**
	 * updates fail exam in database
	 * 
	 * @param e a fail exam
	 * @return true if update in database succeeded, false otherwise
	 */
	public boolean updateFinishedFailExamInDB(DoneExam e) {
		boolean ok;
		String sql = "insert into done_exam (ExamID, StudentID, OriginalDuration, ActualDuration, isFinishSuccessful, SysGrade, GradeStatus, copycatSuspecious)"
				+ " values ('" + e.getExamID() + "', '" + e.getStudentID() + "', " + e.getDuration() + ", "
				+ e.getActualDuration() + ", " + e.getFinishedSuccessful() + ", " + e.getSysGrade() + ", 0, 0)";
		ok = runUpdateQuery(sql); /* update done_exam table in DB */
		if (!ok)
			return false;
		sql = "update done_exam set Date ='" + e.getFormatDateTime() + "' where done_exam.ExamID ='" + e.getExamID()
				+ "' and done_exam.StudentID='" + e.getStudentID() + "'";
		ok = runUpdateQuery(sql); /* update done_exam table in DB */
		if (!ok)
			return false;
		/* updates for statistic data */
		sql = "update exam_log set exam_log.median=" + jdbc.calculateMedian(e.getExamID()) + ", exam_log.average="
				+ jdbc.calculateAverage(e.getExamID()) + ", exam_log.numFinishSuccessful="
				+ jdbc.getIntQuery("select count(StudentID) from done_exam where done_exam.ExamID='" + e.getExamID()
						+ "' and done_exam.isFinishSuccessful=1")
				+ ", exam_log.numFinishUnsuccessful="
				+ jdbc.getIntQuery("select count(StudentID) from done_exam where done_exam.ExamID='" + e.getExamID()
						+ "' and done_exam.isFinishSuccessful!=1")
				+ " where exam_log.ExamID='" + e.getExamID() + "'";
		ok = runUpdateQuery(sql); /*
									 * update exam's median, average, numFinishedSuccessful, numFinishedUnsuccessful
									 * in database
									 */
		if (!ok)
			return false;

		sql = "update exam_log set exam_log.0_9=exam_log.0_9+1 where exam_log.ExamID='" + e.getExamID() + "'";
		ok = runUpdateQuery(sql); /* update exam's distribution in database */
		if (!ok)
			return false;

		return true;
	}

	/**
	 * checks to see if user already submitted exam
	 * 
	 * @param examID exam ID number
	 * @param userID user ID number
	 * @return true if user already submitted exam
	 */
	public boolean SubmitAlready(String examID, String userID) {
		String sql = "select count(done_exam.StudentID) from done_exam where done_exam.StudentID='" + userID
				+ "' and done_exam.ExamID='" + examID + "'";
		int res = jdbc.getIntQuery(sql);
		if (res > 0)
			return true;

		return false;
	}

	/**
	 * check to see if exam is online or offline
	 * 
	 * @param examID exam ID number
	 * @return 1 if exam is online, 0 if exam is offline
	 */
	public int getExamType(String examID) {
		String sql = "select exam.ExamType from exam where exam.ExamID='" + examID + "'";
		return jdbc.getIntQuery(sql);
	}

	/**
	 * creates a new unique execution code for exam
	 * 
	 * @return a unique alphanumeric execution code of 4 digits
	 */
	public String generateUniqueExecutionCode() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 4;
		Random random = new Random();
		boolean foundUnique = false;
		String generatedString = null;
		while (!foundUnique) {
			generatedString = random.ints(leftLimit, rightLimit + 1)
					.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
					.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
			if (!jdbc.validExecutionCode(generatedString))
				foundUnique = true;
		}
		return generatedString;

	}

	/**
	 * sync data from external system's csv files to our system's database
	 */
	public void loadDataFromFile() {
		boolean ok = true;

		/* load person data */
		ok = jdbc.runUpdateQuery(
				"load data local infile 'C:\\\\CEMS_server\\\\External\\\\person.csv' into table external_data fields terminated by ','\r\n"
						+ "enclosed by '\"'\r\n" + "lines terminated by '\\n'");
		if (!ok)
			ServerUI.sb.importFail();
		;

		/* check if this is the first time the system is up */
		int res = jdbc.getIntQuery("select count(person.ID) from person");
		if (res == 0) // first time
		{
			ok = jdbc.runUpdateQuery(
					"insert into person(ID, Name, Surname, Email, Affiliation, RelevantSubjects, Username, Password) select * from external_data");
			if (!ok)
				ServerUI.sb.importFail();
			;
			ok = jdbc.runUpdateQuery("update person set person.Status=0");
			if (!ok)
				ServerUI.sb.importFail();
			;
		}
		/* load course data */
		ok = jdbc.runUpdateQuery(
				"load data local infile 'C:\\\\CEMS_server\\\\External\\\\course.csv' into table course fields terminated by ','\r\n"
						+ "enclosed by '\"'\r\n" + "lines terminated by '\\n' IGNORE 1 LINES;");
		if (!ok)
			ServerUI.sb.importFail();
		;

		/* load subjects data */
		ok = jdbc.runUpdateQuery(
				"load data local infile 'C:\\\\CEMS_server\\\\External\\\\subject.csv' into table subject fields terminated by ','\r\n"
						+ "enclosed by '\"'\r\n" + "lines terminated by '\\n' IGNORE 1 LINES;");
		if (!ok)
			ServerUI.sb.importFail();
		;

		ServerUI.sb.importSucess();
	}

	/**
	 * get user type (Teacher, Student, Principal)
	 * 
	 * @param username
	 * @return person's affiliation
	 */
	public String getUserTypeByUsername(String username) {
		return jdbc.getStringQuery("select person.Affiliation from person where person.Username='" + username + "'");
	}

	/**
	 * returns number of student who got grade in decile i under given exam ID
	 * 
	 * @param i      decile
	 * @param examID
	 * @return number of students who got grade in decile i
	 */
	public int getNumStudentWhoGotGradeInGivenDecile(int i, String examID) { // REFACTOR
		if (i < 0 || i > 9)
			return -1; // invalid decile
		String sql, decile = "";
		switch (i) {
		case 0:
			decile = "0_9";
			break;
		case 1:
			decile = "10_19";
			break;
		case 2:
			decile = "20_29";
			break;
		case 3:
			decile = "30_39";
			break;
		case 4:
			decile = "40_49";
			break;
		case 5:
			decile = "50_59";
			break;
		case 6:
			decile = "60_69";
			break;
		case 7:
			decile = "70_79";
			break;
		case 8:
			decile = "80_89";
			break;
		case 9:
			decile = "90_100";
		}

		sql = "select exam_log." + decile + " from exam_log where exam_log.ExamID='" + examID + "'";

		return jdbc.getIntQuery(sql);
	}

}