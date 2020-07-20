package application;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainMenuControler {	
	
	@FXML
	private Button btn_continu;
	
	@FXML
    public void initialize() {
	
		// check if we have a current graph
		File[] temp_files = new File("./CurrentGraph").listFiles(new FilenameFilter() { 
			 @Override public boolean accept(File dir, String name) { return name.endsWith(".csv"); } });

		// make directory if it dosen't exist
		 if (temp_files==null) {
			 new File("./CurrentGraph").mkdirs();	
			}
		 else
		 {
		 
		 //check if empty
			if(temp_files.length!=0){
				// if it is not make the button available
				btn_continu.setDisable(false);
			}
			else
			{
				btn_continu.setDisable(true);
			}
		 }
		
	}
	
	public void continu(ActionEvent event)throws IOException
	{
		// go to editor
		
		// change window
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Editor.fxml"));
		Parent temp_parent = (Parent)loader.load();
		Scene temp_scene = new Scene(temp_parent);
		Stage app_Stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		//keep the current window size
		double stage_height = app_Stage.getHeight();
		double stage_width = app_Stage.getWidth();
		
		// change scene
		app_Stage.setScene(temp_scene);
		
		// set window size to previous scene
		app_Stage.setHeight(stage_height);
		app_Stage.setWidth(stage_width);
		
		EditorControler controller = loader.getController();		
		controller.addMainDisplaySizeListeners(app_Stage.getScene().getHeight(),app_Stage.getScene().getWidth());
		
	}
	
	public void makeNewGraph(ActionEvent event)throws IOException
	{	
		
		// change window
		
		Parent temp_parent = FXMLLoader.load(getClass().getResource("NewGraph.fxml"));
		Scene temp_scene = new Scene(temp_parent);
		Stage app_Stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		//keep the current window size
		double stage_height = app_Stage.getHeight();
		double stage_width = app_Stage.getWidth();
		
		// change scene
		app_Stage.setScene(temp_scene);
		
		// set window size to previous scene
		app_Stage.setHeight(stage_height);
		app_Stage.setWidth(stage_width);		
		
	}
	
	public void openGraph(ActionEvent event)throws IOException
	{	
		
		// change window
		
		Parent temp_parent = FXMLLoader.load(getClass().getResource("OpenGraph.fxml"));
		Scene temp_scene = new Scene(temp_parent);
		Stage app_Stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		//keep the current window size
		double stage_height = app_Stage.getHeight();
		double stage_width = app_Stage.getWidth();
		
		// change scene
		app_Stage.setScene(temp_scene);
		
		// set window size to previous scene
		app_Stage.setHeight(stage_height);
		app_Stage.setWidth(stage_width);		
		
	}
	
	public void goOtherFeatures(ActionEvent event)throws IOException
	{
		// go to OtherFeatures
		
		// change window
		FXMLLoader loader = new FXMLLoader(getClass().getResource("OtherFeatures_MainMenu.fxml"));
		Parent temp_parent = (Parent)loader.load();
		Scene temp_scene = new Scene(temp_parent);
		Stage app_Stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		//keep the current window size
		double stage_height = app_Stage.getHeight();
		double stage_width = app_Stage.getWidth();
		
		// change scene
		app_Stage.setScene(temp_scene);
		
		// set window size to previous scene
		app_Stage.setHeight(stage_height);
		app_Stage.setWidth(stage_width);
		
	}
	
	
	public void exitApp(ActionEvent event)
	{	
		
			Platform.exit();
			System.exit(0);
		
	}
	
}
