import fr.quentinmachu.infernalmaze.game.MazeGame;

public class Launcher {
	public static void main(String[] args) {		
		new MazeGame(1024, 768, "SpaceNavigatorController", 10, 3).run();
		//TODO Better maze management (Aurelien)
		//TODO Ground texture
		//TODO Add missing controls on Mouse Controller
		//TODO GG screen
	}
}
