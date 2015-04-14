package fr.quentinmachu.infernalmaze.graph;

import java.util.ArrayList;
import java.awt.Point;

public class Node {
	private int id;
	private int level;
	private Point topLeft;
	private Point botRight;
	private ArrayList<Vertice> links;
	private ArrayList<Point> deadEnds;
	
	public Node(int i, int j, Point a, Point b, ArrayList<Point> deadEnds){
		this.topLeft = a;
		this.botRight = b;
		this.id = i;
		this.level =j;
		this.links = new ArrayList<Vertice>();
		this.deadEnds = deadEnds;
		this.links.clear();
	}

	public Node(int i, int j, ArrayList<Point> deadEnds){
		Point noPoint = new Point(-1,-1);
		this.deadEnds = deadEnds;
		this.topLeft = noPoint;
		this.botRight = noPoint;
		this.id = i;
		this.level =j;
		this.links = new ArrayList<Vertice>();
		this.links.clear();
	}
	
	public int getId() {
		return id;
	}
	
	public ArrayList<Integer> getNextNodes(){
		ArrayList<Integer> nextNodes = new ArrayList<Integer>();
		for(int i = 0; i < links.size(); i++){
			if(links.get(i).getIdNodeFrom() != this.id){
				nextNodes.add(links.get(i).getIdNodeFrom());
			}
			if(links.get(i).getIdNodeTo() != this.id){
				nextNodes.add(links.get(i).getIdNodeTo());
			}
		}
		return nextNodes;
	}
	
	public ArrayList<Vertice> getLinks() {
		return links;
	}
	
	public Vertice getLink(int i){
		return links.get(i);
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getNbrLinks(){
		return links.size();
	}
	
	public Point getTopLeft() {
		return topLeft;
	}

	public Point getBotRight() {
		return botRight;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setBotRight(Point botRight) {
		this.botRight = botRight;
	}
	
	public void setTopLeft(Point topLeft) {
		this.topLeft = topLeft;
	}
	
	public void setLinks(ArrayList<Vertice> links) {
		this.links = links;
	}
	
	public void removeLink(int i){
		links.remove(i);
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	public void addLink(Vertice V){
		links.add(V);
	}
	
	public void removeLink(Vertice V){
		links.remove(V);
	}
	
	public ArrayList<Point> getDeadEnds() {
		return deadEnds;
	}

	public void setDeadEnds(ArrayList<Point> deadEnds) {
		this.deadEnds = deadEnds;
	}
	
	public Point getDeadEnd(int i){
		return this.deadEnds.get(i);
	}
}
