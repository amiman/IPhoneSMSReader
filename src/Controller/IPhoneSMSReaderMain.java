package Controller;

import java.io.IOException;

import Controller.Model.Model;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class IPhoneSMSReaderMain extends Application {

	private AnchorPane mainLayout;
	private Model model;

	@Override
	public void start(Stage primaryStage) {

		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(IPhoneSMSReader.class.getResource("View/MainView.fxml"));
			mainLayout = (AnchorPane) loader.load();
			
			// Show the scene containing the root layout.
			Scene scene = new Scene(mainLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// Create model
			model = new Model(scene,primaryStage);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
