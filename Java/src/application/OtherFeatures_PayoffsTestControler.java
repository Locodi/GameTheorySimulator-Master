package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

/*
 * 
 * Goes over all payoff ratios from 1 to 10 with increment of 0.1
 * and for each one tries to find variables for a=a' and b (the number of minor nodes, it looks up to 20000) 
 * such that we can build the LocodiGraph
 * 
 * There is an option for S=0
 */

public class OtherFeatures_PayoffsTestControler {

	
	@FXML
	private CheckBox cbox_S0;
	
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
			
			int S0; // S = 0?
			// GraphType
			if(cbox_S0.isSelected()==true)
				S0=1;
			else
				S0=0;
			
			
		    String temp_FileText;
			
			temp_FileText="PayoffsTest"+".csv";
			
			PrintWriter pw = new PrintWriter(new File(temp_FileText));
			StringBuilder sb = new StringBuilder(); 
		    
			
			sb.append("Variables");
    		sb.append(',');
    		
    		sb.append("T/R");
    		sb.append(',');
    		
    		sb.append("R/P");
    		sb.append(',');
    		
    		if(S0 == 0)
    		{
    			sb.append("P/S");
    			sb.append(',');  
    		}
    		
    		sb.append("a");
    		sb.append(',');
    		
    		sb.append("b");
    		sb.append(',');
    		
    		sb.append("Found");
    		sb.append(',');
    		
    		sb.append("a/b");
    		sb.append(',');
    		
    		sb.append("T/R<R/P");
    		sb.append(',');
    		
    		sb.append('\n');
			
