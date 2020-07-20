package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;


public class MainMenu extends Application {
	
	public static void main(String[] args) {
		  launch(args);
		 }
	
	 @Override
	 public void start(Stage primaryStage) throws IOException {
	
	 Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
	 
	 Scene scene = new Scene(root);
	 scene.getStylesheets().add(getClass().getResource("MainMenu.css").toExternalForm());
	 primaryStage.setTitle("Simulator");
	 primaryStage.setScene(scene);
	 
	 // The user can't resize the window smaller than 600*600
	 primaryStage.setMinHeight(600);
	 primaryStage.setMinWidth(600);
	 
	 primaryStage.show();
	 
	 
	 primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	       @Override
	       public void handle(WindowEvent e) {
	          Platform.exit();
	          System.exit(0);
	       }
	    });
	
	 }
}