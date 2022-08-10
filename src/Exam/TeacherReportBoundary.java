package Exam;

import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
import Data.Exam;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * class for teachers report GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class TeacherReportBoundary {
	@FXML
	private Button logoutTeachRepBtn;

	@FXML
	private TableView<Exam> tblExam;

	@FXML
	private TableColumn<Exam, String> ExamIDTeachRep;

	@FXML
	private TableColumn<Exam, String> SubTeachRep;
	@FXML
	private TableColumn<Exam, String> CourseTeachRep;

	@FXML
	private Button NextTeachRepBtn;

	@FXML
	private Label lblIndetifier;

	@FXML
	private Button backTeachRepBtn;

	static ObservableList<Exam> data;
	Exam select;
	static String examid, cou;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		ClientUI.chat.accept("getAllExamsOfTeacher" + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		data = FXCollections.observableArrayList(MyClient.exams1);
		ExamIDTeachRep.setCellValueFactory(new PropertyValueFactory<Exam, String>("ExamID"));
		SubTeachRep.setCellValueFactory(new PropertyValueFactory<Exam, String>("subjectName"));
		CourseTeachRep.setCellValueFactory(new PropertyValueFactory<Exam, String>("courseName"));
		tblExam.setItems(data);
	}

	/**
	 * start teacher reports GUI
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/TeacherReport.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Teacher report form");
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
	 * back to teachers main boundary GUI
	 * 
	 * @param event user clicks back button
	 */
	@FXML
	void back(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		TeacherBoundary tb = new TeacherBoundary();
		Stage primaryStage = new Stage();
		try {
			tb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void next(ActionEvent event) {

		select = tblExam.getSelectionModel().getSelectedItem();
		if (select != null) {
			examid = select.getExamID();
			cou = select.getCourseName();
			if (!ReportController.getExamReportFromServer(examid)) { // REFACTOR
				return;
			}
			((Node) event.getSource()).getScene().getWindow().hide();
			TeacherReport2Boundary trb = new TeacherReport2Boundary();
			Stage primaryStage = new Stage();
			try {
				trb.start(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
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