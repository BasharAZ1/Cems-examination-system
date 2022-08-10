package Data;

/**
 * answer can be right or wrong & holds the answer text
 * 
 * @author Ayala Cohen
 *
 */
public class Answer {
	public static enum Type {
		CorrectAns, IncorrectAns
	}

	private String answerText = "";
	Type ansType;

	public Answer(Type ansType, String answerText) {
		super();
		this.ansType = ansType;
		this.answerText = answerText;
	}

	public Answer(String answerText) {
		super();
		this.answerText = answerText;
		this.ansType = Type.IncorrectAns;
	}

	public Type getAnsType() {
		return ansType;
	}

	public void setAnsType(Type ansType) {
		this.ansType = ansType;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
}