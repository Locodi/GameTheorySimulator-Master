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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AdvanceSimulator_FullSimulationControler {
	
	@FXML
	private TextField txt_NumberOfRepeats;
	
	@FXML
	private TextField txt_NumberOfMaxTurns;
	
	@FXML
	private TextField txt_StepSize;
	
	@FXML
	private TextField txt_RangeStart;
	
	@FXML
	private TextField txt_RangeEnd;
	
	
	
	@FXML
	private Label lbl_NumberOfRepeats;
	
	@FXML
	private Label lbl_NumberOfMaxTurns;
	
	@FXML
	private Label lbl_StepSize;
	
	@FXML
	private Label lbl_Range;
	
	@FXML
	private Label lbl_StartError;
	
	@FXML
	private Label lbl_NumberOfNodes;
	
	private int numberOfRepeats, numberOfMaxTurns, stepSize, rangeStart, rangeEnd;

	private int payoff_T,payoff_R,payoff_P,payoff_S;
	private int numberOfNodes;	
	private ArrayList<Vertex> nodes = new ArrayList<Vertex> ();	
	
	private int interaction_model,update_mechanism;
	
	private String graphName;
	
	@FXML
    public void initialize() throws IOException {
		
		numberOfNodes=0;
		
		// open current graph
				initObjects();
		
		// initialize input
		numberOfRepeats=100;//1000 
		numberOfMaxTurns=40000;//1000 
		stepSize=2073;//1
		rangeStart=0;//0
		rangeEnd=numberOfNodes-1;//numberOfNodes-1
		
		
		
		txt_NumberOfRepeats.setText(Integer.toString(numberOfRepeats));
		txt_NumberOfMaxTurns.setText(Integer.toString(numberOfMaxTurns));		
		txt_StepSize.setText(Integer.toString(stepSize));
		txt_RangeStart.setText(Integer.toString(rangeStart));		
		txt_RangeEnd.setText(Integer.toString(rangeEnd+1));		
		lbl_NumberOfNodes.setText(lbl_NumberOfNodes.getText() + Integer.toString(numberOfNodes));
		
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
				graphName = temp_files[0].getName();
			    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			    String line;
			    
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
				    	// File empty, throw bad file format error
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
				    	placeNode(0, 0);
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
				    	// File empty, throw bad file format error
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
				    	placeNode(0, 0);
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
		
		return 1; // all good
	}
	
	public void placeNode(double ev_x, double ev_y)
	{		
		numberOfNodes++;		
		
		if(interaction_model == 1 && update_mechanism == 1)
		{
			Vertex c = new Vertex_1_1();				
			
			// add the node to the list
			nodes.add(c);
		}
		else if(interaction_model == 1 && update_mechanism == 2)
		{
			Vertex c = new Vertex_1_2();				
			
			// add the node to the list
			nodes.add(c);
		}
		else
			System.out.println("Undefined behaviour for current interaction_model and update_mechanism");
		
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
	
	public int checkForConnectedGraph()
	{
		
		Queue<Vertex> verticesQueue= new LinkedList<Vertex>();
		
		int[] seen = new int[numberOfNodes];
		
		for(int i = 1; i<numberOfNodes; i++)
		{
			seen[i] = 0;
		}
		seen[0] = 1;
		verticesQueue.add(nodes.get(0));
		
		int counter = 1;
		while(counter < numberOfNodes)
		{
			Vertex currentVertex = verticesQueue.poll();
			if(currentVertex != null)
			{
				for(int i = 0; i<currentVertex.getDegree(); i++)
				{
					if(seen[currentVertex.getNeighbor(i).getVertexNumber()] == 0)
					{
						verticesQueue.add(currentVertex.getNeighbor(i));
						counter++;
						seen[currentVertex.getNeighbor(i).getVertexNumber()]=1;
					}
				
				}
			}
			else
				return 0; // Graph is not connected		
		}		
		// Graph is connected
		return 1;
	}
	
	public void start(ActionEvent event)throws IOException
	{
		
		final long startTime = System.currentTimeMillis();
		
		// check input
		
		int all_good = 0;
		
		// NumberOfRepeats
		
		lbl_NumberOfRepeats.setText("");
    	try{
    		int tempImpVal = Integer.parseInt(txt_NumberOfRepeats.getText());
    		
    		if(tempImpVal>0)
    		{
    			numberOfRepeats = tempImpVal;	    			
    		}   		
    		else
    			{
    				lbl_NumberOfRepeats.setText("Input needs to be > 0");
    				all_good = 1;
    			}
    	}
    	catch(Exception e)
    	{
    		lbl_NumberOfRepeats.setText("Input needs to be an integer");
    		all_good = 1;
    	}  
		
    	// NumberOfMaxTurns
		
		lbl_NumberOfMaxTurns.setText("");
    	try{
    		int tempImpVal = Integer.parseInt(txt_NumberOfMaxTurns.getText());
    		
    		if(tempImpVal>0)
    		{
    			numberOfMaxTurns = tempImpVal;	    			
    		}   		
    		else
    			{
    				lbl_NumberOfMaxTurns.setText("Input needs to be > 0");
    				all_good = 1;
    			}
    	}
    	catch(Exception e)
    	{
    		lbl_NumberOfMaxTurns.setText("Input needs to be an integer");
    		all_good = 1;
    	} 
		    	
    	// StepSize
		
		lbl_StepSize.setText("");
    	try{
    		int tempImpVal = Integer.parseInt(txt_StepSize.getText());
    		
    		if(tempImpVal>0 && tempImpVal<numberOfNodes)
    		{
    			stepSize = tempImpVal;	    			
    		}   		
    		else
    			{
    				lbl_Range.setText("Input needs to be > 0 and <= " + Integer.toString(numberOfNodes));  
    				all_good = 1;
    			}
    	}
    	catch(Exception e)
    	{
    		lbl_StepSize.setText("Input needs to be an integer");
    		all_good = 1;
    	} 
    	    	
    	// Range
		
		lbl_Range.setText("");
    	try{
    		int tempImpVal1 = Integer.parseInt(txt_RangeStart.getText());
    		int tempImpVal2 = Integer.parseInt(txt_RangeEnd.getText());
    		
    		if(tempImpVal1>=0 && tempImpVal2>0 && tempImpVal1<= numberOfNodes && tempImpVal2<=numberOfNodes)
    		{
    			rangeStart = tempImpVal1;
    			rangeEnd = tempImpVal2;
    			
    			if(rangeStart>rangeEnd)
    			{
        			lbl_Range.setText("Range start needs to be > than range end");
        			all_good = 1;
        		}	
    			
    		}   		
    		else
    			{
    				lbl_Range.setText("Input needs to be > 0 and <= " + Integer.toString(numberOfNodes));    			
    				all_good = 1;
    			}
    	}
    	catch(Exception e)
    	{
    		lbl_Range.setText("Input needs to be an integer");
    		all_good = 1;
    	}     	
    	
    	
    	if(all_good == 0)
    	{       	
        	
        	// Check if graph is connected 
        	
    		int isConnected = checkForConnectedGraph();
    		
    		if(isConnected == 0)
    			lbl_StartError.setText("Graph is not connected");
    		else
    		{     			
    			// variables to hold results
    			
    			/*
    			* Results:
    			* row 0 step values
    			* row 1 average results (avg num. of cooperators)
    			* row i run i-1 results	(num. of cooperators)
    			*/
    			int [][]results = new int[2+numberOfRepeats][(rangeEnd-rangeStart)/stepSize +1];
    			
    			// Calculate row 0
    			
    			for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
    			{
    				results[0][i]=rangeStart+i*stepSize;
    			}    			
    			
    			// run simulations
    			
    			// for each step
    			for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
    			{
    				System.out.println("step: " + i );
    				// for each run
					for(int j = 2;j<2+numberOfRepeats;j++)
					{				
						
						
						// initialize nodes to all cooperating
						for(int i1 = 0; i1<numberOfNodes; i1++)
						{
							nodes.get(i1).setCurrentStrategy(1);
						}
						
						// set initial defectors
						for(int i1 = 0; i1<results[0][i]; i1++)
						{
							// node to defect
							int defectorNumber = (int) (Math.random()*(numberOfNodes-i1));
							
							int counter = 0;
							
							for(int j1 = 0; j1<numberOfNodes; j1++)
							{
								if(nodes.get(j1).getCurrentStrategy() == 1)
								{
									if(counter == defectorNumber)
									{
										nodes.get(j1).setCurrentStrategy(0);
										break;
									}
									else
										counter++;
									
								}
							}
						}
						
												
						if(interaction_model == 1 && update_mechanism == 1)
						{
							int[] payoffs = {payoff_T, payoff_R, payoff_P, payoff_S};
							
							// Calculate current Payoff for each node				
							for(int i1 = 0; i1<numberOfNodes; i1++)
							{							
								nodes.get(i1).calculateNextPayoff(payoffs);
								nodes.get(i1).updateCurrentPayoff();							
							}			
						}
						else if(interaction_model == 1 && update_mechanism == 2)
						{
							int[] payoffs = {payoff_T, payoff_R, payoff_P, payoff_S};
							
							// Calculate current Payoff for each node				
							for(int i1 = 0; i1<numberOfNodes; i1++)
							{							
								nodes.get(i1).calculateNextPayoff(payoffs);
								nodes.get(i1).updateCurrentPayoff();							
							}
						}
						else
							System.out.println("Undefined behaviour for current interaction_model and update_mechanism");
												
						
						
						// play game						
						int game_result = playGame(numberOfMaxTurns,10);
						
						if(game_result == 0)
							System.out.println("Reached max turns");
						else if(game_result == 2)
							System.out.println("no big change over long time");
						
						// record results
						for(int i1 = 0; i1<numberOfNodes; i1++)
						{
							if(nodes.get(i1).getCurrentStrategy()==1)
							{
								results[j][i]++;
							}
						}
						
						// Calculate row 2
						results[1][i] = results[1][i] + results[j][i];						
		    			
					}
					
					
    			}
    			
    			// store results in a csv file
    			
    			storeResults(results);
    			
    			System.out.println("Done");
    			
    		}
    	}
    	
    	final long endTime = System.currentTimeMillis();

    	System.out.println("Total execution time in milliseconds: " + (endTime - startTime));
    	
    	
	}
	
	
	
	public int playGame(int maxNumberOfTurns, int maxNoChange)
	{	
		int changeCounter = 0;
		int largeChangeCooperatorCounter=0;
		
		for(int t = 0; t<maxNumberOfTurns;t++)
		{
			
			// Calculate next defect for each node
			for(int i = 0; i<numberOfNodes; i++)
			{
				nodes.get(i).calculateNextStrategy();
			}
			
			// Update current defect
			int temp_change =0;
			for(int i = 0; i<numberOfNodes; i++)
			{
				
				if(nodes.get(i).getCurrentStrategy()!= nodes.get(i).getNextStrategy())
					temp_change = 1;
				
				nodes.get(i).updateCurrentStrategy();			
			}
			
			if(temp_change == 0)
				changeCounter++;// no change
			else
				changeCounter = 0;
			
			if(changeCounter == maxNoChange)
			{				
				return 1; // No more change in the graph
			}
			
			if(interaction_model == 1 && update_mechanism == 1)
			{
				int[] payoffs = {payoff_T, payoff_R, payoff_P, payoff_S};
				
				// Calculate current Payoff for each node				
				for(int i = 0; i<numberOfNodes; i++)
				{							
					nodes.get(i).calculateNextPayoff(payoffs);
					nodes.get(i).updateCurrentPayoff();							
				}			
			}
			else if(interaction_model == 1 && update_mechanism == 2)
			{
				int[] payoffs = {payoff_T, payoff_R, payoff_P, payoff_S};
				
				// Calculate current Payoff for each node				
				for(int i = 0; i<numberOfNodes; i++)
				{							
					nodes.get(i).calculateNextPayoff(payoffs);
					nodes.get(i).updateCurrentPayoff();							
				}
			}
			else
				System.out.println("Undefined behaviour for current interaction_model and update_mechanism");
			
			
			if(t%100==0)
			{
				int currentNumberOfCooperators=0;
				for(int i = 0; i<numberOfNodes; i++)
				{						
					if(nodes.get(i).getCurrentStrategy()!= 0)
						currentNumberOfCooperators ++;
				}
				if(t<=100)
					largeChangeCooperatorCounter=currentNumberOfCooperators;
				else
					{
						if(currentNumberOfCooperators-10 <= largeChangeCooperatorCounter && largeChangeCooperatorCounter<=currentNumberOfCooperators+10)
							{									
								return 2; // no big change over long time									
							}
						
						largeChangeCooperatorCounter=currentNumberOfCooperators;
					}
				
			}
		}
		return 0; //Reached maxTurns
	}
	
	

	public void storeResults(int [][]results)
	{	
		try {
			//PrintWriter pw = new PrintWriter(new File("./Graphs/" + file_name));
			PrintWriter pw = new PrintWriter(new File("test.csv"));
			StringBuilder sb = new StringBuilder();
			
			// graph name
			sb.append(graphName);
			sb.append(',');
			sb.append('\n');
			
			// payoffs
			sb.append(payoff_T);
			sb.append(',');	
			sb.append(payoff_R);
			sb.append(',');
			sb.append(payoff_P);
			sb.append(',');
			sb.append(payoff_S);
			sb.append(',');
			sb.append('\n');
			
			
			// Simulation results
			// for each run
			for(int j = 0;j<2+numberOfRepeats;j++)
			{
				if(j==1)
				{
					// for each step
					for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
					{
						sb.append(((double)results[j][i])/numberOfRepeats);
						sb.append(',');
					}
				}
				else
				{
					// for each step
					for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
					{
						sb.append(results[j][i]);
						sb.append(',');
					}
				}
				sb.append('\n');
			}
			
			
			
			pw.write(sb.toString());
			pw.close();			
				
			
		}
		catch(FileNotFoundException ex){
			// TO DO: handle exception
		}
		
		
	}
	
	
	
	public void goBack(ActionEvent event)throws IOException
	{	
		if(interaction_model == 1 && update_mechanism == 1)
		{
			// reset vertex count
			Vertex c = new Vertex_1_1();
			c.setCount(0);			
		}
		else if(interaction_model == 1 && update_mechanism == 2)
		{
			// reset vertex count
			Vertex c = new Vertex_1_2();
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

}
