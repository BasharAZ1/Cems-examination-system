package Server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Optional;
import Client.ClientUI;
//import com.sun.media.jfxmediaimpl.platform.Platform;
import Client.MyClient;
import Data.Person;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ocsf.server.ConnectionToClient;

/**
 * boundary class for MyServer - controller for the server GUI
 * 
 * @author G6
 *
 */
public class MyServerBoundary {
	enum Affiliation {
		Student, Teacher, Principal
	}

	private Pane root = new Pane();
	private Label lbl = new Label("CEMS Server");
	private Label lblTotalStudents = new Label("Total students logged in: ");
	private Label lblTotalTeachers = new Label("Total teachers logged in: ");
	private Label lblTotalPrincipals = new Label("Total principals logged in: ");
	private Label lblNumStudents = new Label("0");
	private Label lblNumTeachers = new Label("0");
	private Label lblNumPrincipals = new Label("0");
	private Label lblPortNumber = new Label("Server port number: 5555");
	private Button btnLoadData = new Button();
	private TableView<ClientInfo> tbl = new TableView<>();
	private TableColumn<ClientInfo, String> column1 = new TableColumn<>("ClientIP");
	private TableColumn<ClientInfo, String> column2 = new TableColumn<>("Host");
	private TableColumn<ClientInfo, String> column4 = new TableColumn<>("Username");
	private TableColumn<ClientInfo, String> column3 = new TableColumn<>("Status");
	private static boolean flag = false;
	ArrayList<ClientInfo> clientInfo = new ArrayList<>();
	ArrayList<Person> usersInfo = new ArrayList<>();

	final ObservableList<ClientInfo> data = FXCollections.observableArrayList(clientInfo);

