import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;

import fr.quentinmachu.infernalmaze.game.MazeGame;
import fr.quentinmachu.infernalmaze.maze.MazeTowerCuts;

public class Launcher {
    public static void main(String[] args) {
	if (args.length < 8) {
	    usage();
	} else {
	    try {
		int windowWidth = Integer.parseInt(args[0]);
		int windowHeight = Integer.parseInt(args[1]);

		int width = Integer.parseInt(args[2]);
		int height = Integer.parseInt(args[3]);
		int minWidthSize = Integer.parseInt(args[4]);
		int minHeightSize = Integer.parseInt(args[5]);
		int depth = Integer.parseInt(args[6]);

		String controller = args[7] + "Controller";

		Point origin = new Point(ThreadLocalRandom.current().nextInt(width), ThreadLocalRandom.current().nextInt(height));
		new MazeGame(windowWidth, windowHeight, controller, new MazeTowerCuts(width, height, depth, minWidthSize, minHeightSize, origin)).run();
	    } catch (NumberFormatException e) {
		usage();
	    }
	}
    }

    public static void usage() {
	System.out.println("Please feed me with me parameters ... !");
	System.out.println("Usage: [windowWidth] [windowHeight] [mazeWidth] [mazeHeight] [submazeMinWidth] [submazeMinHeight] [depth] [controller (SpaceNavigator/Mouse)]");
	System.out.println("Example: 800 600 15 15 5 5 3 Mouse");
	System.exit(0);
    }
}
