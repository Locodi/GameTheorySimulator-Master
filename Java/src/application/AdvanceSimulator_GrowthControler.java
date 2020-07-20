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

public class AdvanceSimulator_GrowthControler {
	
	@FXML
	private TextField txt_Growth;
	
	@FXML
	private Label lbl_NumberOfNodes;
	
	@FXML
	private Label lbl_StartError;
	
	private int payoff_T,payoff_R,payoff_P,payoff_S;
	private int numberOfNodes;	
	private ArrayList<Vertex> nodes = new ArrayList<Vertex> ();
	
	private int growth;
	
	private int numberOfRepeats, numberOfMaxTurns, stepSize, rangeStart, rangeEnd;
	
	private int interaction_model,update_mechanism;
	

	@FXML
    public void initialize() throws IOException {
		
		// initialize input
		numberOfRepeats=1000; 
		numberOfMaxTurns=1000; 
		stepSize=1;
		rangeStart=0;
		numberOfNodes=0;
		
		growth = 3;
		
		
		txt_Growth.setText(Integer.toString(growth));
		
		// open current graph
		initObjects();
		
		rangeEnd=numberOfNodes-1;
		
		
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
					    				nodes.get(i).setCurrentDefect(true);
					    				nodes.get(i).setFill(Color.rgb(200, 0, 0));
					    			}
					    		else
					    			{
					    				nodes.get(i).setCurrentDefect(false);
					    				nodes.get(i).setFill(Color.rgb(0, 200, 0));
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
				    
				    
				    br.close();
				    fis.close();
					
					// get data
					
					
					
					
					// edges
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
			
			
			Vertex c = new Vertex ();
			
			// add the node to the list
			nodes.add(c);		
			
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
					  
					 
					  temp_edge.setStrokeWidth(3);
					  
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
			
			
			// check input
			
			int all_good = 0;
			
			// NumberOfRepeats
			
			
	    	try{
	    		int tempImpVal = Integer.parseInt(txt_Growth.getText());
	    		
	    		if(tempImpVal>0)
	    		{
	    			growth = tempImpVal;	    			
	    		}   		
	    		
	    	}
	    	catch(Exception e)
	    	{
	    		
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
	    			//TO DO: add degree deviation
	    			
	    			// Grow - add minor nodes
	    			{
	    				
    				/*
	    			* results2:
	    			* row 0 step values
	    			* row 1 average results (avg num. of cooperators) for growth 1
	    			* row i average results (avg num. of cooperators) for growth i
	    			* column n-2 density 
	    			* column n-1 diameter
	    			* column n avgDegree
	    			*/	    				
					double [][]results2 = new double[1+growth][(rangeEnd-rangeStart)/stepSize +4 + growth];
					
					for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1 + growth;i++)
	    			{
	    				
	    				results2[0][i]=rangeStart+i*stepSize;
	    			}
					
					results2[0][(rangeEnd-rangeStart)/stepSize +1 + growth]= -1;
					results2[0][(rangeEnd-rangeStart)/stepSize +2 + growth]= -1;
					results2[0][(rangeEnd-rangeStart)/stepSize +3 + growth]= -1;
					
					for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1 + growth;i++)
	    			{
						for(int j = 1;j<1+growth;j++)
		    			{
							results2[j][i]= -1;
		    			}
	    			}
					
	    			for(int t = 0; t< growth;t++)	    			
	    			{	
	    				System.out.println("step: " + t );
	    				
	    				// Reset graph
	    				
	    				//remove all edges and nodes
	    				nodes.get(0).setCount(0);
	    				
	    				for(int i = numberOfNodes-1; i>= 0;i--)	    			
		    			{	
	    					nodes.remove(i);
		    			}
	    				numberOfNodes=0;
	    				
	    				//re-add them from file
	    				initObjects();
	    				
	    				// Grow graph
	    					
	    				//grow t times
	    				for(int i = 0; i<= t;i++)	    			
		    			{
	    					int finishedGrowth =  0;
	    					
	    					while(finishedGrowth == 0)
	    					{
	    						int tempCounter = 0;
	    						//find number of large nodes
	    						for(int j = 0; j< numberOfNodes;j++)	    			
	    		    			{
	    							if(nodes.get(j).getDegree() > 2)
	    								tempCounter++;
	    		    			}
	    						//pick one at random
	    						int type = (int)(Math.random()*(tempCounter));
	    						
	    						//add a minor node to it
	    						tempCounter = 0;
	    						for(int j = 0; j< numberOfNodes;j++)	    			
	    		    			{
	    							if(nodes.get(j).getDegree() > 2)
	    								tempCounter++;
	    							
	    							if(tempCounter-1 == type)
	    							{	    								
	    								placeNode(0, 0);
	    								placeEdge(nodes.get(numberOfNodes-1), nodes.get(j));
	    								finishedGrowth=1;
	    								break;
	    							}
	    								
	    		    			}
	    					}
	    					
		    			}
	    				System.out.println("Finished growth");
	    				//Test robustness
	    				
	    				/*
		    			* results:
		    			* row 0 step values
		    			* row 1 average results (avg num. of cooperators)
		    			* row i run i-1 results	(num. of cooperators)
		    			*/	    				   				
						double [][]results = new double[2+numberOfRepeats][(rangeEnd-rangeStart)/stepSize +1 + t];
						
						
						
		    			
		    			// Calculate row 0
		    			
		    			for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1 + t;i++)
		    			{
		    				results[0][i]=rangeStart+i*stepSize;
		    			}    			
		    			
		    			// run simulations
		    			calculateRobustness(results);
		    			
		    			for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1 + t;i++)
		    			{
		    				results2[t+1][i] = results[1][i];
		    			}
	    				
