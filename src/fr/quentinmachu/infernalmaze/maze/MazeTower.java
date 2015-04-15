package fr.quentinmachu.infernalmaze.maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

// A tower of mazes that we have to browse from top to bottom
public class MazeTower {
	private int width;
	private int height;
	private int depth;
	private Point origin;
	private ArrayList<Integer>[] fakexiteger;
	private ArrayList<Point>[] fakexitpoint;
	
	Maze[] mazes;
	
	private Point end;
		
	private static final ThreadLocalRandom rnd = ThreadLocalRandom.current();
	
	public MazeTower(int width, int height, int depth) {
		this(width, height, depth, new Point(rnd.nextInt(width), rnd.nextInt(height)));
	}
	
	@SuppressWarnings("unchecked")
	public MazeTower(int width, int height, int depth, Point origin) {
		if(width<=0 || height<=0 || depth<1 || origin.x<0 || origin.x>=width || origin.y<0 || origin.y>=height)
			throw new IllegalArgumentException();
		
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.origin = origin;
		
		mazes = new Maze[depth];
		
		generate();
		
		fakexiteger = new ArrayList[depth];
		fakexitpoint = new ArrayList[depth];
		if(Version == 1)
		for(int i = 0; i < depth-1; i++){
			//Create new points to top
			int NbPointToTopLevel = rnd.nextInt(0,mazes[i].getDeadEnds().size());
			fakexiteger[i] = new ArrayList<Integer>();
			fakexitpoint[i] = new ArrayList<Point>();
			while(fakexiteger[i].size() != NbPointToTopLevel){
				int index = rnd.nextInt(0,mazes[i].getDeadEnds().size());
				//TODO rajouter des comparaisons entre points pour vérifier qu'un accès d'en bas ne soit pas un accès en haut
				if(!fakexiteger[i].contains(index)){
					fakexiteger[i].add(index);
					fakexitpoint[i].add(mazes[i].getDeadEnds().get(index));
				}
			}
		}
	}
	
	private void generate() {
		// First maze with origin
		mazes[depth-1] = new Maze(width, height, origin);
		
		for(int d = depth-2; d>=0; d--) {
			// A new maze with the origin set to the end of the upper one
			mazes[d] = new Maze(width, height, mazes[d+1].getEnd());
		}
		end = mazes[0].getEnd();
	}

	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @return the origin
	 */
	public Point getOrigin() {
		return origin;
	}

	/**
	 * @return the mazes
	 */
	public Maze[] getMazes() {
		return mazes;
	}

	/**
	 * @return the end
	 */
	public Point getEnd() {
		return end;
	}
	
	public String toString() {
		String s = "";
		ArrayList<Point> NullArray = new ArrayList<Point>();
		s += "<MazeTower width="+width+" height="+height+" depth="+depth+" origin=("+origin.x+","+origin.y+") end=("+end.x+","+end.y+")>\n";
		if(Version == 1){
			for(int d = depth-1; d>=0; d--)
				if(d == 0){
					s+= mazes[d].toString(NullArray, fakexitpoint[d]);
				}else if(d == depth-1){
					s+= mazes[d].toString(fakexitpoint[d-1],NullArray);
				}else{
					s += mazes[d].toString(fakexitpoint[d-1], fakexitpoint[d]);
				}
		}else{
			for(int d = depth-1; d>=0; d--)
				s+= mazes[d];
		}
		s += "</MazeTower>";
		
		return s;
	}
}
