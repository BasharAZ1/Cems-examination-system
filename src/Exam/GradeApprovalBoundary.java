package Exam;

import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
import Data.Course;
import Data.Exam;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * start first screen of grade approval GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class GradeApprovalBoundary {
	@FXML
	private Button logOutBn;

	@FXML
	private TableView<Exam> ExamSelect;

	@FXML
	private TableColumn<Exam, String> ExamId;

	@FXML
	private TableColumn<Exam, String> Subject;

	@FXML
	private TableColumn<Exam, String> Course;

	@FXML
	private TableColumn<Exam, String> executionCode;

	@FXML
	private ComboBox<String> SubjectList;

	@FXML
	private ComboBox<String> CourseList;

	@FXML
	private Button nextBtn;

	@FXML
	private Button backBtn;

	@FXML
	private Label lblIndetifier;

	public static String Id;
	public static boolean prevSubCou = false;
	public static String subjectGradeAppString, courseGradeAppString;
	static ObservableList<Exam> data;
	ObservableList<Subject> dataSub;
	String bankName;
	ObservableList<Course> dataCou;
	ObservableList<Exam> examIDs;
	ObservableList<String> execCodesGradeApproval;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		ClientUI.chat.accept("examBank_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);

		dataSub = FXCollections.observableArrayList(MyClient.subjects);
		ObservableList<String> list3 = FXCollections.observableArrayList();
		for (int i = 0; i < dataSub.size(); i++) {
			String str;
			str = dataSub.get(i).getSubjectName();
			list3.add(str);
		}

		SubjectList.setItems(list3);

		if (prevSubCou == true) {
			dataSub = FXCollections.observableArrayList(MyClient.subjects);
			list3 = FXCollections.observableArrayList();
			for (int i = 0; i < dataSub.size(); i++) {
				String str;
				str = dataSub.get(i).getSubjectName();
				list3.add(str);
			}

			SubjectList.setItems(list3);

			bankName = SubjectList.getValue();
			dataCou = FXCollections.observableArrayList(MyClient.courses);
			ObservableList<String> list2 = FXCollections.observableArrayList();

			for (int i = 0; i < dataCou.size(); i++) {
				String str;
				str = dataCou.get(i).getCourseName();
				list2.add(str);
			}
			CourseList.setItems(list2);

			SubjectList.setValue(subjectGradeAppString);
			CourseList.setValue(courseGradeAppString);
			for (int i = 0; i < MyClient.examsForEdit.size(); i++) {

				MyClient.examsForEdit.get(i).setCourseName(CourseList.getValue());
				MyClient.examsForEdit.get(i).setSubjectName(SubjectList.getValue());
			}
			examIDs = FXCollections.observableArrayList(MyClient.examsForEdit);
			ExamId.setCellValueFactory(new PropertyValueFactory<Exam, String>("ExamID"));
			executionCode.setCellValueFactory(new PropertyValueFactory<Exam, String>("executionCode"));
			Subject.setCellValueFactory(new PropertyValueFactory<Exam, String>("subjectName"));
			Course.setCellValueFactory(new PropertyValueFactory<Exam, String>("courseName"));
			ExamSelect.setItems(examIDs);
		}
	}

	/**
	 * start GUI of first grade approval screen
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/GradeApproval.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Grade approval form");
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
	 * choose subject of exams to show in table
	 * 
	 * @param event user chooses subject from its combo box
	 */
	@FXML
	void subjChoice(ActionEvent event) {
		ClientUI.chat.accept("getCoursesUnderSubject&Teacher_" + SubjectList.getValue());
		bankName = SubjectList.getValue();
		dataCou = FXCollections.observableArrayList(MyClient.courses);
		ObservableList<String> list2 = FXCollections.observableArrayList();

		for (int i = 0; i < dataCou.size(); i++) {
			String str;
			str = dataCou.get(i).getCourseName();
			list2.add(str);
		}
		CourseList.setItems(list2);
	}

	/**
	 * choose course of exams to show in table
	 * 
	 * @param event user chooses course from its combo box
	 */
	@FXML
	void QuestionGet(ActionEvent event) {
		subjectGradeAppString = SubjectList.getValue();

		courseGradeAppString = CourseList.getValue();
		if (CourseList.getValue() != null) {
			ClientUI.chat.accept("getExamsUnderTeacherExamBankCourse_" + SubjectList.getValue() + "_"
					+ CourseList.getValue() + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
			if (MyClient.status.equals("1"))
				return;

			for (int i = 0; i < MyClient.examsForEdit.size(); i++) {
				MyClient.examsForEdit.get(i).setCourseName(CourseList.getValue());
				MyClient.examsForEdit.get(i).setSubjectName(SubjectList.getValue());
			}
			examIDs = FXCollections.observableArrayList(MyClient.examsForEdit);
			ExamId.setCellValueFactory(new PropertyValueFactory<Exam, String>("ExamID"));
			executionCode.setCellValueFactory(new PropertyValueFactory<Exam, String>("executionCode"));
			Subject.setCellValueFactory(new PropertyValueFactory<Exam, String>("subjectName"));
			Course.setCellValueFactory(new PropertyValueFactory<Exam, String>("courseName"));
			ExamSelect.setItems(examIDs);
		}
	}

	/**
	 * go to teachers main exam GUI
	 * 
	 * @param event user clicks back button
	 */
	@FXML
	void back(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		TeacherExamsBoundary teb = new TeacherExamsBoundary();
		Stage primaryStage = new Stage();
		try {
			teb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * show grades to approve (if exists) GUI
	 * 
	 * @param event user clicks next button
	 */
	@FXML
	void next(ActionEvent event) {
		int selectedExamIndex = ExamSelect.getSelectionModel().getFocusedIndex();
		Exam st1 = examIDs.get(selectedExamIndex);
		Id = st1.getExamID();
		ClientUI.chat
				.accept("getPendingGrades" + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass + "_" + Id);
		if (MyClient.pendingGrade.equals("No"))
			return;
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		GradeApproval2Boundary ga2 = new GradeApproval2Boundary();
		Stage primaryStage = new Stage();
		try {
			ga2.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
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