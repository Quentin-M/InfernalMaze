package fr.quentinmachu.infernalmaze.maze;

import java.awt.Point;

public class MazeFactory {
	private static int size = 10;
	private static int depth = 5;
	private static int minSizeMaze = 5;
	private static int NumberLevel = 5;
	private static Point origin;

	
	static void CreateSimpleMazeTower(){
		MazeTower tower = new MazeTower(size, size, depth);
		System.out.println(tower);
	}
	
	static void CreateComplexMazeTower(){
		origin.x = 1;
		origin.y = 1;
		MazeTowerCuts tower = new MazeTowerCuts(size,size,depth,minSizeMaze, minSizeMaze, origin);
	}
}
