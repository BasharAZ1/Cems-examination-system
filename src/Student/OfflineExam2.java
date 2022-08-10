package Student;

import java.util.Optional;


import Client.ClientUI;
import Client.MyClient;
import Data.MyFile;
import Data.MyFile.FileSource;
import Login.MyClientBoundary;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * offline exam GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class OfflineExam2 {

	@FXML
	private Button btnBack;

	@FXML
	private SplitPane screen;

	@FXML
	private AnchorPane mainscreen;

	@FXML
	private Pane screenpane;

	@FXML
	private Button StudDwnldExamBtn;

	@FXML
	private Button subOffExamBtn;

	@FXML
	private Label fullName;

	@FXML
	private Label hours;

	@FXML
	private Label minutes;

	@FXML
	private Label seconds;

	@FXML
	private Button subExamBtn;

	@FXML
	private Label twoMinsLeft;

	public static String exeCo;
	Timeline myTimer;
	int counter = 0;
	int hoursTime, minTime, secTime;
	int flag = 0, flag1 = 0;;
	Stage pS;

	/**
	 * initialize GUI with back button disabled
	 */
	@FXML
	private void initialize() {
		btnBack.setDisable(true);
		fullName.setText(MyClient.studentFullName);
		twoMinsLeft.setVisible(false);
	}

	/**
	 * start offline exam window
	 * 
	 * @param primaryStage
	 * @throws Exception
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Student/OfflineExam2.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Offline examination form");
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
	 * downloads exam to user's filesystem
	 * 
	 * @param event user clicks Download button to get the exam to his computer
	 */
	@FXML
	void download(ActionEvent event) {
		exeCo = OfflineExaminationBoundary.excode;
		ClientUI.chat.accept("getOfflineExam_" + exeCo + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		if (!MyClient.StatusOF.equals("decline")) {
			counter = MyClient.timerStartExam;
			System.out.println("exams duration is " + counter);
			flag = 0;
			hoursTime = counter / 60;
			minTime = counter - (hoursTime * 60);
			secTime = 0;

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
						if (hoursTime == 0 && minTime == 0 && secTime == 0) {
							Alert alert = new Alert(AlertType.INFORMATION, "The exam's time is over.\nGood luck!",
									ButtonType.OK);
							Platform.runLater(alert::showAndWait);
						} else if (MyClient.stopExamination.equals("lock")) {
							Alert alert = new Alert(AlertType.INFORMATION, "The exam is locked.\nGood luck!",
									ButtonType.OK);
							Platform.runLater(alert::showAndWait);
						}
						timerOrLockSubmitExam();
					}
				}
			}));
			myTimer.setCycleCount(Timeline.INDEFINITE);
			myTimer.play();
		} else
			javafx.application.Platform.runLater(new Runnable() {

				@Override
				public void run() {
					btnBack.setDisable(false);
				}
			});
	}

	/**
	 * submits exam to database via client
	 * 
	 * @param event user clicks submit after uploading exam
	 */
	@FXML
	void submitExam(ActionEvent event) {
		int n = -1;
		if (uploadOfflineExamBoundary.flag == 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to submit an empty exam?",
					ButtonType.YES, ButtonType.NO);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES) {
			    n=0;
			}
			else if (alert.getResult() == ButtonType.NO) {
			    n=1;
			}

		}
		if (n == 1) // Picked not to submit
			return;

		myTimer.stop();

		if (n == 0) {
			MyFile emptyFile = new MyFile(null);
			emptyFile.setExecutionCode(exeCo);
			emptyFile.setUsername(MyClientBoundary.usName);
			emptyFile.setPassword(MyClientBoundary.pass);
			emptyFile.setFileSource(FileSource.StudentSubmitOfflineExam);
			ClientUI.chat.accept(emptyFile);
			((Node) event.getSource()).getScene().getWindow().hide(); // hide window
			StudentBoundary sb = new StudentBoundary();
			Stage ps = new Stage();
			try {
				sb.start(ps);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			ClientUI.chat.accept(uploadOfflineExamBoundary.msg);

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

	/**
	 * after exam is locked, user now can return to main window
	 */
	void timerOrLockSubmitExam() {
		javafx.application.Platform.runLater(new Runnable() {

			@Override
			public void run() {
				btnBack.setDisable(false);
			}
		});
		ClientUI.chat.accept(uploadOfflineExamBoundary.msg);
	}

	/**
	 * uploads user's exam from filesystem
	 * 
	 * @param event user clicks upload button to upload his exam from filesystem
	 */
	@FXML
	void upload(ActionEvent event) {
		exeCo = OfflineExaminationBoundary.excode;
		uploadOfflineExamBoundary ae = new uploadOfflineExamBoundary();
		Stage primaryStage = new Stage();
		ae.start(primaryStage);
	}

	/**
	 * returns to student main window
	 * 
	 * @param event user clicks Back button to return to main window after exam's
	 *              time is up
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