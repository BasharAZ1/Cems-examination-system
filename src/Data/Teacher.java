package Data;

import java.util.ArrayList;

/**
 * teacher has question banks and exam banks
 * @author G6
 *
 */
public class Teacher extends Person {
	ArrayList<String> questionBanks;
	ArrayList<String> examBanks;
	public ArrayList<String> getExamBanks() {
		return examBanks;
	}

	public void setExamBanks(ArrayList<String> examBanks) {
		this.examBanks = examBanks;
	}

	String DBqbanks, DBexamBanks;
	
	public String getDBexamBanks() {
		return DBexamBanks;
	}

	public void setDBexamBanks(String dBexamBanks) {
		DBexamBanks = dBexamBanks;
	}

	public String getDBqbanks() {
		return DBqbanks;
	}

	public void setDBqbanks(String dBqbanks) {
		DBqbanks = dBqbanks;
	}

	public ArrayList<String> getQuestionBanks() {
		return questionBanks;
	}

	public void setQuestionBanks(ArrayList<String> questionBanks) {
		this.questionBanks = questionBanks;
	}
}
