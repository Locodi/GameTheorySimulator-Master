package application;

import java.util.ArrayList;

import javafx.scene.shape.Circle;

public abstract class Vertex
{	
	 
	private int current_strategy;
	private int next_strategy;
	
	private double current_payoff;
	private double next_payoff;
	
	
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
		

	}
	
	Vertex(int strategy)
	{
		
	}
	
	
	
	public void setCurrentPayoff(double payoff)
	{
		this.current_payoff = payoff;
	}
	
	public double getCurrentPayoff()
	{
		return this.current_payoff;
	}
	
	public void setNextPayoff(double payoff)
	{
		this.next_payoff = payoff;
	}
	
	public double getNextPayoff()
	{
		return this.next_payoff;
	}
		
	public void setCurrentStrategy(int strategy)
	{
		this.current_strategy = strategy;
	}
	
	public int getCurrentStrategy()
	{
		return this.current_strategy;
	}	
		
	public void setNextStrategy(int strategy)
	{
		this.next_strategy = strategy;
	}
	
	public int getNextStrategy()
	{
		return this.next_strategy;
	}			
	
	public int getVertexNumber()
	{
		return this.vertexNumber;
	}
	
	public void setVertexNumber(int v)
	{
		this.vertexNumber=v;
	}
	
	public abstract int getCount();
	
	
	public abstract void setCount(int c);
	
	
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
	
	public void setNeighbors(ArrayList<Vertex> a)
	{
		this.neighbors = a;
	}
	
	public ArrayList<Vertex> getNeighbors()
	{
		return this.neighbors;
	}	
	
	public void setEdges(ArrayList<Edge> a)
	{
		this.edges = a;
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
	

	//update current strategy to next strategy
	public void updateCurrentStrategy()
	{
		this.current_strategy = this.next_strategy;
	}
	
	// calculates and sets the next payoff
	public abstract void calculateNextPayoff(int[] payoffs);
	
	
	public abstract void calculateNextStrategy();
	
			
}