	/**
	 * creating and initializing server gui window
	 * 
	 * @param primaryStage
	 * @throws Exception
	 */
	public void start(Stage primaryStage) throws Exception {
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));

		column1.setMinWidth(160);
		column1.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>("ClientIP"));
		column2.setMinWidth(250);
		column2.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>("Host"));
		column4.setMinWidth(160);
		column4.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>("Username"));
		column3.setMinWidth(160);
		column3.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>("Status"));

		tbl.getColumns().add(column1);
		tbl.getColumns().add(column2);
		tbl.getColumns().add(column4);
		tbl.getColumns().add(column3);

		tbl.setItems(data);
		tbl.setLayoutX(65);
		tbl.setLayoutY(90);
		root.getChildren().add(tbl);
		lblPortNumber.setLayoutX(100);
		lblPortNumber.setLayoutY(590);
		lblTotalStudents.setLayoutX(100);
		lblTotalStudents.setLayoutY(500);
		lblTotalTeachers.setLayoutX(100);
		lblTotalTeachers.setLayoutY(530);
		lblTotalPrincipals.setLayoutX(100);
		lblTotalPrincipals.setLayoutY(560);
		lblNumStudents.setLayoutY(500);
		lblNumStudents.setLayoutX(300);
		lblNumTeachers.setLayoutY(530);
		lblNumTeachers.setLayoutX(300);
		lblNumPrincipals.setLayoutY(560);
		lblNumPrincipals.setLayoutX(300);
		root.getChildren().addAll(lblTotalStudents, lblTotalTeachers, lblTotalPrincipals, lblNumStudents,
				lblNumTeachers, lblNumPrincipals, lblPortNumber);
		lbl.setLayoutX(380);
		lbl.setLayoutY(30);
		lbl.setStyle("-fx-font-family: Dubai;");
		root.getChildren().add(lbl);
		root.setBackground(new Background(new BackgroundFill(Color.LAVENDER, null, null)));
		btnLoadData.setText("Import Data");
		btnLoadData.setStyle("-fx-font-family: Dubai;");
		btnLoadData.setCursor(Cursor.HAND);
		btnLoadData.setLayoutX(65);
		btnLoadData.setLayoutY(20);
		btnLoadData.setTooltip(new Tooltip("Import data from external system"));
		btnLoadData.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// update database according to files
				ServerUI.sv.jdbc.loadDataFromFile();
			}
		});
		root.getChildren().add(btnLoadData);
		primaryStage.setTitle("CEMS Server");
		primaryStage.setScene(new Scene(root, 860, 650));
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

					System.exit(0);
				}
			}
		});
		ServerUI.runServer(ServerUI.DEFAULT_PORT);
	}

	/**
	 * update the labels of serverBoundary after client connected
	 * 
	 * @param host
	 * @param ip
	 * @param username
	 * @param status online or offline
	 * @param aff affiliation - teacher, student, principal
	 */
	public void updateServerBoundary(String host, InetAddress ip, String username, String status, Affiliation aff) {
		javafx.application.Platform.runLater(new Runnable() {
			@Override
			public void run() {

				updateUserInfo(host, ip.toString(), username, status);
				updateLabels(status.equals("Connected") ? 1 : -1, aff);
				refreshClientTable();
			}
		});
	}

	/**
	 * updates number of connected users by affiliation
	 * 
	 * @param status of the user (connected-1, disconnected-0)
	 * @param aff    affiliation of the user (teacher, student, principal)
	 */
	public void updateLabels(int status, Affiliation aff) {

		if (aff != null) {
			javafx.application.Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int sumStudents = status + Integer.parseInt(lblNumStudents.getText());
					if (sumStudents < 0)
						sumStudents = 0;
					int sumTeachers = status + Integer.parseInt(lblNumTeachers.getText());
					if (sumTeachers < 0)
						sumTeachers = 0;
					int sumPrincipals = status + Integer.parseInt(lblNumPrincipals.getText());
					if (sumPrincipals < 0)
						sumPrincipals = 0;
					if (aff.equals(Affiliation.Student))
						lblNumStudents.setText(String.valueOf(sumStudents));
					else if (aff.equals(Affiliation.Teacher))
						lblNumTeachers.setText(String.valueOf(sumTeachers));
					else if (aff.equals(Affiliation.Principal))
						lblNumPrincipals.setText(String.valueOf(sumPrincipals));
				}
			});
		}
	}

	/**
	 * updates the ClientInfo array list that holds all details concerning
	 * connected/disconnected clients
	 * 
	 * @param host
	 * @param ip
	 * @param status
	 */
	public void updateClientInfo(String host, String ip, String username, String status) {

		ClientInfo cic = new ClientInfo(host, ip, username, "Connected");
		ClientInfo cid = new ClientInfo(host, ip, username, "Disconected");
		if (clientInfo.contains(cic) || clientInfo.contains(cid)) { /* client already in list */
			for (int i = 0; i < clientInfo.size(); i++) {
				if ((clientInfo.get(i)).equals(cic) || (clientInfo.get(i)).equals(cid))
					(clientInfo.get(i)).setStatus(status); /* update client's status */
			}
		} else
			clientInfo.add(new ClientInfo(host, ip, username, status)); /* client new to list */
	}

	public void updateUserInfo(String host, String ip, String username, String status) {
		ClientInfo user = new ClientInfo(host, ip, username, status);
		boolean updated = false;

		if (!username.equals("") && !username.equals("null")) {
			for (int i = 0; i < clientInfo.size(); i++)
				if ((clientInfo.get(i)).equals(user) && ((clientInfo.get(i)).getUsername().equals(username))) {
					(clientInfo.get(i)).setStatus(status); /* update client's status */
					(clientInfo.get(i)).setUsername(username);
					updated = true;
				}

			if (!updated && user.getUsername() != null) {
				clientInfo.add(user);
			}
		}
	}

	/**
	 * refreshes the server gui table according to the clientInfo array list
	 */
	public void refreshClientTable() {
		data.clear(); /* clear list */
		System.out.println("refreshing table");
		Collections.sort(clientInfo, new Comparator<ClientInfo>() {
			public int compare(ClientInfo c1, ClientInfo c2) {
				return c1.getStatus().compareTo(c2.getStatus());
			}
		});
		for (int i = 0; i < clientInfo.size(); i++) /* update data according to clientInfo */
		{
			data.add(clientInfo.get(i));
		}
	}

	/**
	 * notify user import from external system is successful
	 */
	public void importSucess() {

		javafx.application.Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Database Import Status");
				alert.setHeaderText("Database Import");
				alert.setContentText("Import from external system successful");
				alert.showAndWait();
				btnLoadData.setDisable(true);
				btnLoadData.setText("Data Imported");
			}
		});
	}

	/**
	 * notify user import from external system failed
	 */
	public void importFail() {

		javafx.application.Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Database Import Status");
				alert.setHeaderText("Database Import");
				alert.setContentText("Import from external system failed");
				alert.showAndWait();
				btnLoadData.setDisable(true);
			}
		});
	}
}