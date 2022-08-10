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
 * start teacher main window GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class TeacherBoundary {
	@FXML
	private Label TeacherLabel;

	@FXML
	private Button ExamsTeacherBtn;

	@FXML
	private Button QuestionTeacherBtn;

	@FXML
	private Label lblIndetifier;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);
	}

	/**
	 * start teacher main GUI
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/Teacher.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Teacher form");
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
	 * go to main exams window GUI
	 * 
	 * @param event user clicks exam menu button
	 */
	@FXML
	void examsMenu(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		TeacherExamsBoundary teb = new TeacherExamsBoundary();
		Stage primaryStage = new Stage();
		try {
			teb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * go to main reports window GUI
	 * 
	 * @param event user clicks report menu button
	 */
	@FXML
	void getReportMenu(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		TeacherReportBoundary trb = new TeacherReportBoundary();
		Stage primaryStage = new Stage();
		try {
			trb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * go to main questions windows GUI
	 * 
	 * @param event user clicks questions menu button
	 */
	@FXML
	void questionsMenu(ActionEvent event) {
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
	 * logout, return to login GUI
	 * 
	 * @param event user clicks logout
	 */
	@FXML
	void logout(ActionEvent event) {
		ClientUI.chat.accept("logout_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		MyClientBoundary.usName = "";
		MyClientBoundary.pass = "";

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