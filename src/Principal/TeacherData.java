package Principal;

import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
import Data.Person;
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
 * class to show principal user the teachers data base
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class TeacherData {
	@FXML
	private Button logoutViewTeachDataCopyBtn;

	@FXML
	private TableView<Person> table;

	@FXML
	private TableColumn<Person, String> TeachIDData;

	@FXML
	private TableColumn<Person, String> TeachNameData;

	@FXML
	private TableColumn<Person, String> TeachSurenameData;

	@FXML
	private TableColumn<Person, String> TeachEmailData;

	@FXML
	private TableColumn<Person, String> TeachSubjectsData;

	@FXML
	private Button viewTeachDataBtn;

	@FXML
	private Label fullName;

	private ObservableList<Person> data1;

	@FXML
	private void initialize() {
		fullName.setText(MyClient.principalFullName);

		ClientUI.chat.accept("GetTeachersData");
		data1 = FXCollections.observableArrayList(MyClient.persons);
		TeachIDData.setCellValueFactory(new PropertyValueFactory<Person, String>("id"));
		TeachNameData.setCellValueFactory(new PropertyValueFactory<Person, String>("name"));
		TeachSurenameData.setCellValueFactory(new PropertyValueFactory<Person, String>("surname"));
		TeachEmailData.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));
		TeachSubjectsData.setCellValueFactory(new PropertyValueFactory<Person, String>("Subjects"));
		table.setItems(data1);
	}

	/**
	 * starts the teachers data screen for principal user
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Principal/ViewTeachersData.fxml"));
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
	 * go back to main data screen for principal user
	 * 
	 * @param event user clicks on "back" button
	 */
	@FXML
	void back(ActionEvent event) {
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