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
 * class for viewing student copy of exam GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class ViewExamCopyBoundary {

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
	private Label lblIndetifier;

	@FXML
	private ScrollPane sPane;

	@FXML
	private VBox VBContainer;

	@FXML
	private Button EditOnlExamBtn1;

	@FXML
	private Label teacherCmt;

	@FXML
	private Label studentCmt;
	@FXML
	private Label GradeSys;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		// getCopyOfExam_examID_teacherName
		// teacherLastname_subject_course_studentComment_teacherComment_questionText_ans1_ans2_ans3_ans4_chosenAns_correctAns_qScore_actualqScore

		ObservableList<String> list = FXCollections.observableArrayList();
		list.addAll("1", "2", "3", "4");
		GradeSys.setText("System Grade: " + MyClient.FinalGrade);
		GradeSys.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		subject.setText("Subject: " + MyClient.Subject);
		subject.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		course.setText("Course: " + MyClient.Course);
		course.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		teacherName.setText("Teacher: " + MyClient.TeacherName);
		teacherName.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		teacherCmt.setText("Comment for teachers: " + MyClient.TeacherComments);
		teacherCmt.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		studentCmt.setText("Comment for students: " + MyClient.StudentComments);
		studentCmt.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		int j = 0;
		for (int i = 0; j < MyClient.questionsize; i += 5) {
			Label questionLb = new Label(MyClient.QuestionforCopy.get(j).getQuestionText());
			questionLb.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: purple");
			int actualScore = MyClient.QuestionforCopy.get(j).getActualScore();
			Label scoreLb = new Label("(" + actualScore + " points of out "
					+ Integer.toString(MyClient.QuestionforCopy.get(j).getScore()) + ")");
			scoreLb.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: purple");

			HBox txtAndScoreContainer = new HBox();
			txtAndScoreContainer.getChildren().add(0, questionLb);
			txtAndScoreContainer.getChildren().add(1, scoreLb);
			txtAndScoreContainer.setSpacing(10);

			VBContainer.getChildren().add(i, txtAndScoreContainer);

			String ans1 = new String(MyClient.QuestionforCopy.get(j).getAnswer1().getAnswerText());
			String ans2 = new String(MyClient.QuestionforCopy.get(j).getAnswer2().getAnswerText());
			String ans3 = new String(MyClient.QuestionforCopy.get(j).getAnswer3().getAnswerText());
			String ans4 = new String(MyClient.QuestionforCopy.get(j).getAnswer4().getAnswerText());

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

			int correctAns = MyClient.QuestionforCopy.get(j).getCorrectAns();
			int chosenAns = MyClient.QuestionforCopy.get(j).getChosenAns();

			if (chosenAns == 1)
				ans1Lb.setStyle(
						"-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: white; -fx-background-color: #e55b2f;");
			if (chosenAns == 2)
				ans2Lb.setStyle(
						"-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: white; -fx-background-color: #e55b2f;");
			if (chosenAns == 3)
				ans3Lb.setStyle(
						"-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: white; -fx-background-color: #e55b2f;");
			if (chosenAns == 4)
				ans4Lb.setStyle(
						"-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: white; -fx-background-color: #e55b2f;");

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
		MyClient.questionsize = 0;
		VBContainer.setSpacing(15);

	}

	/**
	 * start GUI to preview students exam
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/PreviewOnlineExamination.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Grade approval form");
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
	 * back to grade approval second phase GUI
	 * 
	 * @param event user clicks on back button
	 */
	@FXML
	void back(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		GradeApproval2Boundary ga2 = new GradeApproval2Boundary();
		Stage primaryStage = new Stage();
		try {
			ga2.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * logout, return to login screen
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