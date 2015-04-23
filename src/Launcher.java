import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;

import fr.quentinmachu.infernalmaze.game.MazeGame;
import fr.quentinmachu.infernalmaze.maze.MazeTowerCuts;

public class Launcher {
    public static void main(String[] args) {
	int s = ThreadLocalRandom.current().nextInt(6);
	int width = 15 + s;
	int height = 15 + s;
	int minSizeWidth = 5;
	int minSizeHeight = 5;
	int depth = 3 + ThreadLocalRandom.current().nextInt(3);

	Point origin = new Point(ThreadLocalRandom.current().nextInt(width), ThreadLocalRandom.current().nextInt(height));

	new MazeGame(1650, 1050, "SpaceNavigatorController", new MazeTowerCuts(width, height, depth, minSizeWidth, minSizeHeight, origin)).run();
    }
}
