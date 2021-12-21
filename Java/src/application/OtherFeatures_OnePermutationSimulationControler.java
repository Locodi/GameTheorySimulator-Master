package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/*
 * Goes over all connected graphs of 6 nodes and looks at every possible permutation of 
 * one initial defector and stores the averege result of several runs
 */

public class OtherFeatures_OnePermutationSimulationControler {

	
	@FXML
	private CheckBox cbox_WeightedGraphs;
	
	@FXML
	private Label lbl_NumberOfRepeats;
	
	@FXML
	private Label lbl_NumberOfMaxTurns;
	
	@FXML
	private Label lbl_NumberOfNodes;
	
	@FXML
	private TextField txt_NumberOfRepeats;
	
	@FXML
	private TextField txt_NumberOfMaxTurns;
	
	@FXML
	private TextField txt_NumberOfNodes;
	

	private int numberOfNodes;
	
	// Payoff details	
	private static final int payoff_T = 5;
	private static final int payoff_R = 3;	
	private static final int payoff_P = 1;
	private static final int payoff_S = 0;
	
	// run details
	private int numberOfRepeats; // run a configuration (graph + defect node) this amount of times	
	private static final int END_RUN_REPEAT = 10; // after this amount of ticks with no change the run will end
	private int numberOfMaxTurns; // after this amount of ticks the run will end
	private int graphType; // 0 = unweighed graph, 1 = weighted graph	
	
	@FXML
    public void initialize() throws IOException 
	{
		// initialize input
		numberOfRepeats=100; 
		numberOfMaxTurns=200; 
		numberOfNodes=6;
		graphType=0;
		
		txt_NumberOfRepeats.setText(Integer.toString(numberOfRepeats));
		txt_NumberOfMaxTurns.setText(Integer.toString(numberOfMaxTurns));		
		txt_NumberOfNodes.setText(Integer.toString(numberOfNodes));
		cbox_WeightedGraphs.setSelected(false);
	}
	
	public void start(ActionEvent event)throws IOException
	{
		// get and check input
		
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
    	
    	// NumberOfNodes
		
		lbl_NumberOfNodes.setText("");
    	try{
    		int tempImpVal = Integer.parseInt(txt_NumberOfNodes.getText());
    		
    		if(tempImpVal>0 && tempImpVal<10)
    		{
    			numberOfNodes = tempImpVal;	    			
    		}   		
    		else
    			{
    				lbl_NumberOfNodes.setText("Input needs to be > 0 and < 10");
    				all_good = 1;
    			}
    	}
    	catch(Exception e)
    	{
    		lbl_NumberOfNodes.setText("Input needs to be an integer");
    		all_good = 1;
    	}
    	
    	// GraphType
		if(cbox_WeightedGraphs.isSelected()==true)
			graphType=1;
		else
			graphType=0;
		
		// if all good do the operations
		if(all_good == 0)
			generateDatabase();
		
		
	}
	
	

