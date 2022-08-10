package Data;

/**
 * each question in an exam has a score
 * 
 * @author Ayala Cohen
 *
 */
public class QuestionInExam extends Question {
	int score, chosenAns;
	int actualScore;
	public int getActualScore() {
		return actualScore;
	}
	public void setActualScore(int actualScore) {
		this.actualScore = actualScore;
	}
	public int getChosenAns() {
		return chosenAns;
	}
	public void setChosenAns(int chosenAns) {
		this.chosenAns = chosenAns;
	}
	public QuestionInExam(String questionText, String questionID, String authorName, int score) {
		super(questionText, questionID, authorName);
		this.score = score;
	}
	public QuestionInExam() {
		
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}