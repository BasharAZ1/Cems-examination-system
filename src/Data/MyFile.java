package Data;

import java.io.Serializable;

/**
 * 
 * @author yarden this class represent an offline exam files (upload, download)
 */
public class MyFile implements Serializable {
	public static enum FileSource {
		TeacherAddOfflineExam, StudentSubmitOfflineExam, CopyForStudent
	}

	private static final long serialVersionUID = 1L;
	private String Description = null;
	private String fileName, ExecutionCode, SubjectName, CourseName, Username, Password;
	private int duration;
	private int size = 0;
	FileSource fileSource;
	private int grade;

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "MyFile [Description=" + Description + ", fileName=" + fileName + ", ExecutionCode=" + ExecutionCode
				+ ", SubjectName=" + SubjectName + ", CourseName=" + CourseName + ", Username=" + Username
				+ ", Password=" + Password + ", duration=" + duration + ", fileSource=" + fileSource + "]";
	}

	public FileSource getFileSource() {
		return fileSource;
	}

	public void setFileSource(FileSource fileSource) {
		this.fileSource = fileSource;
	}

	public String getExecutionCode() {
		return ExecutionCode;
	}

	public void setExecutionCode(String executionCode) {
		ExecutionCode = executionCode;
	}

	public String getSubjectName() {
		return SubjectName;
	}

	public void setSubjectName(String subjectName) {
		SubjectName = subjectName;
	}

	public String getCourseName() {
		return CourseName;
	}

	public void setCourseName(String courseName) {
		CourseName = courseName;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public byte[] mybytearray;

	public void initArray(int size) {
		mybytearray = new byte[size];
	}

	public MyFile(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public byte[] getMybytearray() {
		return mybytearray;
	}

	public byte getMybytearray(int i) {
		return mybytearray[i];
	}

	public void setMybytearray(byte[] mybytearray) {

		for (int i = 0; i < mybytearray.length; i++)
			this.mybytearray[i] = mybytearray[i];
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public boolean isPrefixValid() {
		if (fileName == null)
			return false;
		if (!fileName.toLowerCase().endsWith(".docx")) {
			return false;
		}
		return true;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
}
