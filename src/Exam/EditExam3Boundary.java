package Exam;

import java.util.ArrayList;
import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
import Data.MyFile;
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
 * edit exam third phase GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class EditExam3Boundary {
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
	private Label lblIndetifier;

	public static Stage pS2;

	public static MyFile msgEdit = new MyFile(offlineExam.strpath);

	public static int flag1 = 1;
	public static String temp5, durEditExam, exeEditExam, commentsForSEditExam, commentsForTEditExam;
	public static ArrayList<QuestionInExam> quesinEditExam = new ArrayList<>();

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		DurationTextField.setText(Integer.toString(MyClient.exam2.getDuration()));
		ExecutionCodeTextField.setText(MyClient.exam2.getExecutionCode());
		ExecutionCodeTextField.setDisable(true);
		CommentsStudentsTextField.setText(MyClient.exam2.getStudentComment().getFreeText());
		CommentsTeacherTextField.setText(MyClient.exam2.getTeacherComment().getFreeText());

	}

	/**
	 * helper for saving info when initializing pages in editing exam stages
	 */
	public static void flag() {
		flag1 = 0;
	}

	/**
	 * start edit exam third phase GUI
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting stage was unsuccessful
	 */
	@SuppressWarnings("static-access")
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/EditExam33.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Edit exam form");
		primaryStage.setScene(scene);
		primaryStage.show();

		this.pS2 = primaryStage;

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
	 * return to second phase GUI of editing exam initialize text fields
	 * 
	 * @param event user clicks back button
	 */
	@FXML
	void backBtn(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		durEditExam = this.DurationTextField.getText().replaceAll("_", "1a6gf");
		exeEditExam = this.ExecutionCodeTextField.getText().replaceAll("_", "1a6gf");
		commentsForSEditExam = this.CommentsStudentsTextField.getText().replaceAll("_", "1a6gf");
		commentsForTEditExam = this.CommentsTeacherTextField.getText().replaceAll("_", "1a6gf");
		flag1 = 1;
		EditExam2Boundary.pSEditExam.show();
	}

	/**
	 * if info is valid, go to preview edited exam GUI
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

			ClientUI.chat.accept("uniqueExecutionCode_" + this.ExecutionCodeTextField.getText());

			/*
			 * editExam_oldExamID_type_bankName_courseName_duration_execCode_StudentCom_TeacherCom_QIDs_Scores
			 */
			if (this.CommentsStudentsTextField.getText() == null)
				this.CommentsStudentsTextField.setText("");
			if (this.CommentsTeacherTextField.getText() == null)
				this.CommentsTeacherTextField.setText("");

			temp5 = "editExam" + "_" + EditExam1Boundary.Id + "_1_" + EditExam2Boundary.subjEdit + "_"
					+ EditExam2Boundary.courseEditExam + "_" + this.DurationTextField.getText().replaceAll("_", "1a6gf")
					+ "_" + this.ExecutionCodeTextField.getText().replaceAll("_", "1a6gf") + "_"
					+ this.CommentsStudentsTextField.getText().replaceAll("_", "1a6gf") + " " + "_"
					+ this.CommentsTeacherTextField.getText().replaceAll("_", "1a6gf") + " " + "_"
					+ EditExam2Boundary.questIDEdit + "_" + EditExam2Boundary.questScoreEdit;
			System.out.println(temp5);

			((Node) event.getSource()).getScene().getWindow().hide();
			PreviewEditExamBoundary previewExam = new PreviewEditExamBoundary();
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