package Exam;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
import Data.MyFile;
import Data.MyFile.FileSource;
import Data.QuestionInExam;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * second window for adding a new exam
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class AddExam2Boundary {
	@FXML
	private Button SaveBtn;

	@FXML
	private Button BackBtn;

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
	private Button generateExecCode;

	@FXML
	private Label lblIndetifier;

	public static MyFile msg = new MyFile(offlineExam.strpath);
	static String temp;
	public static int flag2 = 0;
	public static String dur, exe, commentsForS, commentsForT;
	public static ArrayList<QuestionInExam> quesin = new ArrayList<>();
	public static Stage pS;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);
		flag2 = 0;
		if (AddExamsBoundary.flag == 1) {
			DurationTextField.setText(AddExamsBoundary.dur);
			ExecutionCodeTextField.setText(AddExamsBoundary.exe);
			CommentsStudentsTextField.setText(AddExamsBoundary.com1);
			CommentsTeacherTextField.setText(AddExamsBoundary.com2);
		}
	}

	public static void flag() {
		flag2 = 0;
	}

	@SuppressWarnings("static-access")
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/AddExam2.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Add exam form");
		primaryStage.setScene(scene);
		primaryStage.show();
		this.pS = primaryStage;

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
	 * goto previous window
	 * 
	 * @param event user clicks back button
	 */
	@FXML
	void backBtn(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		dur = this.DurationTextField.getText().replaceAll("_", "1a6gf");
		exe = this.ExecutionCodeTextField.getText().replaceAll("_", "1a6gf");
		commentsForS = this.CommentsStudentsTextField.getText().replaceAll("_", "1a6gf");
		commentsForT = this.CommentsTeacherTextField.getText().replaceAll("_", "1a6gf");
		flag2 = 1;
		AddExamsBoundary.pS.show();
	}

	/**
	 * upload offline exam file to database via client
	 */
	private void sendoffline() {
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
			msg.setDuration(Integer.parseInt(this.DurationTextField.getText()));
			msg.setExecutionCode(this.ExecutionCodeTextField.getText());

			ClientUI.chat.accept(msg);
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR, "Error send (Files)msg) to Server", ButtonType.OK);
			alert.showAndWait();
			System.out.println("Error send (Files)msg) to Server");
			e.printStackTrace();
		}

		pS.hide();
	}

	/**
	 * if exam is online, the next window is a preview window. If the exam is
	 * offline, the exam is saved
	 * 
	 * @param event user clicks next button
	 */
	@FXML
	void NextButton(ActionEvent event) {
		if (MyClient.StatusOF.equals("invalid")) {
			Alert alert = new Alert(AlertType.WARNING, "Invalid execution code.", ButtonType.OK);
			alert.showAndWait();
			return;
		}

		if (AddExamsBoundary.onOffExamType.equals("0")) {
			sendoffline();
			return;
		}
		if (DurationTextField.getText().length() == 0) {
			Alert alert = new Alert(AlertType.WARNING, "Enter duration!", ButtonType.OK);
			alert.showAndWait();
			return;
		}

		String sr = DurationTextField.getText();
		if (sr.matches("[0-9]+")) {
			if (this.ExecutionCodeTextField.getText().length() != 4) {
				Alert alert = new Alert(AlertType.WARNING, "Execution code must be 4 characters!", ButtonType.OK);
				alert.showAndWait();
				return;
			}
			if (MyClient.excutioncodereq.equals("taken")) {
				Alert alert = new Alert(AlertType.WARNING, "Execution code is taken!\nPlease choose a different code.",
						ButtonType.OK);
				alert.showAndWait();
				return;
			}
			ClientUI.chat.accept("uniqueExecutionCode_" + this.ExecutionCodeTextField.getText());

			/*
			 * addExam_type_username_userpass_bankname_coursename_duration_execCode_StudentCom_TeacherCom_QIDs_Scores
			 */
			temp = "addExam" + "_" + AddExamsBoundary.onOffExamType + "_" + MyClientBoundary.usName + "_"
					+ MyClientBoundary.pass + "_" + AddExamsBoundary.subj + "_" + AddExamsBoundary.course + "_"
					+ this.DurationTextField.getText().replaceAll("_", "1a6gf") + "_"
					+ this.ExecutionCodeTextField.getText().replaceAll("_", "1a6gf") + "_"
					+ this.CommentsStudentsTextField.getText().replaceAll("_", "1a6gf") + " " + "_"
					+ this.CommentsTeacherTextField.getText().replaceAll("_", "1a6gf") + " " + "_"
					+ AddExamsBoundary.questID + "_" + AddExamsBoundary.questScore;
			System.out.println(temp);

			((Node) event.getSource()).getScene().getWindow().hide();
			PreviewAddExamBoundary previewExam = new PreviewAddExamBoundary();
			try {
				Stage primaryStage = new Stage();
				previewExam.start(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING, "Duration must be a number!", ButtonType.OK);
			alert.showAndWait();
		}
	}

	/**
	 * generate a unique execution code via client-server & inject it in field
	 * 
	 * @param event
	 */
	@FXML
	void generateExecCode(ActionEvent event) {
		ClientUI.chat.accept("generateUniqueExecutionCode");
		ExecutionCodeTextField.setText(MyClient.generatedCode);
	}

	/**
	 * user is logged out of the CEMS system, return to main login screen
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