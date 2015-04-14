package fr.quentinmachu.infernalmaze.graph;

import java.awt.Point;
import java.util.ArrayList;

public class Graph {
	private int nodeCountId;
	private int verticeCountId;
	private ArrayList<Node> Nodes;
	private ArrayList<Vertice> Vertices;

	public Graph(){
		this.nodeCountId = 0;
		this.verticeCountId = 0;
		this.Nodes = new ArrayList<Node>();
		this.Nodes.clear();
		this.Vertices = new ArrayList<Vertice>();
		Vertices.clear();
	}
	
	/**
	 * Return the list of all graph's node.
	 * @return
	 */
	public ArrayList<Node> getNodes() {
		return Nodes;
	}
	
	public ArrayList<Node> getNodesByLevel(int level){
		ArrayList<Node> NodeByLevel = new ArrayList<Node>();
		for(int i = 0; i < this.getNodes().size(); i++){
			if(this.getNode(i).getLevel() == level) NodeByLevel.add(this.getNode(i));
		}
		return NodeByLevel;
	}

	/**
	 * set the list of node in the graph
	 * @param nodes ArrayList<> the list of node to set.
	 */
	public void setNodes(ArrayList<Node> nodes) {
		Nodes = nodes;
	}
	
	/**
	 * Get a Node with a specific position inside the node list of the graph
	 * @param i
	 * @return
	 */
	public Node getNode(int i){
		return Nodes.get(i);
	}
	
	/**
	 * get a Node with a specific Id in the graph
	 * @param i the Id of the node
	 * @return the Node or null if not found
	 */
	public Node getNodeById(int i){
		try{
			for(int j = 0; j < Nodes.size(); j++){
				if(Nodes.get(j).getId() == i) return Nodes.get(j);
			}
		}catch(Exception E){
			System.out.println("Exception dans la recherche du node par ID");
		}
		return null;
	}
	
	/**
	 * Create a node and add it to the graph with and Id but nothing about the level in the tower.
	 * This function should not be used anymore
	 */
	public void addNode(int Level,  ArrayList<Point> deadEnds){
		Node NodeToAdd = new Node(this.getNodeCount(), Level, deadEnds);
		nodeCountId++;
		Nodes.add(NodeToAdd);
	}
	
	
	public void addNode(int Level, Point a, Point b, ArrayList<Point> deadEnds){
		Node NodeToAdd = new Node(this.getNodeCount(), Level, a, b, deadEnds);
		nodeCountId++;
		Nodes.add(NodeToAdd);
	}
	

	
	/**
	 * Add node the graph
	 * @param E node to add
	 */
	public void addNode(Node E){
		Nodes.add(E);
	}
	
	/**
	 * Add node to the graph
	 * @param i the level in the tower
	 */
	public void addNode(int i, int Level,  ArrayList<Point> deadEnds){
		Node NodeToAdd = new Node(this.getNodeCount(), Level, deadEnds);
		NodeToAdd.setLevel(i);
		Nodes.add(NodeToAdd);
		nodeCountId++;
	}
	
	public void addNode(int i, int Level, Point a, Point b, ArrayList<Point> deadEnds){
		Node NodeToAdd = new Node(this.getNodeCount(), Level, a, b, deadEnds);
		NodeToAdd.setLevel(i);
		Nodes.add(NodeToAdd);
		nodeCountId++;
	}
	
	/**
	 * return the number of current Node in the graph
	 * (The last node Id is this number less one)
	 * @return the node number
	 */
	public int getNodeCount(){
		return this.nodeCountId;
	}
	
	
	public int getNodeCountByLevel(int level){
		int count = 0;
		for(int i = 0; i < this.getNodes().size(); i++){
			if(this.getNode(i).getLevel() == level) count++;
		}
		return count;
	}
	
	/**
	 * return the number of vertices in the graph
	 * @return the Vertice number
	 */
	public int getVerticeCount(){
		return this.verticeCountId;
	}
	
	/**
	 * add a Vertice to the graph list
	 * @param node1 node from
	 * @param node2 node to
	 */
	public void addVertice(int node1, int node2, double d, double e){
		Vertice VerticeToAdd = new Vertice(verticeCountId, node1, node2, d, e);
		Nodes.get(node1).addLink(VerticeToAdd);
		Nodes.get(node2).addLink(VerticeToAdd);
		verticeCountId++;
		Vertices.add(VerticeToAdd);
	}
	
	/**
	 * create a node but doesn't add it to the graph it's return instead. 
	 * Use with addNode(Node N) to link it to the node list of the graph
	 * @return
	 */
	public Node createNode(int Level, ArrayList<Point> deadEnds){
		Node NodeToAdd = new Node(this.getNodeCount(), Level, deadEnds);
		nodeCountId++;
		return NodeToAdd;
	}
	
	public Node createNode(int Level, Point a, Point b, ArrayList<Point> deadEnds){
		Node NodeToAdd = new Node(this.getNodeCount(), Level, a, b, deadEnds);
		nodeCountId++;
		return NodeToAdd;
	}
	
	public Vertice getVertice(int i){
		return Vertices.get(i);
	}
}
