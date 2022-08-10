package Exam;

import java.util.ArrayList;
import java.util.Optional;
import Client.ClientUI;
import Client.MyClient;
import Data.Course;
import Data.Question;
import Data.QuestionInExam;
import Data.Subject;
import Login.MyClientBoundary;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * When teacher wants to add a new exam, this is the first screen she sees. Here
 * she chooses if the exam is online or offline. She picks a bank & course that
 * this exam will be associated with Online exam - and then adds questions with
 * scores. Offline exam - she uploads the exam.
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class AddExamsBoundary {
	@FXML
	private Button NextBtn;

	@FXML
	private Button logOutBtn;

	@FXML
	private Button QuestionBackBtn;

	@FXML
	private ComboBox<String> ExamBankComboBox;

	@FXML
	private TableView<Question> SelectQuestionTable;

	@FXML
	private TableColumn<Question, String> QuestionID;

	@FXML
	private TableColumn<Question, String> QuestionName;

	@FXML
	private ComboBox<String> CourseComboBox;

	@FXML
	private TableView<QuestionInExam> QuestionInExamTable;

	@FXML
	private TableColumn<QuestionInExam, String> QuestionsIDS;

	@FXML
	private TableColumn<QuestionInExam, String> QuestionsNameS;

	@FXML
	private TableColumn<QuestionInExam, Integer> QuestionsGrade;
	@FXML
	private ComboBox<String> ExamBankComboBox2;

	@FXML
	private ComboBox<String> CourseComboBox2;

	@FXML
	private Button AddBtn;

	@FXML
	private Button RemoveBtn;

	@FXML
	private Button UploadExamBtn;

	@FXML
	private RadioButton OnlineRadioBtn;

	@FXML
	private RadioButton OfflineRadioBtn;

	@FXML
	private Button changegradebtn;

	@FXML
	private Label lblIndetifier;

	public String temp;
	static String onOffExamType = "1";

	Question QuestionSelect;
	static ArrayList<QuestionInExam> questable = new ArrayList<>();
	static ObservableList<Question> data1;
	static ObservableList<Course> data2;
	static ObservableList<Subject> data3;
	static ObservableList<Subject> data4;
	static ObservableList<QuestionInExam> dataInExam;
	static String subj = null, course = "", questID = "", questScore = "", subjOF = "", courseOF = "", dur, exe, com1,
			com2;
	public static Stage pS;
	public static int flag = 0;

	/**
	 * initialize the GUI screen with the default elements
	 */
	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		onOffExamType = "1";

		OnlineRadioBtn.setSelected(true);
		ExamBankComboBox.setDisable(false);
		CourseComboBox.setDisable(false);
		ExamBankComboBox2.setDisable(true);
		CourseComboBox2.setDisable(true);
		ClientUI.chat.accept("questionBank_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		questID = "";
		questScore = "";
		dur = "";
		exe = "";
		com1 = "";
		com2 = "";
		flag = 0;
		MyClient.QuestionsInex.clear();
		data3 = FXCollections.observableArrayList(MyClient.subjects);
		ObservableList<String> list3 = FXCollections.observableArrayList();
		for (int i = 0; i < data3.size(); i++) {
			String str;
			str = data3.get(i).getSubjectName();
			list3.add(str);
		}
		ExamBankComboBox.setItems(list3);
		data4 = FXCollections.observableArrayList(MyClient.subjects1);
		ObservableList<String> list4 = FXCollections.observableArrayList();
		for (int i = 0; i < data4.size(); i++) {
			String str;
			str = data4.get(i).getSubjectName();
			list4.add(str);
		}
		ExamBankComboBox2.setItems(list4);
	}

	/**
	 * get questions from database via client
	 * 
	 * @param questions1 array list of questions that we got from the client to fill
	 *                   the questions table
	 */
	public static void getQuestions(ArrayList<Question> questions1) {
		data1 = FXCollections.observableArrayList(MyClient.Questions);
	}

	/**
	 * get courses from database via client
	 * 
	 * @param Courses1 array list of courses to fill the courses combo-box
	 */
	public static void getCourse(ArrayList<Course> Courses1) {
		data2 = FXCollections.observableArrayList(MyClient.courses);
	}

	/**
	 * opens a text dialog so the user can enter a score for the question when
	 * adding it to the exam
	 * 
	 * @return score input from user
	 */
	private Optional<String> getValueFromUser() {
		TextInputDialog dialog = new TextInputDialog("...");

		dialog.setTitle("Score Input");
		dialog.setHeaderText("Enter score:");
		dialog.setContentText("Score:");
		return dialog.showAndWait();
	}

	/**
	 * start the boundary window
	 * 
	 * @param primaryStage
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/AddExam.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Add exam form");
		primaryStage.setScene(scene);
		primaryStage.show();
		this.pS = primaryStage;

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {

				// consume event
				event.consume();

				// show close dialog
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Close Confirmation");
				alert.setHeaderText("Do you really want to quit?");
				alert.initOwner(primaryStage);

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					ClientUI.chat.client.quit();
					Platform.exit();
				}
			}
		});
	}

	/**
	 * method called when user picks a subject from the Bank combo-box, gets all
	 * courses under this subject for courses combo-box
	 * 
	 * @param event user picks a subject from the combo-box
	 */
	@FXML
	void subjChoice(ActionEvent event) {
		SelectQuestionTable.getItems().clear();
		QuestionInExamTable.getItems().clear();
		questable.clear();
		ClientUI.chat.accept("getCoursesUnderSubject&Teacher_" + ExamBankComboBox.getValue());

		data2 = FXCollections.observableArrayList(MyClient.courses);
		ObservableList<String> list2 = FXCollections.observableArrayList();

		for (int i = 0; i < data2.size(); i++) {
			String str;
			str = data2.get(i).getCourseName();
			list2.add(str);
		}

		CourseComboBox.setItems(list2);
		subj = ExamBankComboBox.getValue();
	}

	/**
	 * checks if user completed the process of this phase. if user did not complete
	 * the process, asks the user to complete it if user completed this phase OK,
	 * instantiates the next window
	 * 
	 * @param event user clicks Next button
	 */
	@FXML
	void NextButton(ActionEvent event) {
		questID = "";
		questScore = "";
		int counter = 0;
		for (int k = 0; k < questable.size(); k++)
			counter += questable.get(k).getScore();

		if (counter == 100) {
			for (int i = 0; i < questable.size(); i++) {
				questID += questable.get(i).getQuestionID() + " ";
				questScore += questable.get(i).getScore() + " ";
			}
			if (AddExam2Boundary.flag2 == 1) {
				dur = AddExam2Boundary.dur;
				exe = AddExam2Boundary.exe;
				com1 = AddExam2Boundary.commentsForS;
				com2 = AddExam2Boundary.commentsForT;
				flag = 1;
			}
			((Node) event.getSource()).getScene().getWindow().hide(); // hide window
			AddExam2Boundary ae2 = new AddExam2Boundary();
			Stage primaryStage = new Stage();
			try {
				ae2.start(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING, "Total exam grade is not 100!", ButtonType.OK);
			alert.showAndWait();
			counter = 0;
		}
		if (AddExam2Boundary.flag2 == 1) {
			dur = AddExam2Boundary.dur;
			exe = AddExam2Boundary.exe;
			com1 = AddExam2Boundary.commentsForS;
			com2 = AddExam2Boundary.commentsForT;
			flag = 1;
		}
	}

	/**
	 * get all questions under Bank and Course for the questions table
	 * 
	 * @param event user chose a course from the Course combo-box
	 */
	@FXML
	void QuestionGet(ActionEvent event) {
		SelectQuestionTable.getItems().clear();
		QuestionInExamTable.getItems().clear();
		questable.clear();
		if (CourseComboBox.getValue() != null) {
			ClientUI.chat.accept("getQuestions_" + ExamBankComboBox.getValue() + "_" + CourseComboBox.getValue());
			data1 = FXCollections.observableArrayList(MyClient.Questions);
			QuestionID.setCellValueFactory(new PropertyValueFactory<Question, String>("QuestionID"));
			QuestionName.setCellValueFactory(new PropertyValueFactory<Question, String>("questionText"));
			SelectQuestionTable.setItems(data1);
			course = CourseComboBox.getValue();
		}
	}

	/**
	 * instantiates the previous window
	 * 
	 * @param event user clicks Back button
	 */
	@FXML
	void Backbtn(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		flag = 0;
		AddExam2Boundary.flag();
		TeacherExamsBoundary teb = new TeacherExamsBoundary();
		Stage primaryStage = new Stage();
		try {
			teb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * adds question to questions in exam table
	 * 
	 * @param event user chose a question from the questions table and clicks Add
	 */
	@FXML
	void AddQuesbtn(ActionEvent event) {

		QuestionSelect = SelectQuestionTable.getSelectionModel().getSelectedItem();
		int flag = 0;
		for (int j = 0; j < questable.size(); j++) {
			if (QuestionSelect.getQuestionID().equals(questable.get(j).getQuestionID())) {
				Alert alert = new Alert(AlertType.WARNING, "Question Already in exam.", ButtonType.OK);
				alert.showAndWait();
				flag = 1;
			}
		}
		if (flag == 1)
			return;
		Optional<String> i = getValueFromUser();
		String questionGradetemp = i.get();
		if (!questionGradetemp.matches("[0-9]+")) {
			Alert alert = new Alert(AlertType.WARNING, "Question score must contain numbers only", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		int questionGrade = Integer.parseInt(questionGradetemp);
		QuestionsIDS.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("QuestionID"));
		QuestionsNameS.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("questionText"));
		QuestionsGrade.setCellValueFactory(new PropertyValueFactory<QuestionInExam, Integer>("score"));

		QuestionInExam questiontemp = new QuestionInExam(QuestionsNameS.toString(), QuestionsIDS.toString(),
				MyClientBoundary.usName, questionGrade);
		questiontemp.setQuestionID(QuestionSelect.getQuestionID());
		questiontemp.setQuestionText(QuestionSelect.getQuestionText());
		questiontemp.setScore(questionGrade);
		questiontemp.setAnswer1(QuestionSelect.getAnswer1());
		questiontemp.setAnswer2(QuestionSelect.getAnswer2());
		questiontemp.setAnswer3(QuestionSelect.getAnswer3());
		questiontemp.setAnswer4(QuestionSelect.getAnswer4());
		questiontemp.setCorrectAns(QuestionSelect.getCorrectAns());
		questable.add(questiontemp);
		dataInExam = FXCollections.observableArrayList(questable);
		MyClient.QuestionsInex.add(questiontemp);
		QuestionInExamTable.setItems(dataInExam);
	}

	/**
	 * removes question from exam
	 * 
	 * @param event user chose a question from the questions in exam table and
	 *              clicked Remove
	 */
	@FXML
	void RemoveQuestionBtn(ActionEvent event) {
		QuestionSelect = QuestionInExamTable.getSelectionModel().getSelectedItem();
		QuestionInExamTable.getItems().remove(QuestionSelect);
		MyClient.QuestionsInex.remove(QuestionSelect);
		questable.remove(QuestionSelect);
	}

	/**
	 * opens the offline exam options to user
	 * 
	 * @param event user picks Offline radio button
	 */
	@FXML
	void offlineExam(ActionEvent event) {
		onOffExamType = "0";
		OnlineRadioBtn.setSelected(false);
		OfflineRadioBtn.setSelected(true);
		ExamBankComboBox.setDisable(true);
		CourseComboBox.setDisable(true);
		ExamBankComboBox2.setDisable(false);
		CourseComboBox2.setDisable(false);
		QuestionInExamTable.setDisable(true);
		SelectQuestionTable.setDisable(true);
		AddBtn.setDisable(true);
		RemoveBtn.setDisable(true);
		;
		UploadExamBtn.setDisable(false);
		changegradebtn.setDisable(true);
		UploadExamBtn.setDisable(false);
	}

	/**
	 * opens the online exam options to user
	 * 
	 * @param event user picks Online radio button
	 */
	@FXML
	void onlineExam(ActionEvent event) {
		onOffExamType = "1";
		OnlineRadioBtn.setSelected(true);
		OfflineRadioBtn.setSelected(false);
		ExamBankComboBox.setDisable(false);
		CourseComboBox.setDisable(false);
		ExamBankComboBox2.setDisable(true);
		CourseComboBox2.setDisable(true);
		QuestionInExamTable.setDisable(false);
		SelectQuestionTable.setDisable(false);
		AddBtn.setDisable(false);
		RemoveBtn.setDisable(false);
		UploadExamBtn.setDisable(true);
		changegradebtn.setDisable(false);
		UploadExamBtn.setDisable(true);
	}

	/**
	 * changes score to question in exam to new score given by user
	 * 
	 * @param event user chose a question from the questions in exam table and
	 *              clicked Change Score button
	 */
	@FXML
	void changegrade(ActionEvent event) {
		QuestionSelect = QuestionInExamTable.getSelectionModel().getSelectedItem();
		int index = QuestionInExamTable.getSelectionModel().getSelectedIndex();
		Optional<String> i = getValueFromUser();
		String questionGradetemp = i.get();
		QuestionInExamTable.getItems().remove(QuestionSelect);
		questable.remove(QuestionSelect);
		int questionGrade = Integer.parseInt(questionGradetemp);
		QuestionsIDS.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("QuestionID"));
		QuestionsNameS.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("questionText"));
		QuestionsGrade.setCellValueFactory(new PropertyValueFactory<QuestionInExam, Integer>("score"));
		QuestionInExam questiontemp = new QuestionInExam(QuestionsNameS.toString(), QuestionsIDS.toString(),
				MyClientBoundary.usName, questionGrade);
		questiontemp.setQuestionID(QuestionSelect.getQuestionID());
		questiontemp.setQuestionText(QuestionSelect.getQuestionText());
		questiontemp.setAnswer1(QuestionSelect.getAnswer1());
		questiontemp.setAnswer2(QuestionSelect.getAnswer2());
		questiontemp.setAnswer3(QuestionSelect.getAnswer3());
		questiontemp.setAnswer4(QuestionSelect.getAnswer4());
		questiontemp.setChosenAns(QuestionSelect.getCorrectAns());
		questiontemp.setScore(questionGrade);
		questable.add(index, questiontemp);
		dataInExam = FXCollections.observableArrayList(questable);
		QuestionInExamTable.setItems(dataInExam);
	}

	/**
	 * opens user's filesystem & uploads it
	 * 
	 * @param event user clicks upload button for offline exam
	 */
	@FXML
	void upload(ActionEvent event) {
		courseOF = CourseComboBox2.getValue();
		if (subjOF.length() == 0) {
			Alert alert = new Alert(AlertType.WARNING, "choose Subject!", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		if (courseOF == null) {
			Alert alert = new Alert(AlertType.WARNING, "choose course!", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		if (OfflineRadioBtn.isSelected()) {
			subjOF = ExamBankComboBox2.getValue();
			courseOF = CourseComboBox2.getValue();
			((Node) event.getSource()).getScene().getWindow().hide(); // hide window
			offlineExam ae = new offlineExam();
			Stage primaryStage = new Stage();
			ae.start(primaryStage);
		} else {
			Alert alert = new Alert(AlertType.WARNING, "choose Offline Exam", ButtonType.OK);
			alert.showAndWait();
		}
	}

	/**
	 * user logs out of CEMS, back to main login screen
	 * 
	 * @param event user clicks Logout button
	 */
	@FXML
	void logout(ActionEvent event) {
		ClientUI.chat.accept("logout_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		if (MyClient.statusINOUT.equals("logout")) {
			((Node) event.getSource()).getScene().getWindow().hide();
			MyClientBoundary login = new MyClientBoundary();
			try {
				Stage primaryStage = new Stage();
				login.start(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * gets all courses associated with chosen bank to Course combo-box
	 * 
	 * @param event user chooses a subject from the offline exam Bank combo-box
	 */
	@FXML
	void subjcet(ActionEvent event) {
		ClientUI.chat.accept("getCoursesUnderSubject&Teacher_" + ExamBankComboBox2.getValue());
		subjOF = ExamBankComboBox2.getValue();
		data2 = FXCollections.observableArrayList(MyClient.courses);
		ObservableList<String> list2 = FXCollections.observableArrayList();

		for (int i = 0; i < data2.size(); i++) {
			String str;
			str = data2.get(i).getCourseName();
			list2.add(str);
		}
		CourseComboBox2.setItems(list2);
	}

	/**
	 * updates course name for exam
	 * 
	 * @param event user chooses a course from the offline exam Course combo-box
	 */
	@FXML
	void CourseCho(ActionEvent event) {
		courseOF = CourseComboBox2.getValue();
	}
}