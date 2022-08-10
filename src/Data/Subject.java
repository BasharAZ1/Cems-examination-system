package Data;

/**
 * Subject comprised out of subject name (e.g. math) and subject ID (e.g. 02)
 * 
 * @author Ayala Cohen
 *
 */
public class Subject {
	String subjectName = "";
	String subjectID = "";

	public Subject(String subjectName, String subjectID) {
		super();
		this.subjectName = subjectName;
		this.subjectID = subjectID;
	}
	
	public Subject(String subjectName) {
		super();
		this.subjectName = subjectName;
	}
	
	public Subject() {
		super();
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}

	@Override
	public String toString() {
		return "Subject [subjectName=" + subjectName + ", subjectID=" + subjectID + "]";
	}
	
}