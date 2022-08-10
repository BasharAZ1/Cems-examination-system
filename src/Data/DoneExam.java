package Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * represents a done exam that was submitted by student
 * 
 * @author G6
 *
 */
public class DoneExam extends Exam {
	
	private String StudentID = "";
	private String StudentName = "";
	private String CopyStatus="";
	public String getCopyStatus() {
		return CopyStatus;
	}

	public void setCopyStatus(String copyStatus) {
		CopyStatus = copyStatus;
	}

	private LocalDate Date;
	private LocalDateTime Time;
	private String TimeOfsub;
	private int OriginalDuration, ActualDuration, NumStudentsStarted;
	private int NumStudentsFinishedSuccessful, NumStudentFinishedUnsuccessful, Grade;
	private int SysGrade, GradeStatus;
	private String GradeChangeRationale, TeacherComments;
	private String DBanswer = ""; /* exam answers in format: 1,2,3,.. */
	int finishedSuccessful; /* finished successfully-1, finished unsuccessfully-0 */
	String formatDateTime;

	public String getFormatDateTime() {
		return formatDateTime;
	}

	public int getFinishedSuccessful() {
		return finishedSuccessful;
	}

	public void setFinishedSuccessful(int finishedSuccessful) {
		this.finishedSuccessful = finishedSuccessful;
	}

	public DoneExam() {
		super();
	}

	public String getStudentName() {
		return StudentName;
	}

	public String getDBanswer() {
		return DBanswer;
	}

	public void setDBanswer(String dBanswer) {
		DBanswer = dBanswer;
	}

	public void setStudentName(String studentName) {
		StudentName = studentName;
	}

	public String getStudentID() {
		return StudentID;
	}

	public void setStudentID(String studentID) {
		StudentID = studentID;
	}

	public LocalDate getDate() {
		return Date;
	}

	public void setDate(LocalDate date) {
		Date = date;
	}

	public LocalDateTime getTime() {
		return Time;
	}

	public void setTime(LocalDateTime time) {
		Time = time;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		formatDateTime = Time.format(formatter);
	}

	public int getOriginalDuration() {
		return OriginalDuration;
	}

	public void setOriginalDuration(int originalDuration) {
		OriginalDuration = originalDuration;
	}

	public int getActualDuration() {
		return ActualDuration;
	}

	public void setActualDuration(int actualDuration) {
		ActualDuration = actualDuration;
	}

	public int getNumStudentsStarted() {
		return NumStudentsStarted;
	}

	public void setNumStudentsStarted(int numStudentsStarted) {
		NumStudentsStarted = numStudentsStarted;
	}

	public int getNumStudentsFinishedSuccessful() {
		return NumStudentsFinishedSuccessful;
	}

	public void setNumStudentsFinishedSuccessful(int numStudentsFinishedSuccessful) {
		NumStudentsFinishedSuccessful = numStudentsFinishedSuccessful;
	}

	public int getNumStudentFinishedUnsuccessful() {
		return NumStudentFinishedUnsuccessful;
	}

	public void setNumStudentFinishedUnsuccessful(int numStudentFinishedUnsuccessful) {
		NumStudentFinishedUnsuccessful = numStudentFinishedUnsuccessful;
	}

	public int getGrade() {
		return Grade;
	}

	public void setGrade(int grade) {
		Grade = grade;
	}

	public int getSysGrade() {
		return SysGrade;
	}

	public void setSysGrade(int sysGrade) {
		SysGrade = sysGrade;
	}

	public int getGradeStatus() {
		return GradeStatus;
	}

	public void setGradeStatus(int gradeStatus) {
		GradeStatus = gradeStatus;
	}

	public String getGradeChangeRationale() {
		return GradeChangeRationale;
	}

	public void setGradeChangeRationale(String gradeChangeRationale) {
		GradeChangeRationale = gradeChangeRationale;
	}

	public String getTeacherComments() {
		return TeacherComments;
	}

	public void setTeacherComments(String teacherComments) {
		TeacherComments = teacherComments;
	}

	public String getTimeOfsub() {
		return TimeOfsub;
	}

	public void setTimeOfsub(String timeOfsub) {
		TimeOfsub = timeOfsub;
	}
}
