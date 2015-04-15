import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;

import fr.quentinmachu.infernalmaze.game.MazeGame;
//import fr.quentinmachu.infernalmaze.game.MazeGame;
import fr.quentinmachu.infernalmaze.maze.MazeTowerCuts;

public class Launcher {
	public static void main(String[] args) {
		int width = 20;
		int height = 20;
		int minSizeWidth = 5;
		int minSizeHeight = 5;
		int depth = 4;
		Point origin = new Point(ThreadLocalRandom.current().nextInt(width), ThreadLocalRandom.current().nextInt(height));
		
		new MazeGame(1024, 768, "MouseController",  new MazeTowerCuts(width, height, depth, minSizeWidth, minSizeHeight, origin)).run();
	}
}
