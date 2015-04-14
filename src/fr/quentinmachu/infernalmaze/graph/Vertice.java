package fr.quentinmachu.infernalmaze.graph;

import java.awt.Point;

public class Vertice {
	private int id;
	private int NodeFrom;
	private int NodeTo;
	private Point gate;
	
	public Point getGate() {
		return gate;
	}

	public void setGate(Point gate) {
		this.gate = gate;
	}



	public Vertice(int id, int NodeFrom, int NodeTo, double d, double e){
		this.id = id;
		this.NodeFrom = NodeFrom;
		this.NodeTo = NodeTo;
		this.gate = new Point();
		this.gate.setLocation(d, e);
	}
	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdNodeFrom() {
		return NodeFrom;
	}
	
	public int getIdNodeTo() {
		return NodeTo;
	}
	
}
