package application;

import java.util.ArrayList;

import javafx.scene.shape.Circle;

public class Vertex
{	
	// false = Cooperate ; true = defect
	private boolean current_defect;
	private boolean next_defect;
	
	private int current_payoff;
	private int next_payoff;
	
		
	//used to assign the vertex number
	private static int count = 0;
	
	private int vertexNumber;	
	
	//used to determine which nodes to display: 0 - do not show, 1 - show, 2 - was shown before
	private int mark;
	
	//used for displaying the node
	private Circle visualObject;
	
	// the positions of the visual object
	Double centerX, centerY;
	
	

	// list of all the neighbors
	private ArrayList<Vertex> neighbors = new ArrayList<Vertex> ();
	// list of all edges this node is part of
	private ArrayList<Edge> edges = new ArrayList<Edge> ();
	
	Vertex()
	{
		this.mark=0;
		
		this.current_defect = false;
		this.next_defect = false;
		this.current_payoff = 0;
		this.next_payoff = 0;
		
		this.vertexNumber = Vertex.count;
		Vertex.count++;
		
		
		visualObject=null;
		this.neighbors = new ArrayList<Vertex> ();
		this.edges = new ArrayList<Edge> ();

	}
	
	Vertex(boolean defect)
	{
		this.mark=0;
		
		this.current_defect = defect;
		this.next_defect = false;
		this.current_payoff = 0;
		this.next_payoff = 0;
		
		this.vertexNumber = Vertex.count;
		Vertex.count++;
		
		visualObject=null;
		this.neighbors = new ArrayList<Vertex> ();
		this.edges = new ArrayList<Edge> ();
	}
	
	public void setNextPayoff(int payoff)
	{
		this.next_payoff = payoff;
	}
	
	public int getCurrentPayoff()
	{
		return this.current_payoff;
	}
		
	public void setCurrentDefect(boolean defect)
	{
		this.current_defect = defect;
	}
	
	public boolean getCurrentDefect()
	{
		return this.current_defect;
	}	
		
	public void setNextDefect(boolean defect)
	{
		this.next_defect = defect;
	}
	
	public boolean getNextDefect()
	{
		return this.next_defect;
	}	
			
	public int getVertexNumber()
	{
		return this.vertexNumber;
	}
	
	public void setVertexNumber(int v)
	{
		this.vertexNumber=v;
	}
	
	public int getCount()
	{
		return Vertex.count;
	}
	
	public void setCount(int c)
	{
		Vertex.count = c;
	}
	
	public void setMark(int newMark)
	{
		this.mark=newMark;
	}
	
	public int getMark()
	{
		return this.mark;
	}
	
	public void setVisualObject(Circle newVisualObject)
	{
		this.visualObject=newVisualObject;
	}
	
	public Circle getVisualObject()
	{
		return this.visualObject;
	}
		
	public Double getCenterX() {
		return centerX;
	}

	public void setCenterX(Double centerX) {
		this.centerX = centerX;
	}

	public Double getCenterY() {
		return centerY;
	}

	public void setCenterY(Double centerY) {
		this.centerY = centerY;
	}
	
	public void addNeighbor(Vertex neighbor, Edge edge)
	{
		this.neighbors.add(neighbor);	
		this.edges.add(edge);
	}
	
	public void removeNeighbor(Vertex neighbor)
	{		
		this.edges.remove(this.neighbors.indexOf(neighbor));
		this.neighbors.remove(neighbor);		
	}
	
	public ArrayList<Vertex> getNeighbors()
	{
		return this.neighbors;
	}	
	
	public ArrayList<Edge> getEdges()
	{
		return this.edges;
	}
	
	// returns the index of the neighbour or -1 if the nodes are not neighbours
	public int isNeighbor(Vertex neighbor)
	{
		if(this.neighbors.contains(neighbor))			
			return this.neighbors.indexOf(neighbor);
		else
			return -1;
	}
	
	public Edge getEdge(int index)
	{
		return this.edges.get(index);
	}
	
	public Vertex getNeighbor(int index)
	{
		return this.neighbors.get(index);
	}
	
	public int getDegree()
	{
		return neighbors.size();
	}
	
	//update current payoff to next payoff and reset next payoff
	public void updateCurrentPayoff()
	{
		this.current_payoff = this.next_payoff;
		this.next_payoff = 0;
	}
	

	//update current defect to next defect
	public void updateCurrentDefect()
	{
		this.current_defect = this.next_defect;
	}
	
	// calculates and sets the next payoff
	public void calculateNextPayoff(int payoff_T, int payoff_R, int payoff_P, int payoff_S)
	{
		int score = 0;
				
		for(int j = 0; j<this.getNeighbors().size(); j++)
		{
			Vertex current_neighbour = this.getNeighbors().get(j);
			if(this.getCurrentDefect() == true )
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
		
		this.setNextPayoff(score);
	}
	
	public void calculateNextDefect()
	{
		// set best score to current node score
		int best_score = this.getCurrentPayoff();
		
		int number_of_cooperators = 0;
		int number_of_defectors = 0;
		
		if(this.getCurrentDefect() == false)
			number_of_cooperators++;
		else
			number_of_defectors++;
		
		
		for(int j = 0; j<this.getNeighbors().size(); j++)
		{
			Vertex current_neighbour = this.getNeighbors().get(j);
			
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
		
		// if only defectors in the neighborhood have the best payoff
		if(number_of_cooperators==0)
		{
			this.setNextDefect(true); // the current node will defect in the next turn
		}
		else if(number_of_defectors==0) // if only cooperators
		{
			this.setNextDefect(false); // it will cooperate
		}
		else
		{	
			//we randomly set the next defect proportional to the number of cooperators and defectors with the best payoff
			double type = Math.random()*(number_of_cooperators + number_of_defectors);
			if(type>number_of_defectors)
				this.setNextDefect(false);
			else
				this.setNextDefect(true);
		}
	}
			
}
