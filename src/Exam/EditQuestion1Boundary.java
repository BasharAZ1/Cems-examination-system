package Exam;

import java.util.ArrayList;
import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
import Data.Course;
import Data.Question;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * start edit question first phase GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class EditQuestion1Boundary {
	@FXML
	private Button logoutEditQuestBtn;

	@FXML
	private TableView<Question> QuestionsTable;

	@FXML
	private TableColumn<Question, String> EditQuestID;

	@FXML
	private TableColumn<Question, String> EditQuestName;

	@FXML
	private ComboBox<String> EditCourseList;

	@FXML
	private ComboBox<String> EditSubList;

	@FXML
	private Button editEditQ1Btn;

	@FXML
	private Button backEditQ1Btn;
	@FXML
	private Button deleteQuestionBtn;

	@FXML
	private Button deleteBankBtn;

	@FXML
	private Label lblIndetifier;

	Question QuestionSelect;
	static String str1, Bankname;
	static ObservableList<Question> data1;
	static ObservableList<Course> data2;
	static ObservableList<Subject> data3;
	static String subj, course, questID, questScore;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		ClientUI.chat.accept("questionBank_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);

		data3 = FXCollections.observableArrayList(MyClient.subjects);
		ObservableList<String> list3 = FXCollections.observableArrayList();
		for (int i = 0; i < data3.size(); i++) {
			String str;
			str = data3.get(i).getSubjectName();
			list3.add(str);
		}
		EditSubList.setItems(list3);
		EditSubList.setValue("Choose subject:");

		getQuestions(MyClient.Questions);
	}

	/**
	 * set questions observable of data base
	 * 
	 * @param questions1
	 */
	public static void getQuestions(ArrayList<Question> questions1) {
		data1 = FXCollections.observableArrayList(MyClient.Questions);
	}

	/**
	 * set courses observable of data base
	 * 
	 * @param Courses1
	 */
	public static void getCourse(ArrayList<Course> Courses1) {
		data2 = FXCollections.observableArrayList(MyClient.courses);
	}

	/**
	 * subject combo box
	 * 
	 * @param event user chooses subject from combo box
	 */
	@FXML
	void subjChoice(ActionEvent event) {
		ClientUI.chat.accept("getCoursesUnderSubject&Teacher_" + EditSubList.getValue());
		Bankname = EditSubList.getValue();
		data2 = FXCollections.observableArrayList(MyClient.courses);
		ObservableList<String> list2 = FXCollections.observableArrayList();

		for (int i = 0; i < data2.size(); i++) {
			String str;
			str = data2.get(i).getCourseName();
			list2.add(str);
		}
		EditCourseList.setItems(list2);
		subj = EditSubList.getValue();
	}

	/**
	 * questions combo box
	 * 
	 * @param event user chooses course from combo box
	 */
	@FXML
	void QuestionGet(ActionEvent event) {
		if (EditCourseList.getValue() != null) {
			ClientUI.chat.accept("getQuestions_" + EditSubList.getValue() + "_" + EditCourseList.getValue());
			data1 = FXCollections.observableArrayList(MyClient.Questions);
			if (MyClient.noQue == "decline") {
				QuestionsTable.getItems().removeAll(data1);
				MyClient.noQue = "";
				return;
			}
			EditQuestID.setCellValueFactory(new PropertyValueFactory<Question, String>("questionID"));
			EditQuestName.setCellValueFactory(new PropertyValueFactory<Question, String>("questionText"));
			QuestionsTable.setItems(data1);

			course = EditCourseList.getValue();
		}
	}

	/**
	 * start edit questions first phase GUI
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage is unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/EditQuestion1.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Add exam form");
		primaryStage.setScene(scene);
		primaryStage.show();

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
	 * go to teacher main questions GUI
	 * 
	 * @param event user click back button
	 */
	@FXML
	void back(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		TeacherQuestionsBoundary tqb = new TeacherQuestionsBoundary();
		Stage primaryStage = new Stage();
		try {
			tqb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * go to second phase of editing question GUI
	 * 
	 * @param event user clicks next button
	 */
	@FXML
	void next(ActionEvent event) {
		QuestionSelect = QuestionsTable.getSelectionModel().getSelectedItem();
		if (QuestionSelect != null) {
			str1 = QuestionSelect.getQuestionID();
			((Node) event.getSource()).getScene().getWindow().hide();
			EditQuestion2Boundary qb = new EditQuestion2Boundary();
			Stage primaryStage = new Stage();
			try {
				qb.start(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * delete questions under chosen bank
	 * 
	 * @param event user clicks delete bank button
	 */
	@FXML
	void deletBank(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this bank?", ButtonType.YES,
				ButtonType.NO);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.NO)
			return;
		ClientUI.chat.accept("deleteQuestionsUnderQuestionBankAndCourse_" + EditSubList.getValue() + "_"
				+ EditCourseList.getValue());
		if (MyClient.canDeleteTable.equals("no")) {
			alert = new Alert(AlertType.WARNING, "There are questions which belong to exams, \ncannot be deleted before deleting them from the exam itself.", ButtonType.OK);
			alert.showAndWait();
			MyClient.canDeleteTable = "";
			return;
		}
		for (int i = 0; i < QuestionsTable.getItems().size(); i++) {
			QuestionsTable.getItems().clear();
		}
	}

	/**
	 * delete chosen question
	 * 
	 * @param event user clicks delete question button
	 */
	@FXML
	void deletQuestion(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this question?",
				ButtonType.YES, ButtonType.NO);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.NO)
			return;
		QuestionSelect = QuestionsTable.getSelectionModel().getSelectedItem();
		ClientUI.chat.accept("deleteQuestion_" + QuestionSelect.getQuestionID());
		if (QuestionSelect != null) {
			if (MyClient.queDeleteUnsucc.equals("decline"))
				return;
			QuestionsTable.getItems().remove(QuestionSelect);
		}
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