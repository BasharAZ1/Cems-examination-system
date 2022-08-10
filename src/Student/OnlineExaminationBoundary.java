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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * online exam first screen GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class OnlineExaminationBoundary {

	@FXML
	private Button logoutTeachRepBtn;

	@FXML
	private TextField StudExamCodeTxt;

	@FXML
	private Button startExamBtn;

	@FXML
	private Button backOnlineExamtBtn;

	@FXML
	private TextField StudIDTxt;

	@FXML
	private Label fullName;

	static String stuID;

	@FXML
	private void initialize() {
		fullName.setText(MyClient.studentFullName);
	}

	/**
	 * start online examination first screen
	 * 
	 * @param primaryStage
	 * @throws Exception
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Student/OnlineExamination1.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Online examination form");
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
	 * start exam for student user
	 * 
	 * @param event user clicks start exam button
	 */
	@FXML
	void startExam(ActionEvent event) {
		// getFullOnlineExam_executionCode_studentID
		stuID = StudIDTxt.getText();
		ClientUI.chat.accept("getFullOnlineExam_" + StudExamCodeTxt.getText() + "_" + StudIDTxt.getText());
		if (MyClient.getFullOnlineExam.equals("accept")) {
			((Node) event.getSource()).getScene().getWindow().hide(); // hide window
			OnlineExamination2Boundary oeb2 = new OnlineExamination2Boundary();
			Stage primaryStage = new Stage();
			try {
				oeb2.start(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * back to StudentBoundary GUI
	 * 
	 * @param event user clicks back button
	 */
	@FXML
	void back(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		StudentBoundary sb = new StudentBoundary();
		Stage ps = new Stage();
		try {
			sb.start(ps);
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