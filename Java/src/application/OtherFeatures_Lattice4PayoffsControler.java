package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
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

public class OtherFeatures_Lattice4PayoffsControler {

	
	@FXML
    public void initialize() throws IOException 
	{
		// initialize input
		
	}
	
	public void start(ActionEvent event)throws IOException
	{		
		
		generateDatabase();	
		
	}
	
	

	public int generateDatabase()
	{
		try {
			
			
			// for each possible payoff types
			
			
			
			ArrayList<double[]> payoffTypes=new ArrayList<double[]>();
			int counter = 0;
		    int R3S_Start = 0;
		    int R2S2_Start = 0;
		    int RS3_Start = 0;
		    
		      
		    for(int R4=1;R4<5;R4++)
		    {
		    	if(R4>2)
		    		R3S_Start = R4;
		    	else
		    		R3S_Start = 2;
		    	
		    	for(int R3S=R3S_Start;R3S<6;R3S++)
		    	{
		    		if(R3S>3)
		    			R2S2_Start = R3S;
		    		else
		    			R2S2_Start = 3;
		    		
		    		for(int R2S2=R2S2_Start;R2S2<6;R2S2++)
		    		{
		    			if(R2S2>4)
		    				RS3_Start = R2S2;
		    			else
		    				RS3_Start = 4; 
		    			
		    			for(int RS3=RS3_Start;RS3<6;RS3++)
		    			{
		    				counter++;
		    				
		    				double[] list = new double[8];
		    				list[0] = R4;
		    				list[1] = R3S;
		    				list[2] = R2S2;
		    				list[3] = RS3;
		    				
		    				list[4] = 0; // t
		    				list[5] = 0; // r
		    				list[6] = 0; // p
		    				list[7] = 0; // s
		    				payoffTypes.add(list);
		    				
		                }
		    		} 
		        }
		    }

		    System.out.println("counter = " + counter);


			// for a range off payoffs check to see if they fit any of the list
			
		    for(BigDecimal t = new BigDecimal("0.3"); t.doubleValue() < 30.1; t=t.add(new BigDecimal("0.1")))
		    {
		    	 System.out.println(" T = " + t);
		    	 
		    	 for(BigDecimal r = new BigDecimal("0.2"); r.doubleValue() < t.doubleValue(); r=r.add(new BigDecimal("0.1")))
				    {
			    		for(BigDecimal p = new BigDecimal("0.1"); p.doubleValue() < r.doubleValue(); p=p.add(new BigDecimal("0.1")))
			 		    {
			    			 for(BigDecimal s = new BigDecimal("0.0"); s.doubleValue() < p.doubleValue(); s=s.add(new BigDecimal("0.1")))
			    			 {				    				 
			    				 for(int i = 0; i < payoffTypes.size(); i++)
		    				     {
			    					 int all_good = 1;
			    					 // check to see if payoffs found			    					 
		    						 if(payoffTypes.get(i)[4] == 0)
		    							 all_good = 0;
			    					 
			    					 if(all_good == 0)
			    					 {
			    						 
			    						 int all_good2=0;
			    						 
			    						 for(int j=0; j<4 && all_good2 == 0; j++)
			    						 {
			    							 	if(payoffTypes.get(i)[j] == 1 && all_good2 == 0)
			    							 	{
			    							 		// 4T > 4*r-j*(r-s) > 3T+P
			    							 		if(!(t.multiply(new BigDecimal("4.0")).doubleValue() > r.multiply(new BigDecimal("4.0")).subtract(new BigDecimal(j).multiply(r.subtract(s))).doubleValue() && 
			    							 				t.multiply(new BigDecimal("3.0")).add(p).doubleValue() < r.multiply(new BigDecimal("4.0")).subtract(new BigDecimal(j).multiply(r.subtract(s))).doubleValue() ))
			    							 		{
			    							 			all_good2 = 1;
			    							 		}			    							 			
			    							 	}
			    							 	else if(payoffTypes.get(i)[j] == 2 && all_good2 == 0)
			    							 	{
			    							 		// 3T+P > 4*r-j*(r-s) > 2T+2P
			    							 		if(!( t.multiply(new BigDecimal("3.0")).add(p).doubleValue()> r.multiply(new BigDecimal("4.0")).subtract(new BigDecimal(j).multiply(r.subtract(s))).doubleValue() && 
			    							 				t.multiply(new BigDecimal("2.0")).add(p.multiply(new BigDecimal("2.0"))).doubleValue()<r.multiply(new BigDecimal("4.0")).subtract(new BigDecimal(j).multiply(r.subtract(s))).doubleValue()))
			    							 		{
			    							 			all_good2 = 1;
			    							 		}
			    							 	}
			    							 	else if(payoffTypes.get(i)[j] == 3 && all_good2 == 0)
			    							 	{
			    							 		// 2T+2P > 4*r-j*(r-s) > T+3P
			    							 		if(!(t.multiply(new BigDecimal("2.0")).add(p.multiply(new BigDecimal("2.0"))).doubleValue()>r.multiply(new BigDecimal("4.0")).subtract(new BigDecimal(j).multiply(r.subtract(s))).doubleValue() &&
			    							 				t.add(p.multiply(new BigDecimal("3.0"))).doubleValue()<r.multiply(new BigDecimal("4.0")).subtract(new BigDecimal(j).multiply(r.subtract(s))).doubleValue()))
			    							 		{
			    							 			all_good2 = 1;
			    							 		}
			    							 	}
			    							 	else if(payoffTypes.get(i)[j] == 4 && all_good2 == 0)
			    							 	{
			    							 		// T+3P > 4*r-j*(r-s) > 4P
			    							 		if(!(t.add(p.multiply(new BigDecimal("3.0"))).doubleValue()>r.multiply(new BigDecimal("4.0")).subtract(new BigDecimal(j).multiply(r.subtract(s))).doubleValue() && 
			    							 				p.multiply(new BigDecimal("4.0")).doubleValue()<r.multiply(new BigDecimal("4.0")).subtract(new BigDecimal(j).multiply(r.subtract(s))).doubleValue()))
			    							 		{
			    							 			all_good2 = 1;
			    							 		}
			    							 	}
			    							 	else if(payoffTypes.get(i)[j] == 5 && j != 0 && all_good2 == 0) // 4R > 4P
			    							 	{
			    							 		// 4P > 4*r-j*(r-s) > 4S
			    							 		if(!(p.multiply(new BigDecimal("4.0")).doubleValue()>r.multiply(new BigDecimal("4.0")).subtract(new BigDecimal(j).multiply(r.subtract(s))).doubleValue() &&
			    							 				s.multiply(new BigDecimal("4.0")).doubleValue()<r.multiply(new BigDecimal("4.0")).subtract(new BigDecimal(j).multiply(r.subtract(s))).doubleValue()))
			    							 		{
			    							 			all_good2 = 1;
			    							 		}
			    							 	}
			    						 }
			    						 
			    						 if(all_good2 == 0)
			    						 {
			    							 payoffTypes.get(i)[4]=t.doubleValue();
			    							 payoffTypes.get(i)[5]=r.doubleValue();
			    							 payoffTypes.get(i)[6]=p.doubleValue();
			    							 payoffTypes.get(i)[7]=s.doubleValue();
			    						 }
			    						 
			    					 }
		    				     }			    			    	
			    			 }
			 		    }
				    }
		    }
		    
		    
		    
		    // save in the results document
				// payoff type  - payoffs: T R P S
				// results 3x5000
				// results 122x123
			
			
			
		    String temp_FileText;
			
			temp_FileText="Lattice4Payoffs"+".csv";
			
			PrintWriter pw = new PrintWriter(new File(temp_FileText));
			StringBuilder sb = new StringBuilder(); 
		    
			sb.append("Payoff Type");
    		sb.append(',');
    		
    		sb.append("T");
    		sb.append(',');
    		
    		sb.append("R");
    		sb.append(',');
    		
    		sb.append("P");
    		sb.append(',');
    		
    		sb.append("S");
    		sb.append(',');
    		
    		sb.append('\n');
			
    		
    		
    		
    		
		    for(int i = 0; i < payoffTypes.size(); i++)
		    {
		    	
		    	String eq = "4T < ";
		    	
		    	
	    		if(payoffTypes.get(i)[0] == 1)
	    		{
	    			eq = eq + "4R < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[1] == 1)
	    		{
	    			eq = eq + "3R+S < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[2] == 1)
	    		{
	    			eq = eq + "2R+2S < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[3] == 1)
	    		{
	    			eq = eq + "R+3S < ";
	    		}
		    	
	    		eq = eq + "3T+P < ";
		    	
	    		if(payoffTypes.get(i)[0] == 2)
	    		{
	    			eq = eq + "4R < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[1] == 2)
	    		{
	    			eq = eq + "3R+S < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[2] == 2)
	    		{
	    			eq = eq + "2R+2S < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[3] == 2)
	    		{
	    			eq = eq + "R+3S < ";
	    		}
	    		
	    		eq = eq + "2T+2P < ";
	    		
	    		if(payoffTypes.get(i)[0] == 3)
	    		{
	    			eq = eq + "4R < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[1] == 3)
	    		{
	    			eq = eq + "3R+S < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[2] == 3)
	    		{
	    			eq = eq + "2R+2S < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[3] == 3)
	    		{
	    			eq = eq + "R+3S < ";
	    		}
	    		
	    		eq = eq + "T+3P < ";
	    		
	    		if(payoffTypes.get(i)[0] == 4)
	    		{
	    			eq = eq + "4R < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[1] == 4)
	    		{
	    			eq = eq + "3R+S < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[2] == 4)
	    		{
	    			eq = eq + "2R+2S < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[3] == 4)
	    		{
	    			eq = eq + "R+3S < ";
	    		}
	    		
	    		eq = eq + "4P < ";
	    		
	    		if(payoffTypes.get(i)[0] == 5)
	    		{
	    			eq = eq + "4R < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[1] == 5)
	    		{
	    			eq = eq + "3R+S < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[2] == 5)
	    		{
	    			eq = eq + "2R+2S < ";
	    		}
	    		
	    		if(payoffTypes.get(i)[3] == 5)
	    		{
	    			eq = eq + "R+3S < ";
	    		}
	    		
	    		eq = eq + "4S";
	    		
	    		sb.append(eq);
	    		sb.append(',');
	    		
	    		// T
	    		sb.append(payoffTypes.get(i)[4]);
	    		sb.append(',');
	    		
	    		// R
	    		sb.append(payoffTypes.get(i)[5]);
	    		sb.append(',');
	    		
	    		// P
	    		sb.append(payoffTypes.get(i)[6]);
	    		sb.append(',');
	    		
	    		// S
	    		sb.append(payoffTypes.get(i)[7]);
	    		sb.append(',');
	    		
	    		sb.append('\n');
	    		
	    		sb.append("Results 3x5000");
	    		sb.append(',');
	    		
	    		sb.append('\n');
	    		
	    		
	    		sb.append("Results 122x123");
	    		sb.append(',');
	    		
	    		sb.append('\n');
	    		
	    		
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
	
	
	public void goBack(ActionEvent event)throws IOException
	{	
		// reset vertex count
		Vertex c = new Vertex();
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
