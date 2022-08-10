package Student;

import java.util.Optional;


import Client.ClientUI;
import Client.MyClient;
import Login.MyClientBoundary;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/***
 * online exam GUI class
 * @author Bashar Ali & Samr Arkab
 *
 */
public class OnlineExamination2Boundary {
	

    @FXML
    private Button btnBack;

	@FXML
	private Label hours;

	@FXML
	private Label minutes;

	@FXML
	private Label seconds;

	@FXML
	private Button subExamBtn;

	@FXML
	private Label subject;

	@FXML
	private Label course;

	@FXML
	private Label fullName;

	@FXML
	private Label teacherName;

	@FXML
	private VBox VBContainer;

	@FXML
	private Button backOnExamBtn;

	@FXML
	private Label cmtStudent;

	@FXML
	private Label twoMinsLeft;

	Stage pS;
	Timeline myTimer;
	int counter = 0;
	int hoursTime, minTime, secTime;
	boolean first;

	/**
	 * initialize GUI with full online exam questions, possible answers and choice combo-box
	 */
	@FXML
	private void initialize() {
		fullName.setText(MyClient.studentFullName);
		btnBack.setDisable(true);
		twoMinsLeft.setVisible(false);

		counter = MyClient.timerStartExam;
		System.out.println("exams duration is " + counter);
		hoursTime = counter / 60;
		minTime = counter - (hoursTime * 60);
		secTime = 0;
		first = true;

		myTimer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (hoursTime < 10)
					hours.setText("   0" + Integer.toString(hoursTime));
				else
					hours.setText("    " + Integer.toString(hoursTime));
				if (minTime < 10)
					minutes.setText("   0" + Integer.toString(minTime));
				else
					minutes.setText("  " + Integer.toString(minTime));
				if (secTime < 10)
					seconds.setText("   0" + Integer.toString(secTime));
				else
					seconds.setText("  " + Integer.toString(secTime));

				secTime--;

				if (hoursTime < 0)
					hoursTime = 0;

				if (minTime < 0) {
					minTime = 59;
					hoursTime--;
				}

				if (secTime < 0) {
					secTime = 59;
					if (first == true) {
						secTime = 58;
						first = false;
					}
					minTime--;
				}

				if (MyClient.timeChanged == true) {
					MyClient.timeChanged = false;
					counter = MyClient.timerChanged;
					hoursTime = counter / 60;
					minTime = counter - (hoursTime * 60);
					secTime = 0;
				}

				if (hoursTime == 0 && minTime == 2 && secTime == 0) {
					hours.setStyle("-fx-text-fill: #da1e1b");
					minutes.setStyle("-fx-text-fill: #da1e1b");
					seconds.setStyle("-fx-text-fill: #da1e1b");
					twoMinsLeft.setVisible(true);
				}

				if ((hoursTime == 0 && minTime == 0 && secTime == 0) || MyClient.stopExamination.equals("lock")) {
					myTimer.stop();
					if (hoursTime == 0 && minTime == 0 && secTime == 0)
					{
						Alert alert = new Alert(AlertType.INFORMATION, "The exam's time is over.\nGood luck!",
								ButtonType.OK);
						Platform.runLater(alert::showAndWait);
						timerOrLockSubmitExam();
					}
					else if (MyClient.stopExamination.equals("lock"))
					{
						Alert alert = new Alert(AlertType.INFORMATION, "The exam is locked.\nGood luck!",
								ButtonType.OK);
						Platform.runLater(alert::showAndWait);
						timerOrLockSubmitExam();
					}
					
				}
			}
		}));
		myTimer.setCycleCount(Timeline.INDEFINITE);
		myTimer.play();

		ObservableList<String> list = FXCollections.observableArrayList();
		list.addAll("1", "2", "3", "4");

		subject.setText("Subject: " + MyClient.Subject);
		subject.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		course.setText("Course: " + MyClient.Course);
		course.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		teacherName.setText("Teacher: " + MyClient.TeacherName);
		teacherName.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 14; -fx-text-fill: purple");

		cmtStudent.setText("Comment for students: " + MyClient.StudentComments);
		cmtStudent.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 10; -fx-text-fill: purple");

		int j = 0;
		for (int i = 0; j < MyClient.questionsize; i += 6) {

			Label questionLb = new Label(MyClient.QuestionforExam.get(j).getQuestionText());
			questionLb.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: purple");

			Label scoreLb = new Label("(" + Integer.toString(MyClient.QuestionforExam.get(j).getScore()) + " points)");
			scoreLb.setStyle("-fx-font-family:Dubai Light; -fx-font-size: 16; -fx-text-fill: purple");

			HBox txtAndScoreContainer = new HBox();
			txtAndScoreContainer.getChildren().add(0, questionLb);
			txtAndScoreContainer.getChildren().add(1, scoreLb);
			txtAndScoreContainer.setSpacing(10);

			VBContainer.getChildren().add(i, txtAndScoreContainer);

			String ans1 = new String(MyClient.QuestionforExam.get(j).getAnswer1().getAnswerText());
			String ans2 = new String(MyClient.QuestionforExam.get(j).getAnswer2().getAnswerText());
			String ans3 = new String(MyClient.QuestionforExam.get(j).getAnswer3().getAnswerText());
			String ans4 = new String(MyClient.QuestionforExam.get(j).getAnswer4().getAnswerText());

			String ACon = new String("1) ");
			String BCon = new String("2) ");
			String CCon = new String("3) ");
			String DCon = new String("4) ");

			String ACon2 = ACon.concat(ans1);
			Label ans1Lb = new Label(ACon2);
			VBContainer.getChildren().add(i + 1, ans1Lb);

			String BCon2 = BCon.concat(ans2);
			Label ans2Lb = new Label(BCon2);
			VBContainer.getChildren().add(i + 2, ans2Lb);

			String CCon2 = CCon.concat(ans3);
			Label ans3Lb = new Label(CCon2);
			VBContainer.getChildren().add(i + 3, ans3Lb);

			String DCon2 = DCon.concat(ans4);
			Label ans4Lb = new Label(DCon2);
			VBContainer.getChildren().add(i + 4, ans4Lb);

			ComboBox<String> temp = new ComboBox<>();
			temp.setItems(list);
			temp.setValue("1");
			VBContainer.getChildren().add(i + 5, temp);
			j++;
		}
		VBContainer.setSpacing(15);
	}

	/**
	 * start the GUI window
	 * @param primaryStage
	 * @throws Exception
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Student/OnlineExamination2.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Online examination form");
		primaryStage.setScene(scene);
		primaryStage.show();
		pS = primaryStage;
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
	 * submits user's exam to database via client
	 * @param event user clicks submit button
	 */
	@SuppressWarnings("unchecked")
	@FXML
	void submitExam(ActionEvent event) {
		myTimer.stop();
		// submitExam_examID_username_password_qID1-ans1_qID2-ans2..
		String temp = "submitExam_" + MyClient.ExamID + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass
				+ "_";
		int j = 0;
		for (int i = 0; j < MyClient.questionsize; i += 6) {
			temp += MyClient.QuestionforExam.get(j).getQuestionID() + "-";
			ComboBox<String> tempCB = new ComboBox<>();
			tempCB = (ComboBox<String>) VBContainer.getChildren().get(i + 5);
			temp += tempCB.getValue() + "_";
			j++;
			// "qid1" + "-" + "ans1" + "_" + "...";
		}
		MyClient.questionsize = 0;
		ClientUI.chat.accept(temp);

		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		StudentBoundary sb = new StudentBoundary();
		Stage ps = new Stage();
		try {
			sb.start(ps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * if user did not finish in time, exam is automatically being submitted.
	 * back button now can be pressed to return to main window 
	 */
	@SuppressWarnings("unchecked")
	void timerOrLockSubmitExam() {
		// submitExam_examID_username_password_qID1-ans1_qID2-ans2..
		
		javafx.application.Platform.runLater(new Runnable() {

			@Override
			public void run() {
				btnBack.setDisable(false);
				subExamBtn.setDisable(true);
			}
		});
		
		
		
		String temp = "submitExam_" + MyClient.ExamID + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass
				+ "_";
		int j = 0;
		for (int i = 0; i < MyClient.questionsize; i++) {
			temp += MyClient.QuestionforExam.get(i).getQuestionID() + "-";
			ComboBox<String> tempCB = new ComboBox<>();
			tempCB = (ComboBox<String>) VBContainer.getChildren().get(j + 5);
			temp += tempCB.getValue() + "_";
			j = i + 6;
			// "qid1" + "-" + "ans1" + "_" + "...";
		}
		MyClient.questionsize = 0;
		ClientUI.chat.accept(temp);
		
	}
	
	/**
	 * goes back to main window
	 * @param event user clicks back button after exam time is over
	 */
    @FXML
    void back(ActionEvent event) {
    	((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		StudentBoundary sb = new StudentBoundary();
		Stage ps = new Stage();
		try {
			sb.start(ps);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}