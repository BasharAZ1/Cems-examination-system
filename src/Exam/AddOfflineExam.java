package Exam;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
import Data.MyFile;
import Data.MyFile.FileSource;
import Login.MyClientBoundary;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * start teacher add offline exam GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class AddOfflineExam {
	@FXML
	private Button logOutBtn;

	@FXML
	private TextField DurationTextField;

	@FXML
	private TextField ExecutionCodeTextField;

	@FXML
	private TextArea CommentsStudentsTextField;

	@FXML
	private TextArea CommentsTeacherTextField;

	@FXML
	private Button BackBtn;

	@FXML
	private Button SaveBtn;

	@FXML
	private Button generateExecCode;

	@FXML
	private Label lblIndetifier;

	public static MyFile msg = new MyFile(offlineExam.strpath);
	public int flag = 1;
	public static String Cor = null, sub = null;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);
	}

	/**
	 * return to add exams GUI
	 * 
	 * @param event user clicks back button
	 */
	@FXML
	void backBtn(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		AddExamsBoundary.pS.show();
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

	/**
	 * generate exam execution code from data base
	 * 
	 * @param event user clicks generate execution code button
	 */
	@FXML
	void generateExecCode(ActionEvent event) {
		ClientUI.chat.accept("generateUniqueExecutionCode");
		ExecutionCodeTextField.setText(MyClient.generatedCode);
	}

	/**
	 * save offline exam
	 * 
	 * @param event user clicks save button
	 */
	@FXML
	void saveAction(ActionEvent event) {
		if (MyClient.StatusOF.equals("invalid")) {
			Alert alert = new Alert(AlertType.WARNING, "Invalid execution code.", ButtonType.OK);
			alert.showAndWait();
			return;
		}

		if (DurationTextField.getText().length() == 0) {
			Alert alert = new Alert(AlertType.WARNING, "Enter duration!", ButtonType.OK);
			alert.showAndWait();
			return;
		}

		String sr = DurationTextField.getText();
		if (sr.matches("[0-9]+")) {
			String LocalfilePath = offlineExam.strpath;
			int bg = 0;
			for (int i = LocalfilePath.length(); i > 0; i--) {
				char c = LocalfilePath.charAt(i - 1);
				if (c == ('\\')) {

					bg = i;
					i = 0;
				}
			}
			if (this.ExecutionCodeTextField.getText().length() != 4) {
				Alert alert = new Alert(AlertType.WARNING, "Execution code must be 4 digits!", ButtonType.OK);
				alert.showAndWait();
				return;
			}
			ClientUI.chat.accept("uniqueExecutionCode_" + this.ExecutionCodeTextField.getText());
			if (MyClient.excutioncodereq.equals("taken")) {
				Alert alert = new Alert(AlertType.WARNING, "Execution code is taken!\nPlease choose a different code.",
						ButtonType.OK);
				alert.showAndWait();
				return;
			}
			String filename = LocalfilePath.substring(bg, LocalfilePath.length());
			msg.setFileName(filename);
			try {

				File newFile = new File(LocalfilePath);
				byte[] mybytearray = new byte[(int) newFile.length()];
				FileInputStream fis = new FileInputStream(newFile);
				@SuppressWarnings("resource")
				BufferedInputStream bis = new BufferedInputStream(fis);
				msg.setFileSource(FileSource.TeacherAddOfflineExam);
				msg.initArray(mybytearray.length);
				msg.setSize(mybytearray.length);
				msg.setCourseName(AddExamsBoundary.courseOF);
				msg.setSubjectName(AddExamsBoundary.subjOF);
				msg.setUsername(MyClientBoundary.usName);
				msg.setPassword(MyClientBoundary.pass);
				bis.read(msg.getMybytearray(), 0, mybytearray.length);
				msg.setDuration(Integer.parseInt(this.DurationTextField.getText().replaceAll("_", "1a6gf")));
				msg.setExecutionCode(this.ExecutionCodeTextField.getText().replaceAll("_", "1a6gf"));

				ClientUI.chat.accept(msg);
			} catch (Exception e) {
				Alert alert = new Alert(AlertType.ERROR, "Error send (Files)msg) to Server", ButtonType.OK);
				alert.showAndWait();
				System.out.println("Error send (Files)msg) to Server");
				e.printStackTrace();
			}
			Alert alert = new Alert(AlertType.INFORMATION, "Offline exam successfuly saved in DataBase.",
					ButtonType.OK);
			alert.showAndWait();

			((Node) event.getSource()).getScene().getWindow().hide();
			AddExamsBoundary addExam = new AddExamsBoundary();
			try {
				Stage primaryStage = new Stage();
				addExam.start(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Alert alert = new Alert(AlertType.INFORMATION, "Enter only Numbers for Duration!", ButtonType.OK);
			alert.showAndWait();
		}
	}

	/**
	 * start add offline exam GUI
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/AddExam2Offline.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Add exam form");
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
}