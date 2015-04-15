package fr.quentinmachu.infernalmaze.maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Maze {
	private int width;
	private int height;
	private Point origin;
	private ArrayList<Point> deadEnds;
	//private ArrayList<Point> soluce;
	private ArrayList<Point> toTopMaze;
	private ArrayList<Point> toBottomMaze;
	
	private Point end;
	private int[][] distancesFromOrigin;
	
	private static final ThreadLocalRandom rnd = ThreadLocalRandom.current();
	public static final double RANDOM_RATIO = 0.10;
	
	// Each byte contains 4 bits of data : EWSN
	// When the bit is set, it means that the corresponding direction is opened
	// 0000 <=> an entirely closed cell
	// 0001 <=> a cell opened on the north direction
	private byte grid[][];

	public Maze(int width, int height) {
		this(width, height, new Point(rnd.nextInt(width), rnd.nextInt(height)), new ArrayList<Point>());
	}
	
	public Maze(int width, int height, Point origin) {
		this(width, height, origin, new ArrayList<Point>());
	}
	
	public Maze(int width, int height, ArrayList<Point> deadEnds) {
		this(width, height, new Point(rnd.nextInt(width), rnd.nextInt(height)), deadEnds);
	}
	
	public Maze(byte[][] grid, int width, int height){
		this(width,height);
		this.grid = grid;
	}
	
	public Maze(int width, int height, Point origin, ArrayList<Point> deadEnds) {
		if(width<=0 || height<=0 || origin.x<0 || origin.x>=width || origin.y<0 || origin.y>=height)
			throw new IllegalArgumentException();
		if(deadEnds==null)
			deadEnds = new ArrayList<Point>();
		
		this.width = width;
		this.height = height;
		this.origin = origin;
		this.deadEnds = deadEnds;
		this.toTopMaze = new ArrayList<Point>();
		this.toBottomMaze = new ArrayList<Point>();
		
		grid = new byte[width][height];
		
		// Generate the maze
		generate();
		
		// Set the end
		end = origin;
		distancesFromOrigin = computeDistances(origin);
		for(int x=0; x<width; x++)
			for(int y=0; y<height; y++)
				if(distancesFromOrigin[x][y] > distancesFromOrigin[end.x][end.y])
					end = new Point(x, y);
		
	}
	
	// Growing Tree algorithm
	private void generate() {
		Random rand = new Random();
		ArrayList<Point> C = new ArrayList<Point>();
		List<Direction> directions = Arrays.asList(Direction.values());
		
		// Add origin
		C.add(origin);
		
		while(!C.isEmpty()) {
			boolean unvisitedNeighborFound = false;
				
			// Select next point
			// Newest point <=> Recursive Backtracking
			// Random point <=> Prim's
			// Here we do (100-RANDOM_RATIO)/RANDOM_RATIO
			Point p;
			if(rand.nextDouble() < RANDOM_RATIO) p = C.get(rand.nextInt(C.size()));
			else p = C.get(C.size()-1);
			
			// If the current point is defined to be a dead-end and already is, remove it from the list and pick another one
			if(deadEnds.contains(p) && isDeadEnd(p)) {
				C.remove(p);
				continue;
			}
		
			// Iterate randomly over the directions
			Collections.shuffle(directions);
			for(Direction d: directions) {
				Point np = new Point(p.x + d.dx, p.y + d.dy);
				
				// If the new point is valid and unvisited
				if(np.x >= 0 && np.y >= 0 && np.x < width && np.y < height && grid[np.x][np.y] == 0) {
					grid[p.x][p.y] |= d.bit;
					grid[np.x][np.y] |= d.oppositeDirection().bit;
					
					C.add(np);
					unvisitedNeighborFound = true;
					
					break;
				}
			}
			
			if(!unvisitedNeighborFound) C.remove(p);
		}
		
		// Update deadends
		deadEnds.clear();
		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				Point p = new Point(x,y);
				if(isDeadEnd(p)) deadEnds.add(p);
			}	
		}	
	}
	
	public boolean isDeadEnd(Point p) {
		if(p.x<0 || p.x>=width || p.y<0 || p.y>=height)
			throw new IllegalArgumentException();
		
		return ((grid[p.x][p.y] & (grid[p.x][p.y] - 1)) == 0);
	}

	public int[][] computeDistances(Point start) { //TODO Improve me!
		if(start.x<0 || start.x>=width || start.y<0 || start.y>=height)
			throw new IllegalArgumentException();
		
		// Distances
		int D[][] = new int[width][height];
		// Unvisited nodes
		ArrayList<Point> Q = new ArrayList<Point>();
		
		// Initialization
		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				D[x][y] = Integer.MAX_VALUE;
				Q.add(new Point(x, y));
			}
		}
		D[start.x][start.y] = 0;
		
		// For each point in the unvisited list
		while(!Q.isEmpty()) {
			// Pick the min dist point
			Point minDistPoint = Q.get(0);
			for(Point p: Q)
				if(D[p.x][p.y] < D[minDistPoint.x][minDistPoint.y])
					minDistPoint = p;
			Q.remove(minDistPoint);
			
			// For each neighbors
			ArrayList<Point> neighbors = getNeighbors(minDistPoint);
			for(Point n: neighbors)
				// Is the distance < ?
				if(D[minDistPoint.x][minDistPoint.y] + 1 < D[n.x][n.y]) D[n.x][n.y] = D[minDistPoint.x][minDistPoint.y] + 1;
		}
		
		return D;
	}
	
	public ArrayList<Point> getNeighbors(Point p) {
		return getNeighbors(p.x, p.y);
	}
	
	public ArrayList<Point> getNeighbors(int x, int y) {
		if(x<0 || x>=width || y<0 || y>=height)
			throw new IllegalArgumentException();
		
		ArrayList<Point> neighbors = new ArrayList<Point>();
		
		List<Direction> directions = Arrays.asList(Direction.values());
		for(Direction d: directions) {
			Point np = new Point(x + d.dx, y + d.dy);
			
			// If the new point is valid and the path is opened
			if(np.x >= 0 && np.y >= 0 && np.x < width && np.y < height) {
				if(isPathOpened(x, y, d))
					neighbors.add(np);
			}
		}

		return neighbors;
	}

	public boolean isPathOpened(Point p, Direction d) {
		return isPathOpened(p.x, p.y, d);
	}
	
	public boolean isPathOpened(int x, int y, Direction d) {
		if(x<0 || x>=width || y<0 || y>=height)
			throw new IllegalArgumentException();
		
		return ((grid[x][y] & d.bit) == d.bit);
	}
	
	public void setGrid(int width, int height, byte cell){
		this.grid[width][height] = cell;
	}
	
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	
	// Output the maze
	public String toString() {
		String s = "";
		
		s += "<Maze width="+width+" height="+height+" origin=("+origin.x+","+origin.y+") end=("+end.x+","+end.y+")>\n";
		
		// First line
		s += " ";
		for(int i = 0; i<width*2 - 1; i++) s += "_";
		s += " \n";
		
		for(int y = 0; y < height; y++) {
			s += "|";
			
			for(int x = 0; x < width; x++) {
				if(grid[x][y] == 0 && y+1 < height && grid[x][y+1] == 0) s += " ";
				else if((grid[x][y] & Direction.SOUTH.bit) != 0) s += " ";
				else s += "_";
				
				if(grid[x][y] == 0 && x+1 < width && grid[x+1][y] == 0) {
					if(y+1 < height && (grid[x][y+1] == 0 || grid[x+1][y+1] == 0)) s += " ";
					else s += "_";
				} else if((grid[x][y] & Direction.EAST.bit) != 0) {
					if(((grid[x][y] | grid[x+1][y]) & Direction.SOUTH.bit) != 0) s += " ";
					else s += "_";
				} else {
					s += "|";
				}
			}
			
			s += "\n";
		}
		
		s += "</Maze>\n";
		
		return s;
	}
	
	public String toString(ArrayList<Point> PointFromBot, ArrayList<Point> PointToTop) {
		// Legends : U means point comming from bottom maze with a south wall on this point
		// î means point going to top maze level with south wall on this point
		// ^ means point going to top maze without south wall on this point
		// v means point comming from botton maze without south wall on this point
		
		String s = "";
		
		s += "<Maze width="+width+" height="+height+" origin=("+origin.x+","+origin.y+") end=("+end.x+","+end.y+")>\n";
		
		// First line
		s += " ";
		for(int i = 0; i<width*2 - 1; i++) s += "_";
		s += " \n";
		
		for(int y = 0; y < height; y++) {
			s += "|";
			
			for(int x = 0; x < width; x++) {
				Point p = new Point(x,y);
				if(grid[x][y] == 0 && y+1 < height && grid[x][y+1] == 0) s += " ";
				else if((grid[x][y] & Direction.SOUTH.bit) != 0 && PointToTop.contains(p) && !PointToTop.isEmpty()) s+= "^";
				else if((grid[x][y] & Direction.SOUTH.bit) != 0 && PointFromBot.contains(p) && !PointFromBot.isEmpty()) s+= "V";
				else if((grid[x][y] & Direction.SOUTH.bit) != 0) s += " ";
				else if(PointToTop.contains(p) && !PointToTop.isEmpty()) s+= "î";
				else if(PointFromBot.contains(p) && !PointFromBot.isEmpty()) s+= "U";
				else s += "_";
				
				if(grid[x][y] == 0 && x+1 < width && grid[x+1][y] == 0) {
					if(y+1 < height && (grid[x][y+1] == 0 || grid[x+1][y+1] == 0)) s += " ";
					else s += "_";
				} else if((grid[x][y] & Direction.EAST.bit) != 0) {
					if(((grid[x][y] | grid[x+1][y]) & Direction.SOUTH.bit) != 0) s += " ";
					else s += "_";
				} else {
					s += "|";
				}
			}
			
			s += "\n";
		}
		
		s += "</Maze>\n";
		
		return s;
	}
	
	

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
	 * @return the origin
	 */
	public Point getOrigin() {
		return origin;
	}

	/**
	 * @return the deadEnds
	 */
	public ArrayList<Point> getDeadEnds() {
		return deadEnds;
	}

	/**
	 * @return the grid cell
	 */
	public byte getCell(Point p) {
		return getCell(p.x, p.y);
	}
	
	/**
	 * @return the grid cell
	 */
	public byte getCell(int x, int y) {
		if(x<0 || x>=width || y<0 || y>=height)
			throw new IllegalArgumentException();
		
		return grid[x][y];
	}

	/**
	 * @return the end
	 */
	public Point getEnd() {
		return end;
	}

	/**
	 * @return the distancesFromOrigin
	 */
	public int[][] getDistancesFromOrigin() {
		return distancesFromOrigin;
	}
	
	public ArrayList<Point> getUpGates() {
		ArrayList<Point> upGates = new ArrayList<Point>();
		upGates.add(origin);
		
		return upGates;
	}
	
	public ArrayList<Point> getDownGates() {
		ArrayList<Point> downGates = new ArrayList<Point>();
		downGates.add(end);
		
		return downGates;
	}

	public ArrayList<Point> getToTopMaze() {
		return toTopMaze;
	}

	public void addToTopMaze(Point toTopMaze) {
		this.toTopMaze.add(toTopMaze);
	}

	public ArrayList<Point> getToBottomMaze() {
		return toBottomMaze;
	}

	public void addToBottomMaze(Point toBottomMaze) {
		this.toBottomMaze.add(toBottomMaze);
	}
}