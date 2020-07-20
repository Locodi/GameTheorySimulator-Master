package application;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

public class OpenGraphControler {
	@FXML
	private Label lbl_errorMessage;
	
	//The table
	@FXML
	private TableView<File> tb_files;
	
	//Column for the name of the files
	@FXML
	private TableColumn<File, String> cl_Name;
	
	//Column for the last time the files were modified
	@FXML
	private TableColumn<File, String> cl_LastModified;
	
	@FXML
    public void initialize() {

		ObservableList<File> list = FXCollections.observableArrayList(); 		
		
		//get list of all graph files		 
		File[] files = new File("./Graphs").listFiles(new FilenameFilter() { 
			 @Override public boolean accept(File dir, String name) { return name.endsWith(".csv"); } });
	
		tb_files.setPlaceholder(new Label("No Graphs!"));
		if (files==null) {
		 new File("./Graphs").mkdirs();		
			}
		else
		{
			for (File file : files) {
			    if (file.isFile()) {		    	 
			    	 //add the files to the list
			    	 list.add(file);		    	 
			     }
			 }
					
			cl_Name.setCellValueFactory(new Callback<CellDataFeatures<File, String>, ObservableValue<String>>() {
			     public ObservableValue<String> call(CellDataFeatures<File, String> p) {			        
			         return new SimpleStringProperty(p.getValue().getName());
			     }
			  });
					 
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			 
			cl_LastModified.setCellValueFactory(new Callback<CellDataFeatures<File, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<File, String> p) {
		         // p.getValue() returns the Person instance for a particular TableView row
		         return new SimpleStringProperty(sdf.format(p.getValue().lastModified()));
		     }
		  });		
			tb_files.setItems(list);		
			 // make first row selected			 
			tb_files.getSelectionModel().selectFirst();		 
		}  
	}
	 
	
	public void goBack(ActionEvent event)throws IOException	
	{				
		// change window
		
		Parent temp_parent = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
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
	 
	
	public void openFile(ActionEvent event)throws IOException	
	{
		int all_good=0;
		// get the selected file
		File selected_file = tb_files.getSelectionModel().getSelectedItem();		
		// make it the current file
			//check if ./CurrentGraph directory exists		
		File[] temp_files2 = new File("./CurrentGraph").listFiles(new FilenameFilter() { 
			 @Override public boolean accept(File dir, String name) { return name.endsWith(".csv"); } });		
		//if it dosen't 		
		 if (temp_files2==null) {
			// crate directory
			 new File("./CurrentGraph").mkdirs();	
			// add current graph
			 try{
				    Files.copy(Paths.get("./Graphs/"+selected_file.getName()), 
				    		Paths.get("./CurrentGraph/"+selected_file.getName()));				    
				} catch (IOException e) {
				   // Failed to make graph
					all_good=1;
					lbl_errorMessage.setText("Failed to make graph the current graph");
				}
			}
		 else// if it dose
		 {
			 //check if empty
			if(temp_files2.length!=0){
				// if not remove old one
				for (File file : temp_files2) {
				    if (file.isFile()) {		    	 
				    	 file.delete();				    	 	    	 
				     }
				 }
			}				
			// add current graph
			 try{				 
				    Files.copy(Paths.get("./Graphs/"+selected_file.getName()), 
				    		Paths.get("./CurrentGraph/"+selected_file.getName()));				    
				} catch (IOException e) {
				   // Failed to make graph
					all_good=1;
					lbl_errorMessage.setText("Failed to make graph the current graph");
				}
		 }		
		//if all good go to editor screen
		 if(all_good==0)
		 {
			 
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
	}
	 
	public void deleteFile(ActionEvent event)throws IOException	
	{
		// get the selected file
		File selected_file = tb_files.getSelectionModel().getSelectedItem();	
		int selectedIndex = tb_files.getSelectionModel().getSelectedIndex();
		int all_good=0;
		//remove it from the current file if it is the current file	
			
		//check if ./CurrentGraph directory exists			
		File[] temp_files = new File("./CurrentGraph").listFiles(new FilenameFilter() { 
			 @Override public boolean accept(File dir, String name) { return name.endsWith(".csv"); } });			
		//if it dosen't 		
		 if (temp_files==null) {
			// crate directory
			 new File("./CurrentGraph").mkdirs();					
			}
		 else
		 {
			//check if empty
				if(temp_files.length!=0){
					// if not remove graph
					for (File file : temp_files) {
					    if (file.isFile()) {		    	 
					    	if(file.getName().equals(selected_file.getName())) 
					    		{
					    			file.delete();
					    			break;
					    		}
					     }
					 }
				}
		 }				
		// remove it				
		 File[] temp_files2 = new File("./Graphs").listFiles(new FilenameFilter() { 
			 @Override public boolean accept(File dir, String name) { return name.endsWith(".csv"); } });			 
		// remove graph
			for (File file : temp_files2) {
			    if (file.isFile()) {		    	 
			    	if(file.getName().equals(selected_file.getName())) 
			    		{
			    			file.delete();
			    			break;
			    		}
			     }
			 }			 
		// refresh table			
		tb_files.getItems().remove(selectedIndex);
		
	}
	 
	 
	 
	 
}
