package Exam;

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
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * start second phase of grade approval GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class GradeApproval2Boundary {
	@FXML
	private Button logoutbtn;

	@FXML
	private TableView<DoneExam> studentsTabel;

	@FXML
	private TableColumn<DoneExam, String> StuIDField;

	@FXML
	private TableColumn<DoneExam, Integer> GradeField;

	@FXML
	private TableColumn<DoneExam, String> SuspOfCheat;

	@FXML
	private Button ApprGradeBtn;

	@FXML
	private Button ViewExamBtn;

	@FXML
	private TextField TeacherComm;

	@FXML
	private Button ChangeGradeBtn;

	@FXML
	private Button backbtn;

	@FXML
	private Button savebtn;

	@FXML
	private Label lblIndetifier;

	static ObservableList<DoneExam> data;
	ObservableList<String> sus;
	DoneExam selected;
	static String stuID;
	int examGrade = -1;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		TeacherComm.setText("");
		stuID = "";
		StuIDField.setCellValueFactory(new PropertyValueFactory<DoneExam, String>("StudentID"));
		GradeField.setCellValueFactory(new PropertyValueFactory<DoneExam, Integer>("Grade"));
		SuspOfCheat.setCellValueFactory(new PropertyValueFactory<DoneExam, String>("CopyStatus"));
		data = FXCollections.observableArrayList(MyClient.Grades);
		studentsTabel.setItems(data);
	}

	/**
	 * start second phase grade approval GUI
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage is unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/GradeApproval2.fxml"));
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
	 * choose an exam and approve its grade
	 * 
	 * @param event user clicks approve button
	 */
	@FXML
	void Approve(ActionEvent event) {
		String str = " ";
		if (TeacherComm.getText().length() == 0)
			str = " ";
		else
			str = TeacherComm.getText();

		int selectedStuIndex = studentsTabel.getSelectionModel().getFocusedIndex();
		stuID = studentsTabel.getSelectionModel().getSelectedItem().getStudentID();
		String temp = "approveGrade" + "_" + GradeApprovalBoundary.Id + "_" + stuID + "_" + str;
		System.out.println(temp);
		ClientUI.chat.accept(temp);

//		System.out.println("\ngrade was successfuly changed.\n");

		selectedStuIndex = studentsTabel.getSelectionModel().getFocusedIndex();
		studentsTabel.getItems().remove(selectedStuIndex);
	}

	/**
	 * go to grade approval first phase GUI
	 * 
	 * @param event user clicks back button
	 */
	@FXML
	void Back(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		GradeApprovalBoundary.prevSubCou = true;
		GradeApprovalBoundary ga = new GradeApprovalBoundary();
		Stage primaryStage = new Stage();
		try {
			ga.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Optional<String> getValueFromUser() {
		TextInputDialog dialog = new TextInputDialog("...");
		dialog.setTitle("Score Input");
		dialog.setHeaderText("Enter score:");
		dialog.setContentText("Score:");
		return dialog.showAndWait();
	}

	/**
	 * user changes system given grade for exam must enter reason
	 * 
	 * @param event user clicks change grade button
	 */
	@FXML
	void ChangeGrade(ActionEvent event) {
		boolean valid=false;
		TextInputDialog dialog = new TextInputDialog("Enter a reason");
		dialog.setTitle("Enter a reason");
		dialog.setHeaderText("Please enter a reason for changing the grade");
		dialog.setContentText("Reason:");
		Optional<String> op = dialog.showAndWait();
		String input = op.get();
		if (input.length()==0)
		{
			Alert alert = new Alert(AlertType.WARNING, "You must enter a reason for changing the grade", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		String examGradetemp="";
		int selectedStuIndex = studentsTabel.getSelectionModel().getFocusedIndex();
		String copySta = studentsTabel.getSelectionModel().getSelectedItem().getCopyStatus();
		while (!valid) {
			Optional<String> i = getValueFromUser();
			examGradetemp = i.get();
			if (!examGradetemp.matches("[0-9]+"))
			{
				Alert alert = new Alert(AlertType.WARNING, "Grade must be an integer.", ButtonType.OK);
				alert.showAndWait();
			}
			
			else if (Integer.parseInt(examGradetemp) > 100 || Integer.parseInt(examGradetemp) < 0) {
				Alert alert = new Alert(AlertType.WARNING, "Enter legal grade (between 0 and 100).", ButtonType.OK);
				alert.showAndWait();
			}
			else
				valid=true;
		}
		examGrade = Integer.parseInt(examGradetemp);
		studentsTabel.getItems().remove(selectedStuIndex);
		stuID = MyClient.Grades.get(selectedStuIndex).getStudentID();
		copySta = MyClient.Grades.get(selectedStuIndex).getCopyStatus();
		MyClient.Grades.remove(selectedStuIndex);

		StuIDField.setCellValueFactory(new PropertyValueFactory<DoneExam, String>("StudentID"));
		GradeField.setCellValueFactory(new PropertyValueFactory<DoneExam, Integer>("Grade"));
		SuspOfCheat.setCellValueFactory(new PropertyValueFactory<DoneExam, String>("CopyStatus"));

		DoneExam doneExamTemp = new DoneExam();
		doneExamTemp.setStudentID(stuID);
		doneExamTemp.setExamID(GradeApprovalBoundary.Id);
		doneExamTemp.setGrade(examGrade);
		doneExamTemp.setCopyStatus(copySta);
		MyClient.Grades.add(selectedStuIndex, doneExamTemp);
		data = FXCollections.observableArrayList(MyClient.Grades);
		studentsTabel.setItems(data);

		String temp = "changeGrade" + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass + "_"
				+ GradeApprovalBoundary.Id + "_" + stuID + "_" + examGrade + "_" + input + "_" + TeacherComm.getText();
		System.out.println("\nChange grade: " + temp);
		ClientUI.chat.accept(temp);
		refreshTable();
	}

	/**
	 * refreshes pending grades table
	 */
	private void refreshTable() {
		ClientUI.chat
		.accept("getPendingGrades" + "_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass + "_" + GradeApprovalBoundary.Id);
		studentsTabel.getItems().clear();
		javafx.application.Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (MyClient.Grades != null) {
					data = FXCollections.observableArrayList(MyClient.Grades);
					studentsTabel.setItems(data);
				}
				
			}
		});
		
	}

	/**
	 * user views student exam copy GUI
	 * 
	 * @param event user clicks view exam button
	 */
	@FXML
	void ViewExam(ActionEvent event) {
		int selectedStuIndex = studentsTabel.getSelectionModel().getFocusedIndex();
		String stdid = data.get(selectedStuIndex).getStudentID();
		ClientUI.chat.accept("getCopyOfExam_" + GradeApprovalBoundary.Id + "_" + stdid);
		if (MyClient.offlineExamCopy == true)
			return;

		((Node) event.getSource()).getScene().getWindow().hide();
		ViewExamCopyBoundary ga = new ViewExamCopyBoundary();
		Stage primaryStage = new Stage();
		try {
			ga.start(primaryStage);
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