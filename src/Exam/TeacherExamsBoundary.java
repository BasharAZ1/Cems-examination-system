package Exam;

import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
import Login.MyClientBoundary;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * start teacher exam menu GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class TeacherExamsBoundary {

	@FXML
	private Label ExamsLabel;

	@FXML
	private Button ExamBtn;

	@FXML
	private Button ExamApproveBtn;

	@FXML
	private Button ExamLockBtn;

	@FXML
	private Button ExamChangeTimeBtn;

	@FXML
	private Button ExamBackBtn;

	@FXML
	private Label lblIndetifier;

	@FXML
	private Button logout;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);
	}

	/**
	 * start teacher exam menu GUI
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/TeacherExam.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Teacher exam form");
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
	 * go to add exams GUI
	 * 
	 * @param event user clicks add exam button
	 */
	@FXML
	void addExam(ActionEvent event) {
		ClientUI.chat.accept("getSubjects_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		((Node) event.getSource()).getScene().getWindow().hide();
		AddExamsBoundary aeb = new AddExamsBoundary();
		Stage primaryStage = new Stage();
		try {
			aeb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * go to teacher main GUI
	 * 
	 * @param event user clicks back button
	 */
	@FXML
	void TeacherBackBtn(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		TeacherBoundary tb = new TeacherBoundary();
		Stage primaryStage = new Stage();
		try {
			tb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * go to change exam duration GUI
	 * 
	 * @param event user clicks change duration button
	 */
	@FXML
	void changeDur(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		ChangeDurationBoundary cb = new ChangeDurationBoundary();
		Stage primaryStage = new Stage();
		try {
			cb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * go to exam grade approval GUI
	 * 
	 * @param event user clicks grade approval button
	 */
	@FXML
	void examApproval(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		GradeApprovalBoundary ga = new GradeApprovalBoundary();
		Stage primaryStage = new Stage();
		try {
			ga.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * go to exams lock GUI
	 * 
	 * @param event user clicks lock exam button
	 */
	@FXML
	void LockExam(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		LockExamBoundary lb = new LockExamBoundary();
		Stage primaryStage = new Stage();
		try {
			lb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * go to edit exams bank GUI
	 * 
	 * @param event user clicks edit exams button
	 */
	@FXML
	void EditExam(ActionEvent event) {
		ClientUI.chat.accept("examBank_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		if (MyClient.editExam.equals("No"))
			return;
		((Node) event.getSource()).getScene().getWindow().hide();
		EditExam1Boundary lb = new EditExam1Boundary();
		Stage primaryStage = new Stage();
		try {
			lb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * logout, go back to login screen
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