package Exam;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import java.io.*;

import Client.ClientUI;
import Client.MyClient;
import Data.MyFile;
import Login.MyClientBoundary;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

/**
 * teacher offline exam GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class offlineExam {
	public static String strpath;

	public void start(Stage stage) {
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		try {
			strpath = "";
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
							Alert alert = new Alert(AlertType.WARNING, "invalid file type.", ButtonType.OK);
							alert.showAndWait();
							return;
						}
						AddExamsBoundary.onOffExamType = "0";
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
		}

		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * add offline exam GUI
	 */
	private void nextpage() {
		// (Node) event.getSource()).getScene().getWindow().hide(); // hide window
		AddOfflineExam ae2 = new AddOfflineExam();
		Stage primaryStage = new Stage();
		try {
			ae2.start(primaryStage);
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