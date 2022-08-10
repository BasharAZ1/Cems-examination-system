package Server;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * user interface of CEMS server - starts server & GUI
 * @author Ayala Cohen
 *
 */
public class ServerUI extends Application {
	final public static int DEFAULT_PORT = 5555;
	public Stage primaryStage;
	public static MyServerBoundary sb;
	public static MyServer sv;
	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	/**
	 * create serverBoundary object
	 */
	public void start(Stage pS) throws Exception {
		primaryStage = pS;
		sb = new MyServerBoundary();
		sb.start(primaryStage);
	}

	/**
	 * server start listening for clients
	 * 
	 * @param p
	 */
	public static void runServer(int p) {
		int port = 0; // Port to listen on

		try {
			port = DEFAULT_PORT; // Set port to 5555

		} catch (Throwable t) {
			System.out.println("ERROR - Could not connect!");
		}

		 sv = new MyServer(port);

		try {
			sv.listen(); // Start listening for connections
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}
}