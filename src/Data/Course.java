package Data;

/**
 * course is comprised of course code (2 digits) and corresponding course name
 * (e.g. algebra)
 * 
 * @author Ayala Cohen
 *
 */
public class Course {
	private String courseName = "";
	private String courseCode = "";
	private String SubjectCode = "";
	private String SubjectName="";

	public String getSubjectName() {
		return SubjectName;
	}

	public void setSubjectName(String subjectName) {
		SubjectName = subjectName;
	}

	public Course() {}
	
	public Course(String courseName, String courseCode, String SubjectCode) {
		super();
		this.courseName = courseName;
		this.courseCode = courseCode;
		this.SubjectCode = SubjectCode;
	}
	
	public Course(String courseName) {
		super();
		this.courseName = courseName;
	}

	public Course(String courseName, String courseCode) {
		this.courseName = courseName;
		this.courseCode = courseCode;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getSubjectCode() {
		return SubjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		SubjectCode = subjectCode;
	}
}