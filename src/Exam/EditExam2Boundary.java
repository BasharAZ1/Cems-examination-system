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
import javafx.scene.control.Button;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * start second phase of editing exam GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class EditExam2Boundary {
	@FXML
	private Label AddExamLabel;

	@FXML
	private Button NextBtn;

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
	private Label SelectQuestionLabel;

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
	private Button AddBtn;

	@FXML
	private Label lblIndetifier;

	@FXML
	private Label QuestionInExamLabel;

	@FXML
	private Button RemoveBtn;

	@FXML
	private Label computingExamLabel;

	@FXML
	private RadioButton OnlineRadioBtn;

	@FXML
	private Button Changebtn;

	public String temp;
	boolean noSave = false;
	Question QuestionSelect;
	QuestionInExam Selected;
	public static ArrayList<QuestionInExam> questableEditAL = new ArrayList<>();
	static ObservableList<Question> data1ForEdit;
	static ObservableList<Course> data2ForEdit;
	static ObservableList<Subject> data3ForEdit;
	static ObservableList<QuestionInExam> dataInEditExam;
	static String subjEdit = null, courseEditExam = "", questIDEdit = "", questScoreEdit = "", durEdit, exeEdit,
			com1Edit, com2Edit;
	public static Stage pSEditExam;
	public static int flagForEdit = 0;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		ClientUI.chat.accept("questionBank_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		ClientUI.chat
				.accept("getExam_" + MyClient.examsForEdit.get(EditExam1Boundary.selectedExamIndexToEdit).getExamID());

		OnlineRadioBtn.setSelected(true);
		CourseComboBox.setValue(MyClient.exam2.getCourseName());
		subjEdit = MyClient.exam2.getSubjectName();
		courseEditExam = MyClient.exam2.getCourseName();
		ExamBankComboBox.setValue(MyClient.exam2.getSubjectName());
		ClientUI.chat.accept("getQuestions_" + MyClient.exam2.getSubjectName() + "_" + MyClient.exam2.getCourseName());
		data1ForEdit = FXCollections.observableArrayList(MyClient.Questions);
		QuestionID.setCellValueFactory(new PropertyValueFactory<Question, String>("QuestionID"));
		QuestionName.setCellValueFactory(new PropertyValueFactory<Question, String>("questionText"));
		SelectQuestionTable.setItems(data1ForEdit);

		questableEditAL = MyClient.QuestionsInex;
		for (int i = 0; i < data1ForEdit.size(); i++) {
			for (int j = 0; j < questableEditAL.size(); j++) {
				if (data1ForEdit.get(i).getQuestionID().equals(questableEditAL.get(j).getQuestionID())) {
					questableEditAL.get(j).setQuestionText(data1ForEdit.get(i).getQuestionText());
					questableEditAL.get(j).setAnswer1(data1ForEdit.get(i).getAnswer1());
					questableEditAL.get(j).setAnswer2(data1ForEdit.get(i).getAnswer2());
					questableEditAL.get(j).setAnswer3(data1ForEdit.get(i).getAnswer3());
					questableEditAL.get(j).setAnswer4(data1ForEdit.get(i).getAnswer4());
					questableEditAL.get(j).setCorrectAns(data1ForEdit.get(i).getCorrectAns());

				}
			}
		}
		dataInEditExam = FXCollections.observableArrayList(questableEditAL);
		for (int i = 0; i < data1ForEdit.size(); i++) {
			for (int j = 0; j < dataInEditExam.size(); j++) {
				if (dataInEditExam.get(j).getQuestionID().equals(data1ForEdit.get(i).getQuestionID())) {
					String str1, str2;
					int score;
					str1 = dataInEditExam.get(j).getQuestionID();
					score = dataInEditExam.get(j).getScore();
					str2 = data1ForEdit.get(i).getQuestionText();
					QuestionInExam ques = new QuestionInExam(str2, str1, MyClientBoundary.usName, score);

					dataInEditExam.set(j, ques);
				}
			}
		}
		QuestionsIDS.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("QuestionID"));
		QuestionsNameS.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("questionText"));
		QuestionsGrade.setCellValueFactory(new PropertyValueFactory<QuestionInExam, Integer>("score"));
		QuestionInExamTable.setItems(dataInEditExam);
	}

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/EditExam1.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Add exam form");
		primaryStage.setScene(scene);
		primaryStage.show();
		pSEditExam = primaryStage;
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
	 * get questions from database via client
	 * 
	 * @param questions1 array list of questions that we got from the client to fill
	 *                   the questions table
	 */
	public static void getQuestions(ArrayList<Question> questions1) {
		data1ForEdit = FXCollections.observableArrayList(MyClient.Questions);
	}

	/**
	 * get courses from database via client
	 * 
	 * @param Courses1 array list of courses to fill the courses combo-box
	 */
	public static void getCourse(ArrayList<Course> Courses1) {
		data2ForEdit = FXCollections.observableArrayList(MyClient.courses);
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
		questableEditAL.clear();
		ClientUI.chat.accept("getCoursesUnderSubject&Teacher_" + ExamBankComboBox.getValue());

		data2ForEdit = FXCollections.observableArrayList(MyClient.courses);
		ObservableList<String> list2 = FXCollections.observableArrayList();

		for (int i = 0; i < data2ForEdit.size(); i++) {
			String str;
			str = data2ForEdit.get(i).getCourseName();
			list2.add(str);
		}

		CourseComboBox.setItems(list2);
		subjEdit = ExamBankComboBox.getValue();
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
		questIDEdit = "";
		questScoreEdit = "";
		int counter = 0;
		for (int k = 0; k < questableEditAL.size(); k++)
			counter += questableEditAL.get(k).getScore();
		if (counter == 100) {
			for (int i = 0; i < questableEditAL.size(); i++) {
				questIDEdit += questableEditAL.get(i).getQuestionID() + " ";
				questScoreEdit += questableEditAL.get(i).getScore() + " ";
			}
			if (EditExam3Boundary.flag1 == 1) {
				durEdit = EditExam3Boundary.durEditExam;
				exeEdit = EditExam3Boundary.exeEditExam;
				com1Edit = EditExam3Boundary.commentsForSEditExam;
				com2Edit = EditExam3Boundary.commentsForTEditExam;
				flagForEdit = 1;
			}
			((Node) event.getSource()).getScene().getWindow().hide(); // hide window
			EditExam3Boundary ee3 = new EditExam3Boundary();
			Stage primaryStage = new Stage();
			try {
				ee3.start(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING, "Total exam grade is not 100!", ButtonType.OK);
			alert.showAndWait();
			counter = 0;
		}
		if (EditExam3Boundary.flag1 == 1) {
			durEdit = EditExam3Boundary.durEditExam;
			exeEdit = EditExam3Boundary.exeEditExam;
			com1Edit = EditExam3Boundary.commentsForSEditExam;
			com2Edit = EditExam3Boundary.commentsForTEditExam;
			flagForEdit = 1;

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
		questableEditAL.clear();
		if (CourseComboBox.getValue() != null) {
			ClientUI.chat.accept("getQuestions_" + ExamBankComboBox.getValue() + "_" + CourseComboBox.getValue());
			data1ForEdit = FXCollections.observableArrayList(MyClient.Questions);
			QuestionID.setCellValueFactory(new PropertyValueFactory<Question, String>("QuestionID"));
			QuestionName.setCellValueFactory(new PropertyValueFactory<Question, String>("questionText"));
			SelectQuestionTable.setItems(data1ForEdit);

			courseEditExam = CourseComboBox.getValue();
		}
	}

	/**
	 * instantiates the previous window
	 * 
	 * @param event user clicks Back button
	 */
	@FXML
	void Backbtn(ActionEvent event) {
		if (noSave == false) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Go to previous page without saving?\nAre you sure?",
					ButtonType.YES, ButtonType.NO);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.NO)
				return;

			((Node) event.getSource()).getScene().getWindow().hide(); // hide window
			flagForEdit = 0;
			EditExam3Boundary.flag();
			EditExam1Boundary ed1 = new EditExam1Boundary();
			Stage primaryStage = new Stage();
			try {
				ed1.start(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * choose score for question in exam
	 * 
	 * @return chosen score from user input
	 */
	private Optional<String> getValueFromUser() {
		TextInputDialog dialog = new TextInputDialog("...");

		dialog.setTitle("Score Input");
		dialog.setHeaderText("Enter score:");
		dialog.setContentText("Score:");
		return dialog.showAndWait();
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
		for (int j = 0; j < questableEditAL.size(); j++) {
			if (QuestionSelect.getQuestionID().equals(questableEditAL.get(j).getQuestionID())) {
				Alert alert = new Alert(AlertType.WARNING, "Question already in exam.", ButtonType.OK);
				alert.showAndWait();
				flag = 1;
			}
		}
		if (flag == 1)
			return;
		Optional<String> i = getValueFromUser();
		String questionGradetemp = i.get();
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
		questableEditAL.add(questiontemp);
		dataInEditExam = FXCollections.observableArrayList(questableEditAL);
		QuestionInExamTable.setItems(dataInEditExam);
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
		int f = QuestionInExamTable.getSelectionModel().getSelectedIndex();
		questableEditAL.remove(f);
		QuestionInExamTable.getItems().remove(f);
		MyClient.QuestionsInex.remove(QuestionSelect);
	}

	/**
	 * changes score to question in exam to new score given by user
	 * 
	 * @param event user chose a question from the questions in exam table and
	 *              clicked Change Score button
	 */
	@FXML
	void changeGrade(ActionEvent event) {
		QuestionSelect = QuestionInExamTable.getSelectionModel().getSelectedItem();
		int index = QuestionInExamTable.getSelectionModel().getSelectedIndex();
		Optional<String> i = getValueFromUser();
		String questionGradetemp = i.get();

		questableEditAL.remove(index);
		int questionGrade = Integer.parseInt(questionGradetemp);
		QuestionsIDS.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("QuestionID"));
		QuestionsNameS.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("questionText"));
		QuestionsGrade.setCellValueFactory(new PropertyValueFactory<QuestionInExam, Integer>("score"));
		QuestionInExam questiontemp = new QuestionInExam(QuestionsNameS.toString(), QuestionsIDS.toString(),
				MyClientBoundary.usName, questionGrade);
		questiontemp.setQuestionID(QuestionSelect.getQuestionID());
		questiontemp.setQuestionText(QuestionSelect.getQuestionText());
		for (int j = 0; j < data1ForEdit.size(); j++) {
			if (questiontemp.getQuestionID().equals(data1ForEdit.get(j).getQuestionID())) {
				questiontemp.setQuestionText(data1ForEdit.get(j).getQuestionText());
				questiontemp.setAnswer1(data1ForEdit.get(j).getAnswer1());
				questiontemp.setAnswer2(data1ForEdit.get(j).getAnswer2());
				questiontemp.setAnswer3(data1ForEdit.get(j).getAnswer3());
				questiontemp.setAnswer4(data1ForEdit.get(j).getAnswer4());
				questiontemp.setCorrectAns(data1ForEdit.get(j).getCorrectAns());
			}
		}
		questableEditAL.add(index, questiontemp);
		dataInEditExam = FXCollections.observableArrayList(questableEditAL);
		QuestionInExamTable.setItems(dataInEditExam);
	}

	/**
	 * logout, return to login GUI
	 * 
	 * @param event user clicks logout button
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
}