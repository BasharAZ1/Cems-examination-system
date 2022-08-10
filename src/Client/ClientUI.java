package Client;

import Exam.TeacherBoundary;
import Login.MyClientBoundary;
import Principal.PrincipalBoundary;
import Student.StudentBoundary;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * class instantiates the client's boundary
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class ClientUI extends Application {
	public static ClientController chat;
	public MyClientBoundary bd;
	public TeacherBoundary td;
	public Stage primaryStage;

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * start new client connection to boundary
	 */
	@Override
	public void start(Stage pS) throws Exception {
		chat = new ClientController("localhost", 5555);
		primaryStage = pS;
		bd = new MyClientBoundary();
		bd.start(primaryStage);
	}
}