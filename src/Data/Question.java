package Data;

import java.util.ArrayList;

/**
 * Question comprised of question text (the question itself), question's ID and
 * author of the question (teacher ID)
 * 
 * @author Ayala Cohen
 *
 */
public class Question {
	String questionText = "";
	String questionID = "";
	String authorID = "";
	Answer answer1, answer2, answer3, answer4;
	String qBankName;
	String qBankID;
	String qNum;
	int correctAns;
	
	
	private ArrayList<Course> CourseIDs;
	private String courseIDsString="";
	
	public String getqNum() {
		return qNum;
	}

	public void setqNum(String qNum) {
		this.qNum = qNum;
	}

	
	
	public String getCourseIDsString() {
		return courseIDsString;
	}

	public void setCourseIDsString(String courseIDsString) {
		this.courseIDsString = courseIDsString;
	}

	public ArrayList<Course> getCourseIDs() {
		return CourseIDs;
	}

	public void setCourseIDs(ArrayList<Course> courseIDs) {
		CourseIDs = courseIDs;
	}

	public String getAuthorID() {
		return authorID;
	}

	public void setAuthorID(String authorID) {
		this.authorID = authorID;
	}

	public String getqBankID() {
		return qBankID;
	}

	public void setqBankID(String qBankID) {
		this.qBankID = qBankID;
	}

	

	public Question() {
	}

	public Question(String questionText, String questionID, String authorName) {
		this.questionText = questionText;
		this.questionID = questionID;
		this.authorID = authorName;
	}

	public String getqBankName() {
		return qBankName;
	}

	public void setqBankName(String qBankName) {
		this.qBankName = qBankName;
	}

	public int getCorrectAns() {
		return correctAns;
	}

	public void setCorrectAns(int correctAns) {
		this.correctAns = correctAns;
	}

	public String getQuestionText() {
		return questionText;
	}

	public Answer getAnswer1() {
		return answer1;
	}

	public void setAnswer1(Answer answer1) {
		this.answer1 = answer1;
	}

	public Answer getAnswer2() {
		return answer2;
	}

	public void setAnswer2(Answer answer2) {
		this.answer2 = answer2;
	}

	public Answer getAnswer3() {
		return answer3;
	}

	public void setAnswer3(Answer answer3) {
		this.answer3 = answer3;
	}

	public Answer getAnswer4() {
		return answer4;
	}

	public void setAnswer4(Answer answer4) {
		this.answer4 = answer4;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getQuestionID() {
		return questionID;
	}

	public void setQuestionID(String questionID) {
		this.questionID = questionID;
	}

	@Override
	public String toString() {
		return "Question [questionText=" + questionText + ", questionID=" + questionID + ", authorID=" + authorID
				+ ", answer1=" + answer1 + ", answer2=" + answer2 + ", answer3=" + answer3 + ", answer4=" + answer4
				+ ", qBankName=" + qBankName + ", qBankID=" + qBankID + ", qNum=" + qNum + ", CourseIDs=" + CourseIDs
				+ ", correctAns=" + correctAns + "]";
	}

}