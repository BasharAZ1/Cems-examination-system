package Data;

import java.util.ArrayList;

/**
 * exam is comprised of execution code, Id of the exam, author - the teacher
 * that created the exam, comments for teacher only (optional), comments for
 * students (optional), bank ID (the subject ofthe exam e.g. math), course (the
 * course that the exam belongs to, e.g. algebra), array list of questions
 * (dynamic amount of quesions), duration of the exam (in minutes), scores for
 * each question in the exam, exam type (offline or online exam) and draft mode.
 * 
 * @author Ayala Cohen
 *
 */
public class Exam {
	private String ExamID = "";
	private String newExamID = ""; /* if exam was edited */

	enum Status {
		Locked, Unlocked
	}
	
	enum Ongoing {
		YES, NO
	}
	private int Duration, studentDuration;
	private int newDuration=Duration;
	private String author = "";
	private String authorID = "";
	private String studentID="";
	private String executionCode = "";
	private Comment teacherComment;
	private Comment studentComment;
	private Subject subject;
	private String subjectName = "";
	private ArrayList<QuestionInExam> Questions;
	private ArrayList<String> questionsID;
	private ArrayList<String> scores;
	private String courseName = "";
	private String CourseID;
	private Course course;
	private int examType; /* 1-online, 0-offline */
	private int editMode; /* 1-draft, 0-not draft */
	private String FileName;
	private String bankID = "";
	Status lockStatus;
	Ongoing examOngoing;
	private String lockStatusString="";
	private String OngoingString="";
	private String questions4DB="";
	public String getQuestions4DB() {
		return questions4DB;
	}
	public void setQuestions4DB(String questions4db) {
		questions4DB = questions4db;
	}
	public Exam() {

	}
	public String getStudentID() {
		return studentID;
	}
	public int getStudentDuration() {
		return studentDuration;
	}
	public void setStudentDuration(int studentDuration) {
		this.studentDuration = studentDuration;
	}
	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public Ongoing getExamOngoing() {
		return examOngoing;
	}
	public void setExamOngoing(Ongoing examOngoing) {
		this.examOngoing = examOngoing;
	}
	public String getOngoingString() {
		return OngoingString;
	}
	public int getNewDuration() {
		return newDuration;
	}

	public void setNewDuration(int newDuration) {
		this.newDuration = newDuration;
	}

	public void setOngoingString(String ongoingString) {
		OngoingString = ongoingString;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public String getLockStatusString() {
		return lockStatusString;
	}

	public void setLockStatusString(String lockStatusString) {
		this.lockStatusString = lockStatusString;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public Status getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(Status lockStatus) {
		this.lockStatus = lockStatus;
	}
	public String getLockStatusToString() {
		if (lockStatus.equals(Status.Locked))
			return "Locked";
		return "Unlocked";
	}
	public String getNewExamID() {
		return newExamID;
	}

	public void setNewExamID(String newExamID) {
		this.newExamID = newExamID;
	}
	public String getAuthorID() {
		return authorID;
	}

	public void setAuthorID(String authorID) {
		this.authorID = authorID;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCourseID() {
		return CourseID;
	}

	public void setCourseID(String courseID) {
		CourseID = courseID;
	}

	public String getBankID() {
		return bankID;
	}

	/**
	 * 
	 * @return exam's questions in the format 01001,01002,.. etc (for db)
	 */
	public String getQuestionIDsToString() {
		String result = "";

		for (int i = 0; i < questionsID.size(); i++) {
			result += questionsID.get(i);
			if (i < questionsID.size() - 1)
				result += ",";
		}
		return result;
	}

	/**
	 * 
	 * @return exam's question's scores in the format 30,20,10,.. etc (for db)
	 */
	public String getScoresToString() {
		String result = "";
		for (int i = 0; i < scores.size(); i++) {
			result += scores.get(i);
			if (i < scores.size() - 1)
				result += ",";
		}
		return result;
	}

	public void setBankID(String bankID) {
		this.bankID = bankID;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public ArrayList<String> getScores() {
		return scores;
	}

	public void setScores(ArrayList<String> scores) {
		this.scores = scores;
	}

	public ArrayList<String> getQuestionsID() {
		return questionsID;
	}

	public void setQuestionsID(ArrayList<String> questionsID) {
		this.questionsID = questionsID;
	}

	public String getExamID() {
		return ExamID;
	}

	public void setExamID(String examID) {
		ExamID = examID;
	}

	public int getDuration() {
		return Duration;
	}

	public void setDuration(int duration) {
		Duration = duration;
	}

	public String getExecutionCode() {
		return executionCode;
	}

	public void setExecutionCode(String executionCode) {
		this.executionCode = executionCode;
	}

	public Comment getTeacherComment() {
		return teacherComment;
	}

	public void setTeacherComment(Comment teacherComment) {
		this.teacherComment = teacherComment;
	}

	public Comment getStudentComment() {
		return studentComment;
	}

	public void setStudentComment(Comment studentComment) {
		this.studentComment = studentComment;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public ArrayList<QuestionInExam> getQuestions() {
		return Questions;
	}

	public void setQuestions(ArrayList<QuestionInExam> questions) {
		Questions = questions;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public int getExamType() {
		return examType;
	}

	public void setExamType(int examType) {
		this.examType = examType;
	}

	public int getEditMode() {
		return editMode;
	}

	public void setEditMode(int editMode) {
		this.editMode = editMode;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}
}