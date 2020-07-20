package application;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewGraphControler {
	@FXML
	private Label myLabel;
	
	@FXML
	private TextField newGraphName;
	@FXML
	private TextField newGraph_Lattice_x;
	@FXML
	private TextField newGraph_Lattice_y;
	
	public void makeNewGraph(ActionEvent event) throws IOException
	{
		
		File[] files = new File("./Graphs").listFiles(new FilenameFilter() { 
			 @Override public boolean accept(File dir, String name) { return name.endsWith(".csv"); } });

		int name_taken = 0;
		 if (files==null) {
			 new File("./Graphs").mkdirs();	
			}
		 else
		 {
			 for (File file : files) {
			     if (file.isFile()) {	
			    	 //check if input string empty
			    	 if(newGraphName.getText().isEmpty())
			    	 {
			    		 name_taken=2;
			    		 myLabel.setText("Enter name");
		    	 		 break;
			    	 }
			    	 
			    	 
			    	 //check if name taken
			    	 	 
			    	 	 if(file.getName().equals(newGraphName.getText()+".csv"))
			    	 		 {
			    	 		 	name_taken=1;
			    	 		 	break;
			    	 		 }
			     }
			 }
		 }
		
		 if(name_taken==1)
		 {
			 
				myLabel.setText("Name taken");
		 }
		 
		 if(name_taken==0)
		 {
			 // add new csv file
			myLabel.setText("");
			int all_good = 0;
			File[] temp_files = new File("./Graphs").listFiles(new FilenameFilter() { 
				 @Override public boolean accept(File dir, String name) { return name.endsWith(".csv"); } });

			
			 if (temp_files==null) {
				 new File("./Graphs").mkdirs();	
				}
			 
			 
				
			try{
			    PrintWriter writer = new PrintWriter(new File("./Graphs/"+newGraphName.getText()+".csv"));
			    // initialize document
			    writer.println("1,1,"); // interaction_model & update_mechanism  TO DO: allow user to input this ones
			    writer.println("5,3,1,0,"); // payoffs
			   
			    if(newGraph_Lattice_x.getText().isEmpty() || newGraph_Lattice_y.getText().isEmpty() || 
			    		Integer.valueOf(newGraph_Lattice_x.getText()) < 3 || Integer.valueOf(newGraph_Lattice_y.getText()) < 3)
			    {
			    	writer.println("0,"); // 0 nodes in the graph			   
			    }
			    else
			    {
			    	int x = Integer.valueOf(newGraph_Lattice_x.getText());
			    	int y = Integer.valueOf(newGraph_Lattice_y.getText());
			    	
			    	writer.println(x*y + ",");
			    	
			    	StringBuilder sb = new StringBuilder();
			    	
			    	// Nodes details (cooperation, x position, y position, degree, adjacency)
					
			    	
			    	
			    	for(int j=0; j<y; j++)
				    {
			    		for(int i=0; i<x; i++)
					    {
			    			// cooperator
			    			sb.append("1,");
			    			
			    			// positions		    			
			    			
			    			if( i== 0 || j == 0 || i == x-1 || j == y-1) // edges
			    			{			    				
			    				
			    				if(i == 0 && j == 0) // top left corner
			    				{
			    					sb.append(50);
				    				sb.append(',');
				    				sb.append(50);
				    				sb.append(',');
			    				}
			    				else if(i == x-1 && j == 0) // top right corner
			    				{
			    					sb.append((x*100-50));
				    				sb.append(',');
				    				sb.append(50);
				    				sb.append(',');
			    				}
			    				else if(i == 0 && j == y-1) // bottom left corner
			    				{
			    					sb.append(50);
				    				sb.append(',');
				    				sb.append((y*100-50));
				    				sb.append(',');
			    				}
			    				else if(i == x-1 && j == y-1) // bottom right corner
			    				{
			    					sb.append((x*100-50));
				    				sb.append(',');
				    				sb.append((y*100-50));
				    				sb.append(',');
			    				}
			    				else if(j == 0) // top edge
			    				{
			    					
			    					sb.append((i*100+50));
				    				sb.append(',');
				    				sb.append(100);
				    				sb.append(',');
			    				}
			    				else if(j == y-1) // bottom edge
			    				{
			    					sb.append((i*100+50));
				    				sb.append(',');
				    				sb.append((y*100));
				    				sb.append(',');
			    				}
			    				else if(i == 0) // left edge
			    				{
			    					sb.append(100);
				    				sb.append(',');
				    				sb.append((j*100+50));
				    				sb.append(',');
			    				}
			    				else if(i == x-1) // right edge
			    				{
			    					sb.append((x*100));
				    				sb.append(',');
				    				sb.append((j*100+50));
				    				sb.append(',');
			    				}
			    				
			    			}
			    			else // center
			    			{
			    				sb.append((i*100+100));
			    				sb.append(',');
			    				sb.append((j*100+100));
			    				sb.append(',');
			    			}
			    			
			    			
			    			// degree			    				
			    			sb.append("4,");
			    			
			    			//adjacency
			    			if( i== 0 || j == 0 || i == x-1 || j == y-1) // edges
			    			{			    				
			    				
			    				if(i == 0 && j == 0) // top left corner
			    				{
			    					sb.append(1);
				    				sb.append(',');
				    				sb.append(x-1);
				    				sb.append(',');
				    				sb.append(x);
				    				sb.append(',');
				    				sb.append(x*(y-1));
				    				sb.append(',');
			    				}
			    				else if(i == x-1 && j == 0) // top right corner
			    				{
			    					sb.append(0);
				    				sb.append(',');
				    				sb.append(x-2);
				    				sb.append(',');
				    				sb.append((x*2)-1);
				    				sb.append(',');
				    				sb.append((x*y)-1);
				    				sb.append(',');
			    				}
			    				else if(i == 0 && j == y-1) // bottom left corner
			    				{			    					
			    					sb.append(0);
				    				sb.append(',');
				    				sb.append((j-1)*x);
				    				sb.append(',');
				    				sb.append(j*x+1);
				    				sb.append(',');
				    				sb.append((x*y)-1);
				    				sb.append(',');
			    				}
			    				else if(i == x-1 && j == y-1) // bottom right corner
			    				{			    					
			    					sb.append(x-1);
				    				sb.append(',');
				    				sb.append((j-1)*x+i);
				    				sb.append(',');
				    				sb.append((x*y)-1);
				    				sb.append(',');
				    				sb.append((x*y)-2);
				    				sb.append(',');
			    				}
			    				else if(j == 0) // top edge
			    				{			    					
			    					sb.append(i-1);
				    				sb.append(',');
				    				sb.append(i+1);
				    				sb.append(',');
				    				sb.append(x+i);
				    				sb.append(',');
				    				sb.append(x*(y-1)+i);
				    				sb.append(',');
			    				}
			    				else if(j == y-1) // bottom edge
			    				{			    					
			    					sb.append(i);
				    				sb.append(',');
				    				sb.append(x*(j-1)+i);
				    				sb.append(',');
				    				sb.append(x*j+i-1);
				    				sb.append(',');
				    				sb.append(x*j+i+1);
				    				sb.append(',');
			    				}
			    				else if(i == 0) // left edge
			    				{			    				
			    					sb.append(x*(j-1));
				    				sb.append(',');
				    				sb.append(x*j+1);
				    				sb.append(',');
				    				sb.append(x*j+x-1);
				    				sb.append(',');
				    				sb.append(x*(j+1));
				    				sb.append(',');
			    				}
			    				else if(i == x-1) // right edge
			    				{			    					
			    					sb.append(x*j-1);
				    				sb.append(',');
				    				sb.append(x*j);
				    				sb.append(',');
				    				sb.append(x*(j+1) -2);
				    				sb.append(',');
				    				sb.append(x*(j+2)-1);
				    				sb.append(',');
			    				}
			    				
			    			}
			    			else // center
			    			{
			    				sb.append((j-1)*x+i);
			    				sb.append(',');
			    				sb.append(j*x+i-1);
			    				sb.append(',');
			    				sb.append(j*x+i+1);
			    				sb.append(',');
			    				sb.append((j+1)*x+i);
			    				sb.append(',');
			    			}
			    			
			    			
			    			
			    			sb.append('\n');
					    }
			    		
			    		
			    		
				    }
			    	
			    	writer.write(sb.toString());
			    }
			    
			    writer.close();
			} catch (IOException e) {
			   // Failed to make graph
				all_good=1;
				myLabel.setText("Failed to make graph");
			}
			// make it the current graph
			if(all_good==0)
			{
				File[] temp_files2 = new File("./CurrentGraph").listFiles(new FilenameFilter() { 
					 @Override public boolean accept(File dir, String name) { return name.endsWith(".csv"); } });

				
				 if (temp_files2==null) {
					 new File("./CurrentGraph").mkdirs();	
					}
				 else
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
				 }
					
				try{
				    Files.copy(Paths.get("./Graphs/"+newGraphName.getText()+".csv"), 
				    		Paths.get("./CurrentGraph/"+newGraphName.getText()+".csv"));
				    
				} catch (IOException e) {
				   // Failed to make graph
					all_good=2;
					myLabel.setText("Failed to make graph the current graph");
				}
				// go to editor screen
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
			
			
			
		 }
		
	}
	
	public void goBack(ActionEvent event)throws IOException
	{	
		
		// change window
		
		Parent makeNewGraph_parent = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
		Scene makeNewGraph_scene = new Scene(makeNewGraph_parent);
		Stage app_Stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		//keep the current window size
		double stage_height = app_Stage.getHeight();
		double stage_width = app_Stage.getWidth();
		
		// change scene
		app_Stage.setScene(makeNewGraph_scene);
		
		// set window size to previous scene
		app_Stage.setHeight(stage_height);
		app_Stage.setWidth(stage_width);		
		
	}
}
