package application;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Edge {
	
	// The two nodes that the edge connects
	private Vertex node_a;	
	private Vertex node_b;
	
	//used for displaying the edge
	private Line visualObject;
	
	// the positions of the edge end points which are the same as the node center points
	private Double startX, startY, endX, endY;
	
	Edge(Vertex a, Vertex b){
		node_a = a;
		node_b = b;
		visualObject=null;
	}
	
	public void setNodeA(Vertex a)
	{
		this.node_a = a;
	}
	
	public Vertex getNodeA()
	{
		return this.node_a;
	}
	
	public void setNodeB(Vertex b)
	{
		this.node_b = b;
	}
	
	public Vertex getNodeB()
	{
		return this.node_b;
	}
	
	public void setVisualObject(Line newVisualObject)
	{
		this.visualObject=newVisualObject;
	}
	
	public Line getVisualObject()
	{
		return this.visualObject;
	}
	
	public void setStartX(Double start)
	{
		this.startX=start;
	}
	
	public Double getStartX()
	{
		return this.startX;
	}
	
	public void setStartY(Double start)
	{
		this.startY=start;
	}
	
	public Double getStartY()
	{
		return this.startY;
	}
	
	public void setEndX(Double end)
	{
		this.endX=end;
	}
	
	public Double getEndX()
	{
		return this.endX;
	}
	
	public void setEndY(Double end)
	{
		this.endY=end;
	}
	
	public Double getEndY()
	{
		return this.endY;
	}
	
}