	public int generateDatabase()
	{
		try {
			String temp_FileText;
			if(graphType==1)
				temp_FileText="OnePerturbationSimulation_"+numberOfNodes+ "_nodes_WeightedGraphs"+".csv";
			else
				temp_FileText="OnePerturbationSimulation_"+numberOfNodes+ "_nodes_UnweightedGraphs"+".csv";
			
			PrintWriter pw = new PrintWriter(new File(temp_FileText));
			StringBuilder sb = new StringBuilder();        		
			
			int[][] edges = new int[numberOfNodes][numberOfNodes];
			Vertex[] vertices = new Vertex[numberOfNodes];
			
			
			//Initialize edges to all 0
			for(int i = 0; i<numberOfNodes; i++)
				for(int j = 0; j<numberOfNodes; j++)
					edges[i][j] = 0;		
			
			// representing the edges as a binary number in reverse
			int[] edges_bn = new int[numberOfNodes*(numberOfNodes-1)/2];
			
			for(int i = 0; i<numberOfNodes*(numberOfNodes-1)/2; i++)
				edges_bn[i] = 0;
			
			// for loops generate all possible graphs
			
			for(int graph_counter = 0; graph_counter < Math.pow(2, numberOfNodes*(numberOfNodes-1)/2) ;graph_counter++)
			{
					
				// update edges
				for(int i = 0; i<numberOfNodes*(numberOfNodes-1)/2; i++)
				{
					if(edges_bn[i] == 0)
					{
						edges_bn[i] = 1;
						break;
					}
					else
						edges_bn[i] = 0;
				}
				int edge_counter = 0;
				for(int i = 0; i<numberOfNodes; i++)
					for(int j = i+1; j<numberOfNodes; j++)					
					{					
						edges[i][j] = edges_bn[edge_counter];
						edges[j][i] = edges_bn[edge_counter];
						edge_counter++;
					}							
					
				
				boolean graph_good = false;
				// check if the current graph is connected
				int[] seen = new int[numberOfNodes];
				
				// reset vertices count to 0
				vertices[0]= new Vertex_1_1();
				vertices[0].setCount(0);
				
				for(int i = 0; i<numberOfNodes; i++)
				{				
					vertices[i] = new Vertex_1_1();									
				}
				
				// reset vertices count to 0
				vertices[0].setCount(0);
				
				Queue<Vertex> verticesQueue = new LinkedList<Vertex>();
				for(int i = 1; i<numberOfNodes; i++)
				{
					seen[i] = 0;
				}
				seen[0] = 1;
				verticesQueue.add(vertices[0]);
								
				
				if(1 == isConnected( edges, numberOfNodes, vertices, seen, verticesQueue))
					graph_good = true;
				if(graph_good)
				{
					
					sb.append("Edges:");
					sb.append('\n');
					
					for(int i = 0; i<numberOfNodes; i++)
					{
						for(int j = 0; j<numberOfNodes; j++)
						{
							sb.append(edges[i][j]);
							sb.append(',');
						}
						sb.append('\n');
					}
				}				
				
				if(graph_good)
				{
					
					//the amount of cooperators at the end (END_RUN_REPEAT)
					float[][] graph_robustness = new float[numberOfNodes+1][numberOfNodes+1];
					sb.append("result(avg):");					
			        sb.append(',');
				for(int initial_deffect_vertex = 0; initial_deffect_vertex < numberOfNodes;initial_deffect_vertex++)
				{
					float print_result = 0;
					
					
					for(int repeats=0;repeats<numberOfRepeats;repeats++)
					{
						// reset vertices count to 0
						vertices[0].setCount(0);
						
						// update vertices
						for(int i = 0; i<numberOfNodes; i++)
						{
							if(i != initial_deffect_vertex)
							{
								vertices[i] = new Vertex_1_1();//initial cooperators
							}
							else
								vertices[i] = new Vertex_1_1(0); //initial defector					
						}			
						
						// set vertices degree
						
						for(int i = 0; i<numberOfNodes; i++)
						{
							int degree = 0;
							for(int j = 0; j<numberOfNodes; j++)
							{
								if(edges[i][j]==1)
									degree++;
							}
							
						}
						
						// run them
						int result = playGame(edges, numberOfNodes, vertices);
						// log results
						print_result = print_result + result;
					
						for(int i = 0; i<numberOfNodes; i++)
						{
							if(vertices[i].getCurrentStrategy()== 1)
								graph_robustness[initial_deffect_vertex][i] ++;
									
						}
						
				       
							
					}
					
					print_result = print_result /numberOfRepeats;
					
					
					
			        sb.append(print_result);
			        sb.append(',');		        
			       
					
				}	
				sb.append('\n');
				sb.append("Scores:");
				sb.append('\n');
				
				for(int i = 0; i<numberOfNodes; i++)
				{
					float avg_score = 0;
					for(int j = 0; j<numberOfNodes; j++)
					{
						graph_robustness[i][j] = graph_robustness[i][j] / numberOfRepeats;
						avg_score = avg_score + graph_robustness[i][j];
						
						sb.append(graph_robustness[i][j]);
						sb.append(',');
					}
					
					sb.append(avg_score);
					sb.append(',');
					sb.append('\n');
				}
				float total_score =0;
				for(int i = 0; i<numberOfNodes; i++)
				{
					float avg_score = 0;
					for(int j = 0; j<numberOfNodes; j++)
					{
						avg_score = avg_score + graph_robustness[j][i];
					}
					total_score = total_score + avg_score;
					
					sb.append(avg_score);
					sb.append(',');
				}
				sb.append(total_score);
				sb.append(',');
				// finished with this graph
					sb.append('\n');
				}
			}
		
			pw.write(sb.toString());
	        pw.close();
	        System.out.println("Done!");
	        return 0;
		}
		catch (FileNotFoundException ex) {
			System.out.println("File not found!");
			return 1;
		}
		
		
		
	}
	