    		BigDecimal T_R, R_P, P_S; // T/R  R/P  P/S
    		
    		
    		if(S0 == 0) // S != 0
    		{
				// for each payoff variables
				for(T_R=new BigDecimal("1.1"); T_R.compareTo(new BigDecimal("10"))==-1; T_R = T_R.add(new BigDecimal("0.1")))
				{
					System.out.println("T/R= " + T_R.doubleValue());
					for(R_P=new BigDecimal("1.1"); R_P.compareTo(new BigDecimal("10"))==-1; R_P = R_P.add(new BigDecimal("0.1")))
					{
						for(P_S=new BigDecimal("1.1"); P_S.compareTo(new BigDecimal("10"))==-1; P_S = P_S.add(new BigDecimal("0.1")))
						{
							sb.append(',');
							sb.append(T_R.doubleValue());
				    		sb.append(',');
				    		sb.append(R_P.doubleValue());
				    		sb.append(',');
				    		sb.append(P_S.doubleValue());
				    		sb.append(',');
				    		
				    		int found = 0;
				    		
				    		int a=0,b=0;
				    		
				    		
				    		//see if you can find values for a and b
				    		
				    		//determine min value of a
				    		int mina;			    		
				    		
				    		// = (2*T_R*R_P*P_S-1-R_P*P_S)/(R_P*P_S)
				    		BigDecimal temp = ((new BigDecimal("2").multiply(T_R).multiply(R_P).multiply(P_S).subtract(new BigDecimal("1")).subtract(R_P.multiply(P_S)))).divide(R_P.multiply(P_S), 8, RoundingMode.HALF_UP);
				    					    		
				    		// < (2-R_P-1/P_S)/(R_P-1)
				    		if(temp.compareTo(new BigDecimal("2").subtract(R_P).subtract(new BigDecimal("1").divide(P_S, 8, RoundingMode.HALF_UP)).divide(R_P.subtract(new BigDecimal("1")), 8, RoundingMode.HALF_UP))==-1)
				    			temp = new BigDecimal("2").subtract(R_P).subtract(new BigDecimal("1").divide(P_S, 8, RoundingMode.HALF_UP)).divide(R_P.subtract(new BigDecimal("1")), 8, RoundingMode.HALF_UP);
				    		
				    		// <(1+T_R*R_P-2*R_P)/(R_P-1)
				    		if(temp.compareTo(new BigDecimal("1").add(T_R.multiply(R_P)).subtract(new BigDecimal("2").multiply(R_P)).divide(R_P.subtract(new BigDecimal("1")), 8, RoundingMode.HALF_UP))==-1)
				    			temp =new BigDecimal("1").add(T_R.multiply(R_P)).subtract(new BigDecimal("2").multiply(R_P)).divide(R_P.subtract(new BigDecimal("1")), 8, RoundingMode.HALF_UP);
				    		
				    		// <T_R*R_P*P_S-2*R_P*P_S
				    		if(temp.compareTo(T_R.multiply(R_P).multiply(P_S).subtract(new BigDecimal("2").multiply(R_P).multiply(P_S)))==-1)
				    			temp=T_R.multiply(R_P).multiply(P_S).subtract(new BigDecimal("2").multiply(R_P).multiply(P_S));
				    		
				    		// <(T_R*R_P*P_S+P_S-2)/(R_P*P_S)
							if(temp.compareTo(T_R.multiply(R_P).multiply(P_S).add(P_S).subtract(new BigDecimal("2")).divide(R_P.multiply(P_S), 8, RoundingMode.HALF_UP))==-1)
								temp=T_R.multiply(R_P).multiply(P_S).add(P_S).subtract(new BigDecimal("2")).divide(R_P.multiply(P_S), 8, RoundingMode.HALF_UP);
				    			
				    		if(temp.compareTo(new BigDecimal("2"))==-1)
				    			temp=new BigDecimal("2");
				    			
				    		if(temp.doubleValue()%1 != 0)
				    			temp.add(new BigDecimal("1"));
				    		
				    		
				    		mina=temp.intValue();
				    		
				    		for(a=mina;a<20000;a++)
				    			{
				    				//determine min value of b			    				
				    				int minb;
				    				
				    				// =(2*T_R*R_P*P_S-1-3*R_P*P_S)/(R_P*P_S)
				    				temp =new BigDecimal("2").multiply(T_R).multiply(R_P).multiply(P_S).subtract(new BigDecimal("1")).subtract(new BigDecimal("3").multiply(R_P).multiply(P_S)).divide(R_P.multiply(P_S), 8, RoundingMode.HALF_UP);
				    				
				    				// <T_R*R_P*P_S-4*R_P*P_S
				    				if(temp.compareTo(T_R.multiply(R_P).multiply(P_S).subtract(new BigDecimal("4").multiply(R_P).multiply(P_S)))==-1)
				    					temp = T_R.multiply(R_P).multiply(P_S).subtract(new BigDecimal("4").multiply(R_P).multiply(P_S));
				    				
				    				// <T_R*R_P*P_S-2*R_P*P_S-2
				    				if(temp.compareTo(T_R.multiply(R_P).multiply(P_S).subtract(new BigDecimal("2").multiply(R_P).multiply(P_S)).subtract(new BigDecimal("2")))==-1)
				    					temp = T_R.multiply(R_P).multiply(P_S).subtract(new BigDecimal("2").multiply(R_P).multiply(P_S)).subtract(new BigDecimal("2"));
				    				
				    				// <(P_S*(a+1)+T_R*R_P*P_S-2-2*R_P*P_S)/(R_P*P_S)
				    				if(temp.compareTo((P_S.multiply(new BigDecimal(a+1)).add(T_R.multiply(R_P).multiply(P_S)).subtract(new BigDecimal("2")).subtract(new BigDecimal("2").multiply(R_P).multiply(P_S))).divide(R_P.multiply(P_S), 8, RoundingMode.HALF_UP))==-1)
				    					temp = (P_S.multiply(new BigDecimal(a+1)).add(T_R.multiply(R_P).multiply(P_S)).subtract(new BigDecimal("2")).subtract(new BigDecimal("2").multiply(R_P).multiply(P_S))).divide(R_P.multiply(P_S), 8, RoundingMode.HALF_UP);
				    				
				    				if(temp.compareTo(new BigDecimal("1"))==-1)
						    			temp=new BigDecimal("1");
						    			
				    				if(temp.doubleValue()%1 != 0)
						    			temp.add(new BigDecimal("1"));				    		
						    		
						    		minb=temp.intValue();
				    				
				    				//determine max value of b			    				
				    				int maxb;
				    				
				    				// =(R_P*P_S*a+R_P*P_S+1-4*T_R*R_P*P_S)/(T_R*R_P*P_S)
				    				temp = (R_P.multiply(P_S).multiply(new BigDecimal(a)).add(R_P.multiply(P_S)).add(new BigDecimal("1")).subtract(new BigDecimal("4").multiply(T_R).multiply(R_P).multiply(P_S))).divide(T_R.multiply(R_P).multiply(P_S), 8, RoundingMode.HALF_UP);
				    				
				    				// >(R_P*P_S*a+2-P_S-3*T_R*R_P*P_S)/(T_R*R_P*P_S)
				    				if(temp.compareTo((R_P.multiply(P_S).multiply(new BigDecimal(a)).add(new BigDecimal("2")).subtract(P_S).subtract(new BigDecimal("3").multiply(T_R).multiply(R_P).multiply(P_S))).divide(T_R.multiply(R_P).multiply(P_S), 8, RoundingMode.HALF_UP))==1)
				    					temp = (R_P.multiply(P_S).multiply(new BigDecimal(a)).add(new BigDecimal("2")).subtract(P_S).subtract(new BigDecimal("3").multiply(T_R).multiply(R_P).multiply(P_S))).divide(T_R.multiply(R_P).multiply(P_S), 8, RoundingMode.HALF_UP);
				    				
				    				if(temp.compareTo(new BigDecimal("20000"))==1)
						    			temp=new BigDecimal("20000");
				    				
				    				maxb=temp.intValue();
				    				
					    			for(b=minb;b<maxb;b++)			    			
					    			{
					    				found = 0;
					    								    				
					    				//if it respects all the rules found = 1
					    				if(new BigDecimal(a).compareTo((new BigDecimal("2").multiply(T_R).multiply(R_P).multiply(P_S).subtract(new BigDecimal("1")).subtract(R_P.multiply(P_S))).divide(R_P.multiply(P_S), 8, RoundingMode.HALF_UP))==1)
					    					if(new BigDecimal(b).compareTo(new BigDecimal("2").multiply(T_R).multiply(R_P).multiply(P_S).subtract(new BigDecimal("1")).subtract(new BigDecimal("3").multiply(R_P).multiply(P_S)).divide(R_P.multiply(P_S), 8, RoundingMode.HALF_UP))==1)
					    						if(new BigDecimal(b).compareTo((R_P.multiply(P_S).multiply(new BigDecimal(a)).add(R_P.multiply(P_S)).add(new BigDecimal("1")).subtract(new BigDecimal("4").multiply(T_R).multiply(R_P).multiply(P_S))).divide(T_R.multiply(R_P).multiply(P_S), 8, RoundingMode.HALF_UP))==-1)
					    							if(new BigDecimal(b).compareTo(T_R.multiply(R_P).multiply(P_S).subtract(new BigDecimal("4").multiply(R_P).multiply(P_S)))==1)
					    								if(new BigDecimal(a).compareTo(new BigDecimal("2").subtract(R_P).subtract(new BigDecimal("1").divide(P_S, 8, RoundingMode.HALF_UP)).divide(R_P.subtract(new BigDecimal("1")), 8, RoundingMode.HALF_UP))==1)
					    									if(new BigDecimal(b).compareTo(T_R.multiply(R_P).multiply(P_S).subtract(new BigDecimal("2").multiply(R_P).multiply(P_S)).subtract(new BigDecimal("2")))==1)
					    										if(new BigDecimal(a).compareTo(new BigDecimal("1").add(T_R.multiply(R_P)).subtract(new BigDecimal("2").multiply(R_P)).divide(R_P.subtract(new BigDecimal("1")), 8, RoundingMode.HALF_UP))==1)
					    											if(new BigDecimal(b).compareTo((P_S.multiply(new BigDecimal(a+1)).add(T_R.multiply(R_P).multiply(P_S)).subtract(new BigDecimal("2")).subtract(new BigDecimal("2").multiply(R_P).multiply(P_S))).divide(R_P.multiply(P_S), 8, RoundingMode.HALF_UP))==1)
					    												if(new BigDecimal(a).compareTo(T_R.multiply(R_P).multiply(P_S).subtract(new BigDecimal("2").multiply(R_P).multiply(P_S)))==1)
					    													if(new BigDecimal(a).compareTo(T_R.multiply(R_P).multiply(P_S).add(P_S).subtract(new BigDecimal("2")).divide(R_P.multiply(P_S), 8, RoundingMode.HALF_UP))==1)
					    														if(new BigDecimal(b).compareTo((R_P.multiply(P_S).multiply(new BigDecimal(a)).add(new BigDecimal("2")).subtract(P_S).subtract(new BigDecimal("3").multiply(T_R).multiply(R_P).multiply(P_S))).divide(T_R.multiply(R_P).multiply(P_S), 8, RoundingMode.HALF_UP))==-1)
					    															found = 1;
					    				//found valid values
					    				if(found==1)
						    				break;
					    			}
					    			if(found==1)
					    				break;
				    			}
				    		
				    		
				    		
				    		if(found == 0)
				    		{
				    			sb.append('-');
					    		sb.append(',');
					    		sb.append('-');
					    		sb.append(',');
					    		
					    		sb.append('0');
					    		sb.append(',');  
					    		
					    		sb.append('-');
					    		sb.append(',');
				    		}
				    		else
				    		{
				    			sb.append(a);
					    		sb.append(',');
					    		sb.append(b);
					    		sb.append(',');
					    		
					    		sb.append('1');
					    		sb.append(',');
					    		
					    		sb.append(new BigDecimal(a).divide(new BigDecimal(b), 8, RoundingMode.HALF_UP).doubleValue());
					    		sb.append(',');
				    		}
				    		if(T_R.compareTo(R_P)==-1)
				    			sb.append('1');
				    		else
				    			sb.append('0');
				    		sb.append(',');
				    		
				    		sb.append('\n');
						}
					}
				}
    		
    		}
    		else // S = 0
    		{
    			
    			// for each payoff variables
				for(T_R=new BigDecimal("1.1"); T_R.compareTo(new BigDecimal("2"))==-1; T_R = T_R.add(new BigDecimal("0.1")))
				{
					System.out.println("T/R= " + T_R.doubleValue());
					for(R_P=new BigDecimal("1.1"); R_P.compareTo(new BigDecimal("10"))==-1; R_P = R_P.add(new BigDecimal("0.1")))
					{					
						sb.append(',');
						sb.append(T_R.doubleValue());
			    		sb.append(',');
			    		sb.append(R_P.doubleValue());
			    		sb.append(',');
			    		
			    		int found = 0;
			    		
			    		int a=0,b=0;
			    		
			    		
			    		//see if you can find values for a and b
						
			    		//determine min value of a
			    		int mina;
			    		
			    		
			    		// 1)  a>2*T_R-1			    		
			    		BigDecimal temp = new BigDecimal("2").multiply(T_R).subtract(new BigDecimal("1"));			    					   
			    		
			    		// 5)  a>(2-R_P)/(R_P-1)
			    		if(temp.compareTo((new BigDecimal("2").subtract(R_P)).divide(R_P.subtract(new BigDecimal("1")), 8, RoundingMode.HALF_UP))==-1)
			    			temp = (new BigDecimal("2").subtract(R_P)).divide(R_P.subtract(new BigDecimal("1")), 8, RoundingMode.HALF_UP);
			    		
			    		// 7)  a>(1+T_R*R_P-2*R_P)/(R_P-1)
			    		if(temp.compareTo((new BigDecimal("1").add(T_R.multiply(R_P)).subtract(new BigDecimal("2").multiply(R_P))).divide(R_P.subtract(new BigDecimal("1")), 8, RoundingMode.HALF_UP))==-1)
			    			temp = (new BigDecimal("1").add(T_R.multiply(R_P)).subtract(new BigDecimal("2").multiply(R_P))).divide(R_P.subtract(new BigDecimal("1")), 8, RoundingMode.HALF_UP);
			    		
			    		// 10) a>(T_R*R_P+1)/R_P
			    		if(temp.compareTo((T_R.multiply(R_P).add(new BigDecimal("1"))).divide(R_P, 8, RoundingMode.HALF_UP))==-1)
			    			temp = (T_R.multiply(R_P).add(new BigDecimal("1"))).divide(R_P, 8, RoundingMode.HALF_UP);
			    		
			    		if(temp.compareTo(new BigDecimal("2"))==-1)
			    			temp=new BigDecimal("2");
			    			
			    		if(temp.doubleValue()%1 != 0)
			    			temp.add(new BigDecimal("1"));
			    		
			    		
			    		mina=temp.intValue();
			    		
			    		for(a=mina;a<20000;a++)
		    			{
			    			//determine min value of b
				    		int minb, maxb;
				    		
				    		
				    		// 2)  b>2*T_R-3		    		
				    		temp = new BigDecimal("2").multiply(T_R).subtract(new BigDecimal("3"));
				    		
				    		// 8)  b>(a+1+T_R*R_P-2*R_P)/R_P
				    		if(temp.compareTo((new BigDecimal(a).add(new BigDecimal("1")).add(T_R.multiply(R_P)).subtract(new BigDecimal("2").multiply(R_P))).divide(R_P, 8, RoundingMode.HALF_UP))==-1)
				    			temp = (new BigDecimal(a).add(new BigDecimal("1")).add(T_R.multiply(R_P)).subtract(new BigDecimal("2").multiply(R_P))).divide(R_P, 8, RoundingMode.HALF_UP);
				    		
				    		if(temp.compareTo(new BigDecimal("1"))==-1)
				    			temp=new BigDecimal("1");
				    			
		    				if(temp.doubleValue()%1 != 0)
				    			temp.add(new BigDecimal("1"));				    		
				    		
				    		minb=temp.intValue();
				    		
				    		// 3)  b<(a+1-4*T_R)/T_R
				    		temp = (new BigDecimal(a).add(new BigDecimal("1")).subtract(new BigDecimal("4").multiply(T_R))).divide(T_R, 8, RoundingMode.HALF_UP);
				    		
				    		// 11) b<(R_P*a-1-3*T_R*R_P)/(T_R*R_P)
				    		if(temp.compareTo((R_P.multiply(new BigDecimal(a)).subtract(new BigDecimal("1")).subtract(new BigDecimal("3").multiply(T_R).multiply(R_P))).divide(T_R.multiply(R_P), 8, RoundingMode.HALF_UP))==1)
				    			temp = (R_P.multiply(new BigDecimal(a)).subtract(new BigDecimal("1")).subtract(new BigDecimal("3").multiply(T_R).multiply(R_P))).divide(T_R.multiply(R_P), 8, RoundingMode.HALF_UP);
				    		
				    		if(temp.compareTo(new BigDecimal("20000"))==1)
				    			temp=new BigDecimal("20000");
		    				
		    				maxb=temp.intValue();
				    		
			    			for(b=minb;b<maxb;b++)
			    			{
			    				
			    				
			    				// rules
					    		
					    		// 1)  a>2*T_R-1
					    		// 2)  b>2*T_R-3
					    		// 3)  b<(a+1-4*T_R)/T_R
					    		// 4)  T_R<4 -> TRUE due to 6)
					    		// 5)  a>(2-R_P)/(R_P-1)
					    		// 6)  T_R<2
					    		// 7)  a>(1+T_R*R_P-2*R_P)/(R_P-1)
					    		// 8)  b>(a+1+T_R*R_P-2*R_P)/R_P
					    		// 9)  T_R<2 -> TRUE due to 6)
					    		// 10) a>(T_R*R_P+1)/R_P
					    		// 11) b<(R_P*a-1-3*T_R*R_P)/(T_R*R_P)
			    				
			    								    		
					    		found = 0;	
			    				
			    				//if it respects all the rules found = 1	
			    				if(new BigDecimal(a).compareTo(new BigDecimal("2").multiply(T_R).subtract(new BigDecimal("1")))==1) //1)  a>2*T_R-1
			    					if(new BigDecimal(b).compareTo(new BigDecimal("2").multiply(T_R).subtract(new BigDecimal("3")))==1) // 2)  b>2*T_R-3
			    						if(new BigDecimal(b).compareTo((new BigDecimal(a).add(new BigDecimal("1")).subtract(new BigDecimal("4").multiply(T_R))).divide(T_R, 8, RoundingMode.HALF_UP))==-1) // 3)  b<(a+1-4*T_R)/T_R
			    							if(new BigDecimal(a).compareTo((new BigDecimal("2").subtract(R_P)).divide(R_P.subtract(new BigDecimal("1")), 8, RoundingMode.HALF_UP))==1) // 5)  a>(2-R_P)/(R_P-1)
			    								if(T_R.compareTo(new BigDecimal("2"))==-1) // 6)  T_R<2
			    									if(new BigDecimal(a).compareTo((new BigDecimal("1").add(T_R.multiply(R_P)).subtract(new BigDecimal("2").multiply(R_P))).divide(R_P.subtract(new BigDecimal("1")), 8, RoundingMode.HALF_UP))==1) // 7)  a>(1+T_R*R_P-2*R_P)/(R_P-1)
			    										if(new BigDecimal(b).compareTo((new BigDecimal(a).add(new BigDecimal("1")).add(T_R.multiply(R_P)).subtract(new BigDecimal("2").multiply(R_P))).divide(R_P, 8, RoundingMode.HALF_UP))==1) // 8)  b>(a+1+T_R*R_P-2*R_P)/R_P
			    											if(new BigDecimal(a).compareTo((T_R.multiply(R_P).add(new BigDecimal("1"))).divide(R_P, 8, RoundingMode.HALF_UP))==1) // 10) a>(T_R*R_P+1)/R_P
			    												if(new BigDecimal(b).compareTo((R_P.multiply(new BigDecimal(a)).subtract(new BigDecimal("1")).subtract(new BigDecimal("3").multiply(T_R).multiply(R_P))).divide(T_R.multiply(R_P), 8, RoundingMode.HALF_UP))==-1) // 11) b<(R_P*a-1-3*T_R*R_P)/(T_R*R_P)
			    													found = 1;
			    				
			    				//found valid values
			    				if(found==1)
				    				break;
			    				
			    			}
			    			if(found==1)
			    				break;
		    			}
			    		
			    		
			    		if(found == 0)
			    		{
			    			sb.append('-');
				    		sb.append(',');
				    		sb.append('-');
				    		sb.append(',');
				    		
				    		sb.append('0');
				    		sb.append(',');  
				    		
				    		sb.append('-');
				    		sb.append(',');
			    		}
			    		else
			    		{
			    			sb.append(a);
				    		sb.append(',');
				    		sb.append(b);
				    		sb.append(',');
				    		
				    		sb.append('1');
				    		sb.append(',');
				    		
				    		sb.append(new BigDecimal(a).divide(new BigDecimal(b), 8, RoundingMode.HALF_UP).doubleValue());
				    		sb.append(',');
			    		}
			    		if(T_R.compareTo(R_P)==-1)
			    			sb.append('1');
			    		else
			    			sb.append('0');
			    		sb.append(',');
			    		
			    		sb.append('\n');
			    		
			    		
					}
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
