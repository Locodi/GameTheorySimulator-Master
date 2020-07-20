package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OtherFeatures_MainMenuControler {


	public void goBack(ActionEvent event)throws IOException
	{	
		
		// change window
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
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
	
	public void goOnePermutationSimulation(ActionEvent event)throws IOException
	{			
		
		// change window
		FXMLLoader loader = new FXMLLoader(getClass().getResource("OtherFeatures_OnePermutationSimulation.fxml"));
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
	
	public void goPayoffsTest(ActionEvent event)throws IOException
	{			
		
		// change window
		FXMLLoader loader = new FXMLLoader(getClass().getResource("OtherFeatures_PayoffsTest.fxml"));
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
	
	
}
