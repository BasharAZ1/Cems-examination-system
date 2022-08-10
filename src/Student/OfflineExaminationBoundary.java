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
 * offline exam GUI window - here student enters execution code and clicks start
 * to get to next offline exam window
 * 
 * @author Bashr Ali & Samr Arkab
 *
 */
public class OfflineExaminationBoundary {
	@FXML
	private Button logoutOfflineBtn;

	@FXML
	private TextField StudExamCodeTxt;

	@FXML
	private Button StudDwnldExamBtn;

	@FXML
	private Button StdUploadExamBtn;

	@FXML
	private Button backOfflineBtn;

	@FXML
	private Label fullName;

	public static String excode;

	/**
	 * initialize window with student's name next to logout
	 */
	@FXML
	private void initialize() {
		fullName.setText(MyClient.studentFullName);
	}

	/**
	 * start GUI window
	 * @param primaryStage
	 * @throws Exception
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Student/OfflineExam.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Offline examination form");
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
	 * opens next offline exam window
	 * @param event user clicks start exam after entering execution code
	 */
	@FXML
	void startExam(ActionEvent event) {
		excode = StudExamCodeTxt.getText();
		if (excode.length() != 4) {
			Alert alert = new Alert(AlertType.WARNING, "ExcutionCode Must 4 digits!!!",
					ButtonType.OK);
			alert.showAndWait();
			return;
		}
		ClientUI.chat.accept("canDoExam_" + excode + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		if (MyClient.StatusOF.equals("accept")) {

			((Node) event.getSource()).getScene().getWindow().hide(); // hide window
			OfflineExam2 sb = new OfflineExam2();
			Stage ps = new Stage();
			try {
				sb.start(ps);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * goes to previous window
	 * @param event user clicks Back button
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
	 * user is logged out of the system, goes back to main CEMS login screen
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