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
 * class for teacher questions menu GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class TeacherQuestionsBoundary {

	@FXML
	private Label QuestionLabel;

	@FXML
	private Button AddQuestion;

	@FXML
	private Button EditQuestion;

	@FXML
	private Label lblIndetifier;

	@FXML
	private Button QuestionBackbtn;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);
	}

	/**
	 * start teacher question menu screen
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/TeacherQuestions.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Teacher questions form");
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
	 * go to add questions GUI
	 * 
	 * @param event user clicks add questions button
	 */
	@FXML
	void addQuestions(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		AddQuestionsBoundary aqb = new AddQuestionsBoundary();
		Stage primaryStage = new Stage();
		try {
			aqb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * back to teacher main GUI
	 * 
	 * @param event user clicks back button
	 */
	@FXML
	void backBtn(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		TeacherBoundary tb = new TeacherBoundary();
		Stage primaryStage = new Stage();
		try {
			tb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * go to edit questions bank GUI
	 * 
	 * @param event user clicks edit button
	 */
	@FXML
	void EditQuestion(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		EditQuestion1Boundary tb = new EditQuestion1Boundary();
		Stage primaryStage = new Stage();
		try {
			tb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * logout, return to login screen
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