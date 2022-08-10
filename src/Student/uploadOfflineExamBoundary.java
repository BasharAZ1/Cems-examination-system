package Student;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import Client.ClientUI;
import Client.MyClient;
import Data.MyFile;
import Data.MyFile.FileSource;
import Exam.offlineExam;
import Login.MyClientBoundary;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * upload offline exam GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class uploadOfflineExamBoundary {
	public static String strpath;
	public static MyFile msg = new MyFile(offlineExam.strpath);
	public static int flag = 0;

	/**
	 * start stage of uploading offline exam
	 * 
	 * @param stage set stage for uploading offline exam
	 */
	public void start(Stage stage) {
		try {
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
			// set title for the stage
			stage.setTitle("FileChooser");

			// create a File chooser
			FileChooser fil_chooser = new FileChooser();

			// create a Label
			Label label = new Label("no files selected");

			// create a Button
			Button button = new Button("Choose File (Docx)");

			// create an Event Handler
			EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {

				public void handle(ActionEvent e) {
					// get the file selected
					File file = fil_chooser.showOpenDialog(stage);
					if (file != null) {
						strpath = file.getAbsolutePath();
						MyFile filecheck = new MyFile(strpath);
						if (!filecheck.isPrefixValid()) {
							Alert alert = new Alert(AlertType.ERROR, "invalid file type.", ButtonType.OK);
							alert.showAndWait();
							return;
						}
						nextpage();
						stage.close();
					}
				}
			};
			button.setOnAction(event);

			// create a VBox
			VBox vbox = new VBox(30, label, button);

			// set Alignment
			vbox.setAlignment(Pos.CENTER);

			// create a scene
			Scene scene = new Scene(vbox, 800, 500);

			// set the scene
			stage.setScene(scene);

			stage.show();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * go to next page
	 * 
	 */
	private void nextpage() {
		flag = 1;
		String LocalfilePath = strpath;
		System.out.println(strpath);
		int bg = 0;
		for (int i = LocalfilePath.length(); i > 0; i--) {
			char c = LocalfilePath.charAt(i - 1);
			if (c == ('\\')) {
				bg = i;
				i = 0;
			}
		}
		String filename = LocalfilePath.substring(bg, LocalfilePath.length());
		msg.setFileName(filename);
		try {
			File newFile = new File(LocalfilePath);
			byte[] mybytearray = new byte[(int) newFile.length()];
			FileInputStream fis = new FileInputStream(newFile);
			@SuppressWarnings("resource")
			BufferedInputStream bis = new BufferedInputStream(fis);
			msg.setFileSource(FileSource.StudentSubmitOfflineExam);
			msg.initArray(mybytearray.length);
			msg.setSize(mybytearray.length);
			msg.setUsername(MyClientBoundary.usName);
			msg.setPassword(MyClientBoundary.pass);
			bis.read(msg.getMybytearray(), 0, mybytearray.length);
			msg.setExecutionCode(OfflineExaminationBoundary.excode);

		} catch (Exception e) {
			System.out.println("Error send (Files)msg) to Server");
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