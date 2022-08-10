package Principal;

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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * class to present student reports for principal user
 * 
 * @author Bashar Ali & Samr Arkab
 */
public class StudentReportPrincipal {
	@FXML
	private Button logoutPRepBtn;

	@FXML
	private BarChart<String, Number> graph;

	@FXML
	private Label graphNameLabel;

	@FXML
	private Label graphAverageLabel;

	@FXML
	private Label graphMedianLabel;

	@FXML
	private Button backPrincipalgGraphBtn;

	@FXML
	private TableView<DoneExam> tblStudReport;

	@FXML
	private TableColumn<DoneExam, String> colExam;

	@FXML
	private TableColumn<DoneExam, Integer> colExamDur;

	@FXML
	private TableColumn<DoneExam, Integer> colActualDur;

	@FXML
	private TableColumn<DoneExam, String> colFinishTime;

	@FXML
	private TableColumn<DoneExam, Integer> colGrade;

	@FXML
	private Label fullName;

	private ObservableList<DoneExam> data1;

	@SuppressWarnings("unchecked")
	@FXML
	private void initialize() {
		fullName.setText(MyClient.principalFullName);

		graphNameLabel.setText(ReportsBoundary.Studentname);
		graphAverageLabel.setText(MyClient.Static.get(1));
		graphMedianLabel.setText(MyClient.Static.get(2));
		for (int i = 3; i < MyClient.Static.size(); i++) {
			Series<String, Number> set1 = new XYChart.Series<>();
			set1.getData().add(new XYChart.Data<String, Number>(MyClient.Static.get(i),
					Integer.parseInt(MyClient.Static.get(i + 1))));
			i = i + 4;
			graph.getData().addAll(set1);
		}
		data1 = FXCollections.observableArrayList(MyClient.DoneExamsForReport);
		colExam.setCellValueFactory(new PropertyValueFactory<DoneExam, String>("ExamID"));
		colExamDur.setCellValueFactory(new PropertyValueFactory<DoneExam, Integer>("Duration"));
		colActualDur.setCellValueFactory(new PropertyValueFactory<DoneExam, Integer>("ActualDuration"));
		colFinishTime.setCellValueFactory(new PropertyValueFactory<DoneExam, String>("TimeOfsub"));
		colGrade.setCellValueFactory(new PropertyValueFactory<DoneExam, Integer>("Grade"));
		tblStudReport.setItems(data1);
	}

	/**
	 * starts the student reports screen for principal user
	 * 
	 * @param primaryStage Stage object
	 * @throws Exception in case starting the stage was unsuccessful
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Principal/PrincipalStudentReport.fxml"));
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("Principal students report form");
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
	 * go back to main reports screen for principal user
	 * 
	 * @param event user clicks on "back" button
	 */
	@FXML
	void back(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide(); // hide window
		ReportsBoundary svgb = new ReportsBoundary();
		Stage primaryStage = new Stage();
		try {
			svgb.start(primaryStage);
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