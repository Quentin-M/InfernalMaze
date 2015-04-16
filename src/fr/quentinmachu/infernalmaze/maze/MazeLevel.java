package fr.quentinmachu.infernalmaze.maze;

import fr.quentinmachu.infernalmaze.graph.Graph;

public class MazeLevel {
	private Maze[][] mazes;
	
	public MazeLevel(int MazeSizeWidth, int MazeSizeHeight) {
		mazes = new Maze[MazeSizeWidth][MazeSizeHeight];
	}

	public Maze[][] getMazes() {
		return mazes;
	}

	public void setMazes(Maze[][] mazes) {
		this.mazes = mazes;
	}
	
	public Maze toMaze(int width, int height){
		//int temp = 0;
		byte[][] grid = new byte[width][height];
		int yHeight,xWidth,i,j,k,l;
		Maze Level = new Maze(grid, width, height);
		yHeight = 0; l = 0; k =0; l = 0; i = 0; xWidth = 0;
		for(i = 0; i < this.getMazes().length; i++){ //Nécéssitent un retour à la ligne
			xWidth = 0;
			for(j = 0; j < this.getMazes()[i].length; j++){ //Deux j pour le même i sont sur la même ligne
				Maze theMaze = this.getMazes()[i][j];
				for(k = 0; k < theMaze.getHeight(); k++){	//Hauteur
					for(l = 0; l < theMaze.getWidth(); l++){ //Largeur
						/*System.out.println(Level);
						System.out.println(this.getMazes()[i][j]);
						System.out.print(temp+" Width : "+this.getMazes()[i][j].getWidth()+" Height : "+this.getMazes()[i][j].getHeight());
						System.out.println(" i :"+i+" j :"+j+" l :"+l+" k:"+k);*/
						//temp++;
						//System.out.println("NbrXCut" + NbrXCut + "NbrYCut"+ NbrYCuts);
						if(j != 0){Level.setGrid(l+xWidth, k+yHeight, theMaze.getCell(l,k));
						}else{
							if(i == 0) Level.setGrid(l, k, theMaze.getCell(l,k));
							if(i != 0) Level.setGrid(l, k+yHeight, theMaze.getCell(l,k));
						}
					}
				}
				xWidth += l;
			}
			yHeight += k;
		}
		return Level;
	}
	
	public Maze toMazeWithGraph(int width, int height, Graph G){
		byte[][] grid = new byte[width][height];
		int yHeight,xWidth,i,j,k,l;
		Maze Level = new Maze(grid, width, height);
		yHeight = 0; l = 0; k =0; l = 0; i = 0; xWidth = 0;
		for(i = 0; i < this.getMazes().length; i++){ //Nécéssitent un retour à la ligne
			xWidth = 0;
			for(j = 0; j < this.getMazes()[i].length; j++){//Deux j pour le même i sont sur la même ligne
				Maze theMaze = this.getMazes()[i][j];
				if(i == 0){
					for(k = 0; k < theMaze.getHeight(); k++){	//Hauteur
						if(j == 0){ //on prend la première colonne
							for(l = 0; l < theMaze.getWidth(); l++){ //Largeur
								Level.setGrid(l, k, theMaze.getCell(l,k));
							}
						}else{
							for(l = 0; l < theMaze.getWidth(); l++){
								Level.setGrid(l+xWidth, k+yHeight, theMaze.getCell(l,k));
							}
						}
					}
				}else{
					for(k = 0; k < theMaze.getHeight(); k++){	//Hauteur
						if(j == 0){ //on prend la première colonne
							for(l = 0; l < theMaze.getWidth(); l++){ //Largeur
								Level.setGrid(l, k+yHeight, theMaze.getCell(l,k));
							}
						}else{
							for(l = 0; l < theMaze.getWidth(); l++){
								Level.setGrid(l+xWidth, k+yHeight, theMaze.getCell(l,k));
							}
						}
					}
				}
				xWidth += l;
			}
			yHeight += k;
		}
		return Level;
	}
}
