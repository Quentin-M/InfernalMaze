import java.awt.Point;

//import fr.quentinmachu.infernalmaze.game.MazeGame;
import fr.quentinmachu.infernalmaze.maze.MazeTowerCuts;

public class Launcher {
	public static void main(String[] args) {		
		//new MazeGame(1024, 768, "SpaceNavigatorController", 10, 3).run();
		//TODO Better maze management (Aurelien)
		//TODO Ground texture
		//TODO Add missing controls on Mouse Controller
		//TODO GG screen
		int Width = 10;
		int Height = 10;
		int minSizeWidth = 5;
		int minSizeHeight = 5;
		Point origin = new Point(0,0);
		
		MazeTowerCuts Tower = new MazeTowerCuts(Width,Height,Width, minSizeWidth,minSizeHeight,origin);
		System.out.println(Tower);
	}
}
