package Student;

import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
import Data.DoneExam;
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
 * student view grade GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class StudentViewGradeBoundary {

	@FXML
	private Button logoutViewGradesBtn;

	@FXML
	private Button StudRetCopyBtn;

	@FXML
	private TableView<DoneExam> ExamSelect;

	@FXML
	private TableColumn<DoneExam, String> ExamId;

	@FXML
	private TableColumn<DoneExam, String> Subject;

	@FXML
	private TableColumn<DoneExam, String> Course;

	@FXML
	private TableColumn<DoneExam, String> Grade;

	@FXML
	private Button backVie;

	@FXML
	private Label fullName;

	static ObservableList<DoneExam> data2;
	DoneExam select;

	@FXML
	private void initialize() {
		fullName.setText(MyClient.studentFullName);

		ClientUI.chat.accept("GetDoneExamForStudent_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		data2 = FXCollections.observableArrayList(MyClient.DoneExams);
		ExamId.setCellValueFactory(new PropertyValueFactory<DoneExam, String>("ExamID"));
		Subject.setCellValueFactory(new PropertyValueFactory<DoneExam, String>("subjectName"));
		Course.setCellValueFactory(new PropertyValueFactory<DoneExam, String>("courseName"));
		Grade.setCellValueFactory(new PropertyValueFactory<DoneExam, String>("Grade"));
		ExamSelect.setItems(data2);
	}

	/**
	 * start stage on view grades
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Student/ViewGrades.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("View grades form");
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
	 * view exams copy
	 * 
	 * @param event user clicks view copy button
	 */
	@FXML
	void viewCopy(ActionEvent event) {
		int selectedExamIndex = ExamSelect.getSelectionModel().getFocusedIndex();
		ClientUI.chat.accept("getCopyOfExam_" + MyClient.DoneExams.get(selectedExamIndex).getExamID() + "_"
				+ MyClientBoundary.usName + "_" + MyClientBoundary.pass);
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		ViewExaminationCopyBoundary vec = new ViewExaminationCopyBoundary();
		Stage ps = new Stage();
		try {
			vec.start(ps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * back to students main screen
	 * 
	 * @param event user clicks back button
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

	/**
	 * logout, back to login screen
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