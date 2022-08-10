package Exam;

import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * start final phase of editing exam GUI, preview for final save
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class PreviewEditExamBoundary {

	@FXML
	private ImageView loginOnlBtn;

	@FXML
	private Button logoutOnlineExamBtn;

	@FXML
	private Label course;

	@FXML
	private Label teacherName;

	@FXML
	private Label subject;

	@FXML
	private ScrollPane sPane;

	@FXML
	private VBox VBContainer;

	@FXML
	private Button EditOnlExamBtn1;

	@FXML
	private Button SaveBtn;

	@FXML
	private Label teacherComments;

	@FXML
	private Label studentComments;

	@FXML
	private Label duration;

	@FXML
	private Label lblIndetifier;

	Stage pS;
	public static String Cor = null;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		/*
		 * saved in AddExam2Boundary.temp:
		 * addExam_type_username_userpass_bankname_coursename_duration_execCode_StudentCom_TeacherCom
		 * (here index 9)_QIDs_Scores
		 */
		VBContainer.getChildren().clear();

		String[] decodeMsg = EditExam3Boundary.temp5.split("_");

		ObservableList<String> list = FXCollections.observableArrayList();
		list.addAll("1", "2", "3", "4");

		subject.setText("Subject: " + decodeMsg[3]);
		subject.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		course.setText("Course: " + decodeMsg[4]);
		course.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		duration.setText("Exam duration: " + decodeMsg[5] + " minutes");
		duration.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		teacherName.setText("Teacher: " + MyClient.teacherFullName);
		teacherName.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		studentComments.setText("Comment for students: " + decodeMsg[7]);
		studentComments.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 10; -fx-text-fill: purple");

		teacherComments.setText("Comment for teachers: " + decodeMsg[8]);
		teacherComments.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 10; -fx-text-fill: purple");

		int j = 0;
		for (int i = 0; j < EditExam2Boundary.questableEditAL.size(); i += 5) {
			Label questionLb = new Label(EditExam2Boundary.questableEditAL.get(j).getQuestionText());
			questionLb.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: purple");

			Label scoreLb = new Label(
					"(" + Integer.toString(EditExam2Boundary.questableEditAL.get(j).getScore()) + " points)");
			scoreLb.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: purple");

			HBox txtAndScoreContainer = new HBox();
			txtAndScoreContainer.getChildren().add(0, questionLb);
			txtAndScoreContainer.getChildren().add(1, scoreLb);
			txtAndScoreContainer.setSpacing(10);

			VBContainer.getChildren().add(i, txtAndScoreContainer);

			String ans1 = new String(EditExam2Boundary.questableEditAL.get(j).getAnswer1().getAnswerText());
			String ans2 = new String(EditExam2Boundary.questableEditAL.get(j).getAnswer2().getAnswerText());
			String ans3 = new String(EditExam2Boundary.questableEditAL.get(j).getAnswer3().getAnswerText());
			String ans4 = new String(EditExam2Boundary.questableEditAL.get(j).getAnswer4().getAnswerText());

			String ACon = new String("1) ");
			String BCon = new String("2) ");
			String CCon = new String("3) ");
			String DCon = new String("4) ");

			String ACon2 = ACon.concat(ans1);
			Label ans1Lb = new Label(ACon2);

			String BCon2 = BCon.concat(ans2);
			Label ans2Lb = new Label(BCon2);

			String CCon2 = CCon.concat(ans3);
			Label ans3Lb = new Label(CCon2);

			String DCon2 = DCon.concat(ans4);
			Label ans4Lb = new Label(DCon2);

			int correctAns = EditExam2Boundary.questableEditAL.get(j).getCorrectAns();

			if (correctAns == 1)
				ans1Lb.setStyle(
						"-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: purple; -fx-font-weight: bold; -fx-background-color: #37e52f;");
			if (correctAns == 2)
				ans2Lb.setStyle(
						"-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: purple; -fx-font-weight: bold; -fx-background-color: #37e52f;");
			if (correctAns == 3)
				ans3Lb.setStyle(
						"-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: purple; -fx-font-weight: bold; -fx-background-color: #37e52f;");
			if (correctAns == 4)
				ans4Lb.setStyle(
						"-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: purple; -fx-font-weight: bold; -fx-background-color: #37e52f;");

			VBContainer.getChildren().add(i + 1, ans1Lb);
			VBContainer.getChildren().add(i + 2, ans2Lb);
			VBContainer.getChildren().add(i + 3, ans3Lb);
			VBContainer.getChildren().add(i + 4, ans4Lb);
			j++;
		}
		VBContainer.setSpacing(15);
	}

	/**
	 * start preview edited exam GUI
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/PreviewEditExam.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Preview exam form");
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
	 * edit more parts of the exam
	 * 
	 * @param event user clicks edit button
	 */
	@FXML
	void edit(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		EditExam3Boundary.pS2.show();
	}

	/**
	 * save edited exam in data base
	 * 
	 * @param event user clicks save button
	 */
	@FXML
	void saveAction(ActionEvent event) {
		ClientUI.chat.accept(EditExam3Boundary.temp5);
		EditExam3Boundary.flag1 = 0;
		EditExam3Boundary.temp5 = "";

		((Node) event.getSource()).getScene().getWindow().hide();
		EditExam1Boundary editExam = new EditExam1Boundary();
		try {
			Stage primaryStage = new Stage();
			editExam.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
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