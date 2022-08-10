package Login;

import java.util.Optional;

import Client.ClientUI;
import Client.MyClient;
import Exam.TeacherBoundary;
import Principal.PrincipalBoundary;
import Student.StudentBoundary;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * boundary class for MyClient
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class MyClientBoundary {

	@FXML
	private Label CemsLabel;

	@FXML
	private TextField UsernameTxt;

	@FXML
	private PasswordField PasswordTxt;

	@FXML
	private Text ForgotPass;

	@FXML
	private Button LoginBtn;

	public static String usName, pass;

	public String getUsName() {
		return usName;
	}

	public void setUsName(String usName) {
		this.usName = usName;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	@FXML
	private void initialize() {
		this.UsernameTxt.setOnAction(e -> {
			loginEnter(e);
		});
		this.PasswordTxt.setOnAction(e -> {
			loginEnter(e);
		});
	}

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Login/Login.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("User form");
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
	 * if user-name and password are correct, user is logged into the system
	 * 
	 * @param e2 user hits enter key
	 */
	@FXML
	public void loginEnter(ActionEvent e2) {
		String str = tryToLogIn(UsernameTxt.getText(), PasswordTxt.getText());
		if (str == null)
			return;

		((Node) e2.getSource()).getScene().getWindow().hide();

		switch (str) {
		case "Teacher": {
			TeacherBoundary tb = new TeacherBoundary();
			Stage ps = new Stage();
			try {
				tb.start(ps);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case "Student": {
			StudentBoundary sb = new StudentBoundary();
			Stage ps = new Stage();
			try {
				sb.start(ps);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case "Principal": {
			PrincipalBoundary pb = new PrincipalBoundary();
			Stage ps = new Stage();
			try {
				pb.start(ps);

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		default:
		}
	}

	public String getUserTypeFromDB(String username, String password) { // REFACTORING

		String msg = "login_" + username + "_" + password + "_";
		ClientUI.chat.accept(msg);
		if (MyClient.alreadyLogged.equals("deny"))
			return "alreadyLogged";
		if (MyClient.invalidLogin.equals("deny"))
			return "invalidLogin";
		String role =  MyClient.role;
		
		return role;
	}

	public String tryToLogIn(String username, String password) {  // REFACTORING
		String str = getUserTypeFromDB(username, password);
		
		if (str.equals("invalidLogin")) {
			alertError("Invalid username or password.");
			return null;
		}
		else if (str.equals("alreadyLogged")) {
			alertError("User already logged in.");
			return null;
		}
		usName = username;
		pass=password;
		return str;
	}
	/**
	 * if user-name and password are correct, user is logged into the system
	 * 
	 * @param event user clicks login button
	 */
	@FXML
	public void login(MouseEvent event) {
		String str = tryToLogIn(UsernameTxt.getText(), PasswordTxt.getText());
		if (str == null) {
			UsernameTxt.clear();
			PasswordTxt.clear();
			return;
		}
		Stage ps = new Stage();
		switch (str) {
		case "Teacher":
			((Node) event.getSource()).getScene().getWindow().hide();
			TeacherBoundary tb = new TeacherBoundary();
			try {
				tb.start(ps);

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case "Student":
			((Node) event.getSource()).getScene().getWindow().hide();
			StudentBoundary sb = new StudentBoundary();
			try {
				sb.start(ps);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case "Principal":
			((Node) event.getSource()).getScene().getWindow().hide();
			PrincipalBoundary pb = new PrincipalBoundary();
			try {
				pb.start(ps);

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

	}

	/**
	 * show GUI alert of type error to user
	 * 
	 * @param msg alert error message
	 */
	public void alertError(String msg) {
		javafx.application.Platform.runLater(new Runnable() {
			@Override
			public void run() {

				Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
				alert.showAndWait();
			}
		});
	}

	/**
	 * show GUI alert of type error to user
	 * 
	 * @param msg alert error message
	 */
	public static void staticAlertError(String msg) {
		javafx.application.Platform.runLater(new Runnable() {
			@Override
			public void run() {

				Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
				alert.showAndWait();
			}
		});
	}
	/**
	 * show GUI alert of type information
	 * 
	 * @param msg alert of type information
	 */
	public void alertInfo(String msg) {
		javafx.application.Platform.runLater(new Runnable() {
			@Override
			public void run() {

				Alert alert = new Alert(AlertType.INFORMATION, msg, ButtonType.OK);
				alert.showAndWait();
			}
		});
	}
	
	/**
	 * show GUI alert of type information
	 * 
	 * @param msg alert of type information
	 */
	public static void staticAlertInfo(String msg) {
		javafx.application.Platform.runLater(new Runnable() {
			@Override
			public void run() {

				Alert alert = new Alert(AlertType.INFORMATION, msg, ButtonType.OK);
				alert.showAndWait();
			}
		});
	}
}