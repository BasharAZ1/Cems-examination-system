package Principal;

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
 * class for principal user main screen
 * 
 * @author Bashar Ali & Samr Arkab
 */
public class PrincipalBoundary {
	@FXML
	private Label PrincipalLabel;

	@FXML
	private Button ConfirmChangeTimeBtn;

	@FXML
	private Button ViewDbBtn;

	@FXML
	private Label fullName;

	@FXML
	private void initialize() {
		fullName.setText(MyClient.principalFullName);
	}

	/**
	 * starts the main screen for principal user
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Principal/Principal.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Principal form");
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
	 * change to confirm exams change duration requests screen, if such requests exist
	 * 
	 * @param event user click on confirm duration button
	 */
	@FXML
	void confirmDur(ActionEvent event) {
		ClientUI.chat.accept("getChangeDuration");
		if (MyClient.durationChangeSuccess.equals("accept")) {
			((Node) event.getSource()).getScene().getWindow().hide(); // hide window
			ConfirmDuration svgb = new ConfirmDuration();
			Stage primaryStage = new Stage();
			try {
				svgb.start(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * open and show reports boundary 
	 * 
	 * @param event user clicks button to get reports
	 */
	@FXML
	void getReport(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		ReportsBoundary svgb = new ReportsBoundary();
		Stage primaryStage = new Stage();
		try {
			svgb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * open and show data boundary
	 * 
	 * @param event user clicks button to view data base
	 */
	@FXML
	void viewDb(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		DataMenu svgb = new DataMenu();
		Stage primaryStage = new Stage();
		try {
			svgb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * logout user, go back to MyClientBoundary (login screen)
	 * 
	 * @param event user clicks on "logout" button
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