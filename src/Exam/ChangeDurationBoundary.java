package Exam;

import java.util.Optional;

import javax.swing.JOptionPane;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * teacher can change duration of an exam
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class ChangeDurationBoundary {
	@FXML
	private TextField newTimeTxt;

	@FXML
	private Label lblIndetifier;

	@FXML
	private TableView<Exam> ExamsDuration;

	@FXML
	private TableColumn<Exam, String> ExIDField;

	@FXML
	private TableColumn<Exam, String> SubField1;

	@FXML
	private TableColumn<Exam, Integer> Durtime;

	@FXML
	private TableColumn<Exam, Integer> NewDTime;

	@FXML
	private TableColumn<Exam, String> Ongoing;

	@FXML
	private Button backBtn;

	@FXML
	private Button saveBtn;

	@FXML
	private TextArea reason;

	@FXML
	private Button logoutChangeDurTBtn;

	static ObservableList<Exam> data;
	Exam examselect;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		ClientUI.chat.accept("getAllExamsOfTeacher" + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		data = FXCollections.observableArrayList(MyClient.exams1);
		ExIDField.setCellValueFactory(new PropertyValueFactory<Exam, String>("ExamID"));
		SubField1.setCellValueFactory(new PropertyValueFactory<Exam, String>("subjectName"));
		Durtime.setCellValueFactory(new PropertyValueFactory<Exam, Integer>("Duration"));
		NewDTime.setCellValueFactory(new PropertyValueFactory<Exam, Integer>("newDuration"));
		Ongoing.setCellValueFactory(new PropertyValueFactory<Exam, String>("OngoingString"));
		ExamsDuration.setItems(data);

	}

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/ChangeDuration.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Change duration form");
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
	 * go to exams menu
	 * @param event user clicked back button
	 */
	@FXML
	void back(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		TeacherExamsBoundary teb = new TeacherExamsBoundary();
		Stage primaryStage = new Stage();
		try {
			teb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * save new duration of exam
	 * @param event user clicks save button
	 */
	@FXML
	void save(ActionEvent event) {
		String sr = newTimeTxt.getText();
		if (sr.matches("[0-9]+")) {
			String sr2 = null;
			String sr1 = null;
			String str3 = null;
			examselect = ExamsDuration.getSelectionModel().getSelectedItem();
			if (examselect.getOngoingString().equals("YES")) {
				if (reason.getText().length() < 1) {
					Alert alert = new Alert(AlertType.WARNING, "Enter reason!!", ButtonType.OK);
					alert.showAndWait();
					return;
				}
			}
			int temp;
			temp = Integer.parseInt(sr);
			ClientUI.chat.accept("changeExamDuration_" + examselect.getExamID() + "_" + sr + "_"
					+ reason.getText().replaceAll("_", "1a6gf"));
			if (MyClient.durationChangePending.equals("pending")) {
				Alert alert = new Alert(AlertType.INFORMATION, "Request received waiting for administrator approval",
						ButtonType.OK);
				alert.showAndWait();
				return;
			} else if (MyClient.durationChangeFail.equals("decline")) {
				Alert alert = new Alert(AlertType.INFORMATION, "Your request was denied", ButtonType.OK);
				alert.showAndWait();
				return;
			} else if (MyClient.durationChangeSuccess.equals("accept")) {
				examselect = ExamsDuration.getSelectionModel().getSelectedItem();
				int indexforTable = ExamsDuration.getSelectionModel().getSelectedIndex();

				if (temp == examselect.getDuration()) {
					return;
				}
				sr1 = examselect.getExamID();
				sr2 = examselect.getSubjectName();
				str3 = examselect.getOngoingString();
				int index = 0;
				for (int i = 0; i < data.size(); i++) {
					if (sr == data.get(i).getExamID()) {
						index = i;
					}
				}
				ExamsDuration.getItems().remove(examselect);
				Exam examtemp = new Exam();
				examtemp.setExamID(sr1);
				examtemp.setSubjectName(sr2);
				examtemp.setDuration(temp);
				examtemp.setNewDuration(temp);
				examtemp.setOngoingString(str3);
				data.add(data.size() - index, examtemp);
				ExamsDuration.setItems(data);
				ExamsDuration.getSelectionModel().clearSelection();
				ExamsDuration.getSelectionModel().select(indexforTable);
			} else
				JOptionPane.showMessageDialog(null, "Enter numbers only!");
		}
	}

	/**
	 * user logs out of CEMS, back to main login screen
	 * @param event user clicks Logout button
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