	    				//Calculate Density, Avg Degree, diameter
	    				
		    			double density = calculateDensity(); 
	    				int diameter = calculateDiameter(); 
	    				double avgDegree = calculateAvgDegree(); 
	    				//Store results
	    				
	    				results2[t+1][(rangeEnd-rangeStart)/stepSize +1 + growth] = density;
	    				results2[t+1][(rangeEnd-rangeStart)/stepSize +1 + growth + 1] = diameter;
	    				results2[t+1][(rangeEnd-rangeStart)/stepSize +1 + growth + 2] = avgDegree;
		    			
		    			
		    			
		    			
		    			// store results in csv file
		    			
		    			
	    				
	    			}
	    			storeResults(results2,"test Grow - add minor nodes.csv");
	    			
	    			System.out.println("Done Grow - add minor nodes");
	    			
	    			}// End Grow - add minor nodes
	    			
	    			// Grow - add any edges(minor)
	    			{

	    				
	    				/*
		    			* results2:
		    			* row 0 step values
		    			* row 1 average results (avg num. of cooperators) for growth 1
		    			* row i average results (avg num. of cooperators) for growth i
		    			* column n-2 density 
		    			* column n-1 diameter
		    			* column n avgDegree
		    			*/	    				
						double [][]results2 = new double[1+growth][(rangeEnd-rangeStart)/stepSize +4];
						
						for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
		    			{
		    				
		    				results2[0][i]=rangeStart+i*stepSize;
		    			}
						
						results2[0][(rangeEnd-rangeStart)/stepSize +1]= -1;
						results2[0][(rangeEnd-rangeStart)/stepSize +2]= -1;
						results2[0][(rangeEnd-rangeStart)/stepSize +3]= -1;
						
						for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
		    			{
							for(int j = 1;j<1+growth;j++)
			    			{
								results2[j][i]= -1;
			    			}
		    			}
						
		    			for(int t = 0; t< growth;t++)	    			
		    			{	
		    				System.out.println("step: " + t );
		    				
		    				// Reset graph
		    				
		    				//remove all edges and nodes
		    				nodes.get(0).setCount(0);
		    				
		    				for(int i = numberOfNodes-1; i>= 0;i--)	    			
			    			{	
		    					nodes.remove(i);
			    			}
		    				numberOfNodes=0;
		    				
		    				//re-add them from file
		    				initObjects();
		    				
		    				// Grow graph
		    					
		    				//grow t times
		    				for(int i = 0; i<= t;i++)	    			
			    			{
		    					int finishedGrowth =  0;
		    					while(finishedGrowth == 0)
		    					{
		    					
		    						int tempCounter = 0;
		    						//find number of minor nodes
		    						for(int j = 0; j< numberOfNodes;j++)	    			
		    		    			{
		    							if(nodes.get(j).getDegree() == 1)
		    								tempCounter++;
		    		    			}
		    						//pick one at random
		    						int type = (int)(Math.random()*(tempCounter));
		    						
		    						//find it
		    						
		    						Vertex firstNode = null;
		    						int tempCounter2 = 0;
		    						for(int j = 0; j< numberOfNodes;j++)	    			
		    		    			{
		    							if(nodes.get(j).getDegree() == 1)
		    								tempCounter2++;
		    							
		    							if(tempCounter2-1 == type)
		    							{	    								
		    								firstNode = nodes.get(j);
		    								finishedGrowth=1;
		    								break;
		    							}
		    								
		    		    			}
		    						
		    						//see if we can add more edges to it
		    						if(firstNode.getDegree()<tempCounter)
		    						{
		    							//pick another one at random	    							
			    						 type = (int)(Math.random()*(tempCounter-1));
			    						 
			    						 tempCounter = 0;
			    						for(int j = 0; j< numberOfNodes;j++)	    			
			    		    			{
			    							if(nodes.get(j).getDegree() == 1 && 
			    									nodes.get(j).getVertexNumber()!= firstNode.getVertexNumber())
			    								tempCounter++;
			    							
			    							if(tempCounter-1 == type)
			    							{	    								
			    								
			    								int tempSucccess = placeEdge(firstNode, nodes.get(j));
			    								if(tempSucccess != 0)
			    									{
			    										finishedGrowth=1;			    										
			    									}
			    								break;
			    							}
			    								
			    		    			}
		    							
		    						}
		    						
		    					}
		    					
			    			}
		    				System.out.println("Finished growth");
		    				//Test robustness
		    				
		    				/*
			    			* results:
			    			* row 0 step values
			    			* row 1 average results (avg num. of cooperators)
			    			* row i run i-1 results	(num. of cooperators)
			    			*/	    				   				
							double [][]results = new double[2+numberOfRepeats][(rangeEnd-rangeStart)/stepSize +1];
							
							
							
			    			
			    			// Calculate row 0
			    			
			    			for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
			    			{
			    				results[0][i]=rangeStart+i*stepSize;
			    			}    			
			    			
			    			// run simulations
			    			calculateRobustness(results);
			    			
			    			for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
			    			{
			    				results2[t+1][i] = results[1][i];
			    			}
		    				
		    				//Calculate Density, Avg Degree, diameter
		    				
			    			double density = calculateDensity(); 
		    				int diameter = calculateDiameter(); 
		    				double avgDegree = calculateAvgDegree(); 
		    				//Store results
		    				
		    				results2[t+1][(rangeEnd-rangeStart)/stepSize +1] = density;
		    				results2[t+1][(rangeEnd-rangeStart)/stepSize +1 + 1] = diameter;
		    				results2[t+1][(rangeEnd-rangeStart)/stepSize +1 + 2] = avgDegree;
			    			
			    			
			    			
			    			
			    			
			    			
			    			
		    				
		    			}
		    			
		    			// store results in csv file
		    			storeResults(results2,"test Grow - add any edges(minor).csv");
		    			
		    			System.out.println("Done Grow - add any edges(minor)");
		    			
		    			
	    			}// Done Grow - add any edges(minor)
	    			
	    			
	    			// Grow - add intra edges(minor)
    				{


	    				
	    				/*
		    			* results2:
		    			* row 0 step values
		    			* row 1 average results (avg num. of cooperators) for growth 1
		    			* row i average results (avg num. of cooperators) for growth i
		    			* column n-2 density 
		    			* column n-1 diameter
		    			* column n avgDegree
		    			*/	    				
						double [][]results2 = new double[1+growth][(rangeEnd-rangeStart)/stepSize +4];
						
						for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
		    			{
		    				
		    				results2[0][i]=rangeStart+i*stepSize;
		    			}
						
						results2[0][(rangeEnd-rangeStart)/stepSize +1]= -1;
						results2[0][(rangeEnd-rangeStart)/stepSize +2]= -1;
						results2[0][(rangeEnd-rangeStart)/stepSize +3]= -1;
						
						for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
		    			{
							for(int j = 1;j<1+growth;j++)
			    			{
								results2[j][i]= -1;
			    			}
		    			}
						
		    			for(int t = 0; t< growth;t++)	    			
		    			{	
		    				System.out.println("step: " + t );
		    				
		    				// Reset graph
		    				
		    				//remove all edges and nodes
		    				nodes.get(0).setCount(0);
		    				
		    				for(int i = numberOfNodes-1; i>= 0;i--)	    			
			    			{	
		    					nodes.remove(i);
			    			}
		    				numberOfNodes=0;
		    				
		    				//re-add them from file
		    				initObjects();
		    				
		    				// Grow graph
		    					
		    				//grow t times
		    				for(int i = 0; i<= t;i++)	    			
			    			{
		    					int finishedGrowth =  0;
		    					while(finishedGrowth == 0)
		    					{
		    					
		    						int tempCounter = 0;
		    						//find number of minor nodes
		    						for(int j = 0; j< numberOfNodes;j++)	    			
		    		    			{
		    							if(nodes.get(j).getDegree() == 1)
		    								tempCounter++;
		    		    			}
		    						//pick one at random
		    						int type = (int)(Math.random()*(tempCounter));
		    						
		    						//find it
		    						
		    						Vertex firstNode = null;
		    						int tempCounter2 = 0;
		    						for(int j = 0; j< numberOfNodes;j++)	    			
		    		    			{
		    							if(nodes.get(j).getDegree() == 1)
		    								tempCounter2++;
		    							
		    							if(tempCounter2-1 == type)
		    							{	    								
		    								firstNode = nodes.get(j);
		    								finishedGrowth=1;
		    								break;
		    							}
		    								
		    		    			}
		    						
		    						//find the large node the first node is connected to
		    						Vertex firstNodeLarge = null;
		    						for(int j = 0; j< firstNode.getDegree();j++)	    			
		    		    			{
		    							if(firstNode.getNeighbor(j).getDegree() > 2)
		    								firstNodeLarge = firstNode.getNeighbor(j);
		    		    			}
		    						
		    						// find number of minor nodes connected to firstNodeLarge
		    						tempCounter2 = 0;
		    						for(int j = 0; j< firstNodeLarge.getDegree();j++)	    			
		    		    			{
		    							if(firstNodeLarge.getNeighbor(j).getDegree() == 2)
		    								tempCounter2++;
		    		    			}
		    						
		    						//see if we can add more edges to it
		    						if(firstNode.getDegree()<tempCounter2)
		    						{
		    							//pick another one at random	    							
			    						 type = (int)(Math.random()*(tempCounter2 - firstNode.getDegree()));
			    						 
			    						 tempCounter = 0;
			    						for(int j = 0; j< numberOfNodes;j++)	    			
			    		    			{
			    							if(nodes.get(j).getDegree() == 1 && 
			    									nodes.get(j).getVertexNumber()!= firstNode.getVertexNumber() &&
			    									nodes.get(j).isNeighbor(firstNodeLarge) != -1)
			    								tempCounter++;
			    							
			    							if(tempCounter-1 == type)
			    							{	    								
			    								
			    								int tempSucccess = placeEdge(firstNode, nodes.get(j));
			    								if(tempSucccess != 0)
			    									{
			    										finishedGrowth=1;			    										
			    									}
			    								break;
			    							}
			    								
			    		    			}
		    							
		    						}
		    						
		    					}
		    					
			    			}
		    				System.out.println("Finished growth");
		    				//Test robustness
		    				
		    				/*
			    			* results:
			    			* row 0 step values
			    			* row 1 average results (avg num. of cooperators)
			    			* row i run i-1 results	(num. of cooperators)
			    			*/	    				   				
							double [][]results = new double[2+numberOfRepeats][(rangeEnd-rangeStart)/stepSize +1];
							
							
							
			    			
			    			// Calculate row 0
			    			
			    			for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
			    			{
			    				results[0][i]=rangeStart+i*stepSize;
			    			}    			
			    			
			    			// run simulations
			    			calculateRobustness(results);
			    			
			    			for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
			    			{
			    				results2[t+1][i] = results[1][i];
			    			}
		    				
		    				//Calculate Density, Avg Degree, diameter
		    				
			    			double density = calculateDensity(); 
		    				int diameter = calculateDiameter(); 
		    				double avgDegree = calculateAvgDegree(); 
		    				//Store results
		    				
		    				results2[t+1][(rangeEnd-rangeStart)/stepSize +1] = density;
		    				results2[t+1][(rangeEnd-rangeStart)/stepSize +1 + 1] = diameter;
		    				results2[t+1][(rangeEnd-rangeStart)/stepSize +1 + 2] = avgDegree;
			    			
			    			
			    			
			    			
			    			
			    			
			    			
		    				
		    			}
		    			
		    			// store results in csv file
		    			storeResults(results2,"test Grow - add intra edges(minor).csv");
		    			
		    			System.out.println("Done Grow - add intra edges(minor)");
		    			
		    			
	    			
    				}//End Grow - add intra edges(minor)
	    			
	    			// Grow - add inter edges(minor)
					{



	    				
	    				/*
		    			* results2:
		    			* row 0 step values
		    			* row 1 average results (avg num. of cooperators) for growth 1
		    			* row i average results (avg num. of cooperators) for growth i
		    			* column n-2 density 
		    			* column n-1 diameter
		    			* column n avgDegree
		    			*/	    				
						double [][]results2 = new double[1+growth][(rangeEnd-rangeStart)/stepSize +4];
						
						for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
		    			{
		    				
		    				results2[0][i]=rangeStart+i*stepSize;
		    			}
						
						results2[0][(rangeEnd-rangeStart)/stepSize +1]= -1;
						results2[0][(rangeEnd-rangeStart)/stepSize +2]= -1;
						results2[0][(rangeEnd-rangeStart)/stepSize +3]= -1;
						
						for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
		    			{
							for(int j = 1;j<1+growth;j++)
			    			{
								results2[j][i]= -1;
			    			}
		    			}
						
		    			for(int t = 0; t< growth;t++)	    			
		    			{	
		    				System.out.println("step: " + t );
		    				
		    				// Reset graph
		    				
		    				//remove all edges and nodes
		    				nodes.get(0).setCount(0);
		    				
		    				for(int i = numberOfNodes-1; i>= 0;i--)	    			
			    			{	
		    					nodes.remove(i);
			    			}
		    				numberOfNodes=0;
		    				
		    				//re-add them from file
		    				initObjects();
		    				
		    				// Grow graph
		    					
		    				//grow t times
		    				for(int i = 0; i<= t;i++)	    			
			    			{
		    					int finishedGrowth =  0;
		    					while(finishedGrowth == 0)
		    					{
		    					
		    						int tempCounter = 0;
		    						//find number of minor nodes
		    						for(int j = 0; j< numberOfNodes;j++)	    			
		    		    			{
		    							if(nodes.get(j).getDegree() == 1)
		    								tempCounter++;
		    		    			}
		    						//pick one at random
		    						int type = (int)(Math.random()*(tempCounter));
		    						
		    						//find it
		    						
		    						Vertex firstNode = null;
		    						int tempCounter2 = 0;
		    						for(int j = 0; j< numberOfNodes;j++)	    			
		    		    			{
		    							if(nodes.get(j).getDegree() == 1)
		    								tempCounter2++;
		    							
		    							if(tempCounter2-1 == type)
		    							{	    								
		    								firstNode = nodes.get(j);
		    								finishedGrowth=1;
		    								break;
		    							}
		    								
		    		    			}
		    						
		    						
		    						
		    						//find the large node the first node is connected to
		    						Vertex firstNodeLarge = null;
		    						for(int j = 0; j< firstNode.getDegree();j++)	    			
		    		    			{
		    							if(firstNode.getNeighbor(j).getDegree() > 2)
		    								firstNodeLarge = firstNode.getNeighbor(j);
		    		    			}
		    						
		    						// find number of minor nodes connected to firstNodeLarge
		    						tempCounter2 = 0;
		    						for(int j = 0; j< firstNodeLarge.getDegree();j++)	    			
		    		    			{
		    							if(firstNodeLarge.getNeighbor(j).getDegree() == 2)
		    								tempCounter2++;
		    		    			}
		    						
		    						//see if we can add more edges to it
		    						if(firstNode.getDegree()<tempCounter - tempCounter2)
		    						{
		    							//pick another one at random	    							
			    						 type = (int)(Math.random()*(tempCounter - tempCounter2 - firstNode.getDegree()));
			    						 
			    						 tempCounter = 0;
			    						for(int j = 0; j< numberOfNodes;j++)	    			
			    		    			{
			    							asfasf // degree = 1 is bad as we add more edges
			    							if(nodes.get(j).getDegree() == 1 && 
			    									nodes.get(j).getVertexNumber()!= firstNode.getVertexNumber() &&
			    									nodes.get(j).isNeighbor(firstNodeLarge) == -1)
			    								tempCounter++;
			    							
			    							if(tempCounter-1 == type)
			    							{	    								
			    								
			    								int tempSucccess = placeEdge(firstNode, nodes.get(j));
			    								if(tempSucccess != 0)
			    									{
			    										finishedGrowth=1;			    										
			    									}
			    								break;
			    							}
			    								
			    		    			}
		    							
		    						}
		    						
		    					}
		    					
			    			}
		    				System.out.println("Finished growth");
		    				//Test robustness
		    				
		    				/*
			    			* results:
			    			* row 0 step values
			    			* row 1 average results (avg num. of cooperators)
			    			* row i run i-1 results	(num. of cooperators)
			    			*/	    				   				
							double [][]results = new double[2+numberOfRepeats][(rangeEnd-rangeStart)/stepSize +1];
							
							
							
			    			
			    			// Calculate row 0
			    			
			    			for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
			    			{
			    				results[0][i]=rangeStart+i*stepSize;
			    			}    			
			    			
			    			// run simulations
			    			calculateRobustness(results);
			    			
			    			for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
			    			{
			    				results2[t+1][i] = results[1][i];
			    			}
		    				
		    				//Calculate Density, Avg Degree, diameter
		    				
			    			double density = calculateDensity(); 
		    				int diameter = calculateDiameter(); afasf // increases it for some reason
		    				double avgDegree = calculateAvgDegree(); 
		    				//Store results
		    				
		    				results2[t+1][(rangeEnd-rangeStart)/stepSize +1] = density;
		    				results2[t+1][(rangeEnd-rangeStart)/stepSize +1 + 1] = diameter;
		    				results2[t+1][(rangeEnd-rangeStart)/stepSize +1 + 2] = avgDegree;
			    			
			    			
			    			
			    			
			    			
			    			
			    			
		    				
		    			}
		    			
		    			// store results in csv file
		    			storeResults(results2,"test Grow - add inter edges(minor).csv");
		    			
		    			System.out.println("Done Grow - add inter edges(minor)");
		    			
		    			
	    			
    				
					}	//End Grow - add inter edges(minor)    			
	    			
	    			
	    		} // end start
	    	}
	    	
	    	
	    	
	    	
		}
		
		
		
		public int playGame(int maxNumberOfTurns, int maxNoChange)
		{	
			int changeCounter = 0;
			for(int t = 0; t<maxNumberOfTurns;t++)
			{
				
				// Calculate next defect for each node
				for(int i = 0; i<numberOfNodes; i++)
				{
					// set best score to current node score
					int best_score = nodes.get(i).getCurrentPayoff();
					
					int number_of_cooperators = 0;
					int number_of_defectors = 0;
					
					if(nodes.get(i).getCurrentDefect() == false)
						number_of_cooperators++;
					else
						number_of_defectors++;
					
					ArrayList<Vertex> neighbours =	nodes.get(i).getNeighbors();
					
					for(int j = 0; j<neighbours.size(); j++)
					{
						Vertex current_neighbour = neighbours.get(j);
						
						if(current_neighbour.getCurrentPayoff()==best_score)
						{
							if(current_neighbour.getCurrentDefect() == false)
								number_of_cooperators++;
							else
								number_of_defectors++;
						}
						else if(current_neighbour.getCurrentPayoff()>best_score)
						{
							best_score = current_neighbour.getCurrentPayoff(); 
							number_of_cooperators = 0;
							number_of_defectors = 0;
							
							if(current_neighbour.getCurrentDefect() == false)
								number_of_cooperators++;
							else
								number_of_defectors++;
							
						}
						
					}
					
					if(number_of_cooperators==0)
					{
						nodes.get(i).setNextDefect(true);
					}
					else if(number_of_defectors==0)
					{
						nodes.get(i).setNextDefect(false);
					}
					else
					{
						double type = Math.random()*(number_of_cooperators + number_of_defectors);
						if(type>number_of_defectors)
							nodes.get(i).setNextDefect(false);
						else
							nodes.get(i).setNextDefect(true);
					}
					
				}
			// Update current defect
			int temp_change =0;
			for(int i = 0; i<numberOfNodes; i++)
			{
				
				if(nodes.get(i).getCurrentDefect()!= nodes.get(i).getNextDefect())
					temp_change = 1;
				
				nodes.get(i).updateCurrentDefect();			
			}
			
			if(temp_change == 0)
				changeCounter++;// no change
			else
				changeCounter = 0;
			
			if(changeCounter == maxNoChange)
			{
				return 1; // No more change in the graph
			}
			
			// Calculate next scores and update current score
					for(int i = 0; i<numberOfNodes; i++)
					{
						int score = 0;
						
						ArrayList<Vertex> neighbours =	nodes.get(i).getNeighbors();
						
						for(int j = 0; j<neighbours.size(); j++)
						{
							Vertex current_neighbour = neighbours.get(j);
							if(nodes.get(i).getCurrentDefect() == true )
							{
								if(current_neighbour.getCurrentDefect() == true )
									score = score + payoff_P;
								else
									score = score + payoff_T;
							}
							else
							{
								if(current_neighbour.getCurrentDefect() == true )
									score = score + payoff_S;
								else
									score = score + payoff_R;
							}
							
						}
						nodes.get(i).setNextPayoff(score);
						nodes.get(i).updateCurrentPayoff();
						
					}	
			}
			return 0; //Reached maxTurns
		}
		

		public void calculateRobustness(double [][]results)
		{
			// for each step
			for(int i = 0;i<(rangeEnd-rangeStart)/stepSize +1;i++)
			{				
				// for each run
				for(int j = 2;j<2+numberOfRepeats;j++)
				{						
					// initialize nodes to all cooperating
					for(int i1 = 0; i1<numberOfNodes; i1++)
					{
						nodes.get(i1).setCurrentDefect(false);
					}
					
					// set initial defectors
					for(int i1 = 0; i1<results[0][i]; i1++)
					{
						// node to defect
						int defectorNumber = (int) (Math.random()*(numberOfNodes-i1));
						
						int counter = 0;
						
						for(int j1 = 0; j1<numberOfNodes; j1++)
						{
							if(nodes.get(j1).getCurrentDefect() == false)
							{
								if(counter == defectorNumber)
								{
									nodes.get(j1).setCurrentDefect(true);
									break;
								}
								else
									counter++;
								
							}
						}
					}
					
					// Calculate current score for each node
					
					for(int i1 = 0; i1<numberOfNodes; i1++)
					{
						int score = 0;
						for(int j1 = 0; j1<nodes.get(i1).getDegree(); j1++)
						{								
							if(nodes.get(i1).getCurrentDefect() == true )
							{
								if(nodes.get(i1).getNeighbor(j1).getCurrentDefect() == true )
									score = score + payoff_P;
								else
									score = score + payoff_T;
							}
							else
							{
								if(nodes.get(i1).getNeighbor(j1).getCurrentDefect() == true )
									score = score + payoff_S;
								else
									score = score + payoff_R;
							}
							
						}
						nodes.get(i1).setNextPayoff(score);
						nodes.get(i1).updateCurrentPayoff();
						
					}
					
					// play game
					
					int game_result = playGame(1000,10);
					
					if(game_result == 0)
						System.out.println("Reached max turns");
					
					// record results
					for(int i1 = 0; i1<numberOfNodes; i1++)
					{
						if(nodes.get(i1).getCurrentDefect()==false)
						{
							results[j][i]++;
						}
					}
					
					// Calculate row 2
					results[1][i] = results[1][i] + results[j][i];						
	    			
				}
				results[1][i]= results[1][i] /numberOfRepeats;
				
			}
		}
		
		// (2*nrEdges)/(nrNodes*(nrNodes-1)) 
		public double calculateDensity()
		{
			
			double nrEdgesT2= 0;
			
			for(int i = 0; i<numberOfNodes; i++)
			{
				nrEdgesT2 = nrEdgesT2 + nodes.get(i).getDegree();
			}
			
			
			return nrEdgesT2/((double)numberOfNodes*((double)numberOfNodes-1));
		}
		
		// the greatest distance between any pair of vertices
		public int calculateDiameter()
		{
			int largestDistance=0;
			for(int i = 0; i<numberOfNodes; i++)
			{
				//for each node find its longest path				
				int path = longestPath(i);
				if(largestDistance<path)
					largestDistance = path;
				
			}
			
			return largestDistance;
		}
		
		//calculates the longest path of one node
		public int longestPath(int nodeIndex)
		{
			int longestP = 0;
			Queue<Vertex> verticesQueue= new LinkedList<Vertex>();
						
			int[] seen = new int[numberOfNodes];
			
			for(int i = 0; i<numberOfNodes; i++)
			{
				seen[i] = -1;
			}
			seen[nodeIndex] = 0;
			verticesQueue.add(nodes.get(nodeIndex));
			
			int counter = 1;
			
			while(counter < numberOfNodes)
			{
				Vertex currentVertex = verticesQueue.poll();
				
				for(int i = 0; i<currentVertex.getDegree(); i++)
				{
					
					
					
					if(seen[currentVertex.getNeighbor(i).getVertexNumber()] == -1)
					{
						verticesQueue.add(currentVertex.getNeighbor(i));
						counter++;
						seen[currentVertex.getNeighbor(i).getVertexNumber()]=seen[currentVertex.getVertexNumber()]+1;
					}
					else
						if(seen[currentVertex.getNeighbor(i).getVertexNumber()]<seen[currentVertex.getVertexNumber()]+1)
							seen[currentVertex.getNeighbor(i).getVertexNumber()]=seen[currentVertex.getVertexNumber()]+1;	
				}
				
			}
		
			for(int i = 1; i<numberOfNodes; i++)
			{
				if(seen[i] > longestP)
					longestP =seen[i]; 
			}
			
			return longestP;
		}
		
		
		// (2* nrEdges) /nrNodes
		public double calculateAvgDegree()
		{
			double nrEdgesT2= 0;
			
			for(int i = 0; i<numberOfNodes; i++)
			{
				nrEdgesT2 = nrEdgesT2 + nodes.get(i).getDegree();
			}
			
			
			return nrEdgesT2/((double)numberOfNodes);
		}
		
		public void storeResults(double [][]results, String fileName)
		{	
			try {
				//PrintWriter pw = new PrintWriter(new File("./Graphs/" + file_name));
				PrintWriter pw = new PrintWriter(new File(fileName));
				StringBuilder sb = new StringBuilder();
				
				
				// Simulation results
				// for each run
				for(int j = 0;j<results.length;j++)
				{
					
					// for each step
					for(int i = 0;i<results[0].length;i++)
					{
						sb.append(results[j][i]);
						sb.append(',');
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
			// reset vertex count
			Vertex c = new Vertex();
			c.setCount(0);
			
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
