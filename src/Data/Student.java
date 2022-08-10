package Data;

import java.util.ArrayList;
/**
 * student that uses cems system
 * @author Ayala Cohen
 *
 */
public class Student extends Person {
	private ArrayList<Exam> doneExams = new ArrayList<>();

	public Student(String id, String name, String password, String username, String surname, String email,
			Status userStatus) {
		super(id, name, password, username, surname, email, userStatus);
	}
	
	public Student() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Exam> getDoneExams() {
		return doneExams;
	}

	public void setDoneExams(ArrayList<Exam> doneExams) {
		this.doneExams = doneExams;
	}
}
