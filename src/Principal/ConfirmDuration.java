package Principal;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
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

/**
 * class to present principals confirm duration change request
 * 
 * @author Bashar Ali & Samr Arkab
 */
public class ConfirmDuration {
	@FXML
	private Button ApproveBtn;

	@FXML
	private Button DenyBtn;

	@FXML
	private Button viewReasonBtn;

	@FXML
	private TableView<Exam> Examtoch;

	@FXML
	private TableColumn<Exam, String> ExamIDField;

	@FXML
	private TableColumn<Exam, String> subField;

	@FXML
	private TableColumn<Exam, Integer> PTimeField;

	@FXML
	private TableColumn<Exam, Integer> NTimeFiled;

	@FXML
	private Label reasonLabel;

	@FXML
	private Button backBtn;

	@FXML
	private Button logOut;

	@FXML
	private Label fullName;

	Exam select;
	static ObservableList<Exam> data;
	public static ArrayList<String> reasons = new ArrayList<>();
	public String id;

	@FXML
	private void initialize() {
		fullName.setText(MyClient.principalFullName);

		data = FXCollections.observableArrayList(MyClient.exams);
		reasons = MyClient.resoansprin;
		ExamIDField.setCellValueFactory(new PropertyValueFactory<Exam, String>("ExamID"));
		subField.setCellValueFactory(new PropertyValueFactory<Exam, String>("subjectName"));
		PTimeField.setCellValueFactory(new PropertyValueFactory<Exam, Integer>("Duration"));
		NTimeFiled.setCellValueFactory(new PropertyValueFactory<Exam, Integer>("newDuration"));
		Examtoch.setItems(data);
	}

	/**
	 * starts the confirm duration change screen for principal user
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Principal/ConfirmChageDuration.fxml"));
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
	 * principal user approves exam duration change request
	 * 
	 * @param event user clicks accept button
	 */
	@FXML
	void approve(ActionEvent event) {
		select = Examtoch.getSelectionModel().getSelectedItem();
		id = select.getExamID();
		ClientUI.chat.accept("approvalChangeDuration_" + id + "_1");
		if (select != null) {
			Examtoch.getItems().remove(select);
		}
	}

	/**
	 * go back to main screen for principal user
	 * 
	 * @param event user clicks on "back" button
	 */
	@FXML
	void back(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		PrincipalBoundary teb = new PrincipalBoundary();
		Stage primaryStage = new Stage();
		try {
			teb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * principal user denies exam duration change request
	 * 
	 * @param event user clicks accept button
	 */
	@FXML
	void deny(ActionEvent event) {
		select = Examtoch.getSelectionModel().getSelectedItem();
		id = select.getExamID();
		ClientUI.chat.accept("approvalChangeDuration_" + id + "_0");
		if (select != null) {
			Examtoch.getItems().remove(select);
		}
	}

	/**
	 * show teacher reason for requesting duration change of exam
	 * 
	 * @param event user clicks reason button
	 */
	@FXML
	void showReason(ActionEvent event) {
		select = Examtoch.getSelectionModel().getSelectedItem();
		if (select != null) {
			int index = 0;
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getExamID() == select.getExamID())
					index = i;
				i = data.size();
			}
			reasonLabel.setText(reasons.get(index));
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