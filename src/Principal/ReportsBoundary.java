package Principal;

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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * class to present reports boundary for principal user
 * 
 * @author Bashar Ali & Samr Arkab
 */
public class ReportsBoundary {
	@FXML
	private Button logOurBtn;

	@FXML
	private ComboBox<String> CourseList;

	@FXML
	private ComboBox<String> TeacherList;

	@FXML
	private ComboBox<String> StudentList;

	@FXML
	private Button cours;

	@FXML
	private Button Teacher;

	@FXML
	private Button Student;

	@FXML
	private Button backBtn;

	@FXML
	private Label fullName;

	private ObservableList<String> data2;
	private ObservableList<String> data1;
	private ObservableList<String> data3;
	public static String Coursename, Teachername, Studentname;

	@FXML
	private void initialize() {
		fullName.setText(MyClient.principalFullName);

		ClientUI.chat.accept("GetAllStudents");
		data3 = FXCollections.observableArrayList(MyClient.Students);
		ClientUI.chat.accept("GetAllTeachers");
		data1 = FXCollections.observableArrayList(MyClient.teacher);
		ClientUI.chat.accept("GetAllCourses");
		data2 = FXCollections.observableArrayList(MyClient.Coursesname);
		ObservableList<String> list1 = FXCollections.observableArrayList();
		ObservableList<String> list2 = FXCollections.observableArrayList();
		ObservableList<String> list3 = FXCollections.observableArrayList();

		for (int i = 0; i < data1.size(); i++) {
			String str1;
			str1 = data1.get(i);
			list1.add(str1);
		}
		for (int i = 0; i < data2.size(); i++) {
			String str2;
			;
			str2 = data2.get(i);
			list2.add(str2);

		}
		for (int i = 0; i < data3.size(); i++) {
			String str3;
			str3 = data3.get(i);
			list3.add(str3);
		}

		CourseList.setItems(list2);
		TeacherList.setItems(list1);
		StudentList.setItems(list3);
	}

	/**
	 * starts the main reports screen for principal user
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Principal/ReportsMenu.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Principal reports form");
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
	 * opens stage of course reports for principal user
	 * 
	 * @param event user mouse click on get reports for courses
	 */
	@FXML
	void getCoursereport(ActionEvent event) {
		Coursename = CourseList.getValue();
		ClientUI.chat.accept("getCourseReport_" + Coursename);
		if (MyClient.getreport.equals("No"))
			return;
		if (Coursename != null) {
			((Node) event.getSource()).getScene().getWindow().hide(); // hide window
			CourseReportPrincipal teb = new CourseReportPrincipal();
			Stage primaryStage = new Stage();
			try {
				teb.start(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * opens stage of student reports for principal user
	 * 
	 * @param event user mouse click on get reports for students
	 */
	@FXML
	void getStudentreport(ActionEvent event) {
		Studentname = StudentList.getValue();
		String str = Studentname;
		int index = 0;
		for (int i = 0; i < MyClient.Students.size(); i++) {
			if (str.equals(MyClient.Students.get(i))) {
				index = i;
				i = MyClient.Students.size();
			}

		}
		str = MyClient.Idsforpersons.get(index);
		ClientUI.chat.accept("getStudentReportForPrincipal_" + str);
		if (MyClient.getreport.equals("No"))
			return;
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		StudentReportPrincipal teb = new StudentReportPrincipal();
		Stage primaryStage = new Stage();
		try {
			teb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * opens stage of teacher reports for principal user
	 * 
	 * @param event user mouse click on get reports for teachers
	 */
	@FXML
	void getTeacherreport(ActionEvent event) {
		Teachername = TeacherList.getValue();
		String str = Teachername;
		int index = 0;
		for (int i = 0; i < MyClient.teacher.size(); i++) {
			if (str.equals(MyClient.teacher.get(i))) {
				index = i;
				i = MyClient.teacher.size();
			}
		}
		str = MyClient.Idsforpersons1.get(index);
		ClientUI.chat.accept("getTeacherReportForPrincipal_" + str);
		if (MyClient.getreport.equals("No"))
			return;
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		TeacherReportPrincipal teb = new TeacherReportPrincipal();
		Stage primaryStage = new Stage();
		try {
			teb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * go back to main principal screen
	 * 
	 * @param event user clicks on "back" button
	 */
	@FXML
	void back(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		PrincipalBoundary teb = new PrincipalBoundary();
		Stage primaryStage = new Stage();
		try {
			teb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * logout user, go back to MyClientBoundary (login screen)
	 * 
	 * @param event user clicks on "logout" button
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