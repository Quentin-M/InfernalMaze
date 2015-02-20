import java.awt.Point;
import java.util.ArrayList;

import fr.quentinmachu.infernalmaze.maze.Maze;
import fr.quentinmachu.infernalmaze.maze.MazeTower;

public class Launcher {

	public static void main(String[] args) {
		/*int size = 5;
		Point origin = new Point(0,0);
		ArrayList<Point> deadEnds = new ArrayList<Point>();
		//deadEnds.add(new Point(0, 0));
		//deadEnds.add(new Point(3, 3));
		Maze maze = new Maze(size, size, deadEnds);
		System.out.println(maze);
		int[][] D = maze.getDistancesFromOrigin();
		for(int x=0; x<size; x++)
			for(int y=0; y<size; y++)
				System.out.println("Origin -> ("+x+","+y+") = " + D[x][y]);
		*/
				
		int size = 10;
		int depth = 3;
		MazeTower tower = new MazeTower(size, size, depth);
		System.out.println(tower);
	}

}
