package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;

//import javax.annotation.PostConstruct;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.shape.Line;

public class EditorControler {

	@FXML
	private AnchorPane pane_MainDisplay;	
	
	/*
	 * 
	 *  Graph Selection
	 * 
	 */
	
	@FXML
	private ScrollPane scrlpane_MainDisplay;
	
	@FXML
	private AnchorPane pane_Selection_Graph;
		
	@FXML
	private Label lbl_GraphSelection_Title;
	
	@FXML
	private Label lbl_GraphSelection_NumberOfNodes;
	
	@FXML
	private Label lbl_GraphSelection_InteractionModel;
	
	@FXML
	private Label lbl_GraphSelection_UpdateMechanism;
	
	@FXML
	private TextField txt_GraphSelection_TValue;
	
	@FXML
	private TextField txt_GraphSelection_RValue;
	
	@FXML
	private TextField txt_GraphSelection_PValue;
	
	@FXML
	private TextField txt_GraphSelection_SValue;
		
	@FXML
	private TextField txt_GraphSelection_SetNumberOfDefectors;
		
	@FXML
	private Label lbl_GraphSelection_SetNumberOfDefectorsError;
	
	@FXML
	private ChoiceBox<String> choiceBox_GraphSelection_SetZoom;
	
	
	/*
	 * 
	 * Node Selection
	 * 
	 */
	
	@FXML
	private AnchorPane pane_Selection_Node;
	
	@FXML
	private Label lbl_NodeSelection_Title;
	
	@FXML
	private CheckBox cbox_NodeSelection_Cooperation;
	
	@FXML
	private TextField txt_NodeSelection_XValue;
	
	@FXML
	private TextField txt_NodeSelection_YValue;
	
	/*
	
	0 - Default, nothing selected
	1 - Add node
	2 - Add edge start
	3 - Add edge end
	4 - Delete	
	5 - Start select
	6 - Paste	
	7 - Drag started
	
	*/
	private int clickMode=0;	
	
	private int payoff_T,payoff_R,payoff_P,payoff_S;
	private String file_name;
	private int numberOfNodes;
	private Vertex currentNode;
	private ArrayList<Vertex> nodes = new ArrayList<Vertex> ();		
	
	private int interaction_model, update_mechanism; // to be used in the future
	
	private double select_start_x, select_start_y;
	private double select_end_x, select_end_y;
	
	private ArrayList<Vertex> select_nodes;	
	
	private double zoomLevel, nodeRadius, lineWidth, glowRadius;	
	
	//ghosts
	private ArrayList<Object> ghosts = new ArrayList<Object> ();	
	
	@FXML
    public void initialize() throws IOException {
		
		// open file and initialize variables
		
		int all_good = initObjects();		
		// TO DO: Handle error all_good = 0 
		
		
		// Add text field out of focus listeners
		addTextFieldSubmitListeners();
		
		//settings for zoom
		
		nodeRadius=20;
		lineWidth=3;
		glowRadius=35;
		
		choiceBox_GraphSelection_SetZoom.getItems().addAll("1", "2", "4", "8", "16");
		choiceBox_GraphSelection_SetZoom.setValue("1");
		zoomLevel=1;
		
		// add a listener 
		choiceBox_GraphSelection_SetZoom.getSelectionModel().selectedItemProperty().addListener( (v,oldValue,newValue) -> {	
			
			
			nodeRadius=20/Double.parseDouble(newValue);
			lineWidth=3/Double.parseDouble(newValue);
			glowRadius=35/Double.parseDouble(newValue);
			
			//set zoom
			zoomLevel = Double.parseDouble(oldValue)/Double.parseDouble(newValue);
			
			//update values
			for(int i = 0; i<numberOfNodes; i++)
			{				
				//used the nodes
				nodes.get(i).setCenterX(nodes.get(i).getCenterX()*zoomLevel);
				nodes.get(i).setCenterY(nodes.get(i).getCenterY()*zoomLevel);
				
				//update edges
				for(int j = 0; j<nodes.get(i).getEdges().size(); j++)
				{
					if(nodes.get(i).getEdges().get(j).getNodeA() == nodes.get(i))
		    		{			    			
						nodes.get(i).getEdges().get(j).setStartX(nodes.get(i).getCenterX());
						nodes.get(i).getEdges().get(j).setStartY(nodes.get(i).getCenterY());							
		    		}
		    		else
		    		{
		    			nodes.get(i).getEdges().get(j).setEndX(nodes.get(i).getCenterX());
		    			nodes.get(i).getEdges().get(j).setEndY(nodes.get(i).getCenterY());
		    		}
				}
				
			}
			
			
			//set visual update
			for(int i = 0; i<numberOfNodes; i++)
			{				
				//used to update the visuals
				nodes.get(i).setMark(0);			
			}
			
			//update visuals
			updateMainDisplay();
		});			
		
		
		changeSelectionPaneToGraph();
		
		
		//add listeners for change in the scrollpane (so we can only show the nodes and edges that are visible)
		
		addMainDisplayScrollListeners();
		
		
		
		updateMainDisplay();
		
		
	}
	
	// open file and initialize variables
	public int initObjects()throws IOException
	{
		
		// check if we have a current graph
		File[] temp_files = new File("./CurrentGraph").listFiles(new FilenameFilter() { 
			 @Override public boolean accept(File dir, String name) { return name.endsWith(".csv"); } });

		// make directory if it dosen't exist
		 if (temp_files==null) {
			 new File("./CurrentGraph").mkdirs();	
			 
			 return 0; // file dosen't exist
			}
		 else
		 {
		 
		 //check if it's not empty empty
			if(temp_files.length!=0){
				
				// open file
				try {
				FileInputStream fis = new FileInputStream(temp_files[0].getAbsolutePath());
			    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			    String line;
			    
			    file_name = temp_files[0].getName();
			    
			    int temp_numberOfNodes;
			    
			    
			    //get interaction_model & update_mechanism
			    if((line = br.readLine()) != null)
			    {
			    	
			    	String split[] = line.split(","); 
			    	
			    	if(split.length==2 )
			    	{
			    		interaction_model=Integer.parseInt(split[0]);
			    		update_mechanism=Integer.parseInt(split[1]);				    	
			    	}
			    	else
			    	{
			    		//throw bad file format error
			    		System.out.println("Bad file format");
			    		br.close();
			    		fis.close();
			    		return 0;
			    	}
			    }
			    else
			    {
			    	//throw bad file format error
			    	System.out.println("Bad file format");
			    	br.close();
			    	fis.close();
			    	return 0;
			    }
			    
			    if(interaction_model == 1 && update_mechanism == 1)
			    {
			    	
			    	// get payoffs
				    if((line = br.readLine()) != null)
				    {
				    	
				    	String split[] = line.split(","); 
				    	
				    	if(split.length==4 )
				    	{
					    	payoff_T=Integer.parseInt(split[0]);
					    	payoff_R=Integer.parseInt(split[1]);
					    	payoff_P=Integer.parseInt(split[2]);
					    	payoff_S=Integer.parseInt(split[3]);
				    	}
				    	else
				    	{
				    		//throw bad file format error
				    		System.out.println("Bad file format");
				    		br.close();
				    		fis.close();
				    		return 0;
				    	}
				    }
				    else
				    {
				    	//throw bad file format error
				    	System.out.println("Bad file format");
				    	br.close();
				    	fis.close();
				    	return 0;
				    }
				    
				    // get number of nodes
				    if((line = br.readLine()) != null)
				    {
				    	
				    	String split[] = line.split(","); 
				    	
				    	if(split.length==1 )
				    	{
				    		temp_numberOfNodes=Integer.parseInt(split[0]);
					    	
				    	}
				    	else
				    	{
				    		//throw bad file format error
				    		System.out.println("Bad file format");
				    		br.close();
					    	fis.close();
					    	return 0;
				    	}
				    }
				    else
				    {
				    	//throw bad file format error
				    	System.out.println("Bad file format");
				    	br.close();
				    	fis.close();
				    	return 0;
				    }
				    
				   
				    
				    //initialize nodes
				    for(int i=0; i<temp_numberOfNodes; i++)
				    {
				    	placeNode(0, 0, 1);
				    }
				    
				    // set nodes details
				    for(int i=0; i<temp_numberOfNodes; i++)
				    {
				    	// get node i
					    if((line = br.readLine()) != null)
					    {
					    	
					    	String split[] = line.split(","); 
					    	
					    	if(split.length>3 )
					    	{
					    		
					    		// Set node position
					    		nodes.get(i).setCenterX(Double.parseDouble(split[1]));
					    		nodes.get(i).setCenterY(Double.parseDouble(split[2]));		    		
					    		
					    		
					    		// set defection
					    		if(Integer.parseInt(split[0])==0)
					    			{
					    				nodes.get(i).setCurrentStrategy(0);				    				
					    			}
					    		else
					    			{
					    				nodes.get(i).setCurrentStrategy(1);
					    			}
					    		
					    		int degree = Integer.parseInt(split[3]);
					    		
					    		//set up adjacency
					    		if(split.length==4 + degree)
					    		{
					    			
					    			for(int t=0; t<degree; t++)
								    {
					    				//get neighbor
					    				int neighbor = Integer.parseInt(split[4 + t]);
					    				
					    				
					    				// see if neighbor has current node as a neighbor so we can get the edge
					    				if(neighbor>i)			    					
				    					{
				    						// make it
					    				
					    					// set currentNode
					    					currentNode = nodes.get(i);
					    				
					    					placeEdge(nodes.get(i) , nodes.get(neighbor)); 
				    					}  
								    }
					    			
					    		}
					    		else
						    	{
						    		//throw bad file format error
						    		System.out.println("Bad file format");
						    		br.close();
							    	fis.close();
							    	return 0;
						    	}
					    		
					    	}
					    	else
					    	{
					    		//throw bad file format error
					    		System.out.println("Bad file format");
					    		br.close();
						    	fis.close();
						    	return 0;
					    	}
					    }
					    else
					    {
					    	//throw bad file format error
					    	System.out.println("Bad file format");
					    	br.close();
					    	fis.close();
					    	return 0;
					    }
				    }		
				    
				    // readjust edge positions
				    for(int i=0; i<temp_numberOfNodes; i++)
				    {
				    	ArrayList<Edge> edges = nodes.get(i).getEdges();
				    	for(int j=0; j<edges.size(); j++)
					    {
				    		if(edges.get(j).getNodeA() == nodes.get(i))
				    		{			    			
				    			edges.get(j).setStartX(nodes.get(i).getCenterX());
				    			edges.get(j).setStartY(nodes.get(i).getCenterY());							
				    		}
				    		else
				    		{
				    			edges.get(j).setEndX(nodes.get(i).getCenterX());
				    			edges.get(j).setEndY(nodes.get(i).getCenterY());
				    		}
					    }
				    	
				    }
			    	
			    }
			    else if(interaction_model == 1 && update_mechanism == 2)
			    {			    	
			    	// get payoffs
				    if((line = br.readLine()) != null)
				    {
				    	
				    	String split[] = line.split(","); 
				    	
				    	if(split.length==4 )
				    	{
					    	payoff_T=Integer.parseInt(split[0]);
					    	payoff_R=Integer.parseInt(split[1]);
					    	payoff_P=Integer.parseInt(split[2]);
					    	payoff_S=Integer.parseInt(split[3]);
				    	}
				    	else
				    	{
				    		//throw bad file format error
				    		System.out.println("Bad file format");
				    		br.close();
				    		fis.close();
				    		return 0;
				    	}
				    }
				    else
				    {
				    	//throw bad file format error
				    	System.out.println("Bad file format");
				    	br.close();
				    	fis.close();
				    	return 0;
				    }
				    
				    // get number of nodes
				    if((line = br.readLine()) != null)
				    {
				    	
				    	String split[] = line.split(","); 
				    	
				    	if(split.length==1 )
				    	{
				    		temp_numberOfNodes=Integer.parseInt(split[0]);
					    	
				    	}
				    	else
				    	{
				    		//throw bad file format error
				    		System.out.println("Bad file format");
				    		br.close();
					    	fis.close();
					    	return 0;
				    	}
				    }
				    else
				    {
				    	//throw bad file format error
				    	System.out.println("Bad file format");
				    	br.close();
				    	fis.close();
				    	return 0;
				    }
				    
				   
				    
				    //initialize nodes
				    for(int i=0; i<temp_numberOfNodes; i++)
				    {
				    	placeNode(0, 0, 1);
				    }
				    
				    // set nodes details
				    for(int i=0; i<temp_numberOfNodes; i++)
				    {
				    	// get node i
					    if((line = br.readLine()) != null)
					    {
					    	
					    	String split[] = line.split(","); 
					    	
					    	if(split.length>3 )
					    	{
					    		
					    		// Set node position
					    		nodes.get(i).setCenterX(Double.parseDouble(split[1]));
					    		nodes.get(i).setCenterY(Double.parseDouble(split[2]));		    		
					    		
					    		
					    		// set defection
					    		if(Integer.parseInt(split[0])==0)
					    			{
					    				nodes.get(i).setCurrentStrategy(0);				    				
					    			}
					    		else
					    			{
					    				nodes.get(i).setCurrentStrategy(1);
					    			}
					    		
					    		int degree = Integer.parseInt(split[3]);
					    		
					    		//set up adjacency
					    		if(split.length==4 + degree)
					    		{
					    			
					    			for(int t=0; t<degree; t++)
								    {
					    				//get neighbor
					    				int neighbor = Integer.parseInt(split[4 + t]);
					    				
					    				
					    				// see if neighbor has current node as a neighbor so we can get the edge
					    				if(neighbor>i)			    					
				    					{
				    						// make it
					    				
					    					// set currentNode
					    					currentNode = nodes.get(i);
					    				
					    					placeEdge(nodes.get(i) , nodes.get(neighbor)); 
				    					}  
								    }
					    			
					    		}
					    		else
						    	{
						    		//throw bad file format error
						    		System.out.println("Bad file format");
						    		br.close();
							    	fis.close();
							    	return 0;
						    	}
					    		
					    	}
					    	else
					    	{
					    		//throw bad file format error
					    		System.out.println("Bad file format");
					    		br.close();
						    	fis.close();
						    	return 0;
					    	}
					    }
					    else
					    {
					    	//throw bad file format error
					    	System.out.println("Bad file format");
					    	br.close();
					    	fis.close();
					    	return 0;
					    }
				    }		
				    
				    // readjust edge positions
				    for(int i=0; i<temp_numberOfNodes; i++)
				    {
				    	ArrayList<Edge> edges = nodes.get(i).getEdges();
				    	for(int j=0; j<edges.size(); j++)
					    {
				    		if(edges.get(j).getNodeA() == nodes.get(i))
				    		{			    			
				    			edges.get(j).setStartX(nodes.get(i).getCenterX());
				    			edges.get(j).setStartY(nodes.get(i).getCenterY());							
				    		}
				    		else
				    		{
				    			edges.get(j).setEndX(nodes.get(i).getCenterX());
				    			edges.get(j).setEndY(nodes.get(i).getCenterY());
				    		}
					    }
				    	
				    }
			    	
			    }
			    else
					System.out.println("Undefined behaviour for current interaction_model and update_mechanism");
			    
			    
			   			    
			    br.close();
			    fis.close();				
				
				}
				catch (FileNotFoundException ex) {
					System.out.println("File not found!");	
					return 0;
				}
				
				
			}
			else
			{				
				return 0; // file empty
			}
		 }
		
		currentNode=null;
		return 1; // all good
	}
	
