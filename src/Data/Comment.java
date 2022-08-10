package Data;

/**
 * Comment for exam Can be visible for students or for teacher only
 * 
 * @author Ayala Cohen
 *
 */
public class Comment {
	public static enum Type {
		ForTeacher, ForStudent
	}

	private Type commentType;
	String freeText = "";

	public Type getCommentType() {
		return commentType;
	}

	public void setCommentType(Type commentType) {
		this.commentType = commentType;
	}

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public Comment(Type commentType, String freeText) {
		super();
		this.commentType = commentType;
		this.freeText = freeText;
	}
}