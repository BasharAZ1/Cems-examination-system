package Exam;

import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
import Data.Course;
import Data.Exam;
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
 * first phase of edit exam
 * @author Bashar Ali & Samr Arkab
 *
 */
public class EditExam1Boundary {

	@FXML
	private Button logOutBn;

	@FXML
	private Label lblIndetifier;

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
	private Button deleteExamBtn;

	@FXML
	private Button deleteBankBtn;

	@FXML
	private Button Editbtn;

	@FXML
	private Button backBtn;

	public static String Id;
	public static int selectedExamIndexToEdit;

	Exam examselect;
	ObservableList<Subject> dataSub;
	ObservableList<Question> data1;
	ObservableList<Course> data2;
	ObservableList<Subject> data3;
	ObservableList<Exam> data4;
	String subj, course, questID, questScore, bankName;;
	ObservableList<Exam> examIDs;
	ObservableList<Course> dataCou;
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
	}

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/EditExam.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Edit exams bank form");
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

	@FXML
	void subjChoice(ActionEvent event) {
		ExamSelect.getItems().clear();
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

	@SuppressWarnings("unused")
	@FXML
	void QuestionGet(ActionEvent event) {
		ExamSelect.getItems().clear();
		String subjectString = SubjectList.getValue();

		String courseString = CourseList.getValue();
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

	@FXML
	void edit(ActionEvent event) {

		Id = "";
		selectedExamIndexToEdit = ExamSelect.getSelectionModel().getFocusedIndex();
		@SuppressWarnings("unused")
		Exam st1 = examIDs.get(selectedExamIndexToEdit);

		int selectedExamIndex = ExamSelect.getSelectionModel().getFocusedIndex();
		Exam st11 = examIDs.get(selectedExamIndex);
		Id = st11.getExamID();
		ClientUI.chat.accept("getExam_" + Id);
		if (MyClient.EditOfflineExam.equals("Yes"))
			return;
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		EditExam2Boundary ed2 = new EditExam2Boundary();
		Stage primaryStage = new Stage();
		try {
			ed2.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void deletBank(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this bank?", ButtonType.YES,
				ButtonType.NO);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.NO)
			return;

		for (int i = 0; i < ExamSelect.getItems().size(); i++)
			ExamSelect.getItems().clear();
		examIDs.clear();
		ClientUI.chat.accept("deleteExamBank_" + CourseList.getValue());
		MyClient.ExamID = null;
	}

	@FXML
	void deleteExam(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this exam?", ButtonType.YES,
				ButtonType.NO);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.NO)
			return;
		int selectedExamIndex = ExamSelect.getSelectionModel().getFocusedIndex();
		if (selectedExamIndex != -1) {
			Exam st = ExamSelect.getItems().get(selectedExamIndex);
			// examIDs.remove(selectedExamIndex);
			ClientUI.chat.accept("deleteExam_" + st.getExamID());
			if (MyClient.OngoingStatus.equals("Yes"))
				return;
			ExamSelect.getItems().remove(selectedExamIndex);
		}
	}

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