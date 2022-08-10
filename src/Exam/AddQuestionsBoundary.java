package Exam;

import java.util.ArrayList;
import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
import Data.Course;
import Data.Subject;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

/**
 * in this window teacher creates a new question and saves it in database
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class AddQuestionsBoundary {
	@FXML
	private TextArea Option1Txt;

	@FXML
	private TextArea Option3Txt;

	@FXML
	private TextArea Option4Txt;

	@FXML
	private TextArea Option2Txt;

	@FXML
	private ComboBox<String> subjectBox;

	@FXML
	private ComboBox<String> CorrectAnBtn;

	@FXML
	private ComboBox<String> courseBox;

	@FXML
	private TableColumn<Course, String> c1;

	@FXML
	private Button backBtn;

	@FXML
	private Button saveBtn;

	@FXML
	private TextArea QuestionTxt;

	@FXML
	private Button logoutAddQBtn;

	@FXML
	private Label lblIndetifier;

	@FXML
	private TableView<Course> SelectedCourse;

	private ObservableList<Subject> dataSubjects;
	private ObservableList<Course> data2;
	private ObservableList<Course> data1;
	static ArrayList<Course> Courselist = new ArrayList<>();
	Course selected = new Course();
	int flag = 0;

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		ObservableList<String> list = FXCollections.observableArrayList();
		list.addAll("1", "2", "3", "4");
		CorrectAnBtn.setItems(list);

		ClientUI.chat.accept("getSubjects_" + MyClientBoundary.usName + "_" + MyClientBoundary.pass);

		dataSubjects = FXCollections.observableArrayList(MyClient.subjects1);
		ObservableList<String> list2 = FXCollections.observableArrayList();
		for (int i = 0; i < dataSubjects.size(); i++) {
			String str;
			str = dataSubjects.get(i).getSubjectName();
			list2.add(str);
		}

		subjectBox.setItems(list2);
	}

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/AddQuestions.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Add questions form");
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
	 * get all relevant courses under given subject
	 * 
	 * @param event user chose a subject from the combo-box
	 */
	@FXML
	void subjChoice(ActionEvent event) {
		Courselist.clear();
		SelectedCourse.getItems().clear();
		ClientUI.chat.accept("getCoursesUnderSubject&Teacher_" + subjectBox.getValue());

		data2 = FXCollections.observableArrayList(MyClient.courses);
		ObservableList<String> list2 = FXCollections.observableArrayList();

		for (int i = 0; i < data2.size(); i++) {
			String str;
			str = data2.get(i).getCourseName();
			list2.add(str);
		}
		courseBox.setItems(list2);
	}

	/**
	 * saves question in database
	 * 
	 * @param event user clicks save button
	 */
	@FXML
	void savebtn(ActionEvent event) {
		if (subjectBox.getValue() == null) {
			Alert alert = new Alert(AlertType.WARNING, "Choose subject for question.", ButtonType.OK);
			alert.showAndWait();
			return;

		}
		if (courseBox.getValue() == null) {
			Alert alert = new Alert(AlertType.WARNING, "Choose course for question.", ButtonType.OK);
			alert.showAndWait();
			return;

		}
		if (CorrectAnBtn.getValue() == null) {
			Alert alert = new Alert(AlertType.WARNING, "Choose correct answer for question.", ButtonType.OK);
			alert.showAndWait();
			return;

		}
		if (QuestionTxt.getText().equals("")) {
			Alert alert = new Alert(AlertType.WARNING, "Fill question field.", ButtonType.OK);
			alert.showAndWait();
			return;

		}
		if (Option1Txt.getText().equals("")) {
			Alert alert = new Alert(AlertType.WARNING, "Fill answer 1 field.", ButtonType.OK);
			alert.showAndWait();
			return;

		}
		if (Option2Txt.getText().equals("")) {
			Alert alert = new Alert(AlertType.WARNING, "Fill answer 2 field.", ButtonType.OK);
			alert.showAndWait();
			return;

		}
		if (Option3Txt.getText().equals("")) {
			Alert alert = new Alert(AlertType.WARNING, "Fill answer 3 field.", ButtonType.OK);
			alert.showAndWait();
			return;

		}
		if (Option4Txt.getText().equals("")) {
			Alert alert = new Alert(AlertType.WARNING, "Fill answer 4 field.", ButtonType.OK);
			alert.showAndWait();
			return;
		}

		String str1 = new String();
		for (int i = 0; i < Courselist.size(); i++) {
			if (Courselist.get(i).getCourseName() != null)
				str1 += Courselist.get(i).getCourseName() + "-";
		}

		String temp = "addQuestion" + "_" + this.subjectBox.getValue() + "_" + MyClientBoundary.usName + "_"
				+ MyClientBoundary.pass + "_" + this.QuestionTxt.getText().replaceAll("_", "1a6gf") + "_"
				+ this.Option1Txt.getText().replaceAll("_", "1a6gf") + "_"
				+ this.Option2Txt.getText().replaceAll("_", "1a6gf") + "_"
				+ this.Option3Txt.getText().replaceAll("_", "1a6gf") + "_"
				+ this.Option4Txt.getText().replaceAll("_", "1a6gf") + "_" + this.CorrectAnBtn.getValue() + "_"
				+ str1.substring(0, str1.length() - 1) + "_";
		ClientUI.chat.accept(temp);
		QuestionTxt.setText("");
		Option1Txt.setText("");
		Option2Txt.setText("");
		Option3Txt.setText("");
		Option4Txt.setText("");
		CorrectAnBtn.setValue("");
		Courselist.clear();
		SelectedCourse.getItems().clear();
		courseBox.setValue(null);
	}

	/**
	 * go to questions window
	 * 
	 * @param event user clicks back button
	 */
	@FXML
	void backbtn(ActionEvent event) {
		Courselist.clear();
		((Node) event.getSource()).getScene().getWindow().hide();
		TeacherQuestionsBoundary tqb = new TeacherQuestionsBoundary();
		Stage primaryStage = new Stage();
		try {
			tqb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * associates question with course
	 * 
	 * @param event user chose a course from combo-box
	 */
	@FXML
	void AddCours(ActionEvent event) {
		if (courseBox.getValue() == null)
			return;
		int flag = 0;
		String str1;
		str1 = courseBox.getValue();
		c1.setCellValueFactory(new PropertyValueFactory<Course, String>("courseName"));
		Course temp = new Course();
		temp.setCourseName(str1);
		if (temp.getCourseName() == null)
			return;
		for (int i = 0; i < Courselist.size(); i++) {
			if (Courselist.get(i).getCourseName().equals(temp.getCourseName()))
				flag = 1;
		}
		if (flag == 1) {
			Alert alert = new Alert(AlertType.WARNING, "Course already added", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		Courselist.add(temp);
		data1 = FXCollections.observableArrayList(Courselist);
		SelectedCourse.setItems(data1);
	}

	/**
	 * remove selected course from question's associated course list
	 * 
	 * @param event user clicked remove button
	 */
	@FXML
	void Remove(ActionEvent event) {
		selected = SelectedCourse.getSelectionModel().getSelectedItem();
		SelectedCourse.getItems().remove(selected);
		Courselist.remove(selected);
	}

	/**
	 * user logs out of CEMS, back to main login screen
	 * 
	 * @param event user clicks Logout button
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