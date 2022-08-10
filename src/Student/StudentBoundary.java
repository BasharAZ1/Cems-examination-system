package Student;

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
 * student main boundary GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class StudentBoundary {

	@FXML
	private Button logoutStudentBtn;

	@FXML
	private Button StudentGradeBtn;

	@FXML
	private Button OnExamBtn;

	@FXML
	private Button OffExamBtn;

	@FXML
	private Label fullName;

	@FXML
	private void initialize() {
		fullName.setText(MyClient.studentFullName);
	}

	/**
	 * start stage for students main screen 
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Student/Student.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Student form");
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
	 * view student grades screen
	 * 
	 * @param event user clicks button to view grades
	 */
	@FXML
	void ViewGrades(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		StudentViewGradeBoundary svgb = new StudentViewGradeBoundary();
		Stage primaryStage = new Stage();
		try {
			svgb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * go to first screen of online examination GUI
	 * 
	 * @param event user clicks button for online exam
	 */
	@FXML
	void OnlineExam(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		OnlineExaminationBoundary oeb = new OnlineExaminationBoundary();
		Stage primaryStage = new Stage();
		try {
			oeb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * go to first screen of offline examination GUI
	 * 
	 * @param event user clicks button for offline exam
	 */
	@FXML
	void OfflineExam(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		OfflineExaminationBoundary oeb = new OfflineExaminationBoundary();
		Stage primaryStage = new Stage();
		try {
			oeb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
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