package fr.quentinmachu.infernalmaze.maze;

import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;

// A tower of mazes that we have to browse from top to bottom
public class MazeTower {
	private int width;
	private int height;
	private int depth;
	private Point origin;
	
	Maze[] mazes;
	
	private Point end;
	
	private static final ThreadLocalRandom rnd = ThreadLocalRandom.current();
	
	public MazeTower(int width, int height, int depth) {
		this(width, height, depth, new Point(rnd.nextInt(width), rnd.nextInt(height)));
	}
	
	public MazeTower(int width, int height, int depth, Point origin) {
		if(width<=0 || height<=0 || depth<1 || origin.x<0 || origin.x>=width || origin.y<0 || origin.y>=height)
			throw new IllegalArgumentException();
		
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.origin = origin;
		
		mazes = new Maze[depth];
		
		generate();
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
		
		s += "<MazeTower width="+width+" height="+height+" depth="+depth+" origin=("+origin.x+","+origin.y+") end=("+end.x+","+end.y+")>\n";
		for(int d = depth-1; d>=0; d--)
			s += mazes[d];
		s += "</MazeTower>";
		
		return s;
	}
}
