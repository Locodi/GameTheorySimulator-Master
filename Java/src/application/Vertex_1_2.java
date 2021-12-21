package application;

import java.util.ArrayList;

import javafx.scene.shape.Circle;

/*
 * Vertex for
 * interaction_model = 1 => Prisoners Dilemma
 * update_mechanism = 2  => Copy best performing player in neighborhood based on normalized payoffs,
 * in case of ties pick at random proportional with the strategy of top players
 * */

public class Vertex_1_2 extends Vertex
{
	
		
	//used to assign the vertex number
	private static int count = 0;
	
	
	Vertex_1_2()
	{
		setMark(0);
		setCurrentStrategy(1);
		setNextStrategy(1);
		
		setCurrentPayoff(0);
		setNextPayoff(0);
		
		setVertexNumber(Vertex_1_2.count);		
		Vertex_1_2.count++;
		
		setVisualObject(null);
		
		setNeighbors(new ArrayList<Vertex> ());
		setEdges(new ArrayList<Edge> ());		

	}
	
	Vertex_1_2(int strategy)
	{
		setMark(0);
		setCurrentStrategy(strategy);
		setNextStrategy(1);
		
		setCurrentPayoff(0);
		setNextPayoff(0);
		
		setVertexNumber(Vertex_1_2.count);		
		Vertex_1_2.count++;
		
		setVisualObject(null);
		
		setNeighbors(new ArrayList<Vertex> ());
		setEdges(new ArrayList<Edge> ());
	}
	

		
	
			
	
	
	public int getCount()
	{
		return Vertex_1_2.count;
	}
	
	public void setCount(int c)
	{
		Vertex_1_2.count = c;
	}
	

	
	
	// calculates and sets the next payoff
	public void calculateNextPayoff(int[] payoffs)
	{
		int payoff_T = payoffs[0];
		int payoff_R = payoffs[1];
		int payoff_P = payoffs[2];
		int payoff_S = payoffs[3];
		double score = 0;
				
		for(int j = 0; j<this.getNeighbors().size(); j++)
		{
			Vertex current_neighbour = this.getNeighbors().get(j);
			if(this.getCurrentStrategy() == 0)
			{
				if(current_neighbour.getCurrentStrategy() == 0)
					score = score + payoff_P;
				else
					score = score + payoff_T;
			}
			else
			{
				if(current_neighbour.getCurrentStrategy() == 0)
					score = score + payoff_S;
				else
					score = score + payoff_R;
			}
			
		}
		
		score = score/this.getNeighbors().size(); // normalize payoff
		
		this.setNextPayoff(score);
	}
	
	public void calculateNextStrategy()
	{
		// set best score to current node score
		double best_score = this.getCurrentPayoff();
		
		int number_of_cooperators = 0;
		int number_of_defectors = 0;
		
		if(this.getCurrentStrategy() == 1)
			number_of_cooperators++;
		else
			number_of_defectors++;
		
		
		for(int j = 0; j<this.getNeighbors().size(); j++)
		{
			Vertex current_neighbour = this.getNeighbors().get(j);
			
			if(current_neighbour.getCurrentPayoff()==best_score)
			{
				if(current_neighbour.getCurrentStrategy() == 1)
					number_of_cooperators++;
				else
					number_of_defectors++;
			}
			else if(current_neighbour.getCurrentPayoff()>best_score)
			{
				best_score = current_neighbour.getCurrentPayoff(); 
				number_of_cooperators = 0;
				number_of_defectors = 0;
				
				if(current_neighbour.getCurrentStrategy() == 1)
					number_of_cooperators++;
				else
					number_of_defectors++;
				
			}
			
		}
		
		// if only defectors in the neighborhood have the best payoff
		if(number_of_cooperators==0)
		{
			this.setNextStrategy(0); // the current node will defect in the next turn
		}
		else if(number_of_defectors==0) // if only cooperators
		{
			this.setNextStrategy(1); // it will cooperate
		}
		else
		{	
			//we randomly set the next strategy proportional to the number of cooperators and defectors with the best payoff
			double type = Math.random()*(number_of_cooperators + number_of_defectors);
			if(type>number_of_defectors)
				this.setNextStrategy(1);
			else
				this.setNextStrategy(0);
		}
	}
			
}