	public int playGame(int edges[][], int numberOfNodes, Vertex vertices[])
	{
		
		int repeat_counter = 0;		
		
		// generate initial scores		
		for(int i = 0; i<numberOfNodes; i++)
		{
			int score = 0;
			for(int j = 0; j<numberOfNodes; j++)
			{
				if(edges[i][j] == 1)
				{
					if(vertices[i].getCurrentStrategy() == 0)
					{
						if(vertices[j].getCurrentStrategy() == 0)
							score = score + payoff_P;
						else
							score = score + payoff_T;
					}
					else
					{
						if(vertices[j].getCurrentStrategy() == 0)
							score = score + payoff_S;
						else
							score = score + payoff_R;
					}
				}
			}
			
			vertices[i].setNextPayoff(score);
			vertices[i].setNextStrategy(vertices[i].getCurrentStrategy());
		}
		
		for(int tick_counter = 0; tick_counter<numberOfMaxTurns; tick_counter++ )
		{		
			for(int i = 0; i<numberOfNodes; i++)
			{				
				vertices[i].updateCurrentPayoff();
				vertices[i].setCurrentStrategy(vertices[i].getNextStrategy());
			}
			
			// update next defect
			
			for(int i = 0; i<numberOfNodes; i++)	
			{
				// strategy 1: set all to 0
				/*
				double top_score =0;
				boolean top_defect = true;
				boolean top_defect_set = false;
				int top_defect_number = 0;
				int top_cooperate_number = 0;
				*/
				
				// strategy 2: set all to current vertex
				double top_score = vertices[i].getCurrentPayoff();
				int top_defect = vertices[i].getCurrentStrategy();
				int top_defect_set = 0;				
				int top_defect_number = 0;
				int top_cooperate_number = 0;
				
				if(top_defect == 0)
					top_defect_number++;
				else
					top_cooperate_number++;
				
				for(int j = 0; j<numberOfNodes; j++)				
					if(edges[i][j] == 1)
					{
						double will_play;
						
						if(graphType==0)
							will_play =1;
						else
							will_play = Math.random();
						
						if(graphType==0 || will_play<=1.0/vertices[j].getDegree())
						{
						
						if(vertices[j].getCurrentPayoff() == top_score)
						{
							// we have more than one top score
							if(vertices[j].getCurrentStrategy() == 0)							
								top_defect_number ++;						
							else															
								top_cooperate_number ++;							
						}
						else
							if(vertices[j].getCurrentPayoff() > top_score)
							{
								if(vertices[j].getCurrentStrategy() == 0)
								{
									top_defect_number = 1;
									top_cooperate_number = 0;									
								}
								else
								{
									top_defect_number = 0;
									top_cooperate_number = 1;									
								}
								
								top_score= vertices[j].getCurrentPayoff();
								top_defect = vertices[j].getCurrentStrategy();
							}
						}
					}	
				// if we have more than one top neighbor
				if(top_defect_number>1 || top_cooperate_number>1 || (top_defect_number>0 && top_cooperate_number>0))
				{
					double type = Math.random()*(top_defect_number + top_cooperate_number);
					if(type>top_defect_number)
						vertices[i].setNextStrategy(1);
					else
						vertices[i].setNextStrategy(0);
				}
				else
					vertices[i].setNextStrategy(top_defect);
			}
			// generate next tick scores
			
			for(int i = 0; i<numberOfNodes; i++)
			{
				int score = 0;
				for(int j = 0; j<numberOfNodes; j++)
				{
					if(edges[i][j] == 1)
					{
						if(vertices[i].getNextStrategy() == 0)
						{
							if(vertices[j].getNextStrategy() == 0)
								score = score + payoff_P;
							else
								score = score + payoff_T;
						}
						else
						{
							if(vertices[j].getNextStrategy() == 0)
								score = score + payoff_S;
							else
								score = score + payoff_R;
						}
					}
				}
				
				vertices[i].setNextPayoff(score);				
			}
			
			// check if graph changed from last tick
			
			boolean graph_changed = false;
			for(int i = 0; i<numberOfNodes; i++)
			{
				if(vertices[i].getCurrentStrategy() != vertices[i].getNextStrategy())
				{
					graph_changed = true;
					break;							
				}
			}
			
			if(graph_changed == false)
				repeat_counter++;
			else
				repeat_counter = 0;
			
			
			if(repeat_counter == END_RUN_REPEAT)
			{	
				return 1;// no change in graph in the last END_RUN_REPEAT turns
			}
		}
		
		
		return 0;// reached the maximum amount of turns
	}
	
	// returns 1 if the graph is connected and 0 otherwise
	public static int isConnected(int edges[][], int numberOfNodes, Vertex vertices[], int[] seen, Queue<Vertex> verticesQueue)
	{
		int counter = 1;
		while(counter < numberOfNodes)
		{
			Vertex currentVertex = verticesQueue.poll();
			if(currentVertex != null)
			{
				for(int i = 0; i<numberOfNodes; i++)
				{
					if(edges[i][currentVertex.getVertexNumber()] == 1 && seen[i] == 0)
					{
						verticesQueue.add(vertices[i]);
						counter++;
						seen[i]=1;
					}
				
				}
			}
			else
				return 0;			
		}
		return 1;
	}
	
	public void goBack(ActionEvent event)throws IOException
	{	
		// reset vertex count
		Vertex c = new Vertex_1_1();
		c.setCount(0);
		
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
	
}
