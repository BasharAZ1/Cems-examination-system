package Exam;

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
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * start edit question second phase GUI
 * 
 * @author Bashar Ali & Samr Arkab
 *
 */
public class EditQuestion2Boundary {
	@FXML
	private Button logoutEditQuestBtn;

	@FXML
	private TextArea ChangeQuestionTxt;

	@FXML
	private TextArea changeAns1Txt;

	@FXML
	private TextArea changeAns3Txt;

	@FXML
	private TextArea changeAns4Txt;

	@FXML
	private TextArea changeAns2Txt;

	@FXML
	private ComboBox<String> CangeCorrectAnBtn;

	@FXML
	private Button save;

	@FXML
	private Button backbtn;

	@FXML
	private Pane pane;

	@FXML
	private Label lblIndetifier;

	CheckBox[] sele;
	boolean noSave = false;

	/**
	 * if question editing was not save - show alert return to first screen of
	 * editing question GUI
	 * 
	 * @param event user clicks back button
	 */
	@FXML
	void back(ActionEvent event) {
		if (noSave == false) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Go to previous page without saving?\nAre you sure?",
					ButtonType.YES, ButtonType.NO);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.NO)
				return;

			((Node) event.getSource()).getScene().getWindow().hide();
			EditQuestion1Boundary tb = new EditQuestion1Boundary();
			Stage primaryStage = new Stage();
			try {
				tb.start(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * save question edit clear text fields
	 * 
	 * @param event user clicks save button
	 */
	@FXML
	void save(ActionEvent event) {
		noSave = true;
		if (CangeCorrectAnBtn.getValue() == null) {
			Alert alert = new Alert(AlertType.WARNING, "Fill correct answer field.", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		String temp, temp1 = "";
		temp = "editQuestion_" + EditQuestion1Boundary.Bankname + "_" + EditQuestion1Boundary.str1 + "_"
				+ ChangeQuestionTxt.getText() + "_" + changeAns1Txt.getText() + "_" + changeAns2Txt.getText() + "_"
				+ changeAns3Txt.getText() + "_" + changeAns4Txt.getText() + "_" + CangeCorrectAnBtn.getValue() + "_";
		System.out.println(temp);

		for (int i = 0; i < EditQuestion1Boundary.data2.size(); i++) {
			if (sele[i].isSelected()) {
				temp1 += EditQuestion1Boundary.data2.get(i).getCourseName();
				temp += EditQuestion1Boundary.data2.get(i).getCourseName() + "-";
			}
		}
		if (temp1.length() == 0) {
			Alert alert = new Alert(AlertType.WARNING, "No courses choosen!", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		ClientUI.chat.accept(temp);

		ChangeQuestionTxt.setText("");
		changeAns1Txt.setText("");
		changeAns2Txt.setText("");
		changeAns3Txt.setText("");
		changeAns4Txt.setText("");
		CangeCorrectAnBtn.setValue("");
		for (int i = 0; i < EditQuestion1Boundary.data2.size(); i++) {
			if (sele[i].isSelected()) {
				sele[i].setSelected(false);
			}
		}
		((Node) event.getSource()).getScene().getWindow().hide();
		EditQuestion1Boundary tb = new EditQuestion1Boundary();
		Stage primaryStage = new Stage();
		try {
			tb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void initialize() {
		lblIndetifier.setText(MyClient.teacherFullName);

		ClientUI.chat.accept("getQuestion_" + EditQuestion1Boundary.str1);
		ChangeQuestionTxt.setText(MyClient.question.getQuestionText());
		changeAns1Txt.setText(MyClient.question.getAnswer1().getAnswerText());
		changeAns2Txt.setText(MyClient.question.getAnswer2().getAnswerText());
		changeAns3Txt.setText(MyClient.question.getAnswer3().getAnswerText());
		changeAns4Txt.setText(MyClient.question.getAnswer4().getAnswerText());
		ObservableList<String> list = FXCollections.observableArrayList();
		list.addAll("1", "2", "3", "4");
		CangeCorrectAnBtn.setItems(list);
		sele = new CheckBox[EditQuestion1Boundary.data2.size()];
		int j = 0;
		int i = 0;
		for (i = 0; i < EditQuestion1Boundary.data2.size(); i++) {
			CheckBox cb = new CheckBox(EditQuestion1Boundary.data2.get(i).getCourseName());
			cb.setLayoutX(14);
			cb.setLayoutY(149 + j);
			@SuppressWarnings("unused")
			String s = String.valueOf(i);
			j = j + 20;
			for (int k = 0; k < MyClient.QuestionsForEdit.size(); k++) {
				if (MyClient.QuestionsForEdit.get(k).toString()
						.equals(EditQuestion1Boundary.data2.get(i).getCourseName())) {
					cb.setSelected(true);
				}
			}
			pane.getChildren().add(cb);
			sele[i] = cb;
		}
		int temp = MyClient.question.getCorrectAns();
		CangeCorrectAnBtn.valueProperty().set(Integer.toString(temp));
	}

	/**
	 * start edit question second phase GUI
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Exam/EditQuestion2.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Add exam form");
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