	public void goBack(ActionEvent event)throws IOException
	{	
		try {
			//reset the zoom
			choiceBox_GraphSelection_SetZoom.setValue("1");
			
			PrintWriter pw = new PrintWriter(new File("./Graphs/" + file_name));
			StringBuilder sb = new StringBuilder();
			
			if(interaction_model == 1 && update_mechanism == 1)
			{
				//interaction_model = 1;
				sb.append("1");
				sb.append(',');
				// update_mechanism = 1;
				sb.append("1");
				sb.append(',');
				sb.append('\n');
				
				// Payoffs (T,R,P,S)
				sb.append(payoff_T);
				sb.append(',');	
				sb.append(payoff_R);
				sb.append(',');	
				sb.append(payoff_P);
				sb.append(',');	
				sb.append(payoff_S);
				sb.append(',');			
				sb.append('\n');
				// Number of node
				sb.append(numberOfNodes);
				sb.append(',');			
				sb.append('\n');
				
				// Nodes details (cooperation, x position, y position, degree, adjacency)
				
				for(int i=0; i<numberOfNodes; i++)
				    {
					 	if(nodes.get(i).getCurrentStrategy()== 1)
					 		sb.append("1,");
					 	else
					 		sb.append("0,");
					 	
					 	sb.append(nodes.get(i).getCenterX());
					 	sb.append(',');	
					 	sb.append(nodes.get(i).getCenterY());
					 	sb.append(',');
					 	
					 	//get degree
					 	int degree = nodes.get(i).getDegree();	 
						
						sb.append(degree);	
						sb.append(',');	
						
						//adjacency
						
						ArrayList<Vertex> neighbours =  nodes.get(i).getNeighbors();
						
						for(int j=0; j<degree; j++)			   
						{
							
							sb.append(neighbours.get(j).getVertexNumber());	
							sb.append(',');
							
						}
					 	
						sb.append('\n');
				    }
				
				
				pw.write(sb.toString());
				pw.close();
				
				// replace current graph
				
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
				    Files.copy(Paths.get("./Graphs/"+file_name), 
				    		Paths.get("./CurrentGraph/"+file_name));
				    
				} catch (IOException e) {
				   // Failed to make graph
					
				}
				
				
				// reset node count
				Vertex c=new Vertex_1_1();
				c.setCount(0);
			}
			else if(interaction_model == 1 && update_mechanism == 2)
			{
				//interaction_model = 1;
				sb.append("1");
				sb.append(',');
				// update_mechanism = 2;
				sb.append("2");
				sb.append(',');
				sb.append('\n');
				
				// Payoffs (T,R,P,S)
				sb.append(payoff_T);
				sb.append(',');	
				sb.append(payoff_R);
				sb.append(',');	
				sb.append(payoff_P);
				sb.append(',');	
				sb.append(payoff_S);
				sb.append(',');			
				sb.append('\n');
				// Number of node
				sb.append(numberOfNodes);
				sb.append(',');			
				sb.append('\n');
				
				// Nodes details (cooperation, x position, y position, degree, adjacency)
				
				for(int i=0; i<numberOfNodes; i++)
				    {
					 	if(nodes.get(i).getCurrentStrategy()== 1)
					 		sb.append("1,");
					 	else
					 		sb.append("0,");
					 	
					 	sb.append(nodes.get(i).getCenterX());
					 	sb.append(',');	
					 	sb.append(nodes.get(i).getCenterY());
					 	sb.append(',');
					 	
					 	//get degree
					 	int degree = nodes.get(i).getDegree();	 
						
						sb.append(degree);	
						sb.append(',');	
						
						//adjacency
						
						ArrayList<Vertex> neighbours =  nodes.get(i).getNeighbors();
						
						for(int j=0; j<degree; j++)			   
						{
							
							sb.append(neighbours.get(j).getVertexNumber());	
							sb.append(',');
							
						}
					 	
						sb.append('\n');
				    }
				
				
				pw.write(sb.toString());
				pw.close();
				
				// replace current graph
				
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
				    Files.copy(Paths.get("./Graphs/"+file_name), 
				    		Paths.get("./CurrentGraph/"+file_name));
				    
				} catch (IOException e) {
				   // Failed to make graph
					
				}
				
				
				// reset node count
				Vertex c=new Vertex_1_2();
				c.setCount(0);			
			}
			else
				System.out.println("Undefined behaviour for current interaction_model and update_mechanism");
			
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
		catch(FileNotFoundException ex){
			// TO DO: handle exception
		}
		
		
	}
	
	public void addNode()
	{		
		clickMode = 1;	
		Image image = new Image(new File("./CursorImages/PlaceNode.png").toURI().toString());
		pane_MainDisplay.getScene().setCursor(new ImageCursor(image));
		
		//clear ghosts
		
		//ghost		
		Circle c = new Circle();
		
		//add listener for change in mouse position	
		pane_MainDisplay.setOnMouseMoved(new EventHandler<MouseEvent>(){
			@Override
	          public void handle(MouseEvent e) {
				//make ghost node to follow mouse
				c.setCenterX(e.getX());
				c.setCenterY(e.getY());				
			}
		});	
		
		
		
		c.setRadius(nodeRadius);
		c.setStroke(Color.BLACK);
		c.setFill(Color.rgb(0, 200, 255));
		
		c.setOpacity(0.5);
		pane_MainDisplay.getChildren().addAll(c);
		
		ghosts.add(c);
		
	}
	
	public void addEdge()
	{
		if(select_nodes == null || select_nodes.size()==1)// one node selected	
		{
			clickMode = 2;	
			
			Image image = new Image(new File("./CursorImages/EdgeStart.png").toURI().toString());
			pane_MainDisplay.getScene().setCursor(new ImageCursor(image));
		}
		else
		{
			clickMode = 3;
			
			Image image = new Image(new File("./CursorImages/EdgeEnd.png").toURI().toString());
			pane_MainDisplay.getScene().setCursor(new ImageCursor(image));
			
			//remove ghosts and mouse listener
			pane_MainDisplay.setOnMouseMoved(null);
			for(int i =0; i<ghosts.size(); i++)
			{
				pane_MainDisplay.getChildren().remove(ghosts.get(i));
			}					  					  					  
			ghosts = new ArrayList<Object> ();
			
			//add ghost edges to each selected node
			for(int i = 0; i<select_nodes.size(); i++)
			{				
				//ghost
				Line temp_edge = new Line();
				
				// make the line object
				
				temp_edge.setStartX(select_nodes.get(i).getCenterX());
				temp_edge.setStartY(select_nodes.get(i).getCenterY());
				temp_edge.setEndX(select_nodes.get(i).getCenterX());
				temp_edge.setEndY(select_nodes.get(i).getCenterY());
				 
				temp_edge.setStrokeWidth(lineWidth);
				temp_edge.setOpacity(0.5);
				  
				pane_MainDisplay.getChildren().addAll(temp_edge);
				  
				// make it so the nodes are above the edge so we can click on the nodes
				temp_edge.toBack();	        		  
				
				ghosts.add(temp_edge);     	  
			}
			
			//add listener for change in mouse position
			pane_MainDisplay.setOnMouseMoved(new EventHandler<MouseEvent>(){				
				@Override					
				public void handle(MouseEvent ev) {
					//make ghost node to follow mouse
					for(int i = 0; i<ghosts.size(); i++)
					{							
						if(ghosts.get(i) instanceof Line )
						{
							((Line)ghosts.get(i)).setEndX(ev.getX());
							((Line)ghosts.get(i)).setEndY(ev.getY());
						}
					}			        				  
				}
			});
		}
		
	}
	
	public void deleteElement()
	{	
		if(select_nodes == null || select_nodes.size()==1)
		{
			//prepare to delete elements on click
			clickMode = 4;
			
			Image image = new Image(new File("./CursorImages/Delete.png").toURI().toString());
			pane_MainDisplay.getScene().setCursor(new ImageCursor(image));			
		}
		else
		{
			//delete all nodes from selection
			
			// remove all the edges to the nodes
			for(int i = select_nodes.size() - 1; i>=0; i--)
			{
				for(int j = select_nodes.get(i).getDegree() - 1; j>=0; j--)
		  		{	  			
		  			pane_MainDisplay.getChildren().remove(select_nodes.get(i).getEdge(j).getVisualObject());
		  			select_nodes.get(i).getNeighbor(j).removeNeighbor(select_nodes.get(i));
		  			select_nodes.get(i).removeNeighbor(select_nodes.get(i).getNeighbor(j)); 
		  		}				
			}
			
			
			for(int i = select_nodes.size() - 1; i>=0; i--)
			{
				// remove the node from scene
		  		  
		  		pane_MainDisplay.getChildren().remove(select_nodes.get(i).getVisualObject());
		  		  
		  		// remove it from the list of nodes		        		  
		  		nodes.remove(select_nodes.get(i));
		  		  
		  		numberOfNodes--;
			}
	  		 
	  		if(numberOfNodes>0)
	  		{
	  			  
	      		// update the count/node number of all the nodes higher than it
	      		for(int i = 0; i<numberOfNodes; i++)
	      		{
	      			nodes.get(i).setVertexNumber(i);
	      		}
	      		  
	      		nodes.get(0).setCount(numberOfNodes);		        		 
	  		}
	  		else
	  		{
	  			if(interaction_model == 1 && update_mechanism == 1)
	  			{
	  				Vertex temp = new Vertex_1_1();
		  			temp.setCount(0);
	  			}
	  			else if(interaction_model == 1 && update_mechanism == 2)
	  			{
	  				Vertex temp = new Vertex_1_2();
		  			temp.setCount(0);
	  			}
	  			else
					System.out.println("Undefined behaviour for current interaction_model and update_mechanism");
	  			
	  		}
	  		
	  	    // Make current node null
  		    currentNode = null;
  		    
  		    clickMode = 0;
			select_nodes = null;
  		  
  		    // Change display info 	        			
  		    changeSelectionPaneToGraph();
			
		}
		
	}
	
	public void selectCopy()
	{
		if(select_nodes!= null)
		{			
			// make ghost of each selected node
			for(int i=0;i<select_nodes.size();i++)
			{
				Circle c = new Circle();
				 
				c.setRadius(nodeRadius);						
				c.setStroke(Color.BLACK);						
				c.setFill(Color.rgb(0, 200, 255));						
				
				c.setOpacity(0.5);						
				pane_MainDisplay.getChildren().addAll(c);
				
				ghosts.add(c);						
			}
			
			//make ghost for edges between selected nodes
			
			// add edges
			for(int i=0;i<select_nodes.size();i++)
			{				
				for(int j=i;j<select_nodes.size();j++)
				{	
					// if there is an edge between the initial nodes place an edge between the copy nodes
					if(select_nodes.get(i).isNeighbor(select_nodes.get(j))!=-1)
					{						
						Line temp_edge = new Line();

			  		    // make the line object
			  		    temp_edge.setStrokeWidth(lineWidth);
			  		    temp_edge.setOpacity(0.5);
			  		    			  		    
			  		    pane_MainDisplay.getChildren().addAll(temp_edge);
			  		    ghosts.add(temp_edge);
			  		    // make it so the nodes are above the edge so we can click on the nodes
			  		    temp_edge.toBack();
					}				
				}
			}
			
  		    
  		    
			
			//add listener for change in mouse position	
			pane_MainDisplay.setOnMouseMoved(new EventHandler<MouseEvent>(){
				@Override
		          public void handle(MouseEvent e) {					
					
					//get middle of selection
					int j = 0;
					
					double minX = select_nodes.get(0).getCenterX();
					double minY = select_nodes.get(0).getCenterY();
					double maxX = select_nodes.get(0).getCenterX();
					double maxY = select_nodes.get(0).getCenterY();
					
					
					for(int i=0;i<ghosts.size();i++)
					{
						if(ghosts.get(i) instanceof Circle )
						{
							if(select_nodes.get(j).getCenterX()<minX)
								minX=select_nodes.get(j).getCenterX();
							if(select_nodes.get(j).getCenterX()>maxX)
								maxX=select_nodes.get(j).getCenterX();
							
							if(select_nodes.get(j).getCenterY()<minY)
								minY=select_nodes.get(j).getCenterY();
							if(select_nodes.get(j).getCenterY()>maxY)
								maxY=select_nodes.get(j).getCenterY();							 
							 
							j++;
						}
					}
					
					// update position of ghosts
					j = 0;
					for(int i=0;i<ghosts.size();i++)
					{
						if(ghosts.get(i) instanceof Circle )
						{
							((Circle) ghosts.get(i)).setCenterX(e.getX() + select_nodes.get(j).getCenterX()-(minX+maxX)/2);
							((Circle) ghosts.get(i)).setCenterY(e.getY() + select_nodes.get(j).getCenterY()-(minY+maxY)/2);
							 
							j++;
						}
					}
					
					int t=0;
					for(int i=0;i<select_nodes.size();i++)
					{				
						for(j=i;j<select_nodes.size();j++)
						{
							//find next line							
							for(;t<ghosts.size();t++)
							{
								if(ghosts.get(t) instanceof Line)
								{
									break;
								}
							}							
							
							// if there is an edge between the initial nodes update position of ghost edge
							if(select_nodes.get(i).isNeighbor(select_nodes.get(j))!=-1)
							{								
								((Line) ghosts.get(t)).setStartX(select_nodes.get(i).getCenterX()-((minX+maxX)/2-e.getX()));
								((Line) ghosts.get(t)).setStartY(select_nodes.get(i).getCenterY()-((minY+maxY)/2-e.getY()));
								((Line) ghosts.get(t)).setEndX(select_nodes.get(j).getCenterX()-((minX+maxX)/2-e.getX()));
								((Line) ghosts.get(t)).setEndY(select_nodes.get(j).getCenterY()-((minY+maxY)/2-e.getY()));
								
								t++;
							}						
						}						
					}
				  				
				}
			});	
			
			 
			clickMode = 6;
			
		}
		else
			clickMode = 0;		
	}
	
	public void goSimulator(ActionEvent event)throws IOException
	{
		try {
			//reset the zoom
			choiceBox_GraphSelection_SetZoom.setValue("1");
			
			PrintWriter pw = new PrintWriter(new File("./Graphs/" + file_name));
			StringBuilder sb = new StringBuilder();
			
			if(interaction_model == 1 && update_mechanism == 1)
  			{
				
				//interaction_model = 1;
				sb.append("1");
				sb.append(',');
				// update_mechanism = 1;
				sb.append("1");
				sb.append(',');
				sb.append('\n');
				
				// Payoffs (T,R,P,S)
				sb.append(payoff_T);
				sb.append(',');	
				sb.append(payoff_R);
				sb.append(',');	
				sb.append(payoff_P);
				sb.append(',');	
				sb.append(payoff_S);
				sb.append(',');			
				sb.append('\n');
				// Number of node
				sb.append(numberOfNodes);
				sb.append(',');			
				sb.append('\n');
				
				// Nodes details (cooperation, x position, y position, degree, adjacency)
				
				for(int i=0; i<numberOfNodes; i++)
			    {
				 	if(nodes.get(i).getCurrentStrategy()== 1)
				 		sb.append("1,");
				 	else
				 		sb.append("0,");
				 	
				 	sb.append(nodes.get(i).getCenterX());
				 	sb.append(',');	
				 	sb.append(nodes.get(i).getCenterY());
				 	sb.append(',');
				 	
				 	//get degree
				 	int degree = nodes.get(i).getDegree();	 
					
					sb.append(degree);	
					sb.append(',');	
					
					//adjacency
					
					ArrayList<Vertex> neighbours =  nodes.get(i).getNeighbors();
					
					for(int j=0; j<degree; j++)			   
					{
						
						sb.append(neighbours.get(j).getVertexNumber());	
						sb.append(',');
						
					}
				 	
					sb.append('\n');
			    }
				
				
				pw.write(sb.toString());
				pw.close();
				
				// replace current graph
				
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
				    Files.copy(Paths.get("./Graphs/"+file_name), 
				    		Paths.get("./CurrentGraph/"+file_name));
				    
				} catch (IOException e) {
				   // Failed to make graph
					
				}
				
				
				// reset node count
				Vertex c=new Vertex_1_1();
				c.setCount(0);
  				
  			}
			else if(interaction_model == 1 && update_mechanism == 2)
  			{
				
				//interaction_model = 1;
				sb.append("1");
				sb.append(',');
				// update_mechanism = 2;
				sb.append("2");
				sb.append(',');
				sb.append('\n');
				
				// Payoffs (T,R,P,S)
				sb.append(payoff_T);
				sb.append(',');	
				sb.append(payoff_R);
				sb.append(',');	
				sb.append(payoff_P);
				sb.append(',');	
				sb.append(payoff_S);
				sb.append(',');			
				sb.append('\n');
				// Number of node
				sb.append(numberOfNodes);
				sb.append(',');			
				sb.append('\n');
				
				// Nodes details (cooperation, x position, y position, degree, adjacency)
				
				for(int i=0; i<numberOfNodes; i++)
			    {
				 	if(nodes.get(i).getCurrentStrategy()== 1)
				 		sb.append("1,");
				 	else
				 		sb.append("0,");
				 	
				 	sb.append(nodes.get(i).getCenterX());
				 	sb.append(',');	
				 	sb.append(nodes.get(i).getCenterY());
				 	sb.append(',');
				 	
				 	//get degree
				 	int degree = nodes.get(i).getDegree();	 
					
					sb.append(degree);	
					sb.append(',');	
					
					//adjacency
					
					ArrayList<Vertex> neighbours =  nodes.get(i).getNeighbors();
					
					for(int j=0; j<degree; j++)			   
					{
						
						sb.append(neighbours.get(j).getVertexNumber());	
						sb.append(',');
						
					}
				 	
					sb.append('\n');
			    }
				
				
				pw.write(sb.toString());
				pw.close();
				
				// replace current graph
				
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
				    Files.copy(Paths.get("./Graphs/"+file_name), 
				    		Paths.get("./CurrentGraph/"+file_name));
				    
				} catch (IOException e) {
				   // Failed to make graph
					
				}
				
				
				// reset node count
				Vertex c=new Vertex_1_2();
				c.setCount(0);  				
  			
  			}
			else
				System.out.println("Undefined behaviour for current interaction_model and update_mechanism");

			
			
			
			// change window
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Simulator.fxml"));
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
			
			SimulatorControler controller = loader.getController();		
			controller.addMainDisplaySizeListeners(app_Stage.getScene().getHeight(),app_Stage.getScene().getWidth());
			
		}
		catch(FileNotFoundException ex){
			// TO DO: handle exception
		}
	}
	

	public void goAdvanceSimulator(ActionEvent event)throws IOException
	{
		try {
			//reset the zoom
			choiceBox_GraphSelection_SetZoom.setValue("1");
			
			PrintWriter pw = new PrintWriter(new File("./Graphs/" + file_name));
			StringBuilder sb = new StringBuilder();
			
			if(interaction_model == 1 && update_mechanism == 1)
			{
				
				//interaction_model = 1;
				sb.append("1");
				sb.append(',');
				// update_mechanism = 1;
				sb.append("1");
				sb.append(',');
				sb.append('\n');
				
				// Payoffs (T,R,P,S)
				sb.append(payoff_T);
				sb.append(',');	
				sb.append(payoff_R);
				sb.append(',');	
				sb.append(payoff_P);
				sb.append(',');	
				sb.append(payoff_S);
				sb.append(',');			
				sb.append('\n');
				// Number of node
				sb.append(numberOfNodes);
				sb.append(',');			
				sb.append('\n');
				
				// Nodes details (cooperation, x position, y position, degree, adjacency)
				
				for(int i=0; i<numberOfNodes; i++)
				    {
					 	if(nodes.get(i).getCurrentStrategy()== 1)
					 		sb.append("1,");
					 	else
					 		sb.append("0,");
					 	
					 	sb.append(nodes.get(i).getCenterX());
					 	sb.append(',');	
					 	sb.append(nodes.get(i).getCenterY());
					 	sb.append(',');
					 	
					 	//get degree
					 	int degree = nodes.get(i).getDegree();	 
						
						sb.append(degree);	
						sb.append(',');	
						
						//adjacency
						
						ArrayList<Vertex> neighbours =  nodes.get(i).getNeighbors();
						
						for(int j=0; j<degree; j++)			   
						{
							
							sb.append(neighbours.get(j).getVertexNumber());	
							sb.append(',');
							
						}
					 	
						sb.append('\n');
				    }
				
				
				pw.write(sb.toString());
				pw.close();
				
				// replace current graph
				
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
				    Files.copy(Paths.get("./Graphs/"+file_name), 
				    		Paths.get("./CurrentGraph/"+file_name));
				    
				} catch (IOException e) {
				   // Failed to make graph
					
				}
				
				
				// reset node count
				Vertex c=new Vertex_1_1();
				c.setCount(0);
				
			}
			else if(interaction_model == 1 && update_mechanism == 2)
			{
				
				//interaction_model = 1;
				sb.append("1");
				sb.append(',');
				// update_mechanism = 2;
				sb.append("2");
				sb.append(',');
				sb.append('\n');
				
				// Payoffs (T,R,P,S)
				sb.append(payoff_T);
				sb.append(',');	
				sb.append(payoff_R);
				sb.append(',');	
				sb.append(payoff_P);
				sb.append(',');	
				sb.append(payoff_S);
				sb.append(',');			
				sb.append('\n');
				// Number of node
				sb.append(numberOfNodes);
				sb.append(',');			
				sb.append('\n');
				
				// Nodes details (cooperation, x position, y position, degree, adjacency)
				
				for(int i=0; i<numberOfNodes; i++)
				    {
					 	if(nodes.get(i).getCurrentStrategy()== 1)
					 		sb.append("1,");
					 	else
					 		sb.append("0,");
					 	
					 	sb.append(nodes.get(i).getCenterX());
					 	sb.append(',');	
					 	sb.append(nodes.get(i).getCenterY());
					 	sb.append(',');
					 	
					 	//get degree
					 	int degree = nodes.get(i).getDegree();	 
						
						sb.append(degree);	
						sb.append(',');	
						
						//adjacency
						
						ArrayList<Vertex> neighbours =  nodes.get(i).getNeighbors();
						
						for(int j=0; j<degree; j++)			   
						{
							
							sb.append(neighbours.get(j).getVertexNumber());	
							sb.append(',');
							
						}
					 	
						sb.append('\n');
				    }
				
				
				pw.write(sb.toString());
				pw.close();
				
				// replace current graph
				
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
				    Files.copy(Paths.get("./Graphs/"+file_name), 
				    		Paths.get("./CurrentGraph/"+file_name));
				    
				} catch (IOException e) {
				   // Failed to make graph
					
				}
				
				
				// reset node count
				Vertex c=new Vertex_1_2();
				c.setCount(0);				
			
			}
			else
				System.out.println("Undefined behaviour for current interaction_model and update_mechanism");
					
			
			
			// change window
			FXMLLoader loader = new FXMLLoader(getClass().getResource("AdvanceSimulator_MainMenu.fxml"));
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
		catch(FileNotFoundException ex){
			// TO DO: handle exception
		}
	}
	
	
	// What to do when the user clicks on the screen
	public void mainDisplayClick(MouseEvent event)throws IOException
	{
		if(event.getButton() == MouseButton.PRIMARY)
		{
			if(clickMode == 0)
			{
				
				//clear selected nodes
				select_nodes = null;
				
				// Deselect if possible
				currentNode=null;
							
				//make the graph info visible
				changeSelectionPaneToGraph();
				
			}
			else if(clickMode == 1)
			{
				// place node
				
				// TO DO: check if there is no node in some range
				
				select_nodes = null;	
				currentNode=null;
				
				placeNode(event.getX(),event.getY(), 1);
				
				updateMainDisplay();
				
				
				
				// Change display info and make c the current node
				changeSelectionPaneToNode(nodes.get(nodes.size()-1));
							
				// if shift is not pressed stop placing nodes
				if(event.isShiftDown()==false)
				  {
					  clickMode = 0;
					  pane_MainDisplay.getScene().setCursor(Cursor.DEFAULT);
					  
					  //remove ghosts and mouse listener
					  pane_MainDisplay.setOnMouseMoved(null);
					  for(int i =0; i<ghosts.size(); i++)
					  {						  
						  pane_MainDisplay.getChildren().remove(ghosts.get(i));
					  }					  					  					  
					  ghosts = new ArrayList<Object> ();
					  
				  }
				
			}
			else if(clickMode == 2)
			{
				// Place edge start							
				
			}
			else if(clickMode == 3)
			{
				// Place edge end
			}
			else if(clickMode == 4)
			{
				// Delete
			}
			else if(clickMode == 5)
			{
				
				// End select
				
				select_end_x = event.getX();
				select_end_y = event.getY();
				
				double temp;
				if(select_start_x>select_end_x)
				{
					temp = select_start_x;
					select_start_x = select_end_x;
					select_end_x = temp;			
				}
				
				if(select_start_y>select_end_y)
				{
					temp = select_start_y;
					select_start_y = select_end_y;
					select_end_y = temp;
				}
				
				 select_nodes = new ArrayList<Vertex> ();
				 
				 for(int i=0;i<numberOfNodes;i++)
				 {
					 if(nodes.get(i).getCenterX()>=select_start_x && nodes.get(i).getCenterX()<=select_end_x)
						 if(nodes.get(i).getCenterY()>=select_start_y && nodes.get(i).getCenterY()<=select_end_y)
							 select_nodes.add(nodes.get(i));
				 }
				 
				
				
				//remove ghosts and mouse listener
			    pane_MainDisplay.setOnMouseMoved(null);
			    for(int i =0; i<ghosts.size(); i++)
			    {						  
				    pane_MainDisplay.getChildren().remove(ghosts.get(i));
			    }					  					  					  
			    ghosts = new ArrayList<Object> ();
			    
			    if(select_nodes.size()==0)		    
			    {
					clickMode = 0;
					select_nodes = null;
					changeSelectionPaneToGraph();
			    }
			    else
			    {
			    	changeSelectionPaneToNode(select_nodes.get(0));
			    	clickMode = 0;
			    }
				
			}			
			else if(clickMode == 6)
			{
				// Paste
				
				int startNumberOfNodes = numberOfNodes;
				
				//place new nodes
				
				int tempj = 0;
				for(int i=0;i<ghosts.size();i++)
				{
					if(ghosts.get(i) instanceof Circle )
					{
						
						double new_node_position_x = ((Circle) ghosts.get(i)).getCenterX();
						double new_node_position_y = ((Circle) ghosts.get(i)).getCenterY();
						
						placeNode(new_node_position_x, new_node_position_y, select_nodes.get(tempj).getCurrentStrategy());
											
						tempj++;
						
					}
				}				
				
				// add edges
				for(int i=0;i<select_nodes.size();i++)
				{
					currentNode = nodes.get(i+startNumberOfNodes);
					for(int j=0;j<select_nodes.size();j++)
					{	
						// if there is an edge between the initial nodes place an edge between the copy nodes
						if(select_nodes.get(i).isNeighbor(select_nodes.get(j))!=-1)
							placeEdge(nodes.get(j+startNumberOfNodes),nodes.get(i+startNumberOfNodes));
						
						//TO DO if all_good
						
					}
				}
				
				// if shift is not pressed stop placing edges
				if(event.isShiftDown()==false)
				{					
					clickMode = 0;
					pane_MainDisplay.getScene().setCursor(Cursor.DEFAULT);
					
					//remove ghosts and mouse listener
				    pane_MainDisplay.setOnMouseMoved(null);
				    for(int i =0; i<ghosts.size(); i++)
				    {						  
					    pane_MainDisplay.getChildren().remove(ghosts.get(i));
				    }					  					  					  
				    ghosts = new ArrayList<Object> ();
					
					select_nodes = null;	
					currentNode=null; 
					
					changeSelectionPaneToGraph();
				}
				
				
				updateMainDisplay();
			}
		}
		else if(event.getButton() == MouseButton.SECONDARY)
		{
			clickMode = 0;
			pane_MainDisplay.getScene().setCursor(Cursor.DEFAULT);
			
			//remove ghosts and mouse listener
			pane_MainDisplay.setOnMouseMoved(null);
			for(int i =0; i<ghosts.size(); i++)
			{						  
				pane_MainDisplay.getChildren().remove(ghosts.get(i));
			}					  					  					  
			ghosts = new ArrayList<Object> ();
			
			//clear selected nodes
			select_nodes = null;
			
			// Deselect if possible
			currentNode=null;
						
			//make the graph info visible
			changeSelectionPaneToGraph();
		}
	}
	
	public void mainDisplayDragStart(MouseEvent event)throws IOException
	{
		
		if(clickMode == 0)
		{
			// Start select
			
			select_start_x = event.getX();
			select_start_y = event.getY();
			
			Rectangle r = new Rectangle(select_start_x, select_start_y, 1, 1);
			
			r.setStroke(Color.BLACK);						
			r.setFill(Color.rgb(0, 200, 255));						
				
			r.setOpacity(0.5);						
			pane_MainDisplay.getChildren().addAll(r);
			
			ghosts.add(r);
			
			//add listener for change in mouse position	
			 pane_MainDisplay.setOnMouseDragged(new EventHandler<MouseEvent>(){
				 @Override
		           public void handle(MouseEvent e) {
					 
					// the view start and end range
						double viewXStart = (pane_MainDisplay.getBoundsInLocal().getMaxX()-scrlpane_MainDisplay.getPrefWidth())*scrlpane_MainDisplay.getHvalue();
						viewXStart=viewXStart-50;
						double viewXEnd = viewXStart+scrlpane_MainDisplay.getPrefWidth();
						viewXEnd = viewXEnd +200;
						
						double viewYStart = (pane_MainDisplay.getBoundsInLocal().getMaxY()-scrlpane_MainDisplay.getPrefHeight())*scrlpane_MainDisplay.getVvalue();
						viewYStart=viewYStart-50;
						double viewYEnd = viewYStart+scrlpane_MainDisplay.getPrefHeight();
						viewYEnd = viewYEnd +200;
					 
						if(e.getX()>=viewXStart && e.getY()>=viewYStart && e.getX()<=viewXEnd && e.getY()<=viewYEnd)
						{							
							if(e.getX()>select_start_x)
							{
								r.setWidth(e.getX() - select_start_x);
							}
							else
							{
								r.setWidth(select_start_x - e.getX());
								r.setX(e.getX());
							}
							
							if(e.getY()>select_start_y)
							{
								r.setHeight(e.getY() - select_start_y);
							}
							else
							{
								r.setHeight(select_start_y - e.getY());
								r.setY(e.getY());
							}
						}
				 }
			 });
			
			clickMode = 5;
		}
		
	}
	
	public void placeNode(double ev_x, double ev_y, int defect)
	{		
		numberOfNodes++;		
		
		
		if(interaction_model == 1 && update_mechanism == 1)
		{
			Vertex c = new Vertex_1_1(defect);
			
			c.setCenterX(ev_x);
			c.setCenterY(ev_y);
			
			// increase screen size if needed
			if(ev_x>pane_MainDisplay.getPrefWidth())
				pane_MainDisplay.setPrefWidth(c.getCenterX() + 200);
			if(ev_y>pane_MainDisplay.getPrefHeight())
				pane_MainDisplay.setPrefHeight(c.getCenterY() + 200);
			
			
			
			// add the node to the list
			nodes.add(c);
		}
		else if(interaction_model == 1 && update_mechanism == 2)
		{
			Vertex c = new Vertex_1_2(defect);
			
			c.setCenterX(ev_x);
			c.setCenterY(ev_y);
			
			// increase screen size if needed
			if(ev_x>pane_MainDisplay.getPrefWidth())
				pane_MainDisplay.setPrefWidth(c.getCenterX() + 200);
			if(ev_y>pane_MainDisplay.getPrefHeight())
				pane_MainDisplay.setPrefHeight(c.getCenterY() + 200);
			
			
			
			// add the node to the list
			nodes.add(c);
		}
		else
			System.out.println("Undefined behaviour for current interaction_model and update_mechanism");
		
	}
	
	public void showNode(Vertex v)
	{
		
		Circle c;
		if(v.getVisualObject()==null)
			c = new Circle();
		else
			c=v.getVisualObject();
		
		
		
		c.setCenterX(v.getCenterX());
		c.setCenterY(v.getCenterY());
		c.setRadius(nodeRadius);
		c.setStroke(Color.BLACK);
		
		if(v.getVisualObject()==null)
		{
			// add an on mouse click event listener 
			
			
			c.setOnMouseClicked(new EventHandler<MouseEvent>(){
				 
		          @Override
		          public void handle(MouseEvent e) {
		        	  if(e.getButton() == MouseButton.PRIMARY)
		        	  {		        		  
			        	  if(clickMode == 0)		      		
			        	  {				        		  
		        			  select_nodes = new ArrayList<Vertex> ();
			 				  select_nodes.add(v);
			        		  
			        		  // Change display info and make c the current node	        			
			        		  changeSelectionPaneToNode(v);
			        		  
			        	  }	        	 
			        	  else if(clickMode == 2)			         
			        	  {	        		  
			        		  
			        		  // Change display info and make c the current node	        			
			        		  changeSelectionPaneToNode(v);
			        		  
			        		  clickMode = 3;
			        		  
			        		  Image image = new Image(new File("./CursorImages/EdgeEnd.png").toURI().toString());
			        		  pane_MainDisplay.getScene().setCursor(new ImageCursor(image));
			        		  
			        		  //ghost
			        		  Line temp_edge = new Line();

			        		  // make the line object

			        		  temp_edge.setStartX(e.getX());
			        		  temp_edge.setStartY(e.getY());
			        		  temp_edge.setEndX(e.getX());
			        		  temp_edge.setEndY(e.getY());
			        		  
			        		  temp_edge.setStrokeWidth(lineWidth);
			        		  temp_edge.setOpacity(0.5);
			        		  
			        		  pane_MainDisplay.getChildren().addAll(temp_edge);
			        		  
			        		  // make it so the nodes are above the edge so we can click on the nodes
			        		  temp_edge.toBack();			        		  
			        		  
			        		  //remove ghosts and mouse listener
			        		  pane_MainDisplay.setOnMouseMoved(null);
			        		  for(int i =0; i<ghosts.size(); i++)
			        		  {
			        			  pane_MainDisplay.getChildren().remove(ghosts.get(i));
			        		  }					  					  					  
				    		  ghosts = new ArrayList<Object> ();
			        		  
				    		  ghosts.add(temp_edge);
			        		  
			        		  //add listener for change in mouse position
			        		  pane_MainDisplay.setOnMouseMoved(new EventHandler<MouseEvent>(){
			        			  @Override
			        			  public void handle(MouseEvent ev) {
			        				  //make ghost node to follow mouse
			        				  temp_edge.setEndX(ev.getX());
			        				  temp_edge.setEndY(ev.getY());			        				  
			        			  }
			        		  });

			        		 
			        	  }
			        	  else if(clickMode == 3)			         
			        	  {
			        		  if(select_nodes == null || select_nodes.size()==1)// one node selected	
			        		  {
			        			  			        			  
			        			  // add edge end    		  
				        		  int all_good = placeEdge(v, currentNode);
					        			  
				        		  // if the edge was placed
				        		  if(all_good != 0)
				        		  {			        			  
				        			  // if shift is not pressed stop placing edges
					  				  if(e.isShiftDown()==false)
				  					  {
					  					  // Change display info and make c the current node
					        			  changeSelectionPaneToNode(v);
					        			  
					        			  clickMode = 0;
										  pane_MainDisplay.getScene().setCursor(Cursor.DEFAULT);
										
										  //remove ghosts and mouse listener
										  pane_MainDisplay.setOnMouseMoved(null);
										  for(int i =0; i<ghosts.size(); i++)
										  {						  
											  pane_MainDisplay.getChildren().remove(ghosts.get(i));
										  }					  					  					  
										  ghosts = new ArrayList<Object> ();
										
										  //used to show the new edge
										  v.setMark(0);
										  currentNode.setMark(0);
										  updateMainDisplay();
				  					  }	
					  				  
					  				v.setMark(0);
									currentNode.setMark(0);  
					  				updateMainDisplay();			        			  
				        		  }
			        		  }
			        		  else
			        		  {
			        			  for(int i = 0; i<select_nodes.size(); i++)
			        			  {
			        				  placeEdge(v, select_nodes.get(i));
			        				  
			        				  v.setMark(0);
			        				  select_nodes.get(i).setMark(0);  
						  			  
			        			  }
			        			  
			        			  updateMainDisplay();
			        			  
			        			  if(e.isShiftDown()==false)
			  					  {
				  					  // Change display info and make c the current node
			        				  select_nodes = null;
				        			  changeSelectionPaneToNode(v);
				        			  
				        			  clickMode = 0;
									  pane_MainDisplay.getScene().setCursor(Cursor.DEFAULT);
									
									  //remove ghosts and mouse listener
									  pane_MainDisplay.setOnMouseMoved(null);
									  for(int i =0; i<ghosts.size(); i++)
									  {						  
										  pane_MainDisplay.getChildren().remove(ghosts.get(i));
									  }					  					  					  
									  ghosts = new ArrayList<Object> ();
									  
									  //used to show the new edge
									  v.setMark(0);
									  currentNode.setMark(0);
									  updateMainDisplay();
			  					  }	
			        		  }
			        		  
			        	  }
			        	  else if(clickMode == 4)			         
			        	  {		        
			        		  // remove node	
			        		  
			        		  // remove all the edges to the node
			        		 
			        		  for(int i = v.getDegree() - 1; i>=0; i--)
			        		  {
			        			  pane_MainDisplay.getChildren().remove(v.getEdge(i).getVisualObject());
			        			  v.getNeighbor(i).removeNeighbor(v);
			        			  v.removeNeighbor(v.getNeighbor(i)); 
			        		  }
			        		  
			        		  
			        		  // remove the node from scene
			        		  
			        		  pane_MainDisplay.getChildren().remove(c);
			        		  
			        		  // remove it from the list of nodes		        		  
			        		  nodes.remove(v);
			        		  
			        		  numberOfNodes--;
			        		  
			        		 
			        		  if(numberOfNodes>0)
			        		  {
			        			  
				        		  // update the count/node number of all the nodes higher than it
				        		  for(int i = v.getVertexNumber();i<numberOfNodes;i++)
				        		  {
				        			  
				        			  nodes.get(i).setVertexNumber(i);
				        		  }
				        		  
				        		  nodes.get(0).setCount(nodes.get(0).getCount()-1);		        		 
			        		  }
			        		  else
			        		  {
			        			  v.setCount(0);
			        		  }		        		  
			        		 
			        		  // Make current node null
			        		  currentNode = null;
			        		  
			        		  // Change display info 	        			
			        		  changeSelectionPaneToGraph();
		        			  
			        		  // if shift is not pressed stop placing nodes
			  				  if(e.isShiftDown()==false)
		  					  {
			  					  clickMode = 0;
			  					  pane_MainDisplay.getScene().setCursor(Cursor.DEFAULT);
			  					  
			  					  //remove ghosts and mouse listener
			  					  pane_MainDisplay.setOnMouseMoved(null);
			  					  for(int i =0; i<ghosts.size(); i++)
			  					  {						  
			  						  pane_MainDisplay.getChildren().remove(ghosts.get(i));
			  					  }					  					  					  
			  					  ghosts = new ArrayList<Object> ();
		  					  }
		        				
		        			  updateMainDisplay();
			        	  }
			        	  else if(clickMode == 7)
			        	  {
			        		  clickMode = 0;
			        	  }
		        	  }  
		        	// make sure the events stop here
		        	  e.consume();
		          }
		          
		          });
			
			c.setOnMouseDragged(new EventHandler<MouseEvent>(){
				
				 @Override
		          public void handle(MouseEvent e) {
					 // if start or during drag
					 if(clickMode == 0 || clickMode == 7)
					 {
						 
						 if(select_nodes == null || (select_nodes.size()==1 && select_nodes.contains(v)) || select_nodes.contains(v)==false)// one node selected	
						 {	
							 if(select_nodes == null || select_nodes.contains(v)==false)
							 {
								 select_nodes = null;
							 }
							 
							// bring node to the top
							c.toFront();
							 

							// Change display info 	        			
							changeSelectionPaneToNode(v);
							 				 

							if(e.getX()>nodeRadius)
								v.setCenterX(e.getX());
							if(e.getY()>nodeRadius)
								v.setCenterY(e.getY());
							if(v.getVisualObject()!=null)
							{
								v.getVisualObject().setCenterX(v.getCenterX());
								v.getVisualObject().setCenterY(v.getCenterY());		    						
							}

							 
							// update edges
							for(int i=0;i<v.getDegree();i++)
							{
								
								// if start node
								if(v.getEdge(i).getNodeA() == currentNode)
								{
									v.getEdge(i).setStartX(v.getCenterX());
									v.getEdge(i).setStartY(v.getCenterY());
											    					
									if(v.getEdge(i).getVisualObject()!=null)
									{
										v.getEdge(i).getVisualObject().setStartX(v.getCenterX());
										v.getEdge(i).getVisualObject().setStartY(v.getCenterY());			    						
									}
								}
								else// if end node
								{
									v.getEdge(i).setEndX(v.getCenterX());
									v.getEdge(i).setEndY(v.getCenterY());
											    					
									if(v.getEdge(i).getVisualObject()!=null)
									{
										v.getEdge(i).getVisualObject().setEndX(v.getCenterX());
										v.getEdge(i).getVisualObject().setEndY(v.getCenterY());		    						
									}
								}
								
								
							}

							// increase screen size if needed

							if(v.getCenterX()>pane_MainDisplay.getPrefWidth())
								pane_MainDisplay.setPrefWidth(v.getCenterX() + 200);

							if(v.getCenterY()>pane_MainDisplay.getPrefHeight())
								pane_MainDisplay.setPrefHeight(v.getCenterY() + 200);
							
							 
						 }
						 else
						 {
							 double vStartX=v.getCenterX();
							 double vStartY=v.getCenterY();
							 
							 //nodes positions
							 if(e.getX()>nodeRadius)
									v.setCenterX(e.getX());
								if(e.getY()>nodeRadius)
									v.setCenterY(e.getY());
								if(v.getVisualObject()!=null)
								{
									v.getVisualObject().setCenterX(v.getCenterX());
									v.getVisualObject().setCenterY(v.getCenterY());		    						
								}
							 
							 for(int i=0; i<select_nodes.size(); i++)			
							 {
								 if(select_nodes.get(i)!=v)
								 {
									 								 
									 if(e.getX()>nodeRadius)
										 select_nodes.get(i).setCenterX(select_nodes.get(i).getCenterX()-vStartX+e.getX());
									 if(e.getY()>nodeRadius)
										 select_nodes.get(i).setCenterY(select_nodes.get(i).getCenterY()-vStartY+e.getY());
									 
									 if(select_nodes.get(i).getVisualObject()!=null)
									 {
										 select_nodes.get(i).getVisualObject().setCenterX(select_nodes.get(i).getCenterX());
										 select_nodes.get(i).getVisualObject().setCenterY(select_nodes.get(i).getCenterY());		    						
									 }
								 }
							 }
							 //edges positions
							 for(int j=0; j<select_nodes.size(); j++)			
							 {
								// update edges
									for(int i=0;i<select_nodes.get(j).getDegree();i++)
									{
										
										// if start node
										if(select_nodes.get(j).getEdge(i).getNodeA() == select_nodes.get(j))
										{
											select_nodes.get(j).getEdge(i).setStartX(select_nodes.get(j).getCenterX());
											select_nodes.get(j).getEdge(i).setStartY(select_nodes.get(j).getCenterY());
													    					
											if(select_nodes.get(j).getEdge(i).getVisualObject()!=null)
											{
												select_nodes.get(j).getEdge(i).getVisualObject().setStartX(select_nodes.get(j).getCenterX());
												select_nodes.get(j).getEdge(i).getVisualObject().setStartY(select_nodes.get(j).getCenterY());			    						
											}
										}
										else// if end node
										{
											select_nodes.get(j).getEdge(i).setEndX(select_nodes.get(j).getCenterX());
											select_nodes.get(j).getEdge(i).setEndY(select_nodes.get(j).getCenterY());
													    					
											if(select_nodes.get(j).getEdge(i).getVisualObject()!=null)
											{
												select_nodes.get(j).getEdge(i).getVisualObject().setEndX(select_nodes.get(j).getCenterX());
												select_nodes.get(j).getEdge(i).getVisualObject().setEndY(select_nodes.get(j).getCenterY());		    						
											}
										}
										
										
									}
							 }
							 
							// increase screen size if needed
							for(int i=0; i<select_nodes.size(); i++)			
							{
								if(select_nodes.get(i).getCenterX()>pane_MainDisplay.getPrefWidth())
									pane_MainDisplay.setPrefWidth(select_nodes.get(i).getCenterX() + 200);

								if(select_nodes.get(i).getCenterY()>pane_MainDisplay.getPrefHeight())
									pane_MainDisplay.setPrefHeight(select_nodes.get(i).getCenterY() + 200);
							}
							
							 
						 }
				 
						 clickMode = 7;
						 
						//update view
						updateMainDisplay();
						
						//make sure the mouse event stops here
						e.consume();
					} 
				 }
			});
			
			c.setOnDragDetected(new EventHandler<MouseEvent>(){
				
				 @Override
		          public void handle(MouseEvent e) {
					// make sure the events stop here
		        	e.consume();
				 }
				 });			
		}
		
		if(interaction_model == 1 && update_mechanism == 1)
		{
			if(v.getCurrentStrategy()==0)
			{		
				c.setFill(Color.rgb(150, 0, 0)); //defector
			}
			else
			{
				c.setFill(Color.rgb(0, 100, 255)); // cooperator
			}	        			
		}
		else if(interaction_model == 1 && update_mechanism == 2)
		{
			if(v.getCurrentStrategy()==0)
			{		
				c.setFill(Color.rgb(150, 0, 0)); //defector
			}
			else
			{
				c.setFill(Color.rgb(0, 100, 255)); // cooperator
			}
		}
		else
			System.out.println("Undefined behaviour for current interaction_model and update_mechanism");
		
		if(v.getVisualObject()==null)
		{
			pane_MainDisplay.getChildren().addAll(c);
			v.setVisualObject(c);
		}
		
		//add glow
		if(v==currentNode || (select_nodes!=null && select_nodes.contains(v)))
		{
			
			if(v.getVisualObject() != null)
			{
				// add new visuals
				DropShadow borderGlow = new DropShadow();
				borderGlow.setColor(Color.rgb(255, 255, 100));
				borderGlow.setOffsetX(0f);
				borderGlow.setOffsetY(0f);
				borderGlow.setHeight(glowRadius);
				borderGlow.setWidth(glowRadius);
				borderGlow.setSpread(0.5);
				
				v.getVisualObject().setEffect(borderGlow); 
			}
		}
		else
		{
			ArrayList<Vertex> neighbors = v.getNeighbors();
			for(int i=0;i<v.getDegree();i++)
			{
				if(select_nodes!=null && select_nodes.contains(neighbors.get(i)))
				{
					DropShadow borderGlow2 = new DropShadow();
					borderGlow2.setOffsetX(0f);
					borderGlow2.setOffsetY(0f);
					borderGlow2.setHeight(glowRadius);
					borderGlow2.setWidth(glowRadius);
					borderGlow2.setColor(Color.rgb(100, 100, 200));
					borderGlow2.setSpread(0.5);					          					  	
					v.getVisualObject().setEffect(borderGlow2);	
					
					break;
				}
			}
		}
		
		
		
	}
	
	public int placeEdge(Vertex node , Vertex neighbor)
	{				
		  // Check if start and end node are not the same node
		  if(node!=neighbor)
		  {
			  //Check if the edge is not there already
			  if(node.isNeighbor(neighbor) == -1)
			  {
				  Edge temp_edge = new Edge(node, neighbor);
				 
				  //update neighborhood for both nodes
				  node.addNeighbor(neighbor, temp_edge);
				  neighbor.addNeighbor(node, temp_edge);
				  // make the line object
			
				  temp_edge.setStartX(node.getCenterX());
				  temp_edge.setStartY(node.getCenterY());
				  temp_edge.setEndX(neighbor.getCenterX());
				  temp_edge.setEndY(neighbor.getCenterY());
				  
				 
				 
				  return 1;
				  
			  }
		  }
		  return 0;
		
	}
	
	public void showEdge(Edge edge)
	{
		 Line temp_edge;
		 
		 if(edge.getVisualObject()==null)
			 temp_edge = new Line();
		 else
			 temp_edge=edge.getVisualObject();
		 
		  // make the line object
	
		  temp_edge.setStartX(edge.getStartX());
		  temp_edge.setStartY(edge.getStartY());
		  temp_edge.setEndX(edge.getEndX());
		  temp_edge.setEndY(edge.getEndY());
		  
		 
		  temp_edge.setStrokeWidth(lineWidth);
		  
		  // add it to the pane
		  if(edge.getVisualObject()==null)
			  pane_MainDisplay.getChildren().addAll(temp_edge);
		  
		  // make it so the nodes are above the edges
		  temp_edge.toBack();
		  
		 
		  		 
		  if(edge.getVisualObject()==null)
		  {
			  
		  
			  // add an on mouse click event listener 
			  temp_edge.setOnMouseClicked(new EventHandler<MouseEvent>(){
					 
			          @Override
			          public void handle(MouseEvent e) {  		
			        	  if(e.getButton() == MouseButton.PRIMARY)
			        	  {
				        	  if(clickMode == 4)			         
				        	  {
				        		  // delete edge	
				        		  
				        		  // remove it from the scene
				        		  pane_MainDisplay.getChildren().remove(temp_edge);
				        		  //remove it from the nodes
				        		  edge.getNodeB().removeNeighbor(edge.getNodeA());
				        		  edge.getNodeA().removeNeighbor(edge.getNodeB());
				        		  
				        		  
				        		  // if shift is not pressed stop placing nodes
				  				  if(e.isShiftDown()==false) 
				  				  {
				  					  clickMode = 0;
				  					  pane_MainDisplay.getScene().setCursor(Cursor.DEFAULT);
				  					  
				  					  //remove ghosts and mouse listener
				  					  pane_MainDisplay.setOnMouseMoved(null);
				  					  for(int i =0; i<ghosts.size(); i++)
				  					  {						  
				  						  pane_MainDisplay.getChildren().remove(ghosts.get(i));
				  					  }					  					  					  
				  					  ghosts = new ArrayList<Object> ();
				  					  
				  				  }
				  					  
				        		  
				        		  updateMainDisplay();
				        	  }
			        	  } 
			        	// make sure the events stop here
			        	  e.consume();
			          }
			  });
			  
			  
			 
			  edge.setVisualObject(temp_edge);
		  }
	}
	
	public void changeSelectionPaneToNode(Vertex v)
	{
		if(select_nodes == null || select_nodes.size()==1)// one node selected	
		{
						
			// remove old visuals
			         			  
			  for(int i=0;i<numberOfNodes;i++)
			  {	
				  if(nodes.get(i).getVisualObject() != null)
					  nodes.get(i).getVisualObject().setEffect(null);
			  }
		  
			  
			  // select node
			  currentNode = v;
			  if(currentNode.getVisualObject() != null)
			  {
				// add new visuals
				  DropShadow borderGlow = new DropShadow();
				  borderGlow.setColor(Color.rgb(255, 255, 100));
				  borderGlow.setOffsetX(0f);
				  borderGlow.setOffsetY(0f);
				  borderGlow.setHeight(glowRadius);
				  borderGlow.setWidth(glowRadius);
				  borderGlow.setSpread(0.5);
				  
				  currentNode.getVisualObject().setEffect(borderGlow); 
			  }
			  
			  
			  
			  ArrayList<Vertex> neighbors = currentNode.getNeighbors();
			  
			  for(int i=0;i<currentNode.getDegree();i++)
			  {				  
				  if(neighbors.get(i).getVisualObject() != null)
				  {					  
					  DropShadow borderGlow2 = new DropShadow();
					  borderGlow2.setOffsetX(0f);
					  borderGlow2.setOffsetY(0f);
					  borderGlow2.setHeight(glowRadius);
					  borderGlow2.setWidth(glowRadius);
					  borderGlow2.setColor(Color.rgb(100, 100, 200));
					  borderGlow2.setSpread(0.5);					          					  	
					  neighbors.get(i).getVisualObject().setEffect(borderGlow2);					  
				  }
			  }
			
			
			
			// swap visible pane		
			pane_Selection_Node.setVisible(true);
			pane_Selection_Graph.setVisible(false);
			
			// Update values
			
			lbl_NodeSelection_Title.setText("Node "+ currentNode.getVertexNumber());
			
			if(currentNode.getCurrentStrategy()== 1)
					 cbox_NodeSelection_Cooperation.setSelected(true);
				 else
					 cbox_NodeSelection_Cooperation.setSelected(false);
			
			 
			txt_NodeSelection_XValue.setText(Double.toString(currentNode.getCenterX()));
			txt_NodeSelection_YValue.setText(Double.toString(currentNode.getCenterY()));
			
		}
		else //Group selected
		{
			for(int i=0;i<numberOfNodes;i++)
			{
				if(nodes.get(i).getVisualObject() != null)
					nodes.get(i).getVisualObject().setEffect(null);
			}
			
			// select node
			// add new visuals
			DropShadow borderGlow = new DropShadow();
			borderGlow.setColor(Color.rgb(255, 255, 100));
			borderGlow.setOffsetX(0f);
			borderGlow.setOffsetY(0f);
			borderGlow.setHeight(glowRadius);
			borderGlow.setWidth(glowRadius);
			borderGlow.setSpread(0.5);
			
			for(int i=0; i<select_nodes.size(); i++)			
			{
				if(select_nodes.get(i).getVisualObject() != null)
					{	
						select_nodes.get(i).getVisualObject().setEffect(borderGlow); 
					}
			}
			
			for(int i=0; i<select_nodes.size(); i++)			
			{
				
				ArrayList<Vertex> neighbors = select_nodes.get(i).getNeighbors();
				  
				for(int j=0;j<select_nodes.get(i).getDegree();j++)
				{
					if(neighbors.get(j).getVisualObject() != null && neighbors.get(j).getVisualObject().getEffect() == null)
					{
						DropShadow borderGlow2 = new DropShadow();
						borderGlow2.setOffsetX(0f);
						borderGlow2.setOffsetY(0f);
						borderGlow2.setHeight(glowRadius);
						borderGlow2.setWidth(glowRadius);
						borderGlow2.setColor(Color.rgb(100, 100, 200));
						borderGlow2.setSpread(0.5);					          					  	
						neighbors.get(j).getVisualObject().setEffect(borderGlow2);
					}
				}
			}
			
			// swap visible pane		
			pane_Selection_Node.setVisible(true);
			pane_Selection_Graph.setVisible(false);
			
			// Update values
			
			lbl_NodeSelection_Title.setText("Nodes: "+ select_nodes.size());
			
			if(select_nodes.get(0).getCurrentStrategy()== 1)
					 cbox_NodeSelection_Cooperation.setSelected(true);
				 else
					 cbox_NodeSelection_Cooperation.setSelected(false);
			
			double minX = select_nodes.get(0).getCenterX();
			double minY = select_nodes.get(0).getCenterY();
			
			
			for(int i=1; i<select_nodes.size(); i++)			
			{
				if(minX > select_nodes.get(i).getCenterX())
					minX=select_nodes.get(i).getCenterX();
				
				if(minY > select_nodes.get(i).getCenterY())
					minY=select_nodes.get(i).getCenterY();
			}
			
			txt_NodeSelection_XValue.setText(Double.toString(minX));
			txt_NodeSelection_YValue.setText(Double.toString(minY));
			
			
		}
		 			
	}
	
	public void changeSelectionPaneToGraph()
	{
		
		for(int i=0;i<numberOfNodes;i++)
		{
			if(nodes.get(i).getVisualObject()!=null)
				nodes.get(i).getVisualObject().setEffect(null);
		}
		
		
		pane_Selection_Node.setVisible(false);
		pane_Selection_Graph.setVisible(true);
		
		lbl_GraphSelection_NumberOfNodes.setText(numberOfNodes + " Nodes");
		
		txt_GraphSelection_TValue.setText(Integer.toString(payoff_T));
		txt_GraphSelection_RValue.setText(Integer.toString(payoff_R));
		txt_GraphSelection_PValue.setText(Integer.toString(payoff_P));
		txt_GraphSelection_SValue.setText(Integer.toString(payoff_S));		
		
		lbl_GraphSelection_InteractionModel.setText("Int. Mod. : " + interaction_model);
		
		lbl_GraphSelection_UpdateMechanism.setText("Upd. Mec. : " + update_mechanism);
			
	}
	
	public void checkNodeSelectionCooperationProperty(ActionEvent event)throws IOException
	{
		if(select_nodes == null || select_nodes.size()==1)
		{
			if(cbox_NodeSelection_Cooperation.isSelected()== true)
			{
				currentNode.setCurrentStrategy(1);				
			}
			else
			{
				currentNode.setCurrentStrategy(0);			  
			}
			
			currentNode.setMark(0);
			updateMainDisplay();
		}
		else
		{
			for(int i=0; i<select_nodes.size(); i++)			
			{
				if(cbox_NodeSelection_Cooperation.isSelected()== true)
				{
					select_nodes.get(i).setCurrentStrategy(1);				
				}
				else
				{
					select_nodes.get(i).setCurrentStrategy(0);			  
				}
				
				select_nodes.get(i).setMark(0);				
			}
			updateMainDisplay();
		}
			
	}
	
	public void addTextFieldSubmitListeners()
	{
		
		/*
		 * 
		 * Graph
		 * 
		 */
		
		// Payoff T
		txt_GraphSelection_TValue.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e){
				if(e.getCode() == KeyCode.ENTER)
				{
					pane_Selection_Graph.requestFocus();
				}
			}
			});
		
		txt_GraphSelection_TValue.focusedProperty().addListener((observable, oldValue, newValue) -> {
		    if(newValue==false)
		    {
		    	// check input
		    	try{
		    		int tempImpVal = Integer.parseInt(txt_GraphSelection_TValue.getText());
		    		
		    		if(tempImpVal>=0)
		    		{
		    			if(tempImpVal>payoff_R)
		    				payoff_T = tempImpVal;		    			
		    		}   		
		    		
		    	}
		    	catch(Exception e)
		    	{
		    	}  
		    }
		    // update value if necessary (exception, bad value, starts with 0 error (001))
		    txt_GraphSelection_TValue.setText(Integer.toString(payoff_T));
		});		
		
		
		// Payoff R
		txt_GraphSelection_RValue.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e){
				if(e.getCode() == KeyCode.ENTER)
				{
					pane_Selection_Graph.requestFocus();
				}
			}
			});
		
		txt_GraphSelection_RValue.focusedProperty().addListener((observable, oldValue, newValue) -> {
		    if(newValue==false)
		    {
		    	// check input
		    	try{
		    		int tempImpVal = Integer.parseInt(txt_GraphSelection_RValue.getText());
		    		
		    		if(tempImpVal>=0)
		    		{
		    			if(tempImpVal>payoff_P && tempImpVal<payoff_T)
		    				payoff_R = tempImpVal;			    			
		    		}
		    	}
		    	catch(Exception e)
		    	{
		    	}  
		    }
		    // update value if necessary (exception, bad value, starts with 0 error (001))
		    txt_GraphSelection_RValue.setText(Integer.toString(payoff_R));
		});	
		
		
		// Payoff P
		txt_GraphSelection_PValue.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e){
				if(e.getCode() == KeyCode.ENTER)
				{
					pane_Selection_Graph.requestFocus();
				}
			}
			});
		
		txt_GraphSelection_PValue.focusedProperty().addListener((observable, oldValue, newValue) -> {
		    if(newValue==false)
		    {
		    	// check input
		    	try{
		    		int tempImpVal = Integer.parseInt(txt_GraphSelection_PValue.getText());
		    		
		    		if(tempImpVal>=0)
		    		{
		    			if(tempImpVal>payoff_S && tempImpVal<payoff_R)
		    				payoff_P = tempImpVal;	
		    		}		    		
		    	}
		    	catch(Exception e)
		    	{		    		
		    	}  
		    }
		    // update value if necessary (exception, bad value, starts with 0 error (001))
		    txt_GraphSelection_PValue.setText(Integer.toString(payoff_P));
		});	
		
		
		// Payoff S
		txt_GraphSelection_SValue.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e){
				if(e.getCode() == KeyCode.ENTER)
				{
					pane_Selection_Graph.requestFocus();
				}
			}
			});
		
		txt_GraphSelection_SValue.focusedProperty().addListener((observable, oldValue, newValue) -> {
		    if(newValue==false)
		    {
		    	// check input
		    	try{
		    		int tempImpVal = Integer.parseInt(txt_GraphSelection_SValue.getText());
		    		
		    		if(tempImpVal>=0 && tempImpVal<payoff_P)
		    		{
		    			payoff_S = tempImpVal;		    			
		    		}
		    	}
		    	catch(Exception e)
		    	{		    		
		    	}  
		    }
		    // update value if necessary (exception, bad value, starts with 0 error (001))
		    txt_GraphSelection_SValue.setText(Integer.toString(payoff_S));
		});
		
		
		/*
		 * 
		 * Node
		 * 
		 */
		
		// node Y field		
		
		txt_NodeSelection_YValue.setOnKeyPressed(new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent e){
			if(e.getCode() == KeyCode.ENTER)
			{
				pane_Selection_Node.requestFocus();
			}
		}
		});
		
		txt_NodeSelection_YValue.focusedProperty().addListener((observable, oldValue, newValue) -> {
		    if(newValue==false)
		    {
		    	// check input
		    	try{
		    		double tempImpVal = Double.parseDouble(txt_NodeSelection_YValue.getText());
		    		// TO DO: check if there is no node at new position
		    		if(tempImpVal>=0)
		    		{
		    			if(select_nodes == null || select_nodes.size() == 1)
		    			{		    				
		    				// Change position
				    		currentNode.setCenterY(tempImpVal);
				    					    		
				    		// increase screen size if needed
							if(currentNode.getCenterY()>pane_MainDisplay.getPrefHeight())
								pane_MainDisplay.setPrefHeight(currentNode.getCenterY() + 200);
				    		
				    		// Update edges
							
							ArrayList<Edge> edges = currentNode.getEdges();
							
				    		for(int i=0;i<edges.size();i++)
				    		{			    			
			    				// if start node
			    				if(edges.get(i).getNodeA() == currentNode)
			    				{
			    					edges.get(i).getNodeB().setMark(0);
			    					edges.get(i).setStartY(tempImpVal);
			    				}
			    				else// if end node
			    				{
			    					edges.get(i).getNodeA().setMark(0);
			    					edges.get(i).setEndY(tempImpVal);
			    				}
				    		}
				    		
				    		currentNode.setMark(0);
				    		updateMainDisplay();		    				
		    			}
		    			else
		    			{
		    				double tempY = select_nodes.get(0).getCenterY();
		    				
		    				for(int i=1; i<select_nodes.size(); i++)			
		    				{
		    					if(tempY > select_nodes.get(i).getCenterY())
		    						tempY=select_nodes.get(i).getCenterY();
		    				}
		    				
		    				tempY = tempImpVal - tempY;		    				
		    				for(int i=0; i<select_nodes.size(); i++)			
		    				{
		    					select_nodes.get(i).setCenterY(select_nodes.get(i).getCenterY()+tempY);
		    					select_nodes.get(i).setMark(0);
		    					
		    					// increase screen size if needed
								if(select_nodes.get(i).getCenterY()>pane_MainDisplay.getPrefHeight())
									pane_MainDisplay.setPrefHeight(select_nodes.get(i).getCenterY() + 200);
		    				}
		    				
		    				// Update edges
		    				for(int i=0; i<select_nodes.size(); i++)			
		    				{		    					
		    					ArrayList<Edge> edges = select_nodes.get(i).getEdges();
								
					    		for(int j=0;j<edges.size();j++)
					    		{			    			
				    				// if start node
				    				if(edges.get(j).getNodeA() == select_nodes.get(i))
				    				{
				    					edges.get(j).getNodeB().setMark(0);
				    					edges.get(j).setStartY(select_nodes.get(i).getCenterY());
				    				}
				    				else// if end node
				    				{
				    					edges.get(j).getNodeA().setMark(0);
				    					edges.get(j).setEndY(select_nodes.get(i).getCenterY());
				    				}
					    		}		    					
		    				}
		    				
				    		updateMainDisplay();
		    			}
			    		 
			    		
		    		}
		    	}
		    	catch(Exception e)
		    	{
		    	}  
		    }
		    // update value if necessary (exception, bad value, starts with 0 error (001))		   	
		   	if(select_nodes == null || select_nodes.size() == 1)			
		    	txt_NodeSelection_YValue.setText(Double.toString(currentNode.getCenterY()));
		    else
		    {
		    	
		    	double tempY = select_nodes.get(0).getCenterY();
				
				for(int i=1; i<select_nodes.size(); i++)			
				{
					if(tempY > select_nodes.get(i).getCenterY())
						tempY=select_nodes.get(i).getCenterY();
				}
		    	
				txt_NodeSelection_YValue.setText(Double.toString(tempY));
		    }
		});
		
		// node X field
		
		txt_NodeSelection_XValue.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e){
				if(e.getCode() == KeyCode.ENTER)
				{
					pane_Selection_Node.requestFocus();
				}
			}
			});
		
		txt_NodeSelection_XValue.focusedProperty().addListener((observable, oldValue, newValue) -> {
		    if(newValue==false)
		    {
		    	// check input
		    	try{
		    		double tempImpVal = Double.parseDouble(txt_NodeSelection_XValue.getText());
		    		// TO DO: check if there is no node at new position
		    		if(tempImpVal>=0)
		    		{
		    			if(select_nodes == null || select_nodes.size() == 1)
		    			{		    				
		    				// Change position
				    		currentNode.setCenterX(tempImpVal);
				    		
				    		// increase screen size if needed
				    		if(currentNode.getCenterX()>pane_MainDisplay.getPrefWidth())
								pane_MainDisplay.setPrefWidth(currentNode.getCenterX() + 200);
				    		
				    		// Update edges
				    
							ArrayList<Edge> edges = currentNode.getEdges();
							
				    		for(int i=0;i<edges.size();i++)
				    		{			    			
			    				// if start node
			    				if(edges.get(i).getNodeA() == currentNode)
			    				{
			    					edges.get(i).getNodeB().setMark(0);
			    					edges.get(i).setStartX(tempImpVal);
			    				}
			    				else// if end node
			    				{
			    					edges.get(i).getNodeA().setMark(0);
			    					edges.get(i).setEndX(tempImpVal);
			    				}
				    		}
				    		
				    		currentNode.setMark(0);
				    		updateMainDisplay();		    				
		    			}
		    			else
		    			{
		    				double tempX = select_nodes.get(0).getCenterX();
		    				
		    				for(int i=1; i<select_nodes.size(); i++)			
		    				{
		    					if(tempX > select_nodes.get(i).getCenterX())
		    						tempX=select_nodes.get(i).getCenterX();
		    				}
		    				
		    				tempX = tempImpVal - tempX;		    				
		    				for(int i=0; i<select_nodes.size(); i++)			
		    				{
		    					select_nodes.get(i).setCenterX(select_nodes.get(i).getCenterX()+tempX);
		    					select_nodes.get(i).setMark(0);
		    					
		    					// increase screen size if needed
					    		if(select_nodes.get(i).getCenterX()>pane_MainDisplay.getPrefWidth())
									pane_MainDisplay.setPrefWidth(select_nodes.get(i).getCenterX() + 200);
		    				}		    				
		    				
		    				// Update edges
		    				for(int i=0; i<select_nodes.size(); i++)			
		    				{		    					
		    					ArrayList<Edge> edges = select_nodes.get(i).getEdges();
								
					    		for(int j=0;j<edges.size();j++)
					    		{			    			
				    				// if start node
				    				if(edges.get(j).getNodeA() == select_nodes.get(i))
				    				{
				    					edges.get(j).getNodeB().setMark(0);
				    					edges.get(j).setStartX(select_nodes.get(i).getCenterX());
				    				}
				    				else// if end node
				    				{
				    					edges.get(j).getNodeA().setMark(0);
				    					edges.get(j).setEndX(select_nodes.get(i).getCenterX());
				    				}
					    		}		    					
		    				}
		    				
				    		updateMainDisplay();
		    			}
			    		
			    		
		    		}
		    	}
		    	catch(Exception e)
		    	{
		    	}  
		    }
		    // update value if necessary (exception, bad value, starts with 0 error (001))
		    if(select_nodes == null || select_nodes.size() == 1)			
		    	txt_NodeSelection_XValue.setText(Double.toString(currentNode.getCenterX()));
		    else
		    {
		    	
		    	double tempX = select_nodes.get(0).getCenterX();
				
				for(int i=1; i<select_nodes.size(); i++)			
				{
					if(tempX > select_nodes.get(i).getCenterX())
						tempX=select_nodes.get(i).getCenterX();
				}
		    	
				txt_NodeSelection_XValue.setText(Double.toString(tempX));
		    }
		});
	}

	public void updateMainDisplay()
	{
		
		
		// the view start and end range
		double viewXStart = (pane_MainDisplay.getBoundsInLocal().getMaxX()-scrlpane_MainDisplay.getPrefWidth())*scrlpane_MainDisplay.getHvalue();
		viewXStart=viewXStart-50;
		double viewXEnd = viewXStart+scrlpane_MainDisplay.getPrefWidth();
		viewXEnd = viewXEnd +200;
		
		double viewYStart = (pane_MainDisplay.getBoundsInLocal().getMaxY()-scrlpane_MainDisplay.getPrefHeight())*scrlpane_MainDisplay.getVvalue();
		viewYStart=viewYStart-50;
		double viewYEnd = viewYStart+scrlpane_MainDisplay.getPrefHeight();
		viewYEnd = viewYEnd +200;
		
		
		//mark the nodes in the new range
		for(int i=0;i<nodes.size();i++)
		{
			double currentNodeXPossition = nodes.get(i).getCenterX();
			double currentNodeYPossition = nodes.get(i).getCenterY();
			
			//if in the view range
			if( (currentNodeXPossition> viewXStart && currentNodeXPossition<viewXEnd)&& 
					(currentNodeYPossition> viewYStart && currentNodeYPossition<viewYEnd))
					{
						if(nodes.get(i).getMark()== 0)
							nodes.get(i).setMark(1);
					}
			else
				nodes.get(i).setMark(0);
		}
		
		
		
		for(int i=0;i<nodes.size();i++)
		{
			if(nodes.get(i).getMark()== 1)
			{
				//create the nodes in the new range					
				showNode(nodes.get(i));
			}
			else if(nodes.get(i).getMark()== 0)
			{
				// and remove the ones not in the range	
					
				pane_MainDisplay.getChildren().remove(nodes.get(i).getVisualObject());
				nodes.get(i).setVisualObject(null);
				
			}
		}		
		
		
		for(int i=0;i<nodes.size();i++)
		{
			if(nodes.get(i).getMark()== 1) // create all edges to a new marked node
			{
				for(int j=0;j<nodes.get(i).getEdges().size();j++)
				{
					showEdge(nodes.get(i).getEdges().get(j));
				}
			}
			else if(nodes.get(i).getMark()== 0)//remove all edges from unmarked nodes to other unmarked nodes
			{
				for(int j=0;j<nodes.get(i).getEdges().size();j++)
				{
					if(nodes.get(i).getEdges().get(j).getNodeA().getMark() == 0 && nodes.get(i).getEdges().get(j).getNodeB().getMark()== 0)
						{	
							pane_MainDisplay.getChildren().remove(nodes.get(i).getEdges().get(j).getVisualObject());
							nodes.get(i).getEdges().get(j).setVisualObject(null);
						}
				}
			}
		}
		
		//update marks
		for(int i=0;i<nodes.size();i++)
		{
			if(nodes.get(i).getMark()== 1) 
			{
				nodes.get(i).setMark(2);
			}
			else if(nodes.get(i).getMark()== 0)
			{
				nodes.get(i).setMark(0);
			}
		}
		
		
		
		
	
	}
	
	public void addMainDisplayScrollListeners()
	{
		//add listeners for change in the scrollpane (so we can only show the nodes and edges that are visible)
		
		scrlpane_MainDisplay.vvalueProperty().addListener((observable, oldValue, newValue) -> {			
			//update Main Display
			updateMainDisplay();
	            	 
	        });
		
		
		scrlpane_MainDisplay.hvalueProperty().addListener((observable, oldValue, newValue) -> {						
			//update Main Display
			updateMainDisplay();            
		});
	}
	
	
	public void addMainDisplaySizeListeners(double scene_height,double scene_widht)
	{		
		
		// set the initial height and width of the main display to be equal to the screen sizes
		pane_MainDisplay.setPrefHeight(scene_height);
		pane_MainDisplay.setPrefWidth(scene_widht);
		
		
		// look at all the nodes and see if one of them is outside the boundaries		
		for(int i=0; i<numberOfNodes; i++)
	    {
			if(nodes.get(i).getCenterX()>pane_MainDisplay.getPrefWidth()-200)
				pane_MainDisplay.setPrefWidth(nodes.get(i).getCenterX() + 200);
			
			if(nodes.get(i).getCenterY()>pane_MainDisplay.getPrefHeight()-200)
				pane_MainDisplay.setPrefHeight(nodes.get(i).getCenterY() + 200);
	    }
		
		
		scrlpane_MainDisplay.heightProperty().addListener((observable, oldValue, newValue) -> {
		    if(newValue.doubleValue()>pane_MainDisplay.getPrefHeight())
		    {
		    	pane_MainDisplay.setPrefHeight(newValue.doubleValue());		    	  
		    }
		    
		    scrlpane_MainDisplay.setPrefHeight(newValue.doubleValue());	
		    
		    updateMainDisplay(); 
		    
		});
		
		scrlpane_MainDisplay.widthProperty().addListener((observable, oldValue, newValue) -> {
		    if(newValue.doubleValue()>pane_MainDisplay.getPrefWidth())
		    {
		    	pane_MainDisplay.setPrefWidth(newValue.doubleValue());		    	  
		    }
		    
		    scrlpane_MainDisplay.setPrefWidth(newValue.doubleValue());
		    
		    updateMainDisplay(); 
		    
		});
		
	}
		
	
	public void setNumberOfDefectors()
	{
		try
		{
			int tempImpVal = Integer.parseInt(txt_GraphSelection_SetNumberOfDefectors.getText());
			
			if(tempImpVal<0 || tempImpVal> numberOfNodes)
			{
				lbl_GraphSelection_SetNumberOfDefectorsError.setText("input needs to be >0 and <" + numberOfNodes);
				
			}
			else // all good			
			{
				// update nodes				
			
				// initialize nodes to all cooperating
				for(int i = 0; i<numberOfNodes; i++)
				{
					nodes.get(i).setCurrentStrategy(1);
					
					//used to update the visuals
					nodes.get(i).setMark(0);
    			
				}				
			
				for(int i=0; i<tempImpVal;i++)
				{
					// node to defect
					int defectorNumber = (int) (Math.random()*(numberOfNodes-i));
					
					int counter = 0;
					
					for(int j = 0; j<numberOfNodes; j++)
					{
						if(nodes.get(j).getCurrentStrategy() == 1)
						{
							if(counter == defectorNumber)
							{
								nodes.get(j).setCurrentStrategy(0);
								
								break;
							}
							else
								counter++;							
						}
					}					
				}	
				
				// reset error message
				lbl_GraphSelection_SetNumberOfDefectorsError.setText("");
				
				updateMainDisplay();
				
			}			
			
			
			
		}	
		catch(Exception e)
		{
			lbl_GraphSelection_SetNumberOfDefectorsError.setText("input needs to be an integer");
		} 
	}
	
	
}



