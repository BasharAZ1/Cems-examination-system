package Exam;

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
 * start lock exam GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class LockExamBoundary {
	@FXML
	private TableView<Exam> ExamSelction;

	@FXML
	private TableColumn<Exam, String> ExamId;

	@FXML
	private TableColumn<Exam, String> subj;

	@FXML
	private TableColumn<Exam, String> statusField;
	
    @FXML
    private TableColumn<Exam, String> colOngoing;

	@FXML
	private Button backLockExamBtn;

	@FXML
	private Button lockBtn;

	@FXML
	private Button unlockBtn;

	@FXML
	private Label lblIndetifier;

	static ObservableList<Exam> data;
	Exam examselect;
	String com = "1";

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		ClientUI.chat.accept("getAllExamsOfTeacher" + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		data = FXCollections.observableArrayList(MyClient.exams1);
		ExamId.setCellValueFactory(new PropertyValueFactory<Exam, String>("ExamID"));
		subj.setCellValueFactory(new PropertyValueFactory<Exam, String>("subjectName"));
		statusField.setCellValueFactory(new PropertyValueFactory<Exam, String>("lockStatusString"));
		colOngoing.setCellValueFactory(new PropertyValueFactory<Exam, String>("OngoingString"));
		ExamSelction.setItems(data);
	}

	/**
	 * start lock exam GUI
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/LockExam.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Lock exams form");
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
	 * go to teacher exams main GUI
	 * 
	 * @param event user clicks back button
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
	 * choose exam and lock it
	 * 
	 * @param event user clicks lock button 
	 */
	@FXML
	void Lock(ActionEvent event) {
		examselect = ExamSelction.getSelectionModel().getSelectedItem();
		int indexforTable = ExamSelction.getSelectionModel().getSelectedIndex();
		String sr1, sr2, sr3;
		sr1 = examselect.getExamID();
		sr2 = examselect.getSubjectName();
		sr3 = examselect.getLockStatusString();
		if (sr3 == "Locked")
			return;
		String temp = "lockExam_" + examselect.getExamID() + "_" + "1";
		ClientUI.chat.accept(temp);

		sr3 = "Locked";
		int index = 0;
		for (int i = 0; i < data.size(); i++) {
			if (sr1 == data.get(i).getExamID()) {
				index = i;
			}
		}

		ExamSelction.getItems().remove(examselect);
		Exam examtemp = new Exam();
		examtemp.setExamID(sr1);
		examtemp.setSubjectName(sr2);
		examtemp.setLockStatusString(sr3);
		data.add(index, examtemp);
		ExamSelction.setItems(data);
		ExamSelction.getSelectionModel().clearSelection();
		ExamSelction.getSelectionModel().select(indexforTable);
		
		ExamSelction.getItems().clear();
		ClientUI.chat.accept("getAllExamsOfTeacher" + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		data = FXCollections.observableArrayList(MyClient.exams1);
		ExamSelction.setItems(data);
	}

	/**
	 * choose exam and unlock it
	 * 
	 * @param event user clicks unlock button
	 */
	@FXML
	void Unlock(ActionEvent event) {
		examselect = ExamSelction.getSelectionModel().getSelectedItem();
		int indexforTable = ExamSelction.getSelectionModel().getSelectedIndex();
		String sr1, sr2, sr3;
		sr1 = examselect.getExamID();
		sr2 = examselect.getSubjectName();
		sr3 = examselect.getLockStatusString();
		if (sr3 == "Unlocked")
			return;
		String temp = "lockExam_" + examselect.getExamID() + "_" + "0";
		ClientUI.chat.accept(temp);
		int index = 0;
		for (int i = 0; i < data.size(); i++) {
			if (sr1 == data.get(i).getExamID()) {
				index = i;
			}
		}
		sr3 = "Unlocked";
		ExamSelction.getItems().remove(examselect);
		Exam examtemp = new Exam();
		examtemp.setExamID(sr1);
		examtemp.setSubjectName(sr2);
		examtemp.setLockStatusString(sr3);
		data.add(index, examtemp);
		ExamSelction.setItems(data);
		ExamSelction.getSelectionModel().clearSelection();
		ExamSelction.getSelectionModel().select(indexforTable);
		
		ExamSelction.getItems().clear();
		ClientUI.chat.accept("getAllExamsOfTeacher" + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		data = FXCollections.observableArrayList(MyClient.exams1);
		ExamSelction.setItems(data);
	}

	/**
	 * logout, return to login GUI